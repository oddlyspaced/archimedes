package com.oddlyspaced.calci.mathtype.nodes.elements;

import com.oddlyspaced.calci.mathtype.enums.MTNodeTraits;
import com.oddlyspaced.calci.mathtype.measures.MTCommonMeasures;
import com.oddlyspaced.calci.mathtype.measures.MTMeasureContext;
import com.oddlyspaced.calci.mathtype.measures.MTMeasures;
import com.oddlyspaced.calci.mathtype.nodes.MTElement;
import com.oddlyspaced.calci.mathtype.nodes.MTString;
import java.util.Arrays;
import java.util.EnumSet;
/* loaded from: classes.dex */
public class MTVariable extends MTElement {
    private MTString mName;

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTElement, com.oddlyspaced.calci.mathtype.nodes.MTNode
    public Iterable<? extends MTString> getChildren() {
        return Arrays.asList(this.mName);
    }

    public MTString getName() {
        return this.mName;
    }

    public MTVariable() {
        this.mName = new MTString(this);
        this.mName.mTraits = EnumSet.of(MTNodeTraits.CantSelectOrEditChildren);
    }

    public MTVariable(String text) {
        this(Arrays.asList(new MTText(text)));
    }

    public MTVariable(Iterable<? extends MTElement> nameElements) {
        this();
        if (nameElements != null) {
            this.mName.appendElements(nameElements);
        }
    }

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTNode
    public MTMeasures measureWithContext(MTMeasureContext context) {
        return MTCommonMeasures.measuresForContainer(this, this.mName, context);
    }

    @Override // java.lang.Object
    public String toString() {
        return this.mName.toString();
    }

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTElement, com.oddlyspaced.calci.mathtype.nodes.MTNode
    public MTVariable copy() {
        MTVariable copy = new MTVariable();
        copy.mTraits = this.mTraits.clone();
        copy.mName = this.mName.copy();
        copy.mName.mParent = copy;
        return copy;
    }

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTNode
    public boolean equivalentTo(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MTVariable) || !this.mName.equivalentTo(((MTVariable) other).mName)) {
            return false;
        }
        return true;
    }
}
