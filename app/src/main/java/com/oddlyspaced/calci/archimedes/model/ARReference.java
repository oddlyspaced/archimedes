package com.sparkappdesign.archimedes.archimedes.model;

import com.sparkappdesign.archimedes.mathexpression.enums.MEExpressionForm;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEPlaceholder;
import com.sparkappdesign.archimedes.mathtype.MTMEPlaceholderIdentifier;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTReference;
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

    @Override // com.sparkappdesign.archimedes.mathtype.MTMEPlaceholderIdentifier
    public ArrayList<MEExpression> expressionsForForm(MEExpressionForm form) {
        return null;
    }

    public ArrayList<ARObserver> createExpressionDependencyObserversForForm(MEExpressionForm form) {
        return null;
    }
}
