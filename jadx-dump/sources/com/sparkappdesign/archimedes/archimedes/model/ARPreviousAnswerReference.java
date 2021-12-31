package com.sparkappdesign.archimedes.archimedes.model;

import com.sparkappdesign.archimedes.mathexpression.enums.MEExpressionForm;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression;
import com.sparkappdesign.archimedes.utilities.GeneralUtil;
import com.sparkappdesign.archimedes.utilities.events.ObserverType;
import com.sparkappdesign.archimedes.utilities.observables.ImmutableList;
import com.sparkappdesign.archimedes.utilities.observables.ObservableChainLink;
import com.sparkappdesign.archimedes.utilities.observables.ObservableList;
import com.sparkappdesign.archimedes.utilities.observables.ValueObserver;
import java.io.Serializable;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class ARPreviousAnswerReference extends ARReference implements Serializable {
    private transient ARCalculation mCalculation;
    private transient ARCalculationList mCalculationList;

    public ARCalculation getCalculation() {
        return this.mCalculation;
    }

    public void setCalculation(ARCalculation calculation) {
        this.mCalculation = calculation;
    }

    public ARCalculationList getCalculationList() {
        return this.mCalculationList;
    }

    public void setCalculationList(ARCalculationList calculationList) {
        this.mCalculationList = calculationList;
    }

    private ARPreviousAnswerReference() {
    }

    public ARPreviousAnswerReference(ARCalculation calculation, ARCalculationList calculationList) {
        this.mCalculationList = calculationList;
        this.mCalculation = calculation;
    }

    @Override // com.sparkappdesign.archimedes.archimedes.model.ARReference, com.sparkappdesign.archimedes.mathtype.MTMEPlaceholderIdentifier
    public ArrayList<MEExpression> expressionsForForm(MEExpressionForm form) {
        ImmutableList expressions;
        int indexBeforeCalculation = this.mCalculationList.getCalculations().indexOf(this.mCalculation) - 1;
        if (indexBeforeCalculation < 0 || (expressions = this.mCalculationList.getCalculations().get(indexBeforeCalculation).answerForForm(form).getLines().getExpressions().getValue()) == null) {
            return null;
        }
        return new ArrayList<>(expressions);
    }

    @Override // com.sparkappdesign.archimedes.archimedes.model.ARReference
    public ArrayList<ARObserver> createExpressionDependencyObserversForForm(MEExpressionForm form) {
        ArrayList<ARObserver> result = new ArrayList<>();
        result.add(new ARPreviousAnswerObserver(form, this.mCalculation, this.mCalculationList));
        return result;
    }

    @Override // java.lang.Object
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ARPreviousAnswerReference)) {
            return false;
        }
        ARPreviousAnswerReference otherReference = (ARPreviousAnswerReference) other;
        return this.mCalculationList == otherReference.mCalculationList && this.mCalculation == otherReference.mCalculation;
    }

    @Override // java.lang.Object
    public int hashCode() {
        return GeneralUtil.hashCode(this.mCalculationList) ^ GeneralUtil.hashCode(this.mCalculation);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ARPreviousAnswerObserver extends ARObserver {
        private ObserverType mObserver;

        public ARPreviousAnswerObserver(final MEExpressionForm answerForm, final ARCalculation calculation, ARCalculationList calculationList) {
            this.mObserver = calculationList.getCalculations().chain(new ObservableChainLink<ImmutableList<ARCalculation>, ImmutableList<MEExpression>>() { // from class: com.sparkappdesign.archimedes.archimedes.model.ARPreviousAnswerReference.ARPreviousAnswerObserver.2
                public ObservableList<MEExpression> get(ImmutableList<ARCalculation> base) {
                    int indexOfCalculation = base.indexOf(calculation);
                    if (indexOfCalculation >= 1) {
                        return base.get(indexOfCalculation - 1).answerForForm(answerForm).getLines().getExpressions();
                    }
                    return null;
                }
            }).addObserver(new ValueObserver<ImmutableList<MEExpression>>() { // from class: com.sparkappdesign.archimedes.archimedes.model.ARPreviousAnswerReference.ARPreviousAnswerObserver.1
                public void handle(ImmutableList<MEExpression> newValue) {
                    ARPreviousAnswerObserver.this.notifyDidChange();
                }
            });
        }

        @Override // com.sparkappdesign.archimedes.utilities.events.ObserverType
        public void remove() {
            this.mObserver.remove();
        }
    }
}
