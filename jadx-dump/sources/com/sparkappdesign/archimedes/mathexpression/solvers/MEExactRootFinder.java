package com.sparkappdesign.archimedes.mathexpression.solvers;

import com.sparkappdesign.archimedes.mathexpression.context.MEContext;
import com.sparkappdesign.archimedes.mathexpression.context.MEIssue;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEAdditions;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEMultiplications;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEPower;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEValue;
import java.util.HashSet;
/* loaded from: classes.dex */
public class MEExactRootFinder {
    private MEExactRootFinder() {
    }

    public static HashSet<MEExpression> findRootsForPolynomial(MEPolynomial polynomial) {
        HashSet<MEExpression> quadraticSolutions;
        HashSet<MEExpression> linearSolutions;
        HashSet<MEExpression> solutions = new HashSet<>();
        if (polynomial.getLastCoefficient() != null && polynomial.getLastCoefficient().isZero()) {
            solutions.add(MEValue.zero());
            polynomial = polynomial.polynomialWithNonZeroLastCoefficient();
        }
        if (polynomial.degree() == 1 && (linearSolutions = solveLinearPolynomial(polynomial)) != null) {
            solutions.addAll(linearSolutions);
        }
        if (polynomial.degree() == 2 && (quadraticSolutions = solveQuadraticPolynomial(polynomial)) != null) {
            solutions.addAll(quadraticSolutions);
        }
        if (polynomial.degree() > 2) {
            MEContext.getCurrent().stopWithError(MEIssue.POLYNOMIAL_ORDER_TOO_HIGH);
        }
        return solutions;
    }

    private static HashSet<MEExpression> solveLinearPolynomial(MEPolynomial polynomial) {
        MEExpression x;
        if (polynomial.degree() != 1 || (x = new MEMultiplications(polynomial.getCoefficients().get(1).negate(), polynomial.getCoefficients().get(0).invert()).canonicalize()) == null) {
            return null;
        }
        HashSet<MEExpression> result = new HashSet<>();
        result.add(x);
        return result;
    }

    private static HashSet<MEExpression> solveQuadraticPolynomial(MEPolynomial polynomial) {
        if (polynomial.degree() != 2) {
            return null;
        }
        MEExpression a = polynomial.getCoefficients().get(0);
        MEExpression b = polynomial.getCoefficients().get(1);
        MEExpression two = new MEValue(2L);
        MEExpression D = new MEAdditions(MEPower.powerWithBaseAndExponent(b, two), new MEMultiplications(new MEValue(-4L), a, polynomial.getCoefficients().get(2))).canonicalize();
        if (MEContext.shouldStop(D)) {
            return null;
        }
        MEExpression sqrtD = MEPower.powerForSquareRootWithBase(D);
        MEExpression twoA = new MEMultiplications(two, a);
        MEExpression dividend1 = new MEAdditions(b.negate(), sqrtD);
        MEExpression dividend2 = new MEAdditions(b.negate(), sqrtD.negate());
        MEExpression x1 = new MEMultiplications(dividend1, twoA.invert());
        MEExpression x2 = new MEMultiplications(dividend2, twoA.invert());
        MEExpression x12 = x1.canonicalize();
        if (MEContext.shouldStop()) {
            return null;
        }
        MEExpression x22 = x2.canonicalize();
        if (MEContext.shouldStop()) {
            return null;
        }
        HashSet<MEExpression> solutions = new HashSet<>();
        if (x12 != null) {
            solutions.add(x12);
        }
        if (x22 == null) {
            return solutions;
        }
        solutions.add(x22);
        return solutions;
    }
}
