package com.oddlyspaced.calci.mathtype.parsers;

import com.oddlyspaced.calci.archimedes.model.ARPreviousAnswerReference;
import com.oddlyspaced.calci.mathtype.enums.MTInlineOperatorType;
import com.oddlyspaced.calci.mathtype.enums.MTNodeTraits;
import com.oddlyspaced.calci.mathtype.enums.MTNumericCharacterType;
import com.oddlyspaced.calci.mathtype.nodes.MTString;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTDivision;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTInlineOperator;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTLogarithm;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTNumericCharacter;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTParentheses;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTReference;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTRoot;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTText;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTVariable;
import com.oddlyspaced.calci.utilities.Range;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public class MTTextParser {
    private static final String DEGREE_TEXT = "°";
    private static final String E_TEXT = "e";
    private static final String PI_TEXT = "π";
    private static final String RAD_TEXT = " rad";
    private static HashMap<String, String> mConstants;
    private static Pattern mEngineeringExponentRegex;
    private static HashMap<String, MTInlineOperatorType> mOperators;
    private static HashMap<String, String> mVariables;

    private Pattern getEngineeringExponentRegex() {
        if (mEngineeringExponentRegex == null) {
            mEngineeringExponentRegex = Pattern.compile("(?<=[\\d.,])e(?=[+-\\-]?\\d)|E");
        }
        return mEngineeringExponentRegex;
    }

    private HashMap<String, MTInlineOperatorType> getOperators() {
        if (mOperators == null) {
            mOperators = new HashMap<>();
            mOperators.put("+", MTInlineOperatorType.Plus);
            mOperators.put("-", MTInlineOperatorType.Minus);
            mOperators.put("−", MTInlineOperatorType.Minus);
            mOperators.put("*", MTInlineOperatorType.Dot);
            mOperators.put("·", MTInlineOperatorType.Dot);
            mOperators.put("×", MTInlineOperatorType.Dot);
            mOperators.put("\\cdot", MTInlineOperatorType.Dot);
            mOperators.put("/", MTInlineOperatorType.Division);
            mOperators.put("÷", MTInlineOperatorType.Division);
            mOperators.put(":", MTInlineOperatorType.Division);
            mOperators.put("\\div", MTInlineOperatorType.Division);
            mOperators.put("^", MTInlineOperatorType.Power);
            mOperators.put("√", MTInlineOperatorType.SquareRoot);
            mOperators.put("sqrt", MTInlineOperatorType.SquareRoot);
            mOperators.put("ln", MTInlineOperatorType.NaturalLogarithm);
            mOperators.put("\\ln", MTInlineOperatorType.NaturalLogarithm);
            mOperators.put("=", MTInlineOperatorType.Equals);
            mOperators.put("sin", MTInlineOperatorType.Sine);
            mOperators.put("sine", MTInlineOperatorType.Sine);
            mOperators.put("\\sin", MTInlineOperatorType.Sine);
            mOperators.put("cos", MTInlineOperatorType.Cosine);
            mOperators.put("cosine", MTInlineOperatorType.Cosine);
            mOperators.put("\\cos", MTInlineOperatorType.Cosine);
            mOperators.put("tan", MTInlineOperatorType.Tangent);
            mOperators.put("tangent", MTInlineOperatorType.Tangent);
            mOperators.put("\\tan", MTInlineOperatorType.Tangent);
            mOperators.put("asin", MTInlineOperatorType.ArcSine);
            mOperators.put("arcsin", MTInlineOperatorType.ArcSine);
            mOperators.put("arcsine", MTInlineOperatorType.ArcSine);
            mOperators.put("\\asin", MTInlineOperatorType.ArcSine);
            mOperators.put("acos", MTInlineOperatorType.ArcCosine);
            mOperators.put("arccos", MTInlineOperatorType.ArcCosine);
            mOperators.put("arccosine", MTInlineOperatorType.ArcCosine);
            mOperators.put("\\acos", MTInlineOperatorType.ArcCosine);
            mOperators.put("atan", MTInlineOperatorType.ArcTangent);
            mOperators.put("arctan", MTInlineOperatorType.ArcTangent);
            mOperators.put("arctangent", MTInlineOperatorType.ArcTangent);
            mOperators.put("\\atan", MTInlineOperatorType.ArcTangent);
        }
        return mOperators;
    }

    private HashMap<String, String> getConstants() {
        if (mConstants == null) {
            mConstants = new HashMap<>();
            mConstants.put(PI_TEXT, PI_TEXT);
            mConstants.put("pi", PI_TEXT);
            mConstants.put("\\pi", PI_TEXT);
            mConstants.put(E_TEXT, E_TEXT);
            mConstants.put("\\e", E_TEXT);
            mConstants.put(DEGREE_TEXT, DEGREE_TEXT);
            mConstants.put("deg", DEGREE_TEXT);
            mConstants.put("\\deg", DEGREE_TEXT);
            mConstants.put("rad", RAD_TEXT);
            mConstants.put("\\rad", RAD_TEXT);
        }
        return mConstants;
    }

    private HashMap<String, String> getVariables() {
        if (mVariables == null) {
            mVariables = new HashMap<>();
            mVariables.put("a", "a");
            mVariables.put("b", "b");
            mVariables.put("c", "c");
            mVariables.put("x", "x");
            mVariables.put("y", "y");
            mVariables.put("z", "z");
        }
        return mVariables;
    }

    public List<MTString> parseText(String text) {
        ArrayList<MTString> resultLines = new ArrayList<>();
        for (String line : splitTextIntoLines(text)) {
            MTString resultLine = parseLine(line);
            if (resultLine != null) {
                resultLines.add(resultLine);
            }
        }
        return resultLines;
    }

    private String[] splitTextIntoLines(String text) {
        return text.replace("\r\n", "\n").replace("\r", "\n").replace("\\\\", "\n").split("\n");
    }

    private MTString parseLine(String text) {
        AtomicInteger index = new AtomicInteger(0);
        MTString result = new MTString();
        while (index.intValue() < text.length()) {
            int oldIndex = index.intValue();
            tryParseNextPart(text, index, result);
            if (index.intValue() == oldIndex) {
                index.incrementAndGet();
            }
        }
        MTParser.replaceInlineOperatorsWithContainerFormsWherePossible(result);
        return result;
    }

    private boolean tryParseNextPart(String text, AtomicInteger index, MTString result) {
        if (!skipWhitespace(text, index) && !tryParseNumericCharacter(text, index, result) && !tryParseInlineOperator(text, index, result) && !tryParseReference(text, index, result) && !tryParseConstant(text, index, result) && !tryParseVariable(text, index, result) && !tryParseParentheses(text, index, result) && !tryParseDivision(text, index, result) && !tryParsePower(text, index, result) && !tryParseRoot(text, index, result) && !tryParseLogarithm(text, index, result)) {
            return false;
        }
        return true;
    }

    private boolean tryParseNumericCharacter(String text, AtomicInteger index, MTString result) {
        Character nextChar = Character.valueOf(text.charAt(index.intValue()));
        if (nextChar.charValue() >= '0' && nextChar.charValue() <= '9') {
            result.appendElement(new MTNumericCharacter(MTNumericCharacterType.values()[nextChar.charValue() - '0']));
            index.incrementAndGet();
            return true;
        } else if (nextChar.charValue() == '.' || nextChar.charValue() == ',') {
            result.appendElement(new MTNumericCharacter(MTNumericCharacterType.RadixPoint));
            index.incrementAndGet();
            return true;
        } else {
            Matcher matcher = getEngineeringExponentRegex().matcher(text);
            matcher.useAnchoringBounds(true);
            matcher.useTransparentBounds(true);
            try {
                matcher.start();
                result.appendElement(new MTInlineOperator(MTInlineOperatorType.EngineeringExponent));
                index.incrementAndGet();
                return true;
            } catch (IllegalStateException e) {
                return false;
            }
        }
    }

    private boolean tryParseInlineOperator(String text, AtomicInteger index, MTString result) {
        MTInlineOperatorType match = (MTInlineOperatorType) matchFromDictionary(getOperators(), text, index, false);
        if (match == null) {
            return false;
        }
        result.appendElement(new MTInlineOperator(match));
        return true;
    }

    private boolean tryParseReference(String text, AtomicInteger index, MTString result) {
        if (((String) matchFromArray(Arrays.asList("answer", "ans"), text, index, false)) == null) {
            return false;
        }
        result.appendElement(new MTReference(Arrays.asList(new MTText("ans")), new ARPreviousAnswerReference(null, null)));
        return true;
    }

    private boolean tryParseConstant(String text, AtomicInteger index, MTString result) {
        String match = (String) matchFromDictionary(getConstants(), text, index, false);
        if (match == null) {
            return false;
        }
        result.appendElement(new MTText(match));
        return true;
    }

    private boolean tryParseVariable(String text, AtomicInteger index, MTString result) {
        String match = (String) matchFromDictionary(getVariables(), text, index, false);
        if (match == null) {
            return false;
        }
        result.appendElement(new MTVariable(Arrays.asList(new MTText(match))));
        return true;
    }

    private boolean tryParseParentheses(String text, AtomicInteger index, MTString result) {
        MTParentheses parentheses = new MTParentheses();
        if (!tryParseGroup(text, index, parentheses.getContents(), "({[", ")}]")) {
            return false;
        }
        result.appendElement(parentheses);
        return true;
    }

    private boolean tryParseDivision(String text, AtomicInteger index, MTString result) {
        if (((String) matchFromArray(Arrays.asList("\\frac"), text, index, false)) == null) {
            return false;
        }
        MTDivision division = new MTDivision();
        if (tryParseGroup(text, index, division.getDividend(), "{", "}")) {
            tryParseGroup(text, index, division.getDivisor(), "{", "}");
        }
        result.appendElement(division);
        return true;
    }

    private boolean tryParsePower(String text, AtomicInteger index, MTString result) {
        if (((String) matchFromArray(Arrays.asList("\\exp"), text, index, false)) == null) {
            return false;
        }
        result.appendElement(new MTText(E_TEXT));
        result.appendElement(new MTInlineOperator(MTInlineOperatorType.Power));
        return true;
    }

    private boolean tryParseRoot(String text, AtomicInteger index, MTString result) {
        if (((String) matchFromArray(Arrays.asList("\\sqrt"), text, index, false)) == null) {
            return false;
        }
        MTRoot root = new MTRoot();
        tryParseGroup(text, index, root.getDegree(), "[", "]");
        root.getDegree().setTraits(root.getDegree().isNotEmpty() ? null : EnumSet.of(MTNodeTraits.CantSelectOrEditChildren));
        tryParseGroup(text, index, root.getContents(), "{", "}");
        result.appendElement(root);
        return true;
    }

    private boolean tryParseLogarithm(String text, AtomicInteger index, MTString result) {
        if (((String) matchFromArray(Arrays.asList("log", "\\log"), text, index, false)) == null) {
            return false;
        }
        MTLogarithm logarithm = new MTLogarithm();
        tryParseGroup(text, index, logarithm.getBase(), "[", "]");
        logarithm.getBase().setTraits(logarithm.getBase().isNotEmpty() ? null : EnumSet.of(MTNodeTraits.CantSelectOrEditChildren));
        result.appendElement(logarithm);
        return true;
    }

    private boolean tryParseGroup(String text, AtomicInteger index, MTString result, String openChars, String closeChars) {
        skipWhitespace(text, index);
        if (index.intValue() >= text.length()) {
            return false;
        }
        Range range = rangeForParentheses(text, index, openChars, closeChars);
        if (range.mLength < 2) {
            return false;
        }
        parseLine(text.substring(range.mStartIndex + 1, ((range.mStartIndex + 1) + range.mLength) - 2)).moveElementsToString(result, result.indexAfterLastElement());
        index.set(range.getMaxRange());
        return true;
    }

    private boolean skipWhitespace(String text, AtomicInteger index) {
        if (index.intValue() >= text.length() || !Character.isWhitespace(Character.valueOf(text.charAt(index.intValue())).charValue())) {
            return false;
        }
        index.incrementAndGet();
        return true;
    }

    private Object matchFromArray(Iterable<String> array, String text, AtomicInteger index, boolean caseSensitive) {
        for (String pattern : array) {
            if (isPattern(pattern, index, text, false)) {
                index.set(index.intValue() + pattern.length());
                return pattern;
            }
        }
        return null;
    }

    private Object matchFromDictionary(HashMap<String, ?> dictionary, String text, AtomicInteger index, boolean caseSensitive) {
        for (String key : dictionary.keySet()) {
            if (isPattern(key, index, text, false)) {
                index.set(index.intValue() + key.length());
                return dictionary.get(key);
            }
        }
        return null;
    }

    private boolean isPattern(String pattern, AtomicInteger index, String text, boolean caseSensitive) {
        int endIndex = index.intValue() + pattern.length();
        if (endIndex > text.length()) {
            return false;
        }
        String string = text.substring(index.intValue(), endIndex);
        return caseSensitive ? string.equals(pattern) : string.equalsIgnoreCase(pattern);
    }

    private Range rangeForParentheses(String text, AtomicInteger index, String openChars, String closeChars) {
        int startIndex = index.intValue();
        int depth = 0;
        while (index.intValue() < text.length()) {
            Character chr = Character.valueOf(text.charAt(index.intValue()));
            if (closeChars.indexOf(chr.charValue()) != -1 && depth > 0) {
                depth--;
            } else if (openChars.indexOf(chr.charValue()) != -1) {
                depth++;
            }
            if (depth <= 0) {
                break;
            }
            index.incrementAndGet();
        }
        if (startIndex == index.intValue()) {
            return new Range(startIndex, 0);
        }
        return new Range(startIndex, (index.intValue() - startIndex) + 1);
    }
}
