package com.sparkappdesign.archimedes.mathtype.enums;
/* loaded from: classes.dex */
public enum MTStringInputBehavior {
    None,
    IfEmptyGrabSelectedRange,
    IfEmptyGrabRangeLeftOfCursor,
    IfEmptyGrabRangeRightOfCursor,
    IfNotFilledGrabCursor,
    KeepContentsWhenBackspacingParent,
    SelectContentsWhenBackspacingParent,
    SelectContentsWhenBackspacingParentUnlessEqualToImplicitGrab,
    GrabCursorWhenBackspacingParentInsteadOfDelete,
    SelectParentWhenBackspacingAtIndex0,
    DeleteParentWhenBackspacingAtIndex0,
    DeleteParentWhenLeftEmpty
}
