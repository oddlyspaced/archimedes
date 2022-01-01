package com.sparkappdesign.archimedes.utilities;
/* loaded from: classes.dex */
public class Range {
    public int mLength;
    public int mStartIndex;

    public int getMaxRange() {
        return this.mStartIndex + this.mLength;
    }

    public Range(int startIndex, int length) {
        this.mStartIndex = startIndex;
        this.mLength = length;
    }

    public static Range union(Range range1, Range range2) {
        int startIndex = Math.min(range1.mStartIndex, range2.mStartIndex);
        return new Range(startIndex, Math.max(range1.getMaxRange(), range2.getMaxRange()) - startIndex);
    }

    public static Range intersection(Range range1, Range range2) {
        int startIndex = Math.max(range1.mStartIndex, range2.mStartIndex);
        int endIndex = Math.min(range1.getMaxRange(), range2.getMaxRange());
        if (startIndex <= endIndex) {
            return new Range(startIndex, endIndex - startIndex);
        }
        return new Range(-1, 0);
    }

    public boolean contains(Range inner) {
        return inner.mStartIndex >= this.mStartIndex && inner.getMaxRange() <= getMaxRange();
    }

    public String toString() {
        return "(Index & length: " + this.mStartIndex + ", " + this.mLength + ")";
    }

    public boolean equals(Object other) {
        if (other instanceof Range) {
            Range range = (Range) other;
            if (this.mStartIndex == range.mStartIndex && this.mLength == range.mLength) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return this.mStartIndex ^ this.mLength;
    }
}
