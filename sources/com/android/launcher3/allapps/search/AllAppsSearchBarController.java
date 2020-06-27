package com.android.launcher3.allapps.search;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.android.launcher3.ExtendedEditText;
import com.android.launcher3.ExtendedEditText.OnBackKeyListener;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.discovery.AppDiscoveryItem;
import com.android.launcher3.discovery.AppDiscoveryUpdateState;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.PackageManagerHelper;
import java.util.ArrayList;

public class AllAppsSearchBarController implements TextWatcher, OnEditorActionListener, OnBackKeyListener {
    protected Callbacks mCb;
    protected ExtendedEditText mInput;
    protected InputMethodManager mInputMethodManager;
    protected Launcher mLauncher;
    protected String mQuery;
    protected SearchAlgorithm mSearchAlgorithm;

    public interface Callbacks {
        void clearSearchResult();

        void onAppDiscoverySearchUpdate(@Nullable AppDiscoveryItem appDiscoveryItem, @NonNull AppDiscoveryUpdateState appDiscoveryUpdateState);

        void onSearchResult(String str, ArrayList<ComponentKey> arrayList);
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void setVisibility(int i) {
        this.mInput.setVisibility(i);
    }

    public final void initialize(SearchAlgorithm searchAlgorithm, ExtendedEditText extendedEditText, Launcher launcher, Callbacks callbacks) {
        this.mCb = callbacks;
        this.mLauncher = launcher;
        this.mInput = extendedEditText;
        this.mInput.addTextChangedListener(this);
        this.mInput.setOnEditorActionListener(this);
        this.mInput.setOnBackKeyListener(this);
        this.mInputMethodManager = (InputMethodManager) this.mInput.getContext().getSystemService("input_method");
        this.mSearchAlgorithm = searchAlgorithm;
    }

    public void afterTextChanged(Editable editable) {
        this.mQuery = editable.toString();
        if (this.mQuery.isEmpty()) {
            this.mSearchAlgorithm.cancel(true);
            this.mCb.clearSearchResult();
            return;
        }
        this.mSearchAlgorithm.cancel(false);
        this.mSearchAlgorithm.doSearch(this.mQuery, this.mCb);
    }

    public void refreshSearchResult() {
        if (!TextUtils.isEmpty(this.mQuery)) {
            this.mSearchAlgorithm.cancel(false);
            this.mSearchAlgorithm.doSearch(this.mQuery, this.mCb);
        }
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 3) {
            return false;
        }
        String charSequence = textView.getText().toString();
        if (!charSequence.isEmpty()) {
            return this.mLauncher.startActivitySafely(textView, PackageManagerHelper.getMarketSearchIntent(this.mLauncher, charSequence), null);
        }
        ((InputMethodManager) this.mLauncher.getSystemService("input_method")).hideSoftInputFromWindow(textView.getWindowToken(), 0);
        return false;
    }

    public boolean onBackKey() {
        if (!Utilities.trim(this.mInput.getEditableText().toString()).isEmpty()) {
            return false;
        }
        reset();
        return true;
    }

    public void reset() {
        unfocusSearchField();
        this.mCb.clearSearchResult();
        this.mInput.setText("");
        this.mQuery = null;
        hideKeyboard();
    }

    /* access modifiers changed from: protected */
    public void hideKeyboard() {
        this.mInputMethodManager.hideSoftInputFromWindow(this.mInput.getWindowToken(), 0);
    }

    /* access modifiers changed from: protected */
    public void unfocusSearchField() {
        View focusSearch = this.mInput.focusSearch(130);
        if (focusSearch != null) {
            focusSearch.requestFocus();
        }
    }

    public void focusSearchField() {
        this.mInput.showKeyboard();
    }

    public boolean isSearchFieldFocused() {
        return this.mInput.isFocused();
    }
}
