package com.oddlyspaced.calci.archimedes.model;

import com.oddlyspaced.calci.mathexpression.expressions.MEExpression;
import java.util.ArrayList;
/* loaded from: classes.dex */
public interface ARCalculationDelegate {
    void calculationDidUpdateAnswer(ARCalculation aRCalculation, ARAnswer aRAnswer);

    void calculationWillUpdateAnswer(ARCalculation aRCalculation, ARAnswer aRAnswer);

    ARCalculationOperation createCalculationOperation(ARCalculation aRCalculation, ARAnswer aRAnswer, ArrayList<MEExpression> arrayList);
}
