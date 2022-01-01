package com.sparkappdesign.archimedes.mathtype.measures;

import android.graphics.PointF;
import android.graphics.RectF;
import com.sparkappdesign.archimedes.utilities.path.PathWrapper;
import java.util.Hashtable;
/* loaded from: classes.dex */
public class MTGlyphMeasures {
    private Hashtable<String, Object> mExtraMeasures = new Hashtable<>();
    private PathWrapper mPath = new PathWrapper();
    private RectF mBounds = new RectF();
    private PointF mPosition = new PointF();

    public PathWrapper getPath() {
        return this.mPath;
    }

    public void setPath(PathWrapper path) {
        if (this.mPath != path) {
            this.mPath = path;
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

    public PointF getPosition() {
        return this.mPosition;
    }

    public void setPosition(PointF position) {
        if (this.mPosition != position) {
            this.mPosition.set(position);
        }
    }

    public Hashtable<String, Object> getExtraMeasures() {
        return this.mExtraMeasures;
    }

    public MTGlyphMeasures copy() {
        MTGlyphMeasures copy = new MTGlyphMeasures();
        copy.mPath.set(this.mPath);
        copy.mBounds.set(this.mBounds);
        copy.mPosition.set(this.mPosition);
        return copy;
    }
}
