package com.sparkappdesign.archimedes.archimedes.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.SystemClock;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.sparkappdesign.archimedes.R;
import com.sparkappdesign.archimedes.archimedes.enums.ARLineSetMode;
import com.sparkappdesign.archimedes.archimedes.model.ARIssue;
import com.sparkappdesign.archimedes.archimedes.model.ARLineSet;
import com.sparkappdesign.archimedes.archimedes.model.ARSettings;
import com.sparkappdesign.archimedes.mathexpression.enums.MEExpressionForm;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression;
import com.sparkappdesign.archimedes.mathtype.nodes.MTElement;
import com.sparkappdesign.archimedes.mathtype.nodes.MTString;
import com.sparkappdesign.archimedes.mathtype.parsers.MTParser;
import com.sparkappdesign.archimedes.mathtype.views.MTMathTypeView;
import com.sparkappdesign.archimedes.mathtype.views.MTMathTypeViewDelegate;
import com.sparkappdesign.archimedes.mathtype.views.input.MTMessageType;
import com.sparkappdesign.archimedes.mathtype.views.selection.MTSelection;
import com.sparkappdesign.archimedes.mathtype.writers.MTWriter;
import com.sparkappdesign.archimedes.utilities.GeneralUtil;
import com.sparkappdesign.archimedes.utilities.RectUtil;
import com.sparkappdesign.archimedes.utilities.Timer;
import com.sparkappdesign.archimedes.utilities.events.Observer;
import com.sparkappdesign.archimedes.utilities.events.ObserverType;
import com.sparkappdesign.archimedes.utilities.observables.ImmutableList;
import com.sparkappdesign.archimedes.utilities.observables.ListObserver;
import com.sparkappdesign.archimedes.utilities.observables.MutableObservable;
import com.sparkappdesign.archimedes.utilities.observables.Observable;
import com.sparkappdesign.archimedes.utilities.observables.ObservableChainLink;
import com.sparkappdesign.archimedes.utilities.observables.ObservableChange;
import com.sparkappdesign.archimedes.utilities.observables.ObservableChangeGroup;
import com.sparkappdesign.archimedes.utilities.observables.ObservableList;
import com.sparkappdesign.archimedes.utilities.observables.ValueObserver;
import com.sparkappdesign.archimedes.utilities.responder.Responder;
import com.sparkappdesign.archimedes.utilities.responder.ResponderManager;
import com.sparkappdesign.archimedes.utilities.responder.ResponderMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimerTask;
/* loaded from: classes.dex */
public class ARLineSetView extends ViewGroup implements Responder, MTMathTypeViewDelegate, ARViewGroup {
    private static final long PARSING_DELAY_DEFAULT = 400;
    private static final long WRITING_DELAY_DEFAULT = 0;
    private MTMathTypeView mActiveMathTypeView;
    private ARLineSetViewDelegate mDelegate;
    private boolean mEditable;
    private ARErrorView mErrorView;
    private long mLastEditTime;
    private HashMap<String, Object> mMathTypeAttributes;
    private Timer mParseWriteTimer;
    private MTMathTypeView mPlaceholderMathTypeView;
    private ARStackView mStackView;
    private ObserverType mStringEditedObserver;
    private MutableObservable<ARLineSet> mLineSet = new MutableObservable<>();
    private ArrayList<ObserverType> mObservers = new ArrayList<>();
    private boolean mAllowAddRemoveLines = true;
    private ObservableList<ARIssue> mExtraIssues = new ObservableList<>();
    private int mMathTypeColor = getContext().getResources().getColor(R.color.foreground);
    private BroadcastReceiver mSettingsChangedListener = new BroadcastReceiver() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARLineSetView.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (((ARLineSet) ARLineSetView.this.mLineSet.getValue()).getMode().getValue() == ARLineSetMode.StringBased) {
                ARLineSetView.this.parseStrings();
            }
            if (((ARLineSet) ARLineSetView.this.mLineSet.getValue()).getMode().getValue() == ARLineSetMode.ExpressionBased) {
                ARLineSetView.this.writeExpressions();
            }
        }
    };
    private Runnable mParseRunnable = new Runnable() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARLineSetView.2
        @Override // java.lang.Runnable
        public void run() {
            ARLineSetView.this.parseStrings();
            ARLineSetView.this.mParseWriteTimer.cancel();
            ARLineSetView.this.mParseWriteTimer.purge();
        }
    };
    private Runnable mWriteRunnable = new Runnable() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARLineSetView.3
        @Override // java.lang.Runnable
        public void run() {
            ARLineSetView.this.writeExpressions();
            ARLineSetView.this.mParseWriteTimer.cancel();
            ARLineSetView.this.mParseWriteTimer.purge();
        }
    };

    public ARLineSet getLineSet() {
        return this.mLineSet.getValue();
    }

    public void setLineSet(ARLineSet lineSet) {
        if (this.mLineSet != null) {
            this.mLineSet.setValue(lineSet);
        } else {
            this.mLineSet = new MutableObservable<>(lineSet);
        }
        if (this.mStringEditedObserver != null) {
            this.mStringEditedObserver.remove();
        }
        this.mStringEditedObserver = lineSet.getStringEditedEvent().add(new Observer<MTString>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARLineSetView.4
            public void handle(MTString args) {
                ARLineSetView.this.handleStringsChanged();
            }
        });
    }

    public ImmutableList<ARIssue> getExtraIssues() {
        return (ImmutableList) this.mExtraIssues.getValue();
    }

    public void setExtraIssues(ImmutableList<ARIssue> extraIssues) {
        if (this.mExtraIssues != null) {
            this.mExtraIssues.setValue((ObservableList<ARIssue>) extraIssues);
        } else {
            this.mExtraIssues = new ObservableList<>(extraIssues);
        }
    }

    public boolean isEditable() {
        return this.mEditable;
    }

    public void setEditable(boolean editable) {
        this.mEditable = editable;
        Iterator<View> it = this.mStackView.getLines().iterator();
        while (it.hasNext()) {
            ((MTMathTypeView) it.next()).setEditable(editable);
        }
    }

    public HashMap<String, Object> getMathTypeAttributes() {
        return this.mMathTypeAttributes;
    }

    public void setMathTypeAttributes(HashMap<String, Object> attributes) {
        if (!GeneralUtil.equalOrBothNull(this.mMathTypeAttributes, attributes)) {
            this.mMathTypeAttributes = new HashMap<>(attributes);
            Iterator<View> it = this.mStackView.getLines().iterator();
            while (it.hasNext()) {
                ((MTMathTypeView) it.next()).setAttributes(attributes);
            }
        }
    }

    public int getMathTypeColor() {
        return this.mMathTypeColor;
    }

    public void setMathTypeColor(int mathTypeColor) {
        if (this.mMathTypeColor != mathTypeColor) {
            this.mMathTypeColor = mathTypeColor;
            Iterator<View> it = this.mStackView.getLines().iterator();
            while (it.hasNext()) {
                ((MTMathTypeView) it.next()).setColor(mathTypeColor);
            }
            this.mErrorView.setTextColor(mathTypeColor);
        }
    }

    public ARLineSetViewDelegate getDelegate() {
        return this.mDelegate;
    }

    public void setDelegate(ARLineSetViewDelegate delegate) {
        this.mDelegate = delegate;
    }

    private MTString getActiveLine() {
        return this.mActiveMathTypeView.getString();
    }

    private ObservableList<MTString> getLines() {
        return this.mLineSet.getValue().getStrings();
    }

    public MTMathTypeView getMathTypeViewForLine(MTString line) {
        int index = getLines().indexOf(line);
        if (index != -1) {
            return (MTMathTypeView) this.mStackView.getLines().get(index);
        }
        return null;
    }

    public ARLineSetView(Context context) {
        super(context);
        ARStackView stackView = new ARStackView(getContext());
        addView(stackView);
        this.mStackView = stackView;
        ARErrorView errorView = new ARErrorView(getContext());
        errorView.setTextColor(this.mMathTypeColor);
        errorView.setVisibility(4);
        addView(errorView);
        this.mErrorView = errorView;
        Observable<C> chain = this.mLineSet.chain(new ObservableChainLink<ARLineSet, ImmutableList<MTString>>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARLineSetView.5
            public Observable<ImmutableList<MTString>> get(ARLineSet lineSet) {
                if (lineSet != null) {
                    return lineSet.getStrings();
                }
                return null;
            }
        });
        this.mObservers.add(chain.addObserver(new ListObserver<MTString>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARLineSetView.6
            public void handleAdd(MTString string, int index) {
                ARLineSetView.this.handleAddedLine(string, index);
            }

            public void handleRemove(MTString string, int index) {
                ARLineSetView.this.handleRemovedLine(string, index);
            }
        }));
        this.mObservers.add(chain.addObserver(new ValueObserver<ImmutableList<MTString>>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARLineSetView.7
            public void handle(ImmutableList<MTString> strings) {
                ARLineSetView.this.handleStringsChanged();
            }
        }));
        this.mObservers.add(this.mLineSet.chain(new ObservableChainLink<ARLineSet, ImmutableList<MEExpression>>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARLineSetView.9
            public Observable<ImmutableList<MEExpression>> get(ARLineSet lineSet) {
                if (lineSet != null) {
                    return lineSet.getExpressions();
                }
                return null;
            }
        }).addObserver(new ValueObserver<ImmutableList<MEExpression>>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARLineSetView.8
            public void handle(ImmutableList<MEExpression> expressions) {
                ARLineSetView.this.handleExpressionsChanged();
            }
        }));
        this.mObservers.add(this.mExtraIssues.addObserver(new ValueObserver<ImmutableList<ARIssue>>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARLineSetView.10
            public void handle(ImmutableList<ARIssue> issue) {
                ARLineSetView.this.handleExtraIssuesChanged();
            }
        }));
        this.mObservers.add(ResponderManager.getFirstResponder().addObserver(new Observer<ObservableChange<Responder>>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARLineSetView.11
            public void handle(ObservableChange<Responder> change) {
                ARLineSetView.this.handleFirstResponderChanged(change.getOldValue(), change.getNewValue());
            }
        }));
        getContext().registerReceiver(this.mSettingsChangedListener, new IntentFilter(ARSettings.SETTINGS_DID_CHANGE_NOTIFICATION));
        MTMathTypeView mathTypeView = new MTMathTypeView(getContext());
        mathTypeView.setString(new MTString());
        this.mStackView.insertLine(mathTypeView, 0, true);
        this.mPlaceholderMathTypeView = mathTypeView;
        updateMargins();
    }

    private ARLineSetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private ARLineSetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        this.mStackView.measure(View.MeasureSpec.makeMeasureSpec(parentWidth, ExploreByTouchHelper.INVALID_ID), View.MeasureSpec.makeMeasureSpec(0, 0));
        this.mErrorView.measure(View.MeasureSpec.makeMeasureSpec(parentWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(this.mStackView.getMeasuredHeight(), 1073741824));
        setMeasuredDimension(parentWidth, this.mStackView.getMeasuredHeight());
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        this.mStackView.layout(0, 0, this.mStackView.getMeasuredWidth(), this.mStackView.getMeasuredHeight());
        this.mErrorView.layout(0, 0, this.mErrorView.getMeasuredWidth(), this.mErrorView.getMeasuredHeight());
    }

    public void deinitialize() {
        getContext().unregisterReceiver(this.mSettingsChangedListener);
        Iterator<ObserverType> it = this.mObservers.iterator();
        while (it.hasNext()) {
            it.next().remove();
        }
        this.mStringEditedObserver.remove();
        Iterator<View> it2 = this.mStackView.getLines().iterator();
        while (it2.hasNext()) {
            ((MTMathTypeView) it2.next()).deinitialize();
        }
        this.mStackView.deinitialize();
        if (this.mParseWriteTimer != null) {
            this.mParseWriteTimer.cancel();
            this.mParseWriteTimer.purge();
        }
    }

    public void handleAddedLine(MTString line, int index) {
        if (this.mPlaceholderMathTypeView != null) {
            this.mStackView.removeLine((View) this.mPlaceholderMathTypeView, true);
            this.mPlaceholderMathTypeView.deinitialize();
            this.mPlaceholderMathTypeView = null;
        }
        MTMathTypeView mathTypeView = new MTMathTypeView(getContext());
        mathTypeView.setString(line);
        mathTypeView.setColor(this.mMathTypeColor);
        mathTypeView.setAttributes(this.mMathTypeAttributes);
        mathTypeView.setEditable(this.mEditable);
        mathTypeView.setDelegate(this);
        this.mStackView.insertLine(mathTypeView, index, true);
        updateMargins();
    }

    public void handleRemovedLine(MTString line, int index) {
        MTMathTypeView mathTypeView = (MTMathTypeView) this.mStackView.getLines().get(index);
        if (mathTypeView.isFirstResponder()) {
            ResponderManager.setFirstResponder(null);
        }
        if (this.mStackView.getLines().size() > 1) {
            this.mStackView.removeLine(index, true);
            mathTypeView.deinitialize();
        } else {
            mathTypeView.clearLine(true);
            this.mPlaceholderMathTypeView = mathTypeView;
        }
        updateMargins();
    }

    private void updateMargins() {
        ArrayList<View> views = this.mStackView.getLines();
        float additionalPadding = getResources().getDimension(R.dimen.math_type_additional_padding);
        Iterator<View> it = views.iterator();
        while (it.hasNext()) {
            MTMathTypeView mathTypeView = (MTMathTypeView) it.next();
            if (mathTypeView == views.get(0)) {
                mathTypeView.setAdditionalPaddingTop(additionalPadding);
                if (views.size() != 1) {
                    mathTypeView.setAdditionalPaddingBottom(0.0f);
                }
            }
            if (mathTypeView == views.get(views.size() - 1)) {
                mathTypeView.setAdditionalPaddingBottom(additionalPadding);
                if (views.size() != 1) {
                    mathTypeView.setAdditionalPaddingTop(0.0f);
                }
            }
            if (!(mathTypeView == views.get(0) || mathTypeView == views.get(views.size() - 1))) {
                mathTypeView.setAdditionalPaddingTop(0.0f);
                mathTypeView.setAdditionalPaddingBottom(0.0f);
            }
        }
    }

    public void handleStringsChanged() {
        if (this.mLineSet.getValue().getMode().getValue() == ARLineSetMode.StringBased) {
            this.mLastEditTime = SystemClock.uptimeMillis();
            if (this.mLineSet.getValue().getExpressions().getValue() == null) {
                scheduleParsing();
                return;
            }
            return;
        }
        setSelection(null, true);
        if (this.mLineSet.getValue().getStrings().getValue() == null && this.mLineSet.getValue().getExpressions().getValue() != null) {
            if (this.mParseWriteTimer == null || this.mParseWriteTimer.isCancelled()) {
                scheduleWriting();
            }
        }
    }

    public void handleExpressionsChanged() {
        if (this.mLineSet.getValue().getMode().getValue() == ARLineSetMode.ExpressionBased) {
            this.mLastEditTime = SystemClock.uptimeMillis();
            if (this.mLineSet.getValue().getStrings().getValue() == null) {
                scheduleWriting();
            }
        } else if (this.mLineSet.getValue().getExpressions().getValue() == null && this.mLineSet.getValue().getStrings().getValue() != null) {
            if (this.mParseWriteTimer == null || this.mParseWriteTimer.isCancelled()) {
                scheduleParsing();
            }
        }
    }

    public void handleFirstResponderChanged(Responder oldValue, Responder newValue) {
        MTMathTypeView oldMTV = (MTMathTypeView) oldValue;
        if (oldMTV != null && oldMTV == this.mActiveMathTypeView) {
            this.mActiveMathTypeView = null;
            if (this.mAllowAddRemoveLines) {
                removeLineIfEmpty(oldMTV.getString(), true);
            }
        }
        MTMathTypeView newMTV = (MTMathTypeView) newValue;
        if (mathTypeViewIsChildOfStackView(newMTV)) {
            this.mActiveMathTypeView = newMTV;
            if (this.mEditable) {
                this.mLineSet.getValue().getMode().setValue(ARLineSetMode.StringBased);
            }
        }
    }

    public void handleExtraIssuesChanged() {
        boolean hasError;
        int i;
        int i2 = 4;
        if (this.mExtraIssues.size() != 0) {
            hasError = true;
        } else {
            hasError = false;
        }
        this.mErrorView.setIssue(hasError ? this.mExtraIssues.get(0) : null);
        ARErrorView aRErrorView = this.mErrorView;
        if (hasError) {
            i = 0;
        } else {
            i = 4;
        }
        aRErrorView.setVisibility(i);
        ARStackView aRStackView = this.mStackView;
        if (!hasError) {
            i2 = 0;
        }
        aRStackView.setVisibility(i2);
    }

    private void removeLineIfEmpty(MTString line, boolean animated) {
        if (line.length() == 0 && getLines().size() > 1) {
            ArrayList<MTString> lines = new ArrayList<>(getLines());
            lines.remove(line);
            ObservableChangeGroup changeGroup = new ObservableChangeGroup();
            changeGroup.setNewValue(getLines(), new ImmutableList(lines), ARLineSet.DONT_INVALIDATE_FLAG);
            changeGroup.performChanges();
        }
    }

    private void scheduleParsing() {
        long delay = PARSING_DELAY_DEFAULT;
        if (this.mParseWriteTimer != null) {
            this.mParseWriteTimer.cancel();
            this.mParseWriteTimer.purge();
        }
        if (this.mDelegate != null) {
            delay = this.mDelegate.parsingDelayForLineSetView(this, PARSING_DELAY_DEFAULT);
        }
        long delay2 = GeneralUtil.constrainMin(delay - (SystemClock.uptimeMillis() - this.mLastEditTime), 0);
        if (delay2 > 0) {
            this.mParseWriteTimer = new Timer();
            this.mParseWriteTimer.schedule(new TimerTask() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARLineSetView.12
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    ARLineSetView.this.post(ARLineSetView.this.mParseRunnable);
                }
            }, delay2);
            return;
        }
        parseStrings();
    }

    private void scheduleWriting() {
        long delay;
        if (this.mParseWriteTimer != null) {
            this.mParseWriteTimer.cancel();
            this.mParseWriteTimer.purge();
        }
        if (this.mDelegate != null) {
            delay = this.mDelegate.writingDelayForLineSetView(this, 0);
        } else {
            delay = 0;
        }
        long delay2 = GeneralUtil.constrainMin(delay - (SystemClock.uptimeMillis() - this.mLastEditTime), 0);
        if (delay2 > 0) {
            this.mParseWriteTimer = new Timer();
            this.mParseWriteTimer.schedule(new TimerTask() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARLineSetView.13
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    ARLineSetView.this.post(ARLineSetView.this.mWriteRunnable);
                }
            }, delay2);
            return;
        }
        writeExpressions();
    }

    public void parseStrings() {
        if (this.mLineSet.getValue().getStrings().getValue() != null) {
            MTParser parser = this.mDelegate.parserForLineSetView(this);
            if (parser == null) {
                parser = ARSettings.sharedSettings(getContext()).defaultParser();
            }
            this.mLineSet.getValue().parseStringsToExpressions(parser);
        }
    }

    public void writeExpressions() {
        if (this.mLineSet.getValue().getExpressions().getValue() != null) {
            MTWriter writer = this.mDelegate.writerForLineSetView(this);
            if (writer == null) {
                writer = ARSettings.sharedSettings(getContext()).defaultWriterForForm(getContext(), MEExpressionForm.Exact);
            }
            this.mLineSet.getValue().writeExpressionsToStrings(writer);
        }
    }

    private void setSelection(MTSelection selection, boolean animated) {
        MTString line;
        MTMathTypeView mathTypeView;
        if (selection != null) {
            line = (MTString) selection.getString().rootNode();
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
        } else if (this.mActiveMathTypeView != null) {
            ResponderManager.setFirstResponder(null);
        }
    }

    public void insertElement(MTElement element) {
        performActionInsertElement(element);
        new ResponderMessage(MTMessageType.DID_PERFORM_EDIT, null).send();
    }

    private void performActionInsertElement(MTElement element) {
        if (this.mActiveMathTypeView == null && getLines().size() == 1) {
            MTMathTypeView mathTypeView = (MTMathTypeView) this.mStackView.getLines().get(0);
            if (mathTypeView.isEditable() && mathTypeView.getString() != null) {
                mathTypeView.setSelection(MTSelection.cursorAtEndOfString(mathTypeView.getString()));
                if (this.mActiveMathTypeView != null) {
                    HashMap<String, Object> messageContents = new HashMap<>();
                    messageContents.put("Element to insert", element.copy());
                    new ResponderMessage(MTMessageType.INSERT_ELEMENT, messageContents).send();
                }
            }
        }
    }

    public void backspace(boolean animated) {
        performActionBackspace(animated);
        new ResponderMessage(MTMessageType.DID_PERFORM_EDIT, null).send();
    }

    private void performActionBackspace(boolean animated) {
        if (this.mEditable && this.mActiveMathTypeView != null) {
            if (getActiveLine().isNotEmpty()) {
                this.mActiveMathTypeView.backspace(animated);
            } else if (getActiveLine() == null) {
                setSelection(MTSelection.cursorAtEndOfString(getLines().get(getLines().size() - 1)), true);
            } else if (getActiveLine() != getLines().get(0)) {
                MTString lineBefore = getLines().get(getLines().indexOf(getActiveLine()) - 1);
                if (this.mAllowAddRemoveLines) {
                    getLines().remove(getActiveLine());
                }
                setSelection(MTSelection.cursorAtEndOfString(lineBefore), true);
            }
        }
    }

    public void clearAll(boolean animated) {
        performActionClearAll(animated);
        new ResponderMessage(MTMessageType.DID_PERFORM_EDIT, null).send();
    }

    private void performActionClearAll(boolean animated) {
        if (this.mEditable) {
            if (this.mAllowAddRemoveLines) {
                MTMathTypeView mathTypeViewToKeep = this.mActiveMathTypeView != null ? this.mActiveMathTypeView : getMathTypeViewForLine(getLines().get(getLines().size() - 1));
                MTString lineToKeep = mathTypeViewToKeep.getString();
                for (int i = getLines().size() - 1; i >= 0; i--) {
                    if (getLines().get(i) != lineToKeep) {
                        getLines().remove(i);
                    }
                }
                mathTypeViewToKeep.clearLine(animated);
                return;
            }
            Iterator<View> it = this.mStackView.getLines().iterator();
            while (it.hasNext()) {
                ((MTMathTypeView) it.next()).clearLine(animated);
            }
        }
    }

    public void enter(boolean animated) {
        performActionEnter(animated);
        new ResponderMessage(MTMessageType.DID_PERFORM_EDIT, null).send();
    }

    private void performActionEnter(boolean animated) {
        MTString nextLine;
        if (this.mEditable && this.mActiveMathTypeView != null) {
            if (this.mAllowAddRemoveLines) {
                MTString line = getActiveLine() != null ? getActiveLine() : getLines().get(getLines().size() - 1);
                if (line.isNotEmpty()) {
                    MTString newLine = new MTString();
                    getLines().add(getLines().indexOf(line) + 1, newLine);
                    setSelection(MTSelection.cursorAtEndOfString(newLine), animated);
                }
            } else if (getActiveLine() != null && (nextLine = getLines().get(getLines().indexOf(getActiveLine()) + 1)) != null) {
                setSelection(MTSelection.cursorAtEndOfString(nextLine), animated);
            }
        }
    }

    private boolean mathTypeViewIsChildOfStackView(View view) {
        Iterator<View> it = this.mStackView.getLines().iterator();
        while (it.hasNext()) {
            if (it.next() == view) {
                return true;
            }
        }
        return false;
    }

    @Override // com.sparkappdesign.archimedes.utilities.responder.Responder
    public boolean canHandleMessageType(String type) {
        return type.equals(MTMessageType.INSERT_ELEMENT) || type.equals(MTMessageType.BACKSPACE) || type.equals(MTMessageType.CLEAR_ALL) || type.equals(MTMessageType.ENTER);
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
        return (message.getType().equals(MTMessageType.INSERT_ELEMENT) && this.mActiveMathTypeView != null) || message.getType().equals(MTMessageType.CLEAR_LINE) || message.getType().equals(MTMessageType.COPY) || message.getType().equals(MTMessageType.PASTE);
    }

    @Override // com.sparkappdesign.archimedes.utilities.responder.Responder
    public void handleMessage(String type, HashMap<String, Object> contents) {
        if (type.equals(MTMessageType.INSERT_ELEMENT)) {
            insertElement((MTElement) contents.get("Element to insert"));
        }
        if (type.equals(MTMessageType.BACKSPACE)) {
            backspace(true);
        }
        if (type.equals(MTMessageType.CLEAR_ALL)) {
            clearAll(true);
        }
        if (type.equals(MTMessageType.ENTER)) {
            enter(true);
        }
    }

    @Override // com.sparkappdesign.archimedes.mathtype.views.MTMathTypeViewDelegate
    public void mathTypeViewDidChange(MTMathTypeView mathTypeView) {
        this.mLineSet.getValue().handleStringsModified(mathTypeView.getString());
    }

    @Override // com.sparkappdesign.archimedes.archimedes.views.ARViewGroup
    public RectF finalBoundsForChildAtIndex(int index) {
        PointF size = ((ARView) getChildAt(index)).finalSize();
        return RectUtil.create(0.0f, 0.0f, size.x, size.y);
    }

    @Override // com.sparkappdesign.archimedes.archimedes.views.ARView
    public PointF finalSize() {
        return this.mStackView.finalSize();
    }
}
