package ddwucom.mobile.finalproject.ma01_20180985;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AllScheduleActivity extends AppCompatActivity {

    CalendarView calendar;
    Button btn_get_all_schedule;
    ListView listView;
    ScheduleDBHelper helper;
    Cursor cursor;
    ScheduleCursorAdapter adapter;
    String selectedDate;
    String h;
    String m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_schedule);

        calendar = findViewById(R.id.cv_schedule);
        btn_get_all_schedule = findViewById(R.id.btn_get_all_schedule);
        listView = findViewById(R.id.lv_schedule);

        helper = new ScheduleDBHelper(this);

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        getSelectedDateSchedule(sdf.format(new Date(calendar.getDate())));

        Calendar cal = Calendar.getInstance();
        selectedDate = sdf.format(cal.getTime());
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, dayOfMonth);
                selectedDate = sdf.format(cal.getTime());
                getSelectedDateSchedule(selectedDate);
            }
        });

        listView.setOnItemClickListener(itemClickListener);
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(AllScheduleActivity.this, UpdateScheduleActivity.class);
            intent.putExtra("id", String.valueOf(id));
            startActivityForResult(intent, 200);
        }
    };

    public void getAllSchedule() {
        SQLiteDatabase db = helper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + ScheduleDBHelper.TABLE_NAME, null);
        adapter = new ScheduleCursorAdapter(AllScheduleActivity.this, R.layout.adapter_view_all_schedule, cursor);
        listView.setAdapter(adapter);
        adapter.changeCursor(cursor);
    }

    public void getSelectedDateSchedule(String sDate) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String columns[] = {"_id, name, time"};
        String selection = "date=?";
        String selectArgs[] = new String[] {sDate};
        cursor = db.query(ScheduleDBHelper.TABLE_NAME, columns, selection, selectArgs, null, null, null, null);
        adapter = new ScheduleCursorAdapter(AllScheduleActivity.this, R.layout.adapter_view_selected_date_schedule, cursor);
        listView.setAdapter(adapter);
        if(cursor.moveToNext()) { adapter.changeCursor(cursor); }
    }

    protected void onResume() {
        super.onResume();
        helper.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
    }

    public void onClick (View v)
    {
        switch (v.getId()){
            case R.id.btn_new_schedule:
                Intent intent = new Intent(this, AddScheduleActivity.class);
                intent.putExtra("selectedDate", selectedDate);
                startActivityForResult(intent, 100);
                break;
            case R.id.btn_get_all_schedule:
                getAllSchedule();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
            getSelectedDateSchedule(selectedDate);
    }
}
