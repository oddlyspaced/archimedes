package com.oddlyspaced.calci.mathexpression.numbers;

import com.oddlyspaced.calci.mathexpression.context.MEContext;
import com.oddlyspaced.calci.mathexpression.context.MEIssue;
import com.oddlyspaced.calci.tommath.SWIGTYPE_p_mp_int;
import com.oddlyspaced.calci.tommath.tommath;
import java.util.Locale;
/* loaded from: classes.dex */
public class MEReal {
    private double r;

    public int hashCode() {
        return Double.valueOf(this.r).hashCode();
    }

    public String toString() {
        return Double.toString(this.r);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof MEReal) {
            return isEqual(this, (MEReal) other);
        }
        return false;
    }

    public MEReal(double value) {
        this.r = value;
        if (Double.isInfinite(this.r)) {
            MEContext.getCurrent().stopWithError(MEIssue.VALUES_TOO_LARGE);
        }
    }

    public static MEReal positiveInfinity() {
        MEReal a = new MEReal(0.0d);
        a.r = Double.POSITIVE_INFINITY;
        return a;
    }

    public static MEReal negativeInfinity() {
        MEReal a = new MEReal(0.0d);
        a.r = Double.NEGATIVE_INFINITY;
        return a;
    }

    public static MEReal e() {
        return new MEReal(2.718281828459045d);
    }

    public static MEReal pi() {
        return new MEReal(3.141592653589793d);
    }

    public static boolean isEqual(MEReal a, MEReal b) {
        return a.r == b.r;
    }

    public static boolean isEqual(MEReal a, double b) {
        return a.r == b;
    }

    public static boolean isEqual(double a, MEReal b) {
        return isEqual(b, a);
    }

    public static boolean isLess(MEReal a, MEReal b) {
        return a.r < b.r;
    }

    public static boolean isLess(MEReal a, double b) {
        return a.r < b;
    }

    public static boolean isLess(double a, MEReal b) {
        return isMore(b, a);
    }

    public static boolean isMore(MEReal a, MEReal b) {
        return a.r > b.r;
    }

    public static boolean isMore(MEReal a, double b) {
        return a.r > b;
    }

    public static boolean isMore(double a, MEReal b) {
        return isLess(b, a);
    }

    public static MEReal min(MEReal a, MEReal b) {
        return isLess(a, b) ? a : b;
    }

    public static MEReal max(MEReal a, MEReal b) {
        return isMore(a, b) ? a : b;
    }

    public static MEReal add(MEReal a, MEReal b) {
        return new MEReal(a.r + b.r);
    }

    public static MEReal sub(MEReal a, MEReal b) {
        return new MEReal(a.r - b.r);
    }

    public static MEReal mul(MEReal a, MEReal b) {
        return new MEReal(a.r * b.r);
    }

    public static MEReal div(MEReal a, MEReal b) {
        return new MEReal(a.r / b.r);
    }

    public static MEReal pow(MEReal a, MEReal b) {
        MEReal c = new MEReal(Math.pow(a.r, b.r));
        if (!c.isNaN()) {
            return c;
        }
        MEContext.getCurrent().stopWithError(MEIssue.NEGATIVE_ROOT);
        return null;
    }

    public static MEReal sqrt(MEReal x) {
        return new MEReal(Math.sqrt(x.r));
    }

    public static MEReal neg(MEReal x) {
        return new MEReal(-x.r);
    }

    public static int sgn(MEReal x) {
        if (x.r >= 0.0d) {
            return x.r > 0.0d ? 1 : 0;
        }
        return -1;
    }

    public static MEReal abs(MEReal x) {
        return sgn(x) < 0 ? new MEReal(Math.abs(x.r)) : x;
    }

    public static MEReal sqr(MEReal x) {
        return new MEReal(x.r * x.r);
    }

    public static MEReal inv(MEReal x) {
        return new MEReal(1.0d / x.r);
    }

    public static MEReal ln(MEReal x) {
        return new MEReal(Math.log(x.r));
    }

    public static MEReal log10(MEReal x) {
        return new MEReal(Math.log10(x.r));
    }

    public static MEReal log(MEReal x, MEReal base) {
        if (!(!x.isPositive() || !base.isPositive() || isEqual(base, 1.0d))) {
            return new MEReal(Math.log(x.r) / Math.log(base.r));
        }
        MEContext.getCurrent().stopWithError(MEIssue.LOGARITHMIC_DOMAIN);
        return null;
    }

    public static MEReal ceil(MEReal x) {
        return new MEReal(Math.ceil(x.r));
    }

    public static MEReal floor(MEReal x) {
        return new MEReal(Math.floor(x.r));
    }

    public static MEReal trunc(MEReal x) {
        return x.r >= 0.0d ? floor(x) : ceil(x);
    }

    public static MEReal nextToward(MEReal x, MEReal y) {
        return new MEReal(Math.nextAfter(x.r, y.r));
    }

    public static MEReal nextAbove(MEReal x) {
        return new MEReal(Math.nextAfter(x.r, Double.POSITIVE_INFINITY));
    }

    public static MEReal nextBelow(MEReal x) {
        return new MEReal(Math.nextAfter(x.r, Double.NEGATIVE_INFINITY));
    }

    public static MEReal nthRoot(MEReal x, double n) {
        return new MEReal(Math.pow(x.r, 1.0d / n));
    }

    public static MEReal sin(MEReal x) {
        return new MEReal(Math.sin(x.r));
    }

    public static MEReal cos(MEReal x) {
        return new MEReal(Math.cos(x.r));
    }

    public static MEReal tan(MEReal x) {
        return new MEReal(Math.tan(x.r));
    }

    public static MEReal asin(MEReal x) {
        return new MEReal(Math.asin(x.r));
    }

    public static MEReal acos(MEReal x) {
        return new MEReal(Math.acos(x.r));
    }

    public static MEReal atan(MEReal x) {
        return new MEReal(Math.atan(x.r));
    }

    public static MEReal atan2(MEReal y, MEReal x) {
        return new MEReal(Math.atan2(y.r, x.r));
    }

    public boolean isNaN() {
        return Double.isNaN(this.r);
    }

    public boolean isFinite() {
        return !Double.isInfinite(this.r) && !Double.isNaN(this.r);
    }

    public boolean isPositive() {
        return this.r > 0.0d;
    }

    public boolean isNegative() {
        return this.r < 0.0d;
    }

    public boolean isZero() {
        return this.r == 0.0d;
    }

    public String scientificFormatString() {
        return String.format(Locale.US, "%.64E", Double.valueOf(this.r));
    }

    public MEReal(MEInteger value) {
        this(0.0d);
        SWIGTYPE_p_mp_int tmp = new SWIGTYPE_p_mp_int(tommath.mp_alloc_init(), true);
        tommath.mp_init(tmp);
        value.copyRaw(tmp);
        boolean isNegative = tommath.mp_get_sign(tmp) == 1;
        long bitsPerLimb = (long) tommath.getC_MP_DIGIT_BIT();
        for (int i = 0; i < tommath.mp_get_used(tmp); i++) {
            if (i > 0) {
                this.r *= (double) ((1 << ((int) (bitsPerLimb - 1))) * 2);
            }
            this.r += (double) tommath.mp_get_digit(tmp, (tommath.mp_get_used(tmp) - 1) - i);
        }
        this.r = isNegative ? -this.r : this.r;
        if (Double.isInfinite(this.r)) {
            MEContext.getCurrent().stopWithError(MEIssue.VALUES_TOO_LARGE);
        }
    }

    public MEReal(MERational value) {
        this(div(new MEReal(value.numerator()), new MEReal(value.denominator())).r);
    }

    public double toDouble() {
        return this.r;
    }
}
