package p013io.virtualapp.glide;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.lody.virtual.helper.utils.VLog;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* renamed from: io.virtualapp.glide.PackageIconResourceDataFetcher */
public class PackageIconResourceDataFetcher implements DataFetcher<InputStream> {
    private static final String TAG = "PackageIconResourceDataFetcher";
    private Context context;
    private InputStream data;
    private String packageModel;

    public void cancel() {
    }

    public PackageIconResourceDataFetcher(Context context2, String str) {
        this.context = context2.getApplicationContext();
        this.packageModel = str;
    }

    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> dataCallback) {
        try {
            this.data = loadResource();
            dataCallback.onDataReady(this.data);
        } catch (Exception e) {
            VLog.m87e(TAG, "Failed to load data from asset manager", e);
            dataCallback.onLoadFailed(e);
        }
    }

    public void cleanup() {
        if (this.data != null) {
            try {
                this.data.close();
            } catch (IOException unused) {
            }
        }
    }

    @NonNull
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }

    private InputStream loadResource() {
        Drawable drawable;
        try {
            PackageInfo packageInfo = getPackageInfo();
            if (packageInfo == null) {
                return null;
            }
            drawable = packageInfo.applicationInfo.loadIcon(this.context.getPackageManager());
            if (drawable == null) {
                return null;
            }
            return drawableToInputStream(drawable);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            drawable = null;
        }
    }

    private PackageInfo getPackageInfo() throws NameNotFoundException {
        if (this.packageModel.startsWith(PackageIconResourceLoader.DATA_PACKAGE_PREFIX)) {
            return this.context.getPackageManager().getPackageInfo(getPackageTrueModel(PackageIconResourceLoader.DATA_PACKAGE_PREFIX), 0);
        }
        if (this.packageModel.startsWith(PackageIconResourceLoader.DATA_PACKAGE_FILE_PATH_PREFIX)) {
            return this.context.getPackageManager().getPackageArchiveInfo(getPackageTrueModel(PackageIconResourceLoader.DATA_PACKAGE_FILE_PATH_PREFIX), 0);
        }
        return null;
    }

    private String getPackageTrueModel(String str) {
        return this.packageModel.replaceAll(str, "");
    }

    private InputStream drawableToInputStream(Drawable drawable) {
        Bitmap drawableToBitmap = drawableToBitmap(drawable);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        drawableToBitmap.compress(CompressFormat.PNG, 100, byteArrayOutputStream);
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int i = 1;
        if (intrinsicWidth <= 0) {
            intrinsicWidth = 1;
        }
        int intrinsicHeight = drawable.getIntrinsicHeight();
        if (intrinsicHeight > 0) {
            i = intrinsicHeight;
        }
        Bitmap createBitmap = Bitmap.createBitmap(intrinsicWidth, i, Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return createBitmap;
    }
}
