package p013io.virtualapp.glide;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator.Builder;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.lody.virtual.helper.utils.VLog;
import java.io.InputStream;

/* renamed from: io.virtualapp.glide.MyGlideModule */
public class MyGlideModule extends AppGlideModule {
    public boolean isManifestParsingEnabled() {
        return false;
    }

    public void applyOptions(Context context, GlideBuilder glideBuilder) {
        glideBuilder.setMemoryCache(new LruResourceCache((long) (new Builder(context).build().getMemoryCacheSize() / 2)));
        VLog.m89i("MyGlideModule", "applyOptions", new Object[0]);
    }

    public void registerComponents(Context context, Glide glide, Registry registry) {
        super.registerComponents(context, glide, registry);
        registry.prepend(String.class, InputStream.class, (ModelLoaderFactory<Model, Data>) new PackageIconResourceLoaderFactory<Model,Data>(context));
    }
}
