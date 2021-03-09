package ddwucom.mobile.finalproject.ma01_20180985;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AllDiaryActivity extends AppCompatActivity {

    ListView listView;
    DiaryDBHelper diaryDBHelper;
    SpecDBHelper specDBHelper;
    Cursor diarycursor;
    Cursor specursor;
    DiaryCursorAdapter adapter;

    EditText etSpec;
    EditText etDis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_diary);

        etSpec = findViewById(R.id.et_my_spec);
        etDis = findViewById(R.id.et_my_disadvantage);
        listView = findViewById(R.id.lv_diary_list);

        diaryDBHelper = new DiaryDBHelper(this);
        specDBHelper = new SpecDBHelper(this);
        listView.setOnItemClickListener(itemClickListener);
        listView.setOnItemLongClickListener(itemLongClickListener);
        getSpec();
        getAllDiary();
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(AllDiaryActivity.this, DiaryViewActivity.class);
            intent.putExtra("id", String.valueOf(id));
            startActivityForResult(intent, 100);
        }
    };

    AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AllDiaryActivity.this);
            final SQLiteDatabase db = diaryDBHelper.getWritableDatabase();
            diarycursor = db.rawQuery("select _id, title from diary_table where _id = '" + id +"';", null);
            diarycursor.moveToNext();
            builder.setTitle("<" + diarycursor.getString(diarycursor.getColumnIndex("title")) + ">");
            builder.setMessage("삭제하시겠습니까?");
            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String deleteId = "_id=?";
                    String[] delete = new String[] {String.valueOf(id)};
                    db.delete("diary_table", deleteId, delete);
                    getAllDiary();
                }
            });
            builder.setNegativeButton("아니오", null);
            builder.show();
            return true;
        }
    };

    public void getAllDiary() {
        ContentResolver resolver = getContentResolver();
        SQLiteDatabase db = diaryDBHelper.getReadableDatabase();
        diarycursor = db.rawQuery("select _id, title, date, image from " + DiaryDBHelper.TABLE_NAME, null);
        adapter = new DiaryCursorAdapter(AllDiaryActivity.this, R.layout.adapter_view_all_diary, diarycursor, resolver);
        listView.setAdapter(adapter);
        adapter.changeCursor(diarycursor);
    }

    public void getSpec() {
        SQLiteDatabase db = specDBHelper.getReadableDatabase();
        specursor = db.rawQuery("select _id, * from " + SpecDBHelper.TABLE_NAME, null);
        specursor.moveToNext();
        if (!specursor.isBeforeFirst()) {
            etSpec.setText(specursor.getString(specursor.getColumnIndex("spec")));
            etDis.setText(specursor.getString(specursor.getColumnIndex("dis")));
        }
    }

    protected void onResume() {
        super.onResume();
        diaryDBHelper.close();
        getAllDiary();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (diarycursor != null) diarycursor.close();
        if (specursor != null) specursor.close();
    }

    public void onClick (View v)
    {
        switch (v.getId()){
            case R.id.btn_diary_write:
                Intent intent = new Intent(this, DiaryViewActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.btn_spec_register:
                SQLiteDatabase db = specDBHelper.getWritableDatabase();
                ContentValues row = new ContentValues();
                row.put("spec", etSpec.getText().toString());
                row.put("dis", etDis.getText().toString());
                String updateId = "_id=?";
                String[] update = new String[] {"1"};
                db.update("spec_table", row, updateId, update);
                Toast.makeText(this, "저장 완료!", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
            getAllDiary();
    }
}
