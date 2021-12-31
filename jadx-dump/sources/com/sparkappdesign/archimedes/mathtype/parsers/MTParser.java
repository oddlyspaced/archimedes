package com.sparkappdesign.archimedes.mathtype.parsers;

import com.sparkappdesign.archimedes.mathexpression.enums.METrigonometricFunctionType;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEAdditions;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEConstant;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEEquals;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression;
import com.sparkappdesign.archimedes.mathexpression.expressions.MELogarithm;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEMultiplications;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEPlaceholder;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEPower;
import com.sparkappdesign.archimedes.mathexpression.expressions.METrigonometricFunction;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEUnit;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEValue;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEVariable;
import com.sparkappdesign.archimedes.mathexpression.numbers.MEInteger;
import com.sparkappdesign.archimedes.mathtype.MTMEVariableIdentifier;
import com.sparkappdesign.archimedes.mathtype.enums.MTAssociativity;
import com.sparkappdesign.archimedes.mathtype.enums.MTInlineOperatorType;
import com.sparkappdesign.archimedes.mathtype.enums.MTNumericCharacterType;
import com.sparkappdesign.archimedes.mathtype.enums.MTOperandSide;
import com.sparkappdesign.archimedes.mathtype.enums.MTOperatorNotation;
import com.sparkappdesign.archimedes.mathtype.enums.MTParenthesesPlacement;
import com.sparkappdesign.archimedes.mathtype.enums.MTPrecedence;
import com.sparkappdesign.archimedes.mathtype.nodes.MTElement;
import com.sparkappdesign.archimedes.mathtype.nodes.MTString;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTDivision;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTInlineOperator;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTLogarithm;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTNumericCharacter;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTParentheses;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTPower;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTPowerOfTenExponent;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTReference;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTRoot;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTText;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTVariable;
import com.sparkappdesign.archimedes.utilities.Range;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public class MTParser {
    public ArrayList<MEExpression> parseStrings(List<MTString> strings) {
        ArrayList<MEExpression> expressions = new ArrayList<>();
        for (MTString string : strings) {
            MEExpression expression = parseString(string);
            if (expression != null) {
                expressions.add(expression);
            }
        }
        return expressions;
    }

    private MEExpression parseString(MTString string) {
        return parseStringFromIndexAsRightValueForOperator(string, new AtomicInteger(0), null);
    }

    private MEExpression parseStringFromIndexAsRightValueForOperator(MTString string, AtomicInteger index, MTOperatorInfo callingOperator) {
        MTElement previousElement;
        int startIndex = index.get();
        MEExpression value = null;
        while (index.get() < string.length()) {
            MTElement nextElement = string.elementAtIndex(index.get());
            if (index.get() > startIndex) {
                previousElement = string.elementBefore(nextElement);
            } else {
                previousElement = null;
            }
            MTOperatorInfo nextElementAsOperator = MTOperatorInfo.infoForElement(nextElement);
            if (shouldImplicitlyMultiplyElements(previousElement, nextElement)) {
                nextElementAsOperator = MTOperatorInfo.infoForImplicitMultiplication();
                nextElement = new MTInlineOperator(MTInlineOperatorType.Dot);
            }
            if (nextElementAsOperator != null) {
                if (callingOperator != null && !shouldOperatorBePartOfOperandOfOperator(nextElementAsOperator, MTOperandSide.Right, callingOperator)) {
                    return value;
                }
                value = parseOperatorWithElementAndLeftValueInStringAtIndex(nextElementAsOperator, nextElement, value, string, index);
                if (value == null) {
                    return null;
                }
            } else if (value != null || (value = parseNextExpressionInStringAtIndex(string, index)) == null) {
                return null;
            }
        }
        return value;
    }

    private MEExpression parseOperatorWithElementAndLeftValueInStringAtIndex(MTOperatorInfo operator, MTElement element, MEExpression leftValue, MTString string, AtomicInteger index) {
        MEExpression rightValue;
        MEExpression rightValue2;
        if (!operator.isImplicit()) {
            index.incrementAndGet();
        }
        if (operator.getNotation() == MTOperatorNotation.Infix && leftValue != null && (rightValue2 = parseStringFromIndexAsRightValueForOperator(string, index, operator)) != null) {
            return parseOperatorElementWithLeftValueAndRightValue(element, leftValue, rightValue2);
        }
        if (operator.getNotation() == MTOperatorNotation.Postfix && leftValue != null) {
            return parseOperatorElementWithLeftValueAndRightValue(element, leftValue, null);
        }
        if (operator.getNotation() == MTOperatorNotation.Prefix && leftValue == null && (rightValue = parseStringFromIndexAsRightValueForOperator(string, index, operator)) != null) {
            return parseOperatorElementWithLeftValueAndRightValue(element, null, rightValue);
        }
        return null;
    }

    private MEExpression parseOperatorElementWithLeftValueAndRightValue(MTElement element, MEExpression leftValue, MEExpression rightValue) {
        MEExpression exponent;
        if (element instanceof MTInlineOperator) {
            MTInlineOperator inlineOperator = (MTInlineOperator) element;
            MTInlineOperatorType type = inlineOperator.getType();
            if (inlineOperator.isSign()) {
                return type == MTInlineOperatorType.Minus ? rightValue.negate() : rightValue;
            }
            if (MTInlineOperatorType.isTrigonometric(type)) {
                return new METrigonometricFunction(METrigonometricFunctionType.fromMTInlineOperatorType(type), rightValue);
            }
            switch (type) {
                case Plus:
                    return new MEAdditions(leftValue, rightValue);
                case Minus:
                    return new MEAdditions(leftValue, rightValue.negate());
                case Dot:
                    return new MEMultiplications(leftValue, rightValue);
                case Division:
                    return new MEMultiplications(leftValue, rightValue.invert());
                case Power:
                    return MEPower.powerWithBaseAndExponent(leftValue, rightValue);
                case SquareRoot:
                    return MEPower.powerForSquareRootWithBase(rightValue);
                case NaturalLogarithm:
                    return new MELogarithm(MEConstant.e(), rightValue);
                case Equals:
                    return new MEEquals(leftValue, rightValue);
                case EngineeringExponent:
                    return new MEMultiplications(leftValue, MEPower.powerWithBaseAndExponent(new MEValue(10L), rightValue));
            }
        }
        if (element instanceof MTPower) {
            MEExpression exponent2 = parseString(((MTPower) element).getExponent());
            if (exponent2 == null) {
                return null;
            }
            return MEPower.powerWithBaseAndExponent(leftValue, exponent2);
        } else if (element instanceof MTLogarithm) {
            MTLogarithm logarithm = (MTLogarithm) element;
            MEExpression base = logarithm.getBase().isNotEmpty() ? parseString(logarithm.getBase()) : new MEValue(10L);
            if (base == null) {
                return null;
            }
            return new MELogarithm(base, rightValue);
        } else if (!(element instanceof MTPowerOfTenExponent) || (exponent = parseString(((MTPowerOfTenExponent) element).getExponent())) == null) {
            return null;
        } else {
            return new MEMultiplications(leftValue, MEPower.powerWithBaseAndExponent(new MEValue(10L), exponent));
        }
    }

    private MEExpression parseNextExpressionInStringAtIndex(MTString string, AtomicInteger index) {
        MEValue number = parseNumberInStringAtIndex(string, index);
        if (number != null) {
            return number;
        }
        MEExpression result = parseExpressionElement(string.elementAtIndex(index.get()));
        if (result == null) {
            return null;
        }
        index.incrementAndGet();
        return result;
    }

    private MEExpression parseExpressionElement(MTElement element) {
        if (element instanceof MTDivision) {
            return parseDivision((MTDivision) element);
        }
        if (element instanceof MTRoot) {
            return parseRoot((MTRoot) element);
        }
        if (element instanceof MTVariable) {
            return parseVariable((MTVariable) element);
        }
        if (element instanceof MTParentheses) {
            return parseString(((MTParentheses) element).getContents());
        }
        if (element instanceof MTText) {
            return parseText((MTText) element);
        }
        if (element instanceof MTReference) {
            return parseReference((MTReference) element);
        }
        return null;
    }

    private MEValue parseNumberInStringAtIndex(MTString string, AtomicInteger index) {
        MEInteger numerator = null;
        MEInteger denominator = null;
        boolean parsedDecimalPoint = false;
        int i = index.get();
        while (i < string.length()) {
            MTElement element = string.elementAtIndex(i);
            if (!(element instanceof MTNumericCharacter)) {
                break;
            }
            MTNumericCharacterType type = ((MTNumericCharacter) element).getType();
            if (type == MTNumericCharacterType.RadixPoint) {
                if (parsedDecimalPoint) {
                    return null;
                }
                parsedDecimalPoint = true;
            } else if (type.ordinal() >= MTNumericCharacterType.Number0.ordinal() && type.ordinal() <= MTNumericCharacterType.Number9.ordinal()) {
                if (numerator == null) {
                    numerator = new MEInteger(0);
                }
                if (denominator == null) {
                    denominator = new MEInteger(1);
                }
                numerator = MEInteger.add(MEInteger.mul(numerator, new MEInteger(10)), new MEInteger((long) type.ordinal()));
                if (parsedDecimalPoint) {
                    denominator = MEInteger.mul(denominator, new MEInteger(10));
                }
            }
            i++;
        }
        if (numerator == null || denominator == null) {
            return null;
        }
        index.set(i);
        return new MEValue(numerator, denominator);
    }

    private MEExpression parseDivision(MTDivision division) {
        MEExpression dividend = parseString(division.getDividend());
        MEExpression divisor = parseString(division.getDivisor());
        if (dividend == null || divisor == null) {
            return null;
        }
        return new MEMultiplications(dividend, MEPower.powerWithBaseAndExponent(divisor, MEValue.minusOne()));
    }

    private MEPower parseRoot(MTRoot root) {
        MEExpression exponent;
        MEExpression content = parseString(root.getContents());
        MEExpression degree = parseString(root.getDegree());
        if (content == null) {
            return null;
        }
        if (degree == null) {
            if (root.getDegree().isNotEmpty()) {
                return null;
            }
            degree = new MEValue(2L);
        }
        if (!(degree instanceof MEValue) || degree.isZero()) {
            exponent = MEPower.powerWithBaseAndExponent(degree, MEValue.minusOne());
        } else {
            exponent = degree.invert();
        }
        return MEPower.powerWithBaseAndExponent(content, exponent);
    }

    private MEVariable parseVariable(MTVariable variable) {
        return new MEVariable(new MTMEVariableIdentifier(variable.getName()));
    }

    private MEExpression parseText(MTText element) {
        String text = element.getText();
        if (text.equals("π")) {
            return MEConstant.pi();
        }
        if (text.equals("e")) {
            return MEConstant.e();
        }
        if (text.equals("°")) {
            return MEUnit.degrees();
        }
        if (text.equals(" rad")) {
            return MEUnit.radians();
        }
        return null;
    }

    private MEExpression parseReference(MTReference reference) {
        return new MEPlaceholder(reference.copy());
    }

    private static boolean shouldImplicitlyMultiplyElements(MTElement element1, MTElement element2) {
        boolean element1IsOperandOf2;
        boolean element2IsOperandOf1;
        boolean bothNumeric;
        if (element1 == null || element2 == null || element1.getParent() != element2.getParent()) {
            return false;
        }
        if (element1.nextElement() != element2) {
            if (element2.nextElement() != element1) {
                return false;
            }
            element1 = element2;
            element2 = element1;
        }
        MTOperatorInfo operatorInfo1 = MTOperatorInfo.infoForElement(element1);
        MTOperatorInfo operatorInfo2 = MTOperatorInfo.infoForElement(element2);
        boolean element1IsNumeric = element1 instanceof MTNumericCharacter;
        boolean element2IsNumeric = element2 instanceof MTNumericCharacter;
        if (operatorInfo2 == null || !(operatorInfo2.getNotation() == MTOperatorNotation.Postfix || operatorInfo2.getNotation() == MTOperatorNotation.Infix)) {
            element1IsOperandOf2 = false;
        } else {
            element1IsOperandOf2 = true;
        }
        if (operatorInfo1 == null || !(operatorInfo1.getNotation() == MTOperatorNotation.Prefix || operatorInfo1.getNotation() == MTOperatorNotation.Infix)) {
            element2IsOperandOf1 = false;
        } else {
            element2IsOperandOf1 = true;
        }
        if (!element1IsNumeric || !element2IsNumeric) {
            bothNumeric = false;
        } else {
            bothNumeric = true;
        }
        return !element1IsOperandOf2 && !element2IsOperandOf1 && !bothNumeric;
    }

    private static boolean useImplicitMultiplicationBetween(MTElement element1, MTElement element2) {
        boolean element1IsOperandOf2;
        boolean element2IsOperandOf1;
        boolean bothNumeric;
        if (element1 == null || element2 == null || element1.getParent() != element2.getParent()) {
            return false;
        }
        if (element1.nextElement() != element2) {
            if (element2.nextElement() != element1) {
                return false;
            }
            element1 = element2;
            element2 = element1;
        }
        MTOperatorInfo operatorInfo1 = MTOperatorInfo.infoForElement(element1);
        MTOperatorInfo operatorInfo2 = MTOperatorInfo.infoForElement(element2);
        boolean element1IsNumeric = element1 instanceof MTNumericCharacter;
        boolean element2IsNumeric = element2 instanceof MTNumericCharacter;
        if (operatorInfo2 == null || !(operatorInfo2.getNotation() == MTOperatorNotation.Postfix || operatorInfo2.getNotation() == MTOperatorNotation.Infix)) {
            element1IsOperandOf2 = false;
        } else {
            element1IsOperandOf2 = true;
        }
        if (operatorInfo1 == null || !(operatorInfo1.getNotation() == MTOperatorNotation.Prefix || operatorInfo1.getNotation() == MTOperatorNotation.Infix)) {
            element2IsOperandOf1 = false;
        } else {
            element2IsOperandOf1 = true;
        }
        if (!element1IsNumeric || !element2IsNumeric) {
            bothNumeric = false;
        } else {
            bothNumeric = true;
        }
        return !element1IsOperandOf2 && !element2IsOperandOf1 && !bothNumeric;
    }

    private static boolean shouldOperatorBePartOfOperandOfOperator(MTOperatorInfo operatorInOperand, MTOperandSide operand, MTOperatorInfo operator) {
        if (operand == MTOperandSide.Left && operatorInOperand.getNotation() == MTOperatorNotation.Postfix) {
            return true;
        }
        if ((operand == MTOperandSide.Right && operatorInOperand.getNotation() == MTOperatorNotation.Prefix) || operatorInOperand.getPrecedence().ordinal() > operator.getPrecedence().ordinal()) {
            return true;
        }
        if (operatorInOperand.getPrecedence() == operator.getPrecedence()) {
            if (operand == MTOperandSide.Right && operator.getAssociativity() == MTAssociativity.RightAssociative) {
                return true;
            }
            if (operand == MTOperandSide.Left && operator.getAssociativity() != MTAssociativity.RightAssociative) {
                return true;
            }
        }
        return false;
    }

    public static void replaceInlineOperatorsWithContainerFormsWherePossible(MTString string) {
        int index = 0;
        while (index < string.length()) {
            MTElement element = string.elementAtIndex(index);
            if (element instanceof MTInlineOperator) {
                MTInlineOperatorType type = ((MTInlineOperator) element).getType();
                if (type == MTInlineOperatorType.Division) {
                    MTDivision division = new MTDivision();
                    grabOperandForOperatorElementIntoString(MTOperandSide.Left, element, division.getDividend());
                    index = grabOperandForOperatorElementIntoString(MTOperandSide.Right, element, division.getDivisor());
                    replaceInlineOperatorsWithContainerFormsWherePossible(division.getDivisor());
                    string.replaceElement(index, division);
                } else if (type == MTInlineOperatorType.Power) {
                    MTPower power = new MTPower();
                    index = grabOperandForOperatorElementIntoString(MTOperandSide.Right, element, power.getExponent());
                    replaceInlineOperatorsWithContainerFormsWherePossible(power.getExponent());
                    string.replaceElement(index, power);
                } else if (type == MTInlineOperatorType.SquareRoot) {
                    MTRoot root = new MTRoot(false);
                    index = grabOperandForOperatorElementIntoString(MTOperandSide.Right, element, root.getContents());
                    replaceInlineOperatorsWithContainerFormsWherePossible(root.getContents());
                    string.replaceElement(index, root);
                }
            }
            index++;
        }
    }

    private static int grabOperandForOperatorElementIntoString(MTOperandSide operandSide, MTElement element, MTString string) {
        MTOperatorInfo operatorInfo = MTOperatorInfo.infoForElement(element);
        MTString parentString = element.getParent();
        Range implicitRange = rangeForOperandOfOperatorAtIndexInString(operandSide, operatorInfo, element.indexInParentString(), parentString, true);
        boolean stringWasEmpty = string.isEmpty();
        parentString.moveElementsInRangeToString(implicitRange, string, string.indexAfterLastElement());
        if (stringWasEmpty) {
            while (string.length() == 1) {
                MTElement childElement = string.elementAtIndex(0);
                if (!(childElement instanceof MTParentheses)) {
                    break;
                }
                MTString contents = ((MTParentheses) childElement).getContents();
                string.removeElementAtIndex(0);
                contents.moveElementsToString(string, 0);
            }
        }
        return element.indexInParentString();
    }

    public static Range rangeOfInfluenceForElementsInRangeInString(Range range, MTString string, boolean includeImplicitlyMultipliedElements) {
        MTPrecedence rightPrecedence = includeImplicitlyMultipliedElements ? MTPrecedence.ImplicitMultiply : MTPrecedence.Highest;
        MTPrecedence leftPrecedence = rightPrecedence;
        ArrayList<MTElement> children = (ArrayList) string.getChildren();
        for (int i = range.mStartIndex; i < range.getMaxRange(); i++) {
            MTOperatorInfo operator = MTOperatorInfo.infoForElement(children.get(i));
            if (operator != null) {
                if (operator.hasOperand(MTOperandSide.Left) && leftPrecedence.ordinal() >= operator.getPrecedence().ordinal()) {
                    leftPrecedence = operator.getPrecedence();
                }
                if (operator.hasOperand(MTOperandSide.Right) && rightPrecedence.ordinal() >= operator.getPrecedence().ordinal()) {
                    rightPrecedence = operator.getPrecedence();
                }
            }
        }
        int startIndex = range.mStartIndex;
        int endIndex = range.getMaxRange();
        while (startIndex > 0 && doesRangeOfInfluenceExtendFromElementToElementWithPrecedence(string.elementAtIndex(startIndex), string.elementAtIndex(startIndex - 1), leftPrecedence)) {
            startIndex--;
        }
        while (endIndex < string.length() && doesRangeOfInfluenceExtendFromElementToElementWithPrecedence(string.elementAtIndex(endIndex - 1), string.elementAtIndex(endIndex), rightPrecedence)) {
            endIndex++;
        }
        return new Range(startIndex, endIndex - startIndex);
    }

    private static boolean doesRangeOfInfluenceExtendFromElementToElementWithPrecedence(MTElement fromElement, MTElement toElement, MTPrecedence precedence) {
        boolean z = false;
        MTOperandSide toSide = toElement.indexInParentString() < fromElement.indexInParentString() ? MTOperandSide.Left : MTOperandSide.Right;
        MTOperatorInfo toOperatorInfo = MTOperatorInfo.infoForElement(toElement);
        if (toOperatorInfo == null) {
            return !useImplicitMultiplicationBetween(fromElement, toElement) || precedence.ordinal() <= MTPrecedence.ImplicitMultiply.ordinal();
        }
        if (precedence.ordinal() < toOperatorInfo.getPrecedence().ordinal() || !toOperatorInfo.hasOperand(MTOperandSide.opposite(toSide))) {
            z = true;
        }
        return z;
    }

    public static EnumSet<MTParenthesesPlacement> parenthesesPlacementForOperatorToGrabElementsInRangeInString(MTOperatorInfo operatorInfo, Range range, MTString string) {
        int operatorIndex;
        EnumSet<MTParenthesesPlacement> parenthesesPlacement = EnumSet.noneOf(MTParenthesesPlacement.class);
        int leftOfRange = range.mStartIndex;
        int rightOfRange = range.getMaxRange();
        if (operatorInfo.getNotation() == MTOperatorNotation.Prefix) {
            operatorIndex = leftOfRange;
        } else {
            operatorIndex = rightOfRange;
        }
        Range implicitRange = rangeForOperandOfOperatorAtIndexInString(operatorInfo.getNotation() == MTOperatorNotation.Prefix ? MTOperandSide.Right : MTOperandSide.Left, operatorInfo, operatorIndex, string, false);
        if (operatorInfo.getNotation() == MTOperatorNotation.Infix && rangeForOperandOfOperatorAtIndexInString(MTOperandSide.Right, operatorInfo, operatorIndex, string, false).mLength > 0) {
            parenthesesPlacement.add(MTParenthesesPlacement.AroundOperation);
        }
        if (implicitRange.mLength < range.mLength) {
            parenthesesPlacement.add(MTParenthesesPlacement.AroundOperand);
        } else if (implicitRange.mLength > range.mLength) {
            boolean parenthesesNeeded = true;
            if (operatorInfo.getNotation() == MTOperatorNotation.Infix) {
                MTOperatorInfo previousOperatorInfo = MTOperatorInfo.infoForElement(range.mStartIndex > 0 ? string.elementAtIndex(range.mStartIndex - 1) : null);
                if (previousOperatorInfo != null && previousOperatorInfo.getPrecedence() == operatorInfo.getPrecedence() && previousOperatorInfo.getAssociativity() == MTAssociativity.Associative) {
                    parenthesesNeeded = false;
                }
            }
            if (parenthesesNeeded) {
                parenthesesPlacement.add(MTParenthesesPlacement.AroundOperation);
            }
        }
        return parenthesesPlacement;
    }

    public static Range rangeForOperandsOfOperatorAtIndexInString(MTOperatorInfo operatorInfo, int operatorIndex, MTString string, boolean isOperatorActuallyInString) {
        int indexAfterOperator;
        Range resultRange;
        if (isOperatorActuallyInString) {
            indexAfterOperator = operatorIndex + 1;
        } else {
            indexAfterOperator = operatorIndex;
        }
        switch (operatorInfo.getNotation()) {
            case Prefix:
                resultRange = new Range(indexAfterOperator, 0);
                break;
            case Postfix:
                resultRange = new Range(operatorIndex, 0);
                break;
            case Infix:
                resultRange = new Range(operatorIndex, indexAfterOperator - operatorIndex);
                break;
            default:
                throw new IllegalArgumentException("Invalid enum argument: " + operatorInfo.getNotation());
        }
        if (operatorInfo.getNotation() == MTOperatorNotation.Postfix || operatorInfo.getNotation() == MTOperatorNotation.Infix) {
            resultRange = Range.union(resultRange, rangeForOperandOfOperatorAtIndexInString(MTOperandSide.Left, operatorInfo, operatorIndex, string, isOperatorActuallyInString));
        }
        if (operatorInfo.getNotation() == MTOperatorNotation.Prefix || operatorInfo.getNotation() == MTOperatorNotation.Infix) {
            return Range.union(resultRange, rangeForOperandOfOperatorAtIndexInString(MTOperandSide.Right, operatorInfo, operatorIndex, string, isOperatorActuallyInString));
        }
        return resultRange;
    }

    public static Range rangeForOperandOfOperatorAtIndexInString(MTOperandSide operand, MTOperatorInfo operatorInfo, int operatorIndex, MTString string, boolean isOperatorActuallyInString) {
        int index = operatorIndex;
        if (operand == MTOperandSide.Left) {
            index--;
        } else if (isOperatorActuallyInString) {
            index++;
        }
        if (operatorInfo.getNotation() == MTOperatorNotation.Prefix && operand == MTOperandSide.Left) {
            return new Range(index, 0);
        }
        if (operatorInfo.getNotation() == MTOperatorNotation.Postfix && operand == MTOperandSide.Right) {
            return new Range(index, 0);
        }
        MTElement previousElement = null;
        int length = 0;
        while (index >= 0 && index < string.length()) {
            MTElement nextElement = string.elementAtIndex(index);
            MTOperatorInfo nextElementOperator = MTOperatorInfo.infoForElement(nextElement);
            if (useImplicitMultiplicationBetween(previousElement, nextElement)) {
                nextElementOperator = MTOperatorInfo.infoForImplicitMultiplication();
            }
            if (nextElementOperator != null && !shouldOperatorBePartOfOperandOfOperator(nextElementOperator, operand, operatorInfo)) {
                break;
            }
            previousElement = nextElement;
            index = operand == MTOperandSide.Right ? index + 1 : index - 1;
            length++;
        }
        return new Range(operand == MTOperandSide.Right ? index - length : index + 1, length);
    }

    public static Range rangeForOperandOfOperatorElement(MTOperandSide operandSide, MTElement element) {
        return rangeForOperandOfOperatorAtIndexInString(operandSide, MTOperatorInfo.infoForElement(element), element.indexInParentString(), element.getParent(), true);
    }
}
