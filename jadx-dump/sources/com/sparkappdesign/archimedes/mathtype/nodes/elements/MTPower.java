package com.sparkappdesign.archimedes.mathtype.nodes.elements;

import android.graphics.RectF;
import com.sparkappdesign.archimedes.mathtype.enums.MTBaselineShiftType;
import com.sparkappdesign.archimedes.mathtype.measures.MTCommonMeasures;
import com.sparkappdesign.archimedes.mathtype.measures.MTMeasureContext;
import com.sparkappdesign.archimedes.mathtype.measures.MTMeasures;
import com.sparkappdesign.archimedes.mathtype.nodes.MTElement;
import com.sparkappdesign.archimedes.mathtype.nodes.MTString;
import com.sparkappdesign.archimedes.utilities.RectUtil;
import java.util.Arrays;
/* loaded from: classes.dex */
public class MTPower extends MTElement {
    private MTString mExponent;

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTElement, com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public Iterable<? extends MTString> getChildren() {
        return Arrays.asList(this.mExponent);
    }

    public MTString getExponent() {
        return this.mExponent;
    }

    public MTPower() {
        this.mExponent = new MTString(this);
    }

    public MTPower(Iterable<? extends MTElement> exponentElements) {
        this();
        if (exponentElements != null) {
            this.mExponent.appendElements(exponentElements);
        }
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public MTMeasures measureWithContext(MTMeasureContext context) {
        MTMeasures measures = MTCommonMeasures.measuresForBaselineShift(this, MTBaselineShiftType.Superscript, this.mExponent, context);
        RectF lineTextBounds = context.getFont().genericLineBounds();
        RectF lineTextBounds2 = RectUtil.setWidth(RectUtil.setOrigin(lineTextBounds, measures.getBounds().left, lineTextBounds.top), measures.getBounds().width());
        lineTextBounds2.union(measures.getBounds());
        measures.setBounds(lineTextBounds2);
        return measures;
    }

    @Override // java.lang.Object
    public String toString() {
        return "^(" + this.mExponent + ")";
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTElement, com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public MTPower copy() {
        MTPower copy = new MTPower();
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
        if (!(other instanceof MTPower) || !this.mExponent.equivalentTo(((MTPower) other).mExponent)) {
            return false;
        }
        return true;
    }
}
