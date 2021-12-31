package com.sparkappdesign.archimedes.archimedes.views;

import com.sparkappdesign.archimedes.archimedes.model.ARAnswer;
import com.sparkappdesign.archimedes.archimedes.model.ARCalculationOperation;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression;
import java.util.ArrayList;
/* loaded from: classes.dex */
public interface ARCalculationViewDelegate {
    ARCalculationOperation createCalculationOperation(ARCalculationView aRCalculationView, ARAnswer aRAnswer, ArrayList<MEExpression> arrayList);
}
