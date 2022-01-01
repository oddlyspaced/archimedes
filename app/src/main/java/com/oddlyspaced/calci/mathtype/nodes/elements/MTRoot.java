package com.sparkappdesign.archimedes.mathtype.nodes.elements;

import com.sparkappdesign.archimedes.mathtype.enums.MTNodeTraits;
import com.sparkappdesign.archimedes.mathtype.measures.MTCommonMeasures;
import com.sparkappdesign.archimedes.mathtype.measures.MTMeasureContext;
import com.sparkappdesign.archimedes.mathtype.measures.MTMeasures;
import com.sparkappdesign.archimedes.mathtype.nodes.MTElement;
import com.sparkappdesign.archimedes.mathtype.nodes.MTString;
import java.util.Arrays;
import java.util.EnumSet;
/* loaded from: classes.dex */
public class MTRoot extends MTElement {
    private MTString mContents;
    private MTString mDegree;

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTElement, com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public Iterable<? extends MTString> getChildren() {
        return Arrays.asList(this.mContents, this.mDegree);
    }

    public MTString getContents() {
        return this.mContents;
    }

    public MTString getDegree() {
        return this.mDegree;
    }

    public MTRoot() {
        this.mContents = new MTString(this);
        this.mDegree = new MTString(this);
    }

    public MTRoot(boolean degreeEditable) {
        this(null, null, degreeEditable);
    }

    public MTRoot(Iterable<? extends MTElement> contentElements, boolean degreeEditable) {
        this(contentElements, null, degreeEditable);
    }

    public MTRoot(Iterable<? extends MTElement> contentElements, Iterable<? extends MTElement> degreeElements, boolean degreeEditable) {
        this();
        if (contentElements != null) {
            this.mContents.appendElements(contentElements);
        }
        if (degreeElements != null) {
            this.mDegree.appendElements(degreeElements);
        }
        this.mDegree.mTraits = !degreeEditable ? EnumSet.of(MTNodeTraits.CantSelectOrEditChildren) : EnumSet.noneOf(MTNodeTraits.class);
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public MTMeasures measureWithContext(MTMeasureContext context) {
        return MTCommonMeasures.measuresForRoot(this, this.mContents, this.mDegree, context);
    }

    @Override // java.lang.Object
    public String toString() {
        if (this.mDegree.isNotEmpty()) {
            return "√[" + this.mDegree + "](" + this.mContents + ")";
        }
        return "√(" + this.mContents + ")";
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTElement, com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public MTRoot copy() {
        MTRoot copy = new MTRoot();
        copy.mTraits = this.mTraits.clone();
        copy.mContents = this.mContents.copy();
        copy.mContents.mParent = copy;
        copy.mDegree = this.mDegree.copy();
        copy.mDegree.mParent = copy;
        return copy;
    }

    @Override // com.sparkappdesign.archimedes.mathtype.nodes.MTNode
    public boolean equivalentTo(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof MTRoot) {
            MTRoot otherRoot = (MTRoot) other;
            if (this.mContents.equivalentTo(otherRoot.mContents) && this.mDegree.equivalentTo(otherRoot.mDegree)) {
                return true;
            }
        }
        return false;
    }
}
