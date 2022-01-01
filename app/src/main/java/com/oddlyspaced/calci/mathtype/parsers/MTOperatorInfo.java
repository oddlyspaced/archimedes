package com.sparkappdesign.archimedes.mathtype.parsers;

import com.sparkappdesign.archimedes.mathtype.enums.MTAssociativity;
import com.sparkappdesign.archimedes.mathtype.enums.MTInlineOperatorType;
import com.sparkappdesign.archimedes.mathtype.enums.MTNodeTraits;
import com.sparkappdesign.archimedes.mathtype.enums.MTOperandSide;
import com.sparkappdesign.archimedes.mathtype.enums.MTOperatorNotation;
import com.sparkappdesign.archimedes.mathtype.enums.MTPrecedence;
import com.sparkappdesign.archimedes.mathtype.nodes.MTElement;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTDivision;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTInlineOperator;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTLogarithm;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTPower;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTPowerOfTenExponent;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTRoot;
/* loaded from: classes.dex */
public class MTOperatorInfo {
    private MTAssociativity mAssociativity;
    private boolean mImplicit;
    private MTOperatorNotation mNotation;
    private MTPrecedence mPrecedence;

    public MTOperatorNotation getNotation() {
        return this.mNotation;
    }

    public MTPrecedence getPrecedence() {
        return this.mPrecedence;
    }

    public MTAssociativity getAssociativity() {
        return this.mAssociativity;
    }

    public boolean isImplicit() {
        return this.mImplicit;
    }

    public MTOperatorInfo(MTOperatorNotation notation, MTPrecedence precedence, MTAssociativity associativity, boolean implicit) {
        this.mNotation = notation;
        this.mPrecedence = precedence;
        this.mAssociativity = associativity;
        this.mImplicit = implicit;
    }

    public static MTOperatorInfo infoForImplicitMultiplication() {
        return new MTOperatorInfo(MTOperatorNotation.Infix, MTPrecedence.ImplicitMultiply, MTAssociativity.Associative, true);
    }

    private static MTOperatorInfo infoForInfixOperator(MTAssociativity associativity, MTPrecedence precedence) {
        return new MTOperatorInfo(MTOperatorNotation.Infix, precedence, associativity, false);
    }

    private static MTOperatorInfo infoForOperator(MTOperatorNotation notation, MTPrecedence precedence) {
        return new MTOperatorInfo(notation, precedence, MTAssociativity.NonAssociative, false);
    }

    public static MTOperatorInfo infoForElement(MTElement element) {
        return infoForElement(element, false);
    }

    public static MTOperatorInfo infoForElement(MTElement element, boolean asInlineForm) {
        MTInlineOperatorType inlineOperatorType = MTInlineOperatorType.None;
        if (element instanceof MTInlineOperator) {
            inlineOperatorType = ((MTInlineOperator) element).getType();
        }
        if ((element instanceof MTInlineOperator) && ((MTInlineOperator) element).isSign()) {
            return infoForOperator(MTOperatorNotation.Prefix, MTPrecedence.Sign);
        }
        if (inlineOperatorType == MTInlineOperatorType.Plus) {
            return infoForInfixOperator(MTAssociativity.Associative, MTPrecedence.AddSubtract);
        }
        if (inlineOperatorType == MTInlineOperatorType.Minus) {
            return infoForInfixOperator(MTAssociativity.LeftAssociative, MTPrecedence.AddSubtract);
        }
        if (inlineOperatorType == MTInlineOperatorType.Equals) {
            return infoForInfixOperator(MTAssociativity.Associative, MTPrecedence.Equals);
        }
        if (MTInlineOperatorType.isTrigonometric(inlineOperatorType)) {
            return infoForOperator(MTOperatorNotation.Prefix, MTPrecedence.Function);
        }
        if (inlineOperatorType == MTInlineOperatorType.NaturalLogarithm || ((element instanceof MTLogarithm) && asInlineForm)) {
            return infoForOperator(MTOperatorNotation.Prefix, MTPrecedence.Function);
        }
        if (inlineOperatorType == MTInlineOperatorType.Dot) {
            return infoForInfixOperator(MTAssociativity.Associative, MTPrecedence.MultiplyDivide);
        }
        if (inlineOperatorType == MTInlineOperatorType.Division || ((element instanceof MTDivision) && asInlineForm)) {
            return infoForInfixOperator(MTAssociativity.LeftAssociative, MTPrecedence.MultiplyDivide);
        }
        if (inlineOperatorType == MTInlineOperatorType.Power || ((element instanceof MTPower) && asInlineForm)) {
            return infoForInfixOperator(MTAssociativity.RightAssociative, MTPrecedence.Power);
        }
        if (inlineOperatorType == MTInlineOperatorType.SquareRoot) {
            return infoForOperator(MTOperatorNotation.Prefix, MTPrecedence.Root);
        }
        if (inlineOperatorType == MTInlineOperatorType.EngineeringExponent) {
            return infoForInfixOperator(MTAssociativity.RightAssociative, MTPrecedence.NumberExponent);
        }
        if (inlineOperatorType == MTInlineOperatorType.Percent || inlineOperatorType == MTInlineOperatorType.PerMil) {
            return infoForOperator(MTOperatorNotation.Postfix, MTPrecedence.Unit);
        }
        if (element instanceof MTPower) {
            return infoForOperator(MTOperatorNotation.Postfix, MTPrecedence.Power);
        }
        if (element instanceof MTLogarithm) {
            return infoForOperator(MTOperatorNotation.Prefix, MTPrecedence.Function);
        }
        if (element instanceof MTPowerOfTenExponent) {
            if (asInlineForm) {
                return infoForInfixOperator(MTAssociativity.RightAssociative, MTPrecedence.NumberExponent);
            }
            return infoForOperator(MTOperatorNotation.Postfix, MTPrecedence.NumberExponent);
        } else if (!(element instanceof MTRoot) || !asInlineForm) {
            return null;
        } else {
            if (((MTRoot) element).getDegree().getTraits().contains(MTNodeTraits.CantSelectOrEditChildren)) {
                return infoForInfixOperator(MTAssociativity.LeftAssociative, MTPrecedence.Root);
            }
            return infoForOperator(MTOperatorNotation.Prefix, MTPrecedence.Root);
        }
    }

    public boolean hasOperand(MTOperandSide operandSide) {
        boolean z = false;
        switch (this.mNotation) {
            case Prefix:
                return operandSide == MTOperandSide.Right;
            case Postfix:
                return operandSide == MTOperandSide.Left;
            case Infix:
                if (operandSide == MTOperandSide.Left || operandSide == MTOperandSide.Right) {
                    z = true;
                }
                return z;
            default:
                return false;
        }
    }
}
