package com.sparkappdesign.archimedes.mathexpression.enums;
/* loaded from: classes.dex */
public enum MEIssueType {
    Error,
    Warning,
    Info;

    @Override // java.lang.Enum, java.lang.Object
    public String toString() {
        switch (this) {
            case Error:
                return "Error";
            case Warning:
                return "Warning";
            case Info:
                return "Info";
            default:
                return "Unknown issue";
        }
    }
}
