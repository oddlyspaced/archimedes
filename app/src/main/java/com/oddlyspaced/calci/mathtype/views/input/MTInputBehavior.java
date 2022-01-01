package com.oddlyspaced.calci.mathtype.views.input;

import com.oddlyspaced.calci.mathtype.enums.MTElementInputBehavior;
import com.oddlyspaced.calci.mathtype.enums.MTInlineOperatorType;
import com.oddlyspaced.calci.mathtype.enums.MTStringInputBehavior;
import com.oddlyspaced.calci.mathtype.nodes.MTElement;
import com.oddlyspaced.calci.mathtype.nodes.MTString;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTDivision;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTInlineOperator;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTLogarithm;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTParentheses;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTPower;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTPowerOfTenExponent;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTRoot;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTText;
import com.oddlyspaced.calci.mathtype.parsers.MTOperatorInfo;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
/* loaded from: classes.dex */
public class MTInputBehavior {
    private HashMap<MTString, EnumSet<MTStringInputBehavior>> mChildStringBehaviors;
    private MTElement mElement;
    private EnumSet<MTElementInputBehavior> mElementBehavior;
    private MTOperatorInfo mInlineFormOperatorInfo;

    public MTElement getElement() {
        return this.mElement;
    }

    public MTOperatorInfo getInlineFormOperatorInfo() {
        return this.mInlineFormOperatorInfo;
    }

    public EnumSet<MTElementInputBehavior> getElementBehavior() {
        return this.mElementBehavior;
    }

    private MTInputBehavior(MTElement element, EnumSet<MTElementInputBehavior> elementBehavior, HashMap<MTString, EnumSet<MTStringInputBehavior>> childStringBehaviors, MTOperatorInfo inlineFormOperatorInfo) {
        this.mElement = element;
        this.mElementBehavior = elementBehavior;
        this.mChildStringBehaviors = childStringBehaviors;
        this.mInlineFormOperatorInfo = inlineFormOperatorInfo;
        if (childStringBehaviors == null) {
            this.mChildStringBehaviors = new HashMap<>();
        }
    }

    public static MTInputBehavior behaviorForElement(MTElement element) {
        if (element instanceof MTInlineOperator) {
            return behaviorForInlineOperator((MTInlineOperator) element);
        }
        if (element instanceof MTDivision) {
            return behaviorForDivision((MTDivision) element);
        }
        if (element instanceof MTParentheses) {
            return behaviorForParentheses((MTParentheses) element);
        }
        if (element instanceof MTPower) {
            return behaviorForPower((MTPower) element);
        }
        if (element instanceof MTRoot) {
            return behaviorForRoot((MTRoot) element);
        }
        if (element instanceof MTLogarithm) {
            return behaviorForLogarithm((MTLogarithm) element);
        }
        if (element instanceof MTPowerOfTenExponent) {
            return behaviorForPowerOfTenExponent((MTPowerOfTenExponent) element);
        }
        if (element instanceof MTText) {
            return behaviorForText((MTText) element);
        }
        return new MTInputBehavior(element, EnumSet.noneOf(MTElementInputBehavior.class), null, null);
    }

    private static MTInputBehavior behaviorForInlineOperator(MTInlineOperator inlineOperator) {
        EnumSet<MTElementInputBehavior> elementBehavior = EnumSet.of(MTElementInputBehavior.UseSelectedRangeAsInlineOperand);
        MTOperatorInfo inlineFormOperatorInfo = MTOperatorInfo.infoForElement(inlineOperator);
        if (Arrays.asList(MTInlineOperatorType.Plus, MTInlineOperatorType.Minus, MTInlineOperatorType.Dot).contains(inlineOperator.getType())) {
            elementBehavior.add(MTElementInputBehavior.UseAutoAns);
        }
        return new MTInputBehavior(inlineOperator, elementBehavior, null, inlineFormOperatorInfo);
    }

    private static MTInputBehavior behaviorForDivision(MTDivision division) {
        EnumSet<MTElementInputBehavior> elementBehavior = EnumSet.of(MTElementInputBehavior.AlwaysDeleteElementOnBackspaceWhenAllChildrenAreEmpty);
        if (division.getDividend().isEmpty()) {
            elementBehavior.add(MTElementInputBehavior.UseAutoAns);
        }
        MTOperatorInfo inlineFormOperatorInfo = MTOperatorInfo.infoForElement(division, true);
        HashMap<MTString, EnumSet<MTStringInputBehavior>> childStringBehaviors = new HashMap<>();
        childStringBehaviors.put(division.getDividend(), EnumSet.of(MTStringInputBehavior.IfEmptyGrabSelectedRange, MTStringInputBehavior.IfEmptyGrabRangeLeftOfCursor, MTStringInputBehavior.IfNotFilledGrabCursor, MTStringInputBehavior.KeepContentsWhenBackspacingParent, MTStringInputBehavior.SelectContentsWhenBackspacingParentUnlessEqualToImplicitGrab, MTStringInputBehavior.SelectParentWhenBackspacingAtIndex0));
        childStringBehaviors.put(division.getDivisor(), EnumSet.of(MTStringInputBehavior.IfEmptyGrabSelectedRange, MTStringInputBehavior.IfNotFilledGrabCursor, MTStringInputBehavior.KeepContentsWhenBackspacingParent, MTStringInputBehavior.GrabCursorWhenBackspacingParentInsteadOfDelete, MTStringInputBehavior.DeleteParentWhenBackspacingAtIndex0));
        return new MTInputBehavior(division, elementBehavior, childStringBehaviors, inlineFormOperatorInfo);
    }

    private static MTInputBehavior behaviorForParentheses(MTParentheses parentheses) {
        HashMap<MTString, EnumSet<MTStringInputBehavior>> childStringBehaviors = new HashMap<>();
        childStringBehaviors.put(parentheses.getContents(), EnumSet.of(MTStringInputBehavior.IfEmptyGrabSelectedRange, MTStringInputBehavior.IfNotFilledGrabCursor, MTStringInputBehavior.KeepContentsWhenBackspacingParent, MTStringInputBehavior.SelectContentsWhenBackspacingParent, MTStringInputBehavior.DeleteParentWhenBackspacingAtIndex0));
        return new MTInputBehavior(parentheses, EnumSet.noneOf(MTElementInputBehavior.class), childStringBehaviors, null);
    }

    private static MTInputBehavior behaviorForPower(MTPower power) {
        EnumSet<MTElementInputBehavior> elementBehavior = EnumSet.of(MTElementInputBehavior.AlwaysDeleteElementOnBackspaceWhenAllChildrenAreEmpty, MTElementInputBehavior.InvisibleWhenAllChildrenAreEmpty, MTElementInputBehavior.IsBaselineShift, MTElementInputBehavior.UseSelectedRangeAsInlineOperand, MTElementInputBehavior.ForceParenthesesOnOperandsOfSameElementType, MTElementInputBehavior.UseAutoAns);
        MTOperatorInfo inlineFormOperatorInfo = MTOperatorInfo.infoForElement(power, true);
        HashMap<MTString, EnumSet<MTStringInputBehavior>> childStringBehaviors = new HashMap<>();
        childStringBehaviors.put(power.getExponent(), EnumSet.of(MTStringInputBehavior.IfNotFilledGrabCursor, MTStringInputBehavior.DeleteParentWhenLeftEmpty, MTStringInputBehavior.SelectParentWhenBackspacingAtIndex0));
        return new MTInputBehavior(power, elementBehavior, childStringBehaviors, inlineFormOperatorInfo);
    }

    private static MTInputBehavior behaviorForRoot(MTRoot root) {
        EnumSet<MTElementInputBehavior> elementBehavior = EnumSet.of(MTElementInputBehavior.AlwaysDeleteElementOnBackspaceWhenAllChildrenAreEmpty);
        HashMap<MTString, EnumSet<MTStringInputBehavior>> childStringBehaviors = new HashMap<>();
        childStringBehaviors.put(root.getDegree(), EnumSet.of(MTStringInputBehavior.DeleteParentWhenBackspacingAtIndex0, MTStringInputBehavior.IfNotFilledGrabCursor));
        childStringBehaviors.put(root.getContents(), EnumSet.of(MTStringInputBehavior.IfEmptyGrabSelectedRange, MTStringInputBehavior.IfNotFilledGrabCursor, MTStringInputBehavior.KeepContentsWhenBackspacingParent, MTStringInputBehavior.SelectContentsWhenBackspacingParent, MTStringInputBehavior.DeleteParentWhenBackspacingAtIndex0));
        return new MTInputBehavior(root, elementBehavior, childStringBehaviors, null);
    }

    private static MTInputBehavior behaviorForLogarithm(MTLogarithm logarithm) {
        EnumSet<MTElementInputBehavior> elementBehavior = EnumSet.of(MTElementInputBehavior.UseSelectedRangeAsInlineOperand);
        MTOperatorInfo inlineFormOperatorInfo = MTOperatorInfo.infoForElement(logarithm, true);
        HashMap<MTString, EnumSet<MTStringInputBehavior>> childStringBehaviors = new HashMap<>();
        childStringBehaviors.put(logarithm.getBase(), EnumSet.of(MTStringInputBehavior.DeleteParentWhenBackspacingAtIndex0, MTStringInputBehavior.IfNotFilledGrabCursor));
        return new MTInputBehavior(logarithm, elementBehavior, childStringBehaviors, inlineFormOperatorInfo);
    }

    private static MTInputBehavior behaviorForPowerOfTenExponent(MTPowerOfTenExponent powerOfTenExponent) {
        EnumSet<MTElementInputBehavior> elementBehavior = EnumSet.of(MTElementInputBehavior.AlwaysDeleteElementOnBackspaceWhenAllChildrenAreEmpty, MTElementInputBehavior.UseSelectedRangeAsInlineOperand, MTElementInputBehavior.UseAutoAns);
        MTOperatorInfo inlineFormOperatorInfo = MTOperatorInfo.infoForElement(powerOfTenExponent, true);
        HashMap<MTString, EnumSet<MTStringInputBehavior>> childStringBehaviors = new HashMap<>();
        childStringBehaviors.put(powerOfTenExponent.getExponent(), EnumSet.of(MTStringInputBehavior.IfNotFilledGrabCursor, MTStringInputBehavior.SelectParentWhenBackspacingAtIndex0));
        return new MTInputBehavior(powerOfTenExponent, elementBehavior, childStringBehaviors, inlineFormOperatorInfo);
    }

    private static MTInputBehavior behaviorForText(MTText text) {
        EnumSet<MTElementInputBehavior> elementBehavior = EnumSet.noneOf(MTElementInputBehavior.class);
        if (Arrays.asList("°", " rad").contains(text.getText())) {
            elementBehavior.add(MTElementInputBehavior.UseAutoAns);
        }
        return new MTInputBehavior(text, elementBehavior, null, null);
    }

    public MTString firstChildStringWithBehavior(MTStringInputBehavior behavior) {
        if (this.mElement.getChildren() == null) {
            return null;
        }
        for (MTString childString : this.mElement.getChildren()) {
            if (doesChildStringHaveBehavior(childString, behavior)) {
                return childString;
            }
        }
        return null;
    }

    public MTString firstEmptyChildStringWithBehavior(MTStringInputBehavior behavior) {
        if (this.mElement.getChildren() == null) {
            return null;
        }
        for (MTString childString : this.mElement.getChildren()) {
            if (childString.isEmpty() && doesChildStringHaveBehavior(childString, behavior)) {
                return childString;
            }
        }
        return null;
    }

    public MTString lastChildStringWithBehavior(MTStringInputBehavior behavior) {
        if (this.mElement.getChildren() == null) {
            return null;
        }
        MTString lastString = null;
        for (MTString childString : this.mElement.getChildren()) {
            if (doesChildStringHaveBehavior(childString, behavior)) {
                lastString = childString;
            }
        }
        return lastString;
    }

    public boolean hasBehavior(MTElementInputBehavior behavior) {
        return this.mElementBehavior.contains(behavior);
    }

    public boolean doesChildStringHaveBehavior(MTString string, MTStringInputBehavior behavior) {
        EnumSet<MTStringInputBehavior> stringBehavior = this.mChildStringBehaviors.get(string);
        if (stringBehavior != null) {
            return stringBehavior.contains(behavior);
        }
        return false;
    }

    public boolean doAllChildStringsHaveBehavior(MTStringInputBehavior behavior) {
        if (this.mElement.getChildren() == null) {
            return false;
        }
        for (MTString childString : this.mElement.getChildren()) {
            if (!doesChildStringHaveBehavior(childString, behavior)) {
                return false;
            }
        }
        return true;
    }

    public boolean areAllChildrenEmpty() {
        if (this.mElement.getChildren() == null) {
            return false;
        }
        for (MTString childString : this.mElement.getChildren()) {
            if (childString.isNotEmpty()) {
                return false;
            }
        }
        return true;
    }
}
