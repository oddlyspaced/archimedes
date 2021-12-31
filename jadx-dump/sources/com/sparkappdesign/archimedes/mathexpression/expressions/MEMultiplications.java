package com.sparkappdesign.archimedes.mathexpression.expressions;

import com.sparkappdesign.archimedes.mathexpression.numbers.MEReal;
import com.sparkappdesign.archimedes.utilities.GeneralUtil;
import com.sparkappdesign.archimedes.utilities.ListUtil;
import com.sparkappdesign.archimedes.utilities.Merger;
import com.sparkappdesign.archimedes.utilities.observables.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes.dex */
public class MEMultiplications extends MEExpression {
    private ImmutableList<MEExpression> mOperands;

    public ImmutableList<MEExpression> getOperands() {
        return this.mOperands;
    }

    private MEMultiplications() {
    }

    public MEMultiplications(Collection<? extends MEExpression> operands) {
        this.mOperands = new ImmutableList<>(operands);
    }

    public MEMultiplications(MEExpression... expressions) {
        this(Arrays.asList(expressions));
    }

    public MEMultiplications multiplicationsByAddingOperands(Collection<? extends MEExpression> operands) {
        ArrayList<MEExpression> newOperands = new ArrayList<>(this.mOperands);
        newOperands.addAll(operands);
        return new MEMultiplications(newOperands);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public ImmutableList<MEExpression> children() {
        return this.mOperands;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEMultiplications copyWithChildren(Collection<? extends MEExpression> children) {
        if (GeneralUtil.equalOrBothNull(children(), children)) {
            return this;
        }
        MEMultiplications copy = new MEMultiplications();
        copy.mOperands = new ImmutableList<>(children);
        return copy;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEValue coefficient() {
        MEValue coefficient = MEValue.one();
        Iterator<MEExpression> it = this.mOperands.iterator();
        while (it.hasNext()) {
            coefficient = coefficient.multiplyBy(it.next().coefficient());
        }
        return coefficient;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEExpression partWithoutCoefficient() {
        ArrayList<MEExpression> operands = new ArrayList<>();
        Iterator<MEExpression> it = this.mOperands.iterator();
        while (it.hasNext()) {
            MEExpression partWithoutCoefficient = it.next().partWithoutCoefficient();
            if (partWithoutCoefficient != null) {
                operands.add(partWithoutCoefficient);
            }
        }
        return expressionForMultiplicationOfOperands(operands);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEExpression partWithUnit() {
        ArrayList<MEExpression> operands = new ArrayList<>();
        Iterator<MEExpression> it = this.mOperands.iterator();
        while (it.hasNext()) {
            MEExpression partWithUnit = it.next().partWithUnit();
            if (partWithUnit != null) {
                operands.add(partWithUnit);
            }
        }
        return expressionForMultiplicationOfOperands(operands);
    }

    private MEExpression expressionForMultiplicationOfOperands(ArrayList<MEExpression> operands) {
        if (operands.size() == 0) {
            return null;
        }
        if (operands.size() == 1) {
            return operands.get(0);
        }
        return new MEMultiplications(operands);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public boolean isNegative() {
        boolean isNegative = false;
        Iterator<MEExpression> it = this.mOperands.iterator();
        while (it.hasNext()) {
            if (it.next().isNegative()) {
                isNegative = !isNegative;
            }
        }
        return isNegative;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEExpression negate() {
        ArrayList<MEExpression> newOperands = new ArrayList<>();
        boolean containsNegativeOperand = false;
        Iterator<MEExpression> it = this.mOperands.iterator();
        while (it.hasNext()) {
            if (it.next().isNegative()) {
                containsNegativeOperand = true;
            }
        }
        boolean negated = false;
        Iterator<MEExpression> it2 = this.mOperands.iterator();
        while (it2.hasNext()) {
            MEExpression operand = it2.next();
            if (!negated && ((containsNegativeOperand && operand.isNegative()) || (!containsNegativeOperand && (operand instanceof MEValue)))) {
                operand = operand.negate();
                negated = true;
            }
            newOperands.add(operand);
        }
        if (!negated) {
            newOperands.add(0, MEValue.minusOne());
        }
        return new MEMultiplications(newOperands);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEExpression canonicalize() {
        ArrayList<MEExpression> terms = canonicalizedTermsIncludingNestedMultiplications();
        if (shouldStop()) {
            return null;
        }
        if (terms.size() == 0) {
            return MEValue.one();
        }
        if (terms.size() == 1) {
            return terms.get(0);
        }
        final AtomicBoolean containsZeroTerm = new AtomicBoolean(false);
        Iterator<MEExpression> it = terms.iterator();
        while (it.hasNext()) {
            if (it.next().isZero()) {
                containsZeroTerm.set(true);
            }
        }
        if (containsZeroTerm.get()) {
            return MEValue.zero();
        }
        ListUtil.mergeObjects(terms, new Merger<MEExpression>() { // from class: com.sparkappdesign.archimedes.mathexpression.expressions.MEMultiplications.1
            public MEExpression merge(MEExpression obj1, MEExpression obj2, AtomicBoolean merged, AtomicBoolean stop) {
                stop.set(MEExpression.shouldStop());
                MEExpression result = MEMultiplications.this.tryMergeTerms(obj1, obj2, merged);
                if (merged.get() && result.isZero()) {
                    containsZeroTerm.set(true);
                    stop.set(true);
                }
                return result;
            }
        }, true, true);
        if (containsZeroTerm.get()) {
            return MEValue.zero();
        }
        if (shouldStop()) {
            return null;
        }
        boolean containsAdditionsTerm = false;
        Iterator<MEExpression> it2 = terms.iterator();
        while (it2.hasNext()) {
            if (it2.next() instanceof MEAdditions) {
                containsAdditionsTerm = true;
            }
        }
        if (containsAdditionsTerm) {
            return multiplyOutAdditionsForTerms(terms).canonicalize();
        }
        if (terms.size() == 0) {
            return MEValue.one();
        }
        if (terms.size() == 1) {
            return terms.get(0);
        }
        return new MEMultiplications(terms);
    }

    /* JADX INFO: Multiple debug info for r8v5 com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression: [D('mergedTerm' com.sparkappdesign.archimedes.mathexpression.expressions.MEValue), D('mergedTerm' com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression)] */
    /* JADX INFO: Multiple debug info for r8v8 com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression: [D('mergedTerm' com.sparkappdesign.archimedes.mathexpression.expressions.MEValue), D('mergedTerm' com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression)] */
    public MEExpression tryMergeTerms(MEExpression term, MEExpression otherTerm, AtomicBoolean merged) {
        MEExpression base;
        MEExpression otherBase;
        if ((term instanceof MEValue) && (otherTerm instanceof MEValue)) {
            MEValue mergedTerm = ((MEValue) term).multiplyBy((MEValue) otherTerm);
            merged.set(true);
            return mergedTerm;
        }
        boolean termIsPower = term instanceof MEPower;
        boolean otherTermIsPower = otherTerm instanceof MEPower;
        if (termIsPower) {
            base = ((MEPower) term).getBase();
        } else {
            base = term;
        }
        if (otherTermIsPower) {
            otherBase = ((MEPower) otherTerm).getBase();
        } else {
            otherBase = otherTerm;
        }
        MEExpression exponent = termIsPower ? ((MEPower) term).getExponent() : MEValue.one();
        MEExpression otherExponent = otherTermIsPower ? ((MEPower) otherTerm).getExponent() : MEValue.one();
        if (base.equals(otherBase)) {
            if (!((base instanceof MEValue) && (termIsPower ^ otherTermIsPower))) {
                MEExpression mergedTerm2 = MEPower.powerWithBaseAndExponent(base, new MEAdditions(exponent, otherExponent)).canonicalize();
                if (shouldStop()) {
                    return null;
                }
                merged.set(true);
                return mergedTerm2;
            }
        }
        if (((base instanceof MEValue) && (otherBase instanceof MEValue)) && exponent.equals(otherExponent)) {
            MEExpression mergedTerm3 = MEPower.powerWithBaseAndExponent(((MEValue) base).multiplyBy((MEValue) otherBase), exponent).canonicalize();
            if (shouldStop()) {
                return null;
            }
            merged.set(true);
            return mergedTerm3;
        } else if (term.isZero() || otherTerm.isZero()) {
            merged.set(true);
            return MEValue.zero();
        } else if (term.equals(MEValue.one())) {
            merged.set(true);
            return otherTerm;
        } else if (!otherTerm.equals(MEValue.one())) {
            return null;
        } else {
            merged.set(true);
            return term;
        }
    }

    private ArrayList<MEExpression> canonicalizedTermsIncludingNestedMultiplications() {
        ArrayList<MEExpression> terms = new ArrayList<>();
        Iterator<MEExpression> it = this.mOperands.iterator();
        while (it.hasNext()) {
            MEExpression operand = it.next().canonicalize();
            if (shouldStop(operand)) {
                return null;
            }
            if (operand instanceof MEMultiplications) {
                terms.addAll(((MEMultiplications) operand).mOperands);
            } else if (!operand.equals(MEValue.one())) {
                terms.add(operand);
            }
        }
        return terms;
    }

    public static MEExpression multiplyOutAdditionsForTerms(ArrayList<MEExpression> terms) {
        MEExpression operand;
        ArrayList<ArrayList<MEExpression>> additionTerms = new ArrayList<>();
        additionTerms.add(new ArrayList<>());
        Iterator<MEExpression> it = terms.iterator();
        while (it.hasNext()) {
            MEExpression term = it.next();
            if (term instanceof MEAdditions) {
                MEAdditions termAsAdditions = (MEAdditions) term;
                additionTerms = new ArrayList<>();
                Iterator<ArrayList<MEExpression>> it2 = additionTerms.iterator();
                while (it2.hasNext()) {
                    ArrayList<MEExpression> oldAdditionTerm = it2.next();
                    Iterator<MEExpression> it3 = termAsAdditions.getOperands().iterator();
                    while (it3.hasNext()) {
                        ArrayList<MEExpression> newAdditionTerm = new ArrayList<>(oldAdditionTerm);
                        newAdditionTerm.add(it3.next());
                        additionTerms.add(newAdditionTerm);
                    }
                }
            } else {
                Iterator<ArrayList<MEExpression>> it4 = additionTerms.iterator();
                while (it4.hasNext()) {
                    it4.next().add(term);
                }
            }
        }
        ArrayList<MEExpression> properAdditionTerms = new ArrayList<>();
        Iterator<ArrayList<MEExpression>> it5 = additionTerms.iterator();
        while (it5.hasNext()) {
            ArrayList<MEExpression> termParts = it5.next();
            if (termParts.size() == 1) {
                operand = termParts.get(0);
            } else {
                operand = new MEMultiplications(termParts);
            }
            properAdditionTerms.add(operand);
        }
        if (properAdditionTerms.size() == 0) {
            return MEValue.zero();
        }
        if (properAdditionTerms.size() == 1) {
            return properAdditionTerms.get(0);
        }
        return new MEAdditions(properAdditionTerms);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEReal evaluateNumerically(MEReal value, MEVariable variable) {
        MEReal result = new MEReal(1.0d);
        Iterator<MEExpression> it = this.mOperands.iterator();
        while (it.hasNext()) {
            MEReal operandValue = it.next().evaluateNumerically(value, variable);
            if (shouldStop(operandValue)) {
                return null;
            }
            result = MEReal.mul(result, operandValue);
        }
        return result;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public double evaluateNumerically(double value, MEVariable variable) {
        double result = 1.0d;
        Iterator<MEExpression> it = this.mOperands.iterator();
        while (it.hasNext()) {
            double operandValue = it.next().evaluateNumerically(value, variable);
            if (Double.isInfinite(operandValue) || shouldStop()) {
                return Double.NaN;
            }
            result *= operandValue;
        }
        return result;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MEMultiplications)) {
            return false;
        }
        MEMultiplications otherMultiplications = (MEMultiplications) other;
        return this.mOperands.size() == otherMultiplications.mOperands.size() && this.mOperands.containsAll(otherMultiplications.mOperands) && otherMultiplications.mOperands.containsAll(this.mOperands);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public int hashCode() {
        return ListUtil.hashCodeIgnoringOrder(this.mOperands);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public String toString() {
        String string = "";
        MEExpression previousOperand = null;
        Iterator<MEExpression> it = this.mOperands.iterator();
        while (it.hasNext()) {
            MEExpression operand = it.next();
            if (previousOperand != null) {
                string = string + "·";
            }
            string = string + operand.toString();
            previousOperand = operand;
        }
        return string;
    }
}
