package ddwucom.mobile.finalproject.ma01_20180985;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    ScheduleDBHelper scheduleDBHelper;
    GoalDBHelper goalDBHelper;
    Cursor schcursor;
    Cursor goalcursor;
    TextView latestNm;
    TextView latestDt;
    TextView latestTm;
    ListView goalList;
    SimpleCursorAdapter goalCursorAdapter;
    Button addGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latestNm = findViewById(R.id.tv_latestNm);
        latestDt = findViewById(R.id.tv_latestDt);
        latestTm = findViewById(R.id.tv_latestTm);
        goalList = findViewById(R.id.lv_goal_list);
        addGoal = findViewById(R.id.btn_add_goal);

        scheduleDBHelper = new ScheduleDBHelper(this);
        goalDBHelper = new GoalDBHelper(this);
        goalCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, new String[]{"goal"}, new int[] {android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        goalList.setAdapter(goalCursorAdapter);

        addGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final LinearLayout layout = (LinearLayout) View.inflate(MainActivity.this, R.layout.dialog, null);
                builder.setView(layout);
                builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText etGoal = layout.findViewById(R.id.et_goal);
                        if (etGoal.getText() == null || etGoal.getText().toString().replace(" ", "").equals("")) {
                            Toast.makeText(MainActivity.this, "목표를 입력하세요", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            SQLiteDatabase db = goalDBHelper.getWritableDatabase();
                            ContentValues row = new ContentValues();
                            row.put("goal", etGoal.getText().toString());
                            db.insert("goal_table", null, row);
                            getMyGoal();
                        }
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show();
                getMyGoal();
            }
        });

        goalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final SQLiteDatabase db = goalDBHelper.getWritableDatabase();
                goalcursor = db.rawQuery("select * from goal_table where _id = '" + id +"';", null);
                goalcursor.moveToNext();
                final LinearLayout layout = (LinearLayout) View.inflate(MainActivity.this, R.layout.dialog, null);
                EditText etGoal = layout.findViewById(R.id.et_goal);
                etGoal.setText(goalcursor.getString(goalcursor.getColumnIndex("goal")));
                builder.setView(layout);
                builder.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText etGoal = layout.findViewById(R.id.et_goal);
                        if (etGoal.getText() == null || etGoal.getText().toString().replace(" ", "").equals("")) {
                            Toast.makeText(MainActivity.this, "목표를 입력하세요", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            ContentValues row = new ContentValues();
                            row.put("goal", etGoal.getText().toString());
                            String updateId = "_id=?";
                            String[] update = new String[] {String.valueOf(id)};
                            db.update("goal_table", row, updateId, update);
                            getMyGoal();
                        }
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show();
            }
        });

        goalList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final SQLiteDatabase db = goalDBHelper.getWritableDatabase();
                goalcursor = db.rawQuery("select * from goal_table where _id = '" + id +"';", null);
                goalcursor.moveToNext();
                builder.setTitle("<" + goalcursor.getString(goalcursor.getColumnIndex("goal")) + ">");
                builder.setMessage("삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String deleteId = "_id=?";
                        String[] delete = new String[] {String.valueOf(id)};
                        db.delete("goal_table", deleteId, delete);
                        getMyGoal();
                    }
                });
                builder.setNegativeButton("아니오", null);
                builder.show();
                return true;
            }
        });
    }

    public void getLatestSchedule() {
        SQLiteDatabase db = scheduleDBHelper.getReadableDatabase();
        schcursor = db.rawQuery("select * from " + scheduleDBHelper.TABLE_NAME, null);
        String latestschName = "";
        String latestschDate = "";
        String latestschTime = "";
        long lat = 1000000000;
        while (schcursor.moveToNext()) {
            Calendar calendar = Calendar.getInstance();
            String date = schcursor.getString(schcursor.getColumnIndex("date"));
            String[] array = date.split("\\.");
            int y = Integer.parseInt(array[0]);
            int m = Integer.parseInt(array[1]);
            int d = Integer.parseInt(array[2]);
            m = m - 1;

            String time = schcursor.getString(schcursor.getColumnIndex("time"));
            String[] array2 = time.split(" : ");
            int hour = Integer.parseInt(array2[0]);
            int min = Integer.parseInt(array2[1]);

            calendar.set(y, m, d, hour, min);
            if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
                if (lat > calendar.getTimeInMillis() - System.currentTimeMillis()) {
                    lat = calendar.getTimeInMillis() - System.currentTimeMillis();
                    latestschName = schcursor.getString(schcursor.getColumnIndex("name"));
                    latestschDate = date;
                    latestschTime = time;
                }
            }
        }
        latestNm.setText(latestschName);
        latestDt.setText(latestschDate);
        latestTm.setText(latestschTime);
    }

    public void getMyGoal() {
        SQLiteDatabase db = goalDBHelper.getReadableDatabase();
        goalcursor = db.rawQuery("select * from " + GoalDBHelper.TABLE_NAME, null);
        goalCursorAdapter.changeCursor(goalcursor);
    }

    protected void onResume() {
        super.onResume();
        getLatestSchedule();
        getMyGoal();
        scheduleDBHelper.close();
        goalDBHelper.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (schcursor != null) schcursor.close();
        if (goalcursor != null) goalcursor.close();
    }

    public void onClick (View v)
    {
        Intent intent = null;
        switch (v.getId()){
            case R.id.btn_wanted:
                intent = new Intent(this, WantedListActivity.class);
                break;
            case R.id.btn_schedule:
                intent = new Intent(this, AllScheduleActivity.class);
                break;
            case R.id.btn_job:
                intent = new Intent(this, JobListActivity.class);
                break;
            case R.id.btn_diary:
                intent = new Intent(this, AllDiaryActivity.class);
                break;
        }
        startActivity(intent);
    }
}