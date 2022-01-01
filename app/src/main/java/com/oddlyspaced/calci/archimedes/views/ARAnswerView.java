package com.sparkappdesign.archimedes.archimedes.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.sparkappdesign.archimedes.R;
import com.sparkappdesign.archimedes.archimedes.enums.ARAnswerState;
import com.sparkappdesign.archimedes.archimedes.enums.ARLineSetMode;
import com.sparkappdesign.archimedes.archimedes.model.ARAnswer;
import com.sparkappdesign.archimedes.archimedes.model.ARCalculation;
import com.sparkappdesign.archimedes.archimedes.model.ARIssue;
import com.sparkappdesign.archimedes.archimedes.model.ARSettings;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression;
import com.sparkappdesign.archimedes.mathtype.enums.MTDigitGroupingStyle;
import com.sparkappdesign.archimedes.mathtype.nodes.MTString;
import com.sparkappdesign.archimedes.mathtype.parsers.MTParser;
import com.sparkappdesign.archimedes.mathtype.views.MTMathTypeView;
import com.sparkappdesign.archimedes.mathtype.writers.MTWriter;
import com.sparkappdesign.archimedes.utilities.GeneralUtil;
import com.sparkappdesign.archimedes.utilities.RectUtil;
import com.sparkappdesign.archimedes.utilities.Timer;
import com.sparkappdesign.archimedes.utilities.TypefaceCache;
import com.sparkappdesign.archimedes.utilities.events.Observer;
import com.sparkappdesign.archimedes.utilities.events.ObserverType;
import com.sparkappdesign.archimedes.utilities.observables.ImmutableList;
import com.sparkappdesign.archimedes.utilities.observables.MutableObservable;
import com.sparkappdesign.archimedes.utilities.observables.Observable;
import com.sparkappdesign.archimedes.utilities.observables.ObservableChainLink;
import com.sparkappdesign.archimedes.utilities.observables.ObservableChange;
import com.sparkappdesign.archimedes.utilities.responder.Responder;
import com.sparkappdesign.archimedes.utilities.responder.ResponderMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimerTask;
/* loaded from: classes.dex */
public class ARAnswerView extends ViewGroup implements Responder, ARLineSetViewDelegate, ARViewGroup {
    private static final long RECALCULATION_DELAY_DEFAULT = 400;
    private static final long RECALCULATION_DELAY_WITH_VARIABLES = 1000;
    private static final long SPIN_WHEEL_START_DELAY = 1000;
    private Timer mActivityIndicatorTimer;
    private MutableObservable<ARAnswer> mAnswer;
    private ARCalculation mCalculation;
    private boolean mDeinitialized;
    private long mLastEditTime;
    private ARLineSetView mLineSetView;
    private ArrayList<ObserverType> mObservers;
    private ARPausedLabel mPausedLabel;
    private ARProgressBarContainer mProgressBarContainer;
    private Runnable mRecalculateAnswerRunnable;
    private Timer mRecalculationTimer;
    private ARSeparatorView mSeparatorView;
    private BroadcastReceiver mSettingsChangedListener;
    private Runnable mShowSpinWheelRunnable;
    private ObserverType mStringEditedObserver;
    private int mTintColor;

    public String getTitle() {
        return getTitle(this.mAnswer.getValue());
    }

    private String getTitle(ARAnswer answer) {
        switch (answer.getForm()) {
            case Numeric:
                return "NUMERIC";
            case Exact:
                return "EXACT";
            default:
                return "";
        }
    }

    public void setAnswer(ARAnswer answer) {
        ARCalculation aRCalculation = null;
        if (this.mAnswer.getValue() != answer) {
            ARAnswer oldAnswer = this.mAnswer.getValue();
            if (oldAnswer != null) {
                oldAnswer.setHandler(null);
                this.mStringEditedObserver.remove();
            }
            if (answer != null) {
                aRCalculation = answer.getCalculation();
            }
            this.mCalculation = aRCalculation;
            if (answer != null) {
                answer.setHandler(new Handler(getContext().getMainLooper()));
                this.mLineSetView.setLineSet(answer.getLines());
                this.mSeparatorView.setText(getTitle(answer));
                this.mStringEditedObserver = answer.getInputLines().getStringEditedEvent().add(new Observer<MTString>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.4
                    public void handle(MTString string) {
                        ARAnswerView.this.handleInputStringsChanged();
                    }
                });
            }
            this.mAnswer.setValue(answer);
        }
    }

    public MTMathTypeView getMathTypeViewForLine(MTString line) {
        return this.mLineSetView.getMathTypeViewForLine(line);
    }

    public void setMathTypeColor(int mathTypeColor) {
        this.mLineSetView.setMathTypeColor(mathTypeColor);
    }

    public void setSeparatorColor(int separatorColor) {
        this.mSeparatorView.setColor(separatorColor);
    }

    public int getDistanceToSeparatorLine() {
        return this.mSeparatorView.getMeasuredHeight() / 2;
    }

    public ARAnswerView(Context context) {
        super(context);
        this.mAnswer = new MutableObservable<>();
        this.mSettingsChangedListener = new BroadcastReceiver() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (!ARAnswerView.this.mDeinitialized) {
                    ARAnswerView.this.updateSettingsBasedAttributes();
                    ((ARAnswer) ARAnswerView.this.mAnswer.getValue()).recalculate();
                }
            }
        };
        this.mShowSpinWheelRunnable = new Runnable() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.2
            @Override // java.lang.Runnable
            public void run() {
                if (!ARAnswerView.this.mDeinitialized) {
                    ARAnswerView.this.mProgressBarContainer.setVisibility(0);
                }
            }
        };
        this.mRecalculateAnswerRunnable = new Runnable() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.3
            @Override // java.lang.Runnable
            public void run() {
                if (!ARAnswerView.this.mDeinitialized) {
                    ((ARAnswer) ARAnswerView.this.mAnswer.getValue()).recalculate();
                }
            }
        };
        ARSeparatorView separatorView = new ARSeparatorView(context);
        addView(separatorView);
        this.mSeparatorView = separatorView;
        ARLineSetView lineSetView = new ARLineSetView(context);
        lineSetView.setDelegate(this);
        lineSetView.setEditable(false);
        addView(lineSetView);
        this.mLineSetView = lineSetView;
        ARProgressBarContainer container = new ARProgressBarContainer(context);
        addView(container);
        this.mProgressBarContainer = container;
        this.mAnswer = new MutableObservable<>();
        this.mObservers = new ArrayList<>();
        this.mObservers.add(this.mAnswer.chain(new ObservableChainLink<ARAnswer, ARAnswerState>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.6
            public Observable<ARAnswerState> get(ARAnswer answer) {
                if (answer != null) {
                    return answer.getState();
                }
                return null;
            }
        }).addObserver(new Observer<ObservableChange<ARAnswerState>>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.5
            public void handle(ObservableChange<ARAnswerState> change) {
                ARAnswerView.this.handleAnswerStateChanged();
            }
        }));
        this.mObservers.add(this.mAnswer.chain(new ObservableChainLink<ARAnswer, Boolean>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.8
            public Observable<Boolean> get(ARAnswer answer) {
                if (answer != null) {
                    return answer.isPaused();
                }
                return null;
            }
        }).addObserver(new Observer<ObservableChange<Boolean>>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.7
            public void handle(ObservableChange<Boolean> change) {
                ARAnswerView.this.handlePausedChanged();
            }
        }));
        this.mObservers.add(this.mAnswer.chain(new ObservableChainLink<ARAnswer, ImmutableList<ARIssue>>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.10
            public Observable<ImmutableList<ARIssue>> get(ARAnswer answer) {
                if (answer != null) {
                    return answer.getIssues();
                }
                return null;
            }
        }).addObserver(new Observer<ObservableChange<ImmutableList<ARIssue>>>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.9
            public void handle(ObservableChange<ImmutableList<ARIssue>> change) {
                ARAnswerView.this.handleIssuesChanged(change.getNewValue());
            }
        }));
        this.mObservers.add(this.mAnswer.chain(new ObservableChainLink<ARAnswer, ImmutableList<MTString>>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.12
            public Observable<ImmutableList<MTString>> get(ARAnswer answer) {
                if (answer != null) {
                    return answer.getInputLines().getStrings();
                }
                return null;
            }
        }).addObserver(new Observer<ObservableChange<ImmutableList<MTString>>>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.11
            public void handle(ObservableChange<ImmutableList<MTString>> change) {
                ARAnswerView.this.handleInputStringsChanged();
            }
        }));
        this.mObservers.add(this.mAnswer.chain(new ObservableChainLink<ARAnswer, ImmutableList<MEExpression>>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.14
            public Observable<ImmutableList<MEExpression>> get(ARAnswer answer) {
                if (answer != null) {
                    return answer.getInputLines().getExpressions();
                }
                return null;
            }
        }).addObserver(new Observer<ObservableChange<ImmutableList<MEExpression>>>() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.13
            public void handle(ObservableChange<ImmutableList<MEExpression>> change) {
                ARAnswerView.this.handleInputExpressionsChanged();
            }
        }));
        getContext().registerReceiver(this.mSettingsChangedListener, new IntentFilter(ARSettings.SETTINGS_DID_CHANGE_NOTIFICATION));
        updateSettingsBasedAttributes();
    }

    private ARAnswerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mAnswer = new MutableObservable<>();
        this.mSettingsChangedListener = new BroadcastReceiver() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (!ARAnswerView.this.mDeinitialized) {
                    ARAnswerView.this.updateSettingsBasedAttributes();
                    ((ARAnswer) ARAnswerView.this.mAnswer.getValue()).recalculate();
                }
            }
        };
        this.mShowSpinWheelRunnable = new Runnable() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.2
            @Override // java.lang.Runnable
            public void run() {
                if (!ARAnswerView.this.mDeinitialized) {
                    ARAnswerView.this.mProgressBarContainer.setVisibility(0);
                }
            }
        };
        this.mRecalculateAnswerRunnable = new Runnable() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.3
            @Override // java.lang.Runnable
            public void run() {
                if (!ARAnswerView.this.mDeinitialized) {
                    ((ARAnswer) ARAnswerView.this.mAnswer.getValue()).recalculate();
                }
            }
        };
    }

    private ARAnswerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mAnswer = new MutableObservable<>();
        this.mSettingsChangedListener = new BroadcastReceiver() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (!ARAnswerView.this.mDeinitialized) {
                    ARAnswerView.this.updateSettingsBasedAttributes();
                    ((ARAnswer) ARAnswerView.this.mAnswer.getValue()).recalculate();
                }
            }
        };
        this.mShowSpinWheelRunnable = new Runnable() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.2
            @Override // java.lang.Runnable
            public void run() {
                if (!ARAnswerView.this.mDeinitialized) {
                    ARAnswerView.this.mProgressBarContainer.setVisibility(0);
                }
            }
        };
        this.mRecalculateAnswerRunnable = new Runnable() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.3
            @Override // java.lang.Runnable
            public void run() {
                if (!ARAnswerView.this.mDeinitialized) {
                    ((ARAnswer) ARAnswerView.this.mAnswer.getValue()).recalculate();
                }
            }
        };
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(parentWidth, ExploreByTouchHelper.INVALID_ID);
        int childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        this.mSeparatorView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        this.mLineSetView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        int height = this.mSeparatorView.getMeasuredHeight() + this.mLineSetView.getMeasuredHeight();
        setMeasuredDimension(parentWidth, height);
        int fullWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(parentWidth, 1073741824);
        int fullHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height - getDistanceToSeparatorLine(), 1073741824);
        this.mProgressBarContainer.measure(fullWidthMeasureSpec, fullHeightMeasureSpec);
        if (this.mPausedLabel != null) {
            this.mPausedLabel.measure(fullWidthMeasureSpec, fullHeightMeasureSpec);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        this.mSeparatorView.layout(0, 0, this.mSeparatorView.getMeasuredWidth(), this.mSeparatorView.getMeasuredHeight());
        this.mLineSetView.layout(0, this.mSeparatorView.getMeasuredHeight(), this.mLineSetView.getMeasuredWidth(), this.mSeparatorView.getMeasuredHeight() + this.mLineSetView.getMeasuredHeight());
        this.mProgressBarContainer.layout(0, getDistanceToSeparatorLine(), this.mProgressBarContainer.getMeasuredWidth(), getDistanceToSeparatorLine() + this.mProgressBarContainer.getMeasuredHeight());
        if (this.mPausedLabel != null) {
            this.mPausedLabel.layout(0, getDistanceToSeparatorLine(), this.mPausedLabel.getMeasuredWidth(), getDistanceToSeparatorLine() + this.mPausedLabel.getMeasuredHeight());
        }
    }

    public void deinitialize() {
        this.mDeinitialized = true;
        getContext().unregisterReceiver(this.mSettingsChangedListener);
        removeCallbacks(this.mShowSpinWheelRunnable);
        removeCallbacks(this.mRecalculateAnswerRunnable);
        this.mLineSetView.deinitialize();
        Iterator<ObserverType> it = this.mObservers.iterator();
        while (it.hasNext()) {
            it.next().remove();
        }
        this.mStringEditedObserver.remove();
        if (this.mActivityIndicatorTimer != null) {
            this.mActivityIndicatorTimer.cancel();
            this.mActivityIndicatorTimer.purge();
        }
        if (this.mRecalculationTimer != null) {
            this.mRecalculationTimer.cancel();
            this.mRecalculationTimer.purge();
        }
    }

    public void handleAnswerStateChanged() {
        ARAnswerState state = this.mAnswer.getValue().getState().getValue();
        this.mLineSetView.setVisibility(state != ARAnswerState.Normal ? 4 : 0);
        if (state != ARAnswerState.Recalculating) {
            if (this.mActivityIndicatorTimer != null) {
                this.mActivityIndicatorTimer.cancel();
                this.mActivityIndicatorTimer.purge();
            }
            this.mProgressBarContainer.setVisibility(4);
        } else if ((this.mActivityIndicatorTimer == null || this.mActivityIndicatorTimer.isCancelled()) && !this.mAnswer.getValue().isPaused().getValue().booleanValue()) {
            this.mActivityIndicatorTimer = new Timer();
            this.mActivityIndicatorTimer.schedule(new TimerTask() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.15
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    ARAnswerView.this.post(ARAnswerView.this.mShowSpinWheelRunnable);
                }
            }, 1000);
        }
        if (state == ARAnswerState.Invalidated) {
            startRecalculationTimerIfNeeded();
            return;
        }
        if (this.mRecalculationTimer != null) {
            this.mRecalculationTimer.cancel();
            this.mRecalculationTimer.purge();
        }
        this.mLastEditTime = 0;
    }

    public void handleIssuesChanged(ImmutableList<ARIssue> newIssues) {
        this.mLineSetView.setExtraIssues(newIssues);
    }

    public void handleInputStringsChanged() {
        if (this.mCalculation != null && this.mCalculation.getInputLines().getMode().getValue() == ARLineSetMode.StringBased) {
            markEdit();
        }
    }

    public void handleInputExpressionsChanged() {
        if (this.mCalculation != null && this.mCalculation.getInputLines().getMode().getValue() == ARLineSetMode.ExpressionBased) {
            markEdit();
        }
        startRecalculationTimerIfNeeded();
    }

    public void updateSettingsBasedAttributes() {
        MTDigitGroupingStyle digitGroupingStyle = ARSettings.sharedSettings(getContext()).shouldUseDigitGrouping() ? MTDigitGroupingStyle.Western : MTDigitGroupingStyle.None;
        HashMap<String, Object> mathTypeAttributes = new HashMap<>();
        mathTypeAttributes.put(MTMathTypeView.DIGIT_GROUPING_STYLE_ATTRIBUTE_NAME, digitGroupingStyle);
        this.mLineSetView.setMathTypeAttributes(mathTypeAttributes);
    }

    private void markEdit() {
        this.mLastEditTime = SystemClock.uptimeMillis();
        if (this.mRecalculationTimer != null && !this.mRecalculationTimer.isCancelled()) {
            restartRecalculationTimer();
        }
    }

    private void startRecalculationTimerIfNeeded() {
        boolean shouldRecalculate;
        boolean canRecalculate;
        boolean isTimerRunning;
        if (this.mAnswer.getValue().getState().getValue() == ARAnswerState.Invalidated) {
            shouldRecalculate = true;
        } else {
            shouldRecalculate = false;
        }
        if (this.mAnswer.getValue().getInputLines().getExpressions().getValue() != null) {
            canRecalculate = true;
        } else {
            canRecalculate = false;
        }
        if (this.mRecalculationTimer == null || this.mRecalculationTimer.isCancelled()) {
            isTimerRunning = false;
        } else {
            isTimerRunning = true;
        }
        if (shouldRecalculate && canRecalculate && !isTimerRunning) {
            restartRecalculationTimer();
        }
    }

    private void restartRecalculationTimer() {
        if (this.mCalculation != null) {
            if (this.mRecalculationTimer != null) {
                this.mRecalculationTimer.cancel();
                this.mRecalculationTimer.purge();
            }
            long delay = GeneralUtil.constrainMin((this.mCalculation.getInputLines().getVariableCount() != 0 ? 1000 : RECALCULATION_DELAY_DEFAULT) - (SystemClock.uptimeMillis() - this.mLastEditTime), 0);
            this.mRecalculationTimer = new Timer();
            this.mRecalculationTimer.schedule(new TimerTask() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.16
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    ARAnswerView.this.post(ARAnswerView.this.mRecalculateAnswerRunnable);
                }
            }, delay);
        }
    }

    public void handleTap() {
        if (this.mAnswer.getValue().getState().getValue() == ARAnswerState.Recalculating) {
            this.mAnswer.getValue().setPaused(!this.mAnswer.getValue().isPaused().getValue().booleanValue());
        }
    }

    public void handlePausedChanged() {
        if (this.mAnswer.getValue().isPaused().getValue().booleanValue()) {
            if (this.mPausedLabel == null) {
                createPausedLabel();
            }
            this.mPausedLabel.setVisibility(0);
            this.mProgressBarContainer.setVisibility(4);
            return;
        }
        if (this.mPausedLabel != null) {
            this.mPausedLabel.setVisibility(4);
        }
        if (this.mAnswer.getValue().getState().getValue() == ARAnswerState.Recalculating) {
            this.mProgressBarContainer.setVisibility(0);
        }
    }

    private void createPausedLabel() {
        ARPausedLabel pausedLabel = new ARPausedLabel(getContext());
        pausedLabel.setVisibility(4);
        addView(pausedLabel);
        this.mPausedLabel = pausedLabel;
    }

    public void setFadeAlphaForProtrusions(float alpha) {
        this.mLineSetView.setAlpha(alpha);
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
    public boolean canHandleMessageType(String type) {
        return false;
    }

    @Override // com.sparkappdesign.archimedes.utilities.responder.Responder
    public boolean isChildAllowedToHandleMessage(Responder child, ResponderMessage message) {
        return true;
    }

    @Override // com.sparkappdesign.archimedes.utilities.responder.Responder
    public void handleMessage(String type, HashMap<String, Object> contents) {
    }

    @Override // com.sparkappdesign.archimedes.archimedes.views.ARLineSetViewDelegate
    public MTWriter writerForLineSetView(ARLineSetView lineSetView) {
        return ARSettings.sharedSettings(getContext()).defaultWriterForForm(getContext(), this.mAnswer.getValue().getForm());
    }

    @Override // com.sparkappdesign.archimedes.archimedes.views.ARLineSetViewDelegate
    public MTParser parserForLineSetView(ARLineSetView lineSetView) {
        return null;
    }

    @Override // com.sparkappdesign.archimedes.archimedes.views.ARLineSetViewDelegate
    public long parsingDelayForLineSetView(ARLineSetView lineSetView, long defaultDelay) {
        return defaultDelay;
    }

    @Override // com.sparkappdesign.archimedes.archimedes.views.ARLineSetViewDelegate
    public long writingDelayForLineSetView(ARLineSetView lineSetView, long defaultDelay) {
        return defaultDelay;
    }

    @Override // com.sparkappdesign.archimedes.archimedes.views.ARViewGroup
    public RectF finalBoundsForChildAtIndex(int index) {
        PointF size = ((ARView) getChildAt(index)).finalSize();
        return RectUtil.create(0.0f, (float) this.mSeparatorView.getHeight(), size.x, size.y);
    }

    @Override // com.sparkappdesign.archimedes.archimedes.views.ARView
    public PointF finalSize() {
        PointF finalSize = this.mLineSetView.finalSize();
        finalSize.y += (float) this.mSeparatorView.getHeight();
        return finalSize;
    }

    /* loaded from: classes.dex */
    public class ARPausedLabel extends ViewGroup {
        private TextView mTextView = new TextView(getContext());
        private final int PAUSED_LABEL_WIDTH_MEASURE_SPEC = View.MeasureSpec.makeMeasureSpec(0, 0);
        private final int PAUSED_LABEL_HEIGHT_MEASURE_SPEC = View.MeasureSpec.makeMeasureSpec(0, 0);

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ARPausedLabel(Context context) {
            super(context);
            ARAnswerView.this = r5;
            this.mTextView.setTypeface(TypefaceCache.getMyriadProLight(getContext()));
            this.mTextView.setTextSize(0, getResources().getDimension(R.dimen.paused_font_size));
            this.mTextView.setTextColor(context.getResources().getColor(R.color.tint_dark));
            this.mTextView.setText("PAUSED");
            this.mTextView.setOnClickListener(new View.OnClickListener() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.ARPausedLabel.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    ARAnswerView.this.handleTap();
                }
            });
            addView(this.mTextView);
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        private ARPausedLabel(Context context, AttributeSet attrs) {
            super(context, attrs);
            ARAnswerView.this = r4;
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        private ARPausedLabel(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            ARAnswerView.this = r4;
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
            this.mTextView.measure(this.PAUSED_LABEL_WIDTH_MEASURE_SPEC, this.PAUSED_LABEL_HEIGHT_MEASURE_SPEC);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int l2 = ((r - l) / 2) - (this.mTextView.getMeasuredWidth() / 2);
            int t2 = ((b - t) / 2) - (this.mTextView.getMeasuredHeight() / 2);
            this.mTextView.layout(l2, t2, l2 + this.mTextView.getMeasuredWidth(), t2 + this.mTextView.getMeasuredHeight());
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return true;
        }
    }

    /* loaded from: classes.dex */
    public class ARProgressBarContainer extends ViewGroup {
        private ProgressBar mProgressBar = new ProgressBar(getContext());
        private final int SPIN_WHEEL_WIDTH_MEASURE_SPEC = View.MeasureSpec.makeMeasureSpec((int) getResources().getDimension(R.dimen.spin_wheel_size), 1073741824);
        private final int SPIN_WHEEL_HEIGHT_MEASURE_SPEC = View.MeasureSpec.makeMeasureSpec((int) getResources().getDimension(R.dimen.spin_wheel_size), 1073741824);

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ARProgressBarContainer(Context context) {
            super(context);
            ARAnswerView.this = r5;
            this.mProgressBar.setIndeterminate(true);
            this.mProgressBar.getIndeterminateDrawable().setColorFilter(-1, PorterDuff.Mode.SRC_ATOP);
            this.mProgressBar.setOnClickListener(new View.OnClickListener() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARAnswerView.ARProgressBarContainer.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    ARAnswerView.this.handleTap();
                }
            });
            addView(this.mProgressBar);
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        private ARProgressBarContainer(Context context, AttributeSet attrs) {
            super(context, attrs);
            ARAnswerView.this = r5;
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        private ARProgressBarContainer(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            ARAnswerView.this = r5;
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
            this.mProgressBar.measure(this.SPIN_WHEEL_WIDTH_MEASURE_SPEC, this.SPIN_WHEEL_HEIGHT_MEASURE_SPEC);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int l2 = ((r - l) / 2) - (this.mProgressBar.getMeasuredWidth() / 2);
            int t2 = ((b - t) / 2) - (this.mProgressBar.getMeasuredHeight() / 2);
            this.mProgressBar.layout(l2, t2, l2 + this.mProgressBar.getMeasuredWidth(), t2 + this.mProgressBar.getMeasuredHeight());
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return true;
        }
    }
}
