package com.sparkappdesign.archimedes.mathexpression.solvers;

import com.sparkappdesign.archimedes.mathexpression.context.MEContext;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEAdditions;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEEquals;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEMultiplications;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEPower;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEValue;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEVariable;
import com.sparkappdesign.archimedes.mathexpression.numbers.MEInteger;
import com.sparkappdesign.archimedes.mathexpression.numbers.MERational;
import com.sparkappdesign.archimedes.mathexpression.numbers.MEReal;
import com.sparkappdesign.archimedes.utilities.observables.ImmutableList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes.dex */
public class MEPolynomial {
    private ImmutableList<MEExpression> mCoefficients;

    public ImmutableList<MEExpression> getCoefficients() {
        return this.mCoefficients;
    }

    public MEExpression getFirstCoefficient() {
        if (!this.mCoefficients.isEmpty()) {
            return this.mCoefficients.get(0);
        }
        return null;
    }

    public MEExpression getLastCoefficient() {
        if (!this.mCoefficients.isEmpty()) {
            return this.mCoefficients.get(this.mCoefficients.size() - 1);
        }
        return null;
    }

    private MEPolynomial() {
    }

    public static MEPolynomial polynomialWithCoefficients(ArrayList<MEExpression> coefficients) {
        MEPolynomial polynomial = new MEPolynomial();
        ArrayList<MEExpression> coefficients2 = sanitizeCoefficients(coefficients);
        if (MEContext.shouldStop(coefficients2)) {
            return null;
        }
        polynomial.mCoefficients = new ImmutableList<>(coefficients2);
        return polynomial;
    }

    public static MEPolynomial polynomialWithCoefficientsForDegrees(HashMap<Long, MEExpression> coefficients) {
        ArrayList<MEExpression> coefficientsArray = new ArrayList<>();
        long maxDegree = 0;
        for (Long degree : coefficients.keySet()) {
            maxDegree = Math.max(maxDegree, degree.longValue());
        }
        long count = maxDegree + 1;
        for (int i = 0; ((long) i) < count; i++) {
            long degree2 = maxDegree - ((long) i);
            coefficientsArray.add(coefficients.get(Long.valueOf(degree2)) != null ? coefficients.get(Long.valueOf(degree2)) : MEValue.zero());
        }
        return polynomialWithCoefficients(coefficientsArray);
    }

    private static ArrayList<MEExpression> sanitizeCoefficients(ArrayList<MEExpression> coefficients) {
        ArrayList<MEExpression> newCoefficients = new ArrayList<>();
        if (coefficients == null) {
            return newCoefficients;
        }
        Iterator<MEExpression> it = coefficients.iterator();
        while (it.hasNext()) {
            MEExpression coefficient = it.next().canonicalize();
            if (MEContext.shouldStop(coefficient)) {
                return null;
            }
            if (newCoefficients.size() != 0 || !coefficient.isZero()) {
                newCoefficients.add(coefficient);
            }
        }
        return newCoefficients;
    }

    public MEPolynomial polynomialWithNonZeroLastCoefficient() {
        ArrayList<MEExpression> newCoefficients = new ArrayList<>(this.mCoefficients);
        while (newCoefficients.get(newCoefficients.size() - 1).isZero()) {
            newCoefficients.remove(newCoefficients.size() - 1);
        }
        return polynomialWithCoefficients(newCoefficients);
    }

    public static MEPolynomial parsePolynomial(MEVariable variable, MEExpression expression) {
        return parsePolynomial(variable, expression, false, null);
    }

    public static MEPolynomial parsePolynomial(MEVariable variable, MEExpression expression, boolean simplifyExponents, AtomicReference<MERational> multiplier) {
        HashMap<MERational, MEExpression> coefficientsForExponents;
        if (expression instanceof MEEquals) {
            MEEquals equation = (MEEquals) expression;
            expression = new MEAdditions(equation.getLeftOperand(), equation.getRightOperand().negate()).canonicalize();
            if (MEContext.shouldStop()) {
                return null;
            }
        }
        if (expression.containsExpressionOfType(MEEquals.class)) {
            return null;
        }
        MEExpression expression2 = expression.canonicalize();
        if (!MEContext.shouldStop(expression2) && (coefficientsForExponents = polynomialCoefficientsForVariableInExpression(variable, expression2)) != null) {
            HashMap<Long, MEExpression> coefficientsForDegrees = new HashMap<>();
            if (simplifyExponents) {
                multiplier.set(MERational.multiplierToMakeSmallestPossibleIntegersFromRationals(coefficientsForExponents.keySet()));
                boolean allExponentsAreNegative = true;
                for (MERational exponent : coefficientsForExponents.keySet()) {
                    if (exponent.isPositive()) {
                        allExponentsAreNegative = false;
                    }
                }
                if (allExponentsAreNegative) {
                    multiplier.set(MERational.neg(multiplier.get()));
                }
                for (MERational exponent2 : coefficientsForExponents.keySet()) {
                    long degree = MERational.mul(exponent2, multiplier.get()).numerator().toLong();
                    if (degree < 0) {
                        return null;
                    }
                    coefficientsForDegrees.put(Long.valueOf(degree), coefficientsForExponents.get(exponent2));
                }
            } else {
                for (MERational exponent3 : coefficientsForExponents.keySet()) {
                    if (exponent3.isFractional() || exponent3.isNegative()) {
                        return null;
                    }
                    coefficientsForDegrees.put(Long.valueOf(exponent3.numerator().toLong()), coefficientsForExponents.get(exponent3));
                }
            }
            return polynomialWithCoefficientsForDegrees(coefficientsForDegrees);
        }
        return null;
    }

    private static HashMap<MERational, MEExpression> polynomialCoefficientsForVariableInExpression(MEVariable variable, MEExpression expression) {
        HashMap<MERational, MEExpression> coefficients = new HashMap<>();
        if (expression instanceof MEAdditions) {
            Iterator<MEExpression> it = ((MEAdditions) expression).getOperands().iterator();
            while (it.hasNext()) {
                HashMap<MERational, MEExpression> termsInOperand = polynomialCoefficientsForVariableInExpression(variable, it.next());
                if (MEContext.shouldStop(termsInOperand)) {
                    return null;
                }
                addPolynomialTermsToCoefficientsDictionary(termsInOperand, coefficients);
            }
            return coefficients;
        }
        MERational exponent = null;
        MEExpression variablePart = partOfExpressionWithVariable(expression, variable);
        if (variablePart != null) {
            variablePart = variablePart.canonicalize();
        }
        if (MEContext.shouldStop()) {
            return null;
        }
        if (variablePart == null) {
            exponent = new MERational(0);
        } else if (variablePart instanceof MEVariable) {
            exponent = new MERational(1);
        } else if (variablePart instanceof MEPower) {
            MEPower power = (MEPower) variablePart;
            if ((power.getBase() instanceof MEVariable) && (power.getExponent() instanceof MEValue) && ((MEValue) power.getExponent()).getRational() != null) {
                exponent = ((MEValue) power.getExponent()).getRational();
            }
        }
        if (exponent == null) {
            return null;
        }
        MEExpression coefficient = partOfExpressionWithoutVariable(expression, variable);
        if (coefficient != null) {
            coefficient = coefficient.canonicalize();
        }
        if (MEContext.shouldStop()) {
            return null;
        }
        if (coefficient == null) {
            coefficient = MEValue.one();
        }
        HashMap<MERational, MEExpression> polynomialTerms = new HashMap<>();
        polynomialTerms.put(exponent, coefficient);
        addPolynomialTermsToCoefficientsDictionary(polynomialTerms, coefficients);
        return coefficients;
    }

    private static void addPolynomialTermsToCoefficientsDictionary(HashMap<MERational, MEExpression> terms, HashMap<MERational, MEExpression> coefficients) {
        MEExpression coefficient;
        for (MERational exponent : terms.keySet()) {
            MEExpression coefficient2 = coefficients.get(exponent);
            MEExpression termCoefficient = terms.get(exponent);
            if (coefficient2 != null) {
                coefficient = new MEAdditions(coefficient2, termCoefficient);
            } else {
                coefficient = termCoefficient;
            }
            coefficients.put(exponent, coefficient);
        }
    }

    private static MEExpression partOfExpressionWithVariable(MEExpression expression, MEVariable variable) {
        if (!expression.containsVariable(variable)) {
            return null;
        }
        if (expression instanceof MEVariable) {
            if (expression.equals(variable)) {
            }
            return expression;
        } else if (!(expression instanceof MEMultiplications)) {
            return expression;
        } else {
            ArrayList<MEExpression> newOperands = new ArrayList<>();
            Iterator<MEExpression> it = ((MEMultiplications) expression).getOperands().iterator();
            while (it.hasNext()) {
                MEExpression operandPart = partOfExpressionWithVariable(it.next(), variable);
                if (operandPart != null) {
                    newOperands.add(operandPart);
                }
            }
            if (newOperands.size() > 0) {
                return new MEMultiplications(newOperands);
            }
            return expression;
        }
    }

    private static MEExpression partOfExpressionWithoutVariable(MEExpression expression, MEVariable variable) {
        if (!expression.containsVariable(variable)) {
            return expression;
        }
        if (!(expression instanceof MEMultiplications)) {
            return null;
        }
        ArrayList<MEExpression> newOperands = new ArrayList<>();
        Iterator<MEExpression> it = ((MEMultiplications) expression).getOperands().iterator();
        while (it.hasNext()) {
            MEExpression operandPart = partOfExpressionWithoutVariable(it.next(), variable);
            if (operandPart != null) {
                newOperands.add(operandPart);
            }
        }
        return new MEMultiplications(newOperands);
    }

    public int degree() {
        if (this.mCoefficients.size() >= 1) {
            return this.mCoefficients.size() - 1;
        }
        return -1;
    }

    public int degreeOfCoefficientAtIndex(int index) {
        return degree() - index;
    }

    public int numberOfSignChanges() {
        int count = 0;
        int lastNonZeroSign = getFirstCoefficient().sign();
        for (int i = 1; i < this.mCoefficients.size(); i++) {
            int sign = this.mCoefficients.get(i).sign();
            if (!(sign == 0 || sign == lastNonZeroSign)) {
                count++;
                lastNonZeroSign = sign;
            }
        }
        return count;
    }

    public boolean isZero() {
        return this.mCoefficients.size() == 0;
    }

    public boolean isMonic() {
        return getFirstCoefficient().equals(MEValue.one());
    }

    public MEExpression evaluateForValue(MEExpression value) {
        ArrayList<MEExpression> terms = new ArrayList<>();
        int count = this.mCoefficients.size();
        for (int i = 0; i < count; i++) {
            terms.add(new MEMultiplications(this.mCoefficients.get(i), MEPower.powerWithBaseAndExponent(value, new MEValue((long) degreeOfCoefficientAtIndex(i)))));
        }
        return new MEAdditions(terms).canonicalize();
    }

    public MEPolynomial makeMonic() {
        if (isMonic() || isZero()) {
            return this;
        }
        MEExpression factor = getFirstCoefficient().invert().canonicalize();
        ArrayList<MEExpression> newCoefficients = new ArrayList<>();
        newCoefficients.add(MEValue.one());
        for (int i = 1; i < this.mCoefficients.size(); i++) {
            newCoefficients.add(new MEMultiplications(factor, this.mCoefficients.get(i)));
        }
        return polynomialWithCoefficients(newCoefficients);
    }

    public ArrayList<MEPolynomial> squarefreeFactorization() {
        ArrayList<MEPolynomial> a = new ArrayList<>();
        ArrayList<MEPolynomial> b = new ArrayList<>();
        ArrayList<MEPolynomial> c = new ArrayList<>();
        ArrayList<MEPolynomial> d = new ArrayList<>();
        b.add(null);
        c.add(null);
        d.add(null);
        MEPolynomial fprime = differentiate();
        MEPolynomial gcd = greatestCommonDivisorWith(fprime);
        if (MEContext.shouldStop(gcd)) {
            return null;
        }
        a.add(gcd);
        b.add(divideByWithRemainder(a.get(0), null));
        c.add(fprime.divideByWithRemainder(a.get(0), null));
        d.add(c.get(1).subtractBy(b.get(1).differentiate()));
        int i = 1;
        while (b.get(i).getCoefficients().size() > 1) {
            a.add(b.get(i).greatestCommonDivisorWith(d.get(i)));
            b.add(b.get(i).divideByWithRemainder(a.get(i), null));
            c.add(d.get(i).divideByWithRemainder(a.get(i), null));
            i++;
            d.add(c.get(i).subtractBy(b.get(i).differentiate()));
        }
        a.set(1, a.get(1).multiplyBy(b.get(i)));
        a.remove(0);
        return a;
    }

    public MEPolynomial greatestCommonDivisorWith(MEPolynomial other) {
        if (MEContext.shouldStop(other)) {
            return null;
        }
        ArrayList<MEPolynomial> r = new ArrayList<>();
        r.add(this);
        r.add(other);
        int i = 1;
        while (!r.get(i).isZero()) {
            AtomicReference<MEPolynomial> remainder = new AtomicReference<>();
            r.get(i - 1).divideByWithRemainder(r.get(i), remainder);
            if (MEContext.shouldStop(remainder.get())) {
                return null;
            }
            r.add(remainder.get());
            i++;
        }
        return r.get(i - 1).makeMonic();
    }

    public MEPolynomial addWith(MEPolynomial other) {
        MEExpression coefficient;
        MEExpression otherCoefficient;
        if (MEContext.shouldStop(other)) {
            return null;
        }
        ArrayList<MEExpression> newCoefficients = new ArrayList<>();
        int count = this.mCoefficients.size();
        int otherCount = other.mCoefficients.size();
        int maxCount = Math.max(count, otherCount);
        for (int i = 0; i < maxCount; i++) {
            int j = maxCount - i;
            if (count >= j) {
                coefficient = this.mCoefficients.get(count - j);
            } else {
                coefficient = null;
            }
            if (otherCount >= j) {
                otherCoefficient = other.mCoefficients.get(otherCount - j);
            } else {
                otherCoefficient = null;
            }
            newCoefficients.add((coefficient == null || otherCoefficient == null) ? coefficient != null ? coefficient : otherCoefficient : new MEAdditions(coefficient, otherCoefficient));
        }
        return polynomialWithCoefficients(newCoefficients);
    }

    public MEPolynomial subtractBy(MEPolynomial other) {
        return addWith(other.negate());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public MEPolynomial multiplyBy(MEPolynomial other) {
        if (MEContext.shouldStop(other)) {
            return null;
        }
        HashMap<Long, MEExpression> newCoefficients = new HashMap<>();
        int count = this.mCoefficients.size();
        int otherCount = other.mCoefficients.size();
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < otherCount; j++) {
                MEExpression coefficient = new MEMultiplications(this.mCoefficients.get(i), other.mCoefficients.get(j));
                long degree = (long) (degreeOfCoefficientAtIndex(i) + other.degreeOfCoefficientAtIndex(j));
                MEExpression existingCoefficient = newCoefficients.get(Long.valueOf(degree));
                if (existingCoefficient != null) {
                    coefficient = new MEAdditions(existingCoefficient, coefficient);
                }
                newCoefficients.put(Long.valueOf(degree), coefficient);
            }
        }
        return polynomialWithCoefficientsForDegrees(newCoefficients);
    }

    public MEPolynomial divideByWithRemainder(MEPolynomial other, AtomicReference<MEPolynomial> remainderOut) {
        if (!MEContext.shouldStop(other) && !other.isZero()) {
            MEPolynomial quotient = polynomialWithCoefficients(null);
            MEPolynomial remainder = this;
            while (!remainder.isZero() && remainder.degree() >= other.degree()) {
                MEExpression mEMultiplications = new MEMultiplications(remainder.getFirstCoefficient(), other.getFirstCoefficient().invert());
                HashMap<Long, MEExpression> coefficientsForDegrees = new HashMap<>();
                coefficientsForDegrees.put(Long.valueOf((long) (remainder.degree() - other.degree())), mEMultiplications);
                MEPolynomial term = polynomialWithCoefficientsForDegrees(coefficientsForDegrees);
                quotient = quotient.addWith(term);
                remainder = remainder.subtractBy(term.multiplyBy(other)).roundWithAccuracy(MEContext.getCurrent().getTau());
                if (!MEContext.shouldStop(quotient) || MEContext.shouldStop(remainder)) {
                    return null;
                }
                while (!remainder.isZero()) {
                    MEExpression mEMultiplications2 = new MEMultiplications(remainder.getFirstCoefficient(), other.getFirstCoefficient().invert());
                    HashMap<Long, MEExpression> coefficientsForDegrees2 = new HashMap<>();
                    coefficientsForDegrees2.put(Long.valueOf((long) (remainder.degree() - other.degree())), mEMultiplications2);
                    MEPolynomial term2 = polynomialWithCoefficientsForDegrees(coefficientsForDegrees2);
                    quotient = quotient.addWith(term2);
                    remainder = remainder.subtractBy(term2.multiplyBy(other)).roundWithAccuracy(MEContext.getCurrent().getTau());
                    if (!MEContext.shouldStop(quotient)) {
                    }
                    return null;
                }
            }
            if (remainderOut == null) {
                return quotient;
            }
            remainderOut.set(remainder);
            return quotient;
        }
        return null;
    }

    private MEPolynomial roundWithAccuracy(MEReal accuracy) {
        MEValue absMax = MEValue.negativeInfinity();
        Iterator<MEExpression> it = this.mCoefficients.iterator();
        while (it.hasNext()) {
            MEExpression coefficient = it.next();
            if (coefficient instanceof MEValue) {
                absMax = MEValue.maxOf(absMax, ((MEValue) coefficient).absolute());
            }
        }
        ArrayList<MEExpression> newCoefficients = new ArrayList<>();
        Iterator<MEExpression> it2 = this.mCoefficients.iterator();
        while (it2.hasNext()) {
            MEExpression coefficient2 = it2.next();
            if (!(coefficient2 instanceof MEValue) || !MEReal.isLess(((MEValue) coefficient2).absolute().divideBy(absMax).getReal(), accuracy)) {
                newCoefficients.add(coefficient2);
            } else {
                newCoefficients.add(MEValue.zero());
            }
        }
        return polynomialWithCoefficients(newCoefficients);
    }

    public MEPolynomial negate() {
        ArrayList<MEExpression> newCoefficients = new ArrayList<>();
        Iterator<MEExpression> it = this.mCoefficients.iterator();
        while (it.hasNext()) {
            newCoefficients.add(it.next().negate());
        }
        return polynomialWithCoefficients(newCoefficients);
    }

    public MEPolynomial transformInputByNegation() {
        ArrayList<MEExpression> newCoefficients = new ArrayList<>();
        int count = this.mCoefficients.size();
        for (int i = 0; i < count; i++) {
            MEExpression coefficient = this.mCoefficients.get(i);
            if (degreeOfCoefficientAtIndex(i) % 2 == 1) {
                coefficient = coefficient.negate();
            }
            newCoefficients.add(coefficient);
        }
        return polynomialWithCoefficients(newCoefficients);
    }

    public MEPolynomial transformInputByAdditionOfExpression(MEExpression expression) {
        ArrayList<MEExpression> newCoefficients = new ArrayList<>();
        int count = this.mCoefficients.size();
        for (int i = 0; i < count; i++) {
            ArrayList<MEExpression> terms = new ArrayList<>();
            for (int j = 0; j <= i; j++) {
                int degreeOfK = i - j;
                terms.add(new MEMultiplications(new MEValue(MEInteger.binomial((long) degreeOfCoefficientAtIndex(j), (long) degreeOfK)), this.mCoefficients.get(j), MEPower.powerWithBaseAndExponent(expression, new MEValue((long) degreeOfK))));
            }
            newCoefficients.add(new MEAdditions(terms));
        }
        return polynomialWithCoefficients(newCoefficients);
    }

    public MEPolynomial transformInputByMultiplicationWithExpression(MEExpression expression) {
        ArrayList<MEExpression> newCoefficients = new ArrayList<>();
        int count = this.mCoefficients.size();
        for (int i = 0; i < count; i++) {
            MEExpression coefficient = this.mCoefficients.get(i);
            int degree = degreeOfCoefficientAtIndex(i);
            if (degree > 0) {
                coefficient = new MEMultiplications(coefficient, MEPower.powerWithBaseAndExponent(expression, new MEValue((long) degree)));
            }
            newCoefficients.add(coefficient);
        }
        return polynomialWithCoefficients(newCoefficients);
    }

    public MEPolynomial differentiate() {
        ArrayList<MEExpression> newCoefficients = new ArrayList<>();
        for (int i = 0; i < this.mCoefficients.size() - 1; i++) {
            newCoefficients.add(new MEMultiplications(new MEValue((long) ((this.mCoefficients.size() - i) - 1)), this.mCoefficients.get(i)));
        }
        return polynomialWithCoefficients(newCoefficients);
    }

    public MEExpression expressionWithVariable(MEVariable variable) {
        MEExpression term;
        ArrayList<MEExpression> terms = new ArrayList<>();
        for (int i = 0; i < this.mCoefficients.size(); i++) {
            MEExpression coefficient = this.mCoefficients.get(i);
            if (!coefficient.isZero()) {
                int degree = degreeOfCoefficientAtIndex(i);
                if (degree >= 1) {
                    term = new MEMultiplications(coefficient, MEPower.powerWithBaseAndExponent(variable, new MEValue((long) degree)));
                } else {
                    term = coefficient;
                }
                terms.add(term);
            }
        }
        return new MEAdditions(terms);
    }

    public String toString() {
        String result = "";
        Iterator<MEExpression> it = this.mCoefficients.iterator();
        while (it.hasNext()) {
            result = result + it.next().toString() + " ";
        }
        return result;
    }
}
