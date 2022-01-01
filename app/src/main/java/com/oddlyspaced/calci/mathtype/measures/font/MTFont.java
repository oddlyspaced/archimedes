package com.oddlyspaced.calci.mathtype.measures.font;

import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import com.oddlyspaced.calci.mathtype.enums.MTAlignmentType;
import com.oddlyspaced.calci.mathtype.enums.MTGlyph;
import com.oddlyspaced.calci.mathtype.measures.MTAlignment;
import com.oddlyspaced.calci.mathtype.measures.MTGlyphMeasures;
import com.oddlyspaced.calci.utilities.RectUtil;
import com.oddlyspaced.calci.utilities.path.PathWrapper;
import java.util.EnumSet;
import java.util.Hashtable;
/* loaded from: classes.dex */
public abstract class MTFont {
    public static final String MTDIVISION_GLYPH_DIVIDEND_OFFSET_KEY = "MTDIVISION_GLYPH_DIVIDEND_OFFSET_KEY";
    public static final String MTDIVISION_GLYPH_DIVISOR_OFFSET_KEY = "MTDIVISION_GLYPH_DIVISOR_OFFSET_KEY";
    public static final String MTPARENTHESES_GLYPH_CONTENT_OFFSET_KEY = "MTPARENTHESES_GLYPH_CONTENT_OFFSET_KEY";
    public static final String MTROOT_GLYPH_CONTENT_OFFSET_KEY = "MTROOT_GLYPH_CONTENT_OFFSET_KEY";
    public static final String MTROOT_GLYPH_DEGREE_OFFSET_KEY = "MTROOT_GLYPH_DEGREE_OFFSET_KEY";
    private static Hashtable<Object, MTFont> mFontCache = new Hashtable<>();
    protected Paint.FontMetrics mFontMetrics;
    protected float mFontSizeInPixels;
    protected Hashtable<Object, MTGlyphMeasures> mGlyphCache;
    protected Paint mPaint;
    protected Typeface mTypeface;

    public abstract float ascender();

    public abstract float capHeight();

    public abstract MTFont copy();

    public abstract MTFont copy(float f);

    public abstract float descender();

    public abstract MTGlyphMeasures divisionMeasures(RectF rectF, RectF rectF2);

    public abstract MTGlyphMeasures parenthesesMeasures(RectF rectF);

    public abstract MTGlyphMeasures placeholderMeasures(RectF rectF);

    public abstract MTGlyphMeasures rootMeasures(RectF rectF, RectF rectF2);

    public abstract float xHeight();

    public float getFontSizeInPixels() {
        return this.mFontSizeInPixels;
    }

    public MTFont(Typeface typeface, float fontSizeInPixels) {
        this.mTypeface = typeface;
        this.mFontSizeInPixels = fontSizeInPixels;
        Object cacheKey = cacheKeyForFont(this.mTypeface, this.mFontSizeInPixels);
        MTFont cachedFont = mFontCache.get(cacheKey);
        if (cachedFont != null) {
            this.mGlyphCache = cachedFont.mGlyphCache;
            this.mPaint = cachedFont.mPaint;
            this.mFontMetrics = cachedFont.mFontMetrics;
            return;
        }
        this.mGlyphCache = new Hashtable<>();
        Paint paint = new Paint();
        paint.setTextSize(fontSizeInPixels);
        paint.setTypeface(typeface);
        this.mPaint = paint;
        this.mFontMetrics = paint.getFontMetrics();
        mFontCache.put(cacheKey, this);
    }

    public float exponentScale() {
        return 0.75f;
    }

    public PointF superscriptOffset(RectF superscriptBounds, RectF baseBounds) {
        return MTAlignment.offsetToAlignRectToPoint(superscriptBounds, new PointF(0.0f, Math.min(baseBounds.top + (0.6f * superscriptBounds.height()), baseBounds.top + (0.45f * baseBounds.height()))), EnumSet.of(MTAlignmentType.MinX, MTAlignmentType.MaxY));
    }

    public PointF subscriptOffset(RectF subscriptBounds, RectF baseBounds) {
        return MTAlignment.offsetToAlignRectToPoint(subscriptBounds, new PointF(0.0f, Math.max(baseBounds.bottom - (0.6f * subscriptBounds.height()), baseBounds.bottom - (0.45f * baseBounds.height()))), EnumSet.of(MTAlignmentType.MinX, MTAlignmentType.MinY));
    }

    public MTGlyphMeasures genericGlyphMeasures(String string) {
        MTGlyphMeasures cachedMeasures = this.mGlyphCache.get(cacheKeyForGenericGlyph(string));
        if (cachedMeasures != null) {
            return cachedMeasures;
        }
        RectF genericLineBounds = genericLineBounds();
        PathWrapper path = new PathWrapper();
        float width = this.mPaint.measureText(string);
        this.mPaint.getTextPath(string, 0, string.length(), 0.0f, 0.0f, path.getPath());
        RectF bounds = RectUtil.create(genericLineBounds.left, genericLineBounds.top, width, genericLineBounds.height());
        if (string.equals("1")) {
            path.offset(-0.1f * this.mFontSizeInPixels, 0.0f);
            bounds.right -= bounds.width() / 2.39f;
        }
        MTGlyphMeasures measures = new MTGlyphMeasures();
        measures.setPath(path);
        measures.setBounds(bounds);
        return measures;
    }

    public RectF defaultPlaceholderBounds() {
        return genericLineBounds();
    }

    private static Object cacheKeyForFont(Typeface typeface, float pointSize) {
        return new Object[]{typeface, Float.valueOf(pointSize)};
    }

    protected static Object cacheKeyForGenericGlyph(String string) {
        return string;
    }

    public static Object cacheKeyForDivision(RectF dividendBounds, RectF divisorBounds) {
        return new Object[]{MTGlyph.Division, new RectF(dividendBounds), new RectF(divisorBounds)};
    }

    public static Object cacheKeyForRoot(RectF contentBounds, RectF degreeBounds) {
        return new Object[]{MTGlyph.Root, new RectF(contentBounds), new RectF(degreeBounds)};
    }

    public static Object cacheKeyForParentheses(RectF contentBounds) {
        return new Object[]{MTGlyph.Parentheses, new RectF(contentBounds)};
    }

    public static Object cacheKeyForPlaceholder(RectF bounds) {
        return new Object[]{MTGlyph.Placeholder, new RectF(bounds)};
    }

    public RectF genericLineBounds() {
        return RectUtil.create(0.0f, -ascender(), 0.0f, ascender() - descender());
    }
}
