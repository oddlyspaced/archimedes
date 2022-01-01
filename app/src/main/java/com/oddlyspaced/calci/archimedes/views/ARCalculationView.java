package com.oddlyspaced.calci.archimedes.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.oddlyspaced.calci.R;
import com.oddlyspaced.calci.archimedes.enums.ARCalculationViewStyle;
import com.oddlyspaced.calci.archimedes.enums.ARScrollPriority;
import com.oddlyspaced.calci.archimedes.model.ARAnswer;
import com.oddlyspaced.calci.archimedes.model.ARCalculation;
import com.oddlyspaced.calci.archimedes.model.ARCalculationDelegate;
import com.oddlyspaced.calci.archimedes.model.ARCalculationOperation;
import com.oddlyspaced.calci.archimedes.model.ARSettings;
import com.oddlyspaced.calci.mathexpression.expressions.MEExpression;
import com.oddlyspaced.calci.mathtype.nodes.MTElement;
import com.oddlyspaced.calci.mathtype.nodes.MTNode;
import com.oddlyspaced.calci.mathtype.nodes.MTString;
import com.oddlyspaced.calci.mathtype.parsers.MTParser;
import com.oddlyspaced.calci.mathtype.views.MTMathTypeView;
import com.oddlyspaced.calci.mathtype.views.input.MTMessageType;
import com.oddlyspaced.calci.mathtype.views.selection.MTSelection;
import com.oddlyspaced.calci.mathtype.views.selection.MTSelectionDrawable;
import com.oddlyspaced.calci.mathtype.writers.MTWriter;
import com.oddlyspaced.calci.utilities.GeneralUtil;
import com.oddlyspaced.calci.utilities.RectUtil;
import com.oddlyspaced.calci.utilities.ViewUtil;
import com.oddlyspaced.calci.utilities.events.Observer;
import com.oddlyspaced.calci.utilities.events.ObserverType;
import com.oddlyspaced.calci.utilities.observables.ImmutableList;
import com.oddlyspaced.calci.utilities.observables.ListObserver;
import com.oddlyspaced.calci.utilities.observables.MutableObservable;
import com.oddlyspaced.calci.utilities.observables.Observable;
import com.oddlyspaced.calci.utilities.observables.ObservableChainLink;
import com.oddlyspaced.calci.utilities.observables.ObservableChange;
import com.oddlyspaced.calci.utilities.responder.Responder;
import com.oddlyspaced.calci.utilities.responder.ResponderManager;
import com.oddlyspaced.calci.utilities.responder.ResponderMessage;
import com.oddlyspaced.calci.utilities.responder.ResponderUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
/* loaded from: classes.dex */
public class ARCalculationView extends ViewGroup implements Responder, ARLineSetViewDelegate, ARCalculationDelegate, ARPagerViewDelegate, ARAutoScrollViewItem, ARViewGroup {
    public static final String AREAS_OF_INTEREST_ON_ANSWER_SCROLL = "AREAS_OF_INTEREST_ON_ANSWER_SCROLL";
    public static final String AREAS_OF_INTEREST_ON_UPDATED_ANSWER = "AREAS_OF_INTEREST_ON_UPDATED_ANSWER";
    private static HashSet<String> handledIdentifiers;
    private final float BORDER_THICKNESS;
    private MTMathTypeView mActiveMathTypeView;
    private ARPagerView mAnswersPagerView;
    private ARAutoScrollView mAutoScrollView;
    private Paint mBorderPaint;
    private MutableObservable<ARCalculation> mCalculation;
    private ARCalculationViewDelegate mDelegate;
    private ARLineSetView mInputLinesView;
    private ArrayList<ObserverType> mObservers;
    private ARCalculationViewStyle mStyle;

    private HashSet<String> getHandledIdentifiers() {
        if (handledIdentifiers == null) {
            handledIdentifiers = new HashSet<>();
            handledIdentifiers.add(ARAutoScrollView.AREAS_OF_INTEREST_FOR_MANUAL_SCROLL);
            handledIdentifiers.add(AREAS_OF_INTEREST_ON_ANSWER_SCROLL);
            handledIdentifiers.add(ARAutoScrollView.AREAS_OF_INTEREST_ON_BOUNDS_RESIZE);
            handledIdentifiers.add(MTMathTypeView.AREAS_OF_INTEREST_ON_EDIT);
            handledIdentifiers.add(ARAutoScrollView.AREAS_OF_INTEREST_ON_ITEM_DID_BECOME_ACTIVE);
            handledIdentifiers.add(AREAS_OF_INTEREST_ON_UPDATED_ANSWER);
        }
        return handledIdentifiers;
    }

    public ARCalculationViewDelegate getDelegate() {
        return this.mDelegate;
    }

    public void setDelegate(ARCalculationViewDelegate delegate) {
        this.mDelegate = delegate;
    }

    public ARCalculation getCalculation() {
        return this.mCalculation.getValue();
    }

    public void setCalculation(ARCalculation calculation) {
        if (this.mCalculation.getValue() != calculation) {
            if (this.mCalculation.getValue() != null) {
                this.mCalculation.getValue().setDelegate(null);
            }
            this.mCalculation.setValue(calculation);
            if (calculation != null) {
                calculation.setDelegate(this);
            }
            this.mInputLinesView.setLineSet(calculation.getInputLines());
        }
    }

    public ARCalculationViewStyle getStyle() {
        return this.mStyle;
    }

    public void setStyle(ARCalculationViewStyle style) {
        if (this.mStyle != style) {
            this.mStyle = style;
            updateStyleProperties();
        }
    }

    public ARLineSetView getInputLinesView() {
        return this.mInputLinesView;
    }

    private MTSelection getSelection() {
        return this.mActiveMathTypeView.getSelection();
    }

    private void setSelection(MTSelection selection, boolean animated) {
        MTString line;
        MTMathTypeView mathTypeView;
        MTNode rootNode = selection.getString().rootNode();
        if (rootNode instanceof MTString) {
            line = (MTString) rootNode;
        } else {
            line = null;
        }
        if (line != null) {
            mathTypeView = getMathTypeViewForLine(line);
        } else {
            mathTypeView = null;
        }
        if (mathTypeView != null) {
            mathTypeView.setSelection(selection, animated);
        } else {
            ResponderManager.setFirstResponder(null);
        }
    }

    public MTMathTypeView getMathTypeViewForLine(MTString line) {
        ARAnswerView answerView;
        MTMathTypeView mathTypeView = this.mInputLinesView.getMathTypeViewForLine(line);
        if (mathTypeView != null) {
            return mathTypeView;
        }
        Iterator<ARPagerPage> it = this.mAnswersPagerView.getPages().iterator();
        while (it.hasNext()) {
            ARPagerPage page = it.next();
            if (page.getView() instanceof ARAnswerView) {
                answerView = (ARAnswerView) page.getView();
            } else {
                answerView = null;
            }
            if (answerView != null) {
                mathTypeView = answerView.getMathTypeViewForLine(line);
                continue;
            }
            if (mathTypeView != null) {
                return mathTypeView;
            }
        }
        return null;
    }

    public ARCalculationView(Context context) {
        super(context);
        this.mStyle = ARCalculationViewStyle.Auto;
        this.BORDER_THICKNESS = getResources().getDimension(R.dimen.calculation_border_thickness);
        setBackgroundColor(Color.parseColor("#30FFFFFF"));
        this.mBorderPaint = new Paint();
        this.mBorderPaint.setAntiAlias(true);
        this.mBorderPaint.setColor(Color.parseColor("#FFFFFF"));
        ARLineSetView inputLinesView = new ARLineSetView(context);
        inputLinesView.setDelegate(this);
        inputLinesView.setEditable(true);
        addView(inputLinesView);
        this.mInputLinesView = inputLinesView;
        ARPagerView answersPagerView = new ARPagerView(context, 1, 0, new ARPagerPage[0]);
        answersPagerView.setAllowTapOnHint(true);
        answersPagerView.setContinuousScrollingEnabled(true);
        answersPagerView.setDelegate(this);
        answersPagerView.setId(View.generateViewId());
        addView(answersPagerView);
        this.mAnswersPagerView = answersPagerView;
        this.mCalculation = new MutableObservable<>();
        this.mObservers = new ArrayList<>();
        this.mObservers.add(this.mCalculation.chain(new ObservableChainLink<ARCalculation, ImmutableList<ARAnswer>>() { // from class: com.oddlyspaced.calci.archimedes.views.ARCalculationView.2
            public Observable<ImmutableList<ARAnswer>> get(ARCalculation calculation) {
                if (calculation != null) {
                    return calculation.getAnswers();
                }
                return null;
            }
        }).addObserver(new ListObserver<ARAnswer>() { // from class: com.oddlyspaced.calci.archimedes.views.ARCalculationView.1
            public void handleAdd(ARAnswer answer, int index) {
                ARCalculationView.this.handleAddedAnswer(answer, index);
            }

            public void handleRemove(ARAnswer answer, int index) {
                ARCalculationView.this.handleRemovedAnswer(answer, index);
            }
        }));
        this.mObservers.add(ResponderManager.getFirstResponder().addObserver(new Observer<ObservableChange<Responder>>() { // from class: com.oddlyspaced.calci.archimedes.views.ARCalculationView.3
            public void handle(ObservableChange<Responder> change) {
                ARCalculationView.this.handleFirstResponderChanged(change);
            }
        }));
        updateStyleProperties();
    }

    private ARCalculationView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mStyle = ARCalculationViewStyle.Auto;
        this.BORDER_THICKNESS = getResources().getDimension(R.dimen.calculation_border_thickness);
    }

    private ARCalculationView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.mStyle = ARCalculationViewStyle.Auto;
        this.BORDER_THICKNESS = getResources().getDimension(R.dimen.calculation_border_thickness);
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("savedInstanceState", super.onSaveInstanceState());
        bundle.putInt("answersPagerViewId", this.mAnswersPagerView.getId());
        return bundle;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable("savedInstanceState"));
        this.mAnswersPagerView.setId(bundle.getInt("answersPagerViewId"));
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(parentWidth, ExploreByTouchHelper.INVALID_ID);
        int childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        this.mInputLinesView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        this.mAnswersPagerView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        setMeasuredDimension(parentWidth, this.mInputLinesView.getMeasuredHeight() + this.mAnswersPagerView.getMeasuredHeight());
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int distanceToSeparatorLine = 0;
        if (this.mAnswersPagerView.getPages().size() != 0 && (this.mAnswersPagerView.getPages().get(0).getView() instanceof ARAnswerView)) {
            distanceToSeparatorLine = ((ARAnswerView) this.mAnswersPagerView.getPages().get(0).getView()).getDistanceToSeparatorLine();
        }
        this.mInputLinesView.layout(l, distanceToSeparatorLine, r, this.mInputLinesView.getMeasuredHeight() + distanceToSeparatorLine);
        this.mAnswersPagerView.layout(l, this.mInputLinesView.getMeasuredHeight(), r, this.mInputLinesView.getMeasuredHeight() + this.mAnswersPagerView.getMeasuredHeight());
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect((float) getLeft(), 0.0f, (float) getRight(), 0.0f + this.BORDER_THICKNESS, this.mBorderPaint);
        canvas.drawRect((float) getLeft(), ((float) getMeasuredHeight()) - this.BORDER_THICKNESS, (float) getRight(), (float) getMeasuredHeight(), this.mBorderPaint);
    }

    public void deinitialize() {
        this.mInputLinesView.deinitialize();
        Iterator<ObserverType> it = this.mObservers.iterator();
        while (it.hasNext()) {
            it.next().remove();
        }
        Iterator<ARPagerPage> it2 = this.mAnswersPagerView.getPages().iterator();
        while (it2.hasNext()) {
            View view = it2.next().getView();
            if (view instanceof ARAnswerView) {
                ((ARAnswerView) view).deinitialize();
            }
        }
    }

    public void handleAddedAnswer(ARAnswer answer, int index) {
        ARAnswerView answerView = new ARAnswerView(getContext());
        answerView.setAnswer(answer);
        this.mAnswersPagerView.insertPage(new ARPagerPage(answerView, answerView.getTitle(), 1.0f), index);
        updateStyleProperties();
        this.mAnswersPagerView.requestLayout();
    }

    public void handleRemovedAnswer(ARAnswer answer, int index) {
        this.mAnswersPagerView.removePage(index);
        ((ARAnswerView) this.mAnswersPagerView.getPages().get(index).getView()).deinitialize();
    }

    private void recalculate() {
        if (this.mCalculation.getValue().getInputLines().getExpressions().getValue() == null) {
            this.mInputLinesView.parseStrings();
        }
        if (this.mCalculation.getValue().getInputLines().getStrings().getValue() == null) {
            this.mInputLinesView.writeExpressions();
        }
        Iterator<ARAnswer> it = this.mCalculation.getValue().getAnswers().getValue().iterator();
        while (it.hasNext()) {
            it.next().recalculate();
        }
    }

    public void handleFirstResponderChanged(ObservableChange<Responder> change) {
        handleResponderResignedFirstResponder(change.getOldValue(), change.getNewValue());
        handleResponderBecameFirstResponder(change.getOldValue(), change.getNewValue());
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
            if (this.mStyle == ARCalculationViewStyle.Auto && newMathTypeView != null && !ViewUtil.isDescendant(newMathTypeView, this)) {
                updateStyleProperties();
            }
            this.mActiveMathTypeView = null;
        }
    }

    private void handleResponderBecameFirstResponder(Responder oldFirstResponder, Responder newFirstResponder) {
        MTMathTypeView newMathTypeView = null;
        if (oldFirstResponder instanceof MTMathTypeView) {
            Responder oldFirstResponder2 = (MTMathTypeView) oldFirstResponder;
        }
        if (newFirstResponder instanceof MTMathTypeView) {
            newMathTypeView = (MTMathTypeView) newFirstResponder;
        }
        if (newMathTypeView != null && ViewUtil.isDescendant(newMathTypeView, this)) {
            if (this.mStyle == ARCalculationViewStyle.Auto) {
                updateStyleProperties();
            }
            this.mActiveMathTypeView = newMathTypeView;
        }
    }

    private void updateStyleProperties() {
        int backgroundColor;
        int borderColor;
        int mathTypeColor;
        int separatorColor;
        boolean showHints;
        ARCalculationViewStyle style = this.mStyle;
        if (style == ARCalculationViewStyle.Auto) {
            style = ResponderUtil.containsFirstResponder(this) ? ARCalculationViewStyle.Active : ARCalculationViewStyle.Inactive;
        }
        if (style == ARCalculationViewStyle.Active) {
            backgroundColor = Color.parseColor("#0DFFFFFF");
            borderColor = Color.parseColor("#FF363636");
            mathTypeColor = Color.parseColor("#FFE6E6E6");
            separatorColor = Color.parseColor("#FF4D4D4D");
            showHints = true;
        } else {
            backgroundColor = Color.parseColor("#03FFFFFF");
            borderColor = Color.parseColor("#FF242424");
            mathTypeColor = Color.parseColor("#FF525252");
            separatorColor = Color.parseColor("#FF333333");
            showHints = false;
        }
        setBackgroundColor(backgroundColor);
        this.mBorderPaint.setColor(borderColor);
        this.mInputLinesView.setMathTypeColor(mathTypeColor);
        Iterator<ARPagerPage> it = this.mAnswersPagerView.getPages().iterator();
        while (it.hasNext()) {
            ARAnswerView answerView = (ARAnswerView) it.next().getView();
            answerView.setMathTypeColor(mathTypeColor);
            answerView.setSeparatorColor(separatorColor);
        }
        this.mAnswersPagerView.setShowHints(showHints);
    }

    private void performActionInsertElement(MTElement element) {
        if (this.mActiveMathTypeView != null && !this.mActiveMathTypeView.isEditable()) {
            if (this.mActiveMathTypeView.getSelection() != null) {
                if (this.mInputLinesView.isEditable() && this.mCalculation.getValue().getInputLines().getStrings().getValue().size() == 1) {
                    ImmutableList strings = this.mCalculation.getValue().getInputLines().getStrings().getValue();
                    setSelection(MTSelection.cursorAtEndOfString(strings.get(strings.size() - 1)), false);
                }
            } else if (this.mInputLinesView.isEditable() && this.mCalculation.getValue().getInputLines().getStrings().getValue().size() == 1) {
                ImmutableList strings2 = this.mCalculation.getValue().getInputLines().getStrings().getValue();
                setSelection(MTSelection.cursorAtEndOfString(strings2.get(strings2.size() - 1)), false);
                HashMap<String, Object> messageContents = new HashMap<>();
                messageContents.put("Element to insert", element.copy());
                new ResponderMessage(MTMessageType.INSERT_ELEMENT, messageContents).send();
            }
        }
    }

    @Override // com.oddlyspaced.calci.utilities.responder.Responder
    public boolean canHandleMessageType(String type) {
        return type.equals(MTMessageType.INSERT_ELEMENT);
    }

    @Override // com.oddlyspaced.calci.utilities.responder.Responder
    public Responder getAncestor() {
        ViewParent parent = getParent();
        if (parent instanceof Responder) {
            return (Responder) parent;
        }
        return null;
    }

    @Override // com.oddlyspaced.calci.utilities.responder.Responder
    public boolean isChildAllowedToHandleMessage(Responder child, ResponderMessage message) {
        if (message.getType().equals(MTMessageType.INSERT_ELEMENT)) {
            return child instanceof MTMathTypeView;
        }
        return true;
    }

    @Override // com.oddlyspaced.calci.utilities.responder.Responder
    public void handleMessage(String type, HashMap<String, Object> contents) {
        if (type.equals(MTMessageType.INSERT_ELEMENT)) {
            performActionInsertElement((MTElement) contents.get("Element to insert"));
        }
    }

    @Override // com.oddlyspaced.calci.archimedes.views.ARLineSetViewDelegate
    public long parsingDelayForLineSetView(ARLineSetView lineSetView, long defaultDelay) {
        if (lineSetView != this.mInputLinesView) {
            return defaultDelay;
        }
        if (lineSetView.getLineSet().getVariableCount() >= 2) {
            return defaultDelay * 4;
        }
        if (lineSetView.getLineSet().getVariableCount() == 1) {
            return defaultDelay * 2;
        }
        return defaultDelay;
    }

    @Override // com.oddlyspaced.calci.archimedes.views.ARLineSetViewDelegate
    public MTWriter writerForLineSetView(ARLineSetView lineSetView) {
        return null;
    }

    @Override // com.oddlyspaced.calci.archimedes.views.ARLineSetViewDelegate
    public MTParser parserForLineSetView(ARLineSetView lineSetView) {
        return null;
    }

    @Override // com.oddlyspaced.calci.archimedes.views.ARLineSetViewDelegate
    public long writingDelayForLineSetView(ARLineSetView lineSetView, long defaultDelay) {
        return defaultDelay;
    }

    @Override // com.oddlyspaced.calci.archimedes.model.ARCalculationDelegate
    public ARCalculationOperation createCalculationOperation(ARCalculation calculation, ARAnswer answer, ArrayList<MEExpression> inputExpressions) {
        if (this.mDelegate != null) {
            return this.mDelegate.createCalculationOperation(this, answer, inputExpressions);
        }
        return null;
    }

    @Override // com.oddlyspaced.calci.archimedes.model.ARCalculationDelegate
    public void calculationWillUpdateAnswer(ARCalculation calculation, ARAnswer answer) {
    }

    @Override // com.oddlyspaced.calci.archimedes.model.ARCalculationDelegate
    public void calculationDidUpdateAnswer(ARCalculation calculation, ARAnswer answer) {
        int answerIndex = getCalculation().getAnswers().getValue().indexOf(answer);
        if (this.mAutoScrollView != null && this.mAutoScrollView.getActiveItem() == this && this.mAnswersPagerView.getCurrentRoundedPageIndex() == answerIndex) {
            scrollForUpdatedAnswer();
        }
    }

    private void scrollForUpdatedAnswer() {
        this.mAutoScrollView.scrollToAreaOfInterest(AREAS_OF_INTEREST_ON_UPDATED_ANSWER, ARScrollPriority.ConsiderAsManualScroll, true);
    }

    @Override // com.oddlyspaced.calci.archimedes.views.ARPagerViewDelegate
    public void pagerViewDidScroll(ARPagerView pagerView) {
        Iterator<ARPagerPage> it = this.mAnswersPagerView.getPages().iterator();
        while (it.hasNext()) {
            ARPagerPage page = it.next();
            ((ARAnswerView) page.getView()).setFadeAlphaForProtrusions(GeneralUtil.mapConstrained(pagerView.fractionVisibleOfPageAtIndex(this.mAnswersPagerView.getPages().indexOf(page)), 1.0f, 0.5f, 1.0f, 0.0f));
        }
    }

    @Override // com.oddlyspaced.calci.archimedes.views.ARPagerViewDelegate
    public void pagerViewWillScrollToPageAtIndex(ARPagerView pagerView, int targetIndex) {
        if (this.mAutoScrollView != null && this.mAutoScrollView.getActiveItem() == this) {
            this.mAutoScrollView.scrollToAreaOfInterest(AREAS_OF_INTEREST_ON_ANSWER_SCROLL, ARScrollPriority.ConsiderAsManualScroll, true);
        }
    }

    @Override // com.oddlyspaced.calci.archimedes.views.ARAutoScrollViewItem
    public RectF frameInAutoScrollView(ARAutoScrollView autoScrollView) {
        return finalBoundsForViewInAncestor(this, autoScrollView.getContentView());
    }

    @Override // com.oddlyspaced.calci.archimedes.views.ARAutoScrollViewItem
    public ArrayList<RectF> areasOfInterestWithIdentifier(ARAutoScrollView autoScrollView, String identifier) {
        if (!ViewUtil.isDescendant(this, autoScrollView) || !getHandledIdentifiers().contains(identifier)) {
            return null;
        }
        ArrayList<RectF> results = new ArrayList<>();
        boolean hasSelection = (this.mActiveMathTypeView == null || this.mActiveMathTypeView.getSelection() == null) ? false : true;
        RectF activeMathTypeViewBounds = null;
        if (this.mActiveMathTypeView != null) {
            activeMathTypeViewBounds = finalBoundsForViewInAncestor(this.mActiveMathTypeView, autoScrollView.getContentView());
        }
        RectF activeSelectionBounds = null;
        if (hasSelection) {
            activeSelectionBounds = RectUtil.translate(this.mActiveMathTypeView.getSelectionBounds(), RectUtil.getOrigin(activeMathTypeViewBounds));
            activeSelectionBounds.right += MTSelectionDrawable.CURSOR_WIDTH;
        }
        RectF activeAnswersPagerBounds = finalBoundsForViewInAncestor(this.mAnswersPagerView, autoScrollView.getContentView());
        RectF activeInputLinesBounds = finalBoundsForViewInAncestor(this.mInputLinesView, autoScrollView.getContentView());
        RectF calculationViewBounds = finalBoundsForViewInAncestor(this, autoScrollView.getContentView());
        calculationViewBounds.top -= getResources().getDimension(R.dimen.calculation_spacing);
        calculationViewBounds.bottom += getResources().getDimension(R.dimen.calculation_spacing);
        if (identifier.equals(ARAutoScrollView.AREAS_OF_INTEREST_FOR_MANUAL_SCROLL)) {
            results.add(activeAnswersPagerBounds);
            return results;
        } else if (identifier.equals(AREAS_OF_INTEREST_ON_ANSWER_SCROLL)) {
            results.addAll(Arrays.asList(activeAnswersPagerBounds, activeSelectionBounds, activeMathTypeViewBounds, activeInputLinesBounds, calculationViewBounds));
            return results;
        } else if (identifier.equals(AREAS_OF_INTEREST_ON_UPDATED_ANSWER) && !ARSettings.sharedSettings(getContext()).shouldKeepAnswerInView()) {
            return null;
        } else {
            if (identifier.equals(MTMathTypeView.AREAS_OF_INTEREST_ON_EDIT) && !ARSettings.sharedSettings(getContext()).shouldKeepAnswerInView()) {
                activeAnswersPagerBounds = null;
                calculationViewBounds = null;
            }
            results.addAll(Arrays.asList(activeSelectionBounds, activeMathTypeViewBounds, activeAnswersPagerBounds, activeInputLinesBounds, calculationViewBounds));
            return results;
        }
    }

    @Override // com.oddlyspaced.calci.archimedes.views.ARAutoScrollViewItem
    public ARScrollPriority priorityForScrollToAreasOfInterest(ARAutoScrollView autoScrollView, String identifier) {
        return null;
    }

    @Override // com.oddlyspaced.calci.archimedes.views.ARAutoScrollViewItem
    public void addedToAutoScrollView(ARAutoScrollView autoScrollView) {
        this.mAutoScrollView = autoScrollView;
    }

    @Override // com.oddlyspaced.calci.archimedes.views.ARAutoScrollViewItem
    public void removedFromAutoScrollView(ARAutoScrollView autoScrollView) {
        this.mAutoScrollView = null;
    }

    @Override // com.oddlyspaced.calci.archimedes.views.ARViewGroup
    public RectF finalBoundsForChildAtIndex(int index) {
        PointF size = ((ARView) getChildAt(index)).finalSize();
        float y = 0.0f;
        if (getChildAt(index) == this.mAnswersPagerView) {
            y = 0.0f + this.mInputLinesView.finalSize().y;
        }
        return RectUtil.create(0.0f, y, size.x, size.y);
    }

    @Override // com.oddlyspaced.calci.archimedes.views.ARView
    public PointF finalSize() {
        PointF finalSize = this.mInputLinesView.finalSize();
        finalSize.y += this.mAnswersPagerView.finalSize().y;
        return finalSize;
    }

    private RectF viewBoundsRelativeToView(View view1, View view2) {
        return rectRelativeToView(ViewUtil.getBoundsInScreenCoordinates(view1), view2);
    }

    private RectF rectRelativeToView(RectF rect, View view) {
        RectF viewBounds = ViewUtil.getBoundsInScreenCoordinates(view);
        return new RectF(rect.left - viewBounds.left, rect.top - viewBounds.top, rect.right - viewBounds.left, rect.bottom - viewBounds.top);
    }

    private RectF finalBoundsForViewInAncestor(View view, View ancestor) {
        PointF size = ((ARView) view).finalSize();
        RectF finalBounds = RectUtil.create(0.0f, 0.0f, size.x, size.y);
        while (view != ancestor) {
            RectF bounds = ((ARViewGroup) view.getParent()).finalBoundsForChildAtIndex(((ViewGroup) view.getParent()).indexOfChild(view));
            finalBounds = RectUtil.translate(finalBounds, bounds.left, bounds.top);
            view = (View) view.getParent();
        }
        return finalBounds;
    }
}
