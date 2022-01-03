package com.oddlyspaced.calci.mathtype.writers;

import com.oddlyspaced.calci.mathexpression.numbers.MEInteger;
import com.oddlyspaced.calci.mathexpression.numbers.MEReal;
import com.oddlyspaced.calci.mathtype.enums.MTExponentFormat;
import com.oddlyspaced.calci.mathtype.enums.MTInlineOperatorType;
import com.oddlyspaced.calci.mathtype.enums.MTNumberFormat;
import com.oddlyspaced.calci.mathtype.enums.MTNumericCharacterType;
import com.oddlyspaced.calci.mathtype.nodes.MTElement;
import com.oddlyspaced.calci.mathtype.nodes.MTString;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTInlineOperator;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTNumericCharacter;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTPowerOfTenExponent;
import com.oddlyspaced.calci.utilities.GeneralUtil;
import com.oddlyspaced.calci.utilities.Range;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public class MTNumberFormatter {
    private static final int MAXIMUM_INTEGER_LENGTH = 15;
    private boolean mShowLeadingZeroBeforeRadixPoint;
    private MTNumberFormat mNumberFormat = MTNumberFormat.Auto;
    private MTExponentFormat mtExponentFormat = MTExponentFormat.Engineering;
    private int mDecimalPlaces = 6;
    private boolean mUseRounding = true;

    public void setNumberFormat(MTNumberFormat format) {
        this.mNumberFormat = format;
    }

    public void setExponentFormat(MTExponentFormat format) {
        this.mtExponentFormat = format;
    }

    public void setDecimalPlaces(int decimalPlaces) {
        this.mDecimalPlaces = decimalPlaces;
    }

    public void setUseRounding(boolean useRounding) {
        this.mUseRounding = useRounding;
    }

    public void setShowLeadingZeroBeforeRadixPoint(boolean showLeadingZero) {
        this.mShowLeadingZeroBeforeRadixPoint = showLeadingZero;
    }

    public Range writeIntegerToString(MEInteger number, MTString string) {
        boolean useExponent;
        String digits = number.toString();
        if (this.mUseRounding && digits.length() > 15) {
            digits = roundDigitString(digits, 14, new AtomicBoolean(false));
        }
        String digitsString = digits;
        if (digitsString.length() > 6) {
            useExponent = true;
        } else {
            useExponent = false;
        }
        if (useExponent) {
            int trimmedDigits = 0;
            while (digitsString.length() > 0) {
                int lastCharIndex = digitsString.length() - 1;
                if (digitsString.charAt(lastCharIndex) != '0' && digitsString.length() <= 15) {
                    break;
                }
                digitsString = digitsString.substring(0, lastCharIndex);
                trimmedDigits++;
            }
            if (trimmedDigits == 0) {
                useExponent = false;
            }
            if (useExponent) {
                digitsString = digitsString + "E" + trimmedDigits;
            }
        }
        int startIndex = string.indexAfterLastElement();
        writeElements(digitsString, string);
        return new Range(startIndex, string.indexAfterLastElement() - startIndex);
    }

    public Range writeRealToString(MEReal number, MTString string) {
        Matcher matcher = Pattern.compile("^([+-]?)(\\d\\.?\\d*)E([+-]?)(\\d*)$").matcher(number.scientificFormatString());
        if (!matcher.find()) {
            return new Range(0, 0);
        }
        String signString = matcher.group(1);
        String digitsString = matcher.group(2);
        String exponentSignString = matcher.group(3);
        String exponentString = matcher.group(4);
        boolean isNegative = signString.equals("-");
        String digitsString2 = digitsString.replaceAll("\\.", "");
        int exponent = Integer.valueOf(exponentString).intValue();
        if (exponentSignString.equals("-")) {
            exponent = -exponent;
        }
        int startIndex = string.indexAfterLastElement();
        writeNumber(digitsString2, isNegative, exponent, string);
        return new Range(startIndex, string.indexAfterLastElement() - startIndex);
    }

    private String roundDigitString(String digits, int index, AtomicBoolean outAddedDigit) {
        int newDigit;
        int count = digits.length();
        if (index < 0) {
            index = 0;
        }
        if (index + 1 >= count) {
            return digits;
        }
        String result = digits;
        outAddedDigit.set(false);
        boolean shouldRoundUp = false;
        for (int i = count - 1; i >= 0; i--) {
            int digit = digits.charAt(i) - '0';
            if (digit < 0 || digit > 9) {
                return null;
            }
            if (i <= index) {
                if (!shouldRoundUp) {
                    break;
                }
                newDigit = digit + 1;
                if (newDigit == 10) {
                    newDigit = 0;
                    shouldRoundUp = true;
                } else {
                    shouldRoundUp = false;
                }
            } else {
                if (digit >= 5) {
                    shouldRoundUp = true;
                } else {
                    shouldRoundUp = false;
                }
                newDigit = 0;
            }
            if (newDigit != digit) {
                result = result.substring(0, i) + Integer.toString(newDigit) + result.substring(i + 1);
            }
            if (i == 0 && shouldRoundUp) {
                result = "1" + result;
                outAddedDigit.set(true);
            }
        }
        return result;
    }

    private void writeElements(String numberString, MTString string) {
        MTElement element = null;
        String numberString2 = numberString.toUpperCase();
        for (int i = 0; i < numberString2.length(); i++) {
            char chr = numberString2.charAt(i);
            if (chr >= '0' && chr <= '9') {
                element = new MTNumericCharacter(MTNumericCharacterType.values()[chr - '0']);
            } else if (chr == '.') {
                element = new MTNumericCharacter(MTNumericCharacterType.RadixPoint);
            } else if (chr == 'E') {
                if (this.mtExponentFormat == MTExponentFormat.PowerOfTen) {
                    MTPowerOfTenExponent powerOfTenExponent = new MTPowerOfTenExponent();
                    string.appendElement(powerOfTenExponent);
                    writeElements(numberString2.substring(i + 1), powerOfTenExponent.getExponent());
                    return;
                }
                element = new MTInlineOperator(MTInlineOperatorType.EngineeringExponent);
            } else if (chr == '-') {
                element = new MTInlineOperator(MTInlineOperatorType.Minus);
            }
            string.appendElement(element);
        }
    }

    private void writeNumber(String digits, boolean isNegative, int exponent, MTString string) {
        int displayExponent;
        int indexOfPoint;
        int i;
        int startIndex;
        int endIndex;
        if (isNegative) {
            string.appendElement(new MTInlineOperator(MTInlineOperatorType.Minus));
        }
        while (true) {
            displayExponent = displayExponentForNumberWithScientificNotationExponent(exponent);
            indexOfPoint = (exponent - displayExponent) + 1;
            if (this.mShowLeadingZeroBeforeRadixPoint) {
                i = indexOfPoint - 1;
            } else {
                i = indexOfPoint;
            }
            startIndex = GeneralUtil.constrainMax(i, 0);
            endIndex = indexOfPoint + this.mDecimalPlaces;
            if (!this.mUseRounding) {
                break;
            }
            AtomicBoolean addedDigit = new AtomicBoolean(false);
            digits = roundDigitString(digits, endIndex - 1, addedDigit);
            if (!addedDigit.get()) {
                break;
            }
            exponent++;
        }
        int digitCount = digits.length();
        String numberString = "";
        int i2 = startIndex;
        while (i2 < endIndex) {
            if (i2 == indexOfPoint) {
                numberString = numberString + ".";
            }
            numberString = numberString + ((i2 < 0 || i2 >= digitCount) ? "0" : digits.substring(i2, i2 + 1));
            i2++;
        }
        if (digits.equals(digits)) {
            numberString = trimTrailingZeros(numberString);
        }
        writeElements(numberString, string);
        if (displayExponent != 0) {
            writeElements("E" + displayExponent, string);
        }
    }

    private int displayExponentForNumberWithScientificNotationExponent(int exponent) {
        if (this.mNumberFormat == MTNumberFormat.Plain) {
            return 0;
        }
        if (this.mNumberFormat == MTNumberFormat.Auto) {
            if (exponent > 5 || exponent < -3) {
                return exponent;
            }
            return 0;
        } else if (this.mNumberFormat == MTNumberFormat.Engineering) {
            return ((int) Math.floor(((double) exponent) / 3.0d)) * 3;
        } else {
            return exponent;
        }
    }

    private String trimTrailingZeros(String string) {
        boolean done;
        if (!string.contains(".")) {
            done = true;
        } else {
            done = false;
        }
        while (!done) {
            int lastIndex = string.length() - 1;
            char lastChar = string.charAt(lastIndex);
            boolean removeChar = false;
            if (lastChar == '0') {
                removeChar = true;
            } else if (lastChar == '.') {
                removeChar = true;
                done = true;
            } else {
                done = true;
            }
            if (removeChar) {
                string = string.substring(0, lastIndex);
            }
        }
        return string;
    }
}
