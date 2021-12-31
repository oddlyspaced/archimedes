package com.sparkappdesign.archimedes.mathexpression.numbers;

import com.sparkappdesign.archimedes.mathexpression.context.MEContext;
import com.sparkappdesign.archimedes.mathexpression.context.MEIssue;
import java.util.Collection;
/* loaded from: classes.dex */
public class MERational {
    private MEInteger d;
    private MEInteger n;

    public MEInteger numerator() {
        return this.n;
    }

    public MEInteger denominator() {
        return this.d;
    }

    public int hashCode() {
        return this.n.hashCode() ^ (this.d.hashCode() ^ -1);
    }

    public String toString() {
        return !MEInteger.isEqual(this.d, 1) ? this.n.toString() + "/" + this.d.toString() : this.n.toString();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof MERational) {
            return isEqual(this, (MERational) other);
        }
        return false;
    }

    public MERational(MEInteger n, MEInteger d) {
        if (MEInteger.isEqual(d, 1)) {
            this.n = n;
            this.d = d;
        } else if (MEInteger.isEqual(d, 0)) {
            MEContext.getCurrent().stopWithError(MEIssue.DIVISION_BY_ZERO);
            throw new IllegalArgumentException("divisor can't be 0");
        } else {
            MEInteger factor = MEInteger.gcd(n, d);
            factor = MEInteger.sgn(d) < 0 ? MEInteger.neg(factor) : factor;
            this.n = MEInteger.div(n, factor);
            this.d = MEInteger.div(d, factor);
        }
    }

    public MERational(MEInteger v) {
        this(v, new MEInteger(1));
    }

    public MERational(long v) {
        this(new MEInteger(v));
    }

    public MERational(long n, long d) {
        this(new MEInteger(n), new MEInteger(d));
    }

    public MERational(long n, MEInteger d) {
        this(new MEInteger(n), d);
    }

    public static boolean isEqual(MERational a, MERational b) {
        return MEInteger.isEqual(a.n, b.n) && MEInteger.isEqual(a.d, b.d);
    }

    public static boolean isEqual(MERational a, MEInteger b) {
        return MEInteger.isEqual(a.d, 1) && MEInteger.isEqual(a.n, b);
    }

    public static boolean isEqual(MEInteger a, MERational b) {
        return isEqual(b, a);
    }

    public static boolean isEqual(MERational a, long b) {
        return MEInteger.isEqual(a.d, 1) && MEInteger.isEqual(a.n, b);
    }

    public static boolean isEqual(long a, MERational b) {
        return isEqual(b, a);
    }

    public static boolean isLess(MERational a, MERational b) {
        if (sgn(a) != sgn(b)) {
            return sgn(a) < sgn(b);
        }
        return MEInteger.isLess(MEInteger.mul(a.n, b.d), MEInteger.mul(b.n, a.d));
    }

    public static boolean isMore(MERational a, MERational b) {
        if (sgn(a) != sgn(b)) {
            return sgn(a) > sgn(b);
        }
        return MEInteger.isMore(MEInteger.mul(a.n, b.d), MEInteger.mul(b.n, a.d));
    }

    public static MERational min(MERational a, MERational b) {
        return isLess(a, b) ? a : b;
    }

    public static MERational max(MERational a, MERational b) {
        return isMore(a, b) ? a : b;
    }

    public static MERational add(MERational a, MERational b) {
        return new MERational(MEInteger.add(MEInteger.mul(a.n, b.d), MEInteger.mul(b.n, a.d)), MEInteger.mul(a.d, b.d));
    }

    public static MERational add(MERational a, MEInteger b) {
        return new MERational(MEInteger.add(a.n, MEInteger.mul(b, a.d)), a.d);
    }

    public static MERational add(MEInteger a, MERational b) {
        return add(b, a);
    }

    public static MERational sub(MERational a, MERational b) {
        return new MERational(MEInteger.sub(MEInteger.mul(a.n, b.d), MEInteger.mul(b.n, a.d)), MEInteger.mul(a.d, b.d));
    }

    public static MERational sub(MERational a, MEInteger b) {
        return new MERational(MEInteger.sub(a.n, MEInteger.mul(b, a.d)), a.d);
    }

    public static MERational sub(MEInteger a, MERational b) {
        return new MERational(MEInteger.sub(MEInteger.mul(a, b.d), b.n), b.d);
    }

    public static MERational mul(MERational a, MERational b) {
        return new MERational(MEInteger.mul(a.n, b.n), MEInteger.mul(a.d, b.d));
    }

    public static MERational mul(MERational a, MEInteger b) {
        return new MERational(MEInteger.mul(a.n, b), a.d);
    }

    public static MERational mul(MEInteger a, MERational b) {
        return mul(b, a);
    }

    public static MERational div(MERational a, MERational b) {
        return new MERational(MEInteger.mul(a.n, b.d), MEInteger.mul(a.d, b.n));
    }

    public static MERational div(MERational a, MEInteger b) {
        return new MERational(a.n, MEInteger.mul(a.d, b));
    }

    public static MERational div(MEInteger a, MERational b) {
        return new MERational(MEInteger.mul(a, b.d), b.n);
    }

    public static MERational div(MEInteger a, MEInteger b) {
        return new MERational(a, b);
    }

    public static MERational pow(MERational a, MEInteger b) {
        if (!b.isNegative()) {
            return new MERational(MEInteger.pow(a.n, b), MEInteger.pow(a.d, b));
        }
        MEInteger b2 = MEInteger.abs(b);
        return new MERational(MEInteger.pow(a.d, b2), MEInteger.pow(a.n, b2));
    }

    public static MERational neg(MERational x) {
        return new MERational(MEInteger.neg(x.n), x.d);
    }

    public static MERational abs(MERational x) {
        return MEInteger.sgn(x.n) < 0 ? new MERational(MEInteger.abs(x.n), x.d) : x;
    }

    public static MERational sqr(MERational x) {
        return new MERational(MEInteger.sqr(x.n), MEInteger.sqr(x.d));
    }

    public static MERational inv(MERational x) {
        return new MERational(x.d, x.n);
    }

    public static int sgn(MERational x) {
        return MEInteger.sgn(x.n);
    }

    public static MERational multiplierToMakeSmallestPossibleIntegersFromRationals(Collection<MERational> values) {
        MEInteger lcm = null;
        MEInteger gcd = null;
        for (MERational value : values) {
            if (!value.isZero()) {
                lcm = lcm != null ? MEInteger.lcm(lcm, value.d) : value.d;
                gcd = gcd != null ? MEInteger.gcd(gcd, value.n) : value.n;
            }
        }
        if (lcm == null || gcd == null) {
            return new MERational(1);
        }
        return new MERational(lcm, gcd);
    }

    public boolean isPositive() {
        return this.n.isPositive();
    }

    public boolean isNegative() {
        return this.n.isNegative();
    }

    public boolean isZero() {
        return this.n.isZero();
    }

    public boolean isInteger() {
        return MEInteger.isEqual(this.d, 1);
    }

    public boolean isFractional() {
        return !isInteger();
    }
}
