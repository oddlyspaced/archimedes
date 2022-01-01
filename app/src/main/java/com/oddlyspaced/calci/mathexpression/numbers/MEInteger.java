package com.oddlyspaced.calci.mathexpression.numbers;

import com.oddlyspaced.calci.mathexpression.context.MEContext;
import com.oddlyspaced.calci.mathexpression.context.MEIssue;
import com.oddlyspaced.calci.tommath.SWIGTYPE_p_mp_int;
import com.oddlyspaced.calci.tommath.tommath;
/* loaded from: classes.dex */
public class MEInteger {
    private static final long MAX_WORDS = 12000;
    private SWIGTYPE_p_mp_int z;

    public int hashCode() {
        return (int) tommath.mp_get_int(this.z);
    }

    public String toString() {
        return tommath.mp_get_str(this.z, 10);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof MEInteger) {
            return isEqual(this, (MEInteger) other);
        }
        return false;
    }

    public MEInteger() {
        this.z = new SWIGTYPE_p_mp_int(tommath.mp_alloc_init(), true);
        tommath.mp_init(this.z);
    }

    public MEInteger(long value) {
        this();
        tommath.mp_set(this.z, Math.abs(value));
        tommath.mp_set_sign(this.z, value >= 0 ? 0 : 1);
    }

    protected void finalize() throws Throwable {
        try {
            tommath.mp_clear_free(this.z);
        } finally {
            super.finalize();
        }
    }

    public void copyRaw(SWIGTYPE_p_mp_int target) {
        tommath.mp_copy(this.z, target);
    }

    public static boolean isEqual(MEInteger a, MEInteger b) {
        return tommath.mp_cmp(a.z, b.z) == 0;
    }

    public static boolean isEqual(MEInteger a, long b) {
        return tommath.mp_cmp_d(a.z, Math.abs(b)) == 0 && sgn(a) == sgn(b);
    }

    public static boolean isEqual(long a, MEInteger b) {
        return isEqual(b, a);
    }

    public static boolean isLess(MEInteger a, MEInteger b) {
        return tommath.mp_cmp(a.z, b.z) == -1;
    }

    public static boolean isLess(MEInteger a, long b) {
        return tommath.mp_cmp_d(a.z, b) == -1;
    }

    public static boolean isLess(long a, MEInteger b) {
        return isMore(b, a);
    }

    public static boolean isMore(MEInteger a, MEInteger b) {
        return tommath.mp_cmp(a.z, b.z) == 1;
    }

    public static boolean isMore(MEInteger a, long b) {
        return tommath.mp_cmp_d(a.z, b) == 1;
    }

    public static boolean isMore(long a, MEInteger b) {
        return isLess(b, a);
    }

    public static MEInteger add(MEInteger a, MEInteger b) {
        MEInteger c = new MEInteger();
        tommath.mp_add(a.z, b.z, c.z);
        return c;
    }

    public static MEInteger sub(MEInteger a, MEInteger b) {
        MEInteger c = new MEInteger();
        tommath.mp_sub(a.z, b.z, c.z);
        return c;
    }

    public static MEInteger mul(MEInteger a, MEInteger b) {
        MEInteger c = new MEInteger();
        tommath.mp_mul(a.z, b.z, c.z);
        return c;
    }

    public static MEInteger div(MEInteger a, MEInteger b) {
        if (isEqual(b, 0)) {
            MEContext.getCurrent().stopWithError(MEIssue.DIVISION_BY_ZERO);
            return null;
        }
        MEInteger c = new MEInteger();
        tommath.mp_div(a.z, b.z, c.z, null);
        return c;
    }

    public static MEInteger mod(MEInteger a, MEInteger b) {
        if (isEqual(b, 0)) {
            MEContext.getCurrent().stopWithError(MEIssue.DIVISION_BY_ZERO);
            return null;
        }
        MEInteger c = new MEInteger();
        tommath.mp_mod(a.z, b.z, c.z);
        return c;
    }

    public static MEInteger pow(MEInteger a, MEInteger b) {
        if (isMore(b, Long.MAX_VALUE) || ((long) tommath.mp_get_used(a.z)) * abs(b).toLong() > MAX_WORDS) {
            MEContext.getCurrent().stopWithError(MEIssue.VALUES_TOO_LARGE);
            return null;
        }
        MEInteger c = new MEInteger();
        if (!b.isPositive()) {
            return new MEInteger(0);
        }
        tommath.mp_expt_d(a.z, abs(b).toLong(), c.z);
        return c;
    }

    public static MEInteger neg(MEInteger x) {
        MEInteger y = new MEInteger();
        tommath.mp_neg(x.z, y.z);
        return y;
    }

    public static MEInteger and(MEInteger a, MEInteger b) {
        MEInteger c = new MEInteger();
        tommath.mp_and(a.z, b.z, c.z);
        return c;
    }

    public static MEInteger or(MEInteger a, MEInteger b) {
        MEInteger c = new MEInteger();
        tommath.mp_or(a.z, b.z, c.z);
        return c;
    }

    public static MEInteger xor(MEInteger a, MEInteger b) {
        MEInteger c = new MEInteger();
        tommath.mp_xor(a.z, b.z, c.z);
        return c;
    }

    public static int sgn(MEInteger x) {
        if (tommath.mp_get_sign(x.z) == 0) {
            return tommath.mp_get_used(x.z) > 0 ? 1 : 0;
        }
        return -1;
    }

    public static int sgn(long x) {
        if (x >= 0) {
            return x > 0 ? 1 : 0;
        }
        return -1;
    }

    public static MEInteger abs(MEInteger x) {
        MEInteger y = new MEInteger();
        if (sgn(x) >= 0) {
            return x;
        }
        tommath.mp_abs(x.z, y.z);
        return y;
    }

    public static MEInteger sqr(MEInteger x) {
        MEInteger y = new MEInteger();
        tommath.mp_sqr(x.z, y.z);
        return y;
    }

    public static MERational inv(MEInteger x) {
        return new MERational(1, x);
    }

    public static MEInteger gcd(MEInteger a, MEInteger b) {
        MEInteger c = new MEInteger();
        tommath.mp_gcd(a.z, b.z, c.z);
        return c;
    }

    public static MEInteger lcm(MEInteger a, MEInteger b) {
        MEInteger c = new MEInteger();
        tommath.mp_lcm(a.z, b.z, c.z);
        return c;
    }

    public static MEInteger factorial(long n) {
        if (n < 2) {
            return new MEInteger(1);
        }
        MEInteger result = new MEInteger(1);
        for (int i = 2; ((long) i) <= n; i++) {
            tommath.mp_mul_d(result.z, (long) i, result.z);
        }
        return result;
    }

    public static MEInteger binomial(long n, long k) {
        if (k > n) {
            return new MEInteger(0);
        }
        if (k < 1) {
            return new MEInteger(1);
        }
        MERational result = new MERational(1);
        MEInteger nmk = sub(new MEInteger(n), new MEInteger(k));
        for (int i = 1; ((long) i) <= k; i++) {
            MEInteger j = new MEInteger((long) i);
            result = MERational.mul(result, new MERational(add(nmk, j), j));
        }
        return new MEInteger(result);
    }

    public static MEInteger truncatedNthRoot(MEInteger x, long n) {
        MEInteger result = new MEInteger();
        tommath.mp_n_root(x.z, n, result.z);
        return result;
    }

    public static MEInteger highestPerfectPowerFactor(MEInteger x, long n) {
        if (n == 0) {
            return null;
        }
        MEInteger tmp = new MEInteger();
        tommath.mp_expt_d(truncatedNthRoot(x, n).z, n, tmp.z);
        if (isEqual(tmp, x)) {
            return x;
        }
        MEInteger result = new MEInteger(1);
        MEInteger prime = new MEInteger(2);
        MEInteger factor = new MEInteger(0);
        MEInteger rest = new MEInteger();
        tommath.mp_abs(x.z, rest.z);
        boolean negateResult = x.isOdd() && x.isNegative();
        while (!MEContext.shouldStop()) {
            tommath.mp_expt_d(prime.z, n, factor.z);
            while (true) {
                tommath.mp_mod(rest.z, factor.z, tmp.z);
                if (!tmp.isZero()) {
                    break;
                }
                tommath.mp_div(rest.z, factor.z, rest.z, null);
                tommath.mp_mul(result.z, factor.z, result.z);
            }
            tommath.mp_prime_next_prime(prime.z, 5, 0);
            if (!isMore(rest, factor)) {
                if (negateResult) {
                    tommath.mp_neg(result.z, result.z);
                }
                return result;
            }
        }
        return null;
    }

    public static MEInteger perfectLogarithm(MEInteger x, MEInteger base) {
        if (!base.isPositive() || isEqual(base, 1) || !x.isPositive()) {
            return null;
        }
        long exponent = 0;
        MEInteger result = new MEInteger();
        do {
            tommath.mp_expt_d(base.z, exponent, result.z);
            if (isEqual(result, x)) {
                return new MEInteger(exponent);
            }
            if (isMore(result, x)) {
                return null;
            }
            exponent++;
        } while (exponent != Long.MAX_VALUE);
        return null;
    }

    public boolean isPositive() {
        return sgn(this) > 0;
    }

    public boolean isNegative() {
        return sgn(this) < 0;
    }

    public boolean isZero() {
        return sgn(this) == 0;
    }

    public boolean isOdd() {
        return tommath.mp_get_used(this.z) > 0 && (tommath.mp_get_digit(this.z, 0) & 1) == 1;
    }

    public boolean isEven() {
        return !isOdd();
    }

    public long toLong() {
        return tommath.mp_get_int(this.z);
    }

    public MEInteger(MERational v) {
        this();
        if (isEqual(v.denominator(), 1)) {
            tommath.mp_copy(v.numerator().z, this.z);
        } else {
            tommath.mp_div(v.numerator().z, v.denominator().z, this.z, null);
        }
    }

    public MEInteger(MEReal v) {
        this(0);
        double d = v.toDouble();
        if (!v.isFinite()) {
            throw new IllegalArgumentException("v must be finite");
        } else if (Math.abs(d) >= 1.0d) {
            double significand = significand(d);
            int exponent = exponent(d);
            boolean isNegative = significand < 0.0d;
            double significand2 = Math.abs(significand);
            while (significand2 != 0.0d) {
                if (significand2 >= 1.0d) {
                    tommath.mp_add_d(this.z, 1, this.z);
                    significand2 -= 1.0d;
                }
                significand2 *= 2.0d;
                tommath.mp_mul_2(this.z, this.z);
                exponent--;
            }
            if (exponent > 0) {
                tommath.mp_mul_2d(this.z, exponent, this.z);
            } else {
                tommath.mp_div_2d(this.z, -exponent, this.z, null);
            }
            if (isNegative) {
                tommath.mp_set_sign(this.z, 1);
            }
        }
    }

    private double significand(double d) {
        long lbits = Double.doubleToLongBits(d);
        return Double.longBitsToDouble(((lbits >>> 63) << 63) | ((1023 + 0) << 52) | (lbits & 4503599627370495L));
    }

    private int exponent(double d) {
        return (int) (((Double.doubleToLongBits(d) >>> 52) & 2047) - 1023);
    }
}
