package com.sparkappdesign.archimedes.mathtype.measures.font;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import com.sparkappdesign.archimedes.mathtype.enums.MTAlignmentType;
import com.sparkappdesign.archimedes.mathtype.measures.MTAlignment;
import com.sparkappdesign.archimedes.mathtype.measures.MTGlyphMeasures;
import com.sparkappdesign.archimedes.utilities.MatrixUtil;
import com.sparkappdesign.archimedes.utilities.RectUtil;
import com.sparkappdesign.archimedes.utilities.TypefaceCache;
import com.sparkappdesign.archimedes.utilities.path.TweenablePath;
import java.util.EnumSet;
/* loaded from: classes.dex */
public class MTFontMyriadProLight extends MTFont {
    public MTFontMyriadProLight(float fontSizeInPixels) {
        super(TypefaceCache.get(TypefaceCache.MYRIAD_PRO_LIGHT), fontSizeInPixels);
    }

    @Override // com.sparkappdesign.archimedes.mathtype.measures.font.MTFont
    public MTGlyphMeasures divisionMeasures(RectF dividendBounds, RectF divisorBounds) {
        RectF dividendBounds2 = new RectF(dividendBounds);
        RectF divisorBounds2 = new RectF(divisorBounds);
        Object cacheKey = MTFont.cacheKeyForDivision(dividendBounds2, divisorBounds2);
        MTGlyphMeasures cachedMeasures = (MTGlyphMeasures) this.mGlyphCache.get(cacheKey);
        if (cachedMeasures != null) {
            return cachedMeasures.copy();
        }
        Matrix unstandardizeTransform = new Matrix();
        unstandardizeTransform.setScale(this.mFontSizeInPixels, this.mFontSizeInPixels);
        Matrix standardizeTransform = new Matrix();
        unstandardizeTransform.invert(standardizeTransform);
        standardizeTransform.mapRect(dividendBounds2);
        standardizeTransform.mapRect(divisorBounds2);
        float barWidth = Math.max(dividendBounds2.width(), divisorBounds2.width()) + (2.0f * 0.15f);
        float totalWidth = barWidth + (2.0f * 0.1f);
        PointF dividendOffset = MTAlignment.offsetToAlignRectToPoint(dividendBounds2, new PointF(totalWidth / 2.0f, -0.287f - 0.05f), EnumSet.of(MTAlignmentType.CenterX, MTAlignmentType.MaxY));
        PointF divisorOffset = MTAlignment.offsetToAlignRectToPoint(divisorBounds2, new PointF(totalWidth / 2.0f, -0.287f + 0.04f + 0.15f), EnumSet.of(MTAlignmentType.CenterX, MTAlignmentType.MinY));
        RectF glyphBounds = RectUtil.create(0.0f, -0.287f, totalWidth, 0.04f);
        TweenablePath path = new TweenablePath();
        Matrix pathTransform = new Matrix(unstandardizeTransform);
        path.moveTo(0.1f, -0.287f);
        path.lineTo(0.1f, -0.287f + 0.04f);
        path.lineTo(0.1f + barWidth, -0.287f + 0.04f);
        path.lineTo(0.1f + barWidth, -0.287f);
        path.close();
        path.transform(pathTransform);
        PointF dividendOffset2 = MatrixUtil.mapPoints(dividendOffset, unstandardizeTransform);
        PointF divisorOffset2 = MatrixUtil.mapPoints(divisorOffset, unstandardizeTransform);
        unstandardizeTransform.mapRect(glyphBounds);
        MTGlyphMeasures measures = new MTGlyphMeasures();
        measures.setPath(path);
        measures.setBounds(glyphBounds);
        measures.getExtraMeasures().put(MTFont.MTDIVISION_GLYPH_DIVIDEND_OFFSET_KEY, dividendOffset2);
        measures.getExtraMeasures().put(MTFont.MTDIVISION_GLYPH_DIVISOR_OFFSET_KEY, divisorOffset2);
        this.mGlyphCache.put(cacheKey, measures.copy());
        return measures;
    }

    @Override // com.sparkappdesign.archimedes.mathtype.measures.font.MTFont
    public MTGlyphMeasures rootMeasures(RectF contentBounds, RectF degreeBounds) {
        RectF contentBounds2 = new RectF(contentBounds);
        RectF degreeBounds2 = new RectF(degreeBounds);
        Object cacheKey = MTFont.cacheKeyForRoot(contentBounds2, degreeBounds2);
        MTGlyphMeasures cachedMeasures = (MTGlyphMeasures) this.mGlyphCache.get(cacheKey);
        if (cachedMeasures != null) {
            return cachedMeasures.copy();
        }
        Matrix unstandardizeTransform = new Matrix();
        unstandardizeTransform.setScale(this.mFontSizeInPixels, this.mFontSizeInPixels);
        Matrix standardizeTransform = new Matrix();
        unstandardizeTransform.invert(standardizeTransform);
        standardizeTransform.mapRect(contentBounds2);
        standardizeTransform.mapRect(degreeBounds2);
        float contentPaddingBottom = descender() / this.mFontSizeInPixels;
        float barWidth = contentBounds2.width() + 0.05f + 0.2f;
        float barY = (contentBounds2.top - 0.1f) - 0.044f;
        float bottomY = contentBounds2.bottom + contentPaddingBottom + 0.104f;
        float slope = (barY - bottomY) / 0.227f;
        float barMinX = 0.558f + (0.044f / slope);
        float totalHeight = bottomY - barY;
        float centerY = barY + (totalHeight / 2.0f);
        PointF contentOffset = MTAlignment.offsetToAlignRectToPoint(contentBounds2, new PointF(barMinX + 0.05f, 0.0f), EnumSet.of(MTAlignmentType.MinX));
        PointF degreeOffset = MTAlignment.offsetToAlignRectToPoint(degreeBounds2, new PointF((0.287f + ((centerY - bottomY) / slope)) - 0.05f, (((totalHeight / 2.0f) + barY) - 0.029f) - 0.02f), EnumSet.of(MTAlignmentType.MaxX, MTAlignmentType.MaxY));
        float minX = Math.min(0.039f, degreeOffset.x - 0.1f);
        RectF bounds = RectUtil.create(minX, barY, (barMinX - minX) + barWidth + 0.1f, totalHeight);
        TweenablePath path = new TweenablePath();
        Matrix pathTransform = new Matrix(unstandardizeTransform);
        path.moveTo(barMinX, barY + 0.044f);
        path.lineTo(barMinX + barWidth, barY + 0.044f);
        path.lineTo(barMinX + barWidth, barY);
        path.lineTo(0.514f, barY);
        path.lineTo(0.304f, (0.01699999f * slope) + bottomY);
        path.lineTo(0.153f, centerY - 0.029f);
        path.lineTo(0.039f, 0.023f + centerY);
        path.lineTo(0.054f, 0.06f + centerY);
        path.lineTo(0.126f, 0.025f + centerY);
        path.lineTo(0.287f, bottomY);
        path.lineTo(0.327f, bottomY);
        path.close();
        path.transform(pathTransform);
        PointF contentOffset2 = MatrixUtil.mapPoints(contentOffset, unstandardizeTransform);
        PointF degreeOffset2 = MatrixUtil.mapPoints(degreeOffset, unstandardizeTransform);
        unstandardizeTransform.mapRect(bounds);
        MTGlyphMeasures measures = new MTGlyphMeasures();
        measures.setPath(path);
        measures.setBounds(bounds);
        measures.getExtraMeasures().put(MTFont.MTROOT_GLYPH_CONTENT_OFFSET_KEY, contentOffset2);
        measures.getExtraMeasures().put(MTFont.MTROOT_GLYPH_DEGREE_OFFSET_KEY, degreeOffset2);
        this.mGlyphCache.put(cacheKey, measures.copy());
        return measures;
    }

    @Override // com.sparkappdesign.archimedes.mathtype.measures.font.MTFont
    public MTGlyphMeasures parenthesesMeasures(RectF contentBounds) {
        RectF contentBounds2 = new RectF(contentBounds);
        Object cacheKey = MTFont.cacheKeyForParentheses(contentBounds2);
        MTGlyphMeasures cachedMeasures = (MTGlyphMeasures) this.mGlyphCache.get(cacheKey);
        if (cachedMeasures != null) {
            return cachedMeasures.copy();
        }
        Matrix unstandardizeTransform = new Matrix();
        unstandardizeTransform.setScale(this.mFontSizeInPixels, this.mFontSizeInPixels);
        Matrix standardizeTransform = new Matrix();
        unstandardizeTransform.invert(standardizeTransform);
        standardizeTransform.mapRect(contentBounds2);
        float spacing = contentBounds2.width() + 0.05f + 0.05f;
        float yOffset = contentBounds2.bottom - 31.488f;
        PointF contentOffset = MTAlignment.offsetToAlignRectToPoint(contentBounds2, new PointF(0.1f + 0.186f + 0.05f, 0.0f), EnumSet.of(MTAlignmentType.MinX));
        RectF bounds = RectUtil.create(0.0f, contentBounds2.top, 0.1f + 0.186f + spacing + 0.186f + 0.1f, contentBounds2.height());
        TweenablePath leftPath = new TweenablePath();
        Matrix leftTransform = new Matrix(unstandardizeTransform);
        leftTransform.preTranslate(0.1f, yOffset);
        leftTransform.preScale(1.0f, ((contentBounds2.height() - 77.312f) - 31.488f) / 0.82f);
        leftPath.moveTo(0.141f, -0.82f);
        leftPath.cubicTo(0.072f, -0.732f, 0.0f, -0.606f, 0.0f, -0.408f);
        leftPath.cubicTo(0.0f, -0.211f, 0.072f, -0.086f, 0.141f, 0.0f);
        leftPath.lineTo(0.186f, 0.0f);
        leftPath.cubicTo(0.106f, -0.104f, 0.046f, -0.227f, 0.046f, -0.407f);
        leftPath.cubicTo(0.046f, -0.591f, 0.103f, -0.717f, 0.186f, -0.82f);
        leftPath.close();
        leftPath.transform(leftTransform);
        TweenablePath rightPath = new TweenablePath();
        Matrix rightTransform = new Matrix(leftTransform);
        rightTransform.preTranslate(0.186f + spacing, 0.0f);
        rightPath.moveTo(0.045f, 0.0f);
        rightPath.cubicTo(0.114f, -0.088f, 0.186f, -0.213f, 0.186f, -0.41f);
        rightPath.cubicTo(0.186f, -0.608f, 0.114f, -0.734f, 0.045f, -0.82f);
        rightPath.lineTo(0.0f, -0.82f);
        rightPath.cubicTo(0.081f, -0.716f, 0.14f, -0.592f, 0.14f, -0.411f);
        rightPath.cubicTo(0.14f, -0.229f, 0.08f, -0.103f, 0.0f, 0.0f);
        rightPath.close();
        rightPath.transform(rightTransform);
        TweenablePath path = new TweenablePath();
        path.addPath(leftPath);
        path.addPath(rightPath);
        PointF contentOffset2 = MatrixUtil.mapPoints(contentOffset, unstandardizeTransform);
        unstandardizeTransform.mapRect(bounds);
        MTGlyphMeasures measures = new MTGlyphMeasures();
        measures.setPath(path);
        measures.setBounds(bounds);
        measures.getExtraMeasures().put(MTFont.MTPARENTHESES_GLYPH_CONTENT_OFFSET_KEY, contentOffset2);
        this.mGlyphCache.put(cacheKey, measures.copy());
        return measures;
    }

    @Override // com.sparkappdesign.archimedes.mathtype.measures.font.MTFont
    public RectF defaultPlaceholderBounds() {
        return RectUtil.create(0.0f, -ascender(), this.mFontSizeInPixels * 0.65f, ascender() - (descender() * 0.67f));
    }

    @Override // com.sparkappdesign.archimedes.mathtype.measures.font.MTFont
    public MTGlyphMeasures placeholderMeasures(RectF bounds) {
        RectF bounds2 = new RectF(bounds);
        Object cacheKey = MTFont.cacheKeyForPlaceholder(bounds2);
        MTGlyphMeasures cachedMeasures = (MTGlyphMeasures) this.mGlyphCache.get(cacheKey);
        if (cachedMeasures != null) {
            return cachedMeasures.copy();
        }
        float scale = this.mFontSizeInPixels;
        float inset = 0.03f * scale;
        float lineThickness = 0.044f * scale;
        float dashLength = 0.15f * scale;
        TweenablePath path = new TweenablePath();
        Matrix transform = new Matrix();
        transform.preTranslate(bounds2.left + inset, bounds2.top + inset);
        TweenablePath topLeftPath = addPlaceholderCorner(transform, lineThickness, dashLength);
        transform.preTranslate(0.0f, bounds2.height() - (2.0f * inset));
        transform.preScale(1.0f, -1.0f);
        TweenablePath bottomLeftPath = addPlaceholderCorner(transform, lineThickness, dashLength);
        transform.preTranslate(bounds2.width() - (2.0f * inset), 0.0f);
        transform.preScale(-1.0f, 1.0f);
        TweenablePath bottomRightPath = addPlaceholderCorner(transform, lineThickness, dashLength);
        transform.preTranslate(0.0f, bounds2.height() - (2.0f * inset));
        transform.preScale(1.0f, -1.0f);
        TweenablePath topRightPath = addPlaceholderCorner(transform, lineThickness, dashLength);
        path.addPath(topLeftPath);
        path.addPath(bottomLeftPath);
        path.addPath(bottomRightPath);
        path.addPath(topRightPath);
        MTGlyphMeasures measures = new MTGlyphMeasures();
        measures.setPath(path);
        measures.setBounds(bounds2);
        this.mGlyphCache.put(cacheKey, measures.copy());
        return measures;
    }

    private TweenablePath addPlaceholderCorner(Matrix transform, float t, float l) {
        TweenablePath path = new TweenablePath();
        path.moveTo(0.0f, 0.0f);
        path.lineTo(l, 0.0f);
        path.lineTo(l, t);
        path.lineTo(t, t);
        path.lineTo(t, l);
        path.lineTo(0.0f, l);
        path.close();
        path.transform(transform);
        return path;
    }

    @Override // com.sparkappdesign.archimedes.mathtype.measures.font.MTFont
    public MTFont copy() {
        return new MTFontMyriadProLight(getFontSizeInPixels());
    }

    @Override // com.sparkappdesign.archimedes.mathtype.measures.font.MTFont
    public MTFont copy(float pointSize) {
        return new MTFontMyriadProLight(pointSize);
    }

    @Override // com.sparkappdesign.archimedes.mathtype.measures.font.MTFont
    public float ascender() {
        return 0.75f * this.mFontSizeInPixels;
    }

    @Override // com.sparkappdesign.archimedes.mathtype.measures.font.MTFont
    public float descender() {
        return -0.25f * this.mFontSizeInPixels;
    }

    @Override // com.sparkappdesign.archimedes.mathtype.measures.font.MTFont
    public float xHeight() {
        return 0.484f * this.mFontSizeInPixels;
    }

    @Override // com.sparkappdesign.archimedes.mathtype.measures.font.MTFont
    public float capHeight() {
        return 0.674f * this.mFontSizeInPixels;
    }
}
