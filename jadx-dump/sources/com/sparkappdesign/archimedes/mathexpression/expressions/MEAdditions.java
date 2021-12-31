package com.sparkappdesign.archimedes.mathexpression.expressions;

import com.sparkappdesign.archimedes.mathexpression.context.MEContext;
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
public class MEAdditions extends MEExpression {
    private ImmutableList<MEExpression> mOperands;

    public ImmutableList<MEExpression> getOperands() {
        return this.mOperands;
    }

    private MEAdditions() {
    }

    public MEAdditions(Collection<? extends MEExpression> operands) {
        this.mOperands = new ImmutableList<>(operands);
    }

    public MEAdditions(MEExpression... expressions) {
        this(Arrays.asList(expressions));
    }

    public MEAdditions additionsByAddingOperands(Collection<? extends MEExpression> operands) {
        ArrayList<MEExpression> newOperands = new ArrayList<>(this.mOperands);
        newOperands.addAll(operands);
        return new MEAdditions(newOperands);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public ImmutableList<MEExpression> children() {
        return this.mOperands;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEAdditions copyWithChildren(Collection<? extends MEExpression> children) {
        if (GeneralUtil.equalOrBothNull(children(), children)) {
            return this;
        }
        MEAdditions copy = new MEAdditions();
        copy.mOperands = new ImmutableList<>(children);
        return copy;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEExpression canonicalize() {
        ArrayList<MEExpression> terms = canonicalizedTermsIncludingNestedAdditions();
        if (shouldStop()) {
            return null;
        }
        if (terms.size() == 0) {
            return MEValue.zero();
        }
        if (terms.size() == 1) {
            return terms.get(0);
        }
        ArrayList<MEExpression> units = new ArrayList<>();
        Iterator<MEExpression> it = terms.iterator();
        while (it.hasNext()) {
            MEExpression unit = it.next().partWithUnit();
            if (unit != null) {
                units.add(unit);
            }
        }
        if (!units.isEmpty()) {
            ArrayList<MEExpression> unitSet = new ArrayList<>(units);
            MEUnit targetUnit = unitSet.size() == 1 ? unitSet.get(0) : MEContext.getCurrent().getDefaultUnits().get(unitSet.get(0).quantityOfUnit());
            if (targetUnit != null) {
                ArrayList<MEExpression> newTerms = new ArrayList<>();
                Iterator<MEExpression> it2 = terms.iterator();
                while (it2.hasNext()) {
                    MEExpression newTerm = it2.next().convertToUnit(targetUnit, true);
                    if (newTerm != null) {
                        newTerms.add(newTerm);
                    }
                }
                terms = newTerms;
            }
            if (shouldStop()) {
                return null;
            }
        }
        ListUtil.mergeObjects(terms, new Merger<MEExpression>() { // from class: com.sparkappdesign.archimedes.mathexpression.expressions.MEAdditions.1
            public MEExpression merge(MEExpression obj1, MEExpression obj2, AtomicBoolean merged, AtomicBoolean stop) {
                stop.set(MEExpression.shouldStop());
                return MEAdditions.this.tryMergeTerms(obj1, obj2, merged);
            }
        }, true, false);
        if (terms.size() == 0) {
            return MEValue.zero();
        }
        if (terms.size() == 1) {
            return terms.get(0);
        }
        return new MEAdditions(terms);
    }

    private ArrayList<MEExpression> canonicalizedTermsIncludingNestedAdditions() {
        ArrayList<MEExpression> terms = new ArrayList<>();
        Iterator<MEExpression> it = this.mOperands.iterator();
        while (it.hasNext()) {
            MEExpression operand = it.next().canonicalize();
            if (shouldStop()) {
                return null;
            }
            if (operand instanceof MEAdditions) {
                terms.addAll(((MEAdditions) operand).mOperands);
            } else if (!operand.isZero()) {
                terms.add(operand);
            }
        }
        return terms;
    }

    public MEExpression tryMergeTerms(MEExpression term, MEExpression otherTerm, AtomicBoolean merged) {
        MEExpression body = term.partWithoutCoefficient();
        if (!GeneralUtil.equalOrBothNull(body, otherTerm.partWithoutCoefficient())) {
            return null;
        }
        merged.set(true);
        MEValue coefficient = term.coefficient().addWith(otherTerm.coefficient());
        if (coefficient.isZero()) {
            return null;
        }
        if (body != null) {
            return new MEMultiplications(coefficient, body).canonicalize();
        }
        return coefficient;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEReal evaluateNumerically(MEReal value, MEVariable variable) {
        MEReal result = new MEReal(0.0d);
        Iterator<MEExpression> it = this.mOperands.iterator();
        while (it.hasNext()) {
            MEReal operandValue = it.next().evaluateNumerically(value, variable);
            if (shouldStop(operandValue)) {
                return null;
            }
            result = MEReal.add(result, operandValue);
        }
        return result;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public double evaluateNumerically(double value, MEVariable variable) {
        double result = 0.0d;
        Iterator<MEExpression> it = this.mOperands.iterator();
        while (it.hasNext()) {
            double operandValue = it.next().evaluateNumerically(value, variable);
            if (Double.isInfinite(operandValue) || shouldStop()) {
                return Double.NaN;
            }
            result += operandValue;
        }
        return result;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof MEAdditions) {
            return this.mOperands.equals(((MEAdditions) other).mOperands);
        }
        return false;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public int hashCode() {
        return ListUtil.hashCodeIgnoringOrder(this.mOperands);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public String toString() {
        String string = "";
        boolean addOperator = false;
        Iterator<MEExpression> it = this.mOperands.iterator();
        while (it.hasNext()) {
            MEExpression operand = it.next();
            boolean negative = operand.isNegative();
            if (addOperator) {
                if (negative) {
                    operand = operand.negate();
                    string = string + " - ";
                } else {
                    string = string + " + ";
                }
            }
            string = string + operand.toString();
            addOperator = true;
        }
        return string;
    }
}
