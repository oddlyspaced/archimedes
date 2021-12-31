package com.sparkappdesign.archimedes.mathexpression.expressions;

import com.sparkappdesign.archimedes.mathexpression.numbers.MEReal;
import com.sparkappdesign.archimedes.utilities.GeneralUtil;
/* loaded from: classes.dex */
public class MEVariable extends MEExpression {
    private Object mIdentifier;

    public Object getIdentifier() {
        return this.mIdentifier;
    }

    private MEVariable() {
    }

    public MEVariable(Object identifier) {
        this.mIdentifier = identifier;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEReal evaluateNumerically(MEReal value, MEVariable variable) {
        if (equals(variable)) {
            return value;
        }
        return null;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public double evaluateNumerically(double value, MEVariable variable) {
        if (equals(variable)) {
            return value;
        }
        return Double.NaN;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof MEVariable) {
            return GeneralUtil.equalOrBothNull(this.mIdentifier, ((MEVariable) other).mIdentifier);
        }
        return false;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public int hashCode() {
        return GeneralUtil.hashCode(this.mIdentifier);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public String toString() {
        return this.mIdentifier.toString();
    }
}
