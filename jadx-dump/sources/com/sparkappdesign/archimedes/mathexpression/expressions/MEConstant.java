package com.sparkappdesign.archimedes.mathexpression.expressions;

import com.sparkappdesign.archimedes.mathexpression.context.MEContext;
import com.sparkappdesign.archimedes.mathexpression.enums.MEExpressionForm;
import com.sparkappdesign.archimedes.mathexpression.numbers.MEReal;
/* loaded from: classes.dex */
public class MEConstant extends MEExpression {
    private static MEConstant e;
    private static MEConstant pi;
    private String mName;
    private MEValue mValue;

    public String getName() {
        return this.mName;
    }

    public MEValue getValue() {
        return this.mValue;
    }

    private MEConstant() {
    }

    private MEConstant(String name, MEValue value) {
        this.mName = name;
        this.mValue = value;
    }

    public static synchronized MEConstant pi() {
        MEConstant mEConstant;
        synchronized (MEConstant.class) {
            if (pi == null) {
                pi = new MEConstant("π", MEValue.pi());
            }
            mEConstant = pi;
        }
        return mEConstant;
    }

    public static synchronized MEConstant e() {
        MEConstant mEConstant;
        synchronized (MEConstant.class) {
            if (e == null) {
                e = new MEConstant("e", MEValue.e());
            }
            mEConstant = e;
        }
        return mEConstant;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEExpression canonicalize() {
        return MEContext.getCurrent().getForm() == MEExpressionForm.Numeric ? this.mValue.canonicalize() : this;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEReal evaluateNumerically(MEReal value, MEVariable variable) {
        return this.mValue.evaluateNumerically(value, variable);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public double evaluateNumerically(double value, MEVariable variable) {
        return this.mValue.evaluateNumerically(value, variable);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public boolean equals(Object other) {
        return this == other;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public String toString() {
        return this.mName;
    }
}
