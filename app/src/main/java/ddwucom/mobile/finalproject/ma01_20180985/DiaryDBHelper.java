package ddwucom.mobile.finalproject.ma01_20180985;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DiaryDBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "diary_db";
    public final static String TABLE_NAME = "diary_table";
    public final static String COL_ID = "_id";
    public final static String COL_TITLE = "title";
    public final static String COL_DATE = "date";
    public final static String COL_CONTENT = "content";
    public final static String COL_IMG = "image";

    public DiaryDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement,"
                + COL_TITLE + " TEXT, " + COL_DATE + " TEXT, " + COL_CONTENT + " TEXT, " + COL_IMG + " TEXT);");

//		샘플 데이터
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '청소년 교육 봉사활동', '2020.12.20', '장학재단 연계 아동센터에서 코딩 교육봉사를 진행하고 왔다.', null);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }
}