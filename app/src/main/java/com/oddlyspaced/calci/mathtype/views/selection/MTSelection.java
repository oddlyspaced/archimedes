package com.oddlyspaced.calci.mathtype.views.selection;

import com.oddlyspaced.calci.mathtype.nodes.MTElement;
import com.oddlyspaced.calci.mathtype.nodes.MTString;
import com.oddlyspaced.calci.utilities.GeneralUtil;
import com.oddlyspaced.calci.utilities.Range;
import java.io.Serializable;
/* loaded from: classes.dex */
public class MTSelection implements Serializable {
    private int mIndex;
    private int mLength;
    private MTString mString;

    public MTString getString() {
        return this.mString;
    }

    public int getIndex() {
        return this.mIndex;
    }

    public int getLength() {
        return this.mLength;
    }

    public Range getRange() {
        return new Range(this.mIndex, this.mLength);
    }

    public int getIndexAfterSelection() {
        return this.mIndex + this.mLength;
    }

    public boolean isRange() {
        return this.mLength > 0;
    }

    public boolean isCursor() {
        return this.mLength == 0;
    }

    public boolean isValid() {
        return this.mString != null && getIndexAfterSelection() <= this.mString.indexAfterLastElement();
    }

    public static MTSelection selectionInString(MTString string, int index, int length) {
        MTSelection selection = new MTSelection();
        selection.mString = string;
        selection.mIndex = index;
        selection.mLength = length;
        return selection;
    }

    public static MTSelection selectionInString(MTString string, Range range) {
        return selectionInString(string, range.mStartIndex, range.mLength);
    }

    public static MTSelection cursorInStringAtIndex(MTString string, int index) {
        return selectionInString(string, index, 0);
    }

    public static MTSelection cursorBeforeElement(MTElement element) {
        return cursorInStringAtIndex(element.getParent(), element.indexInParentString());
    }

    public static MTSelection cursorAfterElement(MTElement element) {
        return cursorInStringAtIndex(element.getParent(), element.indexInParentString() + 1);
    }

    public static MTSelection cursorAtEndOfString(MTString string) {
        return cursorInStringAtIndex(string, string.indexAfterLastElement());
    }

    public static MTSelection selectionWithEntireString(MTString string) {
        return selectionInString(string, 0, string.length());
    }

    public static MTSelection selectionWithElement(MTElement element) {
        return selectionInString(element.getParent(), element.indexInParentString(), 1);
    }

    public MTElement elementAfterSelection() {
        if (getIndexAfterSelection() >= this.mString.length()) {
            return null;
        }
        return this.mString.elementAtIndex(getIndexAfterSelection());
    }

    public MTElement elementBeforeSelection() {
        if (this.mIndex == 0) {
            return null;
        }
        return this.mString.elementAtIndex(this.mIndex - 1);
    }

    public MTElement firstElementInRangeSelection() {
        if (!isRange()) {
            return null;
        }
        return this.mString.elementAtIndex(this.mIndex);
    }

    public MTElement lastElementInRangeSelection() {
        if (!isRange()) {
            return null;
        }
        return this.mString.elementAtIndex(getIndexAfterSelection() - 1);
    }

    public MTSelection removeElements() {
        this.mString.removeElementsInRange(getRange());
        return cursorInStringAtIndex(this.mString, this.mIndex);
    }

    public MTSelection moveElementsToString(MTString string, int index) {
        this.mString.moveElementsInRangeToString(getRange(), string, index);
        return cursorInStringAtIndex(this.mString, this.mIndex);
    }

    public void copyElementsToString(MTString string, int index) {
        this.mString.copyElementsInRangeToString(getRange(), string, index);
    }

    @Override // java.lang.Object
    public String toString() {
        return this.mString.toString() + ", index: " + this.mIndex + ", length: " + this.mLength;
    }

    public MTSelection copy() {
        MTSelection copy = new MTSelection();
        copy.mString = this.mString;
        copy.mIndex = this.mIndex;
        copy.mLength = this.mLength;
        return copy;
    }

    @Override // java.lang.Object
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof MTSelection) {
            MTSelection selection = (MTSelection) other;
            if (this.mString == selection.getString() && this.mIndex == selection.getIndex() && this.mLength == selection.getLength()) {
                return true;
            }
        }
        return false;
    }

    @Override // java.lang.Object
    public int hashCode() {
        return (GeneralUtil.hashCode(this.mString) ^ this.mIndex) ^ this.mLength;
    }
}
