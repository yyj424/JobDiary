package ddwucom.mobile.finalproject.ma01_20180985;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StarDBHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "star_db";
    public final static String TABLE_NAME = "star_table";
    public final static String COL_ID = "_id";
    public final static String COL_COMPANY = "company";
    public final static String COL_TITLE = "title";
    public final static String COL_SALTPNM = "salTpNm";
    public final static String COL_SAL = "sal";
    public final static String COL_REGION = "region";
    public final static String COL_HOLIDAYTPNM = "holidayTpNm";
    public final static String COL_MINEDUBG = "minEdubg";
    public final static String COL_CAREER = "career";
    public final static String COL_CLOSEDT = "closeDt";
    public final static String COL_INFOURL = "wantedMobileInfoUrl";
    public final static String COL_ADDR = "basicAddr";
    public final static String COL_JOBSCD = "jobsCd";

    public StarDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement,"
                + COL_COMPANY + " TEXT, " + COL_TITLE + " TEXT, " + COL_SALTPNM + " TEXT, "+ COL_SAL + " TEXT, " + COL_REGION + " TEXT, " + COL_HOLIDAYTPNM + " TEXT, "
                + COL_MINEDUBG + " TEXT, " + COL_CAREER + " TEXT, " + COL_CLOSEDT + " TEXT, " + COL_INFOURL + " TEXT, "
                + COL_ADDR + " TEXT, " + COL_JOBSCD + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }
}
