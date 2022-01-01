package com.oddlyspaced.calci.mathtype;

import com.oddlyspaced.calci.mathexpression.enums.MEExpressionForm;
import com.oddlyspaced.calci.mathexpression.expressions.MEExpression;
import java.util.ArrayList;
/* loaded from: classes.dex */
public interface MTMEPlaceholderIdentifier {
    ArrayList<MEExpression> expressionsForForm(MEExpressionForm mEExpressionForm);
}
