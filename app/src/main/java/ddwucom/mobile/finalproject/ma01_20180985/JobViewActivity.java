package ddwucom.mobile.finalproject.ma01_20180985;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class JobViewActivity extends AppCompatActivity {
     TextView jobLrclNm;
     TextView jobMdclNm;
     TextView jobSmclNm;
     TextView jobSum;
     TextView way;
     TextView sal;
     TextView jobSatis;
     TextView jobProspect;
     TextView jobStatus;
     TextView jobAbil;
     TextView jobChr;
     TextView jobIntrst;
     TextView jobVals;

     JobDetail j;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_view);

        Intent intent = getIntent();
        jobLrclNm = findViewById(R.id.tv_jobLrclNm);
        jobMdclNm = findViewById(R.id.tv_jobMdclNm);
        jobSmclNm = findViewById(R.id.tv_jobSmclNm);
        jobSum = findViewById(R.id.tv_jobSum);
        way = findViewById(R.id.tv_way);
        sal = findViewById(R.id.tv_job_sal);
        jobSatis = findViewById(R.id.tv_jobSatis);
        jobProspect = findViewById(R.id.tv_jobProspect);
        jobStatus = findViewById(R.id.tv_jobStatus);
        jobAbil = findViewById(R.id.tv_jobAbil);
        jobChr = findViewById(R.id.tv_jobChr);
        jobIntrst = findViewById(R.id.tv_jobIntrst);
        jobVals = findViewById(R.id.tv_jobVals);

        j = (JobDetail) intent.getSerializableExtra("j");
        jobLrclNm.setText(j.getJobLrclNm());
        jobMdclNm.setText(j.getJobMdclNm());
        jobSmclNm.setText(j.getJobSmclNm());
        jobSum.setText(j.getJobSum());
        way.setText(j.getWay());
        sal.setText(j.getSal());
        jobSatis.setText(j.getJobSatis());
        jobProspect.setText(j.getJobProspect());
        jobStatus.setText(j.getJobStatus());
        jobAbil.setText(j.getJobAbil());
        jobChr.setText(j.getJobChr());
        jobIntrst.setText(j.getJobIntrst());
        jobVals.setText(j.getJobVals());
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_job_view_back:
                finish();
                break;
        }
    }
}
