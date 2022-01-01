package com.oddlyspaced.calci.mathtype.nodes.elements;

import com.oddlyspaced.calci.mathtype.measures.MTCommonMeasures;
import com.oddlyspaced.calci.mathtype.measures.MTMeasureContext;
import com.oddlyspaced.calci.mathtype.measures.MTMeasures;
import com.oddlyspaced.calci.mathtype.nodes.MTElement;
import com.oddlyspaced.calci.mathtype.nodes.MTString;
import java.util.Arrays;
/* loaded from: classes.dex */
public class MTParentheses extends MTElement {
    private MTString mContents;

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTElement, com.oddlyspaced.calci.mathtype.nodes.MTNode
    public Iterable<? extends MTString> getChildren() {
        return Arrays.asList(this.mContents);
    }

    public MTString getContents() {
        return this.mContents;
    }

    public MTParentheses() {
        this.mContents = new MTString(this);
    }

    public MTParentheses(Iterable<? extends MTElement> contentElements) {
        this();
        if (contentElements != null) {
            this.mContents.appendElements(contentElements);
        }
    }

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTNode
    public MTMeasures measureWithContext(MTMeasureContext context) {
        return MTCommonMeasures.measuresForParentheses(this, this.mContents, context);
    }

    @Override // java.lang.Object
    public String toString() {
        return "(" + this.mContents + ")";
    }

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTElement, com.oddlyspaced.calci.mathtype.nodes.MTNode
    public MTParentheses copy() {
        MTParentheses copy = new MTParentheses();
        copy.mTraits = this.mTraits.clone();
        copy.mContents = this.mContents.copy();
        copy.mContents.mParent = copy;
        return copy;
    }

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTNode
    public boolean equivalentTo(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MTParentheses) || !this.mContents.equivalentTo(((MTParentheses) other).mContents)) {
            return false;
        }
        return true;
    }
}
