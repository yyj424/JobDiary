package ddwucom.mobile.finalproject.ma01_20180985;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class WantedViewActivity  extends AppCompatActivity {
    TextView company;
    TextView title;
    TextView salTp;
    TextView sal;
    TextView region;
    TextView holidayTpNm;
    TextView minEdubg;
    TextView career;
    TextView closeDt;
    TextView wantedMobileInfoUrl;
    TextView jobsCd;
    TextView basicAddr;
    String addr;
    String url;
    LinearLayout addrLayout;
    CheckBox star;

    MapFragment mapFragment;
    private GoogleMap mGoogleMap;
    private LatLngResultReceiver latLngResultReceiver;
    private Marker centerMarker;

    StarDBHelper starDBHelper;
    boolean checked = false;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wanted_view);

        Intent intent = getIntent();
        company = findViewById(R.id.tv_company);
        title = findViewById(R.id.tv_title);
        salTp = findViewById(R.id.tv_salType);
        sal = findViewById(R.id.tv_sal);
        region = findViewById(R.id.tv_region);
        holidayTpNm = findViewById(R.id.tv_holidayType);
        minEdubg = findViewById(R.id.tv_minEdubg);
        career = findViewById(R.id.tv_career);
        closeDt = findViewById(R.id.tv_closeDt);
        wantedMobileInfoUrl = findViewById(R.id.tv_url);
        jobsCd = findViewById(R.id.tv_jobsCd);
        basicAddr = findViewById(R.id.tv_basicAddr);
        addrLayout = findViewById(R.id.hide_view_addr);
        addrLayout.setVisibility(View.GONE);
        star = findViewById(R.id.cb_star);
        star.setButtonDrawable(R.drawable.selector);
        starDBHelper = new StarDBHelper(this);

        company.setText(intent.getStringExtra("company"));
        title.setText(intent.getStringExtra("title"));
        salTp.setText(intent.getStringExtra("salTp"));
        sal.setText(intent.getStringExtra("sal"));
        region.setText(intent.getStringExtra("region"));
        holidayTpNm.setText(intent.getStringExtra("holiTp"));
        minEdubg.setText(intent.getStringExtra("minEdu"));
        career.setText(intent.getStringExtra("career"));
        closeDt.setText(intent.getStringExtra("closeDt"));
        url = intent.getStringExtra("url");
        jobsCd.setText(intent.getStringExtra("jobsCd"));
        addr = intent.getStringExtra("basicAddr");

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallBack);
        latLngResultReceiver = new LatLngResultReceiver(new Handler());

        getStar();
    }

    public void getStar() {
        SQLiteDatabase db = starDBHelper.getReadableDatabase();
        cursor = db.rawQuery("select * from star_table where wantedMobileInfoUrl = ?", new String[]{url});
        if(cursor.moveToNext()) {
            star.setChecked(true);
            checked = true;
        }
        else {
            star.setChecked(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SQLiteDatabase db = starDBHelper.getWritableDatabase();
        if(star.isChecked() && !checked) {
            ContentValues row = new ContentValues();
            row.put("company", company.getText().toString());
            row.put("title", title.getText().toString());
            row.put("salTpNm", salTp.getText().toString());
            row.put("sal", sal.getText().toString());
            row.put("region", region.getText().toString());
            row.put("holidayTpNm", holidayTpNm.getText().toString());
            row.put("minEdubg", minEdubg.getText().toString());
            row.put("career", career.getText().toString());
            row.put("closeDt", closeDt.getText().toString());
            row.put("wantedMobileInfoUrl", url);
            row.put("jobsCd", jobsCd.getText().toString());
            row.put("basicAddr", addr);
            db.insert("star_table", null, row);
            starDBHelper.close();
        }
        else if (!star.isChecked() && checked) {
            String deleteId = "wantedMobileInfoUrl=?";
            String[] delete = new String[] {url};
            db.delete("star_table", deleteId, delete);
        }
    }

    public void onClick(View v)
    {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_wanted_cancel:
                finish();
                break;
            case R.id.btn_wanted_apply:
                intent = new Intent(this, AddScheduleActivity.class);
                intent.putExtra("company", company.getText().toString());
                intent.putExtra("title", title.getText().toString());
                startActivity(intent);
                break;
            case R.id.tv_url:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                break;
        }
        finish();
    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            startLatLngService();
        }
    };

    private void startLatLngService() {
        Intent intent = new Intent(this, FetchLatLngIntentService.class);
        intent.putExtra(Constants.RECEIVER, latLngResultReceiver);
        intent.putExtra(Constants.ADDRESS_DATA_EXTRA, addr);
        startService(intent);
    }

    class LatLngResultReceiver extends ResultReceiver {
        public LatLngResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            ArrayList<LatLng> latLngList = null;
            if (resultCode == Constants.SUCCESS_RESULT) {
                if (resultData == null) return;
                latLngList = (ArrayList<LatLng>) resultData.getSerializable(Constants.RESULT_DATA_KEY);
                if (latLngList != null) {
                    LatLng latlng = latLngList.get(0);
                    moveCam(latlng.latitude, latlng.longitude);
                }
            } else {
                addrLayout.setVisibility(View.VISIBLE);
                basicAddr.setText(addr);
                mapFragment.getView().setVisibility(View.GONE);
                Toast.makeText(WantedViewActivity.this, "위치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void moveCam(double latitude, double longitude) {
        LatLng location = new LatLng(latitude, longitude);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17));

        MarkerOptions options = new MarkerOptions();
        options.position(location);
        options.title("주소");
        options.snippet(addr);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        centerMarker = mGoogleMap.addMarker(options);
        centerMarker.showInfoWindow();
    }
}