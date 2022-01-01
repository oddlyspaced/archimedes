package com.oddlyspaced.calci.utilities.path;

import android.graphics.Path;
/* loaded from: classes.dex */
public class PathWrapper {
    protected Path mPath = new Path();

    public Path getPath() {
        return this.mPath;
    }

    public PathWrapper() {
    }

    public PathWrapper(PathWrapper pathWrapper) {
        set(pathWrapper);
    }

    public void set(PathWrapper path) {
        this.mPath.set(path.mPath);
    }

    public void offset(float dx, float dy) {
        this.mPath.offset(dx, dy);
    }
}
