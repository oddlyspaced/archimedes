package com.oddlyspaced.calci.mathtype.nodes;

import com.oddlyspaced.calci.mathtype.enums.MTNodeTraits;
import com.oddlyspaced.calci.mathtype.measures.MTMeasureContext;
import com.oddlyspaced.calci.mathtype.measures.MTMeasures;
import java.io.Serializable;
import java.util.EnumSet;
/* loaded from: classes.dex */
public abstract class MTNode implements Serializable {
    public MTNode mParent;
    public EnumSet<MTNodeTraits> mTraits = EnumSet.noneOf(MTNodeTraits.class);

    public abstract MTNode copy();

    public abstract boolean equivalentTo(Object obj);

    public abstract Iterable<? extends MTNode> getChildren();

    public abstract MTMeasures measureWithContext(MTMeasureContext mTMeasureContext);

    public MTNode getParent() {
        return this.mParent;
    }

    public EnumSet<MTNodeTraits> getTraits() {
        return this.mTraits;
    }

    public void setTraits(EnumSet<MTNodeTraits> traits) {
        if (traits == null) {
            traits = EnumSet.noneOf(MTNodeTraits.class);
        }
        this.mTraits = traits;
    }

    public MTNode rootNode() {
        return this.mParent == null ? this : this.mParent.rootNode();
    }

    public int numberOfAncestors() {
        if (this.mParent != null) {
            return this.mParent.numberOfAncestors() + 1;
        }
        return 0;
    }

    public boolean isAncestorOf(MTNode node) {
        if (node == null) {
            return false;
        }
        return node.isDescendantOf(this);
    }

    public boolean isDescendantOf(MTNode node) {
        if (this.mParent == null) {
            return false;
        }
        if (this.mParent == node) {
            return true;
        }
        return this.mParent.isDescendantOf(node);
    }
}
