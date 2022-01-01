package com.oddlyspaced.calci.mathexpression.expressions;
/* loaded from: classes.dex */
public class MEQuantity extends MEExpression {
    private static MEQuantity angle;
    private String mName;
    private String mSymbol;

    public String getName() {
        return this.mName;
    }

    public String getSymbol() {
        return this.mSymbol;
    }

    private MEQuantity() {
    }

    private MEQuantity(String name, String symbol) {
        this.mName = name;
        this.mSymbol = symbol;
    }

    public static MEQuantity angle() {
        if (angle == null) {
            angle = new MEQuantity("angle", "θ");
        }
        return angle;
    }

    @Override // com.oddlyspaced.calci.mathexpression.expressions.MEExpression
    public boolean equals(Object other) {
        return this == other;
    }

    @Override // com.oddlyspaced.calci.mathexpression.expressions.MEExpression
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override // com.oddlyspaced.calci.mathexpression.expressions.MEExpression
    public String toString() {
        return "[" + this.mSymbol + "]";
    }
}
