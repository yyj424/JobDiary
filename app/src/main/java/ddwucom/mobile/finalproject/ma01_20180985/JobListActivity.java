package ddwucom.mobile.finalproject.ma01_20180985;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class JobListActivity extends AppCompatActivity {

    ListView lvList;
    String apiAddress;

    JobListAdapter adapter;
    List<Job> resultList;

    String total = null;
    TextView t;
    EditText keyword;

    JobDetail j = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_list);

        lvList = findViewById(R.id.lv_jobList);
        t = findViewById(R.id.tv_job_list_total);
        keyword = findViewById(R.id.et_keyword);

        resultList = new ArrayList<Job>();
        adapter = new JobListAdapter(this, (ArrayList<Job>) resultList);

        lvList.setAdapter(adapter);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                apiAddress = getResources().getString(R.string.jobDetail_url);

                j = new JobDetail();
                new NetworkAsyncTask().execute(apiAddress + resultList.get(position).getJobCd());
            }
        });
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_keyword_job_search:
                if (keyword.getText() == null || keyword.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(this, "키워드를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        resultList.clear();
                        apiAddress = getResources().getString(R.string.jobList_url);
                        new NetworkAsyncTask().execute(apiAddress + URLEncoder.encode(keyword.getText().toString(), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    class NetworkAsyncTask extends AsyncTask<String, Void, String> {

        final static String NETWORK_ERR_MSG = "Server Error!";
        public final static String TAG = "NetworkAsyncTask";
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(JobListActivity.this, "Wait", "Downloading...");     // 진행상황 다이얼로그 출력
        }

        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            String result = downloadContents(address);
            if (result == null) {
                cancel(true);
                return NETWORK_ERR_MSG;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            progressDlg.dismiss();  // 진행상황 다이얼로그 종료

//          parser 생성 및 OpenAPI 수신 결과를 사용하여 parsing 수행
            JobXmlParser parser = new JobXmlParser();
            if (j != null) {
                j = parser.parse3(result);
                if (j == null) {
                    Toast.makeText(JobListActivity.this, "수신 실패", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(JobListActivity.this, JobViewActivity.class);
                    intent.putExtra("j", j);
                    j = null;
                    startActivity(intent);
                }
            }
            else {
                resultList.addAll(parser.parse1(result));
                if (resultList.size() == 0) {
                    t.setText("0");
                    Toast.makeText(JobListActivity.this, "정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    total = parser.parse2(result);
                    if (total == null) {
                        Toast.makeText(JobListActivity.this, "total값 수신 실패", Toast.LENGTH_SHORT).show();
                    } else if (total != null) {
                        t.setText(total);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onCancelled(String msg) {
            super.onCancelled();
            progressDlg.dismiss();
            Toast.makeText(JobListActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }


    /* 이하 네트워크 접속을 위한 메소드 */


    /* 네트워크 환경 조사 */
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    /* 주소(apiAddress)에 접속하여 문자열 데이터를 수신한 후 반환 */
    protected String downloadContents(String address) {
        HttpURLConnection conn = null;
        InputStream stream = null;
        String result = null;

        try {
            URL url = new URL(address);
            conn = (HttpURLConnection)url.openConnection();
            stream = getNetworkConnection(conn);
            result = readStreamToString(stream);
            if (stream != null) stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();
        }

        return result;
    }

    /* URLConnection 을 전달받아 연결정보 설정 후 연결, 연결 후 수신한 InputStream 반환 */
    private InputStream getNetworkConnection(HttpURLConnection conn) throws Exception {
        conn.setReadTimeout(6000);
        conn.setConnectTimeout(6000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code: " + conn.getResponseCode());
        }

        return conn.getInputStream();
    }

    /* InputStream을 전달받아 문자열로 변환 후 반환 */
    protected String readStreamToString(InputStream stream){
        StringBuilder result = new StringBuilder();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String readLine = bufferedReader.readLine();

            while (readLine != null) {
                result.append(readLine + "\n");
                readLine = bufferedReader.readLine();
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}
