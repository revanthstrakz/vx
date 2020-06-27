package com.lody.virtual.client.stub;

import android.accounts.Account;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorDescription;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.android.launcher3.LauncherSettings.BaseLauncherColumns;
import com.lody.virtual.C0966R;
import com.lody.virtual.client.ipc.VAccountManager;
import com.lody.virtual.helper.utils.VLog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ChooseTypeAndAccountActivity extends Activity implements AccountManagerCallback<Bundle> {
    public static final String EXTRA_ADD_ACCOUNT_AUTH_TOKEN_TYPE_STRING = "authTokenType";
    public static final String EXTRA_ADD_ACCOUNT_OPTIONS_BUNDLE = "addAccountOptions";
    public static final String EXTRA_ADD_ACCOUNT_REQUIRED_FEATURES_STRING_ARRAY = "addAccountRequiredFeatures";
    public static final String EXTRA_ALLOWABLE_ACCOUNTS_ARRAYLIST = "allowableAccounts";
    public static final String EXTRA_ALLOWABLE_ACCOUNT_TYPES_STRING_ARRAY = "allowableAccountTypes";
    @Deprecated
    public static final String EXTRA_ALWAYS_PROMPT_FOR_ACCOUNT = "alwaysPromptForAccount";
    public static final String EXTRA_DESCRIPTION_TEXT_OVERRIDE = "descriptionTextOverride";
    public static final String EXTRA_SELECTED_ACCOUNT = "selectedAccount";
    private static final String KEY_INSTANCE_STATE_ACCOUNT_LIST = "accountList";
    private static final String KEY_INSTANCE_STATE_EXISTING_ACCOUNTS = "existingAccounts";
    private static final String KEY_INSTANCE_STATE_PENDING_REQUEST = "pendingRequest";
    private static final String KEY_INSTANCE_STATE_SELECTED_ACCOUNT_NAME = "selectedAccountName";
    private static final String KEY_INSTANCE_STATE_SELECTED_ADD_ACCOUNT = "selectedAddAccount";
    public static final String KEY_USER_ID = "userId";
    public static final int REQUEST_ADD_ACCOUNT = 2;
    public static final int REQUEST_CHOOSE_TYPE = 1;
    public static final int REQUEST_NULL = 0;
    private static final int SELECTED_ITEM_NONE = -1;
    private static final String TAG = "AccountChooser";
    private ArrayList<Account> mAccounts;
    private int mCallingUserId;
    private String mDescriptionOverride;
    private boolean mDontShowPicker;
    private Parcelable[] mExistingAccounts = null;
    /* access modifiers changed from: private */
    public Button mOkButton;
    private int mPendingRequest = 0;
    private String mSelectedAccountName = null;
    private boolean mSelectedAddNewAccount = false;
    /* access modifiers changed from: private */
    public int mSelectedItemIndex;
    private Set<Account> mSetOfAllowableAccounts;
    private Set<String> mSetOfRelevantAccountTypes;

    public void onCreate(Bundle bundle) {
        Intent intent = getIntent();
        boolean z = false;
        if (bundle != null) {
            this.mPendingRequest = bundle.getInt(KEY_INSTANCE_STATE_PENDING_REQUEST);
            this.mExistingAccounts = bundle.getParcelableArray(KEY_INSTANCE_STATE_EXISTING_ACCOUNTS);
            this.mSelectedAccountName = bundle.getString(KEY_INSTANCE_STATE_SELECTED_ACCOUNT_NAME);
            this.mSelectedAddNewAccount = bundle.getBoolean(KEY_INSTANCE_STATE_SELECTED_ADD_ACCOUNT, false);
            this.mAccounts = bundle.getParcelableArrayList(KEY_INSTANCE_STATE_ACCOUNT_LIST);
            this.mCallingUserId = bundle.getInt(KEY_USER_ID);
        } else {
            this.mPendingRequest = 0;
            this.mExistingAccounts = null;
            this.mCallingUserId = intent.getIntExtra(KEY_USER_ID, -1);
            Account account = (Account) intent.getParcelableExtra(EXTRA_SELECTED_ACCOUNT);
            if (account != null) {
                this.mSelectedAccountName = account.name;
            }
        }
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("selected account name is ");
        sb.append(this.mSelectedAccountName);
        VLog.m90v(str, sb.toString(), new Object[0]);
        this.mSetOfAllowableAccounts = getAllowableAccountSet(intent);
        this.mSetOfRelevantAccountTypes = getReleventAccountTypes(intent);
        this.mDescriptionOverride = intent.getStringExtra(EXTRA_DESCRIPTION_TEXT_OVERRIDE);
        this.mAccounts = getAcceptableAccountChoices(VAccountManager.get());
        if (this.mDontShowPicker) {
            super.onCreate(bundle);
            return;
        }
        if (this.mPendingRequest == 0 && this.mAccounts.isEmpty()) {
            setNonLabelThemeAndCallSuperCreate(bundle);
            if (this.mSetOfRelevantAccountTypes.size() == 1) {
                runAddAccountForAuthenticator((String) this.mSetOfRelevantAccountTypes.iterator().next());
            } else {
                startChooseAccountTypeActivity();
            }
        }
        String[] listOfDisplayableOptions = getListOfDisplayableOptions(this.mAccounts);
        this.mSelectedItemIndex = getItemIndexToSelect(this.mAccounts, this.mSelectedAccountName, this.mSelectedAddNewAccount);
        super.onCreate(bundle);
        setContentView(C0966R.layout.choose_type_and_account);
        overrideDescriptionIfSupplied(this.mDescriptionOverride);
        populateUIAccountList(listOfDisplayableOptions);
        this.mOkButton = (Button) findViewById(16908314);
        Button button = this.mOkButton;
        if (this.mSelectedItemIndex != -1) {
            z = true;
        }
        button.setEnabled(z);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "ChooseTypeAndAccountActivity.onDestroy()");
        }
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(KEY_INSTANCE_STATE_PENDING_REQUEST, this.mPendingRequest);
        if (this.mPendingRequest == 2) {
            bundle.putParcelableArray(KEY_INSTANCE_STATE_EXISTING_ACCOUNTS, this.mExistingAccounts);
        }
        if (this.mSelectedItemIndex != -1) {
            if (this.mSelectedItemIndex == this.mAccounts.size()) {
                bundle.putBoolean(KEY_INSTANCE_STATE_SELECTED_ADD_ACCOUNT, true);
            } else {
                bundle.putBoolean(KEY_INSTANCE_STATE_SELECTED_ADD_ACCOUNT, false);
                bundle.putString(KEY_INSTANCE_STATE_SELECTED_ACCOUNT_NAME, ((Account) this.mAccounts.get(this.mSelectedItemIndex)).name);
            }
        }
        bundle.putParcelableArrayList(KEY_INSTANCE_STATE_ACCOUNT_LIST, this.mAccounts);
    }

    public void onCancelButtonClicked(View view) {
        onBackPressed();
    }

    public void onOkButtonClicked(View view) {
        if (this.mSelectedItemIndex == this.mAccounts.size()) {
            startChooseAccountTypeActivity();
        } else if (this.mSelectedItemIndex != -1) {
            onAccountSelected((Account) this.mAccounts.get(this.mSelectedItemIndex));
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        String str;
        String str2;
        if (Log.isLoggable(TAG, 2)) {
            if (!(intent == null || intent.getExtras() == null)) {
                intent.getExtras().keySet();
            }
            Object extras = intent != null ? intent.getExtras() : null;
            String str3 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("ChooseTypeAndAccountActivity.onActivityResult(reqCode=");
            sb.append(i);
            sb.append(", resCode=");
            sb.append(i2);
            sb.append(", extras=");
            sb.append(extras);
            sb.append(")");
            Log.v(str3, sb.toString());
        }
        this.mPendingRequest = 0;
        if (i2 == 0) {
            if (this.mAccounts.isEmpty()) {
                setResult(0);
                finish();
            }
            return;
        }
        if (i2 == -1) {
            if (i == 1) {
                if (intent != null) {
                    String stringExtra = intent.getStringExtra("accountType");
                    if (stringExtra != null) {
                        runAddAccountForAuthenticator(stringExtra);
                        return;
                    }
                }
                Log.d(TAG, "ChooseTypeAndAccountActivity.onActivityResult: unable to find account type, pretending the request was canceled");
            } else if (i == 2) {
                if (intent != null) {
                    str2 = intent.getStringExtra("authAccount");
                    str = intent.getStringExtra("accountType");
                } else {
                    str2 = null;
                    str = null;
                }
                if (str2 == null || str == null) {
                    Account[] accounts = VAccountManager.get().getAccounts(this.mCallingUserId, null);
                    HashSet hashSet = new HashSet();
                    for (Parcelable parcelable : this.mExistingAccounts) {
                        hashSet.add((Account) parcelable);
                    }
                    int length = accounts.length;
                    int i3 = 0;
                    while (true) {
                        if (i3 >= length) {
                            break;
                        }
                        Account account = accounts[i3];
                        if (!hashSet.contains(account)) {
                            str2 = account.name;
                            str = account.type;
                            break;
                        }
                        i3++;
                    }
                }
                if (!(str2 == null && str == null)) {
                    setResultAndFinish(str2, str);
                    return;
                }
            }
            Log.d(TAG, "ChooseTypeAndAccountActivity.onActivityResult: unable to find added account, pretending the request was canceled");
        }
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "ChooseTypeAndAccountActivity.onActivityResult: canceled");
        }
        setResult(0);
        finish();
    }

    /* access modifiers changed from: protected */
    public void runAddAccountForAuthenticator(String str) {
        if (Log.isLoggable(TAG, 2)) {
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("runAddAccountForAuthenticator: ");
            sb.append(str);
            Log.v(str2, sb.toString());
        }
        Bundle bundleExtra = getIntent().getBundleExtra(EXTRA_ADD_ACCOUNT_OPTIONS_BUNDLE);
        String[] stringArrayExtra = getIntent().getStringArrayExtra(EXTRA_ADD_ACCOUNT_REQUIRED_FEATURES_STRING_ARRAY);
        VAccountManager.get().addAccount(this.mCallingUserId, str, getIntent().getStringExtra(EXTRA_ADD_ACCOUNT_AUTH_TOKEN_TYPE_STRING), stringArrayExtra, bundleExtra, null, this, null);
    }

    public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
        try {
            Intent intent = (Intent) ((Bundle) accountManagerFuture.getResult()).getParcelable(BaseLauncherColumns.INTENT);
            if (intent != null) {
                this.mPendingRequest = 2;
                this.mExistingAccounts = VAccountManager.get().getAccounts(this.mCallingUserId, null);
                intent.setFlags(intent.getFlags() & -268435457);
                startActivityForResult(intent, 2);
                return;
            }
        } catch (OperationCanceledException unused) {
            setResult(0);
            finish();
            return;
        } catch (AuthenticatorException | IOException unused2) {
        }
        Bundle bundle = new Bundle();
        bundle.putString("errorMessage", "error communicating with server");
        setResult(-1, new Intent().putExtras(bundle));
        finish();
    }

    private void setNonLabelThemeAndCallSuperCreate(Bundle bundle) {
        if (VERSION.SDK_INT >= 21) {
            setTheme(16974396);
        } else {
            setTheme(16973941);
        }
        super.onCreate(bundle);
    }

    private void onAccountSelected(Account account) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("selected account ");
        sb.append(account);
        Log.d(str, sb.toString());
        setResultAndFinish(account.name, account.type);
    }

    private void setResultAndFinish(String str, String str2) {
        Bundle bundle = new Bundle();
        bundle.putString("authAccount", str);
        bundle.putString("accountType", str2);
        setResult(-1, new Intent().putExtras(bundle));
        String str3 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("ChooseTypeAndAccountActivity.setResultAndFinish: selected account ");
        sb.append(str);
        sb.append(", ");
        sb.append(str2);
        VLog.m90v(str3, sb.toString(), new Object[0]);
        finish();
    }

    private void startChooseAccountTypeActivity() {
        VLog.m90v(TAG, "ChooseAccountTypeActivity.startChooseAccountTypeActivity()", new Object[0]);
        Intent intent = new Intent(this, ChooseAccountTypeActivity.class);
        intent.setFlags(524288);
        intent.putExtra(EXTRA_ALLOWABLE_ACCOUNT_TYPES_STRING_ARRAY, getIntent().getStringArrayExtra(EXTRA_ALLOWABLE_ACCOUNT_TYPES_STRING_ARRAY));
        intent.putExtra(EXTRA_ADD_ACCOUNT_OPTIONS_BUNDLE, getIntent().getBundleExtra(EXTRA_ADD_ACCOUNT_OPTIONS_BUNDLE));
        intent.putExtra(EXTRA_ADD_ACCOUNT_REQUIRED_FEATURES_STRING_ARRAY, getIntent().getStringArrayExtra(EXTRA_ADD_ACCOUNT_REQUIRED_FEATURES_STRING_ARRAY));
        intent.putExtra(EXTRA_ADD_ACCOUNT_AUTH_TOKEN_TYPE_STRING, getIntent().getStringExtra(EXTRA_ADD_ACCOUNT_AUTH_TOKEN_TYPE_STRING));
        startActivityForResult(intent, 1);
        this.mPendingRequest = 1;
    }

    private int getItemIndexToSelect(ArrayList<Account> arrayList, String str, boolean z) {
        if (z) {
            return arrayList.size();
        }
        for (int i = 0; i < arrayList.size(); i++) {
            if (((Account) arrayList.get(i)).name.equals(str)) {
                return i;
            }
        }
        return -1;
    }

    private String[] getListOfDisplayableOptions(ArrayList<Account> arrayList) {
        String[] strArr = new String[(arrayList.size() + 1)];
        for (int i = 0; i < arrayList.size(); i++) {
            strArr[i] = ((Account) arrayList.get(i)).name;
        }
        strArr[arrayList.size()] = getResources().getString(C0966R.string.add_account_button_label);
        return strArr;
    }

    private ArrayList<Account> getAcceptableAccountChoices(VAccountManager vAccountManager) {
        Account[] accounts = vAccountManager.getAccounts(this.mCallingUserId, null);
        ArrayList<Account> arrayList = new ArrayList<>(accounts.length);
        for (Account account : accounts) {
            if ((this.mSetOfAllowableAccounts == null || this.mSetOfAllowableAccounts.contains(account)) && (this.mSetOfRelevantAccountTypes == null || this.mSetOfRelevantAccountTypes.contains(account.type))) {
                arrayList.add(account);
            }
        }
        return arrayList;
    }

    private Set<String> getReleventAccountTypes(Intent intent) {
        String[] stringArrayExtra = intent.getStringArrayExtra(EXTRA_ALLOWABLE_ACCOUNT_TYPES_STRING_ARRAY);
        AuthenticatorDescription[] authenticatorTypes = VAccountManager.get().getAuthenticatorTypes();
        HashSet hashSet = new HashSet(authenticatorTypes.length);
        for (AuthenticatorDescription authenticatorDescription : authenticatorTypes) {
            hashSet.add(authenticatorDescription.type);
        }
        if (stringArrayExtra == null) {
            return hashSet;
        }
        HashSet hashSet2 = new HashSet();
        Collections.addAll(hashSet2, stringArrayExtra);
        hashSet2.retainAll(hashSet);
        return hashSet2;
    }

    private Set<Account> getAllowableAccountSet(Intent intent) {
        ArrayList parcelableArrayListExtra = intent.getParcelableArrayListExtra(EXTRA_ALLOWABLE_ACCOUNTS_ARRAYLIST);
        if (parcelableArrayListExtra == null) {
            return null;
        }
        HashSet hashSet = new HashSet(parcelableArrayListExtra.size());
        Iterator it = parcelableArrayListExtra.iterator();
        while (it.hasNext()) {
            hashSet.add((Account) ((Parcelable) it.next()));
        }
        return hashSet;
    }

    private void overrideDescriptionIfSupplied(String str) {
        TextView textView = (TextView) findViewById(C0966R.C0967id.description);
        if (!TextUtils.isEmpty(str)) {
            textView.setText(str);
        } else {
            textView.setVisibility(8);
        }
    }

    private void populateUIAccountList(String[] strArr) {
        ListView listView = (ListView) findViewById(16908298);
        listView.setAdapter(new ArrayAdapter(this, 17367055, strArr));
        listView.setChoiceMode(1);
        listView.setItemsCanFocus(false);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ChooseTypeAndAccountActivity.this.mSelectedItemIndex = i;
                ChooseTypeAndAccountActivity.this.mOkButton.setEnabled(true);
            }
        });
        if (this.mSelectedItemIndex != -1) {
            listView.setItemChecked(this.mSelectedItemIndex, true);
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("List item ");
            sb.append(this.mSelectedItemIndex);
            sb.append(" should be selected");
            VLog.m90v(str, sb.toString(), new Object[0]);
        }
    }
}
