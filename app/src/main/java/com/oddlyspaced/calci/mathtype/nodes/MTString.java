package com.oddlyspaced.calci.mathtype.nodes;

import android.graphics.PointF;
import android.graphics.RectF;
import com.oddlyspaced.calci.mathtype.enums.MTNodeTraits;
import com.oddlyspaced.calci.mathtype.measures.MTCommonMeasures;
import com.oddlyspaced.calci.mathtype.measures.MTMeasureContext;
import com.oddlyspaced.calci.mathtype.measures.MTMeasures;
import com.oddlyspaced.calci.utilities.PointUtil;
import com.oddlyspaced.calci.utilities.Range;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
/* loaded from: classes.dex */
public class MTString extends MTNode {
    private ArrayList<MTElement> mElements;

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTNode
    public Iterable<? extends MTElement> getChildren() {
        return this.mElements;
    }

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTNode
    public MTElement getParent() {
        return (MTElement) this.mParent;
    }

    public MTString(MTElement parent) {
        this.mParent = parent;
        this.mElements = new ArrayList<>();
    }

    public MTString() {
        this(null);
    }

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTNode
    public MTMeasures measureWithContext(MTMeasureContext context) {
        if (isNotEmpty()) {
            MTMeasures measures = new MTMeasures(this, context);
            PointF offset = new PointF();
            Hashtable<MTElement, MTMeasures> previousMeasures = new Hashtable<>();
            Iterator<MTElement> it = this.mElements.iterator();
            while (it.hasNext()) {
                MTElement element = it.next();
                MTMeasureContext childContext = context.childContext();
                childContext.setMeasures(previousMeasures);
                MTMeasures childMeasures = element.measureWithContext(childContext);
                offset.x -= Math.min(childMeasures.getBounds().left, 0.0f);
                childMeasures.setPosition(offset);
                measures.addChild(childMeasures);
                offset = PointUtil.addPoints(offset, childMeasures.getNextElementOffset());
                previousMeasures.put(element, childMeasures);
            }
            measures.autoCalculateBounds();
            RectF baseBounds = context.getFont().genericLineBounds();
            baseBounds.union(measures.getBounds());
            measures.setBounds(baseBounds);
            return measures;
        } else if (getTraits() != null && getTraits().contains(MTNodeTraits.CantSelectOrEditChildren)) {
            return new MTMeasures(this, context);
        } else {
            return MTCommonMeasures.measuresForPlaceholder(this, context.isshowPlaceholderGlyphs() && getParent() != null, context);
        }
    }

    public int length() {
        return this.mElements.size();
    }

    public int indexAfterLastElement() {
        return this.mElements.size();
    }

    public int lastElementIndex() {
        return this.mElements.size() - 1;
    }

    public boolean isEmpty() {
        return this.mElements.size() == 0;
    }

    public boolean isNotEmpty() {
        return this.mElements.size() > 0;
    }

    public MTElement elementAtIndex(int index) {
        return this.mElements.get(index);
    }

    public MTElement elementBefore(MTElement element) {
        int index = indexOfElement(element);
        if (index > 0) {
            return elementAtIndex(index - 1);
        }
        return null;
    }

    public MTElement elementAfter(MTElement element) {
        int index = indexOfElement(element);
        if (index == -1 || index + 1 >= this.mElements.size()) {
            return null;
        }
        return elementAtIndex(index + 1);
    }

    public MTElement elementWithDescendant(MTNode node) {
        int index = indexOfElementWithDescendant(node);
        if (index == -1) {
            return null;
        }
        return elementAtIndex(index);
    }

    public MTElement firstElement() {
        if (this.mElements.size() == 0) {
            return null;
        }
        return this.mElements.get(0);
    }

    public MTElement lastElement() {
        if (this.mElements.size() == 0) {
            return null;
        }
        return this.mElements.get(this.mElements.size() - 1);
    }

    public boolean containsElement(MTElement element) {
        return this.mElements.contains(element);
    }

    public boolean containsDescendant(MTNode node) {
        return indexOfElementWithDescendant(node) != -1;
    }

    public int indexOfElement(MTElement element) {
        return this.mElements.indexOf(element);
    }

    public int indexOfElementWithDescendant(MTNode node) {
        while (node != null) {
            if (node.mParent == this) {
                return indexOfElement((MTElement) node);
            }
            node = node.mParent;
        }
        return -1;
    }

    public void appendElement(MTElement element) {
        insertElement(element, indexAfterLastElement());
    }

    public void appendElements(Iterable<? extends MTElement> elements) {
        for (MTElement element : elements) {
            appendElement(element);
        }
    }

    public void insertElement(MTElement element, int index) {
        this.mElements.add(index, element);
        element.mParent = this;
    }

    public void removeElementAtIndex(int index) {
        this.mElements.get(index).mParent = null;
        this.mElements.remove(index);
    }

    public void removeElementsInRange(Range range) {
        for (int i = 0; i < range.mLength; i++) {
            removeElementAtIndex(range.mStartIndex);
        }
    }

    public void removeElement(MTElement element) {
        int index = this.mElements.indexOf(element);
        if (index != -1) {
            removeElementAtIndex(index);
        }
    }

    public void removeAllElements() {
        removeElementsInRange(new Range(0, this.mElements.size()));
    }

    public void removeLastElement() {
        removeElementAtIndex(lastElementIndex());
    }

    public void replaceElement(int index, MTElement element) {
        removeElementAtIndex(index);
        insertElement(element, index);
    }

    public void moveElementsToString(MTString targetString, int targetIndex) {
        moveElementsInRangeToString(new Range(0, length()), targetString, targetIndex);
    }

    public void moveElementsInRangeToString(Range sourceRange, MTString targetString, int targetIndex) {
        for (int i = 0; i < sourceRange.mLength; i++) {
            removeElementAtIndex(sourceRange.mStartIndex);
            targetString.insertElement(this.mElements.get(sourceRange.mStartIndex), targetIndex);
            targetIndex++;
        }
    }

    public void copyElementsToString(MTString targetString, int targetIndex) {
        insertElementsInRangeInString(new Range(0, length()), targetString, targetIndex, true);
    }

    public void copyElementsInRangeToString(Range range, MTString targetString, int targetIndex) {
        insertElementsInRangeInString(range, targetString, targetIndex, true);
    }

    public void insertElementsInRangeInString(Range range, MTString string, int targetIndex, boolean copy) {
        MTElement element;
        for (int i = 0; i < range.mLength; i++) {
            if (copy) {
                element = this.mElements.get(range.mStartIndex + i).copy();
            } else {
                element = this.mElements.get(range.mStartIndex);
                removeElementAtIndex(range.mStartIndex);
            }
            string.insertElement(element, targetIndex);
            targetIndex++;
        }
    }

    @Override // java.lang.Object
    public String toString() {
        String string = "";
        Iterator<MTElement> it = this.mElements.iterator();
        while (it.hasNext()) {
            string = string + it.next().toString();
        }
        return string;
    }

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTNode
    public MTString copy() {
        MTString copy = new MTString();
        copy.mTraits = this.mTraits.clone();
        copy.mElements = new ArrayList<>();
        Iterator<MTElement> it = this.mElements.iterator();
        while (it.hasNext()) {
            MTElement elementCopy = it.next().copy();
            elementCopy.mParent = copy;
            copy.mElements.add(elementCopy);
        }
        return copy;
    }

    @Override // com.oddlyspaced.calci.mathtype.nodes.MTNode
    public boolean equivalentTo(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MTString)) {
            return false;
        }
        MTString otherString = (MTString) other;
        int length = length();
        if (length != otherString.length()) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (!elementAtIndex(i).equivalentTo(otherString.elementAtIndex(i))) {
                return false;
            }
        }
        return true;
    }
}
