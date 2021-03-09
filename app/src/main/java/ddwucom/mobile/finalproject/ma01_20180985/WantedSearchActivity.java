package ddwucom.mobile.finalproject.ma01_20180985;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class WantedSearchActivity extends AppCompatActivity {

    String regionCode;
    String jobCode;
    TextView region;
    TextView job;
    CheckBox cbEdu1;
    CheckBox cbEdu2;
    CheckBox cbEdu3;
    CheckBox cbEdu4;
    CheckBox cbHol1;
    CheckBox cbHol2;

    ArrayList<CheckBox> eduCbList;
    ArrayList<CheckBox> holiCbList;

    ArrayList<String> eduList;
    ArrayList<String> holiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wanted_search);

        eduCbList = new ArrayList<>();
        holiCbList = new ArrayList<>();
        eduList = new ArrayList<>();
        holiList = new ArrayList<>();

        region = findViewById(R.id.tv_selected_region);
        job = findViewById(R.id.tv_selected_job);
        cbEdu1 = findViewById(R.id.cb_edu_1);
        cbEdu2 = findViewById(R.id.cb_edu_2);
        cbEdu3 = findViewById(R.id.cb_edu_3);
        cbEdu4 = findViewById(R.id.cb_edu_4);
        cbHol1 = findViewById(R.id.cb_hol_1);
        cbHol2 = findViewById(R.id.cb_hol_2);

        eduCbList.add(cbEdu1);
        eduCbList.add(cbEdu2);
        eduCbList.add(cbEdu3);
        eduCbList.add(cbEdu4);
        holiCbList.add(cbHol1);
        holiCbList.add(cbHol2);
    }

    public void check1() {
        for(int i = 0; i < 4; i++) {
            if (eduCbList.get(i).isChecked()) {
                if (i == 0) {
                    eduList.add("03");
                } else if (i == 1) {
                    eduList.add("04");
                } else if (i == 2) {
                    eduList.add("05");
                } else if (i == 3) {
                    eduList.add("00");
                }
            }
        }
    }

    public void check2() {
        for(int i = 0; i < 2; i++) {
            if (holiCbList.get(i).isChecked()) {
                if (i == 0) {
                    holiList.add("1");
                } else if (i == 1) {
                    holiList.add("2");
                }
            }
        }
    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_region_select:
                intent = new Intent(this, RegionActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.btn_job_select:
                intent = new Intent(this, JobsActivity.class);
                startActivityForResult(intent, 200);
                break;
            case R.id.btn_search:
                check1();
                check2();
                intent = new Intent();
                intent.putExtra("region_code", regionCode);
                intent.putExtra("job_code", jobCode);
                if(eduList.size() > 0) {
                    intent.putExtra("eduList", eduList);
                }
                if(holiList.size() > 0) {
                    intent.putExtra("holiList", holiList);
                }
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    region.setText(data.getStringExtra("region_name"));
                    regionCode = data.getStringExtra("region_code");
                }
                break;
            case 200:
                if (resultCode == RESULT_OK) {
                    job.setText(data.getStringExtra("job_name"));
                    jobCode = data.getStringExtra("job_code");
                }
                break;
        }
    }
}
