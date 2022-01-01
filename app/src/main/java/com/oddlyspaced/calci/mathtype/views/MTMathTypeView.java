package com.oddlyspaced.calci.mathtype.views;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.OverScroller;
import com.oddlyspaced.calci.R;
import com.oddlyspaced.calci.archimedes.views.AROverlayDelegate;
import com.oddlyspaced.calci.archimedes.views.AROverlayView;
import com.oddlyspaced.calci.archimedes.views.ARView;
import com.oddlyspaced.calci.mathtype.enums.MTDigitGroupingStyle;
import com.oddlyspaced.calci.mathtype.enums.MTDragState;
import com.oddlyspaced.calci.mathtype.enums.MTElementInputBehavior;
import com.oddlyspaced.calci.mathtype.enums.MTInlineOperatorType;
import com.oddlyspaced.calci.mathtype.enums.MTNodeTraits;
import com.oddlyspaced.calci.mathtype.enums.MTOperandSide;
import com.oddlyspaced.calci.mathtype.enums.MTOperatorNotation;
import com.oddlyspaced.calci.mathtype.enums.MTParenthesesPlacement;
import com.oddlyspaced.calci.mathtype.enums.MTSelectionHandle;
import com.oddlyspaced.calci.mathtype.enums.MTStringInputBehavior;
import com.oddlyspaced.calci.mathtype.measures.MTMeasures;
import com.oddlyspaced.calci.mathtype.measures.font.MTFontMyriadProLight;
import com.oddlyspaced.calci.mathtype.nodes.MTElement;
import com.oddlyspaced.calci.mathtype.nodes.MTNode;
import com.oddlyspaced.calci.mathtype.nodes.MTString;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTInlineOperator;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTParentheses;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTPower;
import com.oddlyspaced.calci.mathtype.nodes.elements.MTReference;
import com.oddlyspaced.calci.mathtype.parsers.MTOperatorInfo;
import com.oddlyspaced.calci.mathtype.parsers.MTParser;
import com.oddlyspaced.calci.mathtype.parsers.MTTextParser;
import com.oddlyspaced.calci.mathtype.views.input.MTInputBehavior;
import com.oddlyspaced.calci.mathtype.views.input.MTMessageType;
import com.oddlyspaced.calci.mathtype.views.selection.MTSelection;
import com.oddlyspaced.calci.mathtype.views.selection.MTSelectionDrawable;
import com.oddlyspaced.calci.utilities.FloatingOptionsMenu;
import com.oddlyspaced.calci.utilities.GeneralUtil;
import com.oddlyspaced.calci.utilities.PointUtil;
import com.oddlyspaced.calci.utilities.Range;
import com.oddlyspaced.calci.utilities.RectUtil;
import com.oddlyspaced.calci.utilities.Timer;
import com.oddlyspaced.calci.utilities.events.Observer;
import com.oddlyspaced.calci.utilities.events.ObserverType;
import com.oddlyspaced.calci.utilities.observables.ObservableChange;
import com.oddlyspaced.calci.utilities.responder.Responder;
import com.oddlyspaced.calci.utilities.responder.ResponderManager;
import com.oddlyspaced.calci.utilities.responder.ResponderMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes.dex */
public class MTMathTypeView extends View implements Responder, AROverlayDelegate, ARView {
    public static final String AREAS_OF_INTEREST_ON_EDIT = "AREAS_OF_INTEREST_ON_EDIT";
    public static final String DIGIT_GROUPING_STYLE_ATTRIBUTE_NAME = "DIGIT_GROUPING_STYLE_ATTRIBUTE_NAME";
    private static final int MAX_TIME_BETWEEN_TAPS = ViewConfiguration.getLongPressTimeout();
    private static final String MIMETYPE_MATH_TYPE = "archimedes/copiedequation";
    private ValueAnimator mAnimator;
    private HashMap<String, Object> mAttributes;
    private float mAutoScrollSpeed;
    private AutoScrollTask mAutoScrollTask;
    private Timer mAutoScrollTimer;
    private PointF mAutoScrollTouchPoint;
    private AnimatorSet mCursorBlinkAnimation;
    private MTMathTypeViewDelegate mDelegate;
    private boolean mDidAutoInsertAns;
    private MTDragState mDragState;
    private MTSelectionHandle mDraggedHandle;
    private long mLastAutoScrollUpdateTime;
    private long mLastTapDownTime;
    private long mLastTapUpTime;
    private MTMathTypeDrawable mMathTypeDrawable;
    private boolean mNeedsUpdateMathType;
    private boolean mNeedsUpdateScroll;
    private boolean mNeedsUpdateSelection;
    private MTSelection mOldSelection;
    private OverScroller mOverScroller;
    private boolean mPendingMathTypeChangeNotification;
    private int mPreviousEvent;
    private PointF mPreviousTouchPoint;
    private MTSelection mSelection;
    private MTSelectionDrawable mSelectionDrawable;
    private boolean mShouldAnimateMathTypeUpdate;
    private boolean mShouldAnimateScrollUpdate;
    private boolean mShouldAnimateSelectionUpdate;
    private boolean mShouldKeepMeasuresAlignedDuringScaling;
    private boolean mSuspendMathTypeChangeNotification;
    private int mTapCount;
    private PointF mTapDownPoint;
    private Runnable mUpdateRunnable;
    private VelocityTracker mVelocityTracker;
    private boolean mEditable = false;
    private boolean mSelectable = true;
    private ArrayList<ObserverType> mObservers = new ArrayList<>();
    private final Handler mAutoScrollHandler = new Handler();
    private final Handler mLongPressHandler = new Handler();
    private LinearInterpolator mInterpolator = new LinearInterpolator();
    private final long AUTOSCROLL_REFRESH_INTERVAL = (long) getResources().getInteger(R.integer.autoscroll_refresh_interval);
    private final long DEFAULT_ANIMATION_DURATION = (long) getResources().getInteger(R.integer.mathtype_animation_duration);
    private final float AUTOSCROLL_MARGIN = getResources().getDimension(R.dimen.auto_scroll_margin);
    private final float AUTOSCROLL_MAX_SPEED = getResources().getDimension(R.dimen.auto_scroll_max_speed);
    private final float FINGER_MOVEMENT_MARGIN = getResources().getDimension(R.dimen.finger_movement_margin);
    private final float HANDLE_GRAB_MARGIN = getResources().getDimension(R.dimen.handle_grab_margin);
    private final float MAX_DISTANCE_BETWEEN_TAPS = getResources().getDimension(R.dimen.max_distance_between_taps);
    private final float OPTIONS_MENU_MARGIN = getResources().getDimension(R.dimen.options_menu_margin);
    private Runnable waitForLongPress = new Runnable() { // from class: com.oddlyspaced.calci.mathtype.views.MTMathTypeView.3
        @Override // java.lang.Runnable
        public void run() {
            MTMathTypeView.this.handleLongPress(MTMathTypeView.this.mTapDownPoint);
        }
    };

    public MTMathTypeViewDelegate getDelegate() {
        return this.mDelegate;
    }

    public void setDelegate(MTMathTypeViewDelegate delegate) {
        this.mDelegate = delegate;
    }

    public int getColor() {
        return this.mMathTypeDrawable.getColor();
    }

    public void setColor(int color) {
        this.mMathTypeDrawable.setColor(color);
        markMathTypeChanged(true);
    }

    public MTString getString() {
        return this.mMathTypeDrawable.getString();
    }

    public void setString(MTString string) {
        this.mMathTypeDrawable.setString(string);
        if (string != null) {
            updateReferences(string);
        }
        markMathTypeChanged(false);
    }

    public boolean shouldShowPlaceholderGlyphs() {
        return this.mMathTypeDrawable.shouldShowPlaceholderGlyphs();
    }

    public void setShowPlaceholderGlyphs(boolean showPlaceholderGlyphs) {
        this.mMathTypeDrawable.setShowPlaceholderGlyphs(showPlaceholderGlyphs);
        markMathTypeChanged(false);
    }

    public MTMeasures getMeasures() {
        return this.mMathTypeDrawable.getMeasures();
    }

    public MTSelection getSelection() {
        return this.mSelection;
    }

    public void setSelection(MTSelection selection) {
        setSelection(selection, false);
    }

    public void setSelection(MTSelection selection, boolean animated) {
        if (!GeneralUtil.equalOrBothNull(this.mSelection, selection)) {
            if (selection != null) {
                if (!this.mSelectable) {
                    return;
                }
                if (!this.mEditable && selection.isCursor()) {
                    return;
                }
            }
            if (selection != null && !isFirstResponder()) {
                ResponderManager.setFirstResponder(this);
            }
            this.mSelection = selection;
            this.mSelectionDrawable.setSelection(selection);
            markSelectionChanged(animated);
            if (selection != null && selection.isCursor()) {
                resetCursorAnimation();
            } else if (selection != null && selection.isRange()) {
                endCursorAnimation();
            }
        }
    }

    public RectF getSelectionBounds() {
        return this.mSelectionDrawable.getSelectionBounds();
    }

    private void setDraggedHandle(MTSelectionHandle draggedHandle) {
        this.mDraggedHandle = draggedHandle;
    }

    private void setDragState(MTDragState dragState, PointF touchPoint) {
        if (this.mDragState != dragState) {
            MTDragState oldState = this.mDragState;
            this.mDragState = dragState;
            handleDragStateChange(oldState, dragState, touchPoint);
        }
    }

    public HashMap<String, Object> getAttributes() {
        return this.mAttributes;
    }

    public void setAttributes(HashMap<String, Object> attributes) {
        if (!GeneralUtil.equalOrBothNull(this.mAttributes, attributes)) {
            this.mAttributes = new HashMap<>(attributes);
            this.mMathTypeDrawable.setDigitGroupingStyle((MTDigitGroupingStyle) attributes.get(DIGIT_GROUPING_STYLE_ATTRIBUTE_NAME));
            markMathTypeEdited(true);
        }
    }

    public boolean isSelectable() {
        return this.mSelectable;
    }

    public void setSelectable(boolean selectable) {
        if (this.mSelectable != selectable) {
            this.mSelectable = selectable;
            if (!selectable) {
                setSelection(null);
                setEditable(false);
            }
        }
    }

    public boolean isEditable() {
        return this.mEditable;
    }

    public void setEditable(boolean editable) {
        if (this.mEditable != editable) {
            this.mEditable = editable;
            if (editable) {
                setSelectable(true);
            } else if (this.mSelection != null && this.mSelection.isCursor()) {
                setSelection(null);
            }
            setShowPlaceholderGlyphs(editable);
            updateFontSize(isFirstResponder());
        }
    }

    public float getAdditionalPaddingTop() {
        return this.mMathTypeDrawable.getAdditionalPaddingTop();
    }

    public void setAdditionalPaddingTop(float additionalPadding) {
        this.mMathTypeDrawable.setAdditionalPaddingTop(additionalPadding);
        requestLayout();
    }

    public float getAdditionalPaddingBottom() {
        return this.mMathTypeDrawable.getAdditionalPaddingBottom();
    }

    public void setAdditionalPaddingBottom(float additionalPadding) {
        this.mMathTypeDrawable.setAdditionalPaddingBottom(additionalPadding);
        requestLayout();
    }

    private RectF getExpandedSelectionBounds() {
        RectF expandedSelectionBounds = RectUtil.expand(getSelectionBounds(), this.HANDLE_GRAB_MARGIN);
        expandedSelectionBounds.left -= MTSelectionDrawable.HANDLE_RADIUS * 2.0f;
        expandedSelectionBounds.right += MTSelectionDrawable.HANDLE_RADIUS * 2.0f;
        expandedSelectionBounds.bottom += MTSelectionDrawable.HANDLE_RADIUS * 2.0f;
        return expandedSelectionBounds;
    }

    public Rect getBounds() {
        return new Rect(getLeft(), getTop(), getRight(), getBottom());
    }

    private void updateFontSize(boolean isFirstResponder) {
        float fontSize;
        float regularFontSize = getResources().getDimension(R.dimen.regular_font_size);
        float editingFontSize = getResources().getDimension(R.dimen.editing_font_size);
        if (!isFirstResponder || !this.mEditable) {
            fontSize = regularFontSize;
        } else {
            fontSize = editingFontSize;
        }
        this.mMathTypeDrawable.setFont(new MTFontMyriadProLight(fontSize));
        markMathTypeChanged(true);
    }

    private void updateReferences(MTNode node) {
        if (node instanceof MTReference) {
            HashMap<String, Object> messageContents = new HashMap<>();
            messageContents.put("Reference to update", node);
            new ResponderMessage(MTMessageType.UPDATE_REFERENCE, messageContents).send();
        }
        Iterable<? extends MTNode> children = node.getChildren();
        if (children != null) {
            for (MTNode child : children) {
                updateReferences(child);
            }
        }
    }

    private void markMathTypeEdited(boolean shouldAnimateUpdate) {
        markMathTypeChanged(shouldAnimateUpdate);
        this.mPendingMathTypeChangeNotification = true;
        sendMathTypeChangeNotificationIfNeeded();
    }

    private void markMathTypeChanged(boolean shouldAnimateUpdate) {
        this.mShouldAnimateMathTypeUpdate = shouldAnimateUpdate;
        this.mNeedsUpdateMathType = true;
        this.mMathTypeDrawable.setNeedsUpdate();
        scheduleUpdate();
    }

    private void markSelectionChanged(boolean shouldAnimateUpdate) {
        this.mShouldAnimateSelectionUpdate = shouldAnimateUpdate;
        this.mNeedsUpdateSelection = true;
        scheduleUpdate();
    }

    public void markScrollChanged(boolean shouldAnimateUpdate) {
        this.mShouldAnimateScrollUpdate = shouldAnimateUpdate;
        this.mNeedsUpdateScroll = true;
        scheduleUpdate();
    }

    private void markShouldKeepMeasuresAlignedDuringScaling() {
        this.mShouldKeepMeasuresAlignedDuringScaling = true;
        scheduleUpdate();
    }

    private void scheduleUpdate() {
        if (this.mUpdateRunnable == null) {
            this.mUpdateRunnable = new Runnable() { // from class: com.oddlyspaced.calci.mathtype.views.MTMathTypeView.1
                @Override // java.lang.Runnable
                public void run() {
                    MTMathTypeView.this.update();
                    MTMathTypeView.this.mUpdateRunnable = null;
                }
            };
            post(this.mUpdateRunnable);
        }
    }

    public void update() {
        if (this.mNeedsUpdateMathType) {
            if (this.mShouldAnimateMathTypeUpdate) {
                this.mMathTypeDrawable.updateAnimated(this.DEFAULT_ANIMATION_DURATION);
            } else {
                this.mMathTypeDrawable.update();
            }
            this.mNeedsUpdateMathType = false;
        }
        if (this.mNeedsUpdateSelection) {
            if (this.mShouldAnimateSelectionUpdate) {
                this.mSelectionDrawable.updateAnimated(this.DEFAULT_ANIMATION_DURATION);
            } else {
                this.mSelectionDrawable.update();
            }
            this.mNeedsUpdateSelection = false;
        }
        if (this.mNeedsUpdateScroll) {
            scrollToSelection(this.mShouldAnimateScrollUpdate);
            this.mNeedsUpdateScroll = false;
        }
        if (this.mShouldKeepMeasuresAlignedDuringScaling) {
            keepMeasuresAlignedDuringScaling();
            this.mShouldKeepMeasuresAlignedDuringScaling = false;
        }
    }

    private void sendMathTypeChangeNotificationIfNeeded() {
        if (!this.mSuspendMathTypeChangeNotification) {
            if (this.mPendingMathTypeChangeNotification && this.mDelegate != null) {
                this.mDelegate.mathTypeViewDidChange(this);
            }
            this.mPendingMathTypeChangeNotification = false;
        }
    }

    private void suspendMathTypeNotification(Runnable runnable) {
        if (this.mSuspendMathTypeChangeNotification) {
            runnable.run();
            return;
        }
        this.mSuspendMathTypeChangeNotification = true;
        runnable.run();
        this.mSuspendMathTypeChangeNotification = false;
        sendMathTypeChangeNotificationIfNeeded();
    }

    @Override // android.view.View
    public void computeScroll() {
        if (this.mOverScroller.computeScrollOffset()) {
            scrollTo(this.mOverScroller.getCurrX(), this.mOverScroller.getCurrY());
        }
    }

    public MTMathTypeView(Context context) {
        super(context);
        setLayerType(2, null);
        this.mMathTypeDrawable = new MTMathTypeDrawable(context);
        this.mSelectionDrawable = new MTSelectionDrawable(context, this.mMathTypeDrawable);
        this.mMathTypeDrawable.setCallback(this);
        this.mSelectionDrawable.setCallback(this);
        this.mOverScroller = new OverScroller(context);
        this.mObservers.add(ResponderManager.getFirstResponder().getWillChange().add(new Observer<ObservableChange<Responder>>() { // from class: com.oddlyspaced.calci.mathtype.views.MTMathTypeView.2
            public void handle(ObservableChange<Responder> change) {
                MTMathTypeView.this.handleFirstResponderWillChange(change.getOldValue(), change.getNewValue());
            }
        }));
    }

    private MTMathTypeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private MTMathTypeView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    public void deinitialize() {
        if (this.mAnimator != null) {
            this.mAnimator.cancel();
        }
        if (this.mMathTypeDrawable.getAnimator() != null) {
            this.mMathTypeDrawable.getAnimator().cancel();
        }
        if (this.mSelectionDrawable.getAnimator() != null) {
            this.mSelectionDrawable.getAnimator().cancel();
        }
        Iterator<ObserverType> it = this.mObservers.iterator();
        while (it.hasNext()) {
            it.next().remove();
        }
        if (this.mCursorBlinkAnimation != null) {
            this.mCursorBlinkAnimation.end();
        }
        if (this.mAutoScrollTimer != null) {
            this.mAutoScrollTimer.cancel();
            this.mAutoScrollTimer.purge();
        }
        this.mLongPressHandler.removeCallbacks(this.waitForLongPress);
        removeCallbacks(this.mUpdateRunnable);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        float contentHeight = this.mMathTypeDrawable.getCurrentVisualMeasures().getBounds().height();
        float verticalPadding = 2.0f * MTMathTypeDrawable.VERTICAL_CONTENT_PADDING;
        setMeasuredDimension(parentWidth, (int) (contentHeight + verticalPadding + this.mMathTypeDrawable.getAdditionalPaddingTop() + this.mMathTypeDrawable.getAdditionalPaddingBottom()));
    }

    @Override // android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mMathTypeDrawable.setBounds(left, top, right, bottom);
        this.mSelectionDrawable.setBounds(left, top, right, bottom);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0.0f, (this.mMathTypeDrawable.getAdditionalPaddingTop() / 2.0f) - (this.mMathTypeDrawable.getAdditionalPaddingBottom() / 2.0f));
        this.mMathTypeDrawable.draw(canvas);
        if (isFirstResponder()) {
            AROverlayView.getInstance(getContext()).drawDrawable(this.mSelectionDrawable);
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who);
    }

    @Override // android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        if (drawable instanceof MTMathTypeDrawable) {
            requestLayout();
            invalidate();
        } else if (isFirstResponder()) {
            AROverlayView.getInstance(getContext()).drawDrawable(this.mSelectionDrawable);
        }
    }

    public boolean isFirstResponder() {
        return ResponderManager.getFirstResponder().getValue() == this;
    }

    public void handleFirstResponderWillChange(Responder oldValue, Responder newValue) {
        if (this == newValue) {
            updateFontSize(true);
            markScrollChanged(true);
            AROverlayView.getInstance(getContext()).setActiveView(this, this);
        } else if (this == oldValue) {
            updateFontSize(false);
            hideOptionsMenu(false);
            setSelection(null);
            markShouldKeepMeasuresAlignedDuringScaling();
            if (newValue == null) {
                AROverlayView.getInstance(getContext()).setActiveView(null, null);
            }
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        PointF touchPoint = convertFromViewToDrawable(event.getX(), event.getY());
        switch (event.getAction()) {
            case 0:
                this.mLastTapDownTime = SystemClock.uptimeMillis();
                this.mTapDownPoint = touchPoint;
                this.mPreviousEvent = 0;
                this.mLongPressHandler.postDelayed(this.waitForLongPress, (long) MAX_TIME_BETWEEN_TAPS);
                if (this.mVelocityTracker == null) {
                    this.mVelocityTracker = VelocityTracker.obtain();
                } else {
                    this.mVelocityTracker.clear();
                }
                this.mVelocityTracker.addMovement(event);
                this.mOverScroller.forceFinished(true);
                postInvalidate();
                return true;
            case 1:
                this.mLongPressHandler.removeCallbacks(this.waitForLongPress);
                setDragState(MTDragState.None, touchPoint);
                if (this.mPreviousEvent == 2) {
                    this.mVelocityTracker.addMovement(event);
                    this.mVelocityTracker.computeCurrentVelocity(1000);
                    handleFling(this.mTapDownPoint, touchPoint, this.mVelocityTracker.getXVelocity(), this.mVelocityTracker.getYVelocity());
                    return true;
                }
                this.mPreviousEvent = 1;
                long timeDifference = SystemClock.uptimeMillis() - this.mLastTapUpTime;
                this.mLastTapUpTime = SystemClock.uptimeMillis();
                this.mTapCount = timeDifference > ((long) MAX_TIME_BETWEEN_TAPS) ? 0 : this.mTapCount + 1;
                if (this.mLastTapUpTime - this.mLastTapDownTime > ((long) MAX_TIME_BETWEEN_TAPS)) {
                    this.mTapCount = -1;
                    return true;
                }
                if (this.mTapCount > 3) {
                    this.mTapCount = 0;
                }
                if (this.mPreviousTouchPoint != null && PointUtil.distanceToPoint(touchPoint, this.mPreviousTouchPoint) > ((double) this.MAX_DISTANCE_BETWEEN_TAPS)) {
                    this.mTapCount = 0;
                }
                switch (this.mTapCount) {
                    case 0:
                        handleSingleTap(touchPoint);
                        break;
                    case 1:
                        handleDoubleTap(touchPoint);
                        break;
                    case 2:
                        handleTripleTap(touchPoint);
                        break;
                    case 3:
                        handleQuadrupleTap(touchPoint);
                        break;
                }
                this.mPreviousTouchPoint = touchPoint;
                this.mOverScroller.forceFinished(true);
                postInvalidate();
                return true;
            case 2:
                if (PointUtil.distanceToPoint(touchPoint, this.mTapDownPoint) < ((double) this.FINGER_MOVEMENT_MARGIN)) {
                    return true;
                }
                if (this.mPreviousEvent == 0 && this.mSelection != null && this.mSelection.isRange()) {
                    setDragState(MTDragState.Handle, touchPoint);
                }
                this.mPreviousEvent = 2;
                float distanceX = this.mTapDownPoint.x - touchPoint.x;
                float distanceY = this.mTapDownPoint.y - touchPoint.y;
                this.mVelocityTracker.addMovement(event);
                this.mVelocityTracker.computeCurrentVelocity(1000);
                handleScroll(this.mTapDownPoint, touchPoint, distanceX, distanceY);
                this.mLongPressHandler.removeCallbacks(this.waitForLongPress);
                return true;
            case 3:
                this.mLongPressHandler.removeCallbacks(this.waitForLongPress);
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
                this.mPreviousEvent = -1;
                return true;
            default:
                return true;
        }
    }

    private void handleSingleTap(PointF touchPoint) {
        if (this.mSelectable) {
            if (!isOptionsMenuVisible() && this.mSelection != null && this.mSelection.isRange() && RectUtil.expand(getSelectionBounds(), this.HANDLE_GRAB_MARGIN).contains(touchPoint.x, touchPoint.y)) {
                showOptionsMenu(true);
            } else if (this.mEditable) {
                MTSelection oldSelection = this.mSelection;
                MTSelection newSelection = selectionForTouchPoint(touchPoint, getMeasures(), 1);
                setSelection(newSelection);
                if (isOptionsMenuVisible() || !newSelection.equals(oldSelection)) {
                    hideOptionsMenu(true);
                } else {
                    showOptionsMenu(true);
                }
            } else if (this.mSelection != null) {
                setSelection(null);
                hideOptionsMenu(true);
            } else if (!isFirstResponder()) {
                ResponderManager.setFirstResponder(this);
            }
        }
    }

    private void handleDoubleTap(PointF touchPoint) {
        if (this.mSelectable) {
            hideOptionsMenu(true);
            MTSelection newSelection = selectionForTouchPoint(touchPoint, getMeasures(), 2);
            if (newSelection != null) {
                setSelection(newSelection);
            }
        }
    }

    private void handleTripleTap(PointF touchPoint) {
        if (this.mSelectable) {
            hideOptionsMenu(true);
            MTSelection newSelection = selectionForTouchPoint(touchPoint, getMeasures(), 3);
            if (newSelection != null) {
                setSelection(newSelection);
            }
        }
    }

    private void handleQuadrupleTap(PointF touchPoint) {
        if (this.mSelectable) {
            hideOptionsMenu(true);
            MTSelection newSelection = MTSelection.selectionWithEntireString(getString());
            if (newSelection != null) {
                setSelection(newSelection);
            }
        }
    }

    public void handleLongPress(PointF touchPoint) {
        if (this.mSelectable) {
            hideOptionsMenu(true);
            MTSelection newSelection = selectionForTouchPoint(touchPoint, getMeasures(), 2);
            if (newSelection != null) {
                setSelection(newSelection);
                performHapticFeedback(0);
            }
        }
    }

    private void handleScroll(PointF startTouchPoint, PointF currentTouchPoint, float distanceX, float distanceY) {
        hideOptionsMenu(true);
        if (this.mSelection == null || !this.mSelection.isRange() || this.mDraggedHandle == MTSelectionHandle.None) {
            float contentWidth = getMeasures().getBounds().width();
            if (contentWidth >= ((float) getBounds().width()) - (MTMathTypeDrawable.HORIZONTAL_CONTENT_PADDING * 2.0f)) {
                scrollBy((int) GeneralUtil.constrain(distanceX, (((((float) getBounds().width()) - contentWidth) / 2.0f) - MTMathTypeDrawable.HORIZONTAL_CONTENT_PADDING) - ((float) getScrollX()), (((contentWidth - ((float) getBounds().width())) / 2.0f) + MTMathTypeDrawable.HORIZONTAL_CONTENT_PADDING) - ((float) getScrollX())), 0);
                return;
            }
            return;
        }
        updateDrag(currentTouchPoint);
    }

    private void handleFling(PointF beginPoint, PointF currentPoint, float velocityX, float velocityY) {
        float contentWidth = getMeasures().getBounds().width();
        if (contentWidth >= ((float) getBounds().width())) {
            float minX = ((((float) getBounds().width()) - contentWidth) / 2.0f) - MTMathTypeDrawable.HORIZONTAL_CONTENT_PADDING;
            float maxX = ((contentWidth - ((float) getBounds().width())) / 2.0f) + MTMathTypeDrawable.HORIZONTAL_CONTENT_PADDING;
            this.mOverScroller.forceFinished(true);
            this.mOverScroller.fling(getScrollX(), getScrollY(), (int) (-velocityX), (int) (-velocityY), (int) minX, (int) maxX, 0, 0);
            postInvalidate();
        }
    }

    private void handleDragStateChange(MTDragState oldState, MTDragState newState, PointF touchPoint) {
        if (newState == MTDragState.Cursor) {
            setDraggedHandle(MTSelectionHandle.None);
        } else if (newState == MTDragState.Handle) {
            setDraggedHandle(selectionHandleForTouchPoint(touchPoint));
            if (this.mDraggedHandle == MTSelectionHandle.None) {
                setDragState(MTDragState.None, touchPoint);
                return;
            }
            this.mOldSelection = this.mSelection;
        } else if (newState == MTDragState.None) {
            setDraggedHandle(MTSelectionHandle.None);
            if (this.mAutoScrollTimer != null) {
                this.mAutoScrollTimer.cancel();
                this.mAutoScrollTimer.purge();
            }
        }
        if (newState != MTDragState.None) {
            updateDrag(touchPoint);
        }
    }

    private void updateDrag(PointF touchPoint) {
        if (this.mDragState == MTDragState.Cursor) {
            setSelection(selectionForTouchPoint(touchPoint, getMeasures(), 1), true);
        } else if (this.mDragState == MTDragState.Handle) {
            if (this.mDraggedHandle == MTSelectionHandle.Indeterminate) {
                MTSelection selectionWithLeftHandle = selectionAfterDraggingHandle(MTSelectionHandle.Left, touchPoint, this.mOldSelection);
                MTSelection selectionWithRightHandle = selectionAfterDraggingHandle(MTSelectionHandle.Right, touchPoint, this.mOldSelection);
                if (selectionWithLeftHandle.getLength() > 1) {
                    setDraggedHandle(MTSelectionHandle.Left);
                    setSelection(selectionWithLeftHandle, false);
                } else if (selectionWithRightHandle.getLength() > 1) {
                    setDraggedHandle(MTSelectionHandle.Right);
                    setSelection(selectionWithRightHandle, false);
                } else {
                    setSelection(selectionWithLeftHandle, false);
                }
            } else if (this.mDraggedHandle != MTSelectionHandle.None) {
                setSelection(selectionAfterDraggingHandle(this.mDraggedHandle, touchPoint, this.mOldSelection), false);
            }
        }
        if (getMeasures().getBounds().width() > ((float) getBounds().width())) {
            checkDragForAutoScroll(touchPoint);
        }
    }

    private void keepMeasuresAlignedDuringScaling() {
        RectF absoluteViewBounds = RectUtil.setOrigin(new RectF(getBounds()), (float) getScrollX(), (float) getScrollY());
        RectF measuresBounds = this.mMathTypeDrawable.getMeasures().getAbsoluteBounds();
        float gapRight = (absoluteViewBounds.right - MTMathTypeDrawable.HORIZONTAL_CONTENT_PADDING) - measuresBounds.right;
        float gapLeft = (measuresBounds.left - absoluteViewBounds.left) - MTMathTypeDrawable.HORIZONTAL_CONTENT_PADDING;
        final float dx = 0.0f;
        if (gapRight > 0.0f && gapLeft < 0.0f) {
            dx = -gapRight;
        } else if (gapRight < 0.0f && gapLeft > 0.0f) {
            dx = gapLeft;
        }
        if (measuresBounds.width() < absoluteViewBounds.width() - (2.0f * MTMathTypeDrawable.HORIZONTAL_CONTENT_PADDING)) {
            dx = measuresBounds.centerX() - absoluteViewBounds.centerX();
        }
        this.mOverScroller.forceFinished(true);
        final int startX = getScrollX();
        final int startY = getScrollY();
        long remainingTime = GeneralUtil.constrain(this.mMathTypeDrawable.getAnimator().getDuration() - this.mMathTypeDrawable.getAnimator().getCurrentPlayTime(), 0, this.mMathTypeDrawable.getAnimator().getDuration());
        if (this.mAnimator != null) {
            this.mAnimator.cancel();
        }
        this.mAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.mAnimator.setInterpolator(this.mInterpolator);
        this.mAnimator.setDuration(remainingTime);
        this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.oddlyspaced.calci.mathtype.views.MTMathTypeView.4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                MTMathTypeView.this.scrollTo(startX + ((int) (valueAnimator.getAnimatedFraction() * dx)), startY);
            }
        });
        this.mAnimator.start();
    }

    private void scrollToSelection(boolean animated) {
        if (getSelectionBounds() != null) {
            RectF areaToScrollTo = RectUtil.expand(convertFromDrawableToView(getSelectionBounds()), (float) (getBounds().width() / 4));
            float scrollX = (float) getScrollX();
            float distanceBeyondRight = areaToScrollTo.right - ((float) getBounds().right);
            float distanceBeyondLeft = ((float) getBounds().left) - areaToScrollTo.left;
            if (distanceBeyondLeft > 0.0f && distanceBeyondRight < 0.0f) {
                scrollX += -Math.min(distanceBeyondLeft, -distanceBeyondRight);
            } else if (distanceBeyondRight > 0.0f && distanceBeyondLeft < 0.0f) {
                scrollX += Math.min(distanceBeyondRight, -distanceBeyondLeft);
            }
            float maxScrollX = Math.max((RectUtil.expand(getMeasures().getBounds(), MTMathTypeDrawable.HORIZONTAL_CONTENT_PADDING).width() - ((float) getBounds().width())) / 2.0f, 0.0f);
            float scrollX2 = GeneralUtil.constrain(scrollX, -maxScrollX, maxScrollX);
            this.mOverScroller.forceFinished(true);
            int duration = (int) (animated ? this.DEFAULT_ANIMATION_DURATION : 0);
            final float scrollDistance = scrollX2 - ((float) getScrollX());
            final int startX = getScrollX();
            final int startY = getScrollY();
            if (this.mAnimator != null) {
                this.mAnimator.cancel();
            }
            this.mAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.mAnimator.setInterpolator(this.mInterpolator);
            this.mAnimator.setDuration((long) duration);
            this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.oddlyspaced.calci.mathtype.views.MTMathTypeView.5
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    MTMathTypeView.this.scrollTo(startX + ((int) (valueAnimator.getAnimatedFraction() * scrollDistance)), startY);
                }
            });
            this.mAnimator.start();
        }
    }

    private void checkDragForAutoScroll(PointF touchPoint) {
        this.mAutoScrollSpeed = determineAutoScrollSpeed(touchPoint);
        if (this.mAutoScrollSpeed != 0.0f) {
            this.mAutoScrollTouchPoint = touchPoint;
            if (this.mAutoScrollTimer == null || this.mAutoScrollTimer.isCancelled()) {
                this.mLastAutoScrollUpdateTime = SystemClock.uptimeMillis();
                this.mAutoScrollTimer = new Timer();
                this.mAutoScrollTask = new AutoScrollTask();
                this.mAutoScrollTimer.schedule(this.mAutoScrollTask, new Date(), this.AUTOSCROLL_REFRESH_INTERVAL);
            }
        } else if (this.mAutoScrollTimer != null) {
            this.mAutoScrollTimer.cancel();
            this.mAutoScrollTimer.purge();
        }
    }

    /* loaded from: classes.dex */
    public class AutoScrollTask extends TimerTask {
        private AutoScrollTask() {
            MTMathTypeView.this = r1;
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            MTMathTypeView.this.mAutoScrollHandler.post(new Runnable() { // from class: com.oddlyspaced.calci.mathtype.views.MTMathTypeView.AutoScrollTask.1
                @Override // java.lang.Runnable
                public void run() {
                    MTMathTypeView.this.updateAutoScroll(MTMathTypeView.this.mAutoScrollTouchPoint);
                    MTMathTypeView.this.mLastAutoScrollUpdateTime = SystemClock.uptimeMillis();
                }
            });
        }
    }

    private float determineAutoScrollSpeed(PointF touchPoint) {
        PointF touchPoint2 = convertFromDrawableToView(touchPoint);
        if (this.mDragState == MTDragState.None) {
            return 0.0f;
        }
        float dragTouchPointX = constrainDragTouchPointToOppositeHandle(touchPoint2);
        float distanceToLeftEdge = dragTouchPointX - ((float) getBounds().left);
        float distanceToRightEdge = ((float) getBounds().right) - dragTouchPointX;
        float margin = Math.min(this.AUTOSCROLL_MARGIN, (float) getBounds().width());
        float speed = 0.0f;
        if (distanceToLeftEdge < margin) {
            speed = -1.0f + GeneralUtil.constrain(distanceToLeftEdge / margin, 0.0f, 1.0f);
        }
        if (distanceToRightEdge < margin) {
            speed = 1.0f - GeneralUtil.constrain(distanceToRightEdge / margin, 0.0f, 1.0f);
        }
        return speed * this.AUTOSCROLL_MAX_SPEED;
    }

    private float constrainDragTouchPointToOppositeHandle(PointF touchPoint) {
        float touchX = touchPoint.x;
        RectF selectionBounds = convertFromDrawableToView(getSelectionBounds());
        if (this.mDraggedHandle == MTSelectionHandle.Right) {
            return Math.max(touchX, selectionBounds.left);
        }
        if (this.mDraggedHandle == MTSelectionHandle.Left) {
            return Math.min(touchX, selectionBounds.right);
        }
        return touchX;
    }

    public void updateAutoScroll(PointF touchPoint) {
        long now = SystemClock.uptimeMillis();
        long elapsedTime = SystemClock.uptimeMillis() - this.mLastAutoScrollUpdateTime;
        this.mLastAutoScrollUpdateTime = now;
        float distanceX = this.mAutoScrollSpeed * ((float) elapsedTime);
        float contentWidth = getMeasures().getBounds().width();
        float distanceX2 = GeneralUtil.constrain(distanceX, (((((float) getBounds().width()) - contentWidth) / 2.0f) - MTMathTypeDrawable.HORIZONTAL_CONTENT_PADDING) - ((float) getScrollX()), (((contentWidth - ((float) getBounds().width())) / 2.0f) + MTMathTypeDrawable.HORIZONTAL_CONTENT_PADDING) - ((float) getScrollX()));
        scrollBy((int) distanceX2, 0);
        touchPoint.x += (float) ((int) distanceX2);
        updateDrag(touchPoint);
    }

    private MTSelectionHandle selectionHandleForTouchPoint(PointF touchPoint) {
        MTSelection selection = this.mSelection;
        if (selection != null && selection.isRange() && getExpandedSelectionBounds().contains(touchPoint.x, touchPoint.y)) {
            float distanceFromLeft = Math.abs(getSelectionBounds().left - touchPoint.x);
            float distanceFromRight = Math.abs(getSelectionBounds().right - touchPoint.x);
            if (distanceFromLeft < this.HANDLE_GRAB_MARGIN && distanceFromRight < this.HANDLE_GRAB_MARGIN && selection.getLength() == 1) {
                return MTSelectionHandle.Indeterminate;
            }
            if (distanceFromLeft < distanceFromRight) {
                if (distanceFromLeft < this.HANDLE_GRAB_MARGIN) {
                    return MTSelectionHandle.Left;
                }
            } else if (distanceFromRight < this.HANDLE_GRAB_MARGIN) {
                return MTSelectionHandle.Right;
            }
        }
        return MTSelectionHandle.None;
    }

    private MTSelection selectionAfterDraggingHandle(MTSelectionHandle handle, PointF touchPoint, MTSelection oldSelection) {
        int otherHandleIndex;
        int leftIndex;
        MTSelection handlePosition = selectionForTouchPoint(touchPoint, getMeasures(), 1);
        int indexOfElementContainingOldStringInNewString = handlePosition.getString().indexOfElementWithDescendant(oldSelection.getString());
        int indexOfElementContainingNewStringInOldString = oldSelection.getString().indexOfElementWithDescendant(handlePosition.getString());
        if (handlePosition.getString() != oldSelection.getString() && indexOfElementContainingNewStringInOldString == -1 && indexOfElementContainingOldStringInNewString == -1) {
            MTString string = oldSelection.getString();
            do {
                string = string.getParent().getParent();
                if (string == null) {
                    return null;
                }
            } while (string.indexOfElementWithDescendant(handlePosition.getString()) == -1);
            oldSelection = MTSelection.selectionInString(string, string.indexOfElementWithDescendant(oldSelection.getString()), 1);
            indexOfElementContainingNewStringInOldString = oldSelection.getString().indexOfElementWithDescendant(handlePosition.getString());
        }
        if (indexOfElementContainingOldStringInNewString != -1) {
            otherHandleIndex = handle == MTSelectionHandle.Left ? indexOfElementContainingOldStringInNewString + 1 : indexOfElementContainingOldStringInNewString;
        } else if (indexOfElementContainingNewStringInOldString != -1) {
            otherHandleIndex = handle == MTSelectionHandle.Left ? oldSelection.getIndexAfterSelection() : oldSelection.getIndex();
            if (handle == MTSelectionHandle.Left && indexOfElementContainingNewStringInOldString + 1 == otherHandleIndex) {
                otherHandleIndex = handlePosition.getString().indexAfterLastElement();
            } else if (handle == MTSelectionHandle.Right && indexOfElementContainingNewStringInOldString == otherHandleIndex) {
                otherHandleIndex = 0;
            } else {
                handlePosition = MTSelection.cursorInStringAtIndex(oldSelection.getString(), (touchPoint.x > getMeasures().descendantForNode(oldSelection.getString().elementWithDescendant(handlePosition.getString())).getAbsoluteBounds().centerX() ? 1 : (touchPoint.x == getMeasures().descendantForNode(oldSelection.getString().elementWithDescendant(handlePosition.getString())).getAbsoluteBounds().centerX() ? 0 : -1)) < 0 ? indexOfElementContainingNewStringInOldString : indexOfElementContainingNewStringInOldString + 1);
            }
        } else {
            otherHandleIndex = handle == MTSelectionHandle.Left ? oldSelection.getIndexAfterSelection() : oldSelection.getIndex();
        }
        if (handle == MTSelectionHandle.Left) {
            leftIndex = handlePosition.getIndex();
        } else {
            leftIndex = otherHandleIndex;
        }
        int rightIndex = handle == MTSelectionHandle.Left ? otherHandleIndex : handlePosition.getIndex();
        if (leftIndex >= rightIndex) {
            if (handle == MTSelectionHandle.Left && rightIndex > 0) {
                leftIndex = rightIndex - 1;
            } else if (handle != MTSelectionHandle.Right || leftIndex + 1 > handlePosition.getString().indexAfterLastElement()) {
                return null;
            } else {
                rightIndex = leftIndex + 1;
            }
        }
        return MTSelection.selectionInString(handlePosition.getString(), leftIndex, rightIndex - leftIndex);
    }

    private MTSelection selectionForTouchPoint(PointF touchPoint, MTMeasures measures, int tapCount) {
        int cursorIndex;
        MTNode node = measures.getNode();
        if (node.getTraits() != null && node.getTraits().contains(MTNodeTraits.CantSelectOrEditChildren)) {
            return null;
        }
        PointF touchPointInMeasures = PointUtil.subtractPoints(touchPoint, measures.getAbsolutePosition());
        MTSelection closestChildSelection = null;
        MTMeasures closestChildMeasures = null;
        float closestChildDistanceAsFloat = Float.POSITIVE_INFINITY;
        PointF closestChildDistanceAsPoint = new PointF(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        if (!measures.getChildren().isEmpty()) {
            Iterator<MTMeasures> it = measures.getChildren().iterator();
            while (it.hasNext()) {
                MTMeasures childMeasures = it.next();
                if (childMeasures.getNode().getTraits() == null || !childMeasures.getNode().getTraits().contains(MTNodeTraits.CantSelectOrEditChildren)) {
                    PointF point = PointUtil.subtractPoints(touchPointInMeasures, childMeasures.getPosition());
                    float distance = PointUtil.distanceToRectAsFloat(point, childMeasures.getBounds());
                    PointF distanceAsPoint = PointUtil.distanceToRectAsPoint(point, childMeasures.getBounds());
                    if (distanceAsPoint.x + (distanceAsPoint.y * 2.0f) < closestChildDistanceAsPoint.x + (closestChildDistanceAsPoint.y * 2.0f)) {
                        closestChildMeasures = childMeasures;
                        closestChildDistanceAsFloat = distance;
                        closestChildDistanceAsPoint = distanceAsPoint;
                        if (distance <= 0.0f) {
                            break;
                        }
                    } else {
                        continue;
                    }
                }
            }
            if (!(closestChildMeasures == null || (closestChildSelection = selectionForTouchPoint(touchPoint, closestChildMeasures, tapCount)) == null || closestChildDistanceAsFloat > 0.0f)) {
                return closestChildSelection;
            }
        } else if (node instanceof MTString) {
            if (tapCount > 1) {
                return null;
            }
            return MTSelection.cursorAtEndOfString((MTString) node);
        }
        if (!(node instanceof MTElement) || node.getParent() == null) {
            return closestChildSelection;
        }
        MTElement element = (MTElement) node;
        int index = element.indexInParentString();
        float distanceToEdge = Math.min(Math.abs(touchPointInMeasures.x - measures.getBounds().left), Math.abs(measures.getBounds().right - touchPointInMeasures.x));
        float distanceFromChildCenter = closestChildMeasures == null ? closestChildDistanceAsFloat : closestChildDistanceAsFloat + (closestChildMeasures.getBounds().width() / 2.0f);
        float distanceThatAlwaysCountsAsHit = closestChildMeasures == null ? 0.0f : 2.0f * closestChildMeasures.getFontSizeInPixels();
        float allowableYDistance = closestChildMeasures == null ? 0.0f : 0.5f * closestChildMeasures.getFontSizeInPixels();
        if (distanceFromChildCenter < distanceThatAlwaysCountsAsHit && closestChildDistanceAsFloat < distanceToEdge && closestChildDistanceAsPoint.y < allowableYDistance) {
            return closestChildSelection;
        }
        if (tapCount >= 2) {
            Range range = MTParser.rangeOfInfluenceForElementsInRangeInString(new Range(index, 1), element.getParent(), false);
            if (tapCount < 3) {
                return MTSelection.selectionInString(element.getParent(), range);
            }
            Range rangeExtended = MTParser.rangeOfInfluenceForElementsInRangeInString(new Range(index, 1), element.getParent(), true);
            if (range.equals(rangeExtended)) {
                return MTSelection.selectionWithEntireString(element.getParent());
            }
            return MTSelection.selectionInString(element.getParent(), rangeExtended);
        }
        float distanceLimitToSelectChild = closestChildMeasures == null ? 0.0f : 2.0f * closestChildMeasures.getFontSizeInPixels();
        if (distanceToEdge >= closestChildDistanceAsFloat && closestChildDistanceAsFloat <= distanceLimitToSelectChild) {
            return closestChildSelection;
        }
        if (touchPointInMeasures.x - measures.getBounds().centerX() > 0.0f) {
            cursorIndex = index + 1;
        } else {
            cursorIndex = index;
        }
        return MTSelection.cursorInStringAtIndex(element.getParent(), cursorIndex);
    }

    private void startCursorAnimation() {
        this.mCursorBlinkAnimation = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.cursor_animation);
        this.mCursorBlinkAnimation.setInterpolator(new MTCursorInterpolator());
        this.mCursorBlinkAnimation.setTarget(this.mSelectionDrawable);
        this.mCursorBlinkAnimation.setStartDelay(125);
        this.mSelectionDrawable.setShouldBlinkCursor(true);
        this.mCursorBlinkAnimation.start();
    }

    private void resetCursorAnimation() {
        if (this.mCursorBlinkAnimation == null) {
            startCursorAnimation();
            return;
        }
        endCursorAnimation();
        this.mSelectionDrawable.setShouldBlinkCursor(true);
        this.mCursorBlinkAnimation.start();
    }

    private void endCursorAnimation() {
        this.mSelectionDrawable.setShouldBlinkCursor(false);
        this.mSelectionDrawable.resetCursorAlpha();
        if (this.mCursorBlinkAnimation != null) {
            this.mCursorBlinkAnimation.cancel();
        }
    }

    /* loaded from: classes.dex */
    public class MTCursorInterpolator implements Interpolator {
        private MTCursorInterpolator() {
            MTMathTypeView.this = r1;
        }

        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float t) {
            if (t < 0.25f) {
                return 1.0f;
            }
            if (t < 0.25f || t >= 0.75f) {
                return 0.0f;
            }
            return 1.0f - (2.0f * (t - 0.25f));
        }
    }

    @Override // com.oddlyspaced.calci.utilities.responder.Responder
    public boolean canHandleMessageType(String type) {
        if (this.mEditable) {
            return type.equals(MTMessageType.INSERT_ELEMENT) || type.equals(MTMessageType.BACKSPACE) || type.equals(MTMessageType.CLEAR_LINE) || type.equals(MTMessageType.COPY) || type.equals(MTMessageType.PASTE);
        }
        return type.equals(MTMessageType.COPY);
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
        return true;
    }

    @Override // com.oddlyspaced.calci.utilities.responder.Responder
    public void handleMessage(String type, HashMap<String, Object> contents) {
        if (type.equals(MTMessageType.INSERT_ELEMENT)) {
            insertElement((MTElement) contents.get("Element to insert"), true);
            markScrollChanged(true);
            new ResponderMessage(MTMessageType.DID_PERFORM_EDIT, null).send();
        } else if (type.equals(MTMessageType.BACKSPACE)) {
            backspace(true);
            markScrollChanged(true);
            new ResponderMessage(MTMessageType.DID_PERFORM_EDIT, null).send();
        } else if (type.equals(MTMessageType.CLEAR_LINE)) {
            clearLine(false);
            markScrollChanged(false);
            new ResponderMessage(MTMessageType.DID_PERFORM_EDIT, null).send();
        } else if (type.equals(MTMessageType.COPY)) {
            copy();
        } else if (type.equals(MTMessageType.PASTE)) {
            paste();
            markScrollChanged(false);
            new ResponderMessage(MTMessageType.DID_PERFORM_EDIT, null).send();
        }
    }

    private void copy() {
        hideOptionsMenu(true);
        if ((this.mSelection != null ? this.mSelection : MTSelection.selectionWithEntireString(getString())).getString() != null) {
            MTString string = new MTString();
            this.mSelection.getString().copyElementsInRangeToString(this.mSelection.getRange(), string, 0);
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService("clipboard");
            File file = new File(getContext().getFilesDir(), "CopiedArchimedesEquation.ser");
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
                objectOutputStream.writeObject(string);
                objectOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            clipboard.setPrimaryClip(new ClipData(new ClipDescription("Serialized Archimedes Equation", new String[]{MIMETYPE_MATH_TYPE}), new ClipData.Item(Uri.parse(file.getAbsolutePath()))));
        }
    }

    public void paste() {
        String pasteText;
        Uri pasteUri;
        String uriMimeType;
        hideOptionsMenu(true);
        ClipboardManager clipBoard = (ClipboardManager) getContext().getSystemService("clipboard");
        if (clipBoard.hasPrimaryClip()) {
            if (!clipBoard.getPrimaryClipDescription().hasMimeType("text/plain")) {
                ClipData clip = clipBoard.getPrimaryClip();
                if (clip != null && (pasteUri = clip.getItemAt(0).getUri()) != null && clip.getDescription() != null && (uriMimeType = clip.getDescription().getMimeType(0)) != null && uriMimeType.equals(MIMETYPE_MATH_TYPE)) {
                    try {
                        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(new File(pasteUri.toString())));
                        insertStringIntoSelectionOrOverwriteSelectionWithString((MTString) objectInputStream.readObject());
                        objectInputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
                if (item.getText() != null && (pasteText = item.getText().toString()) != null) {
                    insertStringIntoSelectionOrOverwriteSelectionWithString(new MTTextParser().parseText(pasteText).get(0));
                }
            }
        }
    }

    private void insertStringIntoSelectionOrOverwriteSelectionWithString(MTString string) {
        if (this.mSelection != null && this.mSelection.isValid()) {
            int selectionIndex = this.mSelection.getIndex();
            if (this.mSelection.isRange()) {
                this.mSelection.getString().removeElementsInRange(this.mSelection.getRange());
            }
            for (MTElement element : string.getChildren()) {
                this.mSelection.getString().insertElement(element, selectionIndex);
                selectionIndex++;
            }
            updateReferences(string);
            markMathTypeEdited(false);
            setSelection(MTSelection.cursorInStringAtIndex(this.mSelection.getString(), this.mSelection.getIndex() + string.length()));
        }
    }

    public void insertElement(final MTElement element, final boolean animated) {
        suspendMathTypeNotification(new Runnable() { // from class: com.oddlyspaced.calci.mathtype.views.MTMathTypeView.6
            @Override // java.lang.Runnable
            public void run() {
                MTMathTypeView.this.insertElementInternal(element, animated);
            }
        });
    }

    public void insertElementInternal(MTElement element, boolean animated) {
        hideOptionsMenu(true);
        if (getString() != null) {
            AtomicReference<MTSelection> selectionReference = new AtomicReference<>(this.mSelection == null ? MTSelection.cursorAtEndOfString(getString()) : this.mSelection);
            AtomicReference<MTElement> elementReference = new AtomicReference<>(element);
            checkForSubstitutionsForInsertionOfElement(elementReference, selectionReference);
            MTElement element2 = elementReference.get();
            insertAnsIfAppropriateForInsertionOfElement(element2, selectionReference);
            preventDoubleBaselineShiftForInsertionOfElement(element2, selectionReference);
            grabOrRemoveElementsForInsertionOfElement(element2, selectionReference);
            placeParenthesesAsNeededForInsertionOfElement(element2, selectionReference);
            selectionReference.get().getString().insertElement(element2, insertionIndexForElement(element2, selectionReference));
            markMathTypeEdited(animated);
            selectionReference.set(selectionAfterInsertionOfElementAtIndex(element2));
            setSelection(selectionReference.get(), animated);
        }
    }

    private void checkForSubstitutionsForInsertionOfElement(AtomicReference<MTElement> element, AtomicReference<MTSelection> selection) {
        if ((element.get() instanceof MTInlineOperator) && ((MTInlineOperator) element.get()).getType() == MTInlineOperatorType.Dot && selection.get().isCursor()) {
            MTElement elementBeforeCursor = selection.get().elementBeforeSelection();
            if ((elementBeforeCursor instanceof MTInlineOperator) && ((MTInlineOperator) elementBeforeCursor).getType() == MTInlineOperatorType.Dot) {
                elementBeforeCursor.removeFromParentString();
                selection.set(MTSelection.cursorInStringAtIndex(selection.get().getString(), selection.get().getIndex() - 1));
                element.set(new MTPower());
            }
        }
    }

    private void insertAnsIfAppropriateForInsertionOfElement(MTElement element, AtomicReference<MTSelection> selection) {
        if (!getString().isNotEmpty() && !this.mDidAutoInsertAns && MTInputBehavior.behaviorForElement(element).hasBehavior(MTElementInputBehavior.UseAutoAns) && new ResponderMessage(MTMessageType.SHOULD_AUTO_INSERT_ANS, null).send()) {
            selection.set(MTSelection.cursorAtEndOfString(getString()));
            this.mDidAutoInsertAns = true;
        }
    }

    private void preventDoubleBaselineShiftForInsertionOfElement(MTElement element, AtomicReference<MTSelection> selection) {
        MTInputBehavior elementBehavior = MTInputBehavior.behaviorForElement(element);
        MTInputBehavior selectionParentBehavior = MTInputBehavior.behaviorForElement(selection.get().getString().getParent());
        boolean elementIsBaselineShift = elementBehavior.hasBehavior(MTElementInputBehavior.IsBaselineShift);
        boolean selectionParentIsBaselineShift = selectionParentBehavior.hasBehavior(MTElementInputBehavior.IsBaselineShift);
        if (elementIsBaselineShift && selectionParentIsBaselineShift && selection.get().getString().isEmpty()) {
            MTString selectionStringParentString = selection.get().getString().getParent().getParent();
            int index = selectionStringParentString.indexOfElementWithDescendant(selection.get().getString());
            selectionStringParentString.removeElementAtIndex(index);
            selection.set(MTSelection.cursorInStringAtIndex(selectionStringParentString, index));
        }
    }

    private void grabOrRemoveElementsForInsertionOfElement(MTElement element, AtomicReference<MTSelection> selection) {
        MTOperatorInfo operatorBeforeCursor;
        MTInputBehavior elementBehavior = MTInputBehavior.behaviorForElement(element);
        if (!selection.get().isRange()) {
            MTOperatorInfo operator = elementBehavior.getInlineFormOperatorInfo();
            MTString stringGrabbingElementsFromLeft = elementBehavior.firstEmptyChildStringWithBehavior(MTStringInputBehavior.IfEmptyGrabRangeLeftOfCursor);
            if (!(stringGrabbingElementsFromLeft == null || operator == null || ((operatorBeforeCursor = MTOperatorInfo.infoForElement(selection.get().elementBeforeSelection())) != null && operatorBeforeCursor.getNotation() != MTOperatorNotation.Postfix))) {
                Range rangeToGrab = MTParser.rangeForOperandOfOperatorAtIndexInString(MTOperandSide.Left, operator, selection.get().getIndex(), selection.get().getString(), false);
                selection.get().getString().moveElementsInRangeToString(rangeToGrab, stringGrabbingElementsFromLeft, 0);
                selection.set(MTSelection.cursorInStringAtIndex(selection.get().getString(), rangeToGrab.mStartIndex));
            }
            MTString stringGrabbingElementsFromRight = elementBehavior.firstEmptyChildStringWithBehavior(MTStringInputBehavior.IfEmptyGrabRangeRightOfCursor);
            if (stringGrabbingElementsFromRight != null && operator != null) {
                MTOperatorInfo operatorAfterCursor = MTOperatorInfo.infoForElement(selection.get().elementAfterSelection());
                if (operatorAfterCursor == null || operatorAfterCursor.getNotation() == MTOperatorNotation.Prefix) {
                    Range rangeToGrab2 = MTParser.rangeForOperandOfOperatorAtIndexInString(MTOperandSide.Right, operator, selection.get().getIndex(), selection.get().getString(), false);
                    selection.get().getString().moveElementsInRangeToString(rangeToGrab2, stringGrabbingElementsFromRight, 0);
                    selection.set(MTSelection.cursorInStringAtIndex(selection.get().getString(), rangeToGrab2.mStartIndex));
                }
            }
        } else if (!elementBehavior.hasBehavior(MTElementInputBehavior.UseSelectedRangeAsInlineOperand)) {
            MTString stringToAddSelectedRangeTo = elementBehavior.firstEmptyChildStringWithBehavior(MTStringInputBehavior.IfEmptyGrabSelectedRange);
            if (stringToAddSelectedRangeTo != null) {
                selection.set(selection.get().moveElementsToString(stringToAddSelectedRangeTo, 0));
            } else {
                selection.set(selection.get().removeElements());
            }
        }
    }

    private void placeParenthesesAsNeededForInsertionOfElement(MTElement element, AtomicReference<MTSelection> selection) {
        MTOperatorInfo operator;
        MTInputBehavior elementBehavior = MTInputBehavior.behaviorForElement(element);
        if (elementBehavior.hasBehavior(MTElementInputBehavior.UseSelectedRangeAsInlineOperand)) {
            EnumSet<MTParenthesesPlacement> parenthesesPlacement = EnumSet.noneOf(MTParenthesesPlacement.class);
            if (selection.get().isRange() && (operator = elementBehavior.getInlineFormOperatorInfo()) != null) {
                parenthesesPlacement.addAll(MTParser.parenthesesPlacementForOperatorToGrabElementsInRangeInString(operator, selection.get().getRange(), selection.get().getString()));
            }
            if (shouldParenthesesAroundOperandBeForcedForInsertionOfElement(element, selection)) {
                parenthesesPlacement.add(MTParenthesesPlacement.AroundOperand);
            }
            if (!parenthesesPlacement.isEmpty() && selection.get().isRange()) {
                if (parenthesesPlacement.contains(MTParenthesesPlacement.AroundOperand)) {
                    MTParentheses parentheses = new MTParentheses();
                    selection.set(selection.get().moveElementsToString(parentheses.getContents(), 0));
                    selection.get().getString().insertElement(parentheses, selection.get().getIndex());
                    selection.set(MTSelection.selectionWithElement(parentheses));
                }
                if (parenthesesPlacement.contains(MTParenthesesPlacement.AroundOperation)) {
                    MTParentheses parentheses2 = new MTParentheses();
                    selection.get().moveElementsToString(parentheses2.getContents(), 0);
                    selection.get().getString().insertElement(parentheses2, selection.get().getIndex());
                    selection.set(MTSelection.cursorAtEndOfString(parentheses2.getContents()));
                }
            }
        }
    }

    private boolean shouldParenthesesAroundOperandBeForcedForInsertionOfElement(MTElement element, AtomicReference<MTSelection> selection) {
        MTElement operandElement;
        MTOperatorInfo operatorInfo = MTOperatorInfo.infoForElement(element);
        MTInputBehavior elementBehavior = MTInputBehavior.behaviorForElement(element);
        if (operatorInfo != null && elementBehavior.hasBehavior(MTElementInputBehavior.ForceParenthesesOnOperandsOfSameElementType)) {
            MTOperandSide operand = operatorInfo.getNotation() == MTOperatorNotation.Prefix ? MTOperandSide.Right : MTOperandSide.Left;
            if (operand == MTOperandSide.Left) {
                operandElement = selection.get().isRange() ? selection.get().lastElementInRangeSelection() : selection.get().elementBeforeSelection();
            } else if (selection.get().isRange()) {
                operandElement = selection.get().firstElementInRangeSelection();
            } else {
                operandElement = selection.get().elementAfterSelection();
            }
            if (element.getClass().isInstance(operandElement)) {
                if (!selection.get().isRange()) {
                    selection.set(MTSelection.selectionInString(selection.get().getString(), Range.union(new Range(operandElement.indexInParentString(), 1), MTParser.rangeForOperandOfOperatorElement(operand, operandElement))));
                }
                return true;
            }
        }
        return false;
    }

    private int insertionIndexForElement(MTElement element, AtomicReference<MTSelection> selection) {
        if (!selection.get().isRange()) {
            return selection.get().getIndex();
        }
        int insertionIndexBeforeSelection = selection.get().getIndex();
        int insertionIndexAfterSelection = selection.get().getIndexAfterSelection();
        MTInputBehavior elementBehavior = MTInputBehavior.behaviorForElement(element);
        return (!elementBehavior.hasBehavior(MTElementInputBehavior.UseSelectedRangeAsInlineOperand) || elementBehavior.getInlineFormOperatorInfo() == null || elementBehavior.getInlineFormOperatorInfo().getNotation() != MTOperatorNotation.Prefix) ? insertionIndexAfterSelection : insertionIndexBeforeSelection;
    }

    private MTSelection selectionAfterInsertionOfElementAtIndex(MTElement element) {
        MTInputBehavior elementBehavior = MTInputBehavior.behaviorForElement(element);
        MTString stringToPutCursorIn = null;
        if (element.getChildren() != null) {
            Iterator<? extends MTString> it = element.getChildren().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                MTString childString = (MTString) it.next();
                if (elementBehavior.doesChildStringHaveBehavior(childString, MTStringInputBehavior.IfNotFilledGrabCursor) && childString.isEmpty() && !childString.getTraits().contains(MTNodeTraits.CantSelectOrEditChildren)) {
                    stringToPutCursorIn = childString;
                    break;
                }
            }
        }
        if (stringToPutCursorIn != null) {
            return MTSelection.cursorAtEndOfString(stringToPutCursorIn);
        }
        return MTSelection.cursorAfterElement(element);
    }

    public void backspace(final boolean animated) {
        suspendMathTypeNotification(new Runnable() { // from class: com.oddlyspaced.calci.mathtype.views.MTMathTypeView.7
            @Override // java.lang.Runnable
            public void run() {
                MTMathTypeView.this.backspaceInternal(animated);
                MTMathTypeView.this.markScrollChanged(animated);
            }
        });
    }

    public void backspaceInternal(boolean animated) {
        boolean alwaysDelete;
        boolean alwaysDelete2;
        hideOptionsMenu(true);
        MTSelection selection = this.mSelection;
        if (selection != null) {
            if (selection.isRange()) {
                MTSelection selection2 = selection.removeElements();
                markMathTypeEdited(animated);
                setSelection(selection2, animated);
            } else if (selection.getIndex() > 0) {
                MTElement elementBeforeCursor = selection.elementBeforeSelection();
                MTInputBehavior elementBehavior = MTInputBehavior.behaviorForElement(elementBeforeCursor);
                MTString stringGrabbingCursorInsteadOfDeletingParent = elementBehavior.firstChildStringWithBehavior(MTStringInputBehavior.GrabCursorWhenBackspacingParentInsteadOfDelete);
                if (!elementBehavior.hasBehavior(MTElementInputBehavior.AlwaysDeleteElementOnBackspaceWhenAllChildrenAreEmpty) || !elementBehavior.areAllChildrenEmpty()) {
                    alwaysDelete2 = false;
                } else {
                    alwaysDelete2 = true;
                }
                if (alwaysDelete2 || stringGrabbingCursorInsteadOfDeletingParent == null) {
                    backspaceElementFromChildString(elementBeforeCursor, null, animated);
                } else {
                    setSelection(MTSelection.cursorAtEndOfString(stringGrabbingCursorInsteadOfDeletingParent), true);
                }
            } else if (selection.getString() != getString()) {
                MTElement parentElement = selection.getString().getParent();
                MTString parentElementString = parentElement.getParent();
                MTInputBehavior parentBehavior = MTInputBehavior.behaviorForElement(parentElement);
                if (!parentBehavior.hasBehavior(MTElementInputBehavior.AlwaysDeleteElementOnBackspaceWhenAllChildrenAreEmpty) || !parentBehavior.areAllChildrenEmpty()) {
                    alwaysDelete = false;
                } else {
                    alwaysDelete = true;
                }
                if (alwaysDelete || parentBehavior.doesChildStringHaveBehavior(selection.getString(), MTStringInputBehavior.DeleteParentWhenBackspacingAtIndex0)) {
                    backspaceElementFromChildString(parentElement, selection.getString(), animated);
                } else if (parentBehavior.doesChildStringHaveBehavior(selection.getString(), MTStringInputBehavior.SelectParentWhenBackspacingAtIndex0)) {
                    setSelection(MTSelection.selectionInString(parentElementString, parentElement.indexInParentString(), 1), false);
                }
            }
        }
    }

    private void backspaceElementFromChildString(MTElement element, MTString sourceChildString, boolean animated) {
        int cursorIndex;
        MTInputBehavior elementBehavior = MTInputBehavior.behaviorForElement(element);
        MTString parentString = element.getParent();
        int indexOfElement = element.indexInParentString();
        element.removeFromParentString();
        markMathTypeEdited(animated);
        int indexBeforeSourceChildString = -1;
        int index = indexOfElement;
        MTString selectedChildString = null;
        if (element.getChildren() != null) {
            for (MTString childString : element.getChildren()) {
                if (childString.length() != 0 && elementBehavior.doesChildStringHaveBehavior(childString, MTStringInputBehavior.KeepContentsWhenBackspacingParent)) {
                    int length = childString.length();
                    childString.moveElementsToString(parentString, index);
                    markMathTypeEdited(animated);
                    if (childString == sourceChildString) {
                        indexBeforeSourceChildString = index;
                    }
                    index += length;
                    if (selectedChildString == null) {
                        Range contentRange = new Range(index - length, length);
                        boolean shouldSelectContent = elementBehavior.doesChildStringHaveBehavior(childString, MTStringInputBehavior.SelectContentsWhenBackspacingParent);
                        if (!shouldSelectContent && elementBehavior.doesChildStringHaveBehavior(childString, MTStringInputBehavior.SelectContentsWhenBackspacingParentUnlessEqualToImplicitGrab)) {
                            boolean grabLeft = elementBehavior.doesChildStringHaveBehavior(childString, MTStringInputBehavior.IfEmptyGrabRangeLeftOfCursor);
                            boolean grabRight = elementBehavior.doesChildStringHaveBehavior(childString, MTStringInputBehavior.IfEmptyGrabRangeRightOfCursor);
                            if (grabLeft || grabRight) {
                                shouldSelectContent = !contentRange.equals(MTParser.rangeForOperandOfOperatorAtIndexInString(grabLeft ? MTOperandSide.Left : MTOperandSide.Right, elementBehavior.getInlineFormOperatorInfo(), index, parentString, false));
                            }
                        }
                        if (shouldSelectContent) {
                            selectedChildString = childString;
                            setSelection(MTSelection.selectionInString(parentString, contentRange), animated);
                        }
                    }
                }
            }
        }
        if (selectedChildString == null) {
            if (indexBeforeSourceChildString != -1) {
                cursorIndex = indexBeforeSourceChildString;
            } else {
                cursorIndex = index;
            }
            setSelection(MTSelection.cursorInStringAtIndex(parentString, cursorIndex), animated);
        }
    }

    public void clearLine(final boolean animated) {
        suspendMathTypeNotification(new Runnable() { // from class: com.oddlyspaced.calci.mathtype.views.MTMathTypeView.8
            @Override // java.lang.Runnable
            public void run() {
                MTMathTypeView.this.clearLineInternal(animated);
                MTMathTypeView.this.markScrollChanged(animated);
            }
        });
    }

    public void clearLineInternal(boolean animated) {
        hideOptionsMenu(true);
        if (getString() != null) {
            if (getString().isNotEmpty()) {
                getString().removeAllElements();
                markMathTypeEdited(animated);
            }
            if (this.mSelection != null) {
                setSelection(this.mEditable ? MTSelection.cursorAtEndOfString(getString()) : null);
            }
            this.mDidAutoInsertAns = false;
        }
    }

    private PointF convertFromViewToDrawable(PointF point) {
        return new PointF(point.x + ((float) getScrollX()), point.y + ((float) getScrollY()));
    }

    private PointF convertFromViewToDrawable(float x, float y) {
        return new PointF(((float) getScrollX()) + x, ((float) getScrollY()) + y);
    }

    private PointF convertFromDrawableToView(PointF point) {
        return new PointF(point.x - ((float) getScrollX()), point.y - ((float) getScrollY()));
    }

    private RectF convertFromDrawableToView(RectF rect) {
        return new RectF(rect.left - ((float) getScrollX()), rect.top - ((float) getScrollY()), rect.right - ((float) getScrollX()), rect.bottom - ((float) getScrollY()));
    }

    private boolean isOptionsMenuVisible() {
        return FloatingOptionsMenu.getInstance(getContext()).isVisible();
    }

    private void showOptionsMenu(boolean delayed) {
        FloatingOptionsMenu optionsMenu = FloatingOptionsMenu.getInstance(getContext());
        optionsMenu.clear();
        optionsMenu.addOption(MTMessageType.COPY, getResources().getDrawable(this.mEditable ? R.drawable.button_options_menu_left : R.drawable.button_options_menu_single), new Runnable() { // from class: com.oddlyspaced.calci.mathtype.views.MTMathTypeView.9
            @Override // java.lang.Runnable
            public void run() {
                new ResponderMessage(MTMessageType.COPY, null).send();
            }
        });
        if (this.mEditable) {
            optionsMenu.addOption(MTMessageType.PASTE, getResources().getDrawable(R.drawable.button_options_menu_right), new Runnable() { // from class: com.oddlyspaced.calci.mathtype.views.MTMathTypeView.10
                @Override // java.lang.Runnable
                public void run() {
                    new ResponderMessage(MTMessageType.PASTE, null).send();
                }
            });
        }
        optionsMenu.measure(0, 0);
        RectF selection = convertFromDrawableToView(getSelectionBounds());
        selection.intersect(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
        PointF position = new PointF(selection.centerX(), selection.top);
        if (this.mSelection.isCursor()) {
            position.x += MTSelectionDrawable.CURSOR_WIDTH / 2.0f;
        }
        position.x -= (float) (optionsMenu.getMeasuredWidth() / 2);
        position.y -= (float) optionsMenu.getMeasuredHeight();
        position.y -= this.OPTIONS_MENU_MARGIN;
        position.y += getAdditionalPaddingTop() / 2.0f;
        position.y -= getAdditionalPaddingBottom() / 2.0f;
        float menuRight = selection.centerX() + ((float) (optionsMenu.getMeasuredWidth() / 2));
        float menuLeft = selection.centerX() - ((float) (optionsMenu.getMeasuredWidth() / 2));
        float distanceBeyondRight = menuRight - ((float) getRight());
        float distanceBeyondLeft = ((float) getLeft()) - menuLeft;
        if (distanceBeyondRight > 0.0f) {
            position.x -= this.OPTIONS_MENU_MARGIN + distanceBeyondRight;
        }
        if (distanceBeyondLeft > 0.0f) {
            position.x += this.OPTIONS_MENU_MARGIN + distanceBeyondLeft;
        }
        int[] locationHolder = new int[2];
        getLocationInWindow(locationHolder);
        Rect windowVisibleDisplayFrame = new Rect();
        getWindowVisibleDisplayFrame(windowVisibleDisplayFrame);
        if (position.y < (-((float) (locationHolder[1] - windowVisibleDisplayFrame.top)))) {
            if (this.mSelection.isRange()) {
                position.y += ((float) optionsMenu.getMeasuredHeight()) + selection.height() + (2.0f * MTSelectionDrawable.HANDLE_RADIUS) + (2.0f * this.OPTIONS_MENU_MARGIN);
            } else {
                position.y += ((float) optionsMenu.getMeasuredHeight()) + selection.height() + (2.0f * this.OPTIONS_MENU_MARGIN);
            }
        }
        FloatingOptionsMenu.getInstance(getContext()).showAtPosition(delayed, position);
    }

    private void hideOptionsMenu(boolean animated) {
        FloatingOptionsMenu.getInstance(getContext()).hide(animated);
    }

    @Override // com.oddlyspaced.calci.archimedes.views.AROverlayDelegate
    public boolean onOverlayTouchEvent(MotionEvent event) {
        boolean handleEvent;
        if (event.getAction() == 0) {
            if (selectionHandleForTouchPoint(new PointF(event.getX(), event.getY())) != MTSelectionHandle.None) {
                handleEvent = true;
            } else {
                handleEvent = false;
            }
            if (!handleEvent) {
                return false;
            }
        }
        return onTouchEvent(event);
    }

    @Override // com.oddlyspaced.calci.archimedes.views.ARView
    public PointF finalSize() {
        return this.mMathTypeDrawable.predictFinalSize();
    }
}
