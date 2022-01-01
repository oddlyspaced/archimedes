package com.oddlyspaced.calci.mathtype.measures;

import com.oddlyspaced.calci.mathtype.enums.MTDigitGroupingStyle;
import com.oddlyspaced.calci.mathtype.measures.font.MTFont;
import com.oddlyspaced.calci.mathtype.nodes.MTElement;
import java.util.Hashtable;
/* loaded from: classes.dex */
public class MTMeasureContext {
    private MTDigitGroupingStyle mDigitGroupingStyle;
    private MTFont mFont;
    private Hashtable<MTElement, MTMeasures> mMeasures;
    private MTFont mOriginalFont;
    private boolean mShowPlaceholderGlyphs;

    public MTFont getFont() {
        return this.mFont;
    }

    public void setFont(MTFont font) {
        this.mFont = font;
    }

    public MTDigitGroupingStyle getDigitGroupingStyle() {
        return this.mDigitGroupingStyle;
    }

    public void setDigitGroupingStyle(MTDigitGroupingStyle digitGroupingStyle) {
        this.mDigitGroupingStyle = digitGroupingStyle;
    }

    public Hashtable<MTElement, MTMeasures> getMeasures() {
        if (this.mMeasures == null) {
            this.mMeasures = new Hashtable<>();
        }
        return this.mMeasures;
    }

    public void setMeasures(Hashtable<MTElement, MTMeasures> measures) {
        this.mMeasures = measures;
    }

    public boolean isshowPlaceholderGlyphs() {
        return this.mShowPlaceholderGlyphs;
    }

    public void setShowPlaceholderGlyphs(boolean showPlaceholderGlyphs) {
        this.mShowPlaceholderGlyphs = showPlaceholderGlyphs;
    }

    public MTMeasureContext() {
    }

    public MTMeasureContext(MTFont font, MTDigitGroupingStyle digitGroupingStyle, boolean showPlaceholderGlyphs) {
        this.mFont = font;
        this.mOriginalFont = font;
        this.mDigitGroupingStyle = digitGroupingStyle;
        this.mShowPlaceholderGlyphs = showPlaceholderGlyphs;
    }

    public MTMeasureContext childContext() {
        MTMeasureContext context = new MTMeasureContext();
        context.mOriginalFont = this.mOriginalFont;
        context.mFont = this.mFont;
        context.mDigitGroupingStyle = this.mDigitGroupingStyle;
        context.mShowPlaceholderGlyphs = this.mShowPlaceholderGlyphs;
        return context;
    }

    public MTMeasureContext childContextWithRelativeScale(float scale) {
        MTMeasureContext context = childContext();
        if (context != null) {
            context.setFont(this.mFont.copy(this.mFont.getFontSizeInPixels() * scale));
        }
        return context;
    }

    public MTMeasureContext childContextWithAbsoluteScale(float scale) {
        MTMeasureContext context = childContext();
        if (context != null) {
            context.setFont(this.mFont.copy(this.mOriginalFont.getFontSizeInPixels() * scale));
        }
        return context;
    }
}
