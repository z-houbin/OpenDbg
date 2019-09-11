package z.houbin.opendbg;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PackageAdapter extends BaseAdapter {
    private List<BasePackageInfo> packageInfoList = new ArrayList<>();
    private Context context;

    PackageAdapter(Context context, List<BasePackageInfo> packageInfoList) {
        this.context = context;
        this.packageInfoList = packageInfoList;
    }

    @Override
    public int getCount() {
        return packageInfoList.size();
    }

    @Override
    public BasePackageInfo getItem(int position) {
        return packageInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_main, null);
            holder.icon = convertView.findViewById(R.id.icon);
            holder.label = convertView.findViewById(R.id.name);
            holder.pkg = convertView.findViewById(R.id.pkg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.icon.setImageDrawable(getItem(position).getIcon());
        holder.label.setText(getItem(position).getLabel());
        holder.pkg.setText(getItem(position).getPkg());

        return convertView;
    }

    private class ViewHolder {
        private ImageView icon;
        private TextView label;
        private TextView pkg;
    }
}
