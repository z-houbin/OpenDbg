package z.houbin.opendbg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import z.houbin.xposed.lib.Permissions;
import z.houbin.xposed.lib.Util;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Permissions.isOwnPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            loadPackageList();
        } else {
            Permissions.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        loadPackageList();
    }

    private void loadPackageList() {
        Toast.makeText(this, "加载列表中", Toast.LENGTH_SHORT).show();

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    List<String> whiteList = DebugUtil.getDebuggablePackages();

                    final List<BasePackageInfo> packageInfoList = new ArrayList<>();
                    PackageManager packageManager = getPackageManager();
                    List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(0);
                    for (ApplicationInfo installedApplication : installedApplications) {
                        BasePackageInfo basePackageInfo = new BasePackageInfo();
                        basePackageInfo.setPkg(installedApplication.packageName);
                        basePackageInfo.setWhite(whiteList.contains(basePackageInfo.getPkg()));
                        basePackageInfo.setLabel(installedApplication.loadLabel(packageManager).toString());
                        basePackageInfo.setIcon(installedApplication.loadIcon(packageManager));
                        packageInfoList.add(basePackageInfo);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PackageAdapter adapter = new PackageAdapter(getApplicationContext(), packageInfoList);
                            ListView list = findViewById(R.id.list);
                            list.setAdapter(adapter);

                            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                    PackageAdapter adapter1 = (PackageAdapter) parent.getAdapter();
                                    BasePackageInfo packageInfo = adapter1.getItem(position);
                                    DebugUtil.setDebuggable(packageInfo.getPkg(), true);
                                    Toast.makeText(MainActivity.this, "成功加入调试列表,重启应用生效", Toast.LENGTH_SHORT).show();
                                    return false;
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void checkModule(View view) {
        Toast.makeText(this, "模块加载 - " + Util.isHook(), Toast.LENGTH_SHORT).show();
    }

    public void whiteList(View view) {
        Intent intent = new Intent(getApplicationContext(), WhiteListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
