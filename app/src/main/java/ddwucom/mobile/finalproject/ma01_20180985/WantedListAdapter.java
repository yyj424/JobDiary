package ddwucom.mobile.finalproject.ma01_20180985;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WantedListAdapter extends BaseAdapter {
    Context context;
    ArrayList<Wanted> wantedList;
    LayoutInflater layoutInflater;

    public WantedListAdapter(Context context, ArrayList<Wanted> wantedList) {
        this.context = context;
        this.wantedList = wantedList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return wantedList.size();
    }

    @Override
    public Object getItem(int position) {
        return wantedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.adapter_view_wanted_list, parent, false);

            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.tv_wanted_title);
            holder.region = convertView.findViewById(R.id.tv_wanted_region);
            holder.closeDt = convertView.findViewById(R.id.tv_wanted_closeDt);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(wantedList.get(position).getTitle());
        holder.region.setText(wantedList.get(position).getRegion());
        holder.closeDt.setText(wantedList.get(position).getCloseDt());

        return convertView;
    }

    static class ViewHolder {
        TextView title;
        TextView region;
        TextView closeDt;
    }
}