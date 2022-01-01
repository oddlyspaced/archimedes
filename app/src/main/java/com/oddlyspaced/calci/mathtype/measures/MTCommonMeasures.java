package com.oddlyspaced.calci.mathtype.measures;

import android.graphics.PointF;
import android.graphics.RectF;
import com.oddlyspaced.calci.mathtype.enums.MTBaselineShiftType;
import com.oddlyspaced.calci.mathtype.enums.MTMeasuresOptions;
import com.oddlyspaced.calci.mathtype.measures.font.MTFont;
import com.oddlyspaced.calci.mathtype.nodes.MTElement;
import com.oddlyspaced.calci.mathtype.nodes.MTNode;
import com.oddlyspaced.calci.mathtype.nodes.MTString;
import java.util.EnumSet;
/* loaded from: classes.dex */
public class MTCommonMeasures {
    private static final String MTGLYPH_DIVISION_BAR_KEY = "MTGLYPH_DIVISION_BAR_KEY";
    private static final String MTGLYPH_PARENTHESES_KEY = "MTGLYPH_PARENTHESES_KEY";
    private static final String MTGLYPH_PLACEHOLDER_KEY = "MTGLYPH_PLACEHOLDER_KEY";
    private static final String MTGLYPH_ROOT_RADICAL_KEY = "MTGLYPH_ROOT_RADICAL_KEY";
    private static final String MTGLYPH_TEXT_KEY = "MTGLYPH_TEXT_KEY";
    private static final String MTMEASURES_OPTIONS_KEY = "MTMEASURES_OPTIONS_KEY";

    public static MTMeasures measuresForText(MTElement element, String text, MTMeasureContext context) {
        MTGlyphMeasures glyphMeasures = context.getFont().genericGlyphMeasures(text);
        MTMeasures measures = new MTMeasures(element, context);
        measures.getGlyphs().put(MTGLYPH_TEXT_KEY, glyphMeasures);
        measures.autoCalculateBounds();
        return measures;
    }

    public static MTMeasures measuresForParentheses(MTElement element, MTNode contents, MTMeasureContext context) {
        MTMeasures contentMeasures = contents.measureWithContext(context.childContext());
        MTGlyphMeasures parenthesesMeasures = context.getFont().parenthesesMeasures(contentMeasures.getBounds());
        contentMeasures.setPosition((PointF) parenthesesMeasures.getExtraMeasures().get(MTFont.MTPARENTHESES_GLYPH_CONTENT_OFFSET_KEY));
        MTMeasures measures = new MTMeasures(element, context);
        measures.getGlyphs().put(MTGLYPH_PARENTHESES_KEY, parenthesesMeasures);
        measures.addChild(contentMeasures);
        measures.autoCalculateBounds();
        return measures;
    }

    public static MTMeasures measuresForDivision(MTElement element, MTNode dividend, MTNode divisor, MTMeasureContext context) {
        MTMeasures dividendMeasures = dividend.measureWithContext(context.childContext());
        MTMeasures divisorMeasures = divisor.measureWithContext(context.childContext());
        MTGlyphMeasures divisionMeasures = context.getFont().divisionMeasures(dividendMeasures.getBounds(), divisorMeasures.getBounds());
        dividendMeasures.setPosition((PointF) divisionMeasures.getExtraMeasures().get(MTFont.MTDIVISION_GLYPH_DIVIDEND_OFFSET_KEY));
        divisorMeasures.setPosition((PointF) divisionMeasures.getExtraMeasures().get(MTFont.MTDIVISION_GLYPH_DIVISOR_OFFSET_KEY));
        MTMeasures measures = new MTMeasures(element, context);
        measures.getGlyphs().put(MTGLYPH_DIVISION_BAR_KEY, divisionMeasures);
        measures.addChild(dividendMeasures);
        measures.addChild(divisorMeasures);
        measures.autoCalculateBounds();
        return measures;
    }

    public static MTMeasures measuresForRoot(MTElement element, MTNode contents, MTNode degree, MTMeasureContext context) {
        MTMeasures contentMeasures = contents.measureWithContext(context.childContext());
        MTMeasures degreeMeasures = degree.measureWithContext(context.childContextWithRelativeScale(context.getFont().exponentScale()));
        RectF degreeBounds = degreeMeasures.getBounds();
        MTGlyphMeasures rootMeasures = context.getFont().rootMeasures(contentMeasures.getBounds(), degreeBounds);
        PointF contentOffset = (PointF) rootMeasures.getExtraMeasures().get(MTFont.MTROOT_GLYPH_CONTENT_OFFSET_KEY);
        PointF degreeOffset = (PointF) rootMeasures.getExtraMeasures().get(MTFont.MTROOT_GLYPH_DEGREE_OFFSET_KEY);
        float minX = degreeOffset.x + degreeBounds.left;
        if (minX < 0.0f) {
            degreeOffset.x -= minX;
            contentOffset.x -= minX;
            rootMeasures.setPosition(new PointF(rootMeasures.getPosition().x - minX, rootMeasures.getPosition().y));
        }
        contentMeasures.setPosition(contentOffset);
        degreeMeasures.setPosition(degreeOffset);
        MTMeasures measures = new MTMeasures(element, context);
        measures.getGlyphs().put(MTGLYPH_ROOT_RADICAL_KEY, rootMeasures);
        measures.addChild(contentMeasures);
        measures.addChild(degreeMeasures);
        measures.autoCalculateBounds();
        return measures;
    }

    public static MTMeasures measuresForBaselineShift(MTElement element, MTBaselineShiftType type, MTNode contents, MTMeasureContext context) {
        RectF baseBounds;
        MTMeasures contentMeasures = contents.measureWithContext(context.childContextWithRelativeScale(context.getFont().exponentScale()));
        RectF contentBounds = contentMeasures.getBounds();
        while (true) {
            MTElement base = element.getParent().elementBefore(element);
            if (base == null) {
                baseBounds = context.getFont().genericLineBounds();
                break;
            }
            MTMeasures baseMeasures = context.getMeasures().get(base);
            baseBounds = baseMeasures.getBounds();
            EnumSet<MTMeasuresOptions> options = (EnumSet) baseMeasures.getExtraInfo().get(MTMEASURES_OPTIONS_KEY);
            if (options == null) {
                break;
            } else if (type != MTBaselineShiftType.Superscript || !options.contains(MTMeasuresOptions.PlaceSuperscriptAbove)) {
                if (type != MTBaselineShiftType.Subscript || !options.contains(MTMeasuresOptions.PlaceSubscriptBelow)) {
                    break;
                }
            }
        }
        if (type == MTBaselineShiftType.Superscript) {
            contentMeasures.setPosition(context.getFont().superscriptOffset(contentBounds, baseBounds));
        } else {
            contentMeasures.setPosition(context.getFont().subscriptOffset(contentBounds, baseBounds));
        }
        MTMeasures measures = new MTMeasures(element, context);
        measures.getExtraInfo().put(MTMEASURES_OPTIONS_KEY, type == MTBaselineShiftType.Superscript ? EnumSet.of(MTMeasuresOptions.PlaceSubscriptBelow) : EnumSet.of(MTMeasuresOptions.PlaceSuperscriptAbove));
        measures.addChild(contentMeasures);
        measures.autoCalculateBounds();
        return measures;
    }

    public static MTMeasures measuresForContainer(MTElement element, MTNode contents, MTMeasureContext context) {
        MTMeasures measures = new MTMeasures(element, context);
        measures.addChild(contents.measureWithContext(context.childContext()));
        measures.autoCalculateBounds();
        return measures;
    }

    public static MTMeasures measuresForPlaceholder(MTString string, boolean showPlaceholderGlyph, MTMeasureContext context) {
        RectF genericLineBounds = context.getFont().genericLineBounds();
        MTMeasures measures = new MTMeasures(string, context);
        if (showPlaceholderGlyph) {
            measures.getGlyphs().put(MTGLYPH_PLACEHOLDER_KEY, context.getFont().placeholderMeasures(context.getFont().defaultPlaceholderBounds()));
            measures.autoCalculateBounds();
            genericLineBounds.union(measures.getBounds());
            measures.setBounds(genericLineBounds);
        } else {
            measures.setBounds(genericLineBounds);
        }
        return measures;
    }
}
