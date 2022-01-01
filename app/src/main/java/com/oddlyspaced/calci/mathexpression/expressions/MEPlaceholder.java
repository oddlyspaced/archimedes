package com.oddlyspaced.calci.mathexpression.expressions;

import com.oddlyspaced.calci.utilities.GeneralUtil;
/* loaded from: classes.dex */
public class MEPlaceholder extends MEExpression {
    private Object mIdentifier;

    public Object getIdentifier() {
        return this.mIdentifier;
    }

    private MEPlaceholder() {
    }

    public MEPlaceholder(Object identifier) {
        this.mIdentifier = identifier;
    }

    @Override // com.oddlyspaced.calci.mathexpression.expressions.MEExpression
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof MEPlaceholder) {
            return GeneralUtil.equalOrBothNull(this.mIdentifier, ((MEPlaceholder) other).mIdentifier);
        }
        return false;
    }

    @Override // com.oddlyspaced.calci.mathexpression.expressions.MEExpression
    public int hashCode() {
        return GeneralUtil.hashCode(this.mIdentifier);
    }

    @Override // com.oddlyspaced.calci.mathexpression.expressions.MEExpression
    public String toString() {
        return this.mIdentifier.toString();
    }
}
