package com.sparkappdesign.archimedes.testing;

import android.util.Log;
import com.sparkappdesign.archimedes.mathexpression.numbers.MEReal;
import java.util.Random;
/* loaded from: classes.dex */
public class MERealT {
    private static final double DEF_MAX = 64.0d;
    private static final double DEF_MIN = -64.0d;
    private static final double RED_MAX = 4.0d;
    private static final double RED_MIN = -4.0d;
    private static final double TRIG_MAX = 6.283185307179586d;
    private static final double TRIG_MIN = -6.283185307179586d;

    public static void testUnitsWithRandomValues() {
        testMERealIsEqual();
        testMERealIsLess();
        testMERealAdd();
        testMERealSub();
        testMERealMul();
        testMERealDiv();
        testMERealPow();
        testMERealSqrt();
        testMERealNeg();
        testMERealSgn();
        testMERealAbs();
        testMERealSqr();
        testMERealInv();
        testMERealLn();
        testMERealLog10();
        testMERealLog();
        testMERealCeil();
        testMERealFloor();
        testMERealTrunc();
        testMERealNextToward();
        testMERealNextAbove();
        testMERealNextBelow();
        testMERealNthRoot();
        testMERealSin();
        testMERealCos();
        testMERealTan();
        testMERealAsin();
        testMERealAcos();
        testMERealAtan();
        testMERealAtan2();
        testMERealIsNaN();
        testMERealIsFinite();
        testMERealIsPositive();
        testMERealIsNegative();
        testMERealIsZero();
        Log.d("Archimedes", "Passed all MEReal random-valued unit tests!");
    }

    private static void testMERealIsEqual() {
        double random = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        if (!MEReal.isEqual(new MEReal(random), new MEReal(random))) {
            throw new RuntimeException("Failed MEReal isEqual test with value: " + random);
        }
    }

    private static void testMERealIsLess() {
        double random = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        double randomIncrement = randomInRange(0.0d, (double) RED_MAX);
        if (!MEReal.isLess(new MEReal(random), new MEReal(random + randomIncrement))) {
            throw new RuntimeException("Failed MEReal isLess test with values: " + random + ", " + randomIncrement);
        }
    }

    private static void testMERealAdd() {
        double randomA = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        double randomB = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        if (!areEqualOrBothNaN(MEReal.add(new MEReal(randomA), new MEReal(randomB)).toDouble(), randomA + randomB)) {
            throw new RuntimeException("Failed MEReal add test with values: " + randomA + ", " + randomB);
        }
    }

    private static void testMERealSub() {
        double randomA = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        double randomB = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        if (!areEqualOrBothNaN(MEReal.sub(new MEReal(randomA), new MEReal(randomB)).toDouble(), randomA - randomB)) {
            throw new RuntimeException("Failed MEReal sub test with values: " + randomA + ", " + randomB);
        }
    }

    private static void testMERealMul() {
        double randomA = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        double randomB = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        if (!areEqualOrBothNaN(MEReal.mul(new MEReal(randomA), new MEReal(randomB)).toDouble(), randomA * randomB)) {
            throw new RuntimeException("Failed MEReal mul test with values: " + randomA + ", " + randomB);
        }
    }

    private static void testMERealDiv() {
        double randomA = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        double randomB = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        if (!areEqualOrBothNaN(MEReal.div(new MEReal(randomA), new MEReal(randomB)).toDouble(), randomA / randomB)) {
            throw new RuntimeException("Failed MEReal div test with values: " + randomA + ", " + randomB);
        }
    }

    private static void testMERealPow() {
        double randomA = randomInRange((double) RED_MIN, (double) RED_MAX);
        double randomB = randomInRange((double) RED_MIN, (double) RED_MAX);
        MEReal res = MEReal.pow(new MEReal(randomA), new MEReal(randomB));
        if (res != null && !areEqualOrBothNaN(res.toDouble(), Math.pow(randomA, randomB))) {
            throw new RuntimeException("Failed MEReal pow test with values: " + randomA + ", " + randomB);
        }
    }

    private static void testMERealSqrt() {
        double random = randomInRange(0.0d, (double) DEF_MAX);
        if (!areEqualOrBothNaN(MEReal.sqrt(new MEReal(random)).toDouble(), Math.sqrt(random))) {
            throw new RuntimeException("Failed MEReal sqrt test with value: " + random);
        }
    }

    private static void testMERealNeg() {
        double random = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        if (!areEqualOrBothNaN(MEReal.neg(new MEReal(random)).toDouble(), -random)) {
            throw new RuntimeException("Failed MEReal neg test with value: " + random);
        }
    }

    private static void testMERealSgn() {
        double random = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        int res = MEReal.sgn(new MEReal(random));
        if ((random < 0.0d && res >= 0) || ((random == 0.0d && res != 0) || (random > 0.0d && res <= 0))) {
            throw new RuntimeException("Failed MEReal sgn test with value: " + random);
        }
    }

    private static void testMERealAbs() {
        double random = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        if (!areEqualOrBothNaN(MEReal.abs(new MEReal(random)).toDouble(), Math.abs(random))) {
            throw new RuntimeException("Failed MEReal abs test with value: " + random);
        }
    }

    private static void testMERealSqr() {
        double random = randomInRange((double) RED_MIN, (double) RED_MAX);
        if (!areEqualOrBothNaN(MEReal.sqr(new MEReal(random)).toDouble(), random * random)) {
            throw new RuntimeException("Failed MEReal sqr test with value: " + random);
        }
    }

    private static void testMERealInv() {
        double random = randomInRange((double) RED_MIN, (double) RED_MAX);
        if (!areEqualOrBothNaN(MEReal.inv(new MEReal(random)).toDouble(), 1.0d / random)) {
            throw new RuntimeException("Failed MEReal inv test with value: " + random);
        }
    }

    private static void testMERealLn() {
        double random = randomInRange((double) RED_MIN, (double) RED_MAX);
        if (!areEqualOrBothNaN(MEReal.ln(new MEReal(random)).toDouble(), Math.log(random))) {
            throw new RuntimeException("Failed MEReal ln test with value: " + random);
        }
    }

    private static void testMERealLog10() {
        double random = randomInRange((double) RED_MIN, (double) RED_MAX);
        if (!areEqualOrBothNaN(MEReal.log10(new MEReal(random)).toDouble(), Math.log10(random))) {
            throw new RuntimeException("Failed MEReal log10 test with value: " + random);
        }
    }

    private static void testMERealLog() {
        double randomA = randomInRange((double) RED_MIN, (double) RED_MAX);
        double randomB = randomInRange((double) RED_MIN, (double) RED_MAX);
        MEReal res = MEReal.log(new MEReal(randomA), new MEReal(randomB));
        if (res != null && !areEqualOrBothNaN(res.toDouble(), Math.log(randomA) / Math.log(randomB))) {
            throw new RuntimeException("Failed MEReal log test with values: " + randomA + ", " + randomB);
        }
    }

    private static void testMERealCeil() {
        double random = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        if (!areEqualOrBothNaN(MEReal.ceil(new MEReal(random)).toDouble(), Math.ceil(random))) {
            throw new RuntimeException("Failed MEReal ceil test with value: " + random);
        }
    }

    private static void testMERealFloor() {
        double random = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        if (!areEqualOrBothNaN(MEReal.floor(new MEReal(random)).toDouble(), Math.floor(random))) {
            throw new RuntimeException("Failed MEReal floor test with value: " + random);
        }
    }

    private static void testMERealTrunc() {
        double random = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        if (!areEqualOrBothNaN(MEReal.trunc(new MEReal(random)).toDouble(), random >= 0.0d ? Math.floor(random) : Math.ceil(random))) {
            throw new RuntimeException("Failed MEReal trunc test with value: " + random);
        }
    }

    private static void testMERealNextToward() {
        double randomA = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        double randomB = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        if (!areEqualOrBothNaN(MEReal.nextToward(new MEReal(randomA), new MEReal(randomB)).toDouble(), Math.nextAfter(randomA, randomB))) {
            throw new RuntimeException("Failed MEReal nextToward test with values: " + randomA + ", " + randomB);
        }
    }

    private static void testMERealNextAbove() {
        double random = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        if (!areEqualOrBothNaN(MEReal.nextAbove(new MEReal(random)).toDouble(), Math.nextAfter(random, Double.POSITIVE_INFINITY))) {
            throw new RuntimeException("Failed MEReal nextAbove test with value: " + random);
        }
    }

    private static void testMERealNextBelow() {
        double random = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        if (!areEqualOrBothNaN(MEReal.nextBelow(new MEReal(random)).toDouble(), Math.nextAfter(random, Double.NEGATIVE_INFINITY))) {
            throw new RuntimeException("Failed MEReal nextBelow test with value: " + random);
        }
    }

    private static void testMERealNthRoot() {
        double random = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        MEReal a = new MEReal(random);
        double b = (double) randomInRange(0, 8);
        if (!areEqualOrBothNaN(MEReal.nthRoot(a, b).toDouble(), Math.pow(random, 1.0d / b))) {
            throw new RuntimeException("Failed MEReal nthRoot test with values: " + random + ", " + b);
        }
    }

    private static void testMERealSin() {
        double random = randomInRange((double) TRIG_MIN, (double) TRIG_MAX);
        if (!areEqualOrBothNaN(MEReal.sin(new MEReal(random)).toDouble(), Math.sin(random))) {
            throw new RuntimeException("Failed MEReal sin test with value: " + random);
        }
    }

    private static void testMERealCos() {
        double random = randomInRange((double) TRIG_MIN, (double) TRIG_MAX);
        if (!areEqualOrBothNaN(MEReal.cos(new MEReal(random)).toDouble(), Math.cos(random))) {
            throw new RuntimeException("Failed MEReal cos test with value: " + random);
        }
    }

    private static void testMERealTan() {
        double random = randomInRange((double) TRIG_MIN, (double) TRIG_MAX);
        if (!areEqualOrBothNaN(MEReal.tan(new MEReal(random)).toDouble(), Math.tan(random))) {
            throw new RuntimeException("Failed MEReal tan test with value: " + random);
        }
    }

    private static void testMERealAsin() {
        double random = randomInRange((double) TRIG_MIN, (double) TRIG_MAX);
        if (!areEqualOrBothNaN(MEReal.asin(new MEReal(random)).toDouble(), Math.asin(random))) {
            throw new RuntimeException("Failed MEReal asin test with value: " + random);
        }
    }

    private static void testMERealAcos() {
        double random = randomInRange((double) TRIG_MIN, (double) TRIG_MAX);
        if (!areEqualOrBothNaN(MEReal.acos(new MEReal(random)).toDouble(), Math.acos(random))) {
            throw new RuntimeException("Failed MEReal acos test with value: " + random);
        }
    }

    private static void testMERealAtan() {
        double random = randomInRange((double) TRIG_MIN, (double) TRIG_MAX);
        if (!areEqualOrBothNaN(MEReal.atan(new MEReal(random)).toDouble(), Math.atan(random))) {
            throw new RuntimeException("Failed MEReal atan test with value: " + random);
        }
    }

    private static void testMERealAtan2() {
        double randomA = randomInRange((double) TRIG_MIN, (double) TRIG_MAX);
        double randomB = randomInRange((double) TRIG_MIN, (double) TRIG_MAX);
        if (!areEqualOrBothNaN(MEReal.atan2(new MEReal(randomA), new MEReal(randomB)).toDouble(), Math.atan2(randomA, randomB))) {
            throw new RuntimeException("Failed MEReal atan2 test with values: " + randomA + ", " + randomB);
        }
    }

    private static void testMERealIsNaN() {
        double random = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        if (new MEReal(random).isNaN() != Double.isNaN(random)) {
            throw new RuntimeException("Failed MEReal isNaN test with value: " + random);
        }
    }

    private static void testMERealIsFinite() {
        double random = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        if (new MEReal(random).isFinite() == Double.isInfinite(random)) {
            throw new RuntimeException("Failed MEReal isFinite test with value: " + random);
        }
    }

    private static void testMERealIsPositive() {
        double random = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        if (new MEReal(random).isPositive() != (random > 0.0d)) {
            throw new RuntimeException("Failed MEReal isPositive test with value: " + random);
        }
    }

    private static void testMERealIsNegative() {
        double random = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        if (new MEReal(random).isNegative() != (random < 0.0d)) {
            throw new RuntimeException("Failed MEReal isNegative test with value: " + random);
        }
    }

    private static void testMERealIsZero() {
        double random = randomInRange((double) DEF_MIN, (double) DEF_MAX);
        if (new MEReal(random).isZero() != (random == 0.0d)) {
            throw new RuntimeException("Failed MEReal isZero test with value: " + random);
        }
    }

    private static int randomInRange(int min, int max) {
        return ((max - min) * new Random().nextInt()) + min;
    }

    private static double randomInRange(double min, double max) {
        return ((max - min) * new Random().nextDouble()) + min;
    }

    private static boolean areEqualOrBothNaN(double a, double b) {
        return a == b || (Double.isNaN(a) && Double.isNaN(b));
    }
}
