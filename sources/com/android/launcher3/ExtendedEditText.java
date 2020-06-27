package com.android.launcher3;

import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class ExtendedEditText extends EditText {
    private OnBackKeyListener mBackKeyListener;
    private boolean mForceDisableSuggestions = false;
    /* access modifiers changed from: private */
    public boolean mShowImeAfterFirstLayout;

    public interface OnBackKeyListener {
        boolean onBackKey();
    }

    public boolean onDragEvent(DragEvent dragEvent) {
        return false;
    }

    public ExtendedEditText(Context context) {
        super(context);
    }

    public ExtendedEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ExtendedEditText(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setOnBackKeyListener(OnBackKeyListener onBackKeyListener) {
        this.mBackKeyListener = onBackKeyListener;
    }

    public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
        if (i != 4 || keyEvent.getAction() != 1) {
            return super.onKeyPreIme(i, keyEvent);
        }
        if (this.mBackKeyListener != null) {
            return this.mBackKeyListener.onBackKey();
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.mShowImeAfterFirstLayout) {
            post(new Runnable() {
                public void run() {
                    ExtendedEditText.this.showSoftInput();
                    ExtendedEditText.this.mShowImeAfterFirstLayout = false;
                }
            });
        }
    }

    public void showKeyboard() {
        this.mShowImeAfterFirstLayout = !showSoftInput();
    }

    /* access modifiers changed from: private */
    public boolean showSoftInput() {
        if (!requestFocus() || !((InputMethodManager) getContext().getSystemService("input_method")).showSoftInput(this, 1)) {
            return false;
        }
        return true;
    }

    public void dispatchBackKey() {
        ((InputMethodManager) getContext().getSystemService("input_method")).hideSoftInputFromWindow(getWindowToken(), 0);
        if (this.mBackKeyListener != null) {
            this.mBackKeyListener.onBackKey();
        }
    }

    public void forceDisableSuggestions(boolean z) {
        this.mForceDisableSuggestions = z;
    }

    public boolean isSuggestionsEnabled() {
        return !this.mForceDisableSuggestions && super.isSuggestionsEnabled();
    }
}
