package com.sparkappdesign.archimedes.mathexpression.solvers;

import com.sparkappdesign.archimedes.mathexpression.MEInterval;
import com.sparkappdesign.archimedes.mathexpression.context.MEContext;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEValue;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEVariable;
import com.sparkappdesign.archimedes.mathexpression.numbers.MEInteger;
import com.sparkappdesign.archimedes.mathexpression.numbers.MEReal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
/* loaded from: classes.dex */
public class MENumericRootFinder {

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class MEVASStackFrame {
        MEInteger a;
        MEInteger b;
        MEInteger c;
        MEInteger d;
        MEPolynomial p;
        int s;

        MEVASStackFrame(MEInteger a, MEInteger b, MEInteger c, MEInteger d, MEPolynomial p, int s) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.p = p;
            this.s = s;
        }
    }

    private MENumericRootFinder() {
    }

    public static HashSet<MEExpression> findRootsForPolynomial(MEPolynomial fullPolynomial) {
        HashSet<MEExpression> rootValues = null;
        HashSet<MEReal> roots = new HashSet<>();
        ArrayList<MEPolynomial> squarefreeFactors = fullPolynomial.squarefreeFactorization();
        if (!MEContext.shouldStop()) {
            Iterator<MEPolynomial> it = squarefreeFactors.iterator();
            while (true) {
                if (it.hasNext()) {
                    MEPolynomial polynomial = it.next();
                    if (polynomial.getLastCoefficient().isZero()) {
                        roots.add(new MEReal(0.0d));
                        polynomial = polynomial.polynomialWithNonZeroLastCoefficient();
                    }
                    roots.addAll(findPositiveRealRootsForSquarefreePolynomial(polynomial));
                    if (MEContext.shouldStop()) {
                        break;
                    }
                    Iterator<MEReal> it2 = findPositiveRealRootsForSquarefreePolynomial(polynomial.transformInputByNegation()).iterator();
                    while (it2.hasNext()) {
                        roots.add(MEReal.neg(it2.next()));
                    }
                    if (MEContext.shouldStop()) {
                        break;
                    }
                } else {
                    rootValues = new HashSet<>();
                    Iterator<MEReal> it3 = roots.iterator();
                    while (it3.hasNext()) {
                        rootValues.add(new MEValue(it3.next()));
                    }
                }
            }
        }
        return rootValues;
    }

    private static HashSet<MEReal> findPositiveRealRootsForSquarefreePolynomial(MEPolynomial polynomial) {
        MEReal root;
        HashSet<MEReal> roots = new HashSet<>();
        HashSet<MEInterval> rootIntervals = findPositiveRealRootIntervalsUsingVincentAkritasStrzebonskiMethodForSquarefreePolynomial(polynomial);
        if (MEContext.shouldStop() || rootIntervals.size() == 0) {
            return roots;
        }
        MEReal lowerBound = lowerBoundForRootsOfPolynomial(polynomial);
        MEReal upperBound = upperBoundUsingLocalMaxQuadraticImplementationForRootsOfPolynomial(polynomial);
        if (lowerBound == null || upperBound == null) {
            return null;
        }
        MEInterval bounds = new MEInterval(new MEValue(lowerBound), new MEValue(upperBound), false);
        Iterator<MEInterval> it = rootIntervals.iterator();
        while (it.hasNext()) {
            MEInterval interval = it.next().intersectionWithInterval(bounds);
            if (interval.isPoint()) {
                root = interval.getLeftValue().getReal();
            } else {
                root = findRootUsingRiddersMethodInIntervalForFunction(interval, MEFunction.functionWithExpression(polynomial.expressionWithVariable(new MEVariable("x"))));
            }
            if (root != null) {
                roots.add(root);
            }
            if (MEContext.shouldStop()) {
                return null;
            }
        }
        return roots;
    }

    private static HashSet<MEInterval> findPositiveRealRootIntervalsUsingVincentAkritasStrzebonskiMethodForSquarefreePolynomial(MEPolynomial f) {
        int s = f.numberOfSignChanges();
        if (s == 0) {
            return new HashSet<>();
        }
        if (s == 1) {
            HashSet<MEInterval> result = new HashSet<>();
            result.add(new MEInterval(MEValue.zero(), MEValue.positiveInfinity(), false));
            return result;
        }
        MEInteger alpha0 = new MEInteger(16);
        HashSet<MEInterval> intervals = new HashSet<>();
        ArrayList<MEVASStackFrame> stackFrames = new ArrayList<>();
        stackFrames.add(new MEVASStackFrame(new MEInteger(1), new MEInteger(0), new MEInteger(0), new MEInteger(1), f, s));
        while (!MEContext.shouldStop() && stackFrames.size() != 0) {
            MEVASStackFrame frame = stackFrames.get(stackFrames.size() - 1);
            stackFrames.remove(stackFrames.size() - 1);
            MEInteger a = frame.a;
            MEInteger b = frame.b;
            MEInteger c = frame.c;
            MEInteger d = frame.d;
            MEPolynomial p = frame.p;
            int s2 = frame.s;
            MEInteger alpha = new MEInteger(lowerBoundForRootsOfPolynomial(p));
            if (MEInteger.isMore(alpha, alpha0)) {
                p = p.transformInputByMultiplicationWithExpression(new MEValue(alpha));
                a = MEInteger.mul(a, alpha);
                c = MEInteger.mul(c, alpha);
                alpha = new MEInteger(1);
            }
            if (!MEInteger.isLess(alpha, 1)) {
                p = p.transformInputByAdditionOfExpression(new MEValue(alpha));
                b = MEInteger.add(MEInteger.mul(a, alpha), b);
                d = MEInteger.add(MEInteger.mul(c, alpha), d);
                if (p.getLastCoefficient().isZero()) {
                    intervals.add(new MEInterval(new MEValue(b, d)));
                    p = p.polynomialWithNonZeroLastCoefficient();
                }
                s2 = p.numberOfSignChanges();
                if (s2 != 0) {
                    if (s2 == 1) {
                        intervals.add(isolationInterval(a, b, c, d));
                    }
                }
            }
            MEPolynomial p1 = p.transformInputByAdditionOfExpression(MEValue.one());
            MEInteger a1 = a;
            MEInteger b1 = MEInteger.add(a, b);
            MEInteger c1 = c;
            MEInteger d1 = MEInteger.add(c, d);
            int r = 0;
            if (p1.getLastCoefficient().isZero()) {
                intervals.add(new MEInterval(new MEValue(b1, d1)));
                p1 = p1.polynomialWithNonZeroLastCoefficient();
                r = 1;
            }
            int s1 = p1.numberOfSignChanges();
            int s22 = (s2 - s1) - r;
            MEInteger a2 = b;
            MEInteger b2 = MEInteger.add(a, b);
            MEInteger c2 = d;
            MEInteger d2 = MEInteger.add(c, d);
            MEPolynomial p2 = null;
            if (s22 > 1) {
                ArrayList<MEExpression> reverseCoefficients = new ArrayList<>(p.getCoefficients());
                Collections.reverse(reverseCoefficients);
                p2 = MEPolynomial.polynomialWithCoefficients(reverseCoefficients).transformInputByAdditionOfExpression(MEValue.one());
                if (p2.getLastCoefficient().isZero()) {
                    p2 = p2.polynomialWithNonZeroLastCoefficient();
                }
                s22 = p2.numberOfSignChanges();
            }
            if (s1 < s22) {
                a1 = a2;
                a2 = a1;
                b1 = b2;
                b2 = b1;
                c1 = c2;
                c2 = c1;
                d1 = d2;
                d2 = d1;
                p1 = p2;
                p2 = p1;
                s1 = s22;
                s22 = s1;
            }
            if (s1 != 0) {
                if (s1 == 1) {
                    intervals.add(isolationInterval(a1, b1, c1, d1));
                }
                if (s1 >= 2) {
                    stackFrames.add(new MEVASStackFrame(a1, b1, c1, d1, p1, s1));
                }
                if (s22 != 0) {
                    if (s22 == 1) {
                        intervals.add(isolationInterval(a2, b2, c2, d2));
                    }
                    if (s22 >= 2) {
                        stackFrames.add(new MEVASStackFrame(a2, b2, c2, d2, p2, s22));
                    }
                }
            }
        }
        return intervals;
    }

    private static MEInterval isolationInterval(MEInteger a, MEInteger b, MEInteger c, MEInteger d) {
        MEValue aOverC = !c.isZero() ? new MEValue(a, c) : MEValue.positiveInfinity();
        MEValue bOverD = new MEValue(b, d);
        return new MEInterval(MEValue.minOf(aOverC, bOverD), MEValue.maxOf(aOverC, bOverD), false);
    }

    private static MEReal lowerBoundForRootsOfPolynomial(MEPolynomial polynomial) {
        ArrayList<MEExpression> coefficientsInReverse = new ArrayList<>(polynomial.getCoefficients());
        Collections.reverse(coefficientsInReverse);
        return MEReal.inv(upperBoundUsingLocalMaxQuadraticImplementationForRootsOfPolynomial(MEPolynomial.polynomialWithCoefficients(coefficientsInReverse)));
    }

    private static MEReal upperBoundUsingLocalMaxQuadraticImplementationForRootsOfPolynomial(MEPolynomial polynomial) {
        MEReal two = new MEReal(2.0d);
        if (polynomial.getFirstCoefficient().isNegative()) {
            polynomial = polynomial.negate();
        }
        int n = polynomial.degree();
        ArrayList<MEReal> a = new ArrayList<>();
        ArrayList<Integer> t = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            MEExpression coefficient = polynomial.getCoefficients().get(n - i);
            if (!(coefficient instanceof MEValue)) {
                return null;
            }
            a.add(((MEValue) coefficient).getReal());
            t.add(1);
        }
        MEReal max = new MEReal(0.0d);
        for (int i2 = 0; i2 <= n; i2++) {
            if (MEReal.sgn(a.get(i2)) == -1) {
                MEReal min = MEReal.positiveInfinity();
                for (int j = i2 + 1; j <= n; j++) {
                    if (MEContext.shouldStop()) {
                        return null;
                    }
                    if (MEReal.sgn(a.get(j)) == 1) {
                        int tj = t.get(j).intValue();
                        MEReal value = MEReal.nthRoot(MEReal.neg(MEReal.div(a.get(i2), MEReal.div(a.get(j), MEReal.pow(two, new MEReal((double) tj))))), (double) (j - i2));
                        t.set(j, Integer.valueOf(tj + 1));
                        if (MEReal.isLess(value, min)) {
                            min = value;
                        }
                    }
                }
                if (MEReal.isMore(min, max)) {
                    max = min;
                }
            }
        }
        return max;
    }

    public static MEReal findRootUsingRiddersMethodInIntervalForFunction(MEInterval interval, MEFunction function) {
        MEReal two = new MEReal(2.0d);
        MEReal x1 = interval.getLeftValue().getReal();
        MEReal x2 = interval.getRightValue().getReal();
        if (!interval.isLeftClosed()) {
            x1 = MEReal.add(x1, MEReal.mul(MEReal.abs(x1), new MEReal(1.0E-10d)));
        }
        if (!interval.isRightClosed()) {
            x2 = MEReal.sub(x2, MEReal.mul(MEReal.abs(x2), new MEReal(1.0E-10d)));
        }
        while (!MEContext.shouldStop()) {
            MEReal y1 = function.evaluateAt(x1);
            MEReal y2 = function.evaluateAt(x2);
            if (MEReal.sgn(y1) != MEReal.sgn(y2)) {
                MEReal x3 = MEReal.div(MEReal.add(x1, x2), two);
                if (!MEReal.isEqual(x3, x1) && !MEReal.isEqual(x3, x2)) {
                    MEReal y3 = function.evaluateAt(x3);
                    MEReal sign = new MEReal((double) MEReal.sgn(MEReal.sub(y1, y2)));
                    MEReal x4 = MEReal.add(x3, MEReal.div(MEReal.mul(MEReal.mul(MEReal.sub(x3, x1), sign), y3), MEReal.sqrt(MEReal.sub(MEReal.sqr(y3), MEReal.mul(y1, y2)))));
                    MEReal y4 = function.evaluateAt(x4);
                    if (!y4.isZero() && !MEReal.isLess(MEReal.sub(x4, x3), MEContext.getCurrent().getTau())) {
                        if (MEReal.sgn(y4) == MEReal.sgn(y3)) {
                            if (MEReal.sgn(y4) == MEReal.sgn(y1)) {
                                if (MEReal.sgn(y4) == MEReal.sgn(y2)) {
                                    break;
                                }
                                x1 = MEReal.max(x3, x4);
                            } else {
                                x2 = MEReal.min(x3, x4);
                            }
                        } else {
                            x1 = MEReal.min(x3, x4);
                            x2 = MEReal.max(x3, x4);
                        }
                    } else if (MEReal.isLess(MEReal.abs(x4), MEContext.getCurrent().getTau())) {
                        return new MEReal(0.0d);
                    } else {
                        return x4;
                    }
                } else {
                    return x3;
                }
            } else {
                break;
            }
        }
        return null;
    }
}
