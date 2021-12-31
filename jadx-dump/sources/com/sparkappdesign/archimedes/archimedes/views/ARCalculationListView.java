package com.sparkappdesign.archimedes.archimedes.views;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.sparkappdesign.archimedes.R;
import com.sparkappdesign.archimedes.archimedes.enums.ARCalculationListViewAlignment;
import com.sparkappdesign.archimedes.archimedes.enums.ARScrollPriority;
import com.sparkappdesign.archimedes.archimedes.model.ARCalculation;
import com.sparkappdesign.archimedes.archimedes.model.ARCalculationList;
import com.sparkappdesign.archimedes.archimedes.model.ARPreviousAnswerReference;
import com.sparkappdesign.archimedes.archimedes.model.ARSettings;
import com.sparkappdesign.archimedes.mathtype.MTMEPlaceholderIdentifier;
import com.sparkappdesign.archimedes.mathtype.nodes.MTNode;
import com.sparkappdesign.archimedes.mathtype.nodes.MTString;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTReference;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTText;
import com.sparkappdesign.archimedes.mathtype.views.MTMathTypeView;
import com.sparkappdesign.archimedes.mathtype.views.input.MTMessageType;
import com.sparkappdesign.archimedes.mathtype.views.selection.MTSelection;
import com.sparkappdesign.archimedes.utilities.GeneralUtil;
import com.sparkappdesign.archimedes.utilities.ViewUtil;
import com.sparkappdesign.archimedes.utilities.events.Observer;
import com.sparkappdesign.archimedes.utilities.events.ObserverType;
import com.sparkappdesign.archimedes.utilities.observables.ImmutableList;
import com.sparkappdesign.archimedes.utilities.observables.ListObserver;
import com.sparkappdesign.archimedes.utilities.observables.MutableObservable;
import com.sparkappdesign.archimedes.utilities.observables.ObservableChainLink;
import com.sparkappdesign.archimedes.utilities.observables.ObservableChange;
import com.sparkappdesign.archimedes.utilities.observables.ObservableList;
import com.sparkappdesign.archimedes.utilities.responder.Responder;
import com.sparkappdesign.archimedes.utilities.responder.ResponderManager;
import com.sparkappdesign.archimedes.utilities.responder.ResponderMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
/* loaded from: classes.dex */
public class ARCalculationListView extends ViewGroup implements Responder {
    private int CALCULATION_SPACING;
    private ARCalculation mActiveCalculation;
    private ARCalculationView mActiveCalculationView;
    private ARLineSetView mActiveInputLinesView;
    private MTMathTypeView mActiveMathTypeView;
    private ARCalculationListViewAlignment mAlignment;
    private ARAutoScrollView mAutoScrollView;
    private MutableObservable<ARCalculationList> mCalculationList;
    private ARStackView mCalculationsStackView;
    private boolean mFirstLaunch;
    private ArrayList<ObserverType> mObservers;

    public MTSelection getSelection() {
        if (this.mActiveMathTypeView != null) {
            return this.mActiveMathTypeView.getSelection();
        }
        return null;
    }

    public ARCalculationList getCalculationList() {
        return this.mCalculationList.getValue();
    }

    public void setCalculationList(ARCalculationList calculationList) {
        this.mCalculationList.setValue(calculationList);
    }

    public ARCalculationListViewAlignment getAlignment() {
        return this.mAlignment;
    }

    public void setAlignment(ARCalculationListViewAlignment alignment) {
        if (this.mAlignment != alignment) {
            this.mAlignment = alignment;
            if (alignment == ARCalculationListViewAlignment.Bottom || alignment == ARCalculationListViewAlignment.Center) {
            }
        }
    }

    public ARAutoScrollView getAutoScrollView() {
        return this.mAutoScrollView;
    }

    private ObservableList<ARCalculation> getCalculations() {
        return this.mCalculationList.getValue().getCalculations();
    }

    private void setActiveMathTypeView(MTMathTypeView activeMathTypeView) {
        ARCalculation aRCalculation;
        ARCalculationView aRCalculationView;
        ARLineSetView aRLineSetView = null;
        if (this.mActiveMathTypeView != activeMathTypeView) {
            this.mActiveMathTypeView = activeMathTypeView;
            if (this.mActiveMathTypeView != null) {
                aRCalculation = this.mCalculationList.getValue().getCalculationContainingLine(getActiveLine());
            } else {
                aRCalculation = null;
            }
            this.mActiveCalculation = aRCalculation;
            if (this.mActiveCalculation != null) {
                aRCalculationView = getCalculationViewForCalculation(this.mActiveCalculation);
            } else {
                aRCalculationView = null;
            }
            this.mActiveCalculationView = aRCalculationView;
            if (this.mActiveCalculationView != null) {
                aRLineSetView = this.mActiveCalculationView.getInputLinesView();
            }
            this.mActiveInputLinesView = aRLineSetView;
            this.mAutoScrollView.setActiveItem(this.mActiveCalculationView);
        }
    }

    private MTString getActiveLine() {
        return this.mActiveMathTypeView.getString();
    }

    private ImmutableList<MTString> getActiveInputLines() {
        return (ImmutableList) this.mActiveCalculation.getInputLines().getStrings().getValue();
    }

    public int getActiveCalculationIndex() {
        if (this.mCalculationList.getValue() != null) {
            return this.mCalculationList.getValue().getCalculations().indexOf(this.mActiveCalculation);
        }
        return -1;
    }

    private ARCalculationView getCalculationViewForCalculation(ARCalculation calculation) {
        Iterator<View> it = this.mCalculationsStackView.getLines().iterator();
        while (it.hasNext()) {
            ARCalculationView calculationView = (ARCalculationView) it.next();
            if (calculationView.getCalculation() == calculation) {
                return calculationView;
            }
        }
        return null;
    }

    public MTMathTypeView getMathTypeViewForLine(MTString line) {
        return getCalculationViewForCalculation(this.mCalculationList.getValue().getCalculationContainingLine(line)).getMathTypeViewForLine(line);
    }

    public ARCalculationListView(Context context) {
        super(context);
        this.mObservers = new ArrayList<>();
        this.mCalculationList = new MutableObservable<>();
        this.mFirstLaunch = true;
        this.CALCULATION_SPACING = (int) getResources().getDimension(R.dimen.calculation_spacing);
        this.mAlignment = ARCalculationListViewAlignment.Bottom;
        ARAutoScrollView autoScrollView = new ARAutoScrollView(context);
        autoScrollView.setId(View.generateViewId());
        addView(autoScrollView);
        this.mAutoScrollView = autoScrollView;
        ARStackView calculationsStackView = new ARStackView(context);
        autoScrollView.addView(calculationsStackView);
        calculationsStackView.setSpacing(this.CALCULATION_SPACING);
        this.mCalculationsStackView = calculationsStackView;
        this.mObservers.add(this.mCalculationList.chain(new ObservableChainLink<ARCalculationList, ImmutableList<ARCalculation>>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARCalculationListView.2
            public MutableObservable<ImmutableList<ARCalculation>> get(ARCalculationList calculationList) {
                if (calculationList != null) {
                    return calculationList.getCalculations();
                }
                return null;
            }
        }).addObserver(new ListObserver<ARCalculation>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARCalculationListView.1
            public void handleAdd(ARCalculation calculation, int index) {
                ARCalculationListView.this.handleAddedCalculation(calculation, index);
            }

            public void handleRemove(ARCalculation calculation, int index) {
                ARCalculationListView.this.handleRemovedCalculation(calculation, index);
            }
        }));
        this.mObservers.add(ResponderManager.getFirstResponder().addObserver(new Observer<ObservableChange<Responder>>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARCalculationListView.3
            public void handle(ObservableChange<Responder> change) {
                ARCalculationListView.this.handleFirstResponderChanged(change);
            }
        }));
    }

    private ARCalculationListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mObservers = new ArrayList<>();
        this.mCalculationList = new MutableObservable<>();
        this.mFirstLaunch = true;
        this.CALCULATION_SPACING = (int) getResources().getDimension(R.dimen.calculation_spacing);
    }

    private ARCalculationListView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.mObservers = new ArrayList<>();
        this.mCalculationList = new MutableObservable<>();
        this.mFirstLaunch = true;
        this.CALCULATION_SPACING = (int) getResources().getDimension(R.dimen.calculation_spacing);
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("savedInstanceState", super.onSaveInstanceState());
        bundle.putInt("autoScrollViewId", this.mAutoScrollView.getId());
        ArrayList<Integer> calculationViewIds = new ArrayList<>();
        Iterator<View> it = this.mCalculationsStackView.getLines().iterator();
        while (it.hasNext()) {
            calculationViewIds.add(Integer.valueOf(((ARCalculationView) it.next()).getId()));
        }
        bundle.putIntegerArrayList("calculationViewIds", calculationViewIds);
        return bundle;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable("savedInstanceState"));
        this.mAutoScrollView.setId(bundle.getInt("autoScrollViewId"));
        ArrayList<Integer> calculationViewIds = bundle.getIntegerArrayList("calculationViewIds");
        for (int i = 0; i < this.mCalculationsStackView.getLines().size(); i++) {
            ((ARCalculationView) this.mCalculationsStackView.getLines().get(i)).setId(calculationViewIds.get(i).intValue());
        }
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        this.mAutoScrollView.measure(View.MeasureSpec.makeMeasureSpec(parentWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(parentHeight, 1073741824));
        setMeasuredDimension(this.mAutoScrollView.getMeasuredWidth(), this.mAutoScrollView.getMeasuredHeight());
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int t2 = 0;
        int r2 = this.mAutoScrollView.getMeasuredWidth();
        int b2 = this.mAutoScrollView.getMeasuredHeight();
        if (this.mAlignment == ARCalculationListViewAlignment.Bottom && this.mCalculationsStackView.getMeasuredHeight() < this.mAutoScrollView.getMeasuredHeight()) {
            t2 = this.mAutoScrollView.getMeasuredHeight() - this.mCalculationsStackView.getMeasuredHeight();
        }
        this.mAutoScrollView.layout(0, t2, r2, b2);
    }

    public void deinitialize() {
        Iterator<ObserverType> it = this.mObservers.iterator();
        while (it.hasNext()) {
            it.next().remove();
        }
        Iterator<View> it2 = this.mCalculationsStackView.getLines().iterator();
        while (it2.hasNext()) {
            ((ARCalculationView) it2.next()).deinitialize();
        }
        this.mCalculationsStackView.deinitialize();
    }

    public void handleAddedCalculation(ARCalculation calculation, int index) {
        ARCalculationView calculationView = new ARCalculationView(getContext());
        calculationView.setCalculation(calculation);
        calculationView.setId(View.generateViewId());
        this.mCalculationsStackView.insertLine(calculationView, index, true);
        this.mAutoScrollView.addItem(calculationView);
        requestLayout();
    }

    public void handleRemovedCalculation(ARCalculation calculation, int index) {
        ARCalculationView calculationView = (ARCalculationView) this.mCalculationsStackView.getLines().get(index);
        this.mCalculationsStackView.removeLine(index, true);
        this.mAutoScrollView.removeItem(calculationView);
        calculationView.deinitialize();
    }

    public void setSelection(MTSelection selection, boolean animated) {
        MTNode rootNode = selection.getString().rootNode();
        getMathTypeViewForLine(rootNode instanceof MTString ? (MTString) rootNode : null).setSelection(selection, animated);
    }

    private boolean isCalculationEmpty(ARCalculation calculation) {
        Iterator<MTString> it = calculation.getInputLines().getStrings().iterator();
        while (it.hasNext()) {
            if (it.next().isNotEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void performActionBackspace() {
        if (this.mActiveMathTypeView != null) {
            backspaceInternal(true);
            new ResponderMessage(MTMessageType.DID_PERFORM_EDIT, null).send();
        }
    }

    private void backspaceInternal(boolean animated) {
        ARCalculation activeCalculation = this.mActiveCalculation;
        if (isCalculationEmpty(activeCalculation)) {
            ObservableList<ARCalculation> calculations = getCalculations();
            if (activeCalculation != calculations.get(0)) {
                ARCalculation calculationBefore = calculations.get(calculations.indexOf(activeCalculation) - 1);
                calculations.remove(activeCalculation);
                activeCalculation.deinitialize();
                setSelection(MTSelection.cursorAtEndOfString(calculationBefore.getInputLines().getStrings().get(calculationBefore.getInputLines().getStrings().size() - 1)), animated);
                return;
            }
            return;
        }
        this.mActiveInputLinesView.backspace(animated);
    }

    private void performActionClearAll() {
        clearAllInternal(false);
        new ResponderMessage(MTMessageType.DID_PERFORM_EDIT, null).send();
    }

    private void clearAllInternal(boolean animated) {
        ObservableList<ARCalculation> calculations = getCalculations();
        ARCalculation calculationToKeep = this.mActiveCalculation != null ? this.mActiveCalculation : calculations.get(calculations.size() - 1);
        ArrayList<ARCalculation> calculationsToRemove = new ArrayList<>();
        Iterator<ARCalculation> it = calculations.iterator();
        while (it.hasNext()) {
            ARCalculation calculation = it.next();
            if (calculation != calculationToKeep) {
                calculation.deinitialize();
                calculationsToRemove.add(calculation);
            }
        }
        calculations.removeAll(calculationsToRemove);
        this.mActiveInputLinesView.clearAll(animated);
        if (this.mActiveMathTypeView == null) {
            MTMathTypeView mathTypeView = getMathTypeViewForLine(calculationToKeep.getInputLines().getStrings().get(0));
            mathTypeView.setSelection(MTSelection.cursorAtEndOfString(mathTypeView.getString()), true);
        }
    }

    private void performActionEnter() {
        if (this.mActiveMathTypeView != null) {
            enterInternal(true);
            new ResponderMessage(MTMessageType.DID_PERFORM_EDIT, null).send();
        }
    }

    private void enterInternal(boolean animated) {
        if (getActiveInputLines().contains(getActiveLine())) {
            if (!(getActiveLine() == getActiveInputLines().get(getActiveInputLines().size() + -1)) || (getActiveLine().isNotEmpty() && numberOfExtraLinesNeededForCalculation(this.mActiveCalculation) > 0)) {
                this.mActiveInputLinesView.enter(animated);
                return;
            }
        }
        if (!isCalculationEmpty(this.mActiveCalculation)) {
            insertCalculation(animated);
        }
    }

    private ARCalculation insertCalculation(boolean animated) {
        ARCalculation newCalculation = new ARCalculation();
        getCalculations().add(getCalculations().indexOf(this.mActiveCalculation) + 1, newCalculation);
        setSelection(MTSelection.cursorAtEndOfString(newCalculation.getInputLines().getStrings().get(newCalculation.getInputLines().getStrings().size() - 1)), animated);
        return newCalculation;
    }

    private int numberOfExtraLinesNeededForCalculation(ARCalculation calculation) {
        return GeneralUtil.constrainMin(calculation.getInputLines().getVariableCount() - calculation.getInputLines().getStrings().size(), 0);
    }

    private void performActionInsertPreviousAnswerReference() {
        if (this.mActiveMathTypeView != null) {
            Object reference = previousAnswerReference();
            HashMap<String, Object> messageContents = new HashMap<>();
            messageContents.put("Element to insert", reference);
            new ResponderMessage(MTMessageType.INSERT_ELEMENT, messageContents).send();
        }
    }

    private boolean autoInsertPreviousAnswerReference() {
        if (!ARSettings.sharedSettings(getContext()).shouldAutoInsertAns() || getActiveInputLines().size() != 1 || getActiveLine().isNotEmpty()) {
            return false;
        }
        int indexOfCalculationBeforeActiveCalculation = getCalculations().indexOf(this.mActiveCalculation) - 1;
        ARCalculation previousCalculation = null;
        if (indexOfCalculationBeforeActiveCalculation != -1) {
            previousCalculation = getCalculations().get(indexOfCalculationBeforeActiveCalculation);
        }
        if (previousCalculation == null || previousCalculation.getInputLines().getVariableCount() != 0) {
            return false;
        }
        this.mActiveMathTypeView.insertElement(previousAnswerReference(), false);
        return true;
    }

    private MTReference previousAnswerReference() {
        return new MTReference(Arrays.asList(new MTText("ans")), new ARPreviousAnswerReference(this.mActiveCalculation, this.mCalculationList.getValue()));
    }

    private void updateReference(MTReference reference) {
        MTMEPlaceholderIdentifier identifier = reference.getIdentifier();
        if (identifier instanceof ARPreviousAnswerReference) {
            ARPreviousAnswerReference previousAnswerReference = (ARPreviousAnswerReference) identifier;
            previousAnswerReference.setCalculation(this.mCalculationList.getValue().getCalculationContainingLine(reference.rootNode() instanceof MTString ? (MTString) reference.rootNode() : null));
            previousAnswerReference.setCalculationList(this.mCalculationList.getValue());
        }
    }

    public void handleFirstResponderChanged(ObservableChange<Responder> change) {
        handleResponderResignedFirstResponder(change.getOldValue(), change.getNewValue());
        handleResponderBecameFirstResponder(change.getNewValue());
    }

    private void handleResponderResignedFirstResponder(Responder oldFirstResponder, Responder newFirstResponder) {
        MTMathTypeView oldMathTypeView;
        MTMathTypeView newMathTypeView;
        if (oldFirstResponder instanceof MTMathTypeView) {
            oldMathTypeView = (MTMathTypeView) oldFirstResponder;
        } else {
            oldMathTypeView = null;
        }
        if (newFirstResponder instanceof MTMathTypeView) {
            newMathTypeView = (MTMathTypeView) newFirstResponder;
        } else {
            newMathTypeView = null;
        }
        if (oldMathTypeView != null && oldMathTypeView == this.mActiveMathTypeView) {
            setActiveMathTypeView(null);
            if (newMathTypeView != null) {
                ARCalculation oldCalculation = this.mCalculationList.getValue().getCalculationContainingLine(oldMathTypeView.getString());
                ARCalculation newCalculation = this.mCalculationList.getValue().getCalculationContainingLine(newMathTypeView.getString());
                if (oldCalculation != null && newCalculation != oldCalculation) {
                    removeCalculationIfEmpty(oldCalculation, true);
                }
            }
        }
    }

    private void handleResponderBecameFirstResponder(Responder responder) {
        if (responder != null && (responder instanceof MTMathTypeView)) {
            MTMathTypeView mathTypeView = (MTMathTypeView) responder;
            if (ViewUtil.isDescendant(mathTypeView, this)) {
                setActiveMathTypeView(mathTypeView);
            }
        }
    }

    private void removeCalculationIfEmpty(ARCalculation calculation, boolean animated) {
        removeCalculationIfEmptyInternal(calculation, animated);
    }

    private void removeCalculationIfEmptyInternal(ARCalculation calculation, boolean animated) {
        if (isCalculationEmpty(calculation) && getCalculations().size() > 1) {
            getCalculations().remove(calculation);
            calculation.deinitialize();
        }
    }

    private void handleDidPerformEditAction() {
        post(new Runnable() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARCalculationListView.4
            @Override // java.lang.Runnable
            public void run() {
                ARCalculationListView.this.mAutoScrollView.scrollToAreaOfInterest(MTMathTypeView.AREAS_OF_INTEREST_ON_EDIT, ARScrollPriority.RespectManualScrollOverAreasOfInterest, true);
            }
        });
    }

    @Override // com.sparkappdesign.archimedes.utilities.responder.Responder
    public boolean canHandleMessageType(String type) {
        return type.equals(MTMessageType.BACKSPACE) || type.equals(MTMessageType.CLEAR_ALL) || type.equals(MTMessageType.ENTER) || type.equals(MTMessageType.INSERT_ANS) || type.equals(MTMessageType.SHOULD_AUTO_INSERT_ANS) || type.equals(MTMessageType.DID_PERFORM_EDIT) || type.equals(MTMessageType.UPDATE_REFERENCE);
    }

    @Override // com.sparkappdesign.archimedes.utilities.responder.Responder
    public Responder getAncestor() {
        ViewParent parent = getParent();
        if (parent instanceof Responder) {
            return (Responder) parent;
        }
        return null;
    }

    @Override // com.sparkappdesign.archimedes.utilities.responder.Responder
    public boolean isChildAllowedToHandleMessage(Responder child, ResponderMessage message) {
        return !canHandleMessageType(message.getType());
    }

    @Override // com.sparkappdesign.archimedes.utilities.responder.Responder
    public void handleMessage(String type, HashMap<String, Object> contents) {
        char c = 65535;
        switch (type.hashCode()) {
            case -398868440:
                if (type.equals(MTMessageType.DID_PERFORM_EDIT)) {
                    c = 5;
                    break;
                }
                break;
            case -154864545:
                if (type.equals(MTMessageType.BACKSPACE)) {
                    c = 0;
                    break;
                }
                break;
            case 66129592:
                if (type.equals(MTMessageType.ENTER)) {
                    c = 2;
                    break;
                }
                break;
            case 1179701045:
                if (type.equals(MTMessageType.UPDATE_REFERENCE)) {
                    c = 6;
                    break;
                }
                break;
            case 1452531520:
                if (type.equals(MTMessageType.INSERT_ANS)) {
                    c = 3;
                    break;
                }
                break;
            case 1516964975:
                if (type.equals(MTMessageType.CLEAR_ALL)) {
                    c = 1;
                    break;
                }
                break;
            case 1947198788:
                if (type.equals(MTMessageType.SHOULD_AUTO_INSERT_ANS)) {
                    c = 4;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                performActionBackspace();
                return;
            case 1:
                performActionClearAll();
                return;
            case 2:
                performActionEnter();
                return;
            case 3:
                performActionInsertPreviousAnswerReference();
                return;
            case 4:
                autoInsertPreviousAnswerReference();
                return;
            case 5:
                handleDidPerformEditAction();
                return;
            case 6:
                updateReference((MTReference) contents.get("Reference to update"));
                return;
            default:
                return;
        }
    }
}
