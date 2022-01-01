package com.sparkappdesign.archimedes.archimedes.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import com.sparkappdesign.archimedes.R;
import com.sparkappdesign.archimedes.utilities.GeneralUtil;
import com.sparkappdesign.archimedes.utilities.TypefaceCache;
/* loaded from: classes.dex */
public class ARSeparatorView extends TextView {
    private int mColor = getContext().getResources().getColor(R.color.tint);
    private Typeface mFont = TypefaceCache.getMyriadProLight(getContext());
    private float mFontSize = 11.0f;
    private String mText = "";
    private RectF mLeftLine = new RectF();
    private RectF mRightLine = new RectF();
    private Paint mPaint = new Paint();
    private final float TEXT_MARGIN = getContext().getResources().getDimension(R.dimen.answer_title_margin);
    public final float LINE_HEIGHT = getContext().getResources().getDimension(R.dimen.separator_line_thickness);

    public int getColor() {
        return this.mColor;
    }

    public void setColor(int color) {
        if (this.mColor != color) {
            this.mColor = color;
            updateTextAndColor();
        }
    }

    public Typeface getFont() {
        return this.mFont;
    }

    public void setFont(Typeface font) {
        if (!GeneralUtil.equalOrBothNull(this.mFont, font)) {
            this.mFont = font;
            updateTextAndColor();
        }
    }

    public float getFontSize() {
        return this.mFontSize;
    }

    public void setFontSize(float fontSize) {
        if (this.mFontSize != fontSize) {
            this.mFontSize = fontSize;
            updateTextAndColor();
        }
    }

    @Override // android.widget.TextView
    public String getText() {
        return this.mText;
    }

    public void setText(String text) {
        if (!GeneralUtil.equalOrBothNull(this.mText, text)) {
            this.mText = text;
            updateTextAndColor();
        }
    }

    private RectF getBounds() {
        return new RectF((float) getLeft(), (float) getTop(), (float) getRight(), (float) getBottom());
    }

    public ARSeparatorView(Context context) {
        super(context);
        setTextIsSelectable(false);
        this.mPaint.setAntiAlias(true);
        setGravity(17);
        updateTextAndColor();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.sparkappdesign.archimedes.archimedes.views.ARSeparatorView.1
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                ARSeparatorView.this.updateLayout();
                ARSeparatorView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), getLineHeight());
        updateLayout();
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(this.mLeftLine, this.mPaint);
        canvas.drawRect(this.mRightLine, this.mPaint);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLayout() {
        float textMargin;
        if (this.mText.length() > 0) {
            textMargin = this.TEXT_MARGIN;
        } else {
            textMargin = 0.0f;
        }
        float textWidth = this.mPaint.measureText(this.mText);
        PointF viewCenter = new PointF(getBounds().centerX(), getBounds().centerY());
        float lineToCenterDistance = (textWidth / 2.0f) + textMargin;
        float lineWidth = GeneralUtil.constrainMin(((float) (getMeasuredWidth() / 2)) - lineToCenterDistance, 0.0f);
        float lineY = (float) Math.round(viewCenter.y - (this.LINE_HEIGHT / 2.0f));
        this.mLeftLine.set(0.0f, lineY, lineWidth, this.LINE_HEIGHT + lineY);
        this.mRightLine.set(viewCenter.x + lineToCenterDistance, lineY, viewCenter.x + lineToCenterDistance + lineWidth, this.LINE_HEIGHT + lineY);
        invalidate();
    }

    private void updateTextAndColor() {
        this.mPaint.setColor(this.mColor);
        this.mPaint.setTypeface(this.mFont);
        this.mPaint.setTextSize(TypedValue.applyDimension(1, this.mFontSize, getContext().getResources().getDisplayMetrics()));
        super.setTextColor(this.mColor);
        super.setTypeface(this.mFont);
        super.setTextSize(1, this.mFontSize);
        super.setText((CharSequence) this.mText);
    }
}
