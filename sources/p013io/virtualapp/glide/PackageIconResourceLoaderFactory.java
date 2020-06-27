package p013io.virtualapp.glide;

import android.content.Context;
import android.support.annotation.NonNull;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import java.io.InputStream;

/* renamed from: io.virtualapp.glide.PackageIconResourceLoaderFactory */
public class PackageIconResourceLoaderFactory implements ModelLoaderFactory<String, InputStream> {
    private Context context;

    public void teardown() {
    }

    public PackageIconResourceLoaderFactory(Context context2) {
        this.context = context2;
    }

    @NonNull
    public ModelLoader<String, InputStream> build(@NonNull MultiModelLoaderFactory multiModelLoaderFactory) {
        return new PackageIconResourceLoader(this.context);
    }
}
