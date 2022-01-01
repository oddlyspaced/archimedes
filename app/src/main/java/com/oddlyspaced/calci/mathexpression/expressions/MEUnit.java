package com.sparkappdesign.archimedes.mathexpression.expressions;

import com.sparkappdesign.archimedes.mathexpression.numbers.MEReal;
import java.util.HashMap;
/* loaded from: classes.dex */
public class MEUnit extends MEExpression {
    private static HashMap<MEExpression, MEUnit> baseUnits;
    private static MEUnit degrees;
    private static MEUnit radians;
    private MEUnit mBaseUnit;
    private MEExpression mBaseUnitExpressedInUnit;
    private MEQuantity mQuantity;
    private String mSymbol;
    private MEExpression mUnitExpressedInBaseUnit;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class METhisPlaceholder extends MEExpression {
        static METhisPlaceholder placeholder;

        METhisPlaceholder() {
        }

        static METhisPlaceholder placeholder() {
            if (placeholder == null) {
                placeholder = new METhisPlaceholder();
            }
            return placeholder;
        }

        @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
        public boolean equals(Object other) {
            return this == other || (other instanceof METhisPlaceholder);
        }

        @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
        public int hashCode() {
            return 0;
        }

        @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
        public String toString() {
            return "[]";
        }
    }

    public String getSymbol() {
        return this.mSymbol;
    }

    public MEQuantity getQuantity() {
        return this.mQuantity;
    }

    public MEUnit getBaseUnit() {
        return this.mBaseUnit;
    }

    public MEExpression getUnitExpressedInBaseUnit() {
        return this.mUnitExpressedInBaseUnit;
    }

    public MEExpression getBaseUnitExpressedInUnit() {
        return this.mBaseUnitExpressedInUnit;
    }

    public static HashMap<MEExpression, MEUnit> getBaseUnits() {
        if (baseUnits == null) {
            baseUnits = new HashMap<>();
            baseUnits.put(MEQuantity.angle(), radians());
        }
        return baseUnits;
    }

    private MEUnit() {
    }

    public MEUnit(String symbol, MEQuantity quantity) {
        this.mSymbol = symbol;
        this.mQuantity = quantity;
        this.mBaseUnit = this;
        this.mBaseUnitExpressedInUnit = this;
        this.mUnitExpressedInBaseUnit = this;
    }

    public MEUnit(String symbol, MEUnit baseUnit, MEExpression unitExpressedInBaseUnit, MEExpression baseUnitExpressedInUnit) {
        this.mSymbol = symbol;
        this.mQuantity = baseUnit.mQuantity;
        this.mBaseUnit = baseUnit;
        this.mUnitExpressedInBaseUnit = unitExpressedInBaseUnit;
        this.mBaseUnitExpressedInUnit = baseUnitExpressedInUnit.substituteExpression(METhisPlaceholder.placeholder(), this);
    }

    public static MEUnit radians() {
        if (radians == null) {
            radians = new MEUnit("rad", MEQuantity.angle());
        }
        return radians;
    }

    public static MEUnit degrees() {
        if (degrees == null) {
            degrees = new MEUnit("°", radians(), new MEMultiplications(MEConstant.pi(), new MEValue(1, 180), radians()), new MEMultiplications(new MEValue(180L), MEConstant.pi().invert(), METhisPlaceholder.placeholder()));
        }
        return degrees;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEExpression partWithUnit() {
        return this;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEExpression quantityOfUnit() {
        return this.mQuantity;
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public MEReal evaluateNumerically(MEReal value, MEVariable variable) {
        return convertToUnit(null, false).evaluateNumerically(value, variable);
    }

    @Override // com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression
    public double evaluateNumerically(double value, MEVariable variable) {
        return convertToUnit(null, false).evaluateNumerically(value, variable);
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
        return this.mSymbol;
    }
}
