package com.sparkappdesign.archimedes.mathtype.nodes.elements;

import android.graphics.PointF;
import android.graphics.RectF;
import com.sparkappdesign.archimedes.mathtype.enums.MTBaselineShiftType;
import com.sparkappdesign.archimedes.mathtype.measures.MTCommonMeasures;
import com.sparkappdesign.archimedes.mathtype.measures.MTMeasureContext;
import com.sparkappdesign.archimedes.mathtype.measures.MTMeasures;
import com.sparkappdesign.archimedes.mathtype.nodes.MTElement;
import com.sparkappdesign.archimedes.mathtype.nodes.MTString;
import com.sparkappdesign.archimedes.utilities.PointUtil;
import java.util.Arrays;
import java.util.Iterator;
/* loaded from: classes.dex */
public class MTPowerOfTenExponent extends MTElement {
    private MTString mExponent = new MTString(this);

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTElement, com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public Iterable<? extends MTString> getChildren() {
        return Arrays.asList(this.mExponent);
    }

    public MTString getExponent() {
        return this.mExponent;
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public MTMeasures measureWithContext(MTMeasureContext context) {
        MTMeasures measures = MTCommonMeasures.measuresForText(this, "×10", context);
        MTMeasures exponentMeasures = MTCommonMeasures.measuresForBaselineShift(this, MTBaselineShiftType.Superscript, this.mExponent, context);
        PointF offset = measures.getNextElementOffset();
        RectF bounds = new RectF(exponentMeasures.getBounds());
        bounds.offset(offset.x, offset.y);
        measures.getBounds().union(bounds);
        Iterator<MTMeasures> it = exponentMeasures.getChildren().iterator();
        while (it.hasNext()) {
            MTMeasures childMeasures = it.next();
            childMeasures.setPosition(PointUtil.addPoints(childMeasures.getPosition(), offset));
            measures.addChild(childMeasures);
        }
        return measures;
    }

    @Override // java.lang.Object
    public String toString() {
        return "×10^(" + this.mExponent + ")";
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTElement, com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public MTPowerOfTenExponent copy() {
        MTPowerOfTenExponent copy = new MTPowerOfTenExponent();
        copy.mTraits = this.mTraits.clone();
        copy.mExponent = this.mExponent.copy();
        copy.mExponent.mParent = copy;
        return copy;
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public boolean equivalentTo(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MTPowerOfTenExponent) || !this.mExponent.equivalentTo(((MTPowerOfTenExponent) other).mExponent)) {
            return false;
        }
        return true;
    }
}
