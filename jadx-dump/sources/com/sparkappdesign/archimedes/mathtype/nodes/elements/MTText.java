package com.sparkappdesign.archimedes.mathtype.nodes.elements;

import com.sparkappdesign.archimedes.mathtype.measures.MTCommonMeasures;
import com.sparkappdesign.archimedes.mathtype.measures.MTMeasureContext;
import com.sparkappdesign.archimedes.mathtype.measures.MTMeasures;
import com.sparkappdesign.archimedes.mathtype.nodes.MTElement;
import com.sparkappdesign.archimedes.utilities.GeneralUtil;
import java.util.Locale;
/* loaded from: classes.dex */
public class MTText extends MTElement {
    private float mScale;
    private String mText;
    private boolean mUseSmallCaps;

    public String getText() {
        return this.mText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public float getScale() {
        return this.mScale;
    }

    public void setScale(float scale) {
        this.mScale = scale;
    }

    public boolean isUseSmallCaps() {
        return this.mUseSmallCaps;
    }

    public void setUseSmallCaps(boolean useSmallCaps) {
        this.mUseSmallCaps = useSmallCaps;
    }

    private MTText() {
    }

    public MTText(String text) {
        this();
        this.mText = text;
        this.mScale = 1.0f;
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public MTMeasures measureWithContext(MTMeasureContext context) {
        String text = this.mText == null ? "" : this.mText;
        float scale = this.mScale;
        if (this.mUseSmallCaps) {
            text = text.toUpperCase(Locale.getDefault());
            scale = context.getFont().xHeight() / context.getFont().capHeight();
        }
        if (scale != 1.0f) {
            context = context.childContextWithRelativeScale(scale);
        }
        return MTCommonMeasures.measuresForText(this, text, context);
    }

    @Override // java.lang.Object
    public String toString() {
        return this.mText;
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTElement, com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public MTText copy() {
        MTText text = new MTText();
        text.mTraits = this.mTraits.clone();
        text.setText(this.mText);
        text.setScale(this.mScale);
        text.setUseSmallCaps(this.mUseSmallCaps);
        return text;
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public boolean equivalentTo(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof MTText) {
            MTText otherText = (MTText) other;
            if (GeneralUtil.equalOrBothNull(this.mText, otherText.mText) && this.mScale == otherText.mScale && this.mUseSmallCaps == otherText.mUseSmallCaps) {
                return true;
            }
        }
        return false;
    }
}
