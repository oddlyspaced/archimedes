package com.sparkappdesign.archimedes.mathtype.nodes.elements;

import android.graphics.PointF;
import android.graphics.RectF;
import com.sparkappdesign.archimedes.mathtype.enums.MTNodeTraits;
import com.sparkappdesign.archimedes.mathtype.measures.MTGlyphMeasures;
import com.sparkappdesign.archimedes.mathtype.measures.MTMeasureContext;
import com.sparkappdesign.archimedes.mathtype.measures.MTMeasures;
import com.sparkappdesign.archimedes.mathtype.nodes.MTElement;
import com.sparkappdesign.archimedes.mathtype.nodes.MTString;
import com.sparkappdesign.archimedes.utilities.RectUtil;
import java.util.Arrays;
import java.util.EnumSet;
/* loaded from: classes.dex */
public class MTLogarithm extends MTElement {
    private static final String MTGLYPH_LOGARITHM_TEXT_KEY = "MTGLYPH_LOGARITHM_TEXT_KEY";
    private MTString mBase;

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTElement, com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public Iterable<? extends MTString> getChildren() {
        return Arrays.asList(this.mBase);
    }

    public MTString getBase() {
        return this.mBase;
    }

    public MTLogarithm() {
        this.mBase = new MTString(this);
    }

    public MTLogarithm(boolean baseEditable) {
        this(null, baseEditable);
    }

    public MTLogarithm(Iterable<? extends MTElement> baseElements, boolean baseEditable) {
        this();
        if (baseElements != null) {
            this.mBase.appendElements(baseElements);
        }
        this.mBase.mTraits = !baseEditable ? EnumSet.of(MTNodeTraits.CantSelectOrEditChildren) : EnumSet.noneOf(MTNodeTraits.class);
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public MTMeasures measureWithContext(MTMeasureContext context) {
        MTMeasures measures = new MTMeasures(this, context);
        MTGlyphMeasures textMeasures = context.getFont().genericGlyphMeasures("log");
        measures.getGlyphs().put(MTGLYPH_LOGARITHM_TEXT_KEY, textMeasures);
        MTMeasures baseMeasures = this.mBase.measureWithContext(context.childContextWithRelativeScale(context.getFont().exponentScale()));
        baseMeasures.setPosition(context.getFont().subscriptOffset(baseMeasures.getBounds(), textMeasures.getBounds()));
        baseMeasures.setPosition(new PointF(baseMeasures.getPosition().x + textMeasures.getPosition().x + textMeasures.getBounds().right, baseMeasures.getPosition().y));
        measures.addChild(baseMeasures);
        float rightMargin = context.getFont().genericGlyphMeasures(" ").getBounds().width();
        measures.autoCalculateBounds();
        RectF bounds = measures.getBounds();
        measures.setBounds(RectUtil.setWidth(bounds, bounds.width() + rightMargin));
        return measures;
    }

    @Override // java.lang.Object
    public String toString() {
        if (this.mBase.toString().equals("10")) {
            return "log ";
        }
        return "log[" + this.mBase + "] ";
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTElement, com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public MTLogarithm copy() {
        MTLogarithm copy = new MTLogarithm();
        copy.mTraits = this.mTraits.clone();
        copy.mBase = this.mBase.copy();
        copy.mBase.mParent = copy;
        return copy;
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public boolean equivalentTo(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MTLogarithm) || !this.mBase.equivalentTo(((MTLogarithm) other).mBase)) {
            return false;
        }
        return true;
    }
}
