package com.oddlyspaced.calci.mathexpression.expressions;

import com.oddlyspaced.calci.mathexpression.context.MEContext;
import com.oddlyspaced.calci.mathexpression.context.MEIssue;
import com.oddlyspaced.calci.mathexpression.numbers.MEReal;
import com.oddlyspaced.calci.utilities.Transformation;
import com.oddlyspaced.calci.utilities.observables.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
/* loaded from: classes.dex */
public abstract class MEExpression {
    public abstract boolean equals(Object obj);

    public abstract int hashCode();

    public abstract String toString();

    public ImmutableList<MEExpression> children() {
        return null;
    }

    public MEExpression copyWithChildren(Collection<? extends MEExpression> children) {
        return this;
    }

    public MEExpression expressionWithTreeTransformation(Transformation<MEExpression> transformation) {
        MEExpression result = transformation.transform(this);
        Collection<MEExpression> children = children();
        if (children == null || children.size() <= 0) {
            return result;
        }
        ArrayList<MEExpression> newChildren = new ArrayList<>();
        for (MEExpression child : children) {
            MEExpression newChild = child.expressionWithTreeTransformation(transformation);
            if (newChild != null) {
                newChildren.add(newChild);
            }
        }
        if (newChildren.size() == children.size()) {
            return result.copyWithChildren(newChildren);
        }
        return null;
    }

    public HashSet<MEVariable> variables() {
        HashSet<MEVariable> variables = new HashSet<>();
        addVariables(this, variables);
        return variables;
    }

    private void addVariables(MEExpression expression, HashSet<MEVariable> variables) {
        if (expression != null) {
            if (expression instanceof MEVariable) {
                variables.add((MEVariable) expression);
            }
            ImmutableList<MEExpression> children = expression.children();
            if (children != null) {
                Iterator<MEExpression> it = children.iterator();
                while (it.hasNext()) {
                    addVariables(it.next(), variables);
                }
            }
        }
    }

    public boolean containsVariable(MEVariable variable) {
        return variables().contains(variable);
    }

    public boolean containsExpressionOfType(Class type) {
        ArrayList<Object> typeInstances = new ArrayList<>();
        addDescendantsOfType(this, type, typeInstances);
        return typeInstances.size() != 0;
    }

    private void addDescendantsOfType(MEExpression expression, Class type, ArrayList<Object> typeInstances) {
        if (expression != null) {
            if (type.isInstance(expression)) {
                typeInstances.add(expression);
            }
            ImmutableList<MEExpression> children = expression.children();
            if (children != null) {
                Iterator<MEExpression> it = children.iterator();
                while (it.hasNext()) {
                    addDescendantsOfType(it.next(), type, typeInstances);
                }
            }
        }
    }

    public MEExpression substituteExpression(final MEExpression pattern, final MEExpression substitute) {
        return expressionWithTreeTransformation(new Transformation<MEExpression>() { // from class: com.oddlyspaced.calci.mathexpression.expressions.MEExpression.1
            public MEExpression transform(MEExpression expression) {
                return expression.equals(pattern) ? substitute : expression;
            }
        });
    }

    public MEValue coefficient() {
        return MEValue.one();
    }

    public MEExpression partWithoutCoefficient() {
        return this;
    }

    public MEExpression partWithUnit() {
        if (containsExpressionOfType(MEUnit.class)) {
            return this;
        }
        return null;
    }

    public MEExpression quantityOfUnit() {
        MEExpression unit = partWithUnit();
        if (unit != null) {
            return unit.expressionWithTreeTransformation(new Transformation<MEExpression>() { // from class: com.oddlyspaced.calci.mathexpression.expressions.MEExpression.2
                public MEExpression transform(MEExpression expression) {
                    return expression instanceof MEUnit ? ((MEUnit) expression).getQuantity() : expression;
                }
            });
        }
        return null;
    }

    public MEExpression convertToUnit(MEExpression targetUnit, boolean includeUnitInResult) {
        MEExpression sourceUnit;
        MEExpression result = this;
        MEExpression sourceQuantity = quantityOfUnit();
        if (sourceQuantity != null) {
            sourceQuantity = sourceQuantity.canonicalize();
        }
        if (shouldStop()) {
            return null;
        }
        if (targetUnit == null) {
            if (sourceQuantity == null) {
                return this;
            }
            targetUnit = MEContext.getCurrent().getDefaultUnits().get(sourceQuantity);
            if (targetUnit == null) {
                stopWithError(MEIssue.INVALID_UNIT);
                return null;
            }
        }
        MEExpression targetQuantity = targetUnit.quantityOfUnit();
        if (targetQuantity != null) {
            targetQuantity = targetQuantity.canonicalize();
        }
        if (shouldStop(targetQuantity)) {
            return null;
        }
        boolean isSourceQuantityImplicit = false;
        if (sourceQuantity == null) {
            sourceQuantity = targetQuantity;
            isSourceQuantityImplicit = true;
        }
        if (sourceQuantity.equals(targetQuantity) && (sourceUnit = sourceQuantity.expressionWithTreeTransformation(new Transformation<MEExpression>() { // from class: com.oddlyspaced.calci.mathexpression.expressions.MEExpression.3
            public MEExpression transform(MEExpression input) {
                MEExpression defaultUnit = MEContext.getCurrent().getDefaultUnits().get(input);
                return defaultUnit != null ? defaultUnit : input;
            }
        })) != null) {
            if (isSourceQuantityImplicit) {
                result = new MEMultiplications(result, sourceUnit);
            }
            if (!(targetUnit instanceof MEUnit)) {
                stopWithError(MEIssue.INVALID_UNIT);
                return null;
            }
            MEExpression result2 = result.convertUnitsToBase().substituteExpression(((MEUnit) targetUnit).getBaseUnit(), ((MEUnit) targetUnit).getBaseUnitExpressedInUnit());
            if (!includeUnitInResult) {
                result2 = new MEMultiplications(result2, targetUnit.invert());
            }
            return result2.canonicalize();
        }
        return null;
    }

    public MEExpression convertUnitsToDefault() {
        MEExpression mEExpression = null;
        if (!containsExpressionOfType(MEUnit.class)) {
            return this;
        }
        if (partWithUnit() != null) {
            return convertToUnit(null, true);
        }
        Collection<MEExpression> children = children();
        if (children == null || children.size() <= 0) {
            return this;
        }
        ArrayList<MEExpression> newChildren = new ArrayList<>();
        for (MEExpression child : children) {
            MEExpression newChild = child.convertUnitsToDefault();
            if (newChild != null) {
                newChildren.add(newChild);
            }
        }
        if (newChildren.size() == children.size()) {
            mEExpression = copyWithChildren(newChildren);
        }
        return mEExpression;
    }

    public MEExpression convertUnitsToBase() {
        return expressionWithTreeTransformation(new Transformation<MEExpression>() { // from class: com.oddlyspaced.calci.mathexpression.expressions.MEExpression.4
            public MEExpression transform(MEExpression input) {
                return input instanceof MEUnit ? ((MEUnit) input).getUnitExpressedInBaseUnit() : input;
            }
        });
    }

    public MEExpression canonicalize() {
        return this;
    }

    public MEReal evaluateNumerically(MEReal value, MEVariable variable) {
        return null;
    }

    public double evaluateNumerically(double value, MEVariable variable) {
        return Double.NaN;
    }

    public boolean isNegative() {
        return false;
    }

    public boolean isZero() {
        return false;
    }

    public MEExpression negate() {
        return new MEMultiplications(MEValue.minusOne(), this);
    }

    public MEExpression invert() {
        return MEPower.powerWithBaseAndExponent(this, MEValue.minusOne());
    }

    public int sign() {
        if (isNegative()) {
            return -1;
        }
        if (isZero()) {
            return 0;
        }
        return 1;
    }

    protected static void stopWithError(String errorName) {
        MEContext.getCurrent().stopWithError(errorName);
    }

    public static boolean shouldStop() {
        return MEContext.shouldStop();
    }

    protected static boolean shouldStop(Object parameter) {
        return MEContext.shouldStop(parameter);
    }
}
