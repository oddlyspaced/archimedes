package com.sparkappdesign.archimedes.archimedes.views;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.ScrollView;
import android.widget.Scroller;
import com.sparkappdesign.archimedes.R;
import com.sparkappdesign.archimedes.archimedes.enums.ARScrollPriority;
import com.sparkappdesign.archimedes.archimedes.enums.ARScrollState;
import com.sparkappdesign.archimedes.utilities.GeneralUtil;
import com.sparkappdesign.archimedes.utilities.IterableUtil;
import com.sparkappdesign.archimedes.utilities.RectUtil;
import com.sparkappdesign.archimedes.utilities.responder.Responder;
import com.sparkappdesign.archimedes.utilities.responder.ResponderMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class ARAutoScrollView extends ScrollView implements Responder {
    public static final String AREAS_OF_INTEREST_FOR_MANUAL_SCROLL = "AREAS_OF_INTEREST_FOR_MANUAL_SCROLL";
    public static final String AREAS_OF_INTEREST_ON_BOUNDS_RESIZE = "AREAS_OF_INTEREST_ON_BOUNDS_RESIZE";
    public static final String AREAS_OF_INTEREST_ON_ITEM_DID_BECOME_ACTIVE = "AREAS_OF_INTEREST_ON_ITEM_DID_BECOME_ACTIVE";
    private static final int AUTO_SCROLL_VIEW_ID = View.generateViewId();
    private ARAutoScrollViewItem mActiveItem;
    private ArrayList<RectF> mAreaOfInterestRectsInViewAfterManualScroll;
    private boolean mScrollInProgress;
    private ARScrollState mScrollState;
    private int mTargetScrollY;
    private ArrayList<ARAutoScrollViewItem> mItems = new ArrayList<>();
    private PointF mLastMotionPoint = new PointF();
    private Scroller mScroller = new Scroller(getContext());
    private RectF mLastVisibleBounds = new RectF();
    private boolean mAutoScrollEnabled = true;
    private final int ANIMATION_DURATION = getResources().getInteger(R.integer.auto_scroll_view_animation_duration);

    public boolean isAutoScrollEnabled() {
        return this.mAutoScrollEnabled;
    }

    public void setAutoScrollEnabled(boolean autoScrollEnabled) {
        this.mAutoScrollEnabled = autoScrollEnabled;
    }

    public ArrayList<ARAutoScrollViewItem> getItems() {
        return this.mItems;
    }

    public ARAutoScrollViewItem getActiveItem() {
        return this.mActiveItem;
    }

    public void setActiveItem(ARAutoScrollViewItem activeItem) {
        if (this.mActiveItem != activeItem) {
            this.mActiveItem = activeItem;
            if (activeItem != null) {
                scrollToAreaOfInterest(AREAS_OF_INTEREST_ON_ITEM_DID_BECOME_ACTIVE, ARScrollPriority.AlwaysOverrideManualScroll, true);
            }
        }
    }

    private void setScrollState(ARScrollState scrollState) {
        this.mScrollState = scrollState;
        if (scrollState == ARScrollState.DidScrollManually) {
            this.mAreaOfInterestRectsInViewAfterManualScroll = intersectRectsWithRect(getAreasOfInterest(AREAS_OF_INTEREST_FOR_MANUAL_SCROLL), RectUtil.create((float) getScrollX(), (float) getScrollY(), (float) getWidth(), (float) getHeight()));
        } else {
            this.mAreaOfInterestRectsInViewAfterManualScroll = null;
        }
    }

    public View getContentView() {
        return getChildAt(0);
    }

    private RectF getVisibleBounds() {
        return RectUtil.create((float) getScrollX(), (float) getScrollY(), (float) getMeasuredWidth(), (float) getMeasuredHeight());
    }

    public ARAutoScrollView(Context context) {
        super(context);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        setId(AUTO_SCROLL_VIEW_ID);
    }

    private ARAutoScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private ARAutoScrollView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    @Override // android.widget.ScrollView, android.widget.FrameLayout, android.view.View, android.view.ViewGroup
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        RectF visibleBounds = getVisibleBounds();
        if (t - b != 0) {
            if (this.mLastVisibleBounds.width() != visibleBounds.width() || this.mLastVisibleBounds.height() != visibleBounds.height()) {
                this.mLastVisibleBounds = adjustScrollForBoundsResizeFromLastBounds(this.mLastVisibleBounds);
            }
        }
    }

    private RectF adjustScrollForBoundsResizeFromLastBounds(RectF lastVisibleBounds) {
        boolean activeItemWasInView;
        RectF visibleBounds = getVisibleBounds();
        setScrollState(ARScrollState.ScrollAuto);
        RectF activeItemBounds = this.mActiveItem != null ? this.mActiveItem.frameInAutoScrollView(this) : null;
        if (activeItemBounds == null || !RectF.intersects(activeItemBounds, lastVisibleBounds)) {
            activeItemWasInView = false;
        } else {
            activeItemWasInView = true;
        }
        if (activeItemWasInView) {
            scrollToAreaOfInterest(AREAS_OF_INTEREST_ON_BOUNDS_RESIZE, ARScrollPriority.AlwaysOverrideManualScroll, false);
            scrollRectsToVisible(Arrays.asList(lastVisibleBounds), false);
        } else {
            float height = visibleBounds.height();
            int y = (int) (lastVisibleBounds.top - ((height - lastVisibleBounds.height()) / 2.0f));
            visibleBounds.top = (float) y;
            visibleBounds.bottom = ((float) y) + height;
            this.mScroller.forceFinished(true);
            this.mScroller.startScroll(getScrollX(), getScrollY(), 0, y - getScrollY(), 250);
        }
        return visibleBounds;
    }

    @Override // android.widget.ScrollView, android.view.View
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("savedInstanceState", super.onSaveInstanceState());
        bundle.putParcelable("mLastVisibleBounds", this.mLastVisibleBounds);
        return bundle;
    }

    @Override // android.widget.ScrollView, android.view.View
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable("savedInstanceState"));
        this.mLastVisibleBounds = (RectF) bundle.getParcelable("mLastVisibleBounds");
    }

    @Override // android.widget.ScrollView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float x = ev.getX();
        float y = ev.getY();
        switch (action) {
            case 0:
                this.mLastMotionPoint.x = x;
                this.mLastMotionPoint.y = y;
                break;
            case 2:
                if (((int) Math.abs(x - this.mLastMotionPoint.x)) > ((int) Math.abs(y - this.mLastMotionPoint.y))) {
                    return false;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override // android.widget.ScrollView, android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float x = ev.getX();
        float y = ev.getY();
        this.mScroller.forceFinished(true);
        switch (action) {
            case 0:
            case 2:
                this.mLastMotionPoint.x = x;
                this.mLastMotionPoint.y = y;
                if (this.mScrollState != ARScrollState.ScrollingManually) {
                    setScrollState(ARScrollState.ScrollingManually);
                    break;
                }
                break;
            case 1:
            case 3:
                setScrollState(ARScrollState.DidScrollManually);
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void addItem(ARAutoScrollViewItem item) {
        if (!this.mItems.contains(item)) {
            this.mItems.add(item);
            item.addedToAutoScrollView(this);
        }
    }

    public void removeItem(ARAutoScrollViewItem item) {
        if (this.mItems.contains(item)) {
            this.mItems.remove(item);
            item.removedFromAutoScrollView(this);
        }
    }

    public void scrollToAreaOfInterest(String identifier, ARScrollPriority priority, boolean animated) {
        if (this.mAutoScrollEnabled) {
            scrollToAreasOfInterest(getAreasOfInterest(identifier), priority, animated);
        }
    }

    private void scrollToAreaOfInterestAskingForPriorityWithDefault(String identifier, ARScrollPriority defaultPriority, boolean animated) {
        scrollToAreasOfInterest(getAreasOfInterest(identifier), getScrollPriorityForAreasOfInterest(identifier, defaultPriority), animated);
    }

    private void scrollToAreasOfInterest(ArrayList<RectF> areasOfInterest, ARScrollPriority priority, boolean animated) {
        scrollToAreasOfInterestInternal(areasOfInterest, priority, animated);
    }

    private void scrollToAreasOfInterestInternal(ArrayList<RectF> areasOfInterest, ARScrollPriority priority, boolean animated) {
        if (this.mScrollState != ARScrollState.ScrollingManually) {
            if (this.mScrollState != ARScrollState.DidScrollManually || priority != ARScrollPriority.AlwaysRespectManualScroll) {
                scrollRectsToVisible(areasOfInterest, animated);
                if (this.mScrollState == ARScrollState.DidScrollManually && priority == ARScrollPriority.RespectManualScrollOverAreasOfInterest && this.mAreaOfInterestRectsInViewAfterManualScroll != null && !this.mAreaOfInterestRectsInViewAfterManualScroll.isEmpty()) {
                    scrollRectsToVisible(this.mAreaOfInterestRectsInViewAfterManualScroll, animated);
                }
                switch (priority) {
                    case ConsiderAsManualScroll:
                        setScrollState(ARScrollState.DidScrollManually);
                        return;
                    case AlwaysOverrideManualScroll:
                        setScrollState(ARScrollState.ScrollAuto);
                        return;
                    case RespectManualScrollOverAreasOfInterest:
                        if (this.mAreaOfInterestRectsInViewAfterManualScroll == null || this.mAreaOfInterestRectsInViewAfterManualScroll.size() == 0) {
                            setScrollState(ARScrollState.ScrollAuto);
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        }
    }

    private void scrollRectsToVisible(List<RectF> rects, boolean animated) {
        for (RectF rect : IterableUtil.reverse(rects)) {
            float distanceAboveTop = ((float) this.mTargetScrollY) - rect.top;
            float distanceBelowBottom = rect.bottom - ((float) (this.mTargetScrollY + getHeight()));
            if (distanceAboveTop > 0.0f && distanceBelowBottom < 0.0f) {
                this.mTargetScrollY = (int) (((float) this.mTargetScrollY) - Math.min(distanceAboveTop, Math.abs(distanceBelowBottom)));
            } else if (distanceBelowBottom > 0.0f && distanceAboveTop < 0.0f) {
                this.mTargetScrollY = (int) (((float) this.mTargetScrollY) + Math.min(distanceBelowBottom, Math.abs(distanceAboveTop)));
            }
        }
        this.mScroller.forceFinished(true);
        this.mTargetScrollY = GeneralUtil.constrain(this.mTargetScrollY, 0, ((int) ((ARStackView) getChildAt(0)).finalSize().y) - getHeight());
        if (animated) {
            this.mScroller.startScroll(getScrollX(), getScrollY(), 0, this.mTargetScrollY - getScrollY(), this.ANIMATION_DURATION);
        } else {
            this.mScroller.startScroll(getScrollX(), getScrollY(), 0, this.mTargetScrollY - getScrollY());
        }
    }

    @Override // android.widget.ScrollView, android.view.View
    public void computeScroll() {
        super.computeScroll();
        AROverlayView.getInstance(getContext()).invalidate();
        this.mLastVisibleBounds = getVisibleBounds();
        if (this.mScroller.computeScrollOffset()) {
            scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
        } else {
            this.mTargetScrollY = getScrollY();
        }
    }

    private ArrayList<RectF> getAreasOfInterest(String identifier) {
        ArrayList<RectF> areasOfInterest = new ArrayList<>();
        ArrayList<RectF> areasOfInterestRaw = this.mActiveItem != null ? this.mActiveItem.areasOfInterestWithIdentifier(this, identifier) : null;
        if (areasOfInterestRaw != null) {
            Iterator<RectF> it = areasOfInterestRaw.iterator();
            while (it.hasNext()) {
                RectF area = it.next();
                if (area != null) {
                    areasOfInterest.add(area);
                }
            }
        }
        return areasOfInterest;
    }

    private ARScrollPriority getScrollPriorityForAreasOfInterest(String identifier, ARScrollPriority defaultPriority) {
        if (this.mActiveItem.priorityForScrollToAreasOfInterest(this, identifier) != null) {
            return this.mActiveItem.priorityForScrollToAreasOfInterest(this, identifier);
        }
        return defaultPriority;
    }

    private ArrayList<RectF> intersectRectsWithRect(ArrayList<RectF> rects, RectF otherRect) {
        ArrayList<RectF> resultRects = new ArrayList<>();
        Iterator<RectF> it = rects.iterator();
        while (it.hasNext()) {
            RectF result = RectUtil.intersection(it.next(), otherRect);
            if (!result.isEmpty()) {
                resultRects.add(result);
            }
        }
        return resultRects;
    }

    private RectF unionRects(ArrayList<RectF> rects) {
        RectF result = new RectF();
        Iterator<RectF> it = rects.iterator();
        while (it.hasNext()) {
            result.union(it.next());
        }
        return result;
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
}
