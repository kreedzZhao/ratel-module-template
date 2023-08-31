package ratel.com.yuanrenxue.challenge;

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
import com.virjar.ratel.api.rposed.RposedHelpers;
import com.virjar.ratel.api.rposed.callbacks.RC_LoadPackage;

/**
 * Created by virjar on 2018/10/6.
 */

public class HookEntry implements IRposedHookLoadPackage {
    private static final String tag = "Kreedz";


    @Override
    public void handleLoadPackage(final RC_LoadPackage.LoadPackageParam lpparam) {
        Log.i(tag, "hook end");
    }


}
