package ddwucom.mobile.finalproject.ma01_20180985;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DiaryCursorAdapter extends CursorAdapter {
    LayoutInflater inflater;
    int layout;
    ContentResolver cr;

    public DiaryCursorAdapter(Context context, int layout, Cursor c, ContentResolver resolver) {
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
        this.cr = resolver;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(layout, parent, false);
        DiaryCursorAdapter.ViewHolder holder = new DiaryCursorAdapter.ViewHolder();
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        DiaryCursorAdapter.ViewHolder holder = (DiaryCursorAdapter.ViewHolder) view.getTag();
        holder.tvTitle = view.findViewById(R.id.tv_diary_title);
        holder.ivThumb = view.findViewById(R.id.iv_diary_thumbnail);
        holder.tvDate = view.findViewById(R.id.tv_diary_list_date);

        holder.tvTitle.setText(cursor.getString(cursor.getColumnIndex("title")));
        if(cursor.getString(cursor.getColumnIndex("image")) != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(cursor.getString(cursor.getColumnIndex("image")), options);

            holder.ivThumb.setImageBitmap(bitmap);
        }
        holder.tvDate.setText(cursor.getString(cursor.getColumnIndex("date")));
    }
    static class ViewHolder {

        public ViewHolder() {
            tvTitle = null;
            ivThumb = null;
            tvDate = null;
        }
        TextView tvTitle;
        TextView tvDate;
        ImageView ivThumb;
    }
}
