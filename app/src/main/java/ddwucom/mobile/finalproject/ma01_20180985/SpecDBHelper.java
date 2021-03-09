package ddwucom.mobile.finalproject.ma01_20180985;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SpecDBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "spec_db";
    public final static String TABLE_NAME = "spec_table";
    public final static String COL_ID = "_id";
    public final static String COL_SPEC = "spec";
    public final static String COL_DIS = "dis";

    public SpecDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement,"
                + COL_SPEC + " TEXT, " + COL_DIS + " TEXT);");

//		샘플 데이터
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '토익 700점, 정처기 자격증 보유, 해외봉사 경험', '면접 경험 부족, 자소서 첨삭 필요');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }

}
