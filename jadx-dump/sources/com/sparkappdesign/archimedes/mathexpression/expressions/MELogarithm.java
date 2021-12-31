package com.sparkappdesign.archimedes.mathexpression.expressions;

import com.sparkappdesign.archimedes.mathexpression.context.MEContext;
import com.sparkappdesign.archimedes.mathexpression.context.MEIssue;
import com.sparkappdesign.archimedes.mathexpression.enums.MEExpressionForm;
import com.sparkappdesign.archimedes.mathexpression.numbers.MEInteger;
import com.sparkappdesign.archimedes.mathexpression.numbers.MERational;
import com.sparkappdesign.archimedes.mathexpression.numbers.MEReal;
import com.sparkappdesign.archimedes.utilities.GeneralUtil;
import com.sparkappdesign.archimedes.utilities.observables.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
/* loaded from: classes.dex */
public class MELogarithm extends MEExpression {
    private MEExpression mBase;
    private MEExpression mOperand;

    public MEExpression getBase() {
        return this.mBase;
    }

    public MEExpression getOperand() {
        return this.mOperand;
    }

    private MELogarithm() {
    }

    public MELogarithm(MEExpression base, MEExpression operand) {
        this.mBase = base;
        this.mOperand = operand;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public ImmutableList<MEExpression> children() {
        return new ImmutableList<>(Arrays.asList(this.mBase, this.mOperand));
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MELogarithm copyWithChildren(Collection<? extends MEExpression> children) {
        if (GeneralUtil.equalOrBothNull(children(), children)) {
            return this;
        }
        MELogarithm copy = new MELogarithm();
        Iterator<? extends MEExpression> iterator = children.iterator();
        copy.mBase = (MEExpression) iterator.next();
        copy.mOperand = (MEExpression) iterator.next();
        return copy;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEExpression canonicalize() {
        MEInteger perfectLogarithm;
        MEExpression result;
        if (MEContext.getCurrent().getForm() == MEExpressionForm.Numeric && (result = canonicalizeNumerically()) != null) {
            return result;
        }
        MEExpression base = !this.mBase.equals(MEConstant.e()) ? this.mBase.canonicalize() : this.mBase;
        if (shouldStop(base)) {
            return null;
        }
        MEExpression operand = this.mOperand.canonicalize();
        if (shouldStop(operand)) {
            return null;
        }
        if (base.isNegative() || base.isZero() || base.equals(MEValue.one())) {
            stopWithError(MEIssue.LOGARITHMIC_DOMAIN);
            return null;
        } else if (operand.isNegative() || operand.isZero()) {
            stopWithError(MEIssue.LOGARITHMIC_DOMAIN);
            return null;
        } else if (operand.equals(MEValue.one())) {
            return MEValue.zero();
        } else {
            if (base.equals(operand)) {
                return MEValue.one();
            }
            if (operand instanceof MEPower) {
                MEPower power = (MEPower) operand;
                return new MEMultiplications(power.getExponent(), new MELogarithm(base, power.getBase())).canonicalize();
            } else if (operand instanceof MEMultiplications) {
                ArrayList<MEExpression> additionOperands = new ArrayList<>();
                Iterator<MEExpression> it = ((MEMultiplications) operand).getOperands().iterator();
                while (it.hasNext()) {
                    MEExpression multiplicationOperand = it.next();
                    if (shouldStop()) {
                        return null;
                    }
                    additionOperands.add(new MELogarithm(base, multiplicationOperand));
                }
                return new MEAdditions(additionOperands).canonicalize();
            } else {
                if (operand instanceof MEValue) {
                    MERational operandRational = ((MEValue) operand).getRational();
                    if (operandRational.isFractional()) {
                        return new MEAdditions(new MELogarithm(base, new MEValue(operandRational.numerator())), new MELogarithm(base, new MEValue(operandRational.denominator())).negate()).canonicalize();
                    }
                    if (base instanceof MEValue) {
                        MERational baseRational = ((MEValue) base).getRational();
                        if (!baseRational.isFractional() && (perfectLogarithm = MEInteger.perfectLogarithm(operandRational.numerator(), baseRational.numerator())) != null) {
                            return new MEValue(perfectLogarithm);
                        }
                    }
                }
                return new MELogarithm(base, operand);
            }
        }
    }

    private MEExpression canonicalizeNumerically() {
        MEExpression operand = this.mOperand.canonicalize();
        if (shouldStop(operand)) {
            return null;
        }
        MEExpression base = this.mBase;
        if (!base.equals(MEConstant.e())) {
            base = base.canonicalize();
        }
        if (shouldStop(base)) {
            return null;
        }
        if (!((operand instanceof MEValue) && ((base instanceof MEValue) || base.equals(MEConstant.e())))) {
            return null;
        }
        MEReal operandReal = ((MEValue) operand).getReal();
        if (base.equals(MEConstant.e())) {
            base = base.canonicalize();
        }
        MEReal result = MEReal.log(operandReal, ((MEValue) base).getReal());
        if (!shouldStop(result)) {
            return new MEValue(result);
        }
        return null;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEReal evaluateNumerically(MEReal value, MEVariable variable) {
        MEReal operandValue = this.mOperand.evaluateNumerically(value, variable);
        if (shouldStop(operandValue)) {
            return null;
        }
        MEReal baseValue = this.mBase.evaluateNumerically(value, variable);
        if (!shouldStop(baseValue)) {
            return MEReal.log(operandValue, baseValue);
        }
        return null;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public double evaluateNumerically(double value, MEVariable variable) {
        double operandValue = this.mOperand.evaluateNumerically(value, variable);
        if (Double.isInfinite(operandValue) || MEContext.shouldStop()) {
            return Double.NaN;
        }
        double baseValue = this.mBase.evaluateNumerically(value, variable);
        if (Double.isInfinite(baseValue) || MEContext.shouldStop()) {
            return Double.NaN;
        }
        return Math.log(operandValue) / Math.log(baseValue);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MELogarithm)) {
            return false;
        }
        MELogarithm otherLogarithm = (MELogarithm) other;
        return GeneralUtil.equalOrBothNull(this.mBase, otherLogarithm.mBase) && GeneralUtil.equalOrBothNull(this.mOperand, otherLogarithm.mOperand);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public int hashCode() {
        return GeneralUtil.hashCode(this.mBase) ^ GeneralUtil.hashCode(this.mOperand);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public String toString() {
        if (this.mBase.equals(MEConstant.e())) {
            return "ln(" + this.mOperand + ")";
        }
        if (this.mBase.equals(new MEValue(10L))) {
            return "log(" + this.mOperand + ")";
        }
        return "log_" + this.mBase + "(" + this.mOperand + ")";
    }
}
