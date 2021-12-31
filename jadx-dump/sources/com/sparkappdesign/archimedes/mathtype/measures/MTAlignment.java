package com.sparkappdesign.archimedes.mathtype.measures;

import android.graphics.PointF;
import android.graphics.RectF;
import com.sparkappdesign.archimedes.mathtype.enums.MTAlignmentType;
import com.sparkappdesign.archimedes.utilities.PointUtil;
import com.sparkappdesign.archimedes.utilities.RectUtil;
import java.util.EnumSet;
/* loaded from: classes.dex */
public class MTAlignment {
    private static PointF alignPointToRect(PointF point, RectF rect, EnumSet<MTAlignmentType> alignment) {
        PointF p = new PointF(point.x, point.y);
        if (alignment.contains(MTAlignmentType.MinX)) {
            p.x = rect.left;
        } else if (alignment.contains(MTAlignmentType.CenterX)) {
            p.x = rect.centerX();
        } else if (alignment.contains(MTAlignmentType.MaxX)) {
            p.x = rect.right;
        }
        if (alignment.contains(MTAlignmentType.MinY)) {
            p.y = rect.top;
        } else if (alignment.contains(MTAlignmentType.CenterY)) {
            p.y = rect.centerY();
        } else if (alignment.contains(MTAlignmentType.MaxY)) {
            p.y = rect.bottom;
        }
        return p;
    }

    public static RectF alignRectToRect(RectF rect1, RectF rect2, EnumSet<MTAlignmentType> alignment) {
        return alignRectToPoint(rect1, alignPointToRect(RectUtil.getOrigin(rect1), rect2, alignment), alignment);
    }

    public static RectF alignRectToPoint(RectF rect, PointF p, EnumSet<MTAlignmentType> alignment) {
        return RectUtil.translate(rect, PointUtil.subtractPoints(p, alignPointToRect(p, rect, alignment)));
    }

    public static PointF offsetToAlignRectToRect(RectF rect1, RectF rect2, EnumSet<MTAlignmentType> alignment) {
        return PointUtil.subtractPoints(RectUtil.getOrigin(alignRectToRect(rect1, rect2, alignment)), RectUtil.getOrigin(rect1));
    }

    public static PointF offsetToAlignRectToPoint(RectF rect, PointF p, EnumSet<MTAlignmentType> alignment) {
        return PointUtil.subtractPoints(RectUtil.getOrigin(alignRectToPoint(rect, p, alignment)), RectUtil.getOrigin(rect));
    }
}
