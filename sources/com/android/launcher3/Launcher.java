package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.AlertDialog.Builder;
import android.app.SearchManager;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.os.StrictMode;
import android.os.StrictMode.VmPolicy;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.KeyboardShortcutInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnDrawListener;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.android.launcher3.CellLayout.CellInfo;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.LauncherModel.Callbacks;
import com.android.launcher3.LauncherSettings.Favorites;
import com.android.launcher3.Workspace.ItemOperator;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate;
import com.android.launcher3.allapps.AllAppsContainerView;
import com.android.launcher3.allapps.AllAppsTransitionController;
import com.android.launcher3.anim.AnimationLayerSet;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.compat.LauncherAppsCompatVO;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.dragndrop.PinItemDragListener;
import com.android.launcher3.dynamicui.ExtractedColors;
import com.android.launcher3.dynamicui.WallpaperColorInfo;
import com.android.launcher3.dynamicui.WallpaperColorInfo.OnThemeChangeListener;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.keyboard.CustomActionsPopup;
import com.android.launcher3.keyboard.ViewGroupFocusHelper;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.logging.UserEventDispatcher;
import com.android.launcher3.model.ModelWriter;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.pageindicators.PageIndicator;
import com.android.launcher3.pageindicators.PageIndicatorCaretLandscape;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.popup.PopupDataProvider;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.util.ActivityResultInfo;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.MultiHashMap;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.PendingRequestArgs;
import com.android.launcher3.util.RunnableWithId;
import com.android.launcher3.util.Themes;
import com.android.launcher3.util.ViewOnDrawExecutor;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.android.launcher3.widget.WidgetAddFlowHandler;
import com.android.launcher3.widget.WidgetHostViewLoader;
import com.android.launcher3.widget.WidgetsContainerView;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

public class Launcher extends BaseActivity implements LauncherExterns, OnClickListener, OnLongClickListener, Callbacks, OnTouchListener, LauncherProviderChangeListener, AccessibilityStateChangeListener, OnThemeChangeListener {
    static final String APPS_VIEW_SHOWN = "launcher.apps_view_shown";
    private static final float BOUNCE_ANIMATION_TENSION = 1.3f;
    static final boolean DEBUG_RESUME_TIME = false;
    static final boolean DEBUG_STRICT_MODE = false;
    static final boolean DEBUG_WIDGETS = false;
    public static final int EXIT_SPRINGLOADED_MODE_SHORT_TIMEOUT = 500;
    static final String INTENT_EXTRA_IGNORE_LAUNCH_ANIMATION = "com.android.launcher3.intent.extra.shortcut.INGORE_LAUNCH_ANIMATION";
    static final boolean LOGD = false;
    static final int NEW_APPS_ANIMATION_DELAY = 500;
    private static final int NEW_APPS_ANIMATION_INACTIVE_TIMEOUT_SECONDS = 5;
    private static final int NEW_APPS_PAGE_MOVE_DELAY = 500;
    private static final int ON_ACTIVITY_RESULT_ANIMATION_DELAY = 500;
    private static final int REQUEST_BIND_APPWIDGET = 11;
    private static final int REQUEST_BIND_PENDING_APPWIDGET = 12;
    private static final int REQUEST_CREATE_APPWIDGET = 5;
    private static final int REQUEST_CREATE_SHORTCUT = 1;
    protected static final int REQUEST_LAST = 100;
    private static final int REQUEST_PERMISSION_CALL_PHONE = 14;
    private static final int REQUEST_PICK_APPWIDGET = 9;
    private static final int REQUEST_PICK_WALLPAPER = 10;
    private static final int REQUEST_RECONFIGURE_APPWIDGET = 13;
    private static final int RESTORE_SCREEN_ORIENTATION_DELAY = 500;
    private static final String RUNTIME_STATE = "launcher.state";
    private static final String RUNTIME_STATE_CURRENT_SCREEN = "launcher.current_screen";
    private static final String RUNTIME_STATE_PENDING_ACTIVITY_RESULT = "launcher.activity_result";
    private static final String RUNTIME_STATE_PENDING_REQUEST_ARGS = "launcher.request_args";
    private static final int SOFT_INPUT_MODE_ALL_APPS = 16;
    private static final int SOFT_INPUT_MODE_DEFAULT = 32;
    public static final String TAG = "Launcher";
    protected static final HashMap<String, CustomAppWidget> sCustomAppWidgets = new HashMap<>();
    private LauncherAccessibilityDelegate mAccessibilityDelegate;
    private View mAllAppsButton;
    AllAppsTransitionController mAllAppsController;
    private LauncherAppWidgetHost mAppWidgetHost;
    private AppWidgetManagerCompat mAppWidgetManager;
    AllAppsContainerView mAppsView;
    private final ArrayList<Runnable> mBindOnResumeCallbacks = new ArrayList<>();
    final Runnable mBuildLayersRunnable = new Runnable() {
        public void run() {
            if (Launcher.this.mWorkspace != null) {
                Launcher.this.mWorkspace.buildPageHardwareLayers();
            }
        }
    };
    private SpannableStringBuilder mDefaultKeySsb = null;
    private DragController mDragController;
    DragLayer mDragLayer;
    private DropTargetBar mDropTargetBar;
    /* access modifiers changed from: private */
    public Runnable mExitSpringLoadedModeRunnable;
    private final ExtractedColors mExtractedColors = new ExtractedColors();
    public ViewGroupFocusHelper mFocusHandler;
    private final Handler mHandler = new Handler();
    private boolean mHasFocus = false;
    Hotseat mHotseat;
    private IconCache mIconCache;
    private boolean mIsSafeModeEnabled;
    private float mLastDispatchTouchEventX = 0.0f;
    private LauncherCallbacks mLauncherCallbacks;
    private View mLauncherView;
    private LauncherModel mModel;
    private ModelWriter mModelWriter;
    private boolean mMoveToDefaultScreenFromNewIntent;
    private final ArrayList<Runnable> mOnResumeCallbacks = new ArrayList<>();
    private boolean mOnResumeNeedsLoad;
    private State mOnResumeState = State.NONE;
    private int mOrientation;
    /* access modifiers changed from: private */
    public ViewGroup mOverviewPanel;
    private boolean mPaused = true;
    private ActivityResultInfo mPendingActivityResult;
    private ViewOnDrawExecutor mPendingExecutor;
    /* access modifiers changed from: private */
    public PendingRequestArgs mPendingRequestArgs;
    private PopupDataProvider mPopupDataProvider;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.SCREEN_OFF".equals(action)) {
                Launcher.this.mDragLayer.clearResizeFrame();
                if (Launcher.this.mAppsView != null && Launcher.this.mWidgetsView != null && Launcher.this.mPendingRequestArgs == null && !Launcher.this.showWorkspace(false)) {
                    Launcher.this.mAppsView.reset();
                }
                Launcher.this.mShouldFadeInScrim = true;
            } else if ("android.intent.action.USER_PRESENT".equals(action)) {
                Launcher.this.mShouldFadeInScrim = false;
            }
        }
    };
    private boolean mRotationEnabled = false;
    private RotationPrefChangeHandler mRotationPrefChangeHandler;
    /* access modifiers changed from: private */
    public ObjectAnimator mScrimAnimator;
    private SharedPreferences mSharedPrefs;
    /* access modifiers changed from: private */
    public boolean mShouldFadeInScrim;
    State mState = State.WORKSPACE;
    LauncherStateTransitionAnimation mStateTransitionAnimation;
    private final ArrayList<Integer> mSynchronouslyBoundPages = new ArrayList<>();
    private final int[] mTmpAddItemCellCoordinates = new int[2];
    private BubbleTextView mWaitingForResume;
    public View mWeightWatcher;
    private View mWidgetsButton;
    WidgetsContainerView mWidgetsView;
    Workspace mWorkspace;
    boolean mWorkspaceLoading = true;

    public interface CustomContentCallbacks {
        boolean isScrollingAllowed();

        void onHide();

        void onScrollProgressChanged(float f);

        void onShow(boolean z);
    }

    public interface LauncherOverlay {
        void onScrollChange(float f, boolean z);

        void onScrollInteractionBegin();

        void onScrollInteractionEnd();

        void setOverlayCallbacks(LauncherOverlayCallbacks launcherOverlayCallbacks);
    }

    public interface LauncherOverlayCallbacks {
        void onScrollChanged(float f);
    }

    class LauncherOverlayCallbacksImpl implements LauncherOverlayCallbacks {
        LauncherOverlayCallbacksImpl() {
        }

        public void onScrollChanged(float f) {
            if (Launcher.this.mWorkspace != null) {
                Launcher.this.mWorkspace.onOverlayScrollChanged(f);
            }
        }
    }

    private class RotationPrefChangeHandler implements OnSharedPreferenceChangeListener {
        private RotationPrefChangeHandler() {
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
            if (Utilities.ALLOW_ROTATION_PREFERENCE_KEY.equals(str)) {
                Launcher.this.recreate();
            }
        }
    }

    enum State {
        NONE,
        WORKSPACE,
        WORKSPACE_SPRING_LOADED,
        APPS,
        APPS_SPRING_LOADED,
        WIDGETS,
        WIDGETS_SPRING_LOADED
    }

    private void updateSoftInputMode() {
    }

    /* access modifiers changed from: 0000 */
    public void lockAllApps() {
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    /* access modifiers changed from: 0000 */
    public void unlockAllApps() {
    }

    /* access modifiers changed from: 0000 */
    public void setOrientation() {
        if (this.mRotationEnabled) {
            unlockScreenOrientation(true);
        } else {
            setRequestedOrientation(5);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.preOnCreate();
        }
        super.onCreate(bundle);
        LauncherAppState instance = LauncherAppState.getInstance(this);
        this.mDeviceProfile = instance.getInvariantDeviceProfile().getDeviceProfile(this);
        if (isInMultiWindowModeCompat()) {
            Display defaultDisplay = getWindowManager().getDefaultDisplay();
            Point point = new Point();
            defaultDisplay.getSize(point);
            this.mDeviceProfile = this.mDeviceProfile.getMultiWindowProfile(this, point);
        }
        this.mOrientation = getResources().getConfiguration().orientation;
        this.mSharedPrefs = Utilities.getPrefs(this);
        this.mIsSafeModeEnabled = getPackageManager().isSafeMode();
        this.mModel = instance.setLauncher(this);
        this.mModelWriter = this.mModel.getWriter(this.mDeviceProfile.isVerticalBarLayout());
        this.mIconCache = instance.getIconCache();
        this.mAccessibilityDelegate = new LauncherAccessibilityDelegate(this);
        this.mDragController = new DragController(this);
        this.mAllAppsController = new AllAppsTransitionController(this);
        this.mStateTransitionAnimation = new LauncherStateTransitionAnimation(this, this.mAllAppsController);
        this.mAppWidgetManager = AppWidgetManagerCompat.getInstance(this);
        this.mAppWidgetHost = new LauncherAppWidgetHost(this);
        if (Utilities.ATLEAST_MARSHMALLOW) {
            this.mAppWidgetHost.addProviderChangeListener(this);
        }
        this.mAppWidgetHost.startListening();
        this.mPaused = false;
        this.mLauncherView = LayoutInflater.from(this).inflate(C0622R.layout.launcher, null);
        setupViews();
        this.mDeviceProfile.layout(this, false);
        loadExtractedColorsAndColorItems();
        this.mPopupDataProvider = new PopupDataProvider(this);
        ((AccessibilityManager) getSystemService("accessibility")).addAccessibilityStateChangeListener(this);
        lockAllApps();
        restoreState(bundle);
        int i = PagedView.INVALID_RESTORE_PAGE;
        if (bundle != null) {
            i = bundle.getInt(RUNTIME_STATE_CURRENT_SCREEN, PagedView.INVALID_RESTORE_PAGE);
        }
        if (!this.mModel.startLoader(i)) {
            this.mDragLayer.setAlpha(0.0f);
        } else {
            this.mWorkspace.setCurrentPage(i);
            setWorkspaceLoading(true);
        }
        this.mDefaultKeySsb = new SpannableStringBuilder();
        Selection.setSelection(this.mDefaultKeySsb, 0);
        this.mRotationEnabled = getResources().getBoolean(C0622R.bool.allow_rotation);
        if (!this.mRotationEnabled) {
            this.mRotationEnabled = Utilities.isAllowRotationPrefEnabled(getApplicationContext());
            this.mRotationPrefChangeHandler = new RotationPrefChangeHandler();
            this.mSharedPrefs.registerOnSharedPreferenceChangeListener(this.mRotationPrefChangeHandler);
        }
        if (PinItemDragListener.handleDragRequest(this, getIntent())) {
            this.mRotationEnabled = true;
        }
        setOrientation();
        setContentView(this.mLauncherView);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.USER_PRESENT");
        registerReceiver(this.mReceiver, intentFilter);
        this.mShouldFadeInScrim = true;
        getSystemUiController().updateUiState(0, Themes.getAttrBoolean(this, C0622R.attr.isWorkspaceDarkText));
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onCreate(bundle);
        }
    }

    public void onThemeChanged() {
        recreate();
    }

    /* access modifiers changed from: protected */
    public void overrideTheme(boolean z, boolean z2, boolean z3) {
        if (z) {
            setTheme(C0622R.style.LauncherThemeDark);
        } else if (z2) {
            setTheme(C0622R.style.LauncherThemeDarkText);
        } else if (z3) {
            setTheme(C0622R.style.LauncherThemeTransparent);
        }
    }

    public <T extends View> T findViewById(int i) {
        return this.mLauncherView.findViewById(i);
    }

    public void onExtractedColorsChanged() {
        loadExtractedColorsAndColorItems();
        this.mExtractedColors.notifyChange();
    }

    public ExtractedColors getExtractedColors() {
        return this.mExtractedColors;
    }

    public void onAppWidgetHostReset() {
        if (this.mAppWidgetHost != null) {
            this.mAppWidgetHost.startListening();
        }
    }

    private void loadExtractedColorsAndColorItems() {
        this.mExtractedColors.load(this);
        this.mHotseat.updateColor(this.mExtractedColors, !this.mPaused);
        this.mWorkspace.getPageIndicator().updateColor(this.mExtractedColors);
    }

    public void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onPostCreate(bundle);
        }
    }

    public void onInsetsChanged(Rect rect) {
        this.mDeviceProfile.updateInsets(rect);
        this.mDeviceProfile.layout(this, true);
    }

    public void setLauncherOverlay(LauncherOverlay launcherOverlay) {
        if (launcherOverlay != null) {
            launcherOverlay.setOverlayCallbacks(new LauncherOverlayCallbacksImpl());
        }
        this.mWorkspace.setLauncherOverlay(launcherOverlay);
    }

    public boolean setLauncherCallbacks(LauncherCallbacks launcherCallbacks) {
        this.mLauncherCallbacks = launcherCallbacks;
        return true;
    }

    public void onLauncherProviderChanged() {
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onLauncherProviderChange();
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasCustomContentToLeft() {
        if (this.mLauncherCallbacks != null) {
            return this.mLauncherCallbacks.hasCustomContentToLeft();
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void populateCustomContentContainer() {
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.populateCustomContentContainer();
        }
    }

    /* access modifiers changed from: protected */
    public void invalidateHasCustomContentToLeft() {
        if (this.mWorkspace != null && !this.mWorkspace.getScreenOrder().isEmpty()) {
            if (!this.mWorkspace.hasCustomContent() && hasCustomContentToLeft()) {
                this.mWorkspace.createCustomContentContainer();
                populateCustomContentContainer();
            } else if (this.mWorkspace.hasCustomContent() && !hasCustomContentToLeft()) {
                this.mWorkspace.removeCustomContentPage();
            }
        }
    }

    public boolean isDraggingEnabled() {
        return !isWorkspaceLoading();
    }

    public int getViewIdForItem(ItemInfo itemInfo) {
        return (int) itemInfo.f52id;
    }

    public PopupDataProvider getPopupDataProvider() {
        return this.mPopupDataProvider;
    }

    private long completeAdd(int i, Intent intent, int i2, PendingRequestArgs pendingRequestArgs) {
        long j = pendingRequestArgs.screenId;
        if (pendingRequestArgs.container == -100) {
            j = ensurePendingDropLayoutExists(pendingRequestArgs.screenId);
        }
        if (i == 1) {
            completeAddShortcut(intent, pendingRequestArgs.container, j, pendingRequestArgs.cellX, pendingRequestArgs.cellY, pendingRequestArgs);
        } else if (i != 5) {
            switch (i) {
                case 12:
                    LauncherAppWidgetInfo completeRestoreAppWidget = completeRestoreAppWidget(i2, 4);
                    if (completeRestoreAppWidget != null) {
                        LauncherAppWidgetProviderInfo launcherAppWidgetInfo = this.mAppWidgetManager.getLauncherAppWidgetInfo(i2);
                        if (launcherAppWidgetInfo != null) {
                            new WidgetAddFlowHandler((AppWidgetProviderInfo) launcherAppWidgetInfo).startConfigActivity(this, completeRestoreAppWidget, 13);
                            break;
                        }
                    }
                    break;
                case 13:
                    completeRestoreAppWidget(i2, 0);
                    break;
            }
        } else {
            completeAddAppWidget(i2, pendingRequestArgs, null, null);
        }
        return j;
    }

    private void handleActivityResult(int i, int i2, Intent intent) {
        int i3 = i;
        final int i4 = i2;
        Intent intent2 = intent;
        if (isWorkspaceLoading()) {
            this.mPendingActivityResult = new ActivityResultInfo(i3, i4, intent2);
            return;
        }
        this.mPendingActivityResult = null;
        final PendingRequestArgs pendingRequestArgs = this.mPendingRequestArgs;
        setWaitingForResult(null);
        if (pendingRequestArgs != null) {
            final int widgetId = pendingRequestArgs.getWidgetId();
            C05552 r5 = new Runnable() {
                public void run() {
                    Launcher.this.exitSpringLoadedDragModeDelayed(i4 != 0, 500, null);
                }
            };
            int i5 = -1;
            if (i3 == 11) {
                int intExtra = intent2 != null ? intent2.getIntExtra(Favorites.APPWIDGET_ID, -1) : -1;
                if (i4 == 0) {
                    completeTwoStageWidgetDrop(0, intExtra, pendingRequestArgs);
                    this.mWorkspace.removeExtraEmptyScreenDelayed(true, r5, 500, false);
                } else if (i4 == -1) {
                    addAppWidgetImpl(intExtra, pendingRequestArgs, null, pendingRequestArgs.getWidgetHandler(), 500);
                }
            } else if (i3 == 10) {
                if (i4 == -1 && this.mWorkspace.isInOverviewMode()) {
                    this.mWorkspace.setCurrentPage(this.mWorkspace.getPageNearestToCenterOfScreen());
                    showWorkspace(false);
                }
            } else {
                if (i3 == 9 || i3 == 5) {
                    if (intent2 != null) {
                        i5 = intent2.getIntExtra(Favorites.APPWIDGET_ID, -1);
                    }
                    if (i5 >= 0) {
                        widgetId = i5;
                    }
                    if (widgetId < 0 || i4 == 0) {
                        Log.e(TAG, "Error: appWidgetId (EXTRA_APPWIDGET_ID) was not returned from the widget configuration activity.");
                        completeTwoStageWidgetDrop(0, widgetId, pendingRequestArgs);
                        this.mWorkspace.removeExtraEmptyScreenDelayed(true, new Runnable() {
                            public void run() {
                                Launcher.this.exitSpringLoadedDragModeDelayed(false, 0, null);
                            }
                        }, 500, false);
                    } else {
                        if (pendingRequestArgs.container == -100) {
                            pendingRequestArgs.screenId = ensurePendingDropLayoutExists(pendingRequestArgs.screenId);
                        }
                        final CellLayout screenWithId = this.mWorkspace.getScreenWithId(pendingRequestArgs.screenId);
                        screenWithId.setDropPending(true);
                        final int i6 = i2;
                        C05764 r0 = new Runnable() {
                            public void run() {
                                Launcher.this.completeTwoStageWidgetDrop(i6, widgetId, pendingRequestArgs);
                                screenWithId.setDropPending(false);
                            }
                        };
                        this.mWorkspace.removeExtraEmptyScreenDelayed(true, r0, 500, false);
                    }
                } else if (i3 == 13 || i3 == 12) {
                    if (i4 == -1) {
                        completeAdd(i3, intent2, widgetId, pendingRequestArgs);
                    }
                } else {
                    if (i3 == 1) {
                        if (i4 == -1 && pendingRequestArgs.container != -1) {
                            completeAdd(i3, intent2, -1, pendingRequestArgs);
                            this.mWorkspace.removeExtraEmptyScreenDelayed(true, r5, 500, false);
                        } else if (i4 == 0) {
                            this.mWorkspace.removeExtraEmptyScreenDelayed(true, r5, 500, false);
                        }
                    }
                    this.mDragLayer.clearAnimatedView();
                }
            }
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        handleActivityResult(i, i2, intent);
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onActivityResult(i, i2, intent);
        }
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        PendingRequestArgs pendingRequestArgs = this.mPendingRequestArgs;
        if (i == 14 && pendingRequestArgs != null && pendingRequestArgs.getRequestCode() == 14) {
            setWaitingForResult(null);
            CellLayout cellLayout = getCellLayout(pendingRequestArgs.container, pendingRequestArgs.screenId);
            View childAt = cellLayout != null ? cellLayout.getChildAt(pendingRequestArgs.cellX, pendingRequestArgs.cellY) : null;
            Intent pendingIntent = pendingRequestArgs.getPendingIntent();
            if (iArr.length <= 0 || iArr[0] != 0) {
                Toast.makeText(this, getString(C0622R.string.msg_no_phone_permission, new Object[]{getString(C0622R.string.derived_app_name)}), 0).show();
            } else {
                startActivitySafely(childAt, pendingIntent, null);
            }
        }
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onRequestPermissionsResult(i, strArr, iArr);
        }
    }

    private long ensurePendingDropLayoutExists(long j) {
        if (this.mWorkspace.getScreenWithId(j) != null) {
            return j;
        }
        this.mWorkspace.addExtraEmptyScreen();
        return this.mWorkspace.commitExtraEmptyScreen();
    }

    /* access modifiers changed from: 0000 */
    public void completeTwoStageWidgetDrop(int i, int i2, PendingRequestArgs pendingRequestArgs) {
        AppWidgetHostView appWidgetHostView;
        int i3;
        C05775 r7;
        CellLayout screenWithId = this.mWorkspace.getScreenWithId(pendingRequestArgs.screenId);
        if (i == -1) {
            AppWidgetHostView createView = this.mAppWidgetHost.createView(this, i2, pendingRequestArgs.getWidgetHandler().getProviderInfo(this));
            final int i4 = i2;
            final PendingRequestArgs pendingRequestArgs2 = pendingRequestArgs;
            final AppWidgetHostView appWidgetHostView2 = createView;
            final int i5 = i;
            C05775 r6 = new Runnable() {
                public void run() {
                    Launcher.this.completeAddAppWidget(i4, pendingRequestArgs2, appWidgetHostView2, null);
                    Launcher.this.exitSpringLoadedDragModeDelayed(i5 != 0, 500, null);
                }
            };
            appWidgetHostView = createView;
            r7 = r6;
            i3 = 3;
        } else if (i == 0) {
            this.mAppWidgetHost.deleteAppWidgetId(i2);
            r7 = null;
            appWidgetHostView = null;
            i3 = 4;
        } else {
            r7 = null;
            appWidgetHostView = null;
            i3 = 0;
        }
        if (this.mDragLayer.getAnimatedView() != null) {
            this.mWorkspace.animateWidgetDrop(pendingRequestArgs, screenWithId, (DragView) this.mDragLayer.getAnimatedView(), r7, i3, appWidgetHostView, true);
        } else if (r7 != null) {
            r7.run();
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        FirstFrameAnimatorHelper.setIsVisible(false);
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onStop();
        }
        if (Utilities.ATLEAST_NOUGAT_MR1) {
            this.mAppWidgetHost.stopListening();
        }
        NotificationListener.removeNotificationsChangedListener();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        FirstFrameAnimatorHelper.setIsVisible(true);
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onStart();
        }
        if (Utilities.ATLEAST_NOUGAT_MR1) {
            this.mAppWidgetHost.startListening();
        }
        if (!isWorkspaceLoading()) {
            NotificationListener.setNotificationsChangedListener(this.mPopupDataProvider);
        }
        if (this.mShouldFadeInScrim && this.mDragLayer.getBackground() != null) {
            if (this.mScrimAnimator != null) {
                this.mScrimAnimator.cancel();
            }
            this.mDragLayer.getBackground().setAlpha(0);
            this.mScrimAnimator = ObjectAnimator.ofInt(this.mDragLayer.getBackground(), LauncherAnimUtils.DRAWABLE_ALPHA, new int[]{0, 255});
            this.mScrimAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    Launcher.this.mScrimAnimator = null;
                }
            });
            this.mScrimAnimator.setDuration(600);
            this.mScrimAnimator.setStartDelay(getWindow().getTransitionBackgroundFadeDuration());
            this.mScrimAnimator.start();
        }
        this.mShouldFadeInScrim = false;
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.preOnResume();
        }
        super.onResume();
        getUserEventDispatcher().resetElapsedSessionMillis();
        if (this.mOnResumeState == State.WORKSPACE) {
            showWorkspace(false);
        } else if (this.mOnResumeState == State.APPS) {
            showAppsView(false, !(this.mWaitingForResume != null), false);
        } else if (this.mOnResumeState == State.WIDGETS) {
            showWidgetsView(false, false);
        }
        if (this.mOnResumeState != State.APPS) {
            tryAndUpdatePredictedApps();
        }
        this.mOnResumeState = State.NONE;
        this.mPaused = false;
        if (this.mOnResumeNeedsLoad) {
            setWorkspaceLoading(true);
            this.mModel.startLoader(getCurrentWorkspaceScreen());
            this.mOnResumeNeedsLoad = false;
        }
        if (this.mBindOnResumeCallbacks.size() > 0) {
            for (int i = 0; i < this.mBindOnResumeCallbacks.size(); i++) {
                ((Runnable) this.mBindOnResumeCallbacks.get(i)).run();
            }
            this.mBindOnResumeCallbacks.clear();
        }
        if (this.mOnResumeCallbacks.size() > 0) {
            for (int i2 = 0; i2 < this.mOnResumeCallbacks.size(); i2++) {
                ((Runnable) this.mOnResumeCallbacks.get(i2)).run();
            }
            this.mOnResumeCallbacks.clear();
        }
        if (this.mWaitingForResume != null) {
            this.mWaitingForResume.setStayPressed(false);
        }
        if (!isWorkspaceLoading()) {
            getWorkspace().reinflateWidgetsIfNecessary();
        }
        if (this.mWorkspace.getCustomContentCallbacks() != null && !this.mMoveToDefaultScreenFromNewIntent && this.mWorkspace.isOnOrMovingToCustomContent()) {
            this.mWorkspace.getCustomContentCallbacks().onShow(true);
        }
        this.mMoveToDefaultScreenFromNewIntent = false;
        updateInteraction(com.android.launcher3.Workspace.State.NORMAL, this.mWorkspace.getState());
        this.mWorkspace.onResume();
        InstallShortcutReceiver.disableAndFlushInstallQueue(1, this);
        this.mModel.refreshShortcutsIfRequired();
        if (this.mAllAppsController.isTransitioning()) {
            this.mAppsView.setVisibility(0);
        }
        if (shouldShowDiscoveryBounce()) {
            this.mAllAppsController.showDiscoveryBounce();
        }
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onResume();
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        InstallShortcutReceiver.enableInstallQueue(1);
        super.onPause();
        this.mPaused = true;
        this.mDragController.cancelDrag();
        this.mDragController.resetLastGestureUpTime();
        if (this.mWorkspace.getCustomContentCallbacks() != null) {
            this.mWorkspace.getCustomContentCallbacks().onHide();
        }
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onPause();
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasSettings() {
        if (this.mLauncherCallbacks != null) {
            return this.mLauncherCallbacks.hasSettings();
        }
        return Utilities.ATLEAST_OREO || !getResources().getBoolean(C0622R.bool.allow_rotation);
    }

    public void addToCustomContentPage(View view, CustomContentCallbacks customContentCallbacks, String str) {
        this.mWorkspace.addToCustomContentPage(view, customContentCallbacks, str);
    }

    public int getTopOffsetForCustomContent() {
        return this.mWorkspace.getPaddingTop();
    }

    public Object onRetainNonConfigurationInstance() {
        if (this.mModel.isCurrentCallbacks(this)) {
            this.mModel.stopLoader();
        }
        return Boolean.TRUE;
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        this.mHasFocus = z;
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onWindowFocusChanged(z);
        }
    }

    private boolean acceptFilter() {
        return !((InputMethodManager) getSystemService("input_method")).isFullscreenMode();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        int unicodeChar = keyEvent.getUnicodeChar();
        boolean onKeyDown = super.onKeyDown(i, keyEvent);
        boolean z = unicodeChar > 0 && !Character.isWhitespace(unicodeChar);
        if (!onKeyDown && acceptFilter() && z && TextKeyListener.getInstance().onKeyDown(this.mWorkspace, this.mDefaultKeySsb, i, keyEvent) && this.mDefaultKeySsb != null && this.mDefaultKeySsb.length() > 0) {
            return onSearchRequested();
        }
        if (i != 82 || !keyEvent.isLongPress()) {
            return onKeyDown;
        }
        return true;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (i != 82) {
            return super.onKeyUp(i, keyEvent);
        }
        if (!isOnCustomContent() && !this.mDragController.isDragging()) {
            AbstractFloatingView.closeAllOpenViews(this);
            this.mWorkspace.exitWidgetResizeMode();
        }
        return true;
    }

    private String getTypedText() {
        return this.mDefaultKeySsb.toString();
    }

    public void clearTypedText() {
        this.mDefaultKeySsb.clear();
        this.mDefaultKeySsb.clearSpans();
        Selection.setSelection(this.mDefaultKeySsb, 0);
    }

    private void restoreState(Bundle bundle) {
        if (bundle != null) {
            int i = bundle.getInt(RUNTIME_STATE, State.WORKSPACE.ordinal());
            State[] values = State.values();
            State state = (i < 0 || i >= values.length) ? State.WORKSPACE : values[i];
            if (state == State.APPS || state == State.WIDGETS) {
                this.mOnResumeState = state;
            }
            PendingRequestArgs pendingRequestArgs = (PendingRequestArgs) bundle.getParcelable(RUNTIME_STATE_PENDING_REQUEST_ARGS);
            if (pendingRequestArgs != null) {
                setWaitingForResult(pendingRequestArgs);
            }
            this.mPendingActivityResult = (ActivityResultInfo) bundle.getParcelable(RUNTIME_STATE_PENDING_ACTIVITY_RESULT);
        }
    }

    private void setupViews() {
        this.mDragLayer = (DragLayer) findViewById(C0622R.C0625id.drag_layer);
        this.mFocusHandler = this.mDragLayer.getFocusIndicatorHelper();
        this.mWorkspace = (Workspace) this.mDragLayer.findViewById(C0622R.C0625id.workspace);
        this.mWorkspace.initParentViews(this.mDragLayer);
        this.mLauncherView.setSystemUiVisibility(1792);
        this.mDragLayer.setup(this, this.mDragController, this.mAllAppsController);
        this.mHotseat = (Hotseat) findViewById(C0622R.C0625id.hotseat);
        if (this.mHotseat != null) {
            this.mHotseat.setOnLongClickListener(this);
        }
        setupOverviewPanel();
        this.mWorkspace.setHapticFeedbackEnabled(false);
        this.mWorkspace.setOnLongClickListener(this);
        this.mWorkspace.setup(this.mDragController);
        this.mWorkspace.lockWallpaperToDefaultPage();
        this.mWorkspace.bindAndInitFirstWorkspaceScreen(null);
        this.mDragController.addDragListener(this.mWorkspace);
        this.mDropTargetBar = (DropTargetBar) this.mDragLayer.findViewById(C0622R.C0625id.drop_target_bar);
        this.mAppsView = (AllAppsContainerView) findViewById(C0622R.C0625id.apps_view);
        this.mWidgetsView = (WidgetsContainerView) findViewById(C0622R.C0625id.widgets_view);
        this.mDragController.setMoveTarget(this.mWorkspace);
        this.mDragController.addDropTarget(this.mWorkspace);
        this.mDropTargetBar.setup(this.mDragController);
        this.mAllAppsController.setupViews(this.mAppsView, this.mHotseat, this.mWorkspace);
    }

    private void setupOverviewPanel() {
        this.mOverviewPanel = (ViewGroup) findViewById(C0622R.C0625id.overview_panel);
        new OverviewButtonClickListener(3) {
            public void handleViewClick(View view) {
                Launcher.this.onClickWallpaperPicker(view);
            }
        }.attachTo(findViewById(C0622R.C0625id.wallpaper_button));
        this.mWidgetsButton = findViewById(C0622R.C0625id.widget_button);
        new OverviewButtonClickListener(2) {
            public void handleViewClick(View view) {
                Launcher.this.onClickAddWidgetButton(view);
                view.postDelayed(new Runnable() {
                    public final void run() {
                        Launcher.this.showWorkspace(false);
                    }
                }, 500);
            }
        }.attachTo(this.mWidgetsButton);
        View findViewById = findViewById(C0622R.C0625id.settings_button);
        if (hasSettings()) {
            new OverviewButtonClickListener(4) {
                public void handleViewClick(View view) {
                    Launcher.this.onClickSettingsButton(view);
                    view.postDelayed(new Runnable() {
                        public final void run() {
                            Launcher.this.showWorkspace(false);
                        }
                    }, 500);
                }
            }.attachTo(findViewById);
        } else {
            findViewById.setVisibility(8);
        }
        this.mOverviewPanel.setAlpha(0.0f);
    }

    public void setAllAppsButton(View view) {
        this.mAllAppsButton = view;
    }

    public View getStartViewForAllAppsRevealAnimation() {
        return this.mWorkspace.getPageIndicator();
    }

    public View getWidgetsButton() {
        return this.mWidgetsButton;
    }

    /* access modifiers changed from: 0000 */
    public View createShortcut(ShortcutInfo shortcutInfo) {
        return createShortcut((ViewGroup) this.mWorkspace.getChildAt(this.mWorkspace.getCurrentPage()), shortcutInfo);
    }

    public View createShortcut(ViewGroup viewGroup, ShortcutInfo shortcutInfo) {
        BubbleTextView bubbleTextView = (BubbleTextView) LayoutInflater.from(viewGroup.getContext()).inflate(C0622R.layout.app_icon, viewGroup, false);
        bubbleTextView.applyFromShortcutInfo(shortcutInfo);
        bubbleTextView.setOnClickListener(this);
        bubbleTextView.setOnFocusChangeListener(this.mFocusHandler);
        return bubbleTextView;
    }

    private void completeAddShortcut(Intent intent, long j, long j2, int i, int i2, PendingRequestArgs pendingRequestArgs) {
        ShortcutInfo shortcutInfo;
        long j3;
        View view;
        boolean z;
        CellLayout cellLayout;
        int[] iArr;
        char c;
        long j4 = j;
        PendingRequestArgs pendingRequestArgs2 = pendingRequestArgs;
        if (pendingRequestArgs.getRequestCode() == 1 && pendingRequestArgs.getPendingIntent().getComponent() != null) {
            int[] iArr2 = this.mTmpAddItemCellCoordinates;
            CellLayout cellLayout2 = getCellLayout(j4, j2);
            ShortcutInfo createShortcutInfoFromPinItemRequest = Utilities.ATLEAST_OREO ? LauncherAppsCompatVO.createShortcutInfoFromPinItemRequest(this, LauncherAppsCompatVO.getPinItemRequest(intent), 0) : null;
            if (createShortcutInfoFromPinItemRequest == null) {
                ShortcutInfo fromShortcutIntent = Process.myUserHandle().equals(pendingRequestArgs2.user) ? InstallShortcutReceiver.fromShortcutIntent(this, intent) : null;
                if (fromShortcutIntent == null) {
                    Log.e(TAG, "Unable to parse a valid custom shortcut result");
                    return;
                } else if (!new PackageManagerHelper(this).hasPermissionForActivity(fromShortcutIntent.intent, pendingRequestArgs.getPendingIntent().getComponent().getPackageName())) {
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Ignoring malicious intent ");
                    sb.append(fromShortcutIntent.intent.toUri(0));
                    Log.e(str, sb.toString());
                    return;
                } else {
                    shortcutInfo = fromShortcutIntent;
                    j3 = 0;
                }
            } else {
                shortcutInfo = createShortcutInfoFromPinItemRequest;
                j3 = 0;
            }
            if (j4 < j3) {
                View createShortcut = createShortcut(shortcutInfo);
                if (i < 0 || i2 < 0) {
                    view = createShortcut;
                    cellLayout = cellLayout2;
                    iArr = iArr2;
                    c = 1;
                    z = cellLayout.findCellForSpan(iArr, 1, 1);
                } else {
                    iArr2[0] = i;
                    iArr2[1] = i2;
                    view = createShortcut;
                    if (!this.mWorkspace.createUserFolderIfNecessary(createShortcut, j, cellLayout2, iArr2, 0.0f, true, null, null)) {
                        DragObject dragObject = new DragObject();
                        dragObject.dragInfo = shortcutInfo;
                        cellLayout = cellLayout2;
                        iArr = iArr2;
                        if (!this.mWorkspace.addToExistingFolderIfNecessary(view, cellLayout, iArr, 0.0f, dragObject, true)) {
                            c = 1;
                            z = true;
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                if (!z) {
                    this.mWorkspace.onNoCellFound(cellLayout);
                } else {
                    getModelWriter().addItemToDatabase(shortcutInfo, j, j2, iArr[0], iArr[c]);
                    this.mWorkspace.addInScreen(view, shortcutInfo);
                }
            } else {
                FolderIcon findFolderIcon = findFolderIcon(j4);
                if (findFolderIcon != null) {
                    ((FolderInfo) findFolderIcon.getTag()).add(shortcutInfo, pendingRequestArgs2.rank, false);
                } else {
                    String str2 = TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Could not find folder with id ");
                    sb2.append(j4);
                    sb2.append(" to add shortcut.");
                    Log.e(str2, sb2.toString());
                }
            }
        }
    }

    public FolderIcon findFolderIcon(final long j) {
        return (FolderIcon) this.mWorkspace.getFirstMatch(new ItemOperator() {
            public boolean evaluate(ItemInfo itemInfo, View view) {
                return itemInfo != null && itemInfo.f52id == j;
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public void completeAddAppWidget(int i, ItemInfo itemInfo, AppWidgetHostView appWidgetHostView, LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo) {
        if (launcherAppWidgetProviderInfo == null) {
            launcherAppWidgetProviderInfo = this.mAppWidgetManager.getLauncherAppWidgetInfo(i);
        }
        if (launcherAppWidgetProviderInfo.isCustomWidget) {
            i = -100;
        }
        LauncherAppWidgetInfo launcherAppWidgetInfo = new LauncherAppWidgetInfo(i, launcherAppWidgetProviderInfo.provider);
        launcherAppWidgetInfo.spanX = itemInfo.spanX;
        launcherAppWidgetInfo.spanY = itemInfo.spanY;
        launcherAppWidgetInfo.minSpanX = itemInfo.minSpanX;
        launcherAppWidgetInfo.minSpanY = itemInfo.minSpanY;
        launcherAppWidgetInfo.user = launcherAppWidgetProviderInfo.getUser();
        getModelWriter().addItemToDatabase(launcherAppWidgetInfo, itemInfo.container, itemInfo.screenId, itemInfo.cellX, itemInfo.cellY);
        if (appWidgetHostView == null) {
            appWidgetHostView = this.mAppWidgetHost.createView(this, i, launcherAppWidgetProviderInfo);
        }
        appWidgetHostView.setVisibility(0);
        prepareAppWidget(appWidgetHostView, launcherAppWidgetInfo);
        this.mWorkspace.addInScreen(appWidgetHostView, launcherAppWidgetInfo);
    }

    private void prepareAppWidget(AppWidgetHostView appWidgetHostView, LauncherAppWidgetInfo launcherAppWidgetInfo) {
        appWidgetHostView.setTag(launcherAppWidgetInfo);
        launcherAppWidgetInfo.onBindAppWidget(this, appWidgetHostView);
        appWidgetHostView.setFocusable(true);
        appWidgetHostView.setOnFocusChangeListener(this.mFocusHandler);
    }

    public void updateIconBadges(final Set<PackageUserKey> set) {
        C054612 r0 = new Runnable() {
            public void run() {
                Launcher.this.mWorkspace.updateIconBadges(set);
                Launcher.this.mAppsView.updateIconBadges(set);
                PopupContainerWithArrow open = PopupContainerWithArrow.getOpen(Launcher.this);
                if (open != null) {
                    open.updateNotificationHeader(set);
                }
            }
        };
        if (!waitUntilResume(r0)) {
            r0.run();
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        FirstFrameAnimatorHelper.initializeDrawListener(getWindow().getDecorView());
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onAttachedToWindow();
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onDetachedFromWindow();
        }
    }

    public void onWindowVisibilityChanged(int i) {
        if (i == 0) {
            if (!this.mWorkspaceLoading) {
                this.mWorkspace.getViewTreeObserver().addOnDrawListener(new OnDrawListener() {
                    private boolean mStarted = false;

                    public void onDraw() {
                        if (!this.mStarted) {
                            this.mStarted = true;
                            Launcher.this.mWorkspace.postDelayed(Launcher.this.mBuildLayersRunnable, 500);
                            Launcher.this.mWorkspace.post(new Runnable() {
                                public void run() {
                                    if (Launcher.this.mWorkspace != null && Launcher.this.mWorkspace.getViewTreeObserver() != null) {
                                        Launcher.this.mWorkspace.getViewTreeObserver().removeOnDrawListener(this);
                                    }
                                }
                            });
                        }
                    }
                });
            }
            clearTypedText();
        }
    }

    public DragLayer getDragLayer() {
        return this.mDragLayer;
    }

    public AllAppsContainerView getAppsView() {
        return this.mAppsView;
    }

    public WidgetsContainerView getWidgetsView() {
        return this.mWidgetsView;
    }

    public Workspace getWorkspace() {
        return this.mWorkspace;
    }

    public Hotseat getHotseat() {
        return this.mHotseat;
    }

    public ViewGroup getOverviewPanel() {
        return this.mOverviewPanel;
    }

    public DropTargetBar getDropTargetBar() {
        return this.mDropTargetBar;
    }

    public LauncherAppWidgetHost getAppWidgetHost() {
        return this.mAppWidgetHost;
    }

    public LauncherModel getModel() {
        return this.mModel;
    }

    public ModelWriter getModelWriter() {
        return this.mModelWriter;
    }

    public SharedPreferences getSharedPrefs() {
        return this.mSharedPrefs;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean z = false;
        boolean z2 = this.mHasFocus && (intent.getFlags() & 4194304) != 4194304;
        boolean z3 = z2 && this.mState == State.WORKSPACE && AbstractFloatingView.getTopOpenView(this) == null;
        boolean equals = "android.intent.action.MAIN".equals(intent.getAction());
        if (equals) {
            if (this.mWorkspace != null) {
                UserEventDispatcher userEventDispatcher = getUserEventDispatcher();
                this.mWorkspace.exitWidgetResizeMode();
                AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(this);
                if (topOpenView instanceof PopupContainerWithArrow) {
                    userEventDispatcher.logActionCommand(0, topOpenView.getExtendedTouchView(), 9);
                } else if (topOpenView instanceof Folder) {
                    userEventDispatcher.logActionCommand(0, (View) ((Folder) topOpenView).getFolderIcon(), 3);
                } else if (z2) {
                    userEventDispatcher.logActionCommand(0, this.mWorkspace.getState().containerType, this.mWorkspace.getCurrentPage());
                }
                AbstractFloatingView.closeAllOpenViews(this, z2);
                exitSpringLoadedDragMode();
                if (!z2) {
                    this.mOnResumeState = State.WORKSPACE;
                } else if (!this.mAllAppsController.isDragging()) {
                    showWorkspace(true);
                }
                View peekDecorView = getWindow().peekDecorView();
                if (!(peekDecorView == null || peekDecorView.getWindowToken() == null)) {
                    ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(peekDecorView.getWindowToken(), 0);
                }
                if (!z2 && this.mAppsView != null) {
                    this.mAppsView.reset();
                }
                if (!z2 && this.mWidgetsView != null) {
                    this.mWidgetsView.scrollToTop();
                }
                if (this.mLauncherCallbacks != null) {
                    this.mLauncherCallbacks.onHomeIntent();
                }
            } else {
                return;
            }
        }
        PinItemDragListener.handleDragRequest(this, intent);
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onNewIntent(intent);
        }
        if (equals) {
            if (this.mLauncherCallbacks == null || this.mLauncherCallbacks.shouldMoveToDefaultScreenOnHomeIntent()) {
                z = true;
            }
            if (z3 && !this.mWorkspace.isTouchActive() && z) {
                this.mMoveToDefaultScreenFromNewIntent = true;
                this.mWorkspace.post(new Runnable() {
                    public void run() {
                        if (Launcher.this.mWorkspace != null) {
                            Launcher.this.mWorkspace.moveToDefaultScreen(true);
                        }
                    }
                });
            }
        }
    }

    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        Iterator it = this.mSynchronouslyBoundPages.iterator();
        while (it.hasNext()) {
            this.mWorkspace.restoreInstanceStateForChild(((Integer) it.next()).intValue());
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        if (this.mWorkspace.getChildCount() > 0) {
            bundle.putInt(RUNTIME_STATE_CURRENT_SCREEN, this.mWorkspace.getCurrentPageOffsetFromCustomContent());
        }
        super.onSaveInstanceState(bundle);
        bundle.putInt(RUNTIME_STATE, this.mState.ordinal());
        AbstractFloatingView.closeAllOpenViews(this, false);
        if (this.mPendingRequestArgs != null) {
            bundle.putParcelable(RUNTIME_STATE_PENDING_REQUEST_ARGS, this.mPendingRequestArgs);
        }
        if (this.mPendingActivityResult != null) {
            bundle.putParcelable(RUNTIME_STATE_PENDING_ACTIVITY_RESULT, this.mPendingActivityResult);
        }
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onSaveInstanceState(bundle);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mReceiver);
        this.mWorkspace.removeCallbacks(this.mBuildLayersRunnable);
        this.mWorkspace.removeFolderListeners();
        if (this.mModel.isCurrentCallbacks(this)) {
            this.mModel.stopLoader();
            LauncherAppState.getInstance(this).setLauncher(null);
        }
        if (this.mRotationPrefChangeHandler != null) {
            this.mSharedPrefs.unregisterOnSharedPreferenceChangeListener(this.mRotationPrefChangeHandler);
        }
        try {
            this.mAppWidgetHost.stopListening();
        } catch (NullPointerException e) {
            Log.w(TAG, "problem while stopping AppWidgetHost during Launcher destruction", e);
        }
        this.mAppWidgetHost = null;
        TextKeyListener.getInstance().release();
        ((AccessibilityManager) getSystemService("accessibility")).removeAccessibilityStateChangeListener(this);
        WallpaperColorInfo.getInstance(this).setOnThemeChangeListener(null);
        LauncherAnimUtils.onDestroyActivity();
        clearPendingBinds();
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onDestroy();
        }
    }

    public LauncherAccessibilityDelegate getAccessibilityDelegate() {
        return this.mAccessibilityDelegate;
    }

    public DragController getDragController() {
        return this.mDragController;
    }

    public void startActivityForResult(Intent intent, int i, Bundle bundle) {
        super.startActivityForResult(intent, i, bundle);
    }

    public void startIntentSenderForResult(IntentSender intentSender, int i, Intent intent, int i2, int i3, int i4, Bundle bundle) {
        try {
            super.startIntentSenderForResult(intentSender, i, intent, i2, i3, i4, bundle);
        } catch (SendIntentException unused) {
            throw new ActivityNotFoundException();
        }
    }

    public void startSearch(String str, boolean z, Bundle bundle, boolean z2) {
        if (str == null) {
            str = getTypedText();
        }
        if (bundle == null) {
            bundle = new Bundle();
            bundle.putString("source", "launcher-search");
        }
        if (this.mLauncherCallbacks == null || !this.mLauncherCallbacks.startSearch(str, z, bundle)) {
            startGlobalSearch(str, z, bundle, null);
        }
        showWorkspace(true);
    }

    public void startGlobalSearch(String str, boolean z, Bundle bundle, Rect rect) {
        Bundle bundle2;
        ComponentName globalSearchActivity = ((SearchManager) getSystemService("search")).getGlobalSearchActivity();
        if (globalSearchActivity == null) {
            Log.w(TAG, "No global search activity found.");
            return;
        }
        Intent intent = new Intent("android.search.action.GLOBAL_SEARCH");
        intent.addFlags(268435456);
        intent.setComponent(globalSearchActivity);
        if (bundle == null) {
            bundle2 = new Bundle();
        } else {
            bundle2 = new Bundle(bundle);
        }
        if (!bundle2.containsKey("source")) {
            bundle2.putString("source", getPackageName());
        }
        intent.putExtra("app_data", bundle2);
        if (!TextUtils.isEmpty(str)) {
            intent.putExtra("query", str);
        }
        if (z) {
            intent.putExtra("select_query", z);
        }
        intent.setSourceBounds(rect);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Global search activity not found: ");
            sb.append(globalSearchActivity);
            Log.e(str2, sb.toString());
        }
    }

    public boolean isOnCustomContent() {
        return this.mWorkspace.isOnOrMovingToCustomContent();
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (this.mLauncherCallbacks != null) {
            return this.mLauncherCallbacks.onPrepareOptionsMenu(menu);
        }
        return false;
    }

    public boolean onSearchRequested() {
        startSearch(null, false, null, true);
        return true;
    }

    public boolean isWorkspaceLocked() {
        return this.mWorkspaceLoading || this.mPendingRequestArgs != null;
    }

    public boolean isWorkspaceLoading() {
        return this.mWorkspaceLoading;
    }

    /* access modifiers changed from: protected */
    public void setWorkspaceLoading(boolean z) {
        boolean isWorkspaceLocked = isWorkspaceLocked();
        this.mWorkspaceLoading = z;
        if (isWorkspaceLocked != isWorkspaceLocked()) {
            onWorkspaceLockedChanged();
        }
    }

    public void setWaitingForResult(PendingRequestArgs pendingRequestArgs) {
        boolean isWorkspaceLocked = isWorkspaceLocked();
        this.mPendingRequestArgs = pendingRequestArgs;
        if (isWorkspaceLocked != isWorkspaceLocked()) {
            onWorkspaceLockedChanged();
        }
    }

    /* access modifiers changed from: protected */
    public void onWorkspaceLockedChanged() {
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onWorkspaceLockedChanged();
        }
    }

    /* access modifiers changed from: 0000 */
    public void addAppWidgetFromDropImpl(int i, ItemInfo itemInfo, AppWidgetHostView appWidgetHostView, WidgetAddFlowHandler widgetAddFlowHandler) {
        addAppWidgetImpl(i, itemInfo, appWidgetHostView, widgetAddFlowHandler, 0);
    }

    /* access modifiers changed from: 0000 */
    public void addAppWidgetImpl(int i, ItemInfo itemInfo, AppWidgetHostView appWidgetHostView, WidgetAddFlowHandler widgetAddFlowHandler, int i2) {
        if (!widgetAddFlowHandler.startConfigActivity(this, i, itemInfo, 5)) {
            C055015 r0 = new Runnable() {
                public void run() {
                    Launcher.this.exitSpringLoadedDragModeDelayed(true, 500, null);
                }
            };
            completeAddAppWidget(i, itemInfo, appWidgetHostView, widgetAddFlowHandler.getProviderInfo(this));
            this.mWorkspace.removeExtraEmptyScreenDelayed(true, r0, i2, false);
        }
    }

    /* access modifiers changed from: protected */
    public void moveToCustomContentScreen(boolean z) {
        AbstractFloatingView.closeAllOpenViews(this, z);
        this.mWorkspace.moveToCustomContentScreen(z);
    }

    public void addPendingItem(PendingAddItemInfo pendingAddItemInfo, long j, long j2, int[] iArr, int i, int i2) {
        pendingAddItemInfo.container = j;
        pendingAddItemInfo.screenId = j2;
        if (iArr != null) {
            pendingAddItemInfo.cellX = iArr[0];
            pendingAddItemInfo.cellY = iArr[1];
        }
        pendingAddItemInfo.spanX = i;
        pendingAddItemInfo.spanY = i2;
        int i3 = pendingAddItemInfo.itemType;
        if (i3 != 1) {
            switch (i3) {
                case 4:
                case 5:
                    addAppWidgetFromDrop((PendingAddWidgetInfo) pendingAddItemInfo);
                    return;
                default:
                    StringBuilder sb = new StringBuilder();
                    sb.append("Unknown item type: ");
                    sb.append(pendingAddItemInfo.itemType);
                    throw new IllegalStateException(sb.toString());
            }
        } else {
            processShortcutFromDrop((PendingAddShortcutInfo) pendingAddItemInfo);
        }
    }

    private void processShortcutFromDrop(PendingAddShortcutInfo pendingAddShortcutInfo) {
        setWaitingForResult(PendingRequestArgs.forIntent(1, new Intent("android.intent.action.CREATE_SHORTCUT").setComponent(pendingAddShortcutInfo.componentName), pendingAddShortcutInfo));
        if (!pendingAddShortcutInfo.activityInfo.startConfigActivity(this, 1)) {
            handleActivityResult(1, 0, null);
        }
    }

    private void addAppWidgetFromDrop(PendingAddWidgetInfo pendingAddWidgetInfo) {
        AppWidgetHostView appWidgetHostView = pendingAddWidgetInfo.boundWidget;
        WidgetAddFlowHandler handler = pendingAddWidgetInfo.getHandler();
        if (appWidgetHostView != null) {
            getDragLayer().removeView(appWidgetHostView);
            addAppWidgetFromDropImpl(appWidgetHostView.getAppWidgetId(), pendingAddWidgetInfo, appWidgetHostView, handler);
            pendingAddWidgetInfo.boundWidget = null;
            return;
        }
        int allocateAppWidgetId = getAppWidgetHost().allocateAppWidgetId();
        if (this.mAppWidgetManager.bindAppWidgetIdIfAllowed(allocateAppWidgetId, pendingAddWidgetInfo.info, pendingAddWidgetInfo.bindOptions)) {
            addAppWidgetFromDropImpl(allocateAppWidgetId, pendingAddWidgetInfo, null, handler);
        } else {
            handler.startBindFlow(this, allocateAppWidgetId, pendingAddWidgetInfo, 11);
        }
    }

    /* access modifiers changed from: 0000 */
    public FolderIcon addFolder(CellLayout cellLayout, long j, long j2, int i, int i2) {
        FolderInfo folderInfo = new FolderInfo();
        folderInfo.title = getText(C0622R.string.folder_name);
        getModelWriter().addItemToDatabase(folderInfo, j, j2, i, i2);
        CellLayout cellLayout2 = cellLayout;
        FolderIcon fromXml = FolderIcon.fromXml(C0622R.layout.folder_icon, this, cellLayout, folderInfo);
        this.mWorkspace.addInScreen(fromXml, folderInfo);
        this.mWorkspace.getParentCellLayoutForView(fromXml).getShortcutsAndWidgets().measureChild(fromXml);
        return fromXml;
    }

    public boolean removeItem(View view, ItemInfo itemInfo, boolean z) {
        if (itemInfo instanceof ShortcutInfo) {
            View homescreenIconByItemId = this.mWorkspace.getHomescreenIconByItemId(itemInfo.container);
            if (homescreenIconByItemId instanceof FolderIcon) {
                ((FolderInfo) homescreenIconByItemId.getTag()).remove((ShortcutInfo) itemInfo, true);
            } else {
                this.mWorkspace.removeWorkspaceItem(view);
            }
            if (z) {
                getModelWriter().deleteItemFromDatabase(itemInfo);
            }
        } else if (itemInfo instanceof FolderInfo) {
            FolderInfo folderInfo = (FolderInfo) itemInfo;
            if (view instanceof FolderIcon) {
                ((FolderIcon) view).removeListeners();
            }
            this.mWorkspace.removeWorkspaceItem(view);
            if (z) {
                getModelWriter().deleteFolderAndContentsFromDatabase(folderInfo);
            }
        } else if (!(itemInfo instanceof LauncherAppWidgetInfo)) {
            return false;
        } else {
            LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) itemInfo;
            this.mWorkspace.removeWorkspaceItem(view);
            if (z) {
                deleteWidgetInfo(launcherAppWidgetInfo);
            }
        }
        return true;
    }

    private void deleteWidgetInfo(final LauncherAppWidgetInfo launcherAppWidgetInfo) {
        final LauncherAppWidgetHost appWidgetHost = getAppWidgetHost();
        if (appWidgetHost != null && !launcherAppWidgetInfo.isCustomWidget() && launcherAppWidgetInfo.isWidgetIdAllocated()) {
            new AsyncTask<Void, Void, Void>() {
                public Void doInBackground(Void... voidArr) {
                    appWidgetHost.deleteAppWidgetId(launcherAppWidgetInfo.appWidgetId);
                    return null;
                }
            }.executeOnExecutor(Utilities.THREAD_POOL_EXECUTOR, new Void[0]);
        }
        getModelWriter().deleteItemFromDatabase(launcherAppWidgetInfo);
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return keyEvent.getKeyCode() == 3 || super.dispatchKeyEvent(keyEvent);
    }

    public void onBackPressed() {
        if (this.mLauncherCallbacks != null && this.mLauncherCallbacks.handleBackPressed()) {
            return;
        }
        if (this.mDragController.isDragging()) {
            this.mDragController.cancelDrag();
            return;
        }
        UserEventDispatcher userEventDispatcher = getUserEventDispatcher();
        AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(this);
        if (topOpenView != null) {
            if (topOpenView.getActiveTextView() != null) {
                topOpenView.getActiveTextView().dispatchBackKey();
            } else {
                if (topOpenView instanceof PopupContainerWithArrow) {
                    userEventDispatcher.logActionCommand(1, topOpenView.getExtendedTouchView(), 9);
                } else if (topOpenView instanceof Folder) {
                    userEventDispatcher.logActionCommand(1, (View) ((Folder) topOpenView).getFolderIcon(), 3);
                }
                topOpenView.close(true);
            }
        } else if (isAppsViewVisible()) {
            userEventDispatcher.logActionCommand(1, 4);
            showWorkspace(true);
        } else if (isWidgetsViewVisible()) {
            userEventDispatcher.logActionCommand(1, 5);
            showOverviewMode(true);
        } else if (this.mWorkspace.isInOverviewMode()) {
            userEventDispatcher.logActionCommand(1, 6);
            showWorkspace(true);
        } else {
            this.mWorkspace.exitWidgetResizeMode();
            this.mWorkspace.showOutlinesTemporarily();
            int currentPage = this.mWorkspace.getCurrentPage() - 1;
            if (currentPage >= 0) {
                this.mWorkspace.snapToPage(currentPage);
            } else if (!moveTaskToBack(false)) {
                super.onBackPressed();
            }
        }
    }

    public void onClick(View view) {
        if (view.getWindowToken() == null || !this.mWorkspace.isFinishedSwitchingState()) {
            return;
        }
        if (view instanceof Workspace) {
            if (this.mWorkspace.isInOverviewMode()) {
                getUserEventDispatcher().logActionOnContainer(0, 0, 6, this.mWorkspace.getCurrentPage());
                showWorkspace(true);
            }
        } else if (view instanceof CellLayout) {
            if (this.mWorkspace.isInOverviewMode()) {
                int indexOfChild = this.mWorkspace.indexOfChild(view);
                getUserEventDispatcher().logActionOnContainer(0, 0, 6, indexOfChild);
                this.mWorkspace.snapToPageFromOverView(indexOfChild);
                showWorkspace(true);
            }
        } else {
            Object tag = view.getTag();
            if (tag instanceof ShortcutInfo) {
                onClickAppShortcut(view);
            } else if (tag instanceof FolderInfo) {
                if (view instanceof FolderIcon) {
                    onClickFolderIcon(view);
                }
            } else if ((view instanceof PageIndicator) || (view == this.mAllAppsButton && this.mAllAppsButton != null)) {
                onClickAllAppsButton(view);
            } else if (tag instanceof AppInfo) {
                startAppShortcutOrInfoActivity(view);
            } else if ((tag instanceof LauncherAppWidgetInfo) && (view instanceof PendingAppWidgetHostView)) {
                onClickPendingWidget((PendingAppWidgetHostView) view);
            }
        }
    }

    public void onClickPendingWidget(PendingAppWidgetHostView pendingAppWidgetHostView) {
        boolean z = false;
        if (this.mIsSafeModeEnabled) {
            Toast.makeText(this, C0622R.string.safemode_widget_error, 0).show();
            return;
        }
        LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) pendingAppWidgetHostView.getTag();
        if (pendingAppWidgetHostView.isReadyForClickSetup()) {
            LauncherAppWidgetProviderInfo findProvider = this.mAppWidgetManager.findProvider(launcherAppWidgetInfo.providerName, launcherAppWidgetInfo.user);
            if (findProvider != null) {
                WidgetAddFlowHandler widgetAddFlowHandler = new WidgetAddFlowHandler((AppWidgetProviderInfo) findProvider);
                if (!launcherAppWidgetInfo.hasRestoreFlag(1)) {
                    widgetAddFlowHandler.startConfigActivity(this, launcherAppWidgetInfo, 13);
                } else if (launcherAppWidgetInfo.hasRestoreFlag(16)) {
                    widgetAddFlowHandler.startBindFlow(this, launcherAppWidgetInfo.appWidgetId, launcherAppWidgetInfo, 12);
                }
            }
        } else {
            String packageName = launcherAppWidgetInfo.providerName.getPackageName();
            if (launcherAppWidgetInfo.installProgress >= 0) {
                z = true;
            }
            onClickPendingAppItem(pendingAppWidgetHostView, packageName, z);
        }
    }

    /* access modifiers changed from: protected */
    public void onClickAllAppsButton(View view) {
        if (!isAppsViewVisible()) {
            getUserEventDispatcher().logActionOnControl(0, 1);
            showAppsView(true, true, false);
            return;
        }
        showWorkspace(true);
    }

    /* access modifiers changed from: protected */
    public void onLongClickAllAppsButton(View view) {
        if (!isAppsViewVisible()) {
            getUserEventDispatcher().logActionOnControl(1, 1);
            showAppsView(true, true, true);
            return;
        }
        showWorkspace(true);
    }

    private void onClickPendingAppItem(final View view, final String str, boolean z) {
        if (z) {
            startMarketIntentForPackage(view, str);
        } else {
            new Builder(this).setTitle(C0622R.string.abandoned_promises_title).setMessage(C0622R.string.abandoned_promise_explanation).setPositiveButton(C0622R.string.abandoned_search, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Launcher.this.startMarketIntentForPackage(view, str);
                }
            }).setNeutralButton(C0622R.string.abandoned_clean_this, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Launcher.this.mWorkspace.removeAbandonedPromise(str, Process.myUserHandle());
                }
            }).create().show();
        }
    }

    /* access modifiers changed from: private */
    public void startMarketIntentForPackage(View view, String str) {
        if (startActivitySafely(view, PackageManagerHelper.getMarketIntent(str), (ItemInfo) view.getTag()) && (view instanceof BubbleTextView)) {
            this.mWaitingForResume = (BubbleTextView) view;
            this.mWaitingForResume.setStayPressed(true);
        }
    }

    /* access modifiers changed from: protected */
    public void onClickAppShortcut(View view) {
        Object tag = view.getTag();
        if (tag instanceof ShortcutInfo) {
            ShortcutInfo shortcutInfo = (ShortcutInfo) tag;
            if (shortcutInfo.isDisabled == 0 || (shortcutInfo.isDisabled & -5 & -9) == 0) {
                if ((view instanceof BubbleTextView) && shortcutInfo.hasPromiseIconUi()) {
                    String packageName = shortcutInfo.intent.getComponent() != null ? shortcutInfo.intent.getComponent().getPackageName() : shortcutInfo.intent.getPackage();
                    if (!TextUtils.isEmpty(packageName)) {
                        onClickPendingAppItem(view, packageName, shortcutInfo.hasStatusFlag(4));
                        return;
                    }
                }
                startAppShortcutOrInfoActivity(view);
            } else if (!TextUtils.isEmpty(shortcutInfo.disabledMessage)) {
                Toast.makeText(this, shortcutInfo.disabledMessage, 0).show();
            } else {
                int i = C0622R.string.activity_not_available;
                if ((shortcutInfo.isDisabled & 1) != 0) {
                    i = C0622R.string.safemode_shortcut_error;
                } else if (!((shortcutInfo.isDisabled & 16) == 0 && (shortcutInfo.isDisabled & 32) == 0)) {
                    i = C0622R.string.shortcut_not_available;
                }
                Toast.makeText(this, i, 0).show();
            }
        } else {
            throw new IllegalArgumentException("Input must be a Shortcut");
        }
    }

    private void startAppShortcutOrInfoActivity(View view) {
        Intent intent;
        ItemInfo itemInfo = (ItemInfo) view.getTag();
        if (itemInfo instanceof PromiseAppInfo) {
            intent = ((PromiseAppInfo) itemInfo).getMarketIntent();
        } else {
            intent = itemInfo.getIntent();
        }
        if (intent != null) {
            boolean startActivitySafely = startActivitySafely(view, intent, itemInfo);
            getUserEventDispatcher().logAppLaunch(view, intent, itemInfo.user);
            if (startActivitySafely && (view instanceof BubbleTextView)) {
                this.mWaitingForResume = (BubbleTextView) view;
                this.mWaitingForResume.setStayPressed(true);
                return;
            }
            return;
        }
        throw new IllegalArgumentException("Input must have a valid intent");
    }

    /* access modifiers changed from: protected */
    public void onClickFolderIcon(View view) {
        if (view instanceof FolderIcon) {
            Folder folder = ((FolderIcon) view).getFolder();
            if (!folder.isOpen() && !folder.isDestroyed()) {
                folder.animateOpen();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("Input must be a FolderIcon");
    }

    public void onClickAddWidgetButton(View view) {
        if (this.mIsSafeModeEnabled) {
            Toast.makeText(this, C0622R.string.safemode_widget_error, 0).show();
        } else {
            showWidgetsView(true, true);
        }
    }

    public void onClickWallpaperPicker(View view) {
        Bundle bundle;
        if (!Utilities.isWallpaperAllowed(this)) {
            Toast.makeText(this, C0622R.string.msg_disabled_by_admin, 0).show();
            return;
        }
        float wallpaperOffsetForScroll = this.mWorkspace.mWallpaperOffset.wallpaperOffsetForScroll(this.mWorkspace.getScrollForPage(this.mWorkspace.getPageNearestToCenterOfScreen()));
        setWaitingForResult(new PendingRequestArgs(new ItemInfo()));
        Intent putExtra = new Intent("android.intent.action.SET_WALLPAPER").putExtra(Utilities.EXTRA_WALLPAPER_OFFSET, wallpaperOffsetForScroll);
        String string = getString(C0622R.string.wallpaper_picker_package);
        boolean z = !TextUtils.isEmpty(string);
        if (z) {
            try {
                if (getPackageManager().getApplicationInfo(string, 0).enabled) {
                    putExtra.setPackage(string);
                }
            } catch (NameNotFoundException unused) {
            }
        }
        putExtra.setSourceBounds(getViewBounds(view));
        if (z) {
            try {
                bundle = getActivityLaunchOptions(view);
            } catch (ActivityNotFoundException unused2) {
                setWaitingForResult(null);
                Toast.makeText(this, C0622R.string.activity_not_found, 0).show();
            }
        } else {
            bundle = null;
        }
        startActivityForResult(putExtra, 10, bundle);
    }

    public void onClickSettingsButton(View view) {
        Intent intent = new Intent("android.intent.action.APPLICATION_PREFERENCES").setPackage(getPackageName());
        intent.setSourceBounds(getViewBounds(view));
        intent.addFlags(268435456);
        startActivity(intent, getActivityLaunchOptions(view));
    }

    public void onAccessibilityStateChanged(boolean z) {
        this.mDragLayer.onAccessibilityStateChanged(z);
    }

    public void onDragStarted() {
        if (isOnCustomContent()) {
            moveWorkspaceToDefaultScreen();
        }
    }

    /* access modifiers changed from: protected */
    public void onInteractionEnd() {
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onInteractionEnd();
        }
    }

    /* access modifiers changed from: protected */
    public void onInteractionBegin() {
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onInteractionBegin();
        }
    }

    public void updateInteraction(com.android.launcher3.Workspace.State state, com.android.launcher3.Workspace.State state2) {
        boolean z = false;
        boolean z2 = state != com.android.launcher3.Workspace.State.NORMAL;
        if (state2 != com.android.launcher3.Workspace.State.NORMAL) {
            z = true;
        }
        if (z) {
            onInteractionBegin();
        } else if (z2) {
            onInteractionEnd();
        }
    }

    private void startShortcutIntentSafely(Intent intent, Bundle bundle, ItemInfo itemInfo) {
        VmPolicy vmPolicy;
        try {
            vmPolicy = StrictMode.getVmPolicy();
            StrictMode.setVmPolicy(new VmPolicy.Builder().detectAll().penaltyLog().build());
            if (itemInfo.itemType == 6) {
                String deepShortcutId = ((ShortcutInfo) itemInfo).getDeepShortcutId();
                DeepShortcutManager.getInstance(this).startShortcut(intent.getPackage(), deepShortcutId, intent, bundle, itemInfo.user);
            } else {
                startActivity(intent, bundle);
            }
            StrictMode.setVmPolicy(vmPolicy);
        } catch (SecurityException e) {
            if (intent.getComponent() != null || !"android.intent.action.CALL".equals(intent.getAction()) || checkSelfPermission("android.permission.CALL_PHONE") == 0) {
                throw e;
            }
            setWaitingForResult(PendingRequestArgs.forIntent(14, intent, itemInfo));
            requestPermissions(new String[]{"android.permission.CALL_PHONE"}, 14);
        } catch (Throwable th) {
            StrictMode.setVmPolicy(vmPolicy);
            throw th;
        }
    }

    @TargetApi(23)
    public Bundle getActivityLaunchOptions(View view) {
        int i;
        int i2;
        if (Utilities.ATLEAST_MARSHMALLOW) {
            int measuredWidth = view.getMeasuredWidth();
            int measuredHeight = view.getMeasuredHeight();
            int i3 = 0;
            if (view instanceof BubbleTextView) {
                Drawable icon = ((BubbleTextView) view).getIcon();
                if (icon != null) {
                    Rect bounds = icon.getBounds();
                    i3 = (measuredWidth - bounds.width()) / 2;
                    i2 = view.getPaddingTop();
                    i = bounds.width();
                    measuredHeight = bounds.height();
                    return ActivityOptions.makeClipRevealAnimation(view, i3, i2, i, measuredHeight).toBundle();
                }
            }
            i = measuredWidth;
            i2 = 0;
            return ActivityOptions.makeClipRevealAnimation(view, i3, i2, i, measuredHeight).toBundle();
        } else if (Utilities.ATLEAST_LOLLIPOP_MR1) {
            return ActivityOptions.makeCustomAnimation(this, C0622R.C0623anim.task_open_enter, C0622R.C0623anim.no_anim).toBundle();
        } else {
            return null;
        }
    }

    public Rect getViewBounds(View view) {
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        return new Rect(iArr[0], iArr[1], iArr[0] + view.getWidth(), iArr[1] + view.getHeight());
    }

    public void startVirtualActivity(Intent intent, Bundle bundle, int i) {
        startActivity(intent, bundle);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:36:?, code lost:
        com.android.launcher3.compat.LauncherAppsCompat.getInstance(r5).startActivityForProfile(r7.getComponent(), r3, r7.getSourceBounds(), r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0075, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0076, code lost:
        android.widget.Toast.makeText(r5, com.android.launcher3.C0622R.string.activity_not_found, 0).show();
        r0 = TAG;
        r2 = new java.lang.StringBuilder();
        r2.append("Unable to launch. tag=");
        r2.append(r8);
        r2.append(" intent=");
        r2.append(r7);
        android.util.Log.e(r0, r2.toString(), r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x009d, code lost:
        return false;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:35:0x0065 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean startActivitySafely(android.view.View r6, android.content.Intent r7, com.android.launcher3.ItemInfo r8) {
        /*
            r5 = this;
            boolean r0 = r5.mIsSafeModeEnabled
            r1 = 0
            if (r0 == 0) goto L_0x0015
            boolean r0 = com.android.launcher3.Utilities.isSystemApp(r5, r7)
            if (r0 != 0) goto L_0x0015
            int r6 = com.android.launcher3.C0622R.string.safemode_shortcut_error
            android.widget.Toast r6 = android.widget.Toast.makeText(r5, r6, r1)
            r6.show()
            return r1
        L_0x0015:
            r0 = 1
            if (r6 == 0) goto L_0x0022
            java.lang.String r2 = "com.android.launcher3.intent.extra.shortcut.INGORE_LAUNCH_ANIMATION"
            boolean r2 = r7.hasExtra(r2)
            if (r2 != 0) goto L_0x0022
            r2 = 1
            goto L_0x0023
        L_0x0022:
            r2 = 0
        L_0x0023:
            r3 = 0
            if (r2 == 0) goto L_0x002b
            android.os.Bundle r2 = r5.getActivityLaunchOptions(r6)
            goto L_0x002c
        L_0x002b:
            r2 = r3
        L_0x002c:
            if (r8 != 0) goto L_0x002f
            goto L_0x0031
        L_0x002f:
            android.os.UserHandle r3 = r8.user
        L_0x0031:
            r4 = 268435456(0x10000000, float:2.5243549E-29)
            r7.addFlags(r4)
            if (r6 == 0) goto L_0x003f
            android.graphics.Rect r6 = r5.getViewBounds(r6)
            r7.setSourceBounds(r6)
        L_0x003f:
            boolean r6 = com.android.launcher3.Utilities.ATLEAST_MARSHMALLOW     // Catch:{ Throwable -> 0x0065 }
            if (r6 == 0) goto L_0x005d
            boolean r6 = r8 instanceof com.android.launcher3.ShortcutInfo     // Catch:{ Throwable -> 0x0065 }
            if (r6 == 0) goto L_0x005d
            int r6 = r8.itemType     // Catch:{ Throwable -> 0x0065 }
            if (r6 == r0) goto L_0x0050
            int r6 = r8.itemType     // Catch:{ Throwable -> 0x0065 }
            r4 = 6
            if (r6 != r4) goto L_0x005d
        L_0x0050:
            r6 = r8
            com.android.launcher3.ShortcutInfo r6 = (com.android.launcher3.ShortcutInfo) r6     // Catch:{ Throwable -> 0x0065 }
            boolean r6 = r6.isPromise()     // Catch:{ Throwable -> 0x0065 }
            if (r6 != 0) goto L_0x005d
            r5.startShortcutIntentSafely(r7, r2, r8)     // Catch:{ Throwable -> 0x0065 }
            goto L_0x0064
        L_0x005d:
            int r6 = com.android.launcher3.compat.UserManagerCompat.toUserId(r3)     // Catch:{ Throwable -> 0x0065 }
            r5.startVirtualActivity(r7, r2, r6)     // Catch:{ Throwable -> 0x0065 }
        L_0x0064:
            return r0
        L_0x0065:
            com.android.launcher3.compat.LauncherAppsCompat r6 = com.android.launcher3.compat.LauncherAppsCompat.getInstance(r5)     // Catch:{ ActivityNotFoundException | SecurityException -> 0x0075 }
            android.content.ComponentName r0 = r7.getComponent()     // Catch:{ ActivityNotFoundException | SecurityException -> 0x0075 }
            android.graphics.Rect r4 = r7.getSourceBounds()     // Catch:{ ActivityNotFoundException | SecurityException -> 0x0075 }
            r6.startActivityForProfile(r0, r3, r4, r2)     // Catch:{ ActivityNotFoundException | SecurityException -> 0x0075 }
            goto L_0x009d
        L_0x0075:
            r6 = move-exception
            int r0 = com.android.launcher3.C0622R.string.activity_not_found
            android.widget.Toast r0 = android.widget.Toast.makeText(r5, r0, r1)
            r0.show()
            java.lang.String r0 = "Launcher"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Unable to launch. tag="
            r2.append(r3)
            r2.append(r8)
            java.lang.String r8 = " intent="
            r2.append(r8)
            r2.append(r7)
            java.lang.String r7 = r2.toString()
            android.util.Log.e(r0, r7, r6)
        L_0x009d:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Launcher.startActivitySafely(android.view.View, android.content.Intent, com.android.launcher3.ItemInfo):boolean");
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        this.mLastDispatchTouchEventX = motionEvent.getX();
        return super.dispatchTouchEvent(motionEvent);
    }

    public boolean onLongClick(View view) {
        View view2;
        if (!isDraggingEnabled() || isWorkspaceLocked() || this.mState != State.WORKSPACE) {
            return false;
        }
        if ((view instanceof PageIndicatorCaretLandscape) || (view == this.mAllAppsButton && this.mAllAppsButton != null)) {
            onLongClickAllAppsButton(view);
            return true;
        }
        boolean shouldIgnoreLongPressToOverview = this.mDeviceProfile.shouldIgnoreLongPressToOverview(this.mLastDispatchTouchEventX);
        if (!(view instanceof Workspace)) {
            CellInfo cellInfo = null;
            if (view.getTag() instanceof ItemInfo) {
                CellInfo cellInfo2 = new CellInfo(view, (ItemInfo) view.getTag());
                view2 = cellInfo2.cell;
                this.mPendingRequestArgs = null;
                cellInfo = cellInfo2;
            } else {
                view2 = null;
            }
            if (!this.mDragController.isDragging()) {
                if (view2 == null) {
                    if (this.mWorkspace.isInOverviewMode()) {
                        this.mWorkspace.startReordering(view);
                        getUserEventDispatcher().logActionOnContainer(1, 0, 6);
                    } else if (shouldIgnoreLongPressToOverview) {
                        return false;
                    } else {
                        getUserEventDispatcher().logActionOnContainer(1, 0, 1, this.mWorkspace.getCurrentPage());
                        showOverviewMode(true);
                    }
                    this.mWorkspace.performHapticFeedback(0, 1);
                } else if (!(view2 instanceof Folder)) {
                    this.mWorkspace.startDrag(cellInfo, new DragOptions());
                }
            }
            return true;
        } else if (this.mWorkspace.isInOverviewMode() || this.mWorkspace.isTouchActive() || shouldIgnoreLongPressToOverview) {
            return false;
        } else {
            getUserEventDispatcher().logActionOnContainer(1, 0, 1, this.mWorkspace.getCurrentPage());
            showOverviewMode(true);
            this.mWorkspace.performHapticFeedback(0, 1);
            return true;
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean isHotseatLayout(View view) {
        return this.mHotseat != null && view != null && (view instanceof CellLayout) && view == this.mHotseat.getLayout();
    }

    public CellLayout getCellLayout(long j, long j2) {
        if (j != -101) {
            return this.mWorkspace.getScreenWithId(j2);
        }
        if (this.mHotseat != null) {
            return this.mHotseat.getLayout();
        }
        return null;
    }

    public boolean isAllAppsVisible() {
        return isAppsViewVisible();
    }

    public boolean isAppsViewVisible() {
        return this.mState == State.APPS || this.mOnResumeState == State.APPS;
    }

    public boolean isWidgetsViewVisible() {
        return this.mState == State.WIDGETS || this.mOnResumeState == State.WIDGETS;
    }

    public void onTrimMemory(int i) {
        super.onTrimMemory(i);
        if (i >= 20) {
            SQLiteDatabase.releaseMemory();
        }
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onTrimMemory(i);
        }
    }

    public boolean showWorkspace(boolean z) {
        return showWorkspace(z, null);
    }

    public boolean showWorkspace(boolean z, Runnable runnable) {
        boolean z2 = (this.mState == State.WORKSPACE && this.mWorkspace.getState() == com.android.launcher3.Workspace.State.NORMAL) ? false : true;
        if (z2 || this.mAllAppsController.isTransitioning()) {
            this.mWorkspace.setVisibility(0);
            this.mStateTransitionAnimation.startAnimationToWorkspace(this.mState, this.mWorkspace.getState(), com.android.launcher3.Workspace.State.NORMAL, z, runnable);
            if (this.mAllAppsButton != null) {
                this.mAllAppsButton.requestFocus();
            }
        }
        setState(State.WORKSPACE);
        if (z2) {
            getWindow().getDecorView().sendAccessibilityEvent(32);
        }
        return z2;
    }

    public void showOverviewMode(boolean z) {
        showOverviewMode(z, false);
    }

    /* access modifiers changed from: 0000 */
    public void showOverviewMode(boolean z, boolean z2) {
        Runnable r5 = z2 ? new Runnable() {
            public void run() {
                Launcher.this.mOverviewPanel.requestFocusFromTouch();
            }
        } : null;
        this.mWorkspace.setVisibility(0);
        this.mStateTransitionAnimation.startAnimationToWorkspace(this.mState, this.mWorkspace.getState(), com.android.launcher3.Workspace.State.OVERVIEW, z, r5);
        setState(State.WORKSPACE);
        this.mWorkspace.requestDisallowInterceptTouchEvent(z);
    }

    private void setState(State state) {
        this.mState = state;
        updateSoftInputMode();
    }

    public void showAppsView(boolean z, boolean z2, boolean z3) {
        markAppsViewShown();
        if (z2) {
            tryAndUpdatePredictedApps();
        }
        showAppsOrWidgets(State.APPS, z, z3);
    }

    /* access modifiers changed from: 0000 */
    public void showWidgetsView(boolean z, boolean z2) {
        if (z2) {
            this.mWidgetsView.scrollToTop();
        }
        showAppsOrWidgets(State.WIDGETS, z, false);
        this.mWidgetsView.post(new Runnable() {
            public void run() {
                Launcher.this.mWidgetsView.requestFocus();
            }
        });
    }

    private boolean showAppsOrWidgets(State state, boolean z, boolean z2) {
        if (this.mState != State.WORKSPACE && this.mState != State.APPS_SPRING_LOADED && this.mState != State.WIDGETS_SPRING_LOADED && (this.mState != State.APPS || !this.mAllAppsController.isTransitioning())) {
            return false;
        }
        if (state != State.APPS && state != State.WIDGETS) {
            return false;
        }
        if (this.mExitSpringLoadedModeRunnable != null) {
            this.mHandler.removeCallbacks(this.mExitSpringLoadedModeRunnable);
            this.mExitSpringLoadedModeRunnable = null;
        }
        if (state == State.APPS) {
            this.mStateTransitionAnimation.startAnimationToAllApps(z, z2);
        } else {
            this.mStateTransitionAnimation.startAnimationToWidgets(z);
        }
        setState(state);
        AbstractFloatingView.closeAllOpenViews(this);
        getWindow().getDecorView().sendAccessibilityEvent(32);
        return true;
    }

    public Animator startWorkspaceStateChangeAnimation(com.android.launcher3.Workspace.State state, boolean z, AnimationLayerSet animationLayerSet) {
        com.android.launcher3.Workspace.State state2 = this.mWorkspace.getState();
        Animator stateWithAnimation = this.mWorkspace.setStateWithAnimation(state, z, animationLayerSet);
        updateInteraction(state2, state);
        return stateWithAnimation;
    }

    public void enterSpringLoadedDragMode() {
        if (!isStateSpringLoaded()) {
            this.mStateTransitionAnimation.startAnimationToWorkspace(this.mState, this.mWorkspace.getState(), com.android.launcher3.Workspace.State.SPRING_LOADED, true, null);
            setState(State.WORKSPACE_SPRING_LOADED);
        }
    }

    public void exitSpringLoadedDragModeDelayed(final boolean z, int i, final Runnable runnable) {
        if (isStateSpringLoaded()) {
            if (this.mExitSpringLoadedModeRunnable != null) {
                this.mHandler.removeCallbacks(this.mExitSpringLoadedModeRunnable);
            }
            this.mExitSpringLoadedModeRunnable = new Runnable() {
                public void run() {
                    if (z) {
                        Launcher.this.mWidgetsView.setVisibility(8);
                        Launcher.this.showWorkspace(true, runnable);
                    } else {
                        Launcher.this.exitSpringLoadedDragMode();
                    }
                    Launcher.this.mExitSpringLoadedModeRunnable = null;
                }
            };
            this.mHandler.postDelayed(this.mExitSpringLoadedModeRunnable, (long) i);
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean isStateSpringLoaded() {
        return this.mState == State.WORKSPACE_SPRING_LOADED || this.mState == State.APPS_SPRING_LOADED || this.mState == State.WIDGETS_SPRING_LOADED;
    }

    public void exitSpringLoadedDragMode() {
        if (this.mState == State.APPS_SPRING_LOADED) {
            showAppsView(true, false, false);
        } else if (this.mState == State.WIDGETS_SPRING_LOADED) {
            showWidgetsView(true, false);
        } else if (this.mState == State.WORKSPACE_SPRING_LOADED) {
            showWorkspace(true);
        }
    }

    public void tryAndUpdatePredictedApps() {
        if (this.mLauncherCallbacks != null) {
            List predictedApps = this.mLauncherCallbacks.getPredictedApps();
            if (predictedApps != null) {
                this.mAppsView.setPredictedApps(predictedApps);
            }
        }
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        boolean dispatchPopulateAccessibilityEvent = super.dispatchPopulateAccessibilityEvent(accessibilityEvent);
        List text = accessibilityEvent.getText();
        text.clear();
        if (this.mState == State.APPS) {
            text.add(getString(C0622R.string.all_apps_button_label));
        } else if (this.mState == State.WIDGETS) {
            text.add(getString(C0622R.string.widget_button_text));
        } else if (this.mWorkspace != null) {
            text.add(this.mWorkspace.getCurrentPageDescription());
        } else {
            text.add(getString(C0622R.string.all_apps_home_button_label));
        }
        return dispatchPopulateAccessibilityEvent;
    }

    /* access modifiers changed from: 0000 */
    public boolean waitUntilResume(Runnable runnable) {
        if (!this.mPaused) {
            return false;
        }
        if (runnable instanceof RunnableWithId) {
            do {
            } while (this.mBindOnResumeCallbacks.remove(runnable));
        }
        this.mBindOnResumeCallbacks.add(runnable);
        return true;
    }

    public void addOnResumeCallback(Runnable runnable) {
        this.mOnResumeCallbacks.add(runnable);
    }

    public boolean setLoadOnResume() {
        if (!this.mPaused) {
            return false;
        }
        this.mOnResumeNeedsLoad = true;
        return true;
    }

    public int getCurrentWorkspaceScreen() {
        if (this.mWorkspace != null) {
            return this.mWorkspace.getCurrentPage();
        }
        return 0;
    }

    public void clearPendingBinds() {
        this.mBindOnResumeCallbacks.clear();
        if (this.mPendingExecutor != null) {
            this.mPendingExecutor.markCompleted();
            this.mPendingExecutor = null;
        }
    }

    public void startBinding() {
        AbstractFloatingView.closeAllOpenViews(this);
        setWorkspaceLoading(true);
        this.mWorkspace.clearDropTargets();
        this.mWorkspace.removeAllWorkspaceScreens();
        if (this.mHotseat != null) {
            this.mHotseat.resetLayout();
        }
    }

    public void bindScreens(ArrayList<Long> arrayList) {
        if (FeatureFlags.QSB_ON_FIRST_SCREEN && arrayList.indexOf(Long.valueOf(0)) != 0) {
            arrayList.remove(Long.valueOf(0));
            arrayList.add(0, Long.valueOf(0));
            LauncherModel.updateWorkspaceScreenOrder(this, arrayList);
        } else if (!FeatureFlags.QSB_ON_FIRST_SCREEN && arrayList.isEmpty()) {
            this.mWorkspace.addExtraEmptyScreen();
        }
        bindAddScreens(arrayList);
        if (hasCustomContentToLeft()) {
            this.mWorkspace.createCustomContentContainer();
            populateCustomContentContainer();
        }
        this.mWorkspace.unlockWallpaperFromDefaultPageOnNextLayout();
    }

    private void bindAddScreens(ArrayList<Long> arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            long longValue = ((Long) arrayList.get(i)).longValue();
            if (!FeatureFlags.QSB_ON_FIRST_SCREEN || longValue != 0) {
                this.mWorkspace.insertNewWorkspaceScreenBeforeEmptyScreen(longValue);
            }
        }
    }

    public void bindAppsAdded(final ArrayList<Long> arrayList, final ArrayList<ItemInfo> arrayList2, final ArrayList<ItemInfo> arrayList3) {
        if (!waitUntilResume(new Runnable() {
            public void run() {
                Launcher.this.bindAppsAdded(arrayList, arrayList2, arrayList3);
            }
        })) {
            if (arrayList != null) {
                bindAddScreens(arrayList);
            }
            if (arrayList2 != null && !arrayList2.isEmpty()) {
                bindItems(arrayList2, false);
            }
            if (arrayList3 != null && !arrayList3.isEmpty()) {
                bindItems(arrayList3, true);
            }
            this.mWorkspace.removeExtraEmptyScreen(false, false);
        }
    }

    public void bindItems(List<ItemInfo> list, boolean z) {
        View view;
        final List<ItemInfo> list2 = list;
        final boolean z2 = z;
        if (!waitUntilResume(new Runnable() {
            public void run() {
                Launcher.this.bindItems(list2, z2);
            }
        })) {
            final AnimatorSet createAnimatorSet = LauncherAnimUtils.createAnimatorSet();
            final ArrayList arrayList = new ArrayList();
            boolean z3 = z2 && canRunNewAppsAnimation();
            Workspace workspace = this.mWorkspace;
            int size = list.size();
            long j = -1;
            for (int i = 0; i < size; i++) {
                ItemInfo itemInfo = (ItemInfo) list2.get(i);
                if (itemInfo.container != -101 || this.mHotseat != null) {
                    int i2 = itemInfo.itemType;
                    if (i2 != 4) {
                        if (i2 != 6) {
                            switch (i2) {
                                case 0:
                                case 1:
                                    break;
                                case 2:
                                    view = FolderIcon.fromXml(C0622R.layout.folder_icon, this, (ViewGroup) workspace.getChildAt(workspace.getCurrentPage()), (FolderInfo) itemInfo);
                                    break;
                                default:
                                    throw new RuntimeException("Invalid Item Type");
                            }
                        }
                        view = createShortcut((ShortcutInfo) itemInfo);
                    } else {
                        view = inflateAppWidget((LauncherAppWidgetInfo) itemInfo);
                        if (view == null) {
                        }
                    }
                    if (itemInfo.container == -100) {
                        CellLayout screenWithId = this.mWorkspace.getScreenWithId(itemInfo.screenId);
                        if (screenWithId != null && screenWithId.isOccupied(itemInfo.cellX, itemInfo.cellY)) {
                            Object tag = screenWithId.getChildAt(itemInfo.cellX, itemInfo.cellY).getTag();
                            StringBuilder sb = new StringBuilder();
                            sb.append("Collision while binding workspace item: ");
                            sb.append(itemInfo);
                            sb.append(". Collides with ");
                            sb.append(tag);
                            Log.d(TAG, sb.toString());
                            getModelWriter().deleteItemFromDatabase(itemInfo);
                        }
                    }
                    workspace.addInScreenFromBind(view, itemInfo);
                    if (z3) {
                        view.setAlpha(0.0f);
                        view.setScaleX(0.0f);
                        view.setScaleY(0.0f);
                        arrayList.add(createNewAppBounceAnimation(view, i));
                        j = itemInfo.screenId;
                    }
                }
            }
            if (z3 && j > -1) {
                long screenIdForPageIndex = this.mWorkspace.getScreenIdForPageIndex(this.mWorkspace.getNextPage());
                final int pageIndexForScreenId = this.mWorkspace.getPageIndexForScreenId(j);
                final C056024 r7 = new Runnable() {
                    public void run() {
                        createAnimatorSet.playTogether(arrayList);
                        createAnimatorSet.start();
                    }
                };
                if (j != screenIdForPageIndex) {
                    this.mWorkspace.postDelayed(new Runnable() {
                        public void run() {
                            if (Launcher.this.mWorkspace != null) {
                                Launcher.this.mWorkspace.snapToPage(pageIndexForScreenId);
                                Launcher.this.mWorkspace.postDelayed(r7, 500);
                            }
                        }
                    }, 500);
                } else {
                    this.mWorkspace.postDelayed(r7, 500);
                }
            }
            workspace.requestLayout();
        }
    }

    public void bindAppWidget(LauncherAppWidgetInfo launcherAppWidgetInfo) {
        View inflateAppWidget = inflateAppWidget(launcherAppWidgetInfo);
        if (inflateAppWidget != null) {
            this.mWorkspace.addInScreen(inflateAppWidget, launcherAppWidgetInfo);
            this.mWorkspace.requestLayout();
        }
    }

    private View inflateAppWidget(LauncherAppWidgetInfo launcherAppWidgetInfo) {
        LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo;
        AppWidgetHostView appWidgetHostView;
        if (this.mIsSafeModeEnabled) {
            PendingAppWidgetHostView pendingAppWidgetHostView = new PendingAppWidgetHostView(this, launcherAppWidgetInfo, this.mIconCache, true);
            prepareAppWidget(pendingAppWidgetHostView, launcherAppWidgetInfo);
            return pendingAppWidgetHostView;
        }
        if (launcherAppWidgetInfo.hasRestoreFlag(2)) {
            launcherAppWidgetProviderInfo = null;
        } else if (launcherAppWidgetInfo.hasRestoreFlag(1)) {
            launcherAppWidgetProviderInfo = this.mAppWidgetManager.findProvider(launcherAppWidgetInfo.providerName, launcherAppWidgetInfo.user);
        } else {
            launcherAppWidgetProviderInfo = this.mAppWidgetManager.getLauncherAppWidgetInfo(launcherAppWidgetInfo.appWidgetId);
        }
        if (!launcherAppWidgetInfo.hasRestoreFlag(2) && launcherAppWidgetInfo.restoreStatus != 0) {
            if (launcherAppWidgetProviderInfo == null) {
                getModelWriter().deleteItemFromDatabase(launcherAppWidgetInfo);
                return null;
            }
            int i = 4;
            if (launcherAppWidgetInfo.hasRestoreFlag(1)) {
                if (!launcherAppWidgetInfo.hasRestoreFlag(16)) {
                    launcherAppWidgetInfo.appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
                    launcherAppWidgetInfo.restoreStatus = 16 | launcherAppWidgetInfo.restoreStatus;
                    PendingAddWidgetInfo pendingAddWidgetInfo = new PendingAddWidgetInfo(launcherAppWidgetProviderInfo);
                    pendingAddWidgetInfo.spanX = launcherAppWidgetInfo.spanX;
                    pendingAddWidgetInfo.spanY = launcherAppWidgetInfo.spanY;
                    pendingAddWidgetInfo.minSpanX = launcherAppWidgetInfo.minSpanX;
                    pendingAddWidgetInfo.minSpanY = launcherAppWidgetInfo.minSpanY;
                    Bundle defaultOptionsForWidget = WidgetHostViewLoader.getDefaultOptionsForWidget(this, pendingAddWidgetInfo);
                    boolean hasRestoreFlag = launcherAppWidgetInfo.hasRestoreFlag(32);
                    if (hasRestoreFlag && launcherAppWidgetInfo.bindOptions != null) {
                        Bundle extras = launcherAppWidgetInfo.bindOptions.getExtras();
                        if (defaultOptionsForWidget != null) {
                            extras.putAll(defaultOptionsForWidget);
                        }
                        defaultOptionsForWidget = extras;
                    }
                    boolean bindAppWidgetIdIfAllowed = this.mAppWidgetManager.bindAppWidgetIdIfAllowed(launcherAppWidgetInfo.appWidgetId, launcherAppWidgetProviderInfo, defaultOptionsForWidget);
                    launcherAppWidgetInfo.bindOptions = null;
                    launcherAppWidgetInfo.restoreStatus &= -33;
                    if (bindAppWidgetIdIfAllowed) {
                        if (launcherAppWidgetProviderInfo.configure == null || hasRestoreFlag) {
                            i = 0;
                        }
                        launcherAppWidgetInfo.restoreStatus = i;
                    }
                    getModelWriter().updateItemInDatabase(launcherAppWidgetInfo);
                }
            } else if (launcherAppWidgetInfo.hasRestoreFlag(4) && launcherAppWidgetProviderInfo.configure == null) {
                launcherAppWidgetInfo.restoreStatus = 0;
                getModelWriter().updateItemInDatabase(launcherAppWidgetInfo);
            }
        }
        if (launcherAppWidgetInfo.restoreStatus != 0) {
            appWidgetHostView = new PendingAppWidgetHostView(this, launcherAppWidgetInfo, this.mIconCache, false);
        } else if (launcherAppWidgetProviderInfo == null) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Removing invalid widget: id=");
            sb.append(launcherAppWidgetInfo.appWidgetId);
            FileLog.m13e(str, sb.toString());
            deleteWidgetInfo(launcherAppWidgetInfo);
            return null;
        } else {
            launcherAppWidgetInfo.minSpanX = launcherAppWidgetProviderInfo.minSpanX;
            launcherAppWidgetInfo.minSpanY = launcherAppWidgetProviderInfo.minSpanY;
            appWidgetHostView = this.mAppWidgetHost.createView(this, launcherAppWidgetInfo.appWidgetId, launcherAppWidgetProviderInfo);
        }
        prepareAppWidget(appWidgetHostView, launcherAppWidgetInfo);
        return appWidgetHostView;
    }

    private LauncherAppWidgetInfo completeRestoreAppWidget(int i, int i2) {
        LauncherAppWidgetHostView widgetForAppWidgetId = this.mWorkspace.getWidgetForAppWidgetId(i);
        if (widgetForAppWidgetId == null || !(widgetForAppWidgetId instanceof PendingAppWidgetHostView)) {
            Log.e(TAG, "Widget update called, when the widget no longer exists.");
            return null;
        }
        LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) widgetForAppWidgetId.getTag();
        launcherAppWidgetInfo.restoreStatus = i2;
        if (launcherAppWidgetInfo.restoreStatus == 0) {
            launcherAppWidgetInfo.pendingItemInfo = null;
        }
        this.mWorkspace.reinflateWidgetsIfNecessary();
        getModelWriter().updateItemInDatabase(launcherAppWidgetInfo);
        return launcherAppWidgetInfo;
    }

    public void onPageBoundSynchronously(int i) {
        this.mSynchronouslyBoundPages.add(Integer.valueOf(i));
    }

    public void executeOnNextDraw(ViewOnDrawExecutor viewOnDrawExecutor) {
        if (this.mPendingExecutor != null) {
            this.mPendingExecutor.markCompleted();
        }
        this.mPendingExecutor = viewOnDrawExecutor;
        viewOnDrawExecutor.attachTo(this);
    }

    public void clearPendingExecutor(ViewOnDrawExecutor viewOnDrawExecutor) {
        if (this.mPendingExecutor == viewOnDrawExecutor) {
            this.mPendingExecutor = null;
        }
    }

    public void finishFirstPageBind(final ViewOnDrawExecutor viewOnDrawExecutor) {
        if (!waitUntilResume(new Runnable() {
            public void run() {
                Launcher.this.finishFirstPageBind(viewOnDrawExecutor);
            }
        })) {
            C056327 r0 = new Runnable() {
                public void run() {
                    if (viewOnDrawExecutor != null) {
                        viewOnDrawExecutor.onLoadAnimationCompleted();
                    }
                }
            };
            if (this.mDragLayer.getAlpha() < 1.0f) {
                this.mDragLayer.animate().alpha(1.0f).withEndAction(r0).start();
            } else {
                r0.run();
            }
        }
    }

    public void finishBindingItems() {
        if (!waitUntilResume(new Runnable() {
            public void run() {
                Launcher.this.finishBindingItems();
            }
        })) {
            this.mWorkspace.restoreInstanceStateForRemainingPages();
            setWorkspaceLoading(false);
            if (this.mPendingActivityResult != null) {
                handleActivityResult(this.mPendingActivityResult.requestCode, this.mPendingActivityResult.resultCode, this.mPendingActivityResult.data);
                this.mPendingActivityResult = null;
            }
            InstallShortcutReceiver.disableAndFlushInstallQueue(2, this);
            NotificationListener.setNotificationsChangedListener(this.mPopupDataProvider);
            if (this.mLauncherCallbacks != null) {
                this.mLauncherCallbacks.finishBindingItems(false);
            }
        }
    }

    private boolean canRunNewAppsAnimation() {
        return System.currentTimeMillis() - this.mDragController.getLastGestureUpTime() > 5000;
    }

    private ValueAnimator createNewAppBounceAnimation(View view, int i) {
        ObjectAnimator ofViewAlphaAndScale = LauncherAnimUtils.ofViewAlphaAndScale(view, 1.0f, 1.0f, 1.0f);
        ofViewAlphaAndScale.setDuration(450);
        ofViewAlphaAndScale.setStartDelay((long) (i * 85));
        ofViewAlphaAndScale.setInterpolator(new OvershootInterpolator(BOUNCE_ANIMATION_TENSION));
        return ofViewAlphaAndScale;
    }

    public boolean useVerticalBarLayout() {
        return this.mDeviceProfile.isVerticalBarLayout();
    }

    public int getSearchBarHeight() {
        if (this.mLauncherCallbacks != null) {
            return this.mLauncherCallbacks.getSearchBarHeight();
        }
        return 0;
    }

    public void bindAllApplications(final ArrayList<AppInfo> arrayList) {
        C056529 r0 = new RunnableWithId(1) {
            public void run() {
                Launcher.this.bindAllApplications(arrayList);
            }
        };
        if (!waitUntilResume(r0)) {
            if (this.mAppsView != null) {
                Executor pendingExecutor = getPendingExecutor();
                if (pendingExecutor == null || this.mState == State.APPS) {
                    this.mAppsView.setApps(arrayList);
                } else {
                    pendingExecutor.execute(r0);
                    return;
                }
            }
            if (this.mLauncherCallbacks != null) {
                this.mLauncherCallbacks.bindAllApplications(arrayList);
            }
        }
    }

    @Nullable
    public Executor getPendingExecutor() {
        if (this.mPendingExecutor == null || !this.mPendingExecutor.canQueue()) {
            return null;
        }
        return this.mPendingExecutor;
    }

    public void bindDeepShortcutMap(MultiHashMap<ComponentKey, String> multiHashMap) {
        this.mPopupDataProvider.setDeepShortcutMap(multiHashMap);
    }

    public void bindAppsAddedOrUpdated(final ArrayList<AppInfo> arrayList) {
        if (!waitUntilResume(new Runnable() {
            public void run() {
                Launcher.this.bindAppsAddedOrUpdated(arrayList);
            }
        }) && this.mAppsView != null) {
            this.mAppsView.addOrUpdateApps(arrayList);
        }
    }

    public void bindPromiseAppProgressUpdated(final PromiseAppInfo promiseAppInfo) {
        if (!waitUntilResume(new Runnable() {
            public void run() {
                Launcher.this.bindPromiseAppProgressUpdated(promiseAppInfo);
            }
        }) && this.mAppsView != null) {
            this.mAppsView.updatePromiseAppProgress(promiseAppInfo);
        }
    }

    public void bindWidgetsRestored(final ArrayList<LauncherAppWidgetInfo> arrayList) {
        if (!waitUntilResume(new Runnable() {
            public void run() {
                Launcher.this.bindWidgetsRestored(arrayList);
            }
        })) {
            this.mWorkspace.widgetsRestored(arrayList);
        }
    }

    public void bindShortcutsChanged(final ArrayList<ShortcutInfo> arrayList, final UserHandle userHandle) {
        if (!waitUntilResume(new Runnable() {
            public void run() {
                Launcher.this.bindShortcutsChanged(arrayList, userHandle);
            }
        }) && !arrayList.isEmpty()) {
            this.mWorkspace.updateShortcuts(arrayList);
        }
    }

    public void bindRestoreItemsChange(final HashSet<ItemInfo> hashSet) {
        if (!waitUntilResume(new Runnable() {
            public void run() {
                Launcher.this.bindRestoreItemsChange(hashSet);
            }
        })) {
            this.mWorkspace.updateRestoreItems(hashSet);
        }
    }

    public void bindWorkspaceComponentsRemoved(final ItemInfoMatcher itemInfoMatcher) {
        if (!waitUntilResume(new Runnable() {
            public void run() {
                Launcher.this.bindWorkspaceComponentsRemoved(itemInfoMatcher);
            }
        })) {
            this.mWorkspace.removeItemsByMatcher(itemInfoMatcher);
            this.mDragController.onAppsRemoved(itemInfoMatcher);
        }
    }

    public void bindAppInfosRemoved(final ArrayList<AppInfo> arrayList) {
        if (!waitUntilResume(new Runnable() {
            public void run() {
                Launcher.this.bindAppInfosRemoved(arrayList);
            }
        }) && this.mAppsView != null) {
            this.mAppsView.removeApps(arrayList);
            tryAndUpdatePredictedApps();
        }
    }

    public void bindAllWidgets(final MultiHashMap<PackageItemInfo, WidgetItem> multiHashMap) {
        C057437 r0 = new RunnableWithId(2) {
            public void run() {
                Launcher.this.bindAllWidgets(multiHashMap);
            }
        };
        if (!waitUntilResume(r0)) {
            if (!(this.mWidgetsView == null || multiHashMap == null)) {
                Executor pendingExecutor = getPendingExecutor();
                if (pendingExecutor == null || this.mState == State.WIDGETS) {
                    this.mWidgetsView.setWidgets(multiHashMap);
                } else {
                    pendingExecutor.execute(r0);
                    return;
                }
            }
            AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(this);
            if (topOpenView != null) {
                topOpenView.onWidgetsBound();
            }
        }
    }

    public List<WidgetItem> getWidgetsForPackageUser(PackageUserKey packageUserKey) {
        return this.mWidgetsView.getWidgetsForPackageUser(packageUserKey);
    }

    public void notifyWidgetProvidersChanged() {
        if (this.mWorkspace.getState().shouldUpdateWidget) {
            refreshAndBindWidgetsForPackageUser(null);
        }
    }

    public void refreshAndBindWidgetsForPackageUser(@Nullable PackageUserKey packageUserKey) {
        this.mModel.refreshAndBindWidgetsAndShortcuts(packageUserKey);
    }

    public void lockScreenOrientation() {
        if (this.mRotationEnabled) {
            setRequestedOrientation(14);
        }
    }

    public void unlockScreenOrientation(boolean z) {
        if (!this.mRotationEnabled) {
            return;
        }
        if (z) {
            setRequestedOrientation(-1);
        } else {
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    Launcher.this.setRequestedOrientation(-1);
                }
            }, 500);
        }
    }

    private void markAppsViewShown() {
        if (!this.mSharedPrefs.getBoolean(APPS_VIEW_SHOWN, false)) {
            this.mSharedPrefs.edit().putBoolean(APPS_VIEW_SHOWN, true).apply();
        }
    }

    private boolean shouldShowDiscoveryBounce() {
        return this.mState == State.WORKSPACE && !this.mSharedPrefs.getBoolean(APPS_VIEW_SHOWN, false) && !UserManagerCompat.getInstance(this).isDemoUser();
    }

    /* access modifiers changed from: protected */
    public void moveWorkspaceToDefaultScreen() {
        this.mWorkspace.moveToDefaultScreen(false);
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(str, fileDescriptor, printWriter, strArr);
        if (strArr.length > 0) {
            if (TextUtils.equals(strArr[0], "--all")) {
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append("Workspace Items");
                printWriter.println(sb.toString());
                for (int numCustomPages = this.mWorkspace.numCustomPages(); numCustomPages < this.mWorkspace.getPageCount(); numCustomPages++) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str);
                    sb2.append("  Homescreen ");
                    sb2.append(numCustomPages);
                    printWriter.println(sb2.toString());
                    ShortcutAndWidgetContainer shortcutsAndWidgets = ((CellLayout) this.mWorkspace.getPageAt(numCustomPages)).getShortcutsAndWidgets();
                    for (int i = 0; i < shortcutsAndWidgets.getChildCount(); i++) {
                        Object tag = shortcutsAndWidgets.getChildAt(i).getTag();
                        if (tag != null) {
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append(str);
                            sb3.append("    ");
                            sb3.append(tag.toString());
                            printWriter.println(sb3.toString());
                        }
                    }
                }
                StringBuilder sb4 = new StringBuilder();
                sb4.append(str);
                sb4.append("  Hotseat");
                printWriter.println(sb4.toString());
                ShortcutAndWidgetContainer shortcutsAndWidgets2 = this.mHotseat.getLayout().getShortcutsAndWidgets();
                for (int i2 = 0; i2 < shortcutsAndWidgets2.getChildCount(); i2++) {
                    Object tag2 = shortcutsAndWidgets2.getChildAt(i2).getTag();
                    if (tag2 != null) {
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append(str);
                        sb5.append("    ");
                        sb5.append(tag2.toString());
                        printWriter.println(sb5.toString());
                    }
                }
                try {
                    FileLog.flushAll(printWriter);
                } catch (Exception unused) {
                }
            }
        }
        StringBuilder sb6 = new StringBuilder();
        sb6.append(str);
        sb6.append("Misc:");
        printWriter.println(sb6.toString());
        StringBuilder sb7 = new StringBuilder();
        sb7.append(str);
        sb7.append("\tmWorkspaceLoading=");
        sb7.append(this.mWorkspaceLoading);
        printWriter.print(sb7.toString());
        StringBuilder sb8 = new StringBuilder();
        sb8.append(" mPendingRequestArgs=");
        sb8.append(this.mPendingRequestArgs);
        printWriter.print(sb8.toString());
        StringBuilder sb9 = new StringBuilder();
        sb9.append(" mPendingActivityResult=");
        sb9.append(this.mPendingActivityResult);
        printWriter.println(sb9.toString());
        this.mModel.dumpState(str, fileDescriptor, printWriter, strArr);
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.dump(str, fileDescriptor, printWriter, strArr);
        }
    }

    @TargetApi(24)
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> list, Menu menu, int i) {
        ArrayList arrayList = new ArrayList();
        if (this.mState == State.WORKSPACE) {
            arrayList.add(new KeyboardShortcutInfo(getString(C0622R.string.all_apps_button_label), 29, 4096));
        }
        View currentFocus = getCurrentFocus();
        if (new CustomActionsPopup(this, currentFocus).canShow()) {
            arrayList.add(new KeyboardShortcutInfo(getString(C0622R.string.custom_actions), 43, 4096));
        }
        if ((currentFocus.getTag() instanceof ItemInfo) && DeepShortcutManager.supportsShortcuts((ItemInfo) currentFocus.getTag())) {
            arrayList.add(new KeyboardShortcutInfo(getString(C0622R.string.action_deep_shortcut), 47, 4096));
        }
        if (!arrayList.isEmpty()) {
            list.add(new KeyboardShortcutGroup(getString(C0622R.string.home_screen), arrayList));
        }
        super.onProvideKeyboardShortcuts(list, menu, i);
    }

    public boolean onKeyShortcut(int i, KeyEvent keyEvent) {
        if (keyEvent.hasModifiers(4096)) {
            if (i != 29) {
                if (i != 43) {
                    if (i == 47) {
                        View currentFocus = getCurrentFocus();
                        if ((currentFocus instanceof BubbleTextView) && (currentFocus.getTag() instanceof ItemInfo) && this.mAccessibilityDelegate.performAction(currentFocus, (ItemInfo) currentFocus.getTag(), LauncherAccessibilityDelegate.DEEP_SHORTCUTS)) {
                            PopupContainerWithArrow.getOpen(this).requestFocus();
                            return true;
                        }
                    }
                } else if (new CustomActionsPopup(this, getCurrentFocus()).show()) {
                    return true;
                }
            } else if (this.mState == State.WORKSPACE) {
                showAppsView(true, true, false);
                return true;
            }
        }
        return super.onKeyShortcut(i, keyEvent);
    }

    public static CustomAppWidget getCustomAppWidget(String str) {
        return (CustomAppWidget) sCustomAppWidgets.get(str);
    }

    public static HashMap<String, CustomAppWidget> getCustomAppWidgets() {
        return sCustomAppWidgets;
    }

    public static Launcher getLauncher(Context context) {
        if (context instanceof Launcher) {
            return (Launcher) context;
        }
        return (Launcher) ((ContextWrapper) context).getBaseContext();
    }

    /* access modifiers changed from: protected */
    public void setOurWallpaper(Drawable drawable) {
        if (drawable != null) {
            this.mLauncherView.setBackgroundDrawable(drawable);
        }
    }
}
