package com.oddlyspaced.calci.mathexpression.context;

import com.oddlyspaced.calci.mathexpression.enums.MEIssueType;
import java.util.HashMap;
/* loaded from: classes.dex */
public class MEIssue {
    public static final String CANT_SOLVE_GENERIC = "ME_ISSUE_CANT_SOLVE_GENERIC";
    public static final String CONTRADICTION = "ME_ISSUE_CONTRADICTION";
    public static final String DIVISION_BY_ZERO = "ME_ISSUE_DIVISION_BY_ZERO";
    public static final String GENERIC = "ME_ISSUE_GENERIC";
    public static final String IMPROPER_INTEGRAL = "ME_ISSUE_IMPROPER_INTEGRAL";
    public static final String INVALID_UNIT = "ME_ISSUE_INVALID_UNIT";
    public static final String LOGARITHMIC_DOMAIN = "ME_ISSUE_LOGARITHMIC_DOMAIN";
    public static final String NEGATIVE_ROOT = "ME_ISSUE_NEGATIVE_ROOT";
    public static final String NESTED_EQUALS = "ME_ISSUE_NESTED_EQUALS";
    public static final String POLYNOMIAL_ORDER_TOO_HIGH = "ME_ISSUE_POLYNOMIAL_ORDER_TOO_HIGH";
    public static final String SLOPE_SAMPLE_UNDEFINED = "ME_ISSUE_SLOPE_SAMPLE_UNDEFINED";
    public static final String TRIGONOMETRIC_DOMAIN = "ME_ISSUE_TRIGONOMETRIC_DOMAIN";
    public static final String TRUE = "ME_ISSUE_TRUE";
    public static final String UNDEFINED = "ME_ISSUE_UNDEFINED";
    public static final String VALUES_TOO_LARGE = "ME_ISSUE_VALUES_TOO_LARGE";
    private HashMap<String, String> mExtraInfo;
    private String mName;
    private MEIssueType mType;

    public String getName() {
        return this.mName;
    }

    public MEIssue(MEIssueType type, String name, HashMap<String, String> extraInfo) {
        this.mExtraInfo = new HashMap<>();
        this.mType = type;
        this.mName = name;
        if (extraInfo != null) {
            this.mExtraInfo.putAll(extraInfo);
        }
    }

    public MEIssue() {
        this(MEIssueType.Error, null, null);
    }

    public MEIssue(MEIssueType type, String name) {
        this(type, name, null);
    }

    public String toString() {
        return this.mType.toString() + ": " + this.mName;
    }

    public MEIssue copy() {
        return new MEIssue(this.mType, this.mName, this.mExtraInfo);
    }

    public boolean equivalentTo(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MEIssue)) {
            return false;
        }
        MEIssue otherIssue = (MEIssue) other;
        return this.mType == otherIssue.mType && this.mName != null && this.mName.equals(otherIssue.mName) && this.mExtraInfo != null && this.mExtraInfo.equals(otherIssue.mExtraInfo);
    }
}
