package ddwucom.mobile.finalproject.ma01_20180985;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class UpdateScheduleActivity extends AppCompatActivity {

    EditText etName;
    TextView tvDate;
    TimePicker timePicker;
    Button dp;
    DatePickerDialog datePickerDialog;

    ScheduleDBHelper helper;
    Cursor cursor;

    int hour;
    int min;
    String time;
    int req = 0;

    Intent intent;
    String selectedDate;
    String id;
    AlarmManager alarmManager;

    public final static int FLAG = 0;
    PendingIntent pIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_schedule);

        intent = getIntent();
        id = intent.getStringExtra("id");
        helper = new ScheduleDBHelper(this);

        etName = findViewById(R.id.et_scheduleName);
        tvDate = findViewById(R.id.tv_selectedDate);
        timePicker = findViewById(R.id.tp_update);
        dp = findViewById(R.id.btn_datePicker);
        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        getSelectedSchedule(id);
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            if ((month + 1) < 10) {
                selectedDate = year + ".0" + (month + 1);
            }
            else {
                selectedDate = year + "." + (month + 1);
            }
            if (dayOfMonth < 10) {
                selectedDate += ".0" + (dayOfMonth);
            }
            else {
                selectedDate += "." + (dayOfMonth);
            }
            tvDate.setText(selectedDate);
        }
    };

    public void getSelectedSchedule(String id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String selection = "_id=?";
        String selectArgs[] = new String[] {id};
        cursor = db.query(ScheduleDBHelper.TABLE_NAME, null, selection, selectArgs, null, null, null, null);

        cursor.moveToNext();
        etName.setText(cursor.getString(cursor.getColumnIndex("name")));
        selectedDate = cursor.getString(cursor.getColumnIndex("date"));
        tvDate.setText(selectedDate);
        time = cursor.getString(cursor.getColumnIndex("time"));
        req = cursor.getInt(cursor.getColumnIndex("alarm"));

        String[] array = selectedDate.split("\\.");
        int y = Integer.parseInt(array[0]);
        int m = Integer.parseInt(array[1]);
        int d = Integer.parseInt(array[2]);
        m = m - 1;
        datePickerDialog = new DatePickerDialog(UpdateScheduleActivity.this, listener, y, m, d);

        String[] array2 = time.split(" : ");
        hour = Integer.parseInt(array2[0]);
        min = Integer.parseInt(array2[1]);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(min);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                hour = hourOfDay;
                min = minute;
            }
        });
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
        SQLiteDatabase db = helper.getWritableDatabase();
        switch (v.getId()){
            case R.id.btn_update_schedule:
                if (etName.getText() == null || etName.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(this, "일정명을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Calendar calendar = Calendar.getInstance();
                String[] array = selectedDate.split("\\.");
                int y = Integer.parseInt(array[0]);
                int m = Integer.parseInt(array[1]);
                int d = Integer.parseInt(array[2]);
                m = m - 1;
                calendar.set(y, m, d, hour, min);
                if (calendar.getTimeInMillis() - System.currentTimeMillis() < 0) {
                    Toast.makeText(this, "지난 시간으로 설정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                ContentValues row = new ContentValues();
                row.put("name", etName.getText().toString());
                row.put("date", selectedDate);
                if (min < 10) {
                    row.put("time", hour + " : 0" + min);
                }
                else {
                    row.put("time", hour + " : " + min);
                }
                String updateId = "_id=?";
                String[] update = new String[] {id};
                db.update("schedule_table", row, updateId, update);
                helper.close();

                intent = new Intent(this, MyBroadcastReceiver.class);
                intent.putExtra("title", etName.getText().toString());
                intent.putExtra("channelId", getString(R.string.CHANNEL_ID));
                pIntent = PendingIntent.getBroadcast(this, req, intent, PendingIntent.FLAG_NO_CREATE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
                break;
            case R.id.btn_delete_schedule:
                String deleteId = "_id=?";
                String[] delete = new String[] {id};
                db.delete("schedule_table", deleteId, delete);
                helper.close();

                intent = new Intent(this, MyBroadcastReceiver.class);
                intent.putExtra("title", etName.getText().toString());
                intent.putExtra("channelId", getString(R.string.CHANNEL_ID));
                pIntent = PendingIntent.getBroadcast(this, req, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pIntent);
                pIntent.cancel();
                break;
        }
        intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
