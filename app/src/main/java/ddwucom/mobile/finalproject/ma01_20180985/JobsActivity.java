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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class JobsActivity extends AppCompatActivity {

    ListView lvList;

    ArrayAdapter<Job> adapter;

    List<Job> resultList;

    TextView name;
    String code = null;
    String apiAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);

        name = findViewById(R.id.tv_select_job);
        lvList = (ListView)findViewById(R.id.lv_jobs);

        resultList = new ArrayList<Job>();

        adapter = new ArrayAdapter<Job>(this, android.R.layout.simple_list_item_1, resultList);
        lvList.setAdapter(adapter);

        apiAddress = getResources().getString(R.string.jobs_url);

        lvList.setOnItemClickListener(listener);

        if (!isOnline()) {
            Toast.makeText(this, "네트워크를 사용가능하게 설정해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        new JobsActivity.NetworkAsyncTask().execute(apiAddress);
    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            name.setText(resultList.get(position).getJobNm());
            code = resultList.get(position).getJobCd();
        }
    };

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_job_select_complete:
                Intent intent = new Intent();
                intent.putExtra("job_name", name.getText().toString());
                intent.putExtra("job_code", code);
                setResult(RESULT_OK, intent);
                break;
        }
        finish();
    }

    class NetworkAsyncTask extends AsyncTask<String, Void, String> {

        final static String NETWORK_ERR_MSG = "Server Error!";
        public final static String TAG = "NetworkAsyncTask";
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(JobsActivity.this, "Wait", "Downloading...");     // 진행상황 다이얼로그 출력
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
            adapter.clear();        // 어댑터에 남아있는 이전 내용이 있다면 클리어

//          parser 생성 및 OpenAPI 수신 결과를 사용하여 parsing 수행
            JobXmlParser parser = new JobXmlParser();
            resultList = parser.parse4(result);
            if (resultList == null) {
                Toast.makeText(JobsActivity.this, "리스트1 수신 실패", Toast.LENGTH_SHORT).show();
            } else if (!resultList.isEmpty()) {
                adapter.addAll(resultList);     // 리스트뷰에 연결되어 있는 어댑터에 parsing 결과 ArrayList 를 추가
            }
        }

        @Override
        protected void onCancelled(String msg) {
            super.onCancelled();
            progressDlg.dismiss();
            Toast.makeText(JobsActivity.this, msg, Toast.LENGTH_SHORT).show();
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
        conn.setReadTimeout(3000);
        conn.setConnectTimeout(3000);
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
