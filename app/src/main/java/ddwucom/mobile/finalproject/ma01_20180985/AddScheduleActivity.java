package ddwucom.mobile.finalproject.ma01_20180985;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddScheduleActivity extends AppCompatActivity {

    EditText etName;
    TextView tvDate;
    TextView tvCompany;
    TextView tvTitle;
    TimePicker timePicker;
    Button dp;
    LinearLayout applyLayout;

    ScheduleDBHelper helper;

    int hour;
    int min;

    Intent intent;
    String selectedDate;
    AlarmManager alarmManager;

    public final static int FLAG = 0;
    int req = 0;
    PendingIntent pIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        intent = getIntent();
        selectedDate = intent.getStringExtra("selectedDate");

        etName = findViewById(R.id.et_scheduleName);
        tvDate = findViewById(R.id.tv_selectedDate);
        tvCompany = findViewById(R.id.tv_apply_company);
        tvTitle = findViewById(R.id.tv_apply_title);
        tvDate.setText(selectedDate);
        dp = findViewById(R.id.btn_datePicker);
        dp.setVisibility(View.GONE);
        applyLayout = findViewById(R.id.ll_apply_info);
        applyLayout.setVisibility(View.INVISIBLE);
        if(selectedDate == null) {
            dp.setVisibility(View.VISIBLE);
            applyLayout.setVisibility(View.VISIBLE);
            tvCompany.setText(intent.getStringExtra("company"));
            tvTitle.setText(intent.getStringExtra("title"));
            final Calendar calendar = Calendar.getInstance();
            dp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddScheduleActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            if ((month + 1) < 10) {
                                selectedDate =  year + ".0" + (month + 1);
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
                    },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                    datePickerDialog.show();
                }
            });
        }

        timePicker = findViewById(R.id.tp_add);
        createNotificationChannel();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                hour = hourOfDay;
                min = minute;
            }
        });
        helper = new ScheduleDBHelper(this);
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.CHANNEL_ID), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void onClick (View v)
    {
        switch (v.getId()) {
            case R.id.btn_add_schedule:
                if (selectedDate == null) {
                    Toast.makeText(this, "날짜를 선택하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (etName.getText() == null || etName.getText().toString().replace(" ", "").equals("")) {
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
                String time = null;
                if (calendar.getTimeInMillis() - System.currentTimeMillis() < 0) {
                    Toast.makeText(this, "현재 시간 이후로 설정하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (hour == 0 && min == 0) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH : mm");
                    time = sdf.format(System.currentTimeMillis());
                }

                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues row = new ContentValues();
                row.put("name", etName.getText().toString());
                row.put("date", selectedDate);
                if (time != null) {
                    row.put("time", time);
                }
                else {
                    if (min < 10) {
                        row.put("time", hour + " : 0" + min);
                    }
                    else {
                        row.put("time", hour + " : " + min);
                    }
                }
                req = (int) System.currentTimeMillis();
                row.put("alarm", req);
                db.insert("schedule_table", null, row);
                helper.close();

                intent = new Intent(this, MyBroadcastReceiver.class);
                intent.putExtra("title", etName.getText().toString());
                intent.putExtra("channelId", getString(R.string.CHANNEL_ID));
                pIntent = PendingIntent.getBroadcast(this, req, intent, FLAG);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);

                intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.btn_add_cancel:
                finish();
                break;
        }
    }
}
