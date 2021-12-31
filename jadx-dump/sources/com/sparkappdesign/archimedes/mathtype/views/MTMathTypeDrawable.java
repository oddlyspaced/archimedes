package com.sparkappdesign.archimedes.mathtype.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.animation.LinearInterpolator;
import com.sparkappdesign.archimedes.R;
import com.sparkappdesign.archimedes.mathtype.enums.MTAlignmentType;
import com.sparkappdesign.archimedes.mathtype.enums.MTDigitGroupingStyle;
import com.sparkappdesign.archimedes.mathtype.measures.MTAlignment;
import com.sparkappdesign.archimedes.mathtype.measures.MTGlyphMeasures;
import com.sparkappdesign.archimedes.mathtype.measures.MTMeasureContext;
import com.sparkappdesign.archimedes.mathtype.measures.MTMeasures;
import com.sparkappdesign.archimedes.mathtype.measures.font.MTFont;
import com.sparkappdesign.archimedes.mathtype.measures.font.MTFontMyriadProLight;
import com.sparkappdesign.archimedes.mathtype.nodes.MTElement;
import com.sparkappdesign.archimedes.mathtype.nodes.MTNode;
import com.sparkappdesign.archimedes.mathtype.nodes.MTString;
import com.sparkappdesign.archimedes.mathtype.views.selection.MTSelectionDrawable;
import com.sparkappdesign.archimedes.utilities.GeneralUtil;
import com.sparkappdesign.archimedes.utilities.PointUtil;
import com.sparkappdesign.archimedes.utilities.TypefaceCache;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
/* loaded from: classes.dex */
public class MTMathTypeDrawable extends Drawable {
    public static float HORIZONTAL_CONTENT_PADDING;
    public static float VERTICAL_CONTENT_PADDING;
    private float mAdditionalPaddingBottom;
    private float mAdditionalPaddingTop;
    private ValueAnimator mAnimator;
    private int mColor;
    private MTDigitGroupingStyle mDigitGroupingStyle;
    private MTFont mFont;
    private MTMeasures mMeasures;
    private boolean mNeedsUpdate;
    private HashMap<MTNode, MTNodeDrawable> mNodeDrawables;
    private MTMeasures mOldMeasures;
    private MTSelectionDrawable mSelectionDrawable;
    private Typeface mTypeface;
    private MTString mString = new MTString();
    private EnumSet<MTAlignmentType> mAlignment = EnumSet.of(MTAlignmentType.CenterX, MTAlignmentType.CenterY);
    private boolean mShowPlaceholderGlyphs = true;
    private Paint mPaint = new Paint();
    private LinearInterpolator mInterpolator = new LinearInterpolator();

    public MTString getString() {
        return this.mString;
    }

    public void setString(MTString string) {
        if (this.mString != string) {
            this.mString = string;
        }
    }

    public MTFont getFont() {
        return this.mFont;
    }

    public void setFont(MTFont font) {
        if (!GeneralUtil.equalOrBothNull(this.mFont, font)) {
            this.mFont = font;
        }
    }

    public MTMeasures getMeasures() {
        return this.mMeasures;
    }

    public void setMeasures(MTMeasures measures) {
        if (this.mMeasures != measures) {
            this.mMeasures = measures;
        }
    }

    public int getColor() {
        return this.mColor;
    }

    public void setColor(int color) {
        if (this.mColor != color) {
            this.mColor = color;
            this.mPaint.setColor(color);
        }
    }

    public EnumSet<MTAlignmentType> getAlignment() {
        return this.mAlignment;
    }

    public void setAlignment(EnumSet<MTAlignmentType> alignment) {
        if (this.mAlignment != alignment) {
            this.mAlignment = alignment;
            updateMeasuresAlignment();
        }
    }

    public boolean shouldShowPlaceholderGlyphs() {
        return this.mShowPlaceholderGlyphs;
    }

    public void setShowPlaceholderGlyphs(boolean showPlaceholderGlyphs) {
        if (this.mShowPlaceholderGlyphs != showPlaceholderGlyphs) {
            this.mShowPlaceholderGlyphs = showPlaceholderGlyphs;
        }
    }

    public MTDigitGroupingStyle getDigitGroupingStyle() {
        return this.mDigitGroupingStyle;
    }

    public void setDigitGroupingStyle(MTDigitGroupingStyle digitGroupingStyle) {
        if (this.mDigitGroupingStyle != digitGroupingStyle) {
            this.mDigitGroupingStyle = digitGroupingStyle;
        }
    }

    public float getAdditionalPaddingTop() {
        return this.mAdditionalPaddingTop;
    }

    public void setAdditionalPaddingTop(float additionalPadding) {
        this.mAdditionalPaddingTop = additionalPadding;
    }

    public float getAdditionalPaddingBottom() {
        return this.mAdditionalPaddingBottom;
    }

    public void setAdditionalPaddingBottom(float additionalPadding) {
        this.mAdditionalPaddingBottom = additionalPadding;
    }

    public ValueAnimator getAnimator() {
        return this.mAnimator;
    }

    public void setSelectionDrawable(MTSelectionDrawable selectionDrawable) {
        this.mSelectionDrawable = selectionDrawable;
    }

    public void setNeedsUpdate() {
        this.mNeedsUpdate = true;
    }

    public PointF predictFinalSize() {
        MTMeasures measures = this.mMeasures;
        if (this.mNeedsUpdate) {
            measures = this.mString.measureWithContext(new MTMeasureContext(this.mFont, this.mDigitGroupingStyle, this.mShowPlaceholderGlyphs));
        }
        return new PointF(measures.getBounds().width(), measures.getBounds().height() + (2.0f * VERTICAL_CONTENT_PADDING) + this.mAdditionalPaddingTop + this.mAdditionalPaddingBottom);
    }

    public RectF getFinalBounds() {
        return new RectF(0.0f, 0.0f, (float) getBounds().width(), this.mMeasures.getBounds().height() + (2.0f * VERTICAL_CONTENT_PADDING) + this.mAdditionalPaddingTop + this.mAdditionalPaddingBottom);
    }

    private HashMap<MTNode, MTNodeDrawable> getNodeDrawables() {
        if (this.mNodeDrawables == null) {
            this.mNodeDrawables = new HashMap<>();
        }
        return this.mNodeDrawables;
    }

    public MTMeasures getCurrentVisualMeasures() {
        if (this.mAnimator == null || !this.mAnimator.isRunning()) {
            return this.mMeasures;
        }
        MTMeasures copy = this.mMeasures.copy();
        updateMeasuresForCurrentVisualPositionsAndBounds(copy);
        return copy;
    }

    private void updateMeasuresForCurrentVisualPositionsAndBounds(MTMeasures measures) {
        if (measures != null) {
            MTNode node = measures.getNode();
            MTNodeDrawable nodeDrawable = getNodeDrawables().get(node);
            MTNodeDrawable parentNodeDrawable = getNodeDrawables().get(node.getParent());
            PointF visualPosition = nodeDrawable.getCurrentPosition();
            if (parentNodeDrawable != null) {
                visualPosition = PointUtil.subtractPoints(visualPosition, parentNodeDrawable.getCurrentPosition());
            }
            measures.setPosition(visualPosition);
            measures.setBounds(nodeDrawable.getCurrentBounds());
            measures.setFontSizeInPixels(nodeDrawable.getCurrentFontSizeInPixels());
            for (String glyphKey : measures.getGlyphs().keySet()) {
                MTGlyphDrawable glyphDrawable = nodeDrawable.getGlyphDrawables().get(glyphKey);
                MTGlyphMeasures glyphMeasures = measures.getGlyphs().get(glyphKey);
                glyphMeasures.setPosition(glyphDrawable.getCurrentPosition());
                glyphMeasures.setPath(glyphDrawable.getCurrentPath());
                glyphMeasures.setBounds(glyphDrawable.getCurrentBounds());
            }
            Iterator<MTMeasures> it = measures.getChildren().iterator();
            while (it.hasNext()) {
                updateMeasuresForCurrentVisualPositionsAndBounds(it.next());
            }
        }
    }

    public MTMathTypeDrawable(Context context) {
        this.mColor = context.getResources().getColor(R.color.foreground);
        TypefaceCache.put(context, TypefaceCache.MYRIAD_PRO_LIGHT);
        this.mFont = new MTFontMyriadProLight(context.getResources().getDimension(R.dimen.regular_font_size));
        VERTICAL_CONTENT_PADDING = context.getResources().getDimension(R.dimen.math_type_vertical_padding);
        HORIZONTAL_CONTENT_PADDING = context.getResources().getDimension(R.dimen.math_type_horizontal_padding);
        if (this.mTypeface == null) {
            this.mTypeface = TypefaceCache.getMyriadProLight(context);
        }
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(this.mColor);
        this.mPaint.setTypeface(this.mTypeface);
        update();
    }

    public void update() {
        updateAnimated(0);
    }

    public void updateAnimated(long duration) {
        updateMeasures();
        updateDrawableAnimationValues(this.mMeasures);
        removeOrphanedDrawables(this.mOldMeasures);
        updateDrawablesAnimated(duration);
        this.mNeedsUpdate = false;
    }

    private void updateMeasures() {
        MTMeasureContext context = new MTMeasureContext(this.mFont, this.mDigitGroupingStyle, this.mShowPlaceholderGlyphs);
        this.mOldMeasures = this.mMeasures;
        this.mMeasures = this.mString.measureWithContext(context);
        updateMeasuresAlignment();
    }

    private void updateMeasuresAlignment() {
        this.mMeasures.setPosition(MTAlignment.offsetToAlignRectToRect(this.mMeasures.getBounds(), getFinalBounds(), this.mAlignment));
    }

    private void updateDrawableAnimationValues(MTMeasures measures) {
        if (measures != null) {
            MTNode node = measures.getNode();
            MTNodeDrawable nodeDrawable = getNodeDrawables().get(node);
            if (nodeDrawable == null) {
                nodeDrawable = new MTNodeDrawable(this.mPaint, measures, initialPositionForNewNode(node), initialBoundsForNewNode(node), measures.getFontSizeInPixels());
                getNodeDrawables().put(node, nodeDrawable);
            }
            nodeDrawable.setFinalValues(measures, measures.getAbsolutePosition(), measures.getBounds(), measures.getFontSizeInPixels());
            Iterator<MTMeasures> it = measures.getChildren().iterator();
            while (it.hasNext()) {
                updateDrawableAnimationValues(it.next());
            }
        }
    }

    private PointF initialPositionForNewNode(MTNode node) {
        MTMeasures measures = this.mMeasures.descendantForNode(node);
        MTNode referenceNode = firstPreviouslyExistingDescendantOfNode(node);
        if (referenceNode != null) {
            return initialPositionForNewNodeKeepingOffsetToReferenceNode(node, referenceNode);
        }
        if (node instanceof MTElement) {
            MTElement element = (MTElement) node;
            MTString string = element.getParent();
            MTElement previousElement = string.elementBefore(element);
            if (previousElement != null) {
                PointF initialPosition = initialPositionForNewNodeKeepingOffsetToReferenceNode(node, previousElement);
                boolean isAtEnd = string.getParent() == null;
                int i = element.indexInParentString() + 1;
                while (true) {
                    if (i >= string.length()) {
                        break;
                    }
                    MTNodeDrawable nodeDrawableAtIndex = getNodeDrawables().get(string.elementAtIndex(i));
                    if (!(nodeDrawableAtIndex == null || nodeDrawableAtIndex.getCurrentPosition() == null)) {
                        isAtEnd = false;
                        break;
                    }
                    i++;
                }
                if (isAtEnd) {
                    return initialPosition;
                }
                initialPosition.x -= measures.getBounds().width() / 2.0f;
                return initialPosition;
            }
            for (int i2 = element.indexInParentString() + 1; i2 < string.length(); i2++) {
                MTElement nextElement = string.elementAtIndex(i2);
                MTNodeDrawable nextElementDrawable = getNodeDrawables().get(nextElement);
                if (nextElementDrawable != null && nextElementDrawable.getCurrentPosition() != null) {
                    PointF initialPosition2 = initialPositionForNewNodeKeepingOffsetToReferenceNode(node, nextElement);
                    if (string.getParent() == null) {
                        return initialPosition2;
                    }
                    initialPosition2.x += measures.getBounds().width() / 2.0f;
                    return initialPosition2;
                }
            }
            if (string.length() == 1) {
                MTNodeDrawable stringDrawable = getNodeDrawables().get(string);
                PointF p = stringDrawable != null ? new PointF(stringDrawable.getCurrentPosition().x, stringDrawable.getCurrentPosition().y) : new PointF();
                p.x += (stringDrawable != null ? stringDrawable.getCurrentBounds() : new RectF()).width() / 2.0f;
                p.x -= measures.getBounds().width() / 2.0f;
                return p;
            }
        }
        MTNode parent = node.getParent();
        return parent != null ? initialPositionForNewNodeKeepingOffsetToReferenceNode(node, parent) : measures.getPosition();
    }

    private MTNode firstPreviouslyExistingDescendantOfNode(MTNode node) {
        if (!(this.mOldMeasures == null || node.getChildren() == null)) {
            for (MTNode child : node.getChildren()) {
                if (!(this.mOldMeasures.descendantForNode(child) == null || this.mMeasures.descendantForNode(child) == null)) {
                    return child;
                }
                MTNode resultNode = firstPreviouslyExistingDescendantOfNode(child);
                if (resultNode != null) {
                    return resultNode;
                }
            }
        }
        return null;
    }

    private PointF initialPositionForNewNodeKeepingOffsetToReferenceNode(MTNode node, MTNode referenceNode) {
        MTNodeDrawable referenceNodeDrawable = getNodeDrawables().get(referenceNode);
        return PointUtil.addPoints(referenceNodeDrawable != null ? referenceNodeDrawable.getCurrentPosition() : new PointF(), PointUtil.subtractPoints(this.mMeasures.descendantForNode(node).getAbsolutePosition(), this.mMeasures.descendantForNode(referenceNode).getAbsolutePosition()));
    }

    private RectF initialBoundsForNewNode(MTNode node) {
        return this.mMeasures.descendantForNode(node).getBounds();
    }

    private void removeOrphanedDrawables(MTMeasures measures) {
        if (measures != null) {
            MTNode node = measures.getNode();
            if (this.mString.containsDescendant(node) || node == this.mString) {
                getNodeDrawables().get(node).removeOrphanedGlyphDrawables(this.mMeasures.descendantForNode(node));
            } else {
                getNodeDrawables().remove(node);
            }
            Iterator<MTMeasures> it = measures.getChildren().iterator();
            while (it.hasNext()) {
                removeOrphanedDrawables(it.next());
            }
        }
    }

    private void updateDrawablesAnimated(long duration) {
        if (this.mAnimator != null) {
            this.mAnimator.cancel();
        }
        this.mAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.mAnimator.setInterpolator(this.mInterpolator);
        this.mAnimator.setDuration(duration);
        this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.sparkappdesign.archimedes.mathtype.views.MTMathTypeDrawable.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                MTMathTypeDrawable.this.updateDrawablesForAnimationFraction(valueAnimator.getAnimatedFraction());
                MTMathTypeDrawable.this.invalidateSelf();
            }
        });
        this.mAnimator.start();
        if (this.mSelectionDrawable != null) {
            this.mSelectionDrawable.handleMathTypeUpdate(duration);
        }
    }

    public void updateDrawablesForAnimationFraction(float animationFraction) {
        for (MTNodeDrawable nodeDrawable : getNodeDrawables().values()) {
            nodeDrawable.update(animationFraction);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        for (MTNodeDrawable nodeDrawable : getNodeDrawables().values()) {
            nodeDrawable.draw(canvas);
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
