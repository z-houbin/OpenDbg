package z.houbin.opendbg;

import android.os.Environment;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import z.houbin.xposed.lib.Files;

public class DebugUtil {
    private static final File dir = new File(new File(Environment.getExternalStorageDirectory(), ".xposed.lib"), "z.houbin.opendbg");

    public static void setDebuggable(String pkg, boolean debug) {
        if (debug) {
            Files.writeFile(new File(dir, pkg), "");
        } else {
            File file = new File(dir, pkg);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static List<String> getDebuggablePackages() {
        List<String> packages = new ArrayList<>();
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.getName().contains(".")) {
                        return true;
                    }
                    return false;
                }
            });

            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    packages.add(files[i].getName());
                }
            }
        }
        return packages;
    }

    public static boolean isDebuggable(String pkg) {
        File file = new File(dir, pkg);
        return file.exists();
    }
}
