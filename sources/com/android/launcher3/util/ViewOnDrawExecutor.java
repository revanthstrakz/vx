package com.android.launcher3.util;

import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewTreeObserver.OnDrawListener;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executor;

public class ViewOnDrawExecutor implements Executor, OnDrawListener, Runnable, OnAttachStateChangeListener {
    private View mAttachedView;
    private boolean mCompleted;
    private final Executor mExecutor;
    private boolean mFirstDrawCompleted;
    private boolean mIsExecuting;
    private Launcher mLauncher;
    private boolean mLoadAnimationCompleted;
    private final ArrayList<Runnable> mTasks = new ArrayList<>();

    public void onViewDetachedFromWindow(View view) {
    }

    public ViewOnDrawExecutor(Executor executor) {
        this.mExecutor = executor;
    }

    public void attachTo(Launcher launcher) {
        this.mLauncher = launcher;
        this.mAttachedView = launcher.getWorkspace();
        this.mAttachedView.addOnAttachStateChangeListener(this);
        attachObserver();
    }

    private void attachObserver() {
        if (!this.mCompleted) {
            this.mAttachedView.getViewTreeObserver().addOnDrawListener(this);
        }
    }

    public void execute(Runnable runnable) {
        this.mTasks.add(runnable);
        LauncherModel.setWorkerPriority(10);
    }

    public void onViewAttachedToWindow(View view) {
        attachObserver();
    }

    public void onDraw() {
        this.mFirstDrawCompleted = true;
        this.mAttachedView.post(this);
    }

    public boolean canQueue() {
        return !this.mIsExecuting && !this.mCompleted;
    }

    public void onLoadAnimationCompleted() {
        this.mLoadAnimationCompleted = true;
        if (this.mAttachedView != null) {
            this.mAttachedView.post(this);
        }
    }

    public void run() {
        if (this.mLoadAnimationCompleted && this.mFirstDrawCompleted && !this.mCompleted) {
            this.mIsExecuting = true;
            Iterator it = this.mTasks.iterator();
            while (it.hasNext()) {
                this.mExecutor.execute((Runnable) it.next());
            }
            markCompleted();
        }
    }

    public void markCompleted() {
        this.mTasks.clear();
        this.mCompleted = true;
        this.mIsExecuting = false;
        if (this.mAttachedView != null) {
            this.mAttachedView.getViewTreeObserver().removeOnDrawListener(this);
            this.mAttachedView.removeOnAttachStateChangeListener(this);
        }
        if (this.mLauncher != null) {
            this.mLauncher.clearPendingExecutor(this);
        }
        LauncherModel.setWorkerPriority(0);
    }
}
