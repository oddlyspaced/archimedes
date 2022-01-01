package com.oddlyspaced.calci.archimedes.views;

import android.graphics.RectF;
import com.oddlyspaced.calci.archimedes.enums.ARScrollPriority;
import java.util.ArrayList;
/* loaded from: classes.dex */
public interface ARAutoScrollViewItem {
    void addedToAutoScrollView(ARAutoScrollView aRAutoScrollView);

    ArrayList<RectF> areasOfInterestWithIdentifier(ARAutoScrollView aRAutoScrollView, String str);

    RectF frameInAutoScrollView(ARAutoScrollView aRAutoScrollView);

    ARScrollPriority priorityForScrollToAreasOfInterest(ARAutoScrollView aRAutoScrollView, String str);

    void removedFromAutoScrollView(ARAutoScrollView aRAutoScrollView);
}
