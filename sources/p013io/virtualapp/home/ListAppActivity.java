package p013io.virtualapp.home;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.p001v4.app.ActivityCompat;
import android.support.p001v4.view.ViewPager;
import android.support.p004v7.app.AlertDialog.Builder;
import android.support.p004v7.widget.Toolbar;
import android.view.MenuItem;
import io.va.exposed.R;
import p013io.virtualapp.abs.p014ui.VActivity;
import p013io.virtualapp.home.adapters.AppPagerAdapter;

/* renamed from: io.virtualapp.home.ListAppActivity */
public class ListAppActivity extends VActivity {
    private TabLayout mTabLayout;
    private Toolbar mToolBar;
    private ViewPager mViewPager;

    public static void gotoListApp(Activity activity) {
        activity.startActivityForResult(new Intent(activity, ListAppActivity.class), 5);
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_clone_app);
        this.mToolBar = (Toolbar) findViewById(R.id.clone_app_tool_bar);
        this.mTabLayout = (TabLayout) this.mToolBar.findViewById(R.id.clone_app_tab_layout);
        this.mViewPager = (ViewPager) findViewById(R.id.clone_app_view_pager);
        this.mViewPager.setAdapter(new AppPagerAdapter(getSupportFragmentManager()));
        this.mTabLayout.setupWithViewPager(this.mViewPager);
        if (ActivityCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
            try {
                new Builder(this, 2131951907).setMessage((int) R.string.list_app_access_external_storage).setPositiveButton(17039370, (OnClickListener) new OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(ListAppActivity.this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 0);
                    }
                }).create().show();
            } catch (Throwable unused) {
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        for (int i2 : iArr) {
            if (i2 == 0) {
                this.mViewPager.setAdapter(new AppPagerAdapter(getSupportFragmentManager()));
                return;
            }
        }
    }
}
