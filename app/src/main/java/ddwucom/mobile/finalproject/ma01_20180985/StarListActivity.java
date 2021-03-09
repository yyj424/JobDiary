package ddwucom.mobile.finalproject.ma01_20180985;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StarListActivity extends AppCompatActivity {

    StarDBHelper helper;
    Cursor cursor;
    ListView starList;
    SimpleCursorAdapter adapter;
    List<Wanted> resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_list);

        starList = findViewById(R.id.lv_starList);
        resultList = new ArrayList<Wanted>();

        helper = new StarDBHelper(this);

        adapter = new SimpleCursorAdapter(this, R.layout.adapter_view_wanted_list, cursor, new String[]{"title", "region", "closeDt"}, new int[]{R.id.tv_wanted_title, R.id.tv_wanted_region, R.id.tv_wanted_closeDt}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        starList.setAdapter(adapter);
        starList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("yyj", "list wtd : " + resultList.get(position));
                    Intent intent = new Intent(StarListActivity.this, WantedViewActivity.class);
                    intent.putExtra("company", resultList.get(position).getCompany());
                    intent.putExtra("title", resultList.get(position).getTitle());
                    intent.putExtra("salTp", resultList.get(position).getSalTpNm());
                    intent.putExtra("sal", resultList.get(position).getSal());
                    intent.putExtra("region", resultList.get(position).getRegion());
                    intent.putExtra("holiTp", resultList.get(position).getHolidayTpNm());
                    intent.putExtra("minEdu", resultList.get(position).getMinEdubg());
                    intent.putExtra("career", resultList.get(position).getCareer());
                    intent.putExtra("closeDt", resultList.get(position).getCloseDt());
                    intent.putExtra("url", resultList.get(position).getWantedMobileInfoUrl());
                    intent.putExtra("jobsCd", resultList.get(position).getJobsCd());
                    intent.putExtra("basicAddr", resultList.get(position).getBasicAddr());
                    startActivity(intent);
            }
        });
    }

    public void getList() {
        SQLiteDatabase db = helper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + helper.TABLE_NAME, null);
        if (cursor.moveToNext() && adapter != null) {
            Wanted wtd = new Wanted();
            wtd.setCompany(cursor.getString(cursor.getColumnIndex("company")));
            wtd.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            wtd.setSalTpNm(cursor.getString(cursor.getColumnIndex("salTpNm")));
            wtd.setSal(cursor.getString(cursor.getColumnIndex("sal")));
            wtd.setRegion(cursor.getString(cursor.getColumnIndex("region")));
            wtd.setHolidayTpNm(cursor.getString(cursor.getColumnIndex("holidayTpNm")));
            wtd.setMinEdubg(cursor.getString(cursor.getColumnIndex("minEdubg")));
            wtd.setCareer(cursor.getString(cursor.getColumnIndex("career")));
            wtd.setCloseDt(cursor.getString(cursor.getColumnIndex("closeDt")));
            wtd.setWantedMobileInfoUrl(cursor.getString(cursor.getColumnIndex("wantedMobileInfoUrl")));
            wtd.setBasicAddr(cursor.getString(cursor.getColumnIndex("basicAddr")));
            wtd.setJobsCd(cursor.getString(cursor.getColumnIndex("jobsCd")));
            Log.d("yyj", "wtd : " + wtd);
            resultList.add(wtd);
        }
    }

    protected void onResume() {
        super.onResume();
        getList();
        adapter.changeCursor(cursor);
        helper.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
    }
}
