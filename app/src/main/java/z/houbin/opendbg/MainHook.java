package z.houbin.opendbg;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Environment;

import java.io.File;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import z.houbin.xposed.lib.hot.BaseHook;
import z.houbin.xposed.lib.log.Logs;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class MainHook extends BaseHook {
    private static final String LOCALE_PACKAGE = "z.houbin.opendbg";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals(LOCALE_PACKAGE)) {
            XposedHelpers.findAndHookMethod("z.houbin.xposed.lib.Util", loadPackageParam.classLoader, "isHook", XC_MethodReplacement.returnConstant(true));
        } else {
            dispatch(loadPackageParam);
        }
    }

    @Override
    public void dispatch(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        Class packageManagerService = XposedHelpers.findClassIfExists("com.android.server.pm.PackageManagerService", loadPackageParam.classLoader);
        if (packageManagerService != null) {
            XposedBridge.hookAllMethods(packageManagerService, "getPackageInfo", new XC_MethodHook() {
                /* access modifiers changed from: protected */
                public void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        PackageInfo packageInfo = (PackageInfo) param.getResult();

                        if (packageInfo != null) {
                            ApplicationInfo appInfo = packageInfo.applicationInfo;
                            boolean debug = isDebuggable(packageInfo.packageName);

                            if (!debug) {
                                return;
                            }

                            int flags = appInfo.flags;
                            if ((flags & 32768) == 0) {
                                flags |= 32768;
                            }
                            if ((flags & 2) == 0) {
                                flags |= 2;
                            }
                            appInfo.flags = flags;
                            param.setResult(packageInfo);
                        }
                    } catch (Exception e) {
                        Logs.e(e);
                    }
                }
            });
        }
    }

    private boolean isDebuggable(String pkg) {
        File dir = new File(new File(Environment.getExternalStorageDirectory(), ".xposed.lib"), "z.houbin.opendbg");
        File file = new File(dir, pkg);
        return file.exists();
    }
}
