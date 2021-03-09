package ddwucom.mobile.finalproject.ma01_20180985;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GoalDBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "goal_db";
    public final static String TABLE_NAME = "goal_table";
    public final static String COL_ID = "_id";
    public final static String COL_GOAL = "goal";

    public GoalDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement,"
                + COL_GOAL + " TEXT);");

//		샘플 데이터
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '토익 800점 이상');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '모의 면접 10회 이상');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '자소서 미리 작성');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }

}
