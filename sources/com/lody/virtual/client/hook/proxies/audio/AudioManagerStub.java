package com.lody.virtual.client.hook.proxies.audio;

import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceLastPkgMethodProxy;
import mirror.android.media.IAudioService.Stub;

public class AudioManagerStub extends BinderInvocationProxy {
    public AudioManagerStub() {
        super(Stub.asInterface, "audio");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("adjustVolume"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("adjustLocalOrRemoteStreamVolume"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("adjustSuggestedStreamVolume"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("adjustStreamVolume"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("adjustMasterVolume"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("setStreamVolume"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("setMasterVolume"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("setMicrophoneMute"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("setRingerModeExternal"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("setRingerModeInternal"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("setMode"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("avrcpSupportsAbsoluteVolume"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("abandonAudioFocus"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("requestAudioFocus"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("setWiredDeviceConnectionState"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("setSpeakerphoneOn"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("setBluetoothScoOn"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("stopBluetoothSco"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("startBluetoothSco"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("disableSafeMediaVolume"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("registerRemoteControlClient"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("unregisterAudioFocusClient"));
    }
}
