package com.oddlyspaced.calci.mathexpression.solvers;

import com.oddlyspaced.calci.mathexpression.context.MEContext;
import com.oddlyspaced.calci.mathexpression.context.MEIssue;
import com.oddlyspaced.calci.mathexpression.enums.MEExpressionForm;
import com.oddlyspaced.calci.mathexpression.expressions.MEEquals;
import com.oddlyspaced.calci.mathexpression.expressions.MEExpression;
import com.oddlyspaced.calci.mathexpression.expressions.MEPower;
import com.oddlyspaced.calci.mathexpression.expressions.MEValue;
import com.oddlyspaced.calci.mathexpression.expressions.MEVariable;
import com.oddlyspaced.calci.mathexpression.numbers.MERational;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes.dex */
public class MERootFinder {
    private MERootFinder() {
    }

    public static HashSet<MEExpression> findRootsForExpressionAsPolynomialInVariable(final MEExpression expression, final MEVariable variable) {
        HashSet<MEExpression> solutions;
        if (expression.containsExpressionOfType(MEEquals.class)) {
            return null;
        }
        AtomicReference<MERational> multiplier = new AtomicReference<>();
        MEPolynomial polynomial = MEPolynomial.parsePolynomial(variable, expression, true, multiplier);
        if (polynomial == null) {
            MEContext.getCurrent().stopWithError(MEIssue.CANT_SOLVE_GENERIC);
            return null;
        } else if (polynomial.degree() == 0) {
            MEContext.getCurrent().stopWithError(MEIssue.CONTRADICTION);
            return null;
        } else {
            if (MEContext.getCurrent().getForm() != MEExpressionForm.Numeric || expression.variables().size() > 1 || polynomial.degree() <= 2) {
                solutions = MEExactRootFinder.findRootsForPolynomial(polynomial);
            } else {
                solutions = MENumericRootFinder.findRootsForPolynomial(polynomial);
            }
            if (MEContext.shouldStop()) {
                return null;
            }
            if (MERational.isEqual(multiplier.get(), 1)) {
                return solutions;
            }
            final MEValue exponent = new MEValue(multiplier.get());
            HashSet hashSet = new HashSet();
            Iterator<MEExpression> it = solutions.iterator();
            while (it.hasNext()) {
                MEExpression originalSolution = it.next();
                if (!MEContext.shouldStop()) {
                    final AtomicReference<MEExpression> solution = new AtomicReference<>(originalSolution);
                    MEContext.pushContext(null, new Runnable() { // from class: com.oddlyspaced.calci.mathexpression.solvers.MERootFinder.1
                        @Override // java.lang.Runnable
                        public void run() {
                            solution.set(MEPower.powerWithBaseAndExponent((MEExpression) solution.get(), exponent));
                            solution.set(((MEExpression) solution.get()).canonicalize());
                        }
                    });
                    hashSet.add(solution.get());
                }
            }
            if (exponent.getRational().denominator().isEven()) {
                HashSet<MEExpression> negativeSolutions = new HashSet<>();
                Iterator it2 = hashSet.iterator();
                while (it2.hasNext()) {
                    MEExpression solution2 = (MEExpression) it2.next();
                    if (!MEContext.shouldStop(solution2)) {
                        negativeSolutions.add(solution2.negate().canonicalize());
                    }
                }
                hashSet.addAll(negativeSolutions);
            }
            HashSet<MEExpression> checkedSolutions = new HashSet<>();
            Iterator it3 = hashSet.iterator();
            while (it3.hasNext()) {
                final MEExpression solution3 = (MEExpression) it3.next();
                if (!MEContext.shouldStop(solution3)) {
                    if (solution3.containsExpressionOfType(MEVariable.class)) {
                        checkedSolutions.add(solution3);
                    }
                    final AtomicBoolean isValid = new AtomicBoolean(false);
                    final MEContext context = MEContext.getCurrent().copy();
                    context.setForm(MEExpressionForm.Numeric);
                    MEContext.pushContext(context, new Runnable() { // from class: com.oddlyspaced.calci.mathexpression.solvers.MERootFinder.2
                        @Override // java.lang.Runnable
                        public void run() {
                            // TODO
                            MEExpression result = solution3.substituteExpression(variable, solution3).canonicalize();
                            MEValue value = result instanceof MEValue ? (MEValue) result : null;
                            isValid.set(value != null && value.absolute().isLessThanValue(new MEValue(context.getTau())));
                        }
                    });
                    if (isValid.get()) {
                        checkedSolutions.add(solution3);
                    }
                }
            }
            return checkedSolutions;
        }
    }
}
