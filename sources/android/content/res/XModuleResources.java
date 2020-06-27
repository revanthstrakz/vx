package android.content.res;

import android.app.AndroidAppHelper;
import android.util.DisplayMetrics;

public class XModuleResources extends Resources {
    private XModuleResources(AssetManager assetManager, DisplayMetrics displayMetrics, Configuration configuration) {
        super(assetManager, displayMetrics, configuration);
    }

    public static XModuleResources createInstance(String str, XResources xResources) {
        XModuleResources xModuleResources;
        if (str != null) {
            AssetManager assetManager = new AssetManager();
            assetManager.addAssetPath(str);
            if (xResources != null) {
                xModuleResources = new XModuleResources(assetManager, xResources.getDisplayMetrics(), xResources.getConfiguration());
            } else {
                xModuleResources = new XModuleResources(assetManager, null, null);
            }
            AndroidAppHelper.addActiveResource(str, xModuleResources);
            return xModuleResources;
        }
        throw new IllegalArgumentException("path must not be null");
    }

    public XResForwarder fwd(int i) {
        return new XResForwarder(this, i);
    }
}
