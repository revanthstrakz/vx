package p013io.virtualapp.glide;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/* renamed from: io.virtualapp.glide.GlideUtils */
public class GlideUtils {
    public static void loadInstalledPackageIcon(Context context, String str, ImageView imageView, @DrawableRes int i) {
        GlideRequests with = GlideApp.with(context);
        StringBuilder sb = new StringBuilder();
        sb.append(PackageIconResourceLoader.DATA_PACKAGE_PREFIX);
        sb.append(str);
        with.load(sb.toString()).placeholder(i).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
    }

    public static void loadPackageIconFromApkFile(Context context, String str, ImageView imageView, @DrawableRes int i) {
        GlideRequests with = GlideApp.with(context);
        StringBuilder sb = new StringBuilder();
        sb.append(PackageIconResourceLoader.DATA_PACKAGE_FILE_PATH_PREFIX);
        sb.append(str);
        with.load(sb.toString()).placeholder(i).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
    }
}
