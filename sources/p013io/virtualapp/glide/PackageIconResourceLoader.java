package p013io.virtualapp.glide;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.signature.ObjectKey;
import java.io.InputStream;

/* renamed from: io.virtualapp.glide.PackageIconResourceLoader */
public class PackageIconResourceLoader implements ModelLoader<String, InputStream> {
    public static final String DATA_PACKAGE_FILE_PATH_PREFIX = "data:packageFilePath/";
    public static final String DATA_PACKAGE_PREFIX = "data:packageName/";
    private Context context;

    public PackageIconResourceLoader(Context context2) {
        this.context = context2;
    }

    @Nullable
    public LoadData<InputStream> buildLoadData(@NonNull String str, int i, int i2, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(str), new PackageIconResourceDataFetcher(this.context, str));
    }

    public boolean handles(@NonNull String str) {
        return str.startsWith(DATA_PACKAGE_PREFIX) || str.startsWith(DATA_PACKAGE_FILE_PATH_PREFIX);
    }
}
