package com.sparkappdesign.archimedes.mathexpression.context;

import android.util.Log;
import com.sparkappdesign.archimedes.mathexpression.enums.MEContextOptions;
import com.sparkappdesign.archimedes.mathexpression.enums.MEExpressionForm;
import com.sparkappdesign.archimedes.mathexpression.enums.MEIssueType;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEUnit;
import com.sparkappdesign.archimedes.mathexpression.numbers.MEReal;
import com.sparkappdesign.archimedes.utilities.ListUtil;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes.dex */
public class MEContext {
    private static ThreadLocal<MEContext> threadLocalCurrentContext = new ThreadLocal<>();
    private static ThreadLocal<ArrayList<MEContext>> threadLocalStack = new ThreadLocal<>();
    private MEExpressionForm mForm;
    private ArrayList<MEIssue> mIssues = new ArrayList<>();
    private EnumSet<MEContextOptions> mOptions = EnumSet.noneOf(MEContextOptions.class);
    private MEReal mTau = new MEReal(1.0E-8d);
    private AtomicReference<ArrayList<MEContext>> mStack = new AtomicReference<>(new ArrayList());
    private AtomicBoolean mShouldStop = new AtomicBoolean();
    private HashMap<MEExpression, MEUnit> mDefaultUnits = new HashMap<>(MEUnit.getBaseUnits());

    public MEExpressionForm getForm() {
        return this.mForm;
    }

    public ArrayList<MEIssue> getIssues() {
        return this.mIssues;
    }

    public HashMap<MEExpression, MEUnit> getDefaultUnits() {
        return this.mDefaultUnits;
    }

    public EnumSet<MEContextOptions> getOptions() {
        return this.mOptions;
    }

    public MEReal getTau() {
        return this.mTau;
    }

    public void setForm(MEExpressionForm form) {
        this.mForm = form;
    }

    public void setDefaultUnits(HashMap<MEExpression, MEUnit> defaultUnits) {
        this.mDefaultUnits = defaultUnits;
    }

    public static MEContext getContextWithForm(MEExpressionForm form) {
        MEContext context = new MEContext();
        context.mForm = form;
        return context;
    }

    public static MEContext getCurrent() {
        MEContext context = threadLocalCurrentContext.get();
        if (context != null) {
            return context;
        }
        MEContext context2 = new MEContext();
        pushContext(context2);
        return context2;
    }

    public static void pushContext(MEContext context, Runnable runnable) {
        pushContext(context);
        runnable.run();
        popContext();
    }

    private static void pushContext(MEContext context) {
        if (context == null) {
            context = threadLocalCurrentContext.get() != null ? threadLocalCurrentContext.get().copy() : new MEContext();
        }
        ArrayList<MEContext> stack = threadLocalStack.get();
        if (stack == null) {
            stack = new ArrayList<>();
            threadLocalStack.set(stack);
        }
        MEContext oldContext = !stack.isEmpty() ? stack.get(stack.size() - 1) : null;
        synchronized (stack) {
            if (stack.contains(context)) {
                throw new IllegalArgumentException("context is already on stack");
            }
            stack.add(context);
            context.mStack.set(stack);
            if (oldContext != null && oldContext.mShouldStop.get()) {
                context.mShouldStop.set(true);
            }
        }
        threadLocalCurrentContext.set(context);
    }

    private static void popContext() {
        MEContext context;
        ArrayList<MEContext> stack = threadLocalStack.get();
        if (stack != null && !stack.isEmpty()) {
            synchronized (stack) {
                stack.get(stack.size() - 1).mStack.set(null);
                stack.remove(stack.size() - 1);
                if (!stack.isEmpty()) {
                    context = stack.get(stack.size() - 1);
                } else {
                    context = null;
                }
            }
            if (context != null) {
                threadLocalCurrentContext.set(context);
                return;
            }
            threadLocalCurrentContext.remove();
            threadLocalStack.remove();
        }
    }

    public static boolean shouldStop() {
        return getCurrent().mShouldStop.get();
    }

    public static boolean shouldStop(Object parameter) {
        return shouldStop() || parameter == null;
    }

    public void stop() {
        this.mShouldStop.set(true);
        ArrayList<MEContext> stack = this.mStack.get();
        if (stack != null) {
            synchronized (stack) {
                List<MEContext> nestedContexts = ListUtil.objectsAfterObject(stack, this);
                if (nestedContexts != null) {
                    for (MEContext context : nestedContexts) {
                        context.mShouldStop.set(true);
                    }
                }
            }
        }
    }

    public void stopWithError(String errorName) {
        Log.d("Archimedes", this.mForm + " calculation stopping with error: " + errorName);
        this.mIssues.clear();
        this.mIssues.add(new MEIssue(MEIssueType.Error, errorName));
        stop();
    }

    public MEContext copy() {
        MEContext copy = new MEContext();
        copy.mForm = this.mForm;
        copy.mIssues = new ArrayList<>(this.mIssues);
        copy.mDefaultUnits = new HashMap<>(this.mDefaultUnits);
        copy.mOptions = this.mOptions;
        copy.mTau = this.mTau;
        return copy;
    }
}
