package com.oddlyspaced.calci.archimedes.model;

import com.oddlyspaced.calci.mathexpression.enums.MEExpressionForm;
import com.oddlyspaced.calci.mathexpression.expressions.MEExpression;
import com.oddlyspaced.calci.mathexpression.expressions.MEPlaceholder;
import com.oddlyspaced.calci.mathtype.MTMEPlaceholderIdentifier;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTReference;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class ARReference implements MTMEPlaceholderIdentifier {
    public static ARReference referenceFromPlaceholder(MEPlaceholder placeholder) {
        Object identifier = placeholder.getIdentifier();
        if (identifier instanceof MTReference) {
            MTMEPlaceholderIdentifier innerIdentifier = ((MTReference) identifier).getIdentifier();
            if (innerIdentifier instanceof ARReference) {
                return (ARReference) innerIdentifier;
            }
            return null;
        }
        return identifier instanceof ARReference ? (ARReference) identifier : null;
    }

    @Override // com.oddlyspaced.calci.mathtype.MTMEPlaceholderIdentifier
    public ArrayList<MEExpression> expressionsForForm(MEExpressionForm form) {
        return null;
    }

    public ArrayList<ARObserver> createExpressionDependencyObserversForForm(MEExpressionForm form) {
        return null;
    }
}
