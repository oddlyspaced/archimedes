package com.oddlyspaced.calci.utilities.path;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import com.oddlyspaced.calci.utilities.PointUtil;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes.dex */
public class TweenablePath extends PathWrapper {
    private ArrayList<PathElement> mElements = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface PathElement {
        void addToPath(Path path);

        PathElement copy();

        void setInterpolated(PathElement pathElement, PathElement pathElement2, float f);

        void transformPoints(float[] fArr);
    }

    public TweenablePath() {
    }

    public TweenablePath(TweenablePath tweenablePath) {
        super(tweenablePath);
        copyElements(tweenablePath);
    }

    public void moveTo(float x, float y) {
        this.mPath.moveTo(x, y);
        MoveElement element = new MoveElement();
        element.mEndPoint = new PointF(x, y);
        this.mElements.add(element);
    }

    public void lineTo(float x, float y) {
        this.mPath.lineTo(x, y);
        LineElement element = new LineElement();
        element.mEndPoint = new PointF(x, y);
        this.mElements.add(element);
    }

    public void quadTo(float x1, float y1, float x2, float y2) {
        this.mPath.quadTo(x1, y1, x2, y2);
        QuadElement element = new QuadElement();
        element.mControlPoint = new PointF(x1, y1);
        element.mEndPoint = new PointF(x2, y2);
        this.mElements.add(element);
    }

    public void cubicTo(float x1, float y1, float x2, float y2, float x3, float y3) {
        this.mPath.cubicTo(x1, y1, x2, y2, x3, y3);
        CubicElement element = new CubicElement();
        element.mStartControlPoint = new PointF(x1, y1);
        element.mEndControlPoint = new PointF(x2, y2);
        element.mEndPoint = new PointF(x3, y3);
        this.mElements.add(element);
    }

    public void close() {
        this.mPath.close();
        this.mElements.add(new CloseElement());
    }

    public void tweenTo(TweenablePath initial, TweenablePath target, float fraction) {
        this.mPath.rewind();
        int count = this.mElements.size();
        if (initial.mElements.size() != count) {
            throw new IllegalArgumentException("initial has a different number of path elements.");
        } else if (target.mElements.size() != count) {
            throw new IllegalArgumentException("target has a different number of path elements.");
        } else {
            for (int i = 0; i < count; i++) {
                PathElement currentElement = this.mElements.get(i);
                currentElement.setInterpolated(initial.mElements.get(i), target.mElements.get(i), fraction);
                currentElement.addToPath(this.mPath);
            }
        }
    }

    public void transform(Matrix matrix) {
        this.mPath.transform(matrix);
        float[] matrixValues = new float[9];
        matrix.getValues(matrixValues);
        Iterator<PathElement> it = this.mElements.iterator();
        while (it.hasNext()) {
            it.next().transformPoints(matrixValues);
        }
    }

    public void addPath(TweenablePath tweenablePath) {
        this.mPath.addPath(tweenablePath.getPath());
        Iterator<PathElement> it = tweenablePath.mElements.iterator();
        while (it.hasNext()) {
            this.mElements.add(it.next());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MoveElement implements PathElement {
        public PointF mEndPoint;

        private MoveElement() {
        }

        @Override // com.oddlyspaced.calci.utilities.path.TweenablePath.PathElement
        public void transformPoints(float[] matrixValues) {
            TweenablePath.this.transformPoint(this.mEndPoint, matrixValues);
        }

        @Override // com.oddlyspaced.calci.utilities.path.TweenablePath.PathElement
        public void setInterpolated(PathElement initial, PathElement target, float fraction) {
            PointUtil.setInterpolated(this.mEndPoint, ((MoveElement) initial).mEndPoint, ((MoveElement) target).mEndPoint, fraction);
        }

        @Override // com.oddlyspaced.calci.utilities.path.TweenablePath.PathElement
        public void addToPath(Path path) {
            path.moveTo(this.mEndPoint.x, this.mEndPoint.y);
        }

        @Override // com.oddlyspaced.calci.utilities.path.TweenablePath.PathElement
        public MoveElement copy() {
            MoveElement copy = new MoveElement();
            copy.mEndPoint = new PointF(this.mEndPoint.x, this.mEndPoint.y);
            return copy;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class LineElement implements PathElement {
        public PointF mEndPoint;

        private LineElement() {
        }

        @Override // com.oddlyspaced.calci.utilities.path.TweenablePath.PathElement
        public void transformPoints(float[] matrixValues) {
            TweenablePath.this.transformPoint(this.mEndPoint, matrixValues);
        }

        @Override // com.oddlyspaced.calci.utilities.path.TweenablePath.PathElement
        public void setInterpolated(PathElement initial, PathElement target, float fraction) {
            PointUtil.setInterpolated(this.mEndPoint, ((LineElement) initial).mEndPoint, ((LineElement) target).mEndPoint, fraction);
        }

        @Override // com.oddlyspaced.calci.utilities.path.TweenablePath.PathElement
        public void addToPath(Path path) {
            path.lineTo(this.mEndPoint.x, this.mEndPoint.y);
        }

        @Override // com.oddlyspaced.calci.utilities.path.TweenablePath.PathElement
        public LineElement copy() {
            LineElement copy = new LineElement();
            copy.mEndPoint = new PointF(this.mEndPoint.x, this.mEndPoint.y);
            return copy;
        }
    }

    /* loaded from: classes.dex */
    private class QuadElement implements PathElement {
        public PointF mControlPoint;
        public PointF mEndPoint;

        private QuadElement() {
        }

        @Override // com.oddlyspaced.calci.utilities.path.TweenablePath.PathElement
        public void transformPoints(float[] matrixValues) {
            TweenablePath.this.transformPoint(this.mControlPoint, matrixValues);
            TweenablePath.this.transformPoint(this.mEndPoint, matrixValues);
        }

        @Override // com.oddlyspaced.calci.utilities.path.TweenablePath.PathElement
        public void setInterpolated(PathElement initial, PathElement target, float fraction) {
            QuadElement initialMove = (QuadElement) initial;
            QuadElement targetMove = (QuadElement) target;
            PointUtil.setInterpolated(this.mControlPoint, initialMove.mControlPoint, targetMove.mControlPoint, fraction);
            PointUtil.setInterpolated(this.mEndPoint, initialMove.mEndPoint, targetMove.mEndPoint, fraction);
        }

        @Override // com.oddlyspaced.calci.utilities.path.TweenablePath.PathElement
        public void addToPath(Path path) {
            path.quadTo(this.mControlPoint.x, this.mControlPoint.y, this.mEndPoint.x, this.mEndPoint.y);
        }

        @Override // com.oddlyspaced.calci.utilities.path.TweenablePath.PathElement
        public QuadElement copy() {
            QuadElement copy = new QuadElement();
            copy.mControlPoint = new PointF(this.mControlPoint.x, this.mControlPoint.y);
            copy.mEndPoint = new PointF(this.mEndPoint.x, this.mEndPoint.y);
            return copy;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CubicElement implements PathElement {
        public PointF mEndControlPoint;
        public PointF mEndPoint;
        public PointF mStartControlPoint;

        private CubicElement() {
        }

        @Override // com.oddlyspaced.calci.utilities.path.TweenablePath.PathElement
        public void transformPoints(float[] matrixValues) {
            TweenablePath.this.transformPoint(this.mStartControlPoint, matrixValues);
            TweenablePath.this.transformPoint(this.mEndControlPoint, matrixValues);
            TweenablePath.this.transformPoint(this.mEndPoint, matrixValues);
        }

        @Override // com.oddlyspaced.calci.utilities.path.TweenablePath.PathElement
        public void setInterpolated(PathElement initial, PathElement target, float fraction) {
            CubicElement initialMove = (CubicElement) initial;
            CubicElement targetMove = (CubicElement) target;
            PointUtil.setInterpolated(this.mStartControlPoint, initialMove.mStartControlPoint, targetMove.mStartControlPoint, fraction);
            PointUtil.setInterpolated(this.mEndControlPoint, initialMove.mEndControlPoint, targetMove.mEndControlPoint, fraction);
            PointUtil.setInterpolated(this.mEndPoint, initialMove.mEndPoint, targetMove.mEndPoint, fraction);
        }

        @Override // com.oddlyspaced.calci.utilities.path.TweenablePath.PathElement
        public void addToPath(Path path) {
            path.cubicTo(this.mStartControlPoint.x, this.mStartControlPoint.y, this.mEndControlPoint.x, this.mEndControlPoint.y, this.mEndPoint.x, this.mEndPoint.y);
        }

        @Override // com.oddlyspaced.calci.utilities.path.TweenablePath.PathElement
        public CubicElement copy() {
            CubicElement copy = new CubicElement();
            copy.mStartControlPoint = new PointF(this.mStartControlPoint.x, this.mStartControlPoint.y);
            copy.mEndControlPoint = new PointF(this.mEndControlPoint.x, this.mEndControlPoint.y);
            copy.mEndPoint = new PointF(this.mEndPoint.x, this.mEndPoint.y);
            return copy;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CloseElement implements PathElement {
        private CloseElement() {
        }

        @Override // com.oddlyspaced.calci.utilities.path.TweenablePath.PathElement
        public void transformPoints(float[] matrixValues) {
        }

        @Override // com.oddlyspaced.calci.utilities.path.TweenablePath.PathElement
        public void setInterpolated(PathElement initial, PathElement target, float fraction) {
        }

        @Override // com.oddlyspaced.calci.utilities.path.TweenablePath.PathElement
        public void addToPath(Path path) {
            path.close();
        }

        @Override // com.oddlyspaced.calci.utilities.path.TweenablePath.PathElement
        public CloseElement copy() {
            return new CloseElement();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void transformPoint(PointF p, float[] m) {
        p.x = (m[0] * p.x) + (m[1] * p.y) + m[2];
        p.y = (m[3] * p.x) + (m[4] * p.y) + m[5];
    }

    public void set(TweenablePath path) {
        this.mPath.set(path.mPath);
        copyElements(path);
    }

    private void copyElements(TweenablePath path) {
        this.mElements.clear();
        Iterator<PathElement> it = path.mElements.iterator();
        while (it.hasNext()) {
            this.mElements.add(it.next().copy());
        }
    }
}
