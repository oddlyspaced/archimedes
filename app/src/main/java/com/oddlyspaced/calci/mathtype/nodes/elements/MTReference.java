package com.oddlyspaced.calci.mathtype.nodes.elements;

import com.oddlyspaced.calci.mathexpression.enums.MEExpressionForm;
import com.oddlyspaced.calci.mathexpression.expressions.MEExpression;
import com.oddlyspaced.calci.mathtype.MTMEPlaceholderIdentifier;
import com.oddlyspaced.calci.mathtype.enums.MTNodeTraits;
import com.oddlyspaced.calci.mathtype.measures.MTCommonMeasures;
import com.oddlyspaced.calci.mathtype.measures.MTMeasureContext;
import com.oddlyspaced.calci.mathtype.measures.MTMeasures;
import com.oddlyspaced.calci.mathtype.nodes.MTElement;
import com.oddlyspaced.calci.mathtype.nodes.MTString;
import com.oddlyspaced.calci.utilities.GeneralUtil;
import java.util.ArrayList;
import java.util.EnumSet;
/* loaded from: classes.dex */
public class MTReference extends MTElement implements MTMEPlaceholderIdentifier {
    private MTMEPlaceholderIdentifier mIdentifier;
    private MTString mName;

    public MTString getName() {
        return this.mName;
    }

    public MTMEPlaceholderIdentifier getIdentifier() {
        return this.mIdentifier;
    }

    private MTReference() {
        this.mName = new MTString(this);
        this.mName.mTraits = EnumSet.of(MTNodeTraits.CantSelectOrEditChildren);
    }

    public MTReference(Iterable<? extends MTElement> nameElements, MTMEPlaceholderIdentifier identifier) {
        this();
        this.mName.appendElements(nameElements);
        this.mIdentifier = identifier;
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
    public MTReference copy() {
        MTReference copy = new MTReference();
        copy.mName = this.mName.copy();
        copy.mName.mParent = copy;
        copy.mIdentifier = this.mIdentifier;
        return copy;
    }

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTNode
    public boolean equivalentTo(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof MTReference) {
            MTReference otherReference = (MTReference) other;
            if (this.mName.equivalentTo(otherReference.mName) && GeneralUtil.equalOrBothNull(this.mIdentifier, otherReference.mIdentifier)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.oddlyspaced.calci.mathtype.MTMEPlaceholderIdentifier
    public ArrayList<MEExpression> expressionsForForm(MEExpressionForm form) {
        return this.mIdentifier.expressionsForForm(form);
    }
}
