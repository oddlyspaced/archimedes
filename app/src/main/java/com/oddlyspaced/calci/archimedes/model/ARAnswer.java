package com.oddlyspaced.calci.archimedes.model;

import android.os.Handler;
import com.oddlyspaced.calci.archimedes.enums.ARAnswerState;
import com.oddlyspaced.calci.mathexpression.context.MEIssue;
import com.oddlyspaced.calci.mathexpression.enums.MEExpressionForm;
import com.oddlyspaced.calci.mathexpression.expressions.MEExpression;
import com.oddlyspaced.calci.mathexpression.expressions.MEPlaceholder;
import com.oddlyspaced.calci.mathexpression.expressions.MEVariable;
import com.oddlyspaced.calci.utilities.events.Observer;
import com.oddlyspaced.calci.utilities.events.ObserverType;
import com.oddlyspaced.calci.utilities.observables.ImmutableList;
import com.oddlyspaced.calci.utilities.observables.MutableObservable;
import com.oddlyspaced.calci.utilities.observables.Observable;
import com.oddlyspaced.calci.utilities.observables.ObservableChange;
import com.oddlyspaced.calci.utilities.observables.ObservableChangeGroup;
import com.oddlyspaced.calci.utilities.observables.ObservableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/* loaded from: classes.dex */
public class ARAnswer implements ARObserverDelegate {
    private ARCalculation mCalculation;
    private ARCalculationOperation mCalculationOperation;
    private ArrayList<ARObserver> mDependencyObservers;
    private MEExpressionForm mForm;
    private Handler mHandler;
    private ARLineSet mInputLines;
    private ArrayList<ARReference> mReferences;
    private ARLineSet mLines = new ARLineSet();
    private MutableObservable<ARAnswerState> mState = new MutableObservable<>(ARAnswerState.Normal);
    private ExecutorService mCalculationQueue = Executors.newSingleThreadExecutor();
    private ArrayList<ObserverType> mObservers = new ArrayList<>();
    private MutableObservable<Boolean> mPaused = new MutableObservable<>(false);
    private ObservableList<ARIssue> mIssues = new ObservableList<>();

    public ARLineSet getLines() {
        return this.mLines;
    }

    public ARCalculation getCalculation() {
        return this.mCalculation;
    }

    public MEExpressionForm getForm() {
        return this.mForm;
    }

    public ARLineSet getInputLines() {
        return this.mInputLines;
    }

    public Observable<ImmutableList<ARIssue>> getIssues() {
        return this.mIssues;
    }

    public Observable<ARAnswerState> getState() {
        return this.mState;
    }

    public Observable<Boolean> isPaused() {
        return this.mPaused;
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    public void setDependencyObservers(ArrayList<ARObserver> dependencyObservers) {
        if (this.mDependencyObservers != dependencyObservers) {
            ArrayList<ARObserver> oldObservers = this.mDependencyObservers;
            this.mDependencyObservers = dependencyObservers;
            if (oldObservers != null) {
                Iterator<ARObserver> it = oldObservers.iterator();
                while (it.hasNext()) {
                    ARObserver observer = it.next();
                    observer.setDelegate(null);
                    observer.remove();
                }
            }
            if (dependencyObservers != null) {
                Iterator<ARObserver> it2 = dependencyObservers.iterator();
                while (it2.hasNext()) {
                    it2.next().setDelegate(this);
                }
            }
        }
    }

    public void setPaused(boolean paused) {
        if (this.mPaused.getValue().booleanValue() != paused) {
            this.mPaused.setValue(Boolean.valueOf(paused));
            if (paused && this.mState.getValue() == ARAnswerState.Recalculating) {
                cancelCalculationOperations();
            }
            if (!paused && this.mState.getValue() == ARAnswerState.Recalculating) {
                startCalculationOperation();
            }
        }
    }

    private ARAnswer() {
    }

    public ARAnswer(ARCalculation calculation, MEExpressionForm form) {
        this.mCalculation = calculation;
        this.mForm = form;
        this.mInputLines = calculation.getInputLines();
        this.mObservers.add(this.mInputLines.getExpressions().getWillChange().add(new Observer<ObservableChange<ImmutableList<MEExpression>>>() { // from class: com.oddlyspaced.calci.archimedes.model.ARAnswer.1
            public void handle(ObservableChange<ImmutableList<MEExpression>> change) {
                ARAnswer.this.invalidateInternal(change.getGroup());
            }
        }));
        this.mObservers.add(this.mInputLines.getExpressions().getDidChange().add(new Observer<ObservableChange<ImmutableList<MEExpression>>>() { // from class: com.oddlyspaced.calci.archimedes.model.ARAnswer.2
            public void handle(ObservableChange<ImmutableList<MEExpression>> change) {
                ARAnswer.this.handleInputExpressionsChanged();
            }
        }));
    }

    public void deinitialize() {
        this.mCalculationQueue.shutdownNow();
        Iterator<ObserverType> it = this.mObservers.iterator();
        while (it.hasNext()) {
            it.next().remove();
        }
    }

    public void handleInputExpressionsChanged() {
        updateReferences();
        updateDependencyObservers();
    }

    public void invalidate() {
        ObservableChangeGroup changeGroup = new ObservableChangeGroup();
        invalidateInternal(changeGroup);
        changeGroup.performChanges();
    }

    public void invalidateInternal(ObservableChangeGroup changeGroup) {
        if (this.mState.getValue() != ARAnswerState.Invalidated) {
            if (this.mState.getValue() == ARAnswerState.Recalculating) {
                cancelCalculationOperations();
            }
            changeGroup.setNewValue(this.mLines.getExpressions(), null);
            changeGroup.setNewValue(this.mState, ARAnswerState.Invalidated);
        }
    }

    public void recalculate() {
        invalidate();
        if (!this.mPaused.getValue().booleanValue()) {
            startCalculationOperation();
        }
        this.mState.setValue(ARAnswerState.Recalculating);
    }

    private void startCalculationOperation() {
        if (!this.mCalculationQueue.isShutdown()) {
            cancelCalculationOperations();
            ArrayList<MEExpression> input = resolvedInputExpressions();
            final ARCalculationOperation operation = null;
            if (this.mCalculation.mDelegate != null) {
                operation = this.mCalculation.mDelegate.createCalculationOperation(this.mCalculation, this, input);
            }
            if (operation == null) {
                operation = new ARCalculationOperation(input, this.mForm);
            }
            operation.setHandler(this.mHandler);
            operation.setCompletionRunnable(new Runnable() { // from class: com.oddlyspaced.calci.archimedes.model.ARAnswer.3
                @Override // java.lang.Runnable
                public void run() {
                    if (!operation.isCancelled() && operation == ARAnswer.this.mCalculationOperation) {
                        ARAnswer.this.updateAnswerWithExpressions(operation.getAnswerExpressions(), operation.getIssues());
                    }
                }
            });
            this.mCalculationOperation = operation;
            this.mCalculationQueue.submit(operation);
        }
    }

    private void cancelCalculationOperations() {
        if (this.mCalculationOperation != null) {
            this.mCalculationOperation.cancel();
        }
        this.mCalculationOperation = null;
    }

    public void updateAnswerWithExpressions(ArrayList<MEExpression> expressions, ArrayList<MEIssue> issues) {
        this.mCalculation.mDelegate.calculationWillUpdateAnswer(this.mCalculation, this);
        if (expressions == null || expressions.size() == 0) {
            expressions = new ArrayList<>();
            expressions.add(null);
        }
        this.mLines.getExpressions().setValue((Collection<? extends MEExpression>) expressions);
        ArrayList<ARIssue> newIssues = new ArrayList<>();
        Iterator<MEIssue> it = issues.iterator();
        while (it.hasNext()) {
            newIssues.add(new ARIssue(it.next()));
        }
        this.mIssues.setValue((Collection<? extends ARIssue>) newIssues);
        this.mState.setValue(ARAnswerState.Normal);
        this.mCalculation.mDelegate.calculationDidUpdateAnswer(this.mCalculation, this);
    }

    private void updateReferences() {
        ArrayList<ARReference> references = new ArrayList<>();
        ImmutableList calculationExpressions = this.mCalculation.getInputLines().getExpressions().getValue();
        if (calculationExpressions != null) {
            Iterator<MEExpression> it = new ArrayList<>(calculationExpressions).iterator();
            while (it.hasNext()) {
                createReferencesFromPlaceholders(it.next(), references);
            }
        }
        this.mReferences = references;
    }

    private void createReferencesFromPlaceholders(MEExpression expression, ArrayList<ARReference> references) {
        ARReference reference;
        if (expression != null) {
            if ((expression instanceof MEPlaceholder) && (reference = ARReference.referenceFromPlaceholder((MEPlaceholder) expression)) != null) {
                references.add(reference);
            }
            if (expression.children() != null) {
                Iterator<MEExpression> it = expression.children().iterator();
                while (it.hasNext()) {
                    createReferencesFromPlaceholders(it.next(), references);
                }
            }
        }
    }

    private void updateDependencyObservers() {
        ArrayList<ARObserver> observers = new ArrayList<>();
        Iterator<ARReference> it = this.mReferences.iterator();
        while (it.hasNext()) {
            ArrayList<ARObserver> referenceObservers = it.next().createExpressionDependencyObserversForForm(this.mForm);
            if (referenceObservers != null) {
                observers.addAll(referenceObservers);
            }
        }
        setDependencyObservers(observers);
    }

    private ArrayList<MEExpression> resolvedInputExpressions() {
        HashMap<ARReference, MEExpression> referenceExpressions = new HashMap<>();
        if (this.mReferences != null) {
            Iterator<ARReference> it = this.mReferences.iterator();
            while (it.hasNext()) {
                ARReference reference = it.next();
                ArrayList<MEExpression> expressions = reference.expressionsForForm(this.mForm);
                if (expressions == null || expressions.size() != 1) {
                    return null;
                }
                MEExpression expression = expressions.get(0);
                if (expression == null || expression.containsExpressionOfType(MEVariable.class)) {
                    return null;
                }
                referenceExpressions.put(reference, expression);
            }
        }
        ArrayList<MEExpression> expressions2 = new ArrayList<>();
        ImmutableList calculationExpressions = this.mCalculation.getInputLines().getExpressions().getValue();
        if (calculationExpressions != null) {
            expressions2.addAll(calculationExpressions);
        }
        Iterator<MEExpression> it2 = expressions2.iterator();
        while (it2.hasNext()) {
            MEExpression expression2 = it2.next();
            expressions2.set(expressions2.indexOf(expression2), transformRecursive(expression2, referenceExpressions));
        }
        return expressions2;
    }

    private MEExpression transformRecursive(MEExpression expression, HashMap<ARReference, MEExpression> referenceExpressions) {
        MEExpression newExpression = transform(expression, referenceExpressions);
        ImmutableList<MEExpression> children = newExpression.children();
        if (children == null) {
            return newExpression;
        }
        ArrayList<MEExpression> newChildren = new ArrayList<>(children);
        Iterator<MEExpression> it = newChildren.iterator();
        while (it.hasNext()) {
            MEExpression childExpression = it.next();
            newChildren.set(newChildren.indexOf(childExpression), transformRecursive(childExpression, referenceExpressions));
        }
        return newExpression.copyWithChildren(newChildren);
    }

    private MEExpression transform(MEExpression expression, HashMap<ARReference, MEExpression> referenceExpressions) {
        if (!(expression instanceof MEPlaceholder)) {
            return expression;
        }
        ARReference reference = ARReference.referenceFromPlaceholder((MEPlaceholder) expression);
        if (this.mReferences.contains(reference)) {
            return referenceExpressions.get(reference);
        }
        return expression;
    }

    @Override // com.oddlyspaced.calci.archimedes.model.ARObserverDelegate
    public void observerDidObserveChange(ARObserver observer) {
        if (this.mDependencyObservers.contains(observer)) {
            invalidate();
        }
    }
}
