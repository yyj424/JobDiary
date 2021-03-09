package ddwucom.mobile.finalproject.ma01_20180985;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ScheduleCursorAdapter extends CursorAdapter {
    LayoutInflater inflater;
    int layout;

    public ScheduleCursorAdapter(Context context, int layout, Cursor c) {
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(layout, parent, false);
        ViewHolder holder = new ViewHolder();
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder.tvScheduleName == null) {
            holder.tvScheduleName = view.findViewById(R.id.tv_schedule_name);
            holder.tvScheduleDate = view.findViewById(R.id.tv_schedule_date);
            holder.tvScheduleTime = view.findViewById(R.id.tv_schedule_time);
        }

        holder.tvScheduleName.setText(cursor.getString(cursor.getColumnIndex("name")));
        if (holder.tvScheduleDate != null) holder.tvScheduleDate.setText(cursor.getString(cursor.getColumnIndex("date")));
        if (holder.tvScheduleTime != null) holder.tvScheduleTime.setText(cursor.getString(cursor.getColumnIndex("time")));
    }
    static class ViewHolder {

        public ViewHolder() {
            tvScheduleName = null;
            tvScheduleDate = null;
            tvScheduleTime = null;
        }
        TextView tvScheduleName;
        TextView tvScheduleDate;
        TextView tvScheduleTime;
    }
}
