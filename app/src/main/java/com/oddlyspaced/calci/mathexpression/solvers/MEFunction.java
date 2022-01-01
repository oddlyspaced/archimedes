package com.oddlyspaced.calci.mathexpression.solvers;

import com.oddlyspaced.calci.mathexpression.context.MEContext;
import com.oddlyspaced.calci.mathexpression.context.MEIssue;
import com.oddlyspaced.calci.mathexpression.enums.MEExpressionForm;
import com.oddlyspaced.calci.mathexpression.expressions.MEEquals;
import com.oddlyspaced.calci.mathexpression.expressions.MEExpression;
import com.oddlyspaced.calci.mathexpression.expressions.MEQuantity;
import com.oddlyspaced.calci.mathexpression.expressions.MEVariable;
import com.oddlyspaced.calci.mathexpression.numbers.MEReal;
import java.util.HashSet;
/* loaded from: classes.dex */
public class MEFunction {
    private MEExpression mExpression;
    private boolean mIsConstant;
    private boolean mIsLinear;
    private MEVariable mVariable;

    private MEFunction() {
    }

    public static MEFunction functionWithExpression(MEExpression expression) {
        MEFunction function = new MEFunction();
        if (expression.containsExpressionOfType(MEEquals.class)) {
            return null;
        }
        MEExpression expression2 = expression.canonicalize();
        if (!MEContext.shouldStop(expression2) && !expression2.containsExpressionOfType(MEQuantity.class)) {
            function.mExpression = expression2;
            HashSet<MEVariable> variables = expression2.variables();
            if (variables.size() > 1) {
                return null;
            }
            function.mVariable = variables.iterator().next();
            function.classify();
            return function;
        }
        return null;
    }

    private void classify() {
        boolean z;
        boolean z2 = true;
        MEPolynomial polynomial = MEPolynomial.parsePolynomial(this.mVariable, this.mExpression);
        if (polynomial != null) {
            if (polynomial.degree() == 0) {
                z = true;
            } else {
                z = false;
            }
            this.mIsConstant = z;
            if (polynomial.degree() > 1) {
                z2 = false;
            }
            this.mIsLinear = z2;
        }
    }

    public MEReal evaluateAt(MEReal x) {
        if (!MEContext.shouldStop() && MEContext.getCurrent().getForm() == MEExpressionForm.Numeric) {
            MEReal result = this.mExpression.evaluateNumerically(x, this.mVariable);
            if (result != null) {
                return result;
            }
            MEContext.getCurrent().stopWithError(MEIssue.UNDEFINED);
            return null;
        }
        return null;
    }
}
