package com.sparkappdesign.archimedes.mathtype.nodes;
/* loaded from: classes.dex */
public abstract class MTElement extends MTNode {
    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public abstract MTElement copy();

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public Iterable<? extends MTString> getChildren() {
        return null;
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public MTString getParent() {
        return (MTString) this.mParent;
    }

    public int indexInParentString() {
        if (getParent() == null) {
            return -1;
        }
        return getParent().indexOfElement(this);
    }

    public MTElement previousElement() {
        if (getParent() == null) {
            return null;
        }
        return getParent().elementBefore(this);
    }

    public MTElement nextElement() {
        if (getParent() == null) {
            return null;
        }
        return getParent().elementAfter(this);
    }

    public void removeFromParentString() {
        if (getParent() != null) {
            getParent().removeElement(this);
        }
    }
}
