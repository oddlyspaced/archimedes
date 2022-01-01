package com.oddlyspaced.calci.mathtype.writers;

import com.oddlyspaced.calci.mathexpression.enums.MEExpressionForm;
import com.oddlyspaced.calci.mathexpression.expressions.MEAdditions;
import com.oddlyspaced.calci.mathexpression.expressions.MEConstant;
import com.oddlyspaced.calci.mathexpression.expressions.MEEquals;
import com.oddlyspaced.calci.mathexpression.expressions.MEExpression;
import com.oddlyspaced.calci.mathexpression.expressions.MELogarithm;
import com.oddlyspaced.calci.mathexpression.expressions.MEMultiplications;
import com.oddlyspaced.calci.mathexpression.expressions.MEPlaceholder;
import com.oddlyspaced.calci.mathexpression.expressions.MEPower;
import com.oddlyspaced.calci.mathexpression.expressions.MEQuantity;
import com.oddlyspaced.calci.mathexpression.expressions.METrigonometricFunction;
import com.oddlyspaced.calci.mathexpression.expressions.MEUnit;
import com.oddlyspaced.calci.mathexpression.expressions.MEValue;
import com.oddlyspaced.calci.mathexpression.expressions.MEVariable;
import com.oddlyspaced.calci.mathexpression.numbers.MEInteger;
import com.oddlyspaced.calci.mathexpression.numbers.MERational;
import com.oddlyspaced.calci.mathtype.MTMEPlaceholderIdentifier;
import com.oddlyspaced.calci.mathtype.MTMEVariableIdentifier;
import com.oddlyspaced.calci.mathtype.enums.MTInlineOperatorType;
import com.oddlyspaced.calci.mathtype.enums.MTNodeTraits;
import com.oddlyspaced.calci.mathtype.enums.MTNumericCharacterType;
import com.oddlyspaced.calci.mathtype.enums.MTPrecedence;
import com.oddlyspaced.calci.mathtype.nodes.MTElement;
import com.oddlyspaced.calci.mathtype.nodes.MTString;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTDivision;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTInlineOperator;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTLogarithm;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTNumericCharacter;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTParentheses;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTPower;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTReference;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTRoot;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTText;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTVariable;
import com.oddlyspaced.calci.mathtype.parsers.MTOperatorInfo;
import com.oddlyspaced.calci.mathtype.parsers.MTParser;
import com.oddlyspaced.calci.utilities.IterableUtil;
import com.oddlyspaced.calci.utilities.ListUtil;
import com.oddlyspaced.calci.utilities.Range;
import com.oddlyspaced.calci.utilities.observables.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
/* loaded from: classes.dex */
public class MTWriter {
    private MEExpressionForm mForm;
    private MTNumberFormatter mNumberFormatter;
    private ArrayList<MTOperatorRange> mOperatorRanges;

    /* loaded from: classes.dex */
    public class MTOperatorRange {
        MTOperatorInfo mOperator;
        int mOperatorIndex;
        Range mRange;
        MTString mString;

        MTOperatorRange() {
            MTWriter.this = this$0;
        }
    }

    public MEExpressionForm getForm() {
        return this.mForm;
    }

    public MTNumberFormatter getNumberFormatter() {
        return this.mNumberFormatter;
    }

    public void setForm(MEExpressionForm form) {
        this.mForm = form;
    }

    public void setNumberFormatter(MTNumberFormatter numberFormatter) {
        this.mNumberFormatter = numberFormatter;
    }

    public ArrayList<MTString> writeExpressions(Collection<MEExpression> expressions) {
        ArrayList<MTString> strings = new ArrayList<>();
        Iterator<MEExpression> it = expressions.iterator();
        while (it.hasNext()) {
            MEExpression expression = it.next();
            MTString string = expression != null ? writeExpression(expression) : null;
            if (string == null) {
                string = new MTString();
            }
            strings.add(string);
        }
        return strings;
    }

    public MTString writeExpression(MEExpression expression) {
        MTString string = new MTString();
        this.mOperatorRanges = new ArrayList<>();
        writeExpression(preprocessExpression(expression), string);
        addParenthesesToEnforceOperatorRanges();
        this.mOperatorRanges = null;
        return string;
    }

    public Range writeExpression(MEExpression expression, MTString string) {
        int startIndex = string.indexAfterLastElement();
        if (expression instanceof MEValue) {
            writeValue((MEValue) expression, string);
        } else if (expression instanceof MEConstant) {
            writeConstant((MEConstant) expression, string);
        } else if (expression instanceof MEAdditions) {
            writeAdditions((MEAdditions) expression, string);
        } else if (expression instanceof MEMultiplications) {
            writeMultiplications((MEMultiplications) expression, string);
        } else if (expression instanceof MEPower) {
            writePower((MEPower) expression, string);
        } else if (expression instanceof MEVariable) {
            writeVariable((MEVariable) expression, string);
        } else if (expression instanceof METrigonometricFunction) {
            writeTrigonometricFunction((METrigonometricFunction) expression, string);
        } else if (expression instanceof MELogarithm) {
            writeLogarithm((MELogarithm) expression, string);
        } else if (expression instanceof MEEquals) {
            writeEquals((MEEquals) expression, string);
        } else if (expression instanceof MEUnit) {
            writeUnit((MEUnit) expression, string);
        } else if (expression instanceof MEQuantity) {
            writeQuantity((MEQuantity) expression, string);
        } else if (expression instanceof MEPlaceholder) {
            writePlaceholder((MEPlaceholder) expression, string);
        }
        return new Range(startIndex, string.indexAfterLastElement() - startIndex);
    }

    private Range appendElementToString(MTElement element, MTString string) {
        int startIndex = string.indexAfterLastElement();
        string.appendElement(element);
        return new Range(startIndex, 1);
    }

    private MEExpression preprocessExpression(MEExpression expression) {
        return preprocessExpressionRecursive(expression, null);
    }

    private MEExpression preprocessExpressionRecursive(MEExpression expression, MEExpression parent) {
        if (expression instanceof MEUnit) {
            boolean shouldPrefixNumber = true;
            if (parent instanceof MEMultiplications) {
                boolean containsNonUnit = false;
                Iterator<MEExpression> it = ((MEMultiplications) parent).getOperands().iterator();
                while (it.hasNext()) {
                    if (!(it.next() instanceof MEUnit)) {
                        containsNonUnit = true;
                    }
                }
                if (containsNonUnit) {
                    shouldPrefixNumber = false;
                }
            } else if (parent instanceof MEPower) {
                shouldPrefixNumber = ((MEPower) parent).getBase() != expression;
            }
            if (shouldPrefixNumber) {
                return new MEMultiplications(MEValue.one(), expression);
            }
        }
        ImmutableList<MEExpression> children = expression.children();
        ArrayList<MEExpression> newChildren = new ArrayList<>();
        if (children == null) {
            return expression;
        }
        Iterator<MEExpression> it2 = children.iterator();
        while (it2.hasNext()) {
            newChildren.add(preprocessExpressionRecursive(it2.next(), expression));
        }
        return expression.copyWithChildren(newChildren);
    }

    private void writeValue(MEValue value, MTString string) {
        Range operandRange;
        MTElement sign = null;
        if (value.isNegative()) {
            value = value.negate();
            sign = new MTInlineOperator(MTInlineOperatorType.Minus);
            string.appendElement(sign);
        }
        if (value.getRational() == null || this.mForm != MEExpressionForm.Exact) {
            operandRange = this.mNumberFormatter.writeRealToString(value.getReal(), string);
        } else {
            MERational rational = value.getRational();
            MEInteger numerator = rational.numerator();
            MEInteger denominator = rational.denominator();
            if (!rational.isFractional()) {
                operandRange = this.mNumberFormatter.writeIntegerToString(numerator, string);
            } else {
                MTDivision division = new MTDivision();
                this.mNumberFormatter.writeIntegerToString(numerator, division.getDividend());
                this.mNumberFormatter.writeIntegerToString(denominator, division.getDivisor());
                operandRange = appendElementToString(division, string);
            }
        }
        if (sign != null) {
            registerRange(operandRange, sign);
        }
    }

    private void writeConstant(MEConstant constant, MTString string) {
        appendElementToString(new MTText(constant.getName()), string);
    }

    private void writeAdditions(MEAdditions additions, MTString string) {
        Range totalRange = new Range(string.indexAfterLastElement(), 0);
        Iterator<MEExpression> it = additions.getOperands().iterator();
        while (it.hasNext()) {
            MEExpression operand = it.next();
            MTElement operator = null;
            if (operand != additions.getOperands().get(0)) {
                if (operand.isNegative()) {
                    operator = new MTInlineOperator(MTInlineOperatorType.Minus);
                    operand = operand.negate();
                } else {
                    operator = new MTInlineOperator(MTInlineOperatorType.Plus);
                }
                appendElementToString(operator, string);
            }
            totalRange = Range.union(totalRange, writeExpression(operand, string));
            if (operator != null) {
                registerRange(totalRange, operator);
            }
        }
    }

    private void writeMultiplications(MEMultiplications multiplications, MTString string) {
        MEExpression newExpression;
        boolean containsDivision = false;
        Iterator<MEExpression> it = multiplications.getOperands().iterator();
        while (it.hasNext()) {
            MEExpression operand = it.next();
            if ((operand instanceof MEPower) && ((MEPower) operand).getExponent().isNegative()) {
                containsDivision = true;
            }
        }
        if (containsDivision) {
            boolean addMinus = false;
            ArrayList<MEExpression> dividendOperands = new ArrayList<>();
            ArrayList<MEExpression> divisorOperands = new ArrayList<>();
            Iterator<MEExpression> it2 = multiplications.getOperands().iterator();
            while (it2.hasNext()) {
                MEExpression operand2 = it2.next();
                if (operand2 instanceof MEPower) {
                    MEPower power = (MEPower) operand2;
                    if (power.getExponent().isNegative()) {
                        MEExpression newExponent = power.getExponent().negate();
                        if (newExponent.equals(MEValue.one())) {
                            newExpression = power.getBase();
                        } else {
                            newExpression = MEPower.powerWithBaseAndExponent(power.getBase(), newExponent);
                        }
                        divisorOperands.add(newExpression);
                    } else {
                        dividendOperands.add(power);
                    }
                } else if (operand2 instanceof MEValue) {
                    MEValue value = (MEValue) operand2;
                    if (value.isNegative()) {
                        value = value.negate();
                        addMinus = !addMinus;
                    }
                    if (value.getRational() != null) {
                        MEInteger numerator = value.getRational().numerator();
                        MEInteger denominator = value.getRational().denominator();
                        if (!MEInteger.isEqual(numerator, 1)) {
                            dividendOperands.add(new MEValue(numerator));
                        }
                        if (!MEInteger.isEqual(denominator, 1)) {
                            divisorOperands.add(new MEValue(denominator));
                        }
                    } else {
                        dividendOperands.add(value);
                    }
                } else {
                    dividendOperands.add(operand2);
                }
            }
            if (dividendOperands.size() == 0) {
                dividendOperands.add(MEValue.one());
            }
            if (addMinus) {
                string.appendElement(new MTInlineOperator(MTInlineOperatorType.Minus));
            }
            MTDivision division = new MTDivision();
            writeMultiplicationOperandsToString(dividendOperands, division.getDividend());
            writeMultiplicationOperandsToString(divisorOperands, division.getDivisor());
            string.appendElement(division);
            return;
        }
        writeMultiplicationOperandsToString(multiplications.getOperands(), string);
    }

    private void writeMultiplicationOperandsToString(Collection<MEExpression> operands, MTString string) {
        Range totalRange = new Range(string.indexAfterLastElement(), 0);
        boolean addMinus = false;
        ArrayList<MEExpression> newOperands = new ArrayList<>();
        for (MEExpression operand : operands) {
            if (operand.isNegative()) {
                if (!addMinus) {
                    addMinus = true;
                } else {
                    addMinus = false;
                }
                MEExpression negatedOperand = operand.negate();
                if (negatedOperand != null) {
                    newOperands.add(negatedOperand);
                }
            } else {
                newOperands.add(operand);
            }
        }
        if (addMinus) {
            string.appendElement(new MTInlineOperator(MTInlineOperatorType.Minus));
        }
        Collections.sort(newOperands, new Comparator<MEExpression>() { // from class: com.oddlyspaced.calci.mathtype.writers.MTWriter.1
            public int compare(MEExpression operand1, MEExpression operand2) {
                Boolean isUnit1 = Boolean.valueOf(operand1 instanceof MEUnit);
                if (isUnit1 != Boolean.valueOf(operand2 instanceof MEUnit)) {
                    return !isUnit1.booleanValue() ? -1 : 1;
                }
                return (!Boolean.valueOf(operand1 instanceof MEValue).booleanValue() || Boolean.valueOf(operand2 instanceof MEValue).booleanValue()) ? 1 : -1;
            }
        });
        MEExpression previousOperand = null;
        Iterator<MEExpression> it = newOperands.iterator();
        while (it.hasNext()) {
            MEExpression operand2 = it.next();
            if (!operand2.toString().equals("1")) {
                MTOperatorInfo operator = null;
                int operatorIndex = string.indexAfterLastElement();
                if (previousOperand != null) {
                    if (useExplicitMultiplication(previousOperand, operand2)) {
                        MTElement operatorElement = new MTInlineOperator(MTInlineOperatorType.Dot);
                        string.appendElement(operatorElement);
                        operator = MTOperatorInfo.infoForElement(operatorElement);
                    } else {
                        operator = MTOperatorInfo.infoForImplicitMultiplication();
                    }
                }
                if ((operand2 instanceof MEUnit) && previousOperand == null) {
                    string.appendElement(new MTNumericCharacter(MTNumericCharacterType.Number1));
                }
                totalRange = Range.union(totalRange, writeExpression(operand2, string));
                if (operator != null) {
                    registerRange(totalRange, operator, string, operatorIndex);
                }
                previousOperand = operand2;
            }
        }
        if (previousOperand == null) {
            string.appendElement(new MTNumericCharacter(MTNumericCharacterType.Number1));
        }
    }

    private boolean useExplicitMultiplication(MEExpression leftOperand, MEExpression rightOperand) {
        boolean isRoot;
        boolean isRoot2;
        if (leftOperand != null) {
            if (rightOperand instanceof MEValue) {
                return true;
            }
            if (leftOperand instanceof MEPower) {
                MEExpression exponent = ((MEPower) leftOperand).getExponent();
                if (!(exponent instanceof MEValue) || !((MEValue) exponent).getRational().isFractional()) {
                    isRoot2 = false;
                } else {
                    isRoot2 = true;
                }
                if (isRoot2) {
                    return true;
                }
            }
            if (rightOperand instanceof MEPower) {
                MEExpression exponent2 = ((MEPower) rightOperand).getExponent();
                if (!(exponent2 instanceof MEValue) || !((MEValue) exponent2).getRational().isFractional()) {
                    isRoot = false;
                } else {
                    isRoot = true;
                }
                if (!isRoot && (((MEPower) rightOperand).getBase() instanceof MEValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void writePower(MEPower power, MTString string) {
        Range operandRange;
        MEExpression exponent = power.getExponent();
        if (exponent.isNegative()) {
            exponent = exponent.negate();
            MTDivision division = new MTDivision();
            string.appendElement(division);
            writeValue(MEValue.one(), division.getDividend());
            string = division.getDivisor();
        }
        if (!(exponent instanceof MEValue) || !((MEValue) exponent).getRational().isFractional()) {
            operandRange = writeExpression(power.getBase(), string);
        } else {
            MERational exponentRational = ((MEValue) exponent).getRational();
            MTRoot rootElement = new MTRoot();
            operandRange = appendElementToString(rootElement, string);
            if (MEInteger.isEqual(exponentRational.denominator(), 2)) {
                rootElement.getDegree().setTraits(EnumSet.of(MTNodeTraits.CantSelectOrEditChildren));
            } else {
                writeValue(new MEValue(exponentRational.denominator()), rootElement.getDegree());
            }
            writeExpression(power.getBase(), rootElement.getContents());
            if (!MEInteger.isEqual(exponentRational.numerator(), 1)) {
                exponent = new MEValue(exponentRational.numerator());
            } else {
                return;
            }
        }
        if (!exponent.equals(MEValue.one())) {
            MTPower powerElement = new MTPower();
            writeExpression(exponent, powerElement.getExponent());
            string.appendElement(powerElement);
            registerRange(operandRange, powerElement);
        }
    }

    private void writeVariable(MEVariable variable, MTString string) {
        MTMEVariableIdentifier identifier = variable.getIdentifier() instanceof MTMEVariableIdentifier ? (MTMEVariableIdentifier) variable.getIdentifier() : null;
        if (identifier == null) {
            throw new IllegalArgumentException("Variable identifier must be of type MTMEVariableIdentifier");
        }
        MTVariable variableReferenceElement = new MTVariable();
        identifier.getName().copyElementsToString(variableReferenceElement.getName(), 0);
        string.appendElement(variableReferenceElement);
    }

    private void writeTrigonometricFunction(METrigonometricFunction function, MTString string) {
        MTElement operator = new MTInlineOperator(MTInlineOperatorType.fromMETrigonometricFunctionType(function.getType()));
        string.appendElement(operator);
        registerRange(writeExpression(function.getOperand(), string), operator);
    }

    private void writeLogarithm(MELogarithm logarithm, MTString string) {
        MTElement operator;
        if (logarithm.getBase().equals(MEConstant.e())) {
            operator = new MTInlineOperator(MTInlineOperatorType.NaturalLogarithm);
        } else {
            MTLogarithm logarithmElement = new MTLogarithm();
            if (logarithm.getBase().equals(new MEValue(10L))) {
                logarithmElement.getBase().setTraits(EnumSet.of(MTNodeTraits.CantSelectOrEditChildren));
            } else {
                writeExpression(logarithm.getBase(), logarithmElement.getBase());
            }
            operator = logarithmElement;
        }
        string.appendElement(operator);
        registerRange(writeExpression(logarithm.getOperand(), string), operator);
    }

    private void writeEquals(MEEquals equals, MTString string) {
        Range leftRange = writeExpression(equals.getLeftOperand(), string);
        MTElement operator = new MTInlineOperator(MTInlineOperatorType.Equals);
        string.appendElement(operator);
        registerRange(Range.union(leftRange, writeExpression(equals.getRightOperand(), string)), operator);
    }

    private void writeUnit(MEUnit unit, MTString string) {
        String text;
        if (unit.equals(MEUnit.degrees())) {
            text = unit.getSymbol();
        } else {
            text = " " + unit.getSymbol();
        }
        appendElementToString(new MTText(text), string);
    }

    private void writeQuantity(MEQuantity quantity, MTString string) {
        appendElementToString(new MTText("[" + quantity.getSymbol() + "]"), string);
    }

    private void writePlaceholder(MEPlaceholder placeholder, MTString string) {
        MEExpression expression;
        MTMEPlaceholderIdentifier identifier = placeholder.getIdentifier() instanceof MTMEPlaceholderIdentifier ? (MTMEPlaceholderIdentifier) placeholder.getIdentifier() : null;
        if (identifier == null) {
            throw new IllegalArgumentException("Placeholder identifier must be of type MTMEPlaceholderIdentifier");
        } else if (identifier instanceof MTReference) {
            string.appendElement(((MTReference) identifier).copy());
        } else {
            ArrayList<MEExpression> expressions = identifier.expressionsForForm(this.mForm);
            if (expressions.size() != 0 && expressions.size() == 1 && (expression = expressions.get(0)) != null) {
                writeExpression(expression, string);
            }
        }
    }

    private void addParenthesesToEnforceOperatorRanges() {
        ArrayList<MTString> operatorStrings = new ArrayList<>();
        Iterator<MTOperatorRange> it = this.mOperatorRanges.iterator();
        while (it.hasNext()) {
            operatorStrings.add(it.next().mString);
        }
        Iterator<MTString> it2 = new HashSet<>(operatorStrings).iterator();
        while (it2.hasNext()) {
            MTString string = it2.next();
            ArrayList<Integer> leftParenIndices = new ArrayList<>();
            ArrayList<Integer> rightParenIndices = new ArrayList<>();
            Iterator<MTOperatorRange> it3 = this.mOperatorRanges.iterator();
            while (it3.hasNext()) {
                MTOperatorRange operatorRange = it3.next();
                if (operatorRange.mString == string) {
                    MTOperatorInfo operatorInfo = operatorRange.mOperator;
                    Range implicitRange = MTParser.rangeForOperandsOfOperatorAtIndexInString(operatorInfo, operatorRange.mOperatorIndex, string, !operatorInfo.isImplicit());
                    Range range = operatorRange.mRange;
                    if (operatorInfo.getPrecedence() == MTPrecedence.ImplicitMultiply && implicitRange.mStartIndex == range.mStartIndex + 1 && string.elementAtIndex(range.mStartIndex).equivalentTo(new MTInlineOperator(MTInlineOperatorType.Minus))) {
                        implicitRange = new Range(range.mStartIndex, implicitRange.mLength + 1);
                    }
                    if (range.mStartIndex != implicitRange.mStartIndex) {
                        leftParenIndices.add(Integer.valueOf(range.mStartIndex));
                    }
                    if (range.getMaxRange() != implicitRange.getMaxRange()) {
                        rightParenIndices.add(Integer.valueOf(range.getMaxRange()));
                    }
                }
            }
            if (!leftParenIndices.isEmpty() || !rightParenIndices.isEmpty()) {
                addParentheses(string, rangesForParentheses(string, leftParenIndices, rightParenIndices));
            }
        }
    }

    private ArrayList<Range> rangesForParentheses(MTString string, ArrayList<Integer> leftIndices, ArrayList<Integer> rightIndices) {
        ArrayList<Integer> leftIndices2 = new ArrayList<>(leftIndices);
        ArrayList<Integer> rightIndices2 = new ArrayList<>(rightIndices);
        ListUtil.removeDuplicates(leftIndices2);
        ListUtil.removeDuplicates(rightIndices2);
        Collections.sort(leftIndices2);
        Collections.sort(rightIndices2);
        Iterator<Integer> it = leftIndices2.iterator();
        while (it.hasNext()) {
            Integer index = it.next();
            if (index.intValue() < 0 || index.intValue() >= string.indexAfterLastElement()) {
                leftIndices2.remove(index);
            }
        }
        Iterator<Integer> it2 = rightIndices2.iterator();
        while (it2.hasNext()) {
            Integer index2 = it2.next();
            if (index2.intValue() <= 0 || index2.intValue() > string.indexAfterLastElement()) {
                rightIndices2.remove(index2);
            }
        }
        ArrayList<Range> ranges = new ArrayList<>();
        while (true) {
            if (rightIndices2.size() == 0 && leftIndices2.size() == 0) {
                return ranges;
            }
            Integer rightIndex = null;
            if (rightIndices2.size() != 0) {
                rightIndex = rightIndices2.get(0);
                rightIndices2.remove(0);
            }
            Integer rightIndex2 = Integer.valueOf(rightIndex != null ? rightIndex.intValue() : string.indexAfterLastElement());
            Integer leftIndex = 0;
            Iterator it3 = IterableUtil.reverse(new ArrayList(leftIndices2)).iterator();
            while (true) {
                if (it3.hasNext()) {
                    Integer index3 = (Integer) it3.next();
                    if (index3.intValue() < rightIndex2.intValue()) {
                        leftIndex = index3;
                        leftIndices2.remove(leftIndices2.size() - 1);
                        break;
                    }
                }
            }
            ranges.add(new Range(leftIndex.intValue(), rightIndex2.intValue() - leftIndex.intValue()));
        }
    }

    private void addParentheses(MTString string, ArrayList<Range> rangesArray) {
        ArrayList<Range> ranges = new ArrayList<>(rangesArray);
        Collections.sort(ranges, new Comparator<Range>() { // from class: com.oddlyspaced.calci.mathtype.writers.MTWriter.2
            public int compare(Range range1, Range range2) {
                return Integer.valueOf(range1.getMaxRange()).compareTo(Integer.valueOf(range2.getMaxRange()));
            }
        });
        while (!ranges.isEmpty()) {
            Range range = ranges.get(ranges.size() - 1);
            ranges.remove(ranges.size() - 1);
            MTParentheses parentheses = new MTParentheses();
            string.moveElementsInRangeToString(range, parentheses.getContents(), 0);
            string.insertElement(parentheses, range.mStartIndex);
            ArrayList<Range> containedRanges = new ArrayList<>();
            Iterator it = new ArrayList(ranges).iterator();
            while (it.hasNext()) {
                Range otherRange = (Range) it.next();
                if (range.contains(otherRange)) {
                    ranges.remove(otherRange);
                    otherRange.mStartIndex -= range.mStartIndex;
                    containedRanges.add(otherRange);
                }
            }
            addParentheses(parentheses.getContents(), containedRanges);
        }
    }

    private void registerRange(Range range, MTElement element) {
        registerRange(range, MTOperatorInfo.infoForElement(element), element.getParent(), element.indexInParentString());
    }

    private void registerRange(Range range, MTOperatorInfo operator, MTString string, int operatorIndex) {
        MTOperatorRange operatorRange = new MTOperatorRange();
        operatorRange.mRange = range;
        operatorRange.mString = string;
        operatorRange.mOperatorIndex = operatorIndex;
        operatorRange.mOperator = operator;
        this.mOperatorRanges.add(operatorRange);
    }
}
