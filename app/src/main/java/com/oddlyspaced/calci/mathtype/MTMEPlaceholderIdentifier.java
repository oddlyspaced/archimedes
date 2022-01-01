package com.sparkappdesign.archimedes.mathtype;

import com.sparkappdesign.archimedes.mathexpression.enums.MEExpressionForm;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression;
import java.util.ArrayList;
/* loaded from: classes.dex */
public interface MTMEPlaceholderIdentifier {
    ArrayList<MEExpression> expressionsForForm(MEExpressionForm mEExpressionForm);
}
