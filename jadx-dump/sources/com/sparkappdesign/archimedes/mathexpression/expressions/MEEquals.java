package com.sparkappdesign.archimedes.mathexpression.expressions;

import com.sparkappdesign.archimedes.mathexpression.context.MEContext;
import com.sparkappdesign.archimedes.mathexpression.context.MEIssue;
import com.sparkappdesign.archimedes.mathexpression.enums.MEDivisionSide;
import com.sparkappdesign.archimedes.mathexpression.solvers.MEFunctionInverter;
import com.sparkappdesign.archimedes.mathexpression.solvers.MERootFinder;
import com.sparkappdesign.archimedes.utilities.GeneralUtil;
import com.sparkappdesign.archimedes.utilities.observables.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
/* loaded from: classes.dex */
public class MEEquals extends MEExpression {
    private MEExpression mLeftOperand;
    private MEExpression mRightOperand;

    public MEExpression getLeftOperand() {
        return this.mLeftOperand;
    }

    public MEExpression getRightOperand() {
        return this.mRightOperand;
    }

    private MEEquals() {
    }

    public MEEquals(MEExpression leftOperand, MEExpression rightOperand) {
        this.mLeftOperand = leftOperand;
        this.mRightOperand = rightOperand;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public ImmutableList<MEExpression> children() {
        return new ImmutableList<>(Arrays.asList(this.mLeftOperand, this.mRightOperand));
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEEquals copyWithChildren(Collection<? extends MEExpression> children) {
        if (GeneralUtil.equalOrBothNull(children(), children)) {
            return this;
        }
        MEEquals copy = new MEEquals();
        Iterator<? extends MEExpression> iterator = children.iterator();
        copy.mLeftOperand = (MEExpression) iterator.next();
        copy.mRightOperand = (MEExpression) iterator.next();
        return copy;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEExpression partWithUnit() {
        return null;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEExpression canonicalize() {
        MEExpression left = this.mLeftOperand.canonicalize();
        if (shouldStop(left)) {
            return null;
        }
        MEExpression right = this.mRightOperand.canonicalize();
        if (shouldStop(right)) {
            return null;
        }
        return (this.mLeftOperand == left && this.mRightOperand == right) ? this : new MEEquals(left, right);
    }

    public Set<MEExpression> solveForVariable(MEVariable variable) {
        if (this.mLeftOperand.containsExpressionOfType(MEEquals.class) || this.mRightOperand.containsExpressionOfType(MEEquals.class)) {
            MEContext.getCurrent().stopWithError(MEIssue.NESTED_EQUALS);
            return null;
        }
        MEEquals equation = (MEEquals) canonicalize();
        if (shouldStop(equation)) {
            return null;
        }
        if (variable == null) {
            if (equation.variables().isEmpty() && !equation.mLeftOperand.equals(equation.mRightOperand)) {
                MEContext.getCurrent().stopWithError(MEIssue.CONTRADICTION);
            }
            return new HashSet();
        }
        MEEquals equation2 = equation.multiplyOutDivisions();
        if (shouldStop(equation2)) {
            return null;
        }
        MEEquals equation3 = MEFunctionInverter.invertFunctionsAroundVariableInEquation(variable, equation2);
        if (shouldStop(equation3)) {
            return null;
        }
        Set<MEExpression> roots = MERootFinder.findRootsForExpressionAsPolynomialInVariable(new MEAdditions(equation3.mLeftOperand, equation3.mRightOperand.negate()), variable);
        if (shouldStop(roots)) {
            return null;
        }
        return roots;
    }

    public MEEquals multiplyOutDivisions() {
        Collection<MEExpression> leftTerms = this.mLeftOperand instanceof MEAdditions ? ((MEAdditions) this.mLeftOperand).getOperands() : new ArrayList<>(Arrays.asList(this.mLeftOperand));
        Collection<MEExpression> rightTerms = this.mRightOperand instanceof MEAdditions ? ((MEAdditions) this.mRightOperand).getOperands() : new ArrayList<>(Arrays.asList(this.mRightOperand));
        ArrayList<MEExpression> allTerms = new ArrayList<>();
        allTerms.addAll(leftTerms);
        allTerms.addAll(rightTerms);
        if (allTerms.size() > 100) {
            stopWithError(MEIssue.CANT_SOLVE_GENERIC);
            return null;
        }
        Iterator<MEExpression> it = allTerms.iterator();
        while (it.hasNext()) {
            MEExpression term = it.next();
            if (shouldStop()) {
                return null;
            }
            MEExpression divisor = divisionSideForExpression(MEDivisionSide.Divisor, term);
            if (!divisor.equals(MEValue.one())) {
                MEExpression newLeft = multiplyTermsByExpression(leftTerms, divisor);
                if (shouldStop(newLeft)) {
                    return null;
                }
                MEExpression newRight = multiplyTermsByExpression(rightTerms, divisor);
                if (shouldStop(newRight)) {
                    return null;
                }
                return new MEEquals(newLeft, newRight).multiplyOutDivisions();
            }
        }
        return this;
    }

    private MEExpression multiplyTermsByExpression(Collection<MEExpression> terms, MEExpression expression) {
        ArrayList<MEExpression> newTerms = new ArrayList<>();
        for (MEExpression term : terms) {
            if (shouldStop()) {
                return null;
            }
            MEExpression newTerm = multiplyTermByExpression(term, expression);
            if (newTerm != null) {
                newTerms.add(newTerm);
            }
        }
        if (!shouldStop(newTerms)) {
            return new MEAdditions(newTerms).canonicalize();
        }
        return null;
    }

    private MEExpression multiplyTermByExpression(MEExpression term, MEExpression expression) {
        if (divisionSideForExpression(MEDivisionSide.Divisor, term).equals(expression)) {
            return divisionSideForExpression(MEDivisionSide.Dividend, term);
        }
        return new MEMultiplications(term, expression);
    }

    private MEExpression divisionSideForExpression(MEDivisionSide divisionSide, MEExpression expression) {
        Collection<MEExpression> parts = expression instanceof MEMultiplications ? ((MEMultiplications) expression).getOperands() : new ArrayList<>(Arrays.asList(expression));
        ArrayList<MEExpression> newParts = new ArrayList<>();
        for (MEExpression part : parts) {
            MEExpression newPart = divisionSideForPart(divisionSide, part);
            if (newPart != null) {
                newParts.add(newPart);
            }
        }
        return new MEMultiplications(newParts).canonicalize();
    }

    private MEExpression divisionSideForPart(MEDivisionSide divisionSide, MEExpression part) {
        MEPower power = part instanceof MEPower ? (MEPower) part : null;
        if (power != null && power.getExponent().isNegative()) {
            return divisionSide == MEDivisionSide.Dividend ? MEValue.one() : power.invert();
        }
        if (divisionSide != MEDivisionSide.Dividend) {
            part = MEValue.one();
        }
        return part;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MEEquals)) {
            return false;
        }
        MEEquals otherEquals = (MEEquals) other;
        return GeneralUtil.equalOrBothNull(this.mLeftOperand, otherEquals.mLeftOperand) && GeneralUtil.equalOrBothNull(this.mRightOperand, otherEquals.mRightOperand);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public int hashCode() {
        return GeneralUtil.hashCode(this.mLeftOperand) ^ GeneralUtil.hashCode(this.mRightOperand);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public String toString() {
        return this.mLeftOperand.toString() + " = " + this.mRightOperand.toString();
    }
}
