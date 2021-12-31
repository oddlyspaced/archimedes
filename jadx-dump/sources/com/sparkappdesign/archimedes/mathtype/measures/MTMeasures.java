package com.sparkappdesign.archimedes.mathtype.measures;

import android.graphics.PointF;
import android.graphics.RectF;
import com.sparkappdesign.archimedes.mathtype.measures.font.MTFont;
import com.sparkappdesign.archimedes.mathtype.nodes.MTNode;
import com.sparkappdesign.archimedes.utilities.PointUtil;
import com.sparkappdesign.archimedes.utilities.RectUtil;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
/* loaded from: classes.dex */
public class MTMeasures {
    private MTFont mFont;
    private float mFontSizeInPixels;
    private MTNode mNode;
    private MTMeasures mParent;
    private ArrayList<MTMeasures> mChildren = new ArrayList<>();
    private Hashtable<String, MTGlyphMeasures> mGlyphs = new Hashtable<>();
    private Hashtable<String, Object> mExtraInfo = new Hashtable<>();
    private PointF mPosition = new PointF();
    private RectF mBounds = new RectF();

    public MTNode getNode() {
        return this.mNode;
    }

    public void setNode(MTNode node) {
        this.mNode = node;
    }

    public MTFont getFont() {
        return this.mFont;
    }

    public void setFont(MTFont font) {
        this.mFont = font;
    }

    public float getFontSizeInPixels() {
        return this.mFontSizeInPixels;
    }

    public void setFontSizeInPixels(float fontSizeInPixels) {
        this.mFontSizeInPixels = fontSizeInPixels;
    }

    public MTMeasures getParent() {
        return this.mParent;
    }

    public ArrayList<MTMeasures> getChildren() {
        return this.mChildren;
    }

    public Hashtable<String, MTGlyphMeasures> getGlyphs() {
        return this.mGlyphs;
    }

    public Hashtable<String, Object> getExtraInfo() {
        return this.mExtraInfo;
    }

    public PointF getPosition() {
        return this.mPosition;
    }

    public void setPosition(PointF position) {
        if (this.mPosition != position) {
            this.mPosition.set(position);
        }
    }

    public RectF getBounds() {
        return this.mBounds;
    }

    public void setBounds(RectF bounds) {
        if (this.mBounds != bounds) {
            this.mBounds.set(bounds);
        }
    }

    public PointF getAbsolutePosition() {
        if (this.mParent != null) {
            return PointUtil.addPoints(this.mParent.getAbsolutePosition(), this.mPosition);
        }
        return this.mPosition;
    }

    public RectF getAbsoluteBounds() {
        return RectUtil.translate(this.mBounds, getAbsolutePosition());
    }

    public PointF getNextElementOffset() {
        return new PointF(this.mBounds.right, 0.0f);
    }

    public MTMeasures(MTNode node, MTMeasureContext context) {
        this.mNode = node;
        this.mFont = context.getFont();
        this.mFontSizeInPixels = context.getFont().getFontSizeInPixels();
    }

    private MTMeasures() {
    }

    public MTMeasures root() {
        if (this.mParent != null) {
            return this.mParent.root();
        }
        return this;
    }

    public void addChild(MTMeasures child) {
        if (child != null) {
            this.mChildren.add(child);
            child.mParent = this;
        }
    }

    public void autoCalculateBounds() {
        RectF bounds = new RectF();
        for (MTGlyphMeasures glyph : this.mGlyphs.values()) {
            bounds.union(RectUtil.translate(glyph.getBounds(), glyph.getPosition()));
        }
        Iterator<MTMeasures> it = this.mChildren.iterator();
        while (it.hasNext()) {
            MTMeasures child = it.next();
            bounds.union(RectUtil.translate(child.getBounds(), child.getPosition()));
        }
        this.mBounds = bounds;
    }

    public MTMeasures descendantForNode(MTNode node) {
        if (this.mNode == node) {
            return this;
        }
        Iterator<MTMeasures> it = this.mChildren.iterator();
        while (it.hasNext()) {
            MTMeasures result = it.next().descendantForNode(node);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public ArrayList<MTMeasures> descendantsIncludingSelf() {
        ArrayList<MTMeasures> array = new ArrayList<>();
        addDescendantsIncludingSelfToArray(array);
        return array;
    }

    private void addDescendantsIncludingSelfToArray(ArrayList<MTMeasures> array) {
        array.add(this);
        Iterator<MTMeasures> it = this.mChildren.iterator();
        while (it.hasNext()) {
            it.next().addDescendantsIncludingSelfToArray(array);
        }
    }

    public MTMeasures copy() {
        MTMeasures copy = new MTMeasures();
        copy.mNode = this.mNode;
        copy.mFont = this.mFont;
        copy.mFontSizeInPixels = this.mFontSizeInPixels;
        Iterator<MTMeasures> it = this.mChildren.iterator();
        while (it.hasNext()) {
            copy.addChild(it.next().copy());
        }
        Hashtable<String, MTGlyphMeasures> glyphsCopy = new Hashtable<>(this.mGlyphs.size());
        for (String key : this.mGlyphs.keySet()) {
            glyphsCopy.put(key, this.mGlyphs.get(key).copy());
        }
        copy.mGlyphs = glyphsCopy;
        copy.mExtraInfo = new Hashtable<>(this.mExtraInfo);
        PointF positionCopy = new PointF();
        positionCopy.set(this.mPosition);
        copy.setPosition(positionCopy);
        RectF boundsCopy = new RectF();
        boundsCopy.set(this.mBounds);
        copy.setBounds(boundsCopy);
        return copy;
    }
}
