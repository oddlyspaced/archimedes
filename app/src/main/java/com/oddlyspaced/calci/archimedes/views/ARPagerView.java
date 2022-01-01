package com.sparkappdesign.archimedes.archimedes.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.OverScroller;
import com.sparkappdesign.archimedes.R;
import com.sparkappdesign.archimedes.utilities.FloatingOptionsMenu;
import com.sparkappdesign.archimedes.utilities.GeneralUtil;
import com.sparkappdesign.archimedes.utilities.RectUtil;
import com.sparkappdesign.archimedes.utilities.TypefaceCache;
import com.sparkappdesign.archimedes.utilities.responder.Responder;
import com.sparkappdesign.archimedes.utilities.responder.ResponderMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
/* loaded from: classes.dex */
public class ARPagerView extends ViewGroup implements Responder, ARViewGroup {
    private boolean mAllowTapOnHint;
    private ARPagerContentView mContentView;
    private boolean mContinuousScrollingEnabled;
    private ARPagerViewDelegate mDelegate;
    private ARRotatedHintView mLeftHint;
    private int mMainPageIndex;
    private View mOverlayView;
    private ArrayList<ARPagerPage> mPages;
    private int mPagesPerView;
    private PointF mPreviousMovePoint;
    private ARRotatedHintView mRightHint;
    private boolean mShowHints;
    private int mTargetPageIndex;
    private VelocityTracker mVelocityTracker;
    private Rect mLeftHintHitRect = new Rect();
    private Rect mRightHintHitRect = new Rect();
    private Point mDownPoint = new Point();
    private OverScroller mOverScroller = new OverScroller(getContext());
    private int mPagingTouchSlop = ViewConfiguration.get(getContext()).getScaledPagingTouchSlop();
    private final int MINIMUM_FLING_VELOCITY = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
    private final float HINT_FONT_SIZE = getResources().getDimension(R.dimen.hint_font_size);

    public ArrayList<ARPagerPage> getPages() {
        return this.mPages;
    }

    public void setOverlayView(View overlayView) {
        if (!GeneralUtil.equalOrBothNull(this.mOverlayView, overlayView)) {
            if (this.mOverlayView != null) {
                removeView(this.mOverlayView);
            }
            this.mOverlayView = overlayView;
            if (overlayView != null) {
                addView(overlayView, indexOfChild(this.mContentView) + 1);
            }
        }
    }

    public ARPagerViewDelegate getDelegate() {
        return this.mDelegate;
    }

    public void setDelegate(ARPagerViewDelegate delegate) {
        this.mDelegate = delegate;
    }

    public boolean shouldShowHints() {
        return this.mShowHints;
    }

    public void setShowHints(boolean showHints) {
        int i;
        int i2 = 0;
        if (this.mShowHints != showHints) {
            this.mShowHints = showHints;
            ARRotatedHintView aRRotatedHintView = this.mLeftHint;
            if (showHints) {
                i = 0;
            } else {
                i = 8;
            }
            aRRotatedHintView.setVisibility(i);
            ARRotatedHintView aRRotatedHintView2 = this.mRightHint;
            if (!showHints) {
                i2 = 8;
            }
            aRRotatedHintView2.setVisibility(i2);
        }
    }

    public boolean shouldAllowTapOnHint() {
        return this.mAllowTapOnHint;
    }

    public void setAllowTapOnHint(boolean allowTapOnHint) {
        this.mAllowTapOnHint = allowTapOnHint;
    }

    public boolean shouldUseContinuousScrolling() {
        return this.mContinuousScrollingEnabled;
    }

    public void setContinuousScrollingEnabled(boolean continuousScrollingEnabled) {
        this.mContinuousScrollingEnabled = continuousScrollingEnabled;
        int defaultPagingTouchSlop = ViewConfiguration.get(getContext()).getScaledPagingTouchSlop();
        if (continuousScrollingEnabled) {
            defaultPagingTouchSlop *= 4;
        }
        this.mPagingTouchSlop = defaultPagingTouchSlop;
    }

    public int getCurrentRoundedPageIndex() {
        return this.mContentView.getCurrentRoundedPageIndex();
    }

    private ARPagerView(Context context) {
        super(context);
    }

    private ARPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private ARPagerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ARPagerView(Context context, int pagesPerView, int mainPageIndex, ARPagerPage... pages) {
        super(context);
        this.mPages = new ArrayList<>(Arrays.asList(pages));
        this.mMainPageIndex = mainPageIndex;
        this.mPagesPerView = pagesPerView;
        addChildViews(context);
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("savedInstanceState", super.onSaveInstanceState());
        bundle.putInt("currentRoundedPageIndex", getCurrentRoundedPageIndex());
        return bundle;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable("savedInstanceState"));
        final int currentRoundedPageIndex = bundle.getInt("currentRoundedPageIndex");
        this.mContentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARPagerView.1
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                ARPagerView.this.mContentView.scrollTo(ARPagerView.this.mContentView.getScrollXForPage(currentRoundedPageIndex), 0);
                ARPagerView.this.mTargetPageIndex = currentRoundedPageIndex;
                ARPagerView.this.mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.mContentView.measure(widthMeasureSpec, heightMeasureSpec);
        if (this.mOverlayView != null) {
            this.mOverlayView.measure(View.MeasureSpec.makeMeasureSpec(this.mContentView.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(this.mContentView.getMeasuredHeight(), 1073741824));
        }
        int hintWidthMeaureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        int separatorHeight = 0;
        if (this.mPages.size() != 0 && (this.mPages.get(0).getView() instanceof ARAnswerView)) {
            separatorHeight = ((ARAnswerView) this.mPages.get(0).getView()).getDistanceToSeparatorLine();
        }
        int hintHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.mContentView.getMeasuredHeight() - separatorHeight, 1073741824);
        this.mLeftHint.measure(hintWidthMeaureSpec, hintHeightMeasureSpec);
        this.mRightHint.measure(hintWidthMeaureSpec, hintHeightMeasureSpec);
        setMeasuredDimension(this.mContentView.getMeasuredWidth(), this.mContentView.getMeasuredHeight());
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        this.mContentView.layout(0, 0, this.mContentView.getMeasuredWidth(), this.mContentView.getMeasuredHeight());
        if (this.mOverlayView != null) {
            this.mOverlayView.layout(0, 0, this.mOverlayView.getMeasuredWidth(), this.mOverlayView.getMeasuredHeight());
        }
        int separatorHeight = 0;
        if (this.mPages.size() != 0 && (this.mPages.get(0).getView() instanceof ARAnswerView)) {
            separatorHeight = ((ARAnswerView) this.mPages.get(0).getView()).getDistanceToSeparatorLine();
        }
        this.mLeftHint.layout(0, separatorHeight, this.mLeftHint.getMeasuredWidth(), this.mLeftHint.getMeasuredHeight() + separatorHeight);
        this.mRightHint.layout(this.mContentView.getMeasuredWidth() - this.mRightHint.getMeasuredWidth(), separatorHeight, this.mContentView.getMeasuredWidth(), this.mRightHint.getMeasuredHeight() + separatorHeight);
        this.mContentView.updateHints();
    }

    public void addPage(ARPagerPage page) {
        insertPage(page, this.mPages.size());
    }

    public void insertPage(ARPagerPage page, int index) {
        this.mPages.add(index, page);
        this.mContentView.addView(page.getView());
        requestLayout();
    }

    public void removePage(int index) {
        this.mContentView.removeView(this.mPages.get(index).getView());
        this.mPages.remove(index);
        requestLayout();
    }

    private void addChildViews(Context context) {
        int i = 0;
        this.mContentView = new ARPagerContentView(context);
        addView(this.mContentView);
        this.mLeftHint = new ARRotatedHintView(context, 90);
        this.mLeftHint.setTypeface(TypefaceCache.getMyriadProLight(context));
        this.mLeftHint.setTextSize(this.HINT_FONT_SIZE);
        this.mLeftHint.setTextColor(context.getResources().getColor(R.color.tint_dark));
        this.mLeftHint.setVisibility(this.mShowHints ? 0 : 8);
        addView(this.mLeftHint);
        this.mRightHint = new ARRotatedHintView(context, 270);
        this.mRightHint.setTypeface(TypefaceCache.getMyriadProLight(context));
        this.mRightHint.setTextSize(this.HINT_FONT_SIZE);
        this.mRightHint.setTextColor(context.getResources().getColor(R.color.tint_dark));
        ARRotatedHintView aRRotatedHintView = this.mRightHint;
        if (!this.mShowHints) {
            i = 8;
        }
        aRRotatedHintView.setVisibility(i);
        addView(this.mRightHint);
    }

    public void scrollViewDidScrollInternal() {
        if (this.mDelegate != null) {
            this.mDelegate.pagerViewDidScroll(this);
        }
    }

    public float fractionVisibleOfPageAtIndex(int index) {
        if (this.mContentView.getMeasuredWidth() <= 0) {
            return 0.0f;
        }
        float pageWidth = (float) this.mContentView.getPageWidth();
        float leftEdgeIndex = ((float) this.mContentView.getScrollX()) / pageWidth;
        float rightEdgeIndex = ((float) (this.mContentView.getScrollX() + this.mContentView.getWidth())) / pageWidth;
        float pageLeftEdgeIndex = (float) index;
        float pageRightEdgeIndex = (float) (index + 1);
        if (pageLeftEdgeIndex < leftEdgeIndex) {
            return 1.0f - GeneralUtil.constrain(leftEdgeIndex - pageLeftEdgeIndex, 0.0f, 1.0f);
        }
        if (pageRightEdgeIndex > rightEdgeIndex) {
            return 1.0f - GeneralUtil.constrain(pageRightEdgeIndex - rightEdgeIndex, 0.0f, 1.0f);
        }
        return 1.0f;
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

    @Override // com.sparkappdesign.archimedes.archimedes.views.ARViewGroup
    public RectF finalBoundsForChildAtIndex(int index) {
        PointF size = ((ARView) getChildAt(index)).finalSize();
        return RectUtil.create(0.0f, 0.0f, size.x, size.y);
    }

    @Override // com.sparkappdesign.archimedes.archimedes.views.ARView
    public PointF finalSize() {
        return this.mContentView.finalSize();
    }

    /* loaded from: classes.dex */
    public class ARPagerContentView extends ViewGroup implements Responder, ARViewGroup {
        public int getPageWidth() {
            return getWidth() / ARPagerView.this.mPagesPerView;
        }

        private int getContentWidth() {
            return getChildAt(ARPagerView.this.mPages.size() - 1).getRight();
        }

        private boolean indexOutOfBounds(int index) {
            return index < 0 || index > ARPagerView.this.mPages.size() + -1;
        }

        protected int getCurrentRoundedPageIndex() {
            double closestDistance = Double.POSITIVE_INFINITY;
            int closestPageIndex = 0;
            for (int i = 0; i < ARPagerView.this.mPages.size(); i++) {
                double pageDistance = (double) Math.abs(getScrollXForPage(i) - getScrollX());
                if (pageDistance < closestDistance) {
                    closestDistance = pageDistance;
                    closestPageIndex = i;
                }
            }
            return closestPageIndex;
        }

        public int getScrollXForPage(int index) {
            return GeneralUtil.constrain(index * getPageWidth(), 0, getContentWidth() - getWidth());
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ARPagerContentView(Context context) {
            super(context);
            ARPagerView.this = r4;
            Iterator it = r4.mPages.iterator();
            while (it.hasNext()) {
                addView(((ARPagerPage) it.next()).getView());
            }
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARPagerView.ARPagerContentView.1
                @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
                public void onGlobalLayout() {
                    ARPagerContentView.this.scrollTo(ARPagerContentView.this.getScrollXForPage(ARPagerView.this.mMainPageIndex), 0);
                    ARPagerView.this.mTargetPageIndex = ARPagerView.this.mMainPageIndex;
                    ARPagerContentView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        private ARPagerContentView(Context context, AttributeSet attrs) {
            super(context, attrs);
            ARPagerView.this = r1;
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        private ARPagerContentView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            ARPagerView.this = r1;
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int parentWidth = View.MeasureSpec.getSize(widthMeasureSpec);
            int childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
            Iterator it = ARPagerView.this.mPages.iterator();
            while (it.hasNext()) {
                ARPagerPage page = (ARPagerPage) it.next();
                page.getView().measure(View.MeasureSpec.makeMeasureSpec((int) ((page.getRelativeWidth() * ((float) parentWidth)) / ((float) ARPagerView.this.mPagesPerView)), 1073741824), childHeightMeasureSpec);
            }
            setMeasuredDimension(parentWidth, computeCurrentHeight());
            AROverlayView.getInstance(getContext()).invalidate();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int left = 0;
            Iterator it = ARPagerView.this.mPages.iterator();
            while (it.hasNext()) {
                View view = ((ARPagerPage) it.next()).getView();
                int right = left + view.getMeasuredWidth();
                view.layout(left, 0, right, view.getMeasuredHeight());
                left = right;
            }
            ARPagerView.this.mLeftHint.getHitRect(ARPagerView.this.mLeftHintHitRect);
            ARPagerView.this.mRightHint.getHitRect(ARPagerView.this.mRightHintHitRect);
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case 0:
                    ARPagerView.this.mDownPoint.x = (int) event.getX();
                    ARPagerView.this.mDownPoint.y = (int) event.getY();
                    if (ARPagerView.this.mVelocityTracker == null) {
                        ARPagerView.this.mVelocityTracker = VelocityTracker.obtain();
                    } else {
                        ARPagerView.this.mVelocityTracker.clear();
                    }
                    ARPagerView.this.mVelocityTracker.addMovement(event);
                    return false;
                case 1:
                    ARPagerView.this.mPreviousMovePoint = null;
                    if (ARPagerView.this.mContinuousScrollingEnabled) {
                        float distanceX = Math.abs(event.getX() - ((float) ARPagerView.this.mDownPoint.x));
                        float distanceY = Math.abs(event.getY() - ((float) ARPagerView.this.mDownPoint.y));
                        if (Math.hypot((double) distanceX, (double) distanceY) < ((double) ARPagerView.this.mPagingTouchSlop) || distanceY > distanceX) {
                            scrollToPage(getCurrentRoundedPageIndex(), true);
                            return false;
                        }
                        ARPagerView.this.mVelocityTracker.addMovement(event);
                        ARPagerView.this.mVelocityTracker.computeCurrentVelocity(1000);
                        float velocityX = ARPagerView.this.mVelocityTracker.getXVelocity();
                        if (Math.abs(velocityX) > ((float) ARPagerView.this.MINIMUM_FLING_VELOCITY)) {
                            if (velocityX < 0.0f) {
                                scrollToPage(getCurrentRoundedPageIndex() + 1, true);
                            } else {
                                scrollToPage(getCurrentRoundedPageIndex() - 1, true);
                            }
                            ARPagerView.this.mVelocityTracker.recycle();
                            ARPagerView.this.mVelocityTracker = null;
                            return true;
                        }
                    }
                    ARPagerView.this.mVelocityTracker.recycle();
                    ARPagerView.this.mVelocityTracker = null;
                    scrollToPage(getCurrentRoundedPageIndex(), true);
                    return false;
                case 2:
                    if (ARPagerView.this.mContinuousScrollingEnabled) {
                        int dx = (int) ((ARPagerView.this.mPreviousMovePoint != null ? ARPagerView.this.mPreviousMovePoint.x : (float) ARPagerView.this.mDownPoint.x) - event.getX());
                        if (getScrollX() + dx < getLeft()) {
                            dx = getLeft() - getScrollX();
                        } else if (getScrollX() + dx > getRight()) {
                            dx = getRight() - getScrollX();
                        }
                        ARPagerView.this.mOverScroller.forceFinished(true);
                        ARPagerView.this.mOverScroller.startScroll(getScrollX(), getScrollY(), dx, 0, 0);
                        postInvalidate();
                        if (ARPagerView.this.mPreviousMovePoint == null) {
                            ARPagerView.this.mPreviousMovePoint = new PointF();
                        }
                        ARPagerView.this.mPreviousMovePoint.set(event.getX(), event.getY());
                        ARPagerView.this.mVelocityTracker.addMovement(event);
                        return false;
                    }
                    float distanceX2 = Math.abs(event.getX() - ((float) ARPagerView.this.mDownPoint.x));
                    float distanceY2 = Math.abs(event.getY() - ((float) ARPagerView.this.mDownPoint.y));
                    if (Build.VERSION.SDK_INT >= 19 && ((double) (((float) getHeight()) - event.getY())) < 0.09d * ((double) getHeight())) {
                        return true;
                    }
                    if (Math.hypot((double) distanceX2, (double) distanceY2) < ((double) ARPagerView.this.mPagingTouchSlop) || distanceY2 > distanceX2) {
                        return false;
                    }
                    ARPagerView.this.mVelocityTracker.addMovement(event);
                    ARPagerView.this.mVelocityTracker.computeCurrentVelocity(1000);
                    float velocityX2 = ARPagerView.this.mVelocityTracker.getXVelocity();
                    if (Math.abs(velocityX2) > ((float) ARPagerView.this.MINIMUM_FLING_VELOCITY)) {
                        if (velocityX2 < 0.0f) {
                            scrollToPage(getCurrentRoundedPageIndex() + 1, true);
                        } else {
                            scrollToPage(getCurrentRoundedPageIndex() - 1, true);
                        }
                        ARPagerView.this.mVelocityTracker.recycle();
                        ARPagerView.this.mVelocityTracker = null;
                    }
                    return true;
                case 3:
                    ARPagerView.this.mVelocityTracker.recycle();
                    ARPagerView.this.mVelocityTracker = null;
                    ARPagerView.this.mPreviousMovePoint = null;
                    scrollToPage(getCurrentRoundedPageIndex(), true);
                    return false;
                default:
                    return false;
            }
        }

        @Override // android.view.View
        public void computeScroll() {
            if (ARPagerView.this.mOverScroller.computeScrollOffset()) {
                scrollTo(ARPagerView.this.mOverScroller.getCurrX(), ARPagerView.this.mOverScroller.getCurrY());
                ARPagerView.this.scrollViewDidScrollInternal();
                updateHints();
                requestLayout();
            }
        }

        protected void scrollToPage(int index, boolean animated) {
            int duration;
            int index2 = GeneralUtil.constrain(index, 0, ARPagerView.this.mPages.size() - 1);
            if (animated) {
                duration = (int) (((ARPagerPage) ARPagerView.this.mPages.get(index2)).getRelativeWidth() * 400.0f);
            } else {
                duration = 0;
            }
            ARPagerView.this.mOverScroller.forceFinished(true);
            ARPagerView.this.mOverScroller.startScroll(getScrollX(), getScrollY(), getScrollXForPage(index2) - getScrollX(), 0, duration);
            postInvalidate();
            ARPagerView.this.mTargetPageIndex = index2;
            if (ARPagerView.this.mDelegate != null) {
                ARPagerView.this.mDelegate.pagerViewWillScrollToPageAtIndex(ARPagerView.this, index2);
            }
        }

        public void updateHints() {
            float pageWidth = (float) getPageWidth();
            float leftEdgeX = (float) getScrollX();
            float rightEdgeX = (float) (getScrollX() + getWidth());
            int indexOfPageToTheLeft = Math.round((leftEdgeX / pageWidth) - 1.0f);
            int indexOfPageToTheRight = Math.round(rightEdgeX / pageWidth);
            float leftPageBorderDistance = Math.abs(((((float) indexOfPageToTheLeft) * pageWidth) + pageWidth) - leftEdgeX);
            float rightPageBorderDistance = Math.abs((((float) indexOfPageToTheRight) * pageWidth) - rightEdgeX);
            updateHintLabel(ARPagerView.this.mLeftHint, indexOfPageToTheLeft, leftPageBorderDistance);
            updateHintLabel(ARPagerView.this.mRightHint, indexOfPageToTheRight, rightPageBorderDistance);
        }

        private void updateHintLabel(ARRotatedHintView hintLabel, int pageIndex, float distanceToPageBorder) {
            if (indexOutOfBounds(pageIndex)) {
                hintLabel.setAlpha(0.0f);
                return;
            }
            hintLabel.setText(((ARPagerPage) ARPagerView.this.mPages.get(pageIndex)).getTitle());
            hintLabel.setAlpha(GeneralUtil.mapConstrained(distanceToPageBorder, 0.0f, (float) (getPageWidth() / 4), 1.0f, 0.0f));
        }

        private int computeCurrentHeight() {
            if (getContentWidth() <= 0 || ARPagerView.this.mPages.isEmpty()) {
                return 0;
            }
            float pageWidth = (float) getPageWidth();
            float leftEdgeIndex = ((float) getScrollX()) / pageWidth;
            float rightEdgeIndex = ((float) (getScrollX() + getWidth())) / pageWidth;
            int leftmostPageIndex = GeneralUtil.constrain((int) Math.floor((double) leftEdgeIndex), 0, ARPagerView.this.mPages.size() - 1);
            int rightmostPageIndex = GeneralUtil.constrain(((int) Math.ceil((double) rightEdgeIndex)) - 1, 0, ARPagerView.this.mPages.size() - 1);
            if (leftmostPageIndex == rightmostPageIndex) {
                return heightForPageAtIndex(leftmostPageIndex);
            }
            float maxHeightMovingLeft = 0.0f;
            float maxHeightMovingRight = 0.0f;
            for (int i = leftmostPageIndex; i <= rightmostPageIndex; i++) {
                float pageHeight = (float) heightForPageAtIndex(i);
                if (i != rightmostPageIndex) {
                    maxHeightMovingLeft = Math.max(maxHeightMovingLeft, pageHeight);
                }
                if (i != leftmostPageIndex) {
                    maxHeightMovingRight = Math.max(maxHeightMovingRight, pageHeight);
                }
            }
            return (int) GeneralUtil.mapConstrained(leftEdgeIndex - ((float) leftmostPageIndex), 0.0f, 1.0f, maxHeightMovingLeft, maxHeightMovingRight);
        }

        private int heightForPageAtIndex(int index) {
            return ((ARPagerPage) ARPagerView.this.mPages.get(index)).getView().getMeasuredHeight();
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

        @Override // com.sparkappdesign.archimedes.archimedes.views.ARViewGroup
        public RectF finalBoundsForChildAtIndex(int index) {
            PointF size = ((ARView) getChildAt(index)).finalSize();
            return RectUtil.create((float) getChildAt(index).getLeft(), 0.0f, size.x, size.y);
        }

        @Override // com.sparkappdesign.archimedes.archimedes.views.ARView
        public PointF finalSize() {
            return ((ARView) ((ARPagerPage) ARPagerView.this.mPages.get(ARPagerView.this.mTargetPageIndex)).getView()).finalSize();
        }
    }

    /* loaded from: classes.dex */
    public class ARRotatedHintView extends View {
        private int mRotation;
        private float mTextWidth;
        private String mText = "";
        private Paint mPaint = new Paint();
        private final float HINT_MARGIN = getResources().getDimension(R.dimen.hint_margin);
        private final float HINT_TAP_MARGIN = getResources().getDimension(R.dimen.hint_tap_margin);

        public void setText(String text) {
            this.mText = text;
            invalidate();
        }

        public void setTypeface(Typeface typeface) {
            this.mPaint.setTypeface(typeface);
            invalidate();
        }

        public void setTextSize(float textSize) {
            this.mPaint.setTextSize(textSize);
            invalidate();
        }

        public void setTextColor(int color) {
            this.mPaint.setColor(color);
            invalidate();
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ARRotatedHintView(Context context, int rotation) {
            super(context);
            ARPagerView.this = r3;
            this.mRotation = rotation;
            this.mPaint.setAntiAlias(true);
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        private ARRotatedHintView(Context context) {
            super(context);
            ARPagerView.this = r3;
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        private ARRotatedHintView(Context context, AttributeSet attrs) {
            super(context, attrs);
            ARPagerView.this = r3;
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        private ARRotatedHintView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            ARPagerView.this = r3;
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension((int) (this.mPaint.getTextSize() + this.HINT_MARGIN + this.HINT_TAP_MARGIN), View.MeasureSpec.getSize(heightMeasureSpec));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            this.mTextWidth = this.mPaint.measureText(this.mText);
            canvas.save();
            canvas.rotate((float) this.mRotation, (float) (canvas.getWidth() / 2), (float) (canvas.getHeight() / 2));
            canvas.translate(0.0f, ((float) ((-canvas.getWidth()) / 2)) + this.mPaint.getTextSize());
            canvas.drawText(this.mText, ((float) (canvas.getWidth() / 2)) - (this.mTextWidth / 2.0f), this.HINT_TAP_MARGIN + ((float) (canvas.getHeight() / 2)), this.mPaint);
            canvas.restore();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            FloatingOptionsMenu.getInstance(getContext()).hide(true);
            if (!ARPagerView.this.mAllowTapOnHint) {
                return false;
            }
            if (event.getAction() != 0) {
                return true;
            }
            int currentIndex = ARPagerView.this.mContentView.getCurrentRoundedPageIndex();
            ARPagerView.this.mContentView.scrollToPage(this.mRotation == 90 ? currentIndex - 1 : currentIndex + 1, true);
            return true;
        }
    }
}
