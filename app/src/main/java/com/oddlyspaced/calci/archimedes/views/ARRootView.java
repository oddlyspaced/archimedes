package com.oddlyspaced.calci.archimedes.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.oddlyspaced.calci.R;
import com.oddlyspaced.calci.archimedes.enums.ARButtonPadType;
/* loaded from: classes.dex */
public class ARRootView extends ViewGroup {
    private ARButtonPad mButtonPad;
    private ARCalculationListView mCalculationListView;
    private AROverlayView mOverlayView;

    public ARButtonPad getButtonPad() {
        return this.mButtonPad;
    }

    public ARCalculationListView getCalculationListView() {
        return this.mCalculationListView;
    }

    public AROverlayView getOverlayView() {
        return this.mOverlayView;
    }

    public ARRootView(Context context) {
        this(context, null, 0);
    }

    public ARRootView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ARRootView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        setBackground(context.getResources().getDrawable(R.drawable.main_background));
        this.mButtonPad = new ARButtonPad(context, ARButtonPadType.Main);
        this.mButtonPad.setBackgroundResource(R.drawable.button_pad_background);
        this.mButtonPad.setId(R.id.archimedes_button_pad);
        addView(this.mButtonPad);
        this.mCalculationListView = new ARCalculationListView(context);
        this.mCalculationListView.setId(R.id.archimedes_calculation_list_view);
        addView(this.mCalculationListView);
        this.mOverlayView = AROverlayView.getInstance(context);
        addView(this.mOverlayView);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        int childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.EXACTLY);
        this.mButtonPad.measure(childWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int remainingHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(parentHeight - this.mButtonPad.getMeasuredHeight(), MeasureSpec.EXACTLY);
        this.mCalculationListView.measure(childWidthMeasureSpec, remainingHeightMeasureSpec);
        this.mOverlayView.measure(childWidthMeasureSpec, remainingHeightMeasureSpec);
        setMeasuredDimension(parentWidth, parentHeight);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        this.mCalculationListView.layout(0, 0, this.mCalculationListView.getMeasuredWidth(), this.mCalculationListView.getMeasuredHeight());
        this.mOverlayView.layout(0, 0, this.mOverlayView.getMeasuredWidth(), this.mOverlayView.getMeasuredHeight());
        this.mButtonPad.layout(0, this.mCalculationListView.getMeasuredHeight(), this.mButtonPad.getMeasuredWidth(), this.mCalculationListView.getMeasuredHeight() + this.mButtonPad.getMeasuredHeight());
    }

    public void deinitialize() {
        removeAllViews();
        this.mButtonPad.deinitialize();
        this.mCalculationListView.deinitialize();
    }
}
