package ddwucom.mobile.finalproject.ma01_20180985;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScheduleDBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "schedule_db";
    public final static String TABLE_NAME = "schedule_table";
    public final static String COL_ID = "_id";
    public final static String COL_NAME = "name";
    public final static String COL_DATE = "date";
    public final static String COL_TIME = "time";
    public final static String COL_ALARM = "alarm";

    public ScheduleDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement,"
                + COL_NAME + " TEXT, " + COL_DATE + " TEXT, " + COL_TIME + " TEXT, " + COL_ALARM + " integer);");

//		샘플 데이터
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '청년 인턴 서류 제출', '2020.12.08', '15 : 30', '-1');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '카카오 코딩 테스트', '2020.12.22', '16 : 00', '-2');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '정보처리기사 자격증', '2020.12.15', '09 : 30', '-3');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }

}
