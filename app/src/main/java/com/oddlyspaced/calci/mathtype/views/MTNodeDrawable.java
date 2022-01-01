package com.oddlyspaced.calci.mathtype.views;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import com.oddlyspaced.calci.mathtype.measures.MTGlyphMeasures;
import com.oddlyspaced.calci.mathtype.measures.MTMeasures;
import com.oddlyspaced.calci.utilities.PointUtil;
import com.oddlyspaced.calci.utilities.RectUtil;
import com.oddlyspaced.calci.utilities.animatable.AnimatableFloat;
import com.oddlyspaced.calci.utilities.animatable.AnimatablePointF;
import com.oddlyspaced.calci.utilities.animatable.AnimatableRectF;
import com.oddlyspaced.calci.utilities.path.PathWrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/* loaded from: classes.dex */
public class MTNodeDrawable extends Drawable {
    private AnimatableRectF mBounds;
    private AnimatableFloat mFontSizeInPixels;
    private HashMap<String, MTGlyphDrawable> mGlyphDrawables;
    private Paint mPaint;
    private AnimatablePointF mPosition;

    public HashMap<String, MTGlyphDrawable> getGlyphDrawables() {
        if (this.mGlyphDrawables == null) {
            this.mGlyphDrawables = new HashMap<>();
        }
        return this.mGlyphDrawables;
    }

    public PointF getCurrentPosition() {
        return this.mPosition.getCurrentValue();
    }

    public RectF getCurrentBounds() {
        return this.mBounds.getCurrentValue();
    }

    public float getCurrentFontSizeInPixels() {
        return this.mFontSizeInPixels.getCurrentValue().floatValue();
    }

    public MTNodeDrawable(Paint paint, MTMeasures measures, PointF initialPosition, RectF initialBounds, float initialFontSizeInPixels) {
        this.mPaint = paint;
        this.mPosition = new AnimatablePointF(initialPosition);
        this.mBounds = new AnimatableRectF(initialBounds);
        this.mFontSizeInPixels = new AnimatableFloat(Float.valueOf(initialFontSizeInPixels));
        for (String key : measures.getGlyphs().keySet()) {
            MTGlyphMeasures glyphMeasures = measures.getGlyphs().get(key);
            getGlyphDrawables().get(key);
            getGlyphDrawables().put(key, new MTGlyphDrawable(this.mPaint, PointUtil.addPoints(this.mPosition.getCurrentValue(), glyphMeasures.getPosition()), glyphMeasures.getPath(), RectUtil.translate(glyphMeasures.getBounds(), this.mPosition.getCurrentValue())));
        }
    }

    public void setFinalValues(MTMeasures measures, PointF finalPosition, RectF finalBounds, float finalFontSizeInPixels) {
        this.mPosition.setForAnimationWithTargetValue(finalPosition);
        this.mBounds.setForAnimationWithTargetValue(finalBounds);
        this.mFontSizeInPixels.setForAnimationWithTargetValue(Float.valueOf(finalFontSizeInPixels));
        for (String key : measures.getGlyphs().keySet()) {
            MTGlyphMeasures glyphMeasures = measures.getGlyphs().get(key);
            MTGlyphDrawable glyphDrawable = getGlyphDrawables().get(key);
            PointF finalGlyphPosition = PointUtil.addPoints(finalPosition, glyphMeasures.getPosition());
            PathWrapper finalGlyphPath = glyphMeasures.getPath();
            RectF finalGlyphBounds = RectUtil.translate(glyphMeasures.getBounds(), finalPosition);
            if (glyphDrawable == null) {
                glyphDrawable = new MTGlyphDrawable(this.mPaint, finalGlyphPosition, finalGlyphPath, finalGlyphBounds);
                getGlyphDrawables().put(key, glyphDrawable);
            }
            glyphDrawable.setFinalValues(finalGlyphPosition, finalGlyphPath, finalGlyphBounds);
        }
    }

    public void removeOrphanedGlyphDrawables(MTMeasures measures) {
        ArrayList<String> orphanedGlyphDrawableKeys = new ArrayList<>();
        for (String key : getGlyphDrawables().keySet()) {
            if (!measures.getGlyphs().containsKey(key)) {
                orphanedGlyphDrawableKeys.add(key);
            }
        }
        Iterator<String> it = orphanedGlyphDrawableKeys.iterator();
        while (it.hasNext()) {
            getGlyphDrawables().remove(it.next());
        }
    }

    public void update(float animationFraction) {
        this.mPosition.updateForAnimationFraction(animationFraction);
        this.mBounds.updateForAnimationFraction(animationFraction);
        this.mFontSizeInPixels.updateForAnimationFraction(animationFraction);
        for (MTGlyphDrawable glyphDrawable : getGlyphDrawables().values()) {
            glyphDrawable.update(animationFraction);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        for (MTGlyphDrawable glyphDrawable : getGlyphDrawables().values()) {
            glyphDrawable.draw(canvas);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }
}
