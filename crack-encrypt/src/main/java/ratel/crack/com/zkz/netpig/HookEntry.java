package ratel.crack.com.zkz.netpig;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.virjar.ratel.api.RatelToolKit;
import com.virjar.ratel.api.rposed.IRposedHookLoadPackage;
import com.virjar.ratel.api.rposed.RC_MethodHook;
import com.virjar.ratel.api.rposed.RposedBridge;
import com.virjar.ratel.api.rposed.RposedHelpers;
import com.virjar.ratel.api.rposed.callbacks.RC_LoadPackage;

import java.math.BigInteger;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by virjar on 2018/10/6.
 */

public class HookEntry implements IRposedHookLoadPackage {
    private static final String TAG = "[ENCRYPT]";


    @Override
    public void handleLoadPackage(final RC_LoadPackage.LoadPackageParam lpparam) {
        RposedHelpers.findAndHookConstructor(
                SecretKeySpec.class,
                byte[].class, String.class,
                new RC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.i(TAG, String.format(
                                "[SecretKeySpec] %s: %s",
                                new String((byte[])param.args[0]),
                                (String) param.args[1]
                        ));
                    }
                }
        );

        RposedHelpers.findAndHookConstructor(
                IvParameterSpec.class,
                byte[].class,
                new RC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.i(TAG, String.format(
                                "[IvParameterSpec] %s",
                                new String((byte[])param.args[0])
                        ));
                    }
                }
        );

        RposedHelpers.findAndHookMethod(
                Cipher.class, "getInstance",
                String.class,
                new RC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.i(TAG, String.format("[Cipher] %s", param.args[0]));
                    }
                }
        );

        RposedHelpers.findAndHookMethod(
                Cipher.class, "doFinal",
                byte[].class,
                new RC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.i(TAG, String.format("[doFinal] Input: %s, Output: %s",
                                new String((byte[]) param.args[0]),
                                new String((byte[]) param.getResult())
                        ));
                    }
                }
        );

        RposedHelpers.findAndHookMethod(
                MessageDigest.class, "getInstance",
                String.class,
                new RC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.i(TAG, String.format("[Hash Algorithm getInstance] %s",
                                (String)param.args[0]
                        ));
                    }
                }
        );

        RposedBridge.hookAllMethods(
                MessageDigest.class,
                "digest",
                new RC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.i(TAG, String.format("[Hash Algorithm digest] output: %s",
                                new BigInteger(1, (byte[]) param.getResult()).toString(16)
                        ));
                    }
                }
        );

        RposedHelpers.findAndHookMethod(
                MessageDigest.class,
                "update",
                byte[].class,
                new RC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.i(TAG, String.format("[Hash Algorithm digest] update: %s",
                                (String) param.args[0]
                        ));
                    }
                }
        );

        Log.i(TAG, "hook end");
    }

}
