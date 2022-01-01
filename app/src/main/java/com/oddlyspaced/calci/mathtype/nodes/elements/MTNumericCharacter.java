package com.oddlyspaced.calci.mathtype.nodes.elements;

import android.graphics.RectF;
import com.oddlyspaced.calci.mathtype.enums.MTNumericCharacterType;
import com.oddlyspaced.calci.mathtype.measures.MTCommonMeasures;
import com.oddlyspaced.calci.mathtype.measures.MTMeasureContext;
import com.oddlyspaced.calci.mathtype.measures.MTMeasures;
import com.oddlyspaced.calci.mathtype.nodes.MTElement;
import com.oddlyspaced.calci.utilities.RectUtil;
/* loaded from: classes.dex */
public class MTNumericCharacter extends MTElement {
    private MTNumericCharacterType mType;

    public MTNumericCharacterType getType() {
        return this.mType;
    }

    public void setType(MTNumericCharacterType type) {
        this.mType = type;
    }

    private MTNumericCharacter() {
    }

    public MTNumericCharacter(MTNumericCharacterType type) {
        this();
        this.mType = type;
    }

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTNode
    public MTMeasures measureWithContext(MTMeasureContext context) {
        String text = getText();
        if (this.mType != MTNumericCharacterType.GroupingSpace) {
            return MTCommonMeasures.measuresForText(this, text, context);
        }
        RectF bounds = RectUtil.setWidth(context.getFont().genericLineBounds(), (float) (((double) context.getFont().getFontSizeInPixels()) * 0.15d));
        MTMeasures measures = new MTMeasures(this, context);
        measures.setBounds(bounds);
        return measures;
    }

    private String getText() {
        switch (this.mType) {
            case Number0:
                return "0";
            case Number1:
                return "1";
            case Number2:
                return "2";
            case Number3:
                return "3";
            case Number4:
                return "4";
            case Number5:
                return "5";
            case Number6:
                return "6";
            case Number7:
                return "7";
            case Number8:
                return "8";
            case Number9:
                return "9";
            case RadixPoint:
                return ".";
            case GroupingSpace:
                return " ";
            default:
                return "";
        }
    }

    @Override // java.lang.Object
    public String toString() {
        return getText();
    }

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTElement, com.oddlyspaced.calci.mathtype.nodes.MTNode
    public MTNumericCharacter copy() {
        MTNumericCharacter copy = new MTNumericCharacter();
        copy.mTraits = this.mTraits.clone();
        copy.mType = this.mType;
        return copy;
    }

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTNode
    public boolean equivalentTo(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MTNumericCharacter) || this.mType != ((MTNumericCharacter) other).mType) {
            return false;
        }
        return true;
    }
}
