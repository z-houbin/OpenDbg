package z.houbin.opendbg;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class WhiteListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_white);

        loadWhiteList();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadWhiteList() {
        List<String> debuggablePackages = DebugUtil.getDebuggablePackages();

        final List<BasePackageInfo> packageInfoList = new ArrayList<>();

        PackageManager packageManager = getPackageManager();
        for (int i = 0; i < debuggablePackages.size(); i++) {
            String pkg = debuggablePackages.get(i);
            BasePackageInfo basePackageInfo = new BasePackageInfo();
            basePackageInfo.setPkg(pkg);
            basePackageInfo.setWhite(true);
            try {
                basePackageInfo.setLabel(packageManager.getApplicationLabel(packageManager.getApplicationInfo(pkg, 0)).toString());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            try {
                basePackageInfo.setIcon(packageManager.getApplicationIcon(pkg));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
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
                        DebugUtil.setDebuggable(packageInfo.getPkg(), false);
                        Toast.makeText(WhiteListActivity.this, "移除调试列表,重启手机生效", Toast.LENGTH_SHORT).show();

                        loadWhiteList();
                        return false;
                    }
                });
            }
        });
    }
}
