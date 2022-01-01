package com.sparkappdesign.archimedes.mathtype.nodes.elements;

import com.sparkappdesign.archimedes.mathtype.measures.MTCommonMeasures;
import com.sparkappdesign.archimedes.mathtype.measures.MTMeasureContext;
import com.sparkappdesign.archimedes.mathtype.measures.MTMeasures;
import com.sparkappdesign.archimedes.mathtype.nodes.MTElement;
import com.sparkappdesign.archimedes.mathtype.nodes.MTString;
import java.util.Arrays;
/* loaded from: classes.dex */
public class MTDivision extends MTElement {
    private MTString mDividend;
    private MTString mDivisor;

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTElement, com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public Iterable<? extends MTString> getChildren() {
        return Arrays.asList(this.mDividend, this.mDivisor);
    }

    public MTString getDividend() {
        return this.mDividend;
    }

    public MTString getDivisor() {
        return this.mDivisor;
    }

    public MTDivision() {
        this.mDividend = new MTString(this);
        this.mDivisor = new MTString(this);
    }

    public MTDivision(Iterable<? extends MTElement> dividendElements, Iterable<? extends MTElement> divisorElements) {
        this();
        if (dividendElements != null) {
            this.mDividend.appendElements(dividendElements);
        }
        if (divisorElements != null) {
            this.mDivisor.appendElements(divisorElements);
        }
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public MTMeasures measureWithContext(MTMeasureContext context) {
        return MTCommonMeasures.measuresForDivision(this, this.mDividend, this.mDivisor, context);
    }

    @Override // java.lang.Object
    public String toString() {
        return "(" + this.mDividend + "/" + this.mDivisor + ")";
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTElement, com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public MTDivision copy() {
        MTDivision copy = new MTDivision();
        copy.mTraits = this.mTraits.clone();
        copy.mDividend = this.mDividend.copy();
        copy.mDividend.mParent = copy;
        copy.mDivisor = this.mDivisor.copy();
        copy.mDivisor.mParent = copy;
        return copy;
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public boolean equivalentTo(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof MTDivision) {
            MTDivision otherDivision = (MTDivision) other;
            if (this.mDividend.equivalentTo(otherDivision.mDividend) && this.mDivisor.equivalentTo(otherDivision.mDivisor)) {
                return true;
            }
        }
        return false;
    }
}
