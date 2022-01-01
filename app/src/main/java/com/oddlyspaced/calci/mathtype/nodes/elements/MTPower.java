package com.oddlyspaced.calci.mathtype.nodes.elements;

import android.graphics.RectF;
import com.oddlyspaced.calci.mathtype.enums.MTBaselineShiftType;
import com.oddlyspaced.calci.mathtype.measures.MTCommonMeasures;
import com.oddlyspaced.calci.mathtype.measures.MTMeasureContext;
import com.oddlyspaced.calci.mathtype.measures.MTMeasures;
import com.oddlyspaced.calci.mathtype.nodes.MTElement;
import com.oddlyspaced.calci.mathtype.nodes.MTString;
import com.oddlyspaced.calci.utilities.RectUtil;
import java.util.Arrays;
/* loaded from: classes.dex */
public class MTPower extends MTElement {
    private MTString mExponent;

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTElement, com.oddlyspaced.calci.mathtype.nodes.MTNode
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

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTNode
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

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTElement, com.oddlyspaced.calci.mathtype.nodes.MTNode
    public MTPower copy() {
        MTPower copy = new MTPower();
        copy.mTraits = this.mTraits.clone();
        copy.mExponent = this.mExponent.copy();
        copy.mExponent.mParent = copy;
        return copy;
    }

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTNode
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
