package com.oddlyspaced.calci.mathexpression;

import com.oddlyspaced.calci.mathexpression.expressions.MEValue;
import com.oddlyspaced.calci.utilities.GeneralUtil;
/* loaded from: classes.dex */
public class MEInterval {
    private boolean mIsLeftClosed;
    private boolean mIsRightClosed;
    private MEValue mLeftValue;
    private MEValue mRightValue;

    public MEValue getLeftValue() {
        return this.mLeftValue;
    }

    public MEValue getRightValue() {
        return this.mRightValue;
    }

    public boolean isLeftClosed() {
        return this.mIsLeftClosed;
    }

    public boolean isRightClosed() {
        return this.mIsRightClosed;
    }

    public boolean isPoint() {
        return this.mLeftValue.equals(this.mRightValue);
    }

    private MEInterval() {
    }

    public MEInterval(MEValue leftValue, MEValue rightValue, boolean leftClosed, boolean rightClosed) {
        this.mLeftValue = leftValue;
        this.mRightValue = rightValue;
        this.mIsLeftClosed = leftClosed;
        this.mIsRightClosed = rightClosed;
    }

    public MEInterval(MEValue leftValue, MEValue rightValue, boolean closed) {
        this(leftValue, rightValue, closed, closed);
    }

    public MEInterval(double leftValue, double rightValue, boolean closed) {
        this(new MEValue(leftValue), new MEValue(rightValue), closed);
    }

    public MEInterval(MEValue value) {
        this(value, value, true);
    }

    public MEInterval(double value) {
        this(new MEValue(value));
    }

    public MEInterval intersectionWithInterval(MEInterval other) {
        boolean leftClosed;
        MEValue leftValue;
        boolean rightClosed;
        MEValue rightValue;
        if (this.mLeftValue.isMoreThanValue(other.mLeftValue)) {
            MEValue mEValue = this.mLeftValue;
            leftClosed = this.mIsLeftClosed;
            leftValue = mEValue;
        } else if (other.mLeftValue.isMoreThanValue(this.mLeftValue)) {
            MEValue mEValue2 = other.mLeftValue;
            leftClosed = other.mIsLeftClosed;
            leftValue = mEValue2;
        } else {
            MEValue leftValue2 = this.mLeftValue;
            if (!this.mIsLeftClosed || !other.mIsLeftClosed) {
                leftClosed = false;
                leftValue = leftValue2;
            } else {
                leftClosed = true;
                leftValue = leftValue2;
            }
        }
        if (this.mRightValue.isLessThanValue(other.mRightValue)) {
            MEValue mEValue3 = this.mRightValue;
            rightClosed = this.mIsRightClosed;
            rightValue = mEValue3;
        } else if (other.mRightValue.isLessThanValue(this.mRightValue)) {
            MEValue mEValue4 = other.mRightValue;
            rightClosed = other.mIsRightClosed;
            rightValue = mEValue4;
        } else {
            MEValue rightValue2 = this.mRightValue;
            if (!this.mIsRightClosed || !other.mIsRightClosed) {
                rightClosed = false;
                rightValue = rightValue2;
            } else {
                rightClosed = true;
                rightValue = rightValue2;
            }
        }
        if (rightValue.isLessThanValue(leftValue)) {
            return null;
        }
        if (!leftValue.equals(rightValue) || (leftClosed && rightClosed)) {
            return new MEInterval(leftValue, rightValue, leftClosed, rightClosed);
        }
        return null;
    }

    public MEValue length() {
        return this.mRightValue.subtractBy(this.mLeftValue);
    }

    public boolean containsValue(MEValue value) {
        if (this.mIsLeftClosed) {
            if (value.isLessThanValue(this.mLeftValue)) {
                return false;
            }
        } else if (!value.isMoreThanValue(this.mLeftValue)) {
            return false;
        }
        if (this.mIsRightClosed) {
            if (value.isMoreThanValue(this.mRightValue)) {
                return false;
            }
        } else if (!value.isLessThanValue(this.mRightValue)) {
            return false;
        }
        return true;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MEInterval)) {
            return false;
        }
        MEInterval otherInterval = (MEInterval) other;
        return GeneralUtil.equalOrBothNull(this.mLeftValue, otherInterval.mLeftValue) && GeneralUtil.equalOrBothNull(this.mRightValue, otherInterval.mRightValue) && this.mIsLeftClosed == otherInterval.mIsLeftClosed && this.mIsRightClosed == otherInterval.mIsRightClosed;
    }

    public int hashCode() {
        int i = 1;
        int hashCode = (this.mIsLeftClosed ? 1 : 0) ^ (GeneralUtil.hashCode(this.mRightValue) ^ GeneralUtil.hashCode(this.mLeftValue));
        if (!this.mIsRightClosed) {
            i = 0;
        }
        return hashCode ^ i;
    }

    public String toString() {
        return (this.mIsLeftClosed ? "[" : "(") + this.mLeftValue.toString() + ", " + this.mRightValue.toString() + (this.mIsRightClosed ? "]" : ")");
    }
}
