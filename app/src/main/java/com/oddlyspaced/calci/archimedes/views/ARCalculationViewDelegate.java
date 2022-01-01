package com.oddlyspaced.calci.archimedes.views;

import com.oddlyspaced.calci.archimedes.model.ARAnswer;
import com.oddlyspaced.calci.archimedes.model.ARCalculationOperation;
import com.oddlyspaced.calci.mathexpression.expressions.MEExpression;
import java.util.ArrayList;
/* loaded from: classes.dex */
public interface ARCalculationViewDelegate {
    ARCalculationOperation createCalculationOperation(ARCalculationView aRCalculationView, ARAnswer aRAnswer, ArrayList<MEExpression> arrayList);
}
