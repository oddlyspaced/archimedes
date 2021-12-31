package com.sparkappdesign.archimedes.mathexpression.expressions;

import com.sparkappdesign.archimedes.mathexpression.context.MEContext;
import com.sparkappdesign.archimedes.mathexpression.context.MEIssue;
import com.sparkappdesign.archimedes.mathexpression.enums.MEContextOptions;
import com.sparkappdesign.archimedes.mathexpression.enums.MEExpressionForm;
import com.sparkappdesign.archimedes.mathexpression.enums.METrigonometricFunctionType;
import com.sparkappdesign.archimedes.mathexpression.numbers.MEInteger;
import com.sparkappdesign.archimedes.mathexpression.numbers.MERational;
import com.sparkappdesign.archimedes.mathexpression.numbers.MEReal;
import com.sparkappdesign.archimedes.utilities.GeneralUtil;
import com.sparkappdesign.archimedes.utilities.observables.ImmutableList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
/* loaded from: classes.dex */
public class METrigonometricFunction extends MEExpression {
    private static final double ROUND_TO_ZERO_LIMIT = 1.0E-8d;
    private static HashMap<MEExpression, MEExpression> exactCosines;
    private static HashMap<MEExpression, MEExpression> exactSines;
    private static HashMap<MEExpression, MEExpression> exactTangents;
    private MEExpression mOperand;
    private METrigonometricFunctionType mType;

    public METrigonometricFunctionType getType() {
        return this.mType;
    }

    public MEExpression getOperand() {
        return this.mOperand;
    }

    private METrigonometricFunction() {
    }

    public METrigonometricFunction(METrigonometricFunctionType type, MEExpression operand) {
        this.mType = type;
        this.mOperand = operand;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public ImmutableList<MEExpression> children() {
        return new ImmutableList<>(Arrays.asList(this.mOperand));
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public METrigonometricFunction copyWithChildren(Collection<? extends MEExpression> children) {
        if (GeneralUtil.equalOrBothNull(children(), children)) {
            return this;
        }
        METrigonometricFunction copy = new METrigonometricFunction();
        copy.mType = this.mType;
        copy.mOperand = (MEExpression) children.iterator().next();
        return copy;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEExpression canonicalize() {
        MEExpression operand = this.mOperand.canonicalize();
        if (shouldStop(operand)) {
            return null;
        }
        if (!METrigonometricFunctionType.isArc(this.mType)) {
            operand = operand.convertToUnit(MEUnit.radians(), false);
            if (shouldStop(operand)) {
                return null;
            }
        }
        if (MEContext.getCurrent().getForm() == MEExpressionForm.Numeric) {
            MEExpression result = tryEvaluatingOperandNumerically(operand);
            if (shouldStop()) {
                return null;
            }
            if (result != null) {
                return result;
            }
        }
        if (this.mType == METrigonometricFunctionType.Sine || this.mType == METrigonometricFunctionType.Cosine) {
            return canonicalizeSineOrCosineForOperand(operand);
        }
        if (this.mType == METrigonometricFunctionType.Tangent) {
            return canonicalizeTangentForOperand(operand);
        }
        if (METrigonometricFunctionType.isArc(this.mType)) {
            return canonicalizeArcFunctionForOperand(operand);
        }
        MEExpression operand2 = convertOperandToDefaultUnit(operand);
        if (shouldStop(operand2)) {
            return null;
        }
        if (operand2.equals(this.mOperand)) {
            return this;
        }
        return new METrigonometricFunction(this.mType, operand2);
    }

    private MEExpression canonicalizeSineOrCosineForOperand(MEExpression operand) {
        MERational twelthsRational;
        MEExpression result;
        if (operand instanceof METrigonometricFunction) {
            METrigonometricFunction operandFunction = (METrigonometricFunction) operand;
            if (this.mType == METrigonometricFunctionType.inverse(operandFunction.mType)) {
                return operandFunction.mOperand;
            }
        }
        boolean operandWasNegative = false;
        if (operand.isNegative()) {
            operand = operand.negate();
            operandWasNegative = true;
        }
        MEValue coefficient = fractionOfHalfCircleForExpression(operand);
        if (coefficient != null) {
            twelthsRational = coefficient.multiplyBy(new MEValue(12L)).getRational();
        } else {
            twelthsRational = null;
        }
        if (twelthsRational == null || twelthsRational.isFractional()) {
            MEExpression operand2 = convertOperandToDefaultUnit(operand);
            if (shouldStop(operand2)) {
                return null;
            }
            result = new METrigonometricFunction(this.mType, operand2);
        } else {
            long n = MEInteger.mod(twelthsRational.numerator(), new MEInteger(24)).toLong();
            if (this.mType == METrigonometricFunctionType.Cosine) {
                n += 6;
            }
            result = expandedFormForSineOfNthTwelthOfPi(n);
            if (shouldStop()) {
                return null;
            }
        }
        if (!operandWasNegative || this.mType == METrigonometricFunctionType.Cosine) {
            return result;
        }
        if (this.mType == METrigonometricFunctionType.Sine) {
            return result.negate().canonicalize();
        }
        return null;
    }

    private MEExpression canonicalizeTangentForOperand(MEExpression operand) {
        if (operand instanceof METrigonometricFunction) {
            METrigonometricFunction operandFunction = (METrigonometricFunction) operand;
            if (operandFunction.mType == METrigonometricFunctionType.ArcTangent) {
                return operandFunction.mOperand;
            }
        }
        MEExpression operand2 = convertOperandToDefaultUnit(operand);
        if (shouldStop(operand2)) {
            return null;
        }
        MEExpression sine = new METrigonometricFunction(METrigonometricFunctionType.Sine, operand2);
        MEExpression cosine = new METrigonometricFunction(METrigonometricFunctionType.Cosine, operand2).canonicalize();
        if (shouldStop()) {
            return null;
        }
        if (!cosine.isZero()) {
            return new MEMultiplications(sine, cosine.invert()).canonicalize();
        }
        stopWithError(MEIssue.TRIGONOMETRIC_DOMAIN);
        return null;
    }

    private MEExpression canonicalizeArcFunctionForOperand(MEExpression operand) {
        MEValue value;
        boolean operandWasNegative = false;
        if (operand.isNegative()) {
            operand = operand.negate();
            operandWasNegative = true;
        }
        MEExpression result = cancelInversesForOperand(operand);
        if (result == null) {
            HashMap<MEExpression, MEExpression> exactAnswers = null;
            if (this.mType == METrigonometricFunctionType.ArcSine) {
                exactAnswers = exactSines();
            }
            if (this.mType == METrigonometricFunctionType.ArcCosine) {
                exactAnswers = exactCosines();
            }
            if (this.mType == METrigonometricFunctionType.ArcTangent) {
                exactAnswers = exactTangents();
            }
            if (!shouldStop(exactAnswers)) {
                Iterator<MEExpression> it = exactAnswers.keySet().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    MEExpression input = it.next();
                    if (operand.equals(exactAnswers.get(input))) {
                        result = new MEMultiplications(input, MEUnit.radians());
                        break;
                    }
                }
            } else {
                return null;
            }
        }
        if (operand instanceof MEValue) {
            value = (MEValue) operand;
        } else {
            value = null;
        }
        if (operand instanceof MEConstant) {
            value = ((MEConstant) operand).getValue();
        }
        if (result != null || value == null || (!(this.mType == METrigonometricFunctionType.ArcSine || this.mType == METrigonometricFunctionType.ArcCosine) || (!value.isMoreThanValue(MEValue.one()) && !value.isLessThanValue(MEValue.minusOne())))) {
            if (result == null) {
                result = new METrigonometricFunction(this.mType, operand);
            }
            if (operandWasNegative) {
                if (this.mType == METrigonometricFunctionType.ArcSine) {
                    result = result.negate();
                }
                if (this.mType == METrigonometricFunctionType.ArcCosine) {
                    result = new MEAdditions(MEContext.getCurrent().getDefaultUnits().get(MEQuantity.angle()).equals(MEUnit.degrees()) ? new MEValue(180L) : MEConstant.pi(), result.negate());
                }
                if (this.mType == METrigonometricFunctionType.ArcTangent) {
                    result = result.negate();
                }
                result = result.canonicalize();
            }
            return result;
        }
        stopWithError(MEIssue.TRIGONOMETRIC_DOMAIN);
        return null;
    }

    private MEExpression cancelInversesForOperand(MEExpression operand) {
        METrigonometricFunction function;
        MEPower power;
        METrigonometricFunction baseFunction;
        if (operand instanceof METrigonometricFunction) {
            METrigonometricFunction operandFunction = (METrigonometricFunction) operand;
            if (this.mType == METrigonometricFunctionType.inverse(operandFunction.mType)) {
                return operandFunction.mOperand;
            }
        } else if (this.mType == METrigonometricFunctionType.ArcTangent && (operand instanceof MEMultiplications)) {
            MEMultiplications multiplications = (MEMultiplications) operand;
            if (multiplications.getOperands().size() == 2) {
                METrigonometricFunction sine = null;
                METrigonometricFunction cosine = null;
                Iterator<MEExpression> it = multiplications.getOperands().iterator();
                while (it.hasNext()) {
                    MEExpression term = it.next();
                    if (term instanceof METrigonometricFunction) {
                        function = (METrigonometricFunction) term;
                    } else {
                        function = null;
                    }
                    if (function == null || function.mType != METrigonometricFunctionType.Sine) {
                        if (term instanceof MEPower) {
                            power = (MEPower) term;
                        } else {
                            power = null;
                        }
                        if (power != null && power.getExponent().equals(MEValue.minusOne())) {
                            if (power.getBase() instanceof METrigonometricFunction) {
                                baseFunction = (METrigonometricFunction) power.getBase();
                            } else {
                                baseFunction = null;
                            }
                            if (baseFunction != null && baseFunction.mType == METrigonometricFunctionType.Cosine) {
                                cosine = baseFunction;
                            }
                        }
                    } else {
                        sine = function;
                    }
                }
                if (!(sine == null || cosine == null || !sine.mOperand.equals(cosine.mOperand))) {
                    return sine.mOperand;
                }
            }
        }
        return null;
    }

    private MEExpression tryEvaluatingOperandNumerically(MEExpression operand) {
        if (operand.containsExpressionOfType(MEVariable.class) || !(operand instanceof MEValue)) {
            return null;
        }
        MEReal value = ((MEValue) operand).getReal();
        switch (this.mType) {
            case Sine:
                value = MEReal.sin(value);
                break;
            case Cosine:
                value = MEReal.cos(value);
                break;
            case Tangent:
                value = MEReal.tan(value);
                break;
            case ArcSine:
                value = MEReal.asin(value);
                break;
            case ArcCosine:
                value = MEReal.acos(value);
                break;
            case ArcTangent:
                value = MEReal.atan(value);
                break;
        }
        if (value == null || value.isNaN() || MEReal.isMore(MEReal.abs(value), new MEReal(1.0E8d))) {
            stopWithError(MEIssue.TRIGONOMETRIC_DOMAIN);
            return null;
        }
        if (MEContext.getCurrent().getOptions().contains(MEContextOptions.ApplyExtraRounding) && MEReal.isLess(MEReal.abs(value), new MEReal((double) ROUND_TO_ZERO_LIMIT))) {
            value = new MEReal(0.0d);
        }
        MEValue result = new MEValue(value);
        if (METrigonometricFunctionType.isArc(this.mType)) {
            return new MEMultiplications(result, MEUnit.radians());
        }
        return result;
    }

    private MEExpression convertOperandToDefaultUnit(MEExpression operand) {
        if (operand == null) {
            return null;
        }
        MEUnit angleUnit = MEContext.getCurrent().getDefaultUnits().get(MEQuantity.angle());
        if (angleUnit == null) {
            angleUnit = MEUnit.radians();
        }
        if (!angleUnit.equals(MEUnit.radians())) {
            operand = new MEMultiplications(operand, MEUnit.radians()).convertToUnit(angleUnit, false);
        }
        if (operand != null) {
            return operand.canonicalize();
        }
        return null;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEReal evaluateNumerically(MEReal value, MEVariable variable) {
        MEReal result;
        MEReal operandValue = this.mOperand.evaluateNumerically(value, variable);
        if (shouldStop(operandValue)) {
            return null;
        }
        MEUnit defaultUnit = MEContext.getCurrent().getDefaultUnits().get(MEQuantity.angle());
        if (defaultUnit == null) {
            defaultUnit = MEUnit.radians();
        }
        if (!METrigonometricFunctionType.isArc(this.mType) && !defaultUnit.equals(MEUnit.radians())) {
            MEReal conversionFactor = defaultUnit.convertToUnit(MEUnit.radians(), false).evaluateNumerically(value, variable);
            if (shouldStop(conversionFactor)) {
                return null;
            }
            operandValue = MEReal.mul(operandValue, conversionFactor);
        }
        switch (this.mType) {
            case Sine:
                result = MEReal.sin(operandValue);
                break;
            case Cosine:
                result = MEReal.cos(operandValue);
                break;
            case Tangent:
                result = MEReal.tan(operandValue);
                break;
            case ArcSine:
                result = MEReal.asin(operandValue);
                break;
            case ArcCosine:
                result = MEReal.acos(operandValue);
                break;
            case ArcTangent:
                result = MEReal.atan(operandValue);
                break;
            default:
                throw new IllegalArgumentException("Invalid enum argument: " + this.mType);
        }
        if (METrigonometricFunctionType.isArc(this.mType) && !defaultUnit.equals(MEUnit.radians())) {
            MEReal conversionFactor2 = MEUnit.radians().convertToUnit(defaultUnit, false).evaluateNumerically(value, variable);
            if (shouldStop(conversionFactor2)) {
                return null;
            }
            result = MEReal.mul(result, conversionFactor2);
        }
        if (!MEContext.getCurrent().getOptions().contains(MEContextOptions.ApplyExtraRounding) || !MEReal.isLess(MEReal.abs(result), new MEReal((double) ROUND_TO_ZERO_LIMIT))) {
            return result;
        }
        return new MEReal(0.0d);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public double evaluateNumerically(double value, MEVariable variable) {
        double result;
        double operandValue = this.mOperand.evaluateNumerically(value, variable);
        if (Double.isInfinite(operandValue) || shouldStop()) {
            return Double.NaN;
        }
        MEExpression defaultAngleUnit = (MEUnit) MEContext.getCurrent().getDefaultUnits().get(MEQuantity.angle());
        MEExpression defaultUnit = defaultAngleUnit != null ? defaultAngleUnit : MEUnit.radians();
        if (METrigonometricFunctionType.isArc(this.mType) && !defaultUnit.equals(MEUnit.radians())) {
            operandValue *= defaultUnit.convertToUnit(MEUnit.radians(), false).evaluateNumerically(value, variable);
        }
        switch (this.mType) {
            case Sine:
                result = Math.sin(operandValue);
                break;
            case Cosine:
                result = Math.cos(operandValue);
                break;
            case Tangent:
                result = Math.tan(operandValue);
                break;
            case ArcSine:
                result = Math.asin(operandValue);
                break;
            case ArcCosine:
                result = Math.acos(operandValue);
                break;
            case ArcTangent:
                result = Math.atan(operandValue);
                break;
            default:
                throw new IllegalArgumentException("Invalid enum argument: " + this.mType);
        }
        if (!Double.isInfinite(result) && METrigonometricFunctionType.isArc(this.mType) && !defaultUnit.equals(MEUnit.radians())) {
            result *= MEUnit.radians().convertToUnit(defaultUnit, false).evaluateNumerically(value, variable);
        }
        if (!MEContext.getCurrent().getOptions().contains(MEContextOptions.ApplyExtraRounding) || Math.abs(result) >= ROUND_TO_ZERO_LIMIT) {
            return result;
        }
        return 0.0d;
    }

    private static HashMap<MEExpression, MEExpression> exactSines() {
        if (exactSines == null) {
            exactSines = new HashMap<>();
            for (int i = 0; i <= 6; i++) {
                MEExpression operand = fractionOfPiExpression((long) i, 12);
                MEExpression result = expandedFormForSineOfNthTwelthOfPi((long) i);
                if (!(operand == null || result == null)) {
                    exactSines.put(operand, result);
                }
            }
            exactSines = new HashMap<>(exactSines);
        }
        return exactSines;
    }

    private static HashMap<MEExpression, MEExpression> exactCosines() {
        if (exactCosines == null) {
            exactCosines = new HashMap<>();
            for (int i = 0; i <= 6; i++) {
                MEExpression operand = fractionOfPiExpression((long) i, 12);
                MEExpression result = expandedFormForSineOfNthTwelthOfPi((long) (6 - i));
                if (!(operand == null || result == null)) {
                    exactCosines.put(operand, result);
                }
            }
            exactCosines = new HashMap<>(exactCosines);
        }
        return exactCosines;
    }

    private static HashMap<MEExpression, MEExpression> exactTangents() {
        MEExpression tangentResult;
        if (exactTangents == null) {
            exactTangents = new HashMap<>();
            HashMap<MEExpression, MEExpression> exactSines2 = exactSines();
            HashMap<MEExpression, MEExpression> exactCosines2 = exactCosines();
            for (MEExpression operand : exactSines2.keySet()) {
                MEExpression sineResult = exactSines2.get(operand);
                MEExpression cosineResult = exactCosines2.get(operand);
                if (!cosineResult.isZero() && (tangentResult = new MEMultiplications(sineResult, cosineResult.invert()).canonicalize()) != null) {
                    exactTangents.put(operand, tangentResult);
                }
            }
            exactTangents = new HashMap<>(exactTangents);
        }
        return exactTangents;
    }

    private static MEExpression expandedFormForSineOfNthTwelthOfPi(long n) {
        boolean isNegative = false;
        long n2 = n % 24;
        if (n2 < 0) {
            n2 += 24;
        }
        if (n2 > 12) {
            isNegative = true;
            n2 -= 12;
        }
        if (n2 > 6) {
            n2 = 12 - n2;
        }
        MEExpression result = null;
        if (n2 == 0) {
            result = MEValue.zero();
        }
        if (n2 == 6) {
            result = MEValue.one();
        }
        if (n2 == 2) {
            result = new MEValue(new MERational(1, 2));
        }
        if (n2 == 3 || n2 == 4) {
            result = new MEMultiplications(new MEValue(1, 2), MEPower.powerForSquareRootWithBase(new MEValue((long) (n2 == 3 ? 2 : 3))));
        }
        if (n2 == 1 || n2 == 5) {
            MEExpression sqrt6 = MEPower.powerForSquareRootWithBase(new MEValue(6L));
            MEExpression sqrt2 = MEPower.powerForSquareRootWithBase(new MEValue(2L));
            if (n2 == 1) {
                sqrt2 = sqrt2.negate();
            }
            result = new MEMultiplications(new MEValue(1, 4), new MEAdditions(sqrt6, sqrt2));
        }
        if (result == null) {
            return null;
        }
        if (isNegative) {
            return result.negate().canonicalize();
        }
        return result.canonicalize();
    }

    private static MEExpression fractionOfPiExpression(long numerator, long denominator) {
        if (numerator == 0) {
            return MEValue.zero();
        }
        if (numerator == denominator) {
            return MEConstant.pi();
        }
        MEExpression expression = new MEMultiplications(new MEValue(numerator, denominator), MEConstant.pi()).canonicalize();
        if (shouldStop(expression)) {
            return null;
        }
        return expression;
    }

    private static MEValue fractionOfHalfCircleForExpression(MEExpression expression) {
        MEValue coefficient = expression.coefficient();
        if (coefficient.isZero()) {
            return coefficient;
        }
        MEExpression partWithoutCoefficient = expression.partWithoutCoefficient();
        if (partWithoutCoefficient == null || !partWithoutCoefficient.equals(MEConstant.pi())) {
            return null;
        }
        return coefficient;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof METrigonometricFunction)) {
            return false;
        }
        METrigonometricFunction otherTrigonometricFunction = (METrigonometricFunction) other;
        return this.mType == otherTrigonometricFunction.mType && GeneralUtil.equalOrBothNull(this.mOperand, otherTrigonometricFunction.mOperand);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public int hashCode() {
        return GeneralUtil.hashCode(this.mType) ^ GeneralUtil.hashCode(this.mOperand);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public String toString() {
        return name() + " (" + this.mOperand + ")";
    }

    private String name() {
        switch (this.mType) {
            case Sine:
                return "sin";
            case Cosine:
                return "cos";
            case Tangent:
                return "tan";
            case ArcSine:
                return "asin";
            case ArcCosine:
                return "acos";
            case ArcTangent:
                return "atan";
            default:
                throw new IllegalArgumentException("Invalid enum argument: " + this.mType);
        }
    }
}
