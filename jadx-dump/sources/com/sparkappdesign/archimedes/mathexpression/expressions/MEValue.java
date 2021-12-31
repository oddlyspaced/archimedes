package com.sparkappdesign.archimedes.mathexpression.expressions;

import com.sparkappdesign.archimedes.mathexpression.numbers.MEInteger;
import com.sparkappdesign.archimedes.mathexpression.numbers.MERational;
import com.sparkappdesign.archimedes.mathexpression.numbers.MEReal;
import com.sparkappdesign.archimedes.utilities.GeneralUtil;
/* loaded from: classes.dex */
public class MEValue extends MEExpression {
    private static MEValue e;
    private static MEValue minusOne;
    private static MEValue negativeInfinity;
    private static MEValue one;
    private static MEValue pi;
    private static MEValue positiveInfinity;
    private static MEValue zero;
    private MERational mRational;
    private MEReal mReal;

    public MERational getRational() {
        return this.mRational;
    }

    public MEReal getReal() {
        return this.mReal;
    }

    private MEValue() {
    }

    private MEValue(MERational rational, MEReal real) {
        this.mRational = rational;
        this.mReal = real;
    }

    public MEValue(MERational rational) {
        this.mRational = rational;
        this.mReal = new MEReal(rational);
    }

    public MEValue(MEInteger integer) {
        this(new MERational(integer));
    }

    public MEValue(MEReal real) {
        this.mReal = real;
    }

    public MEValue(long value) {
        this(new MERational(value));
    }

    public MEValue(double value) {
        this(new MEReal(value));
    }

    public MEValue(long numerator, long denominator) {
        this(new MERational(numerator, denominator));
    }

    public MEValue(MEInteger numerator, MEInteger denominator) {
        this(new MERational(numerator, denominator));
    }

    public static synchronized MEValue zero() {
        MEValue mEValue;
        synchronized (MEValue.class) {
            if (zero == null) {
                zero = new MEValue(new MERational(0), new MEReal(0.0d));
            }
            mEValue = zero;
        }
        return mEValue;
    }

    public static synchronized MEValue one() {
        MEValue mEValue;
        synchronized (MEValue.class) {
            if (one == null) {
                one = new MEValue(new MERational(1), new MEReal(1.0d));
            }
            mEValue = one;
        }
        return mEValue;
    }

    public static synchronized MEValue minusOne() {
        MEValue mEValue;
        synchronized (MEValue.class) {
            if (minusOne == null) {
                minusOne = new MEValue(new MERational(-1), new MEReal(-1.0d));
            }
            mEValue = minusOne;
        }
        return mEValue;
    }

    public static synchronized MEValue positiveInfinity() {
        MEValue mEValue;
        synchronized (MEValue.class) {
            if (positiveInfinity == null) {
                positiveInfinity = new MEValue(MEReal.positiveInfinity());
            }
            mEValue = positiveInfinity;
        }
        return mEValue;
    }

    public static synchronized MEValue negativeInfinity() {
        MEValue mEValue;
        synchronized (MEValue.class) {
            if (negativeInfinity == null) {
                negativeInfinity = new MEValue(MEReal.negativeInfinity());
            }
            mEValue = negativeInfinity;
        }
        return mEValue;
    }

    public static synchronized MEValue pi() {
        MEValue mEValue;
        synchronized (MEValue.class) {
            if (pi == null) {
                pi = new MEValue(new MEReal(3.141592653589793d));
            }
            mEValue = pi;
        }
        return mEValue;
    }

    public static synchronized MEValue e() {
        MEValue mEValue;
        synchronized (MEValue.class) {
            if (e == null) {
                e = new MEValue(new MEReal(2.718281828459045d));
            }
            mEValue = e;
        }
        return mEValue;
    }

    public MEValue addWith(MEValue other) {
        if (this.mRational == null || other.mRational == null) {
            return new MEValue(MEReal.add(this.mReal, other.mReal));
        }
        return new MEValue(MERational.add(this.mRational, other.mRational));
    }

    public MEValue subtractBy(MEValue other) {
        if (this.mRational == null || other.mRational == null) {
            return new MEValue(MEReal.sub(this.mReal, other.mReal));
        }
        return new MEValue(MERational.sub(this.mRational, other.mRational));
    }

    public MEValue multiplyBy(MEValue other) {
        if (this.mRational == null || other.mRational == null) {
            return new MEValue(MEReal.mul(this.mReal, other.mReal));
        }
        return new MEValue(MERational.mul(this.mRational, other.mRational));
    }

    public MEValue divideBy(MEValue other) {
        if (this.mRational == null || other.mRational == null) {
            return new MEValue(MEReal.div(this.mReal, other.mReal));
        }
        return new MEValue(MERational.div(this.mRational, other.mRational));
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEValue negate() {
        if (this.mRational != null) {
            return new MEValue(MERational.neg(this.mRational));
        }
        return new MEValue(MEReal.neg(this.mReal));
    }

    public MEValue absolute() {
        if (this.mRational != null) {
            return new MEValue(MERational.abs(this.mRational));
        }
        return new MEValue(MEReal.abs(this.mReal));
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEValue invert() {
        if (this.mRational != null) {
            return new MEValue(MERational.inv(this.mRational));
        }
        return new MEValue(MEReal.inv(this.mReal));
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEValue coefficient() {
        return this;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEValue partWithoutCoefficient() {
        return null;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEReal evaluateNumerically(MEReal value, MEVariable variable) {
        return this.mReal;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public double evaluateNumerically(double value, MEVariable variable) {
        return this.mReal.toDouble();
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MEValue)) {
            return false;
        }
        MEValue otherValue = (MEValue) other;
        if (this.mRational == null || otherValue.mRational == null) {
            return MEReal.isEqual(this.mReal, otherValue.mReal);
        }
        return MERational.isEqual(this.mRational, otherValue.mRational);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public int hashCode() {
        return GeneralUtil.hashCode(this.mRational) ^ GeneralUtil.hashCode(this.mReal);
    }

    public boolean isEqual(long other) {
        return this.mRational != null ? MERational.isEqual(this.mRational, other) : MEReal.isEqual(this.mReal, (double) other);
    }

    public boolean isLessThanValue(MEValue other) {
        if (this.mRational == null || other.getRational() == null) {
            return MEReal.isLess(this.mReal, other.getReal());
        }
        return MERational.isLess(this.mRational, other.getRational());
    }

    public boolean isMoreThanValue(MEValue other) {
        if (this.mRational == null || other.getRational() == null) {
            return MEReal.isMore(this.mReal, other.getReal());
        }
        return MERational.isMore(this.mRational, other.getRational());
    }

    public static MEValue minOf(MEValue a, MEValue b) {
        if (a.getRational() != null && b.getRational() != null) {
            return MERational.isLess(a.getRational(), b.getRational()) ? a : b;
        }
        if (a.getReal() == null || b.getReal() == null) {
            return null;
        }
        if (!MEReal.isLess(a.getReal(), b.getReal())) {
            return b;
        }
        return a;
    }

    public static MEValue maxOf(MEValue a, MEValue b) {
        if (a.getRational() != null && b.getRational() != null) {
            return MERational.isMore(a.getRational(), b.getRational()) ? a : b;
        }
        if (a.getReal() == null || b.getReal() == null) {
            return null;
        }
        if (!MEReal.isMore(a.getReal(), b.getReal())) {
            return b;
        }
        return a;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public boolean isNegative() {
        return this.mReal.isNegative();
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public boolean isZero() {
        return this.mReal.isZero();
    }

    public double toDouble() {
        return this.mReal.toDouble();
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public String toString() {
        return this.mRational != null ? this.mRational.toString() : this.mReal.toString();
    }
}
