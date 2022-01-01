package com.oddlyspaced.calci.mathexpression.expressions;

import com.oddlyspaced.calci.mathexpression.context.MEContext;
import com.oddlyspaced.calci.mathexpression.context.MEIssue;
import com.oddlyspaced.calci.mathexpression.enums.MEExpressionForm;
import com.oddlyspaced.calci.mathexpression.numbers.MEInteger;
import com.oddlyspaced.calci.mathexpression.numbers.MERational;
import com.oddlyspaced.calci.mathexpression.numbers.MEReal;
import com.oddlyspaced.calci.utilities.GeneralUtil;
import com.oddlyspaced.calci.utilities.observables.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
/* loaded from: classes.dex */
public class MEPower extends MEExpression {
    private static final int ADDITION_TERMS_CALCULATION_LIMIT = 1000;
    private MEExpression mBase;
    private MEExpression mExponent;

    public MEExpression getBase() {
        return this.mBase;
    }

    public MEExpression getExponent() {
        return this.mExponent;
    }

    private MEPower() {
    }

    public static MEPower powerWithBaseAndExponent(MEExpression base, MEExpression exponent) {
        MEPower power = new MEPower();
        power.mBase = base;
        power.mExponent = exponent;
        return power;
    }

    public static MEPower powerForRootWithDegreeAndBase(MEExpression degree, MEExpression base) {
        return powerWithBaseAndExponent(base, degree.invert());
    }

    public static MEPower powerForSquareRootWithBase(MEExpression base) {
        return powerWithBaseAndExponent(base, new MEValue(1, 2));
    }

    public static MEPower powerForDivisionWithDividend(MEExpression dividend) {
        return powerWithBaseAndExponent(dividend, MEValue.minusOne());
    }

    @Override // com.oddlyspaced.calci.mathexpression.expressions.MEExpression
    public ImmutableList<MEExpression> children() {
        return new ImmutableList<>(Arrays.asList(this.mBase, this.mExponent));
    }

    @Override // com.oddlyspaced.calci.mathexpression.expressions.MEExpression
    public MEPower copyWithChildren(Collection<? extends MEExpression> children) {
        if (GeneralUtil.equalOrBothNull(children(), children)) {
            return this;
        }
        MEPower copy = new MEPower();
        Iterator<? extends MEExpression> iterator = children.iterator();
        copy.mBase = (MEExpression) iterator.next();
        copy.mExponent = (MEExpression) iterator.next();
        return copy;
    }

    @Override // com.oddlyspaced.calci.mathexpression.expressions.MEExpression
    public MEExpression canonicalize() {
        MEExpression base = this.mBase.canonicalize();
        if (shouldStop(base)) {
            return null;
        }
        MEExpression exponent = this.mExponent.canonicalize();
        if (shouldStop(exponent)) {
            return null;
        }
        if (exponent.isZero()) {
            return MEValue.one();
        }
        if (exponent.equals(MEValue.one())) {
            return base;
        }
        if (base.equals(MEValue.one())) {
            return MEValue.one();
        }
        if ((base instanceof MEValue) && (exponent instanceof MEValue)) {
            return canonicalizePowerWithBaseValueAndExponentValue((MEValue) base, (MEValue) exponent);
        }
        if (base instanceof MEPower) {
            MEPower innerPower = (MEPower) base;
            MEExpression newExponent = new MEMultiplications(innerPower.mExponent, this.mExponent).canonicalize();
            if (shouldStop()) {
                return null;
            }
            return powerWithBaseAndExponent(innerPower.mBase, newExponent).canonicalize();
        }
        if (exponent instanceof MELogarithm) {
            MELogarithm logarithm = (MELogarithm) exponent;
            if (base.equals(logarithm.getBase())) {
                return logarithm.getOperand();
            }
        }
        if (base instanceof MEMultiplications) {
            Collection<MEExpression> operands = ((MEMultiplications) base).getOperands();
            ArrayList<MEExpression> newOperands = new ArrayList<>();
            for (MEExpression operand : operands) {
                MEExpression newOperand = powerWithBaseAndExponent(operand, exponent);
                if (newOperand != null) {
                    newOperands.add(newOperand);
                }
            }
            return new MEMultiplications(newOperands).canonicalize();
        } else if (base instanceof MEAdditions) {
            return canonicalizePowerWithBaseAdditionsAndExponent((MEAdditions) base, exponent);
        } else {
            return powerWithBaseAndExponent(base, exponent);
        }
    }

    private MEExpression canonicalizePowerWithBaseValueAndExponentValue(MEValue baseValue, MEValue exponentValue) {
        if (baseValue.isZero() && exponentValue.isNegative()) {
            stopWithError(MEIssue.DIVISION_BY_ZERO);
            return null;
        } else if (MEContext.getCurrent().getForm() == MEExpressionForm.Numeric) {
            boolean negateResult = false;
            if (exponentValue.getRational() != null && exponentValue.getRational().isFractional() && baseValue.isNegative() && exponentValue.getRational().denominator().isOdd()) {
                baseValue = baseValue.negate();
                negateResult = true;
            }
            MEValue result = null;
            if (baseValue.getRational() == null || exponentValue.getRational() == null || exponentValue.getRational().isFractional()) {
                MEReal realResult = MEReal.pow(baseValue.getReal(), exponentValue.getReal());
                if (realResult != null) {
                    result = new MEValue(realResult);
                }
            } else {
                MERational rationalResult = MERational.pow(baseValue.getRational(), exponentValue.getRational().numerator());
                if (rationalResult != null) {
                    result = new MEValue(rationalResult);
                }
            }
            if (result == null) {
                if (MEContext.getCurrent().getIssues().size() != 0) {
                    return null;
                }
                stopWithError(MEIssue.NEGATIVE_ROOT);
                return null;
            } else if (negateResult) {
                return result.negate();
            } else {
                return result;
            }
        } else {
            MERational base = baseValue.getRational();
            MERational exponent = exponentValue.getRational();
            MERational coefficient = new MERational(1);
            if (MERational.isEqual(exponent, 0)) {
                return MEValue.one();
            }
            if (MERational.isEqual(exponent, 1)) {
                return baseValue;
            }
            if (MERational.isEqual(base, 0) && MERational.isMore(exponent, new MERational(0))) {
                return MEValue.zero();
            }
            if (MERational.isEqual(base, 1)) {
                return MEValue.one();
            }
            if (!MEInteger.isEqual(exponent.numerator(), 1)) {
                base = MERational.pow(base, exponent.numerator());
                if (base == null) {
                    stopWithError(MEIssue.DIVISION_BY_ZERO);
                    return null;
                }
                exponent = new MERational(1, exponent.denominator());
            }
            if (!MEInteger.isEqual(exponent.denominator(), 1)) {
                MEInteger rootDegree = exponent.denominator();
                if (!base.isNegative() || !rootDegree.isEven()) {
                    MEInteger baseAsInteger = MEInteger.mul(base.numerator(), MEInteger.pow(base.denominator(), MEInteger.sub(rootDegree, new MEInteger(1))));
                    MERational coefficient2 = new MERational(1, base.denominator());
                    MEInteger factor = MEInteger.highestPerfectPowerFactor(baseAsInteger, rootDegree.toLong());
                    if (shouldStop(factor)) {
                        return null;
                    }
                    coefficient = MERational.mul(coefficient2, MEInteger.truncatedNthRoot(factor, rootDegree.toLong()));
                    base = new MERational(MEInteger.div(baseAsInteger, factor));
                } else {
                    stopWithError(MEIssue.NEGATIVE_ROOT);
                    return null;
                }
            }
            if (MERational.isEqual(exponent, 1) || MERational.isEqual(base, 1)) {
                return new MEValue(MERational.mul(base, coefficient));
            }
            MEExpression result2 = powerWithBaseAndExponent(new MEValue(base), new MEValue(exponent));
            if (!MERational.isEqual(coefficient, 1)) {
                return new MEMultiplications(new MEValue(coefficient), result2);
            }
            return result2;
        }
    }

    private MEExpression canonicalizePowerWithBaseAdditionsAndExponent(MEAdditions additions, MEExpression exponent) {
        MERational restExponent;
        if (!(exponent instanceof MEValue)) {
            return powerWithBaseAndExponent(additions, exponent);
        }
        MERational restExponent2 = ((MEValue) exponent).getRational();
        if (MERational.isEqual(restExponent2, 1)) {
            return additions;
        }
        MEInteger n = restExponent2.numerator();
        MERational restExponent3 = new MERational(1, restExponent2.denominator());
        if (n.isNegative()) {
            n = MEInteger.neg(n);
            restExponent = MERational.neg(restExponent3);
        } else {
            restExponent = restExponent3;
        }
        if (MEInteger.isEqual(n, 1)) {
            return splitOutCommonFactorsForPowerOnAdditions(powerWithBaseAndExponent(additions, exponent));
        }
        if (((int) Math.pow((double) additions.getOperands().size(), (double) n.toLong())) > ADDITION_TERMS_CALCULATION_LIMIT) {
            return powerWithBaseAndExponent(additions, exponent);
        }
        ArrayList<MEExpression> terms = new ArrayList<>();
        while (!MEInteger.isEqual(n, 0)) {
            terms.add(additions);
            n = MEInteger.sub(n, new MEInteger(1));
        }
        MEExpression result = MEMultiplications.multiplyOutAdditionsForTerms(terms).canonicalize();
        if (shouldStop()) {
            return null;
        }
        if (!MERational.isEqual(restExponent, 1)) {
            result = powerWithBaseAndExponent(result, new MEValue(restExponent)).canonicalize();
        }
        return result;
    }

    private MEExpression splitOutCommonFactorsForPowerOnAdditions(MEPower power) {
        if (!(power.mBase instanceof MEAdditions)) {
            return null;
        }
        MEAdditions additions = (MEAdditions) power.mBase;
        Collection<MEExpression> additionsOperands = additions.getOperands();
        ArrayList<MERational> coefficients = new ArrayList<>();
        for (MEExpression additionOperand : additionsOperands) {
            MERational coefficient = additionOperand.coefficient().getRational();
            if (coefficient != null) {
                coefficients.add(coefficient);
            }
        }
        MERational multiplier = MERational.multiplierToMakeSmallestPossibleIntegersFromRationals(coefficients);
        if (MERational.isEqual(multiplier, 1)) {
            return powerWithBaseAndExponent(power.mBase, power.mExponent);
        }
        MEValue multiplierValue = new MEValue(multiplier);
        return new MEMultiplications(powerWithBaseAndExponent(multiplierValue.invert(), power.mExponent), powerWithBaseAndExponent(new MEMultiplications(multiplierValue, additions), power.mExponent)).canonicalize();
    }

    @Override // com.oddlyspaced.calci.mathexpression.expressions.MEExpression
    public MEReal evaluateNumerically(MEReal value, MEVariable variable) {
        MEReal baseValue = this.mBase.evaluateNumerically(value, variable);
        if (shouldStop(baseValue)) {
            return null;
        }
        MEReal exponentValue = this.mExponent.evaluateNumerically(value, variable);
        if (!shouldStop(exponentValue)) {
            return MEReal.pow(baseValue, exponentValue);
        }
        return null;
    }

    @Override // com.oddlyspaced.calci.mathexpression.expressions.MEExpression
    public double evaluateNumerically(double value, MEVariable variable) {
        double baseValue = this.mBase.evaluateNumerically(value, variable);
        if (Double.isInfinite(baseValue) || shouldStop()) {
            return Double.NaN;
        }
        double exponentValue = this.mExponent.evaluateNumerically(value, variable);
        if (Double.isInfinite(exponentValue) || shouldStop()) {
            return Double.NaN;
        }
        return Math.pow(baseValue, exponentValue);
    }

    @Override // com.oddlyspaced.calci.mathexpression.expressions.MEExpression
    public MEExpression invert() {
        return powerWithBaseAndExponent(this.mBase, this.mExponent.negate());
    }

    @Override // com.oddlyspaced.calci.mathexpression.expressions.MEExpression
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MEPower)) {
            return false;
        }
        MEPower otherPower = (MEPower) other;
        return GeneralUtil.equalOrBothNull(this.mBase, otherPower.mBase) && GeneralUtil.equalOrBothNull(this.mExponent, otherPower.mExponent);
    }

    @Override // com.oddlyspaced.calci.mathexpression.expressions.MEExpression
    public int hashCode() {
        return GeneralUtil.hashCode(this.mBase) ^ GeneralUtil.hashCode(this.mExponent);
    }

    @Override // com.oddlyspaced.calci.mathexpression.expressions.MEExpression
    public String toString() {
        if (this.mExponent.equals(new MEValue(1, 2))) {
            return "√" + descriptionForOperand(this.mBase);
        }
        return descriptionForOperand(this.mBase) + "^" + descriptionForOperand(this.mExponent);
    }

    private String descriptionForOperand(MEExpression operand) {
        boolean isSimpleValue;
        boolean needsParentheses;
        String description = operand.toString();
        if (!(operand instanceof MEValue) || ((MEValue) operand).getRational() == null || ((MEValue) operand).getRational().isFractional()) {
            isSimpleValue = false;
        } else {
            isSimpleValue = true;
        }
        boolean hasParentheses = GeneralUtil.doesStringHaveMatchingParenthesesAroundIt(description);
        if (isSimpleValue || hasParentheses) {
            needsParentheses = false;
        } else {
            needsParentheses = true;
        }
        return needsParentheses ? "(" + description + ")" : description;
    }
}
