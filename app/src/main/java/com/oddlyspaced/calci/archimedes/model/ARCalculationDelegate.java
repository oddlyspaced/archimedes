package com.sparkappdesign.archimedes.archimedes.model;

import com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression;
import java.util.ArrayList;
/* loaded from: classes.dex */
public interface ARCalculationDelegate {
    void calculationDidUpdateAnswer(ARCalculation aRCalculation, ARAnswer aRAnswer);

    void calculationWillUpdateAnswer(ARCalculation aRCalculation, ARAnswer aRAnswer);

    ARCalculationOperation createCalculationOperation(ARCalculation aRCalculation, ARAnswer aRAnswer, ArrayList<MEExpression> arrayList);
}
