package com.sparkappdesign.archimedes.mathexpression.solvers;

import com.sparkappdesign.archimedes.mathexpression.context.MEContext;
import com.sparkappdesign.archimedes.mathexpression.enums.MEEquationSide;
import com.sparkappdesign.archimedes.mathexpression.enums.METrigonometricFunctionType;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEAdditions;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEEquals;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression;
import com.sparkappdesign.archimedes.mathexpression.expressions.MELogarithm;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEMultiplications;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEPower;
import com.sparkappdesign.archimedes.mathexpression.expressions.METrigonometricFunction;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEValue;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
/* loaded from: classes.dex */
public final class MEFunctionInverter {
    private MEFunctionInverter() {
    }

    public static MEEquals invertFunctionsAroundVariableInEquation(MEVariable variable, MEEquals equation) {
        MEEquals equation2;
        MEEquals equation3 = new MEEquals(new MEAdditions(equation.getLeftOperand(), equation.getRightOperand().negate()), MEValue.zero());
        if (equation3.canonicalize() instanceof MEEquals) {
            equation2 = (MEEquals) equation3.canonicalize();
        } else {
            equation2 = null;
        }
        if (MEContext.shouldStop(equation2)) {
            return null;
        }
        MEEquationSide startingSide = MEEquationSide.None;
        do {
            ArrayList<MEExpression> ancestry = ancestryOfFirstOccurrenceOfVariableNeedingInversionInExpression(variable, equation2, false);
            if (ancestry == null || ancestry.size() <= 1) {
                return equation2;
            }
            MEExpression parentExpression = ancestry.get(0);
            MEExpression childExpression = ancestry.get(1);
            MEEquationSide side = parentExpression == equation2.getLeftOperand() ? MEEquationSide.Left : MEEquationSide.Right;
            if (startingSide == MEEquationSide.None) {
                startingSide = side;
            }
            if (side != startingSide) {
                return equation2;
            }
            equation2 = applyInversionForChildExpressionOnSideOfEquation(childExpression, side, equation2);
        } while (!MEContext.shouldStop());
        return null;
    }

    private static ArrayList<MEExpression> ancestryOfFirstOccurrenceOfVariableNeedingInversionInExpression(MEVariable variable, MEExpression expression, boolean alreadyNeedsInversion) {
        boolean needsInversion;
        if (expression.children() == null) {
            return null;
        }
        Iterator<MEExpression> it = expression.children().iterator();
        while (it.hasNext()) {
            MEExpression child = it.next();
            if (alreadyNeedsInversion || isInversionNeededForChildExpressionOfExpression(child, expression)) {
                needsInversion = true;
            } else {
                needsInversion = false;
            }
            if (child.equals(variable) && needsInversion) {
                return new ArrayList<>(Arrays.asList(child));
            }
            ArrayList<MEExpression> subAncestry = ancestryOfFirstOccurrenceOfVariableNeedingInversionInExpression(variable, child, needsInversion);
            if (subAncestry != null) {
                ArrayList<MEExpression> result = new ArrayList<>();
                result.add(child);
                result.addAll(subAncestry);
                return result;
            }
        }
        return null;
    }

    private static boolean isInversionNeededForChildExpressionOfExpression(MEExpression child, MEExpression parent) {
        boolean z = false;
        if ((parent instanceof METrigonometricFunction) || (parent instanceof MELogarithm)) {
            return true;
        }
        if (parent instanceof MEPower) {
            MEPower power = (MEPower) parent;
            if (child == power.getExponent()) {
                return true;
            }
            if (child == power.getBase()) {
                if (!(child instanceof MEVariable) || !(power.getExponent() instanceof MEValue)) {
                    z = true;
                }
                return z;
            }
        }
        return false;
    }

    private static MEEquals applyInversionForChildExpressionOnSideOfEquation(MEExpression childExpression, MEEquationSide side, MEEquals equation) {
        MEExpression left;
        MEExpression right;
        MEExpression expression = side == MEEquationSide.Left ? equation.getLeftOperand() : equation.getRightOperand();
        MEExpression otherExpression = side == MEEquationSide.Left ? equation.getRightOperand() : equation.getLeftOperand();
        if (expression instanceof MEAdditions) {
            ArrayList<MEExpression> operands = new ArrayList<>();
            Iterator<MEExpression> it = ((MEAdditions) expression).getOperands().iterator();
            while (it.hasNext()) {
                MEExpression operand = it.next();
                if (operand != childExpression) {
                    operands.add(operand.negate());
                }
            }
            operands.add(otherExpression);
            otherExpression = new MEAdditions(operands);
        } else if (expression instanceof MEMultiplications) {
            ArrayList<MEExpression> operands2 = new ArrayList<>();
            Iterator<MEExpression> it2 = ((MEMultiplications) expression).getOperands().iterator();
            while (it2.hasNext()) {
                MEExpression operand2 = it2.next();
                if (operand2 != childExpression) {
                    operands2.add(operand2.invert());
                }
            }
            operands2.add(otherExpression);
            otherExpression = new MEMultiplications(operands2);
        } else if (expression instanceof MEPower) {
            MEPower power = (MEPower) expression;
            if (childExpression == power.getBase()) {
                otherExpression = MEPower.powerWithBaseAndExponent(otherExpression, power.getExponent().invert());
            } else if (childExpression == power.getExponent()) {
                otherExpression = new MELogarithm(power.getBase(), otherExpression);
            }
        } else if (expression instanceof METrigonometricFunction) {
            otherExpression = new METrigonometricFunction(METrigonometricFunctionType.inverse(((METrigonometricFunction) expression).getType()), otherExpression);
        } else if (expression instanceof MELogarithm) {
            MELogarithm logarithm = (MELogarithm) expression;
            if (childExpression == logarithm.getOperand()) {
                otherExpression = MEPower.powerWithBaseAndExponent(logarithm.getBase(), otherExpression);
            } else if (childExpression == logarithm.getBase()) {
                otherExpression = MEPower.powerWithBaseAndExponent(logarithm.getOperand(), otherExpression.invert());
            }
        }
        if (side == MEEquationSide.Left) {
            left = childExpression;
        } else {
            left = otherExpression;
        }
        if (side == MEEquationSide.Left) {
            right = otherExpression;
        } else {
            right = childExpression;
        }
        if (left == null || right == null) {
            return null;
        }
        return (MEEquals) new MEEquals(left, right).canonicalize();
    }
}
