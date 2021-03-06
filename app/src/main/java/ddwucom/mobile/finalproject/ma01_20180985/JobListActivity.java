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
                    Toast.makeText(this, "???????????? ???????????????.", Toast.LENGTH_SHORT).show();
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
            progressDlg = ProgressDialog.show(JobListActivity.this, "Wait", "Downloading...");     // ???????????? ??????????????? ??????
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
            progressDlg.dismiss();  // ???????????? ??????????????? ??????

//          parser ?????? ??? OpenAPI ?????? ????????? ???????????? parsing ??????
            JobXmlParser parser = new JobXmlParser();
            if (j != null) {
                j = parser.parse3(result);
                if (j == null) {
                    Toast.makeText(JobListActivity.this, "?????? ??????", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(JobListActivity.this, "????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                }
                else {
                    total = parser.parse2(result);
                    if (total == null) {
                        Toast.makeText(JobListActivity.this, "total??? ?????? ??????", Toast.LENGTH_SHORT).show();
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


    /* ?????? ???????????? ????????? ?????? ????????? */


    /* ???????????? ?????? ?????? */
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    /* ??????(apiAddress)??? ???????????? ????????? ???????????? ????????? ??? ?????? */
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

    /* URLConnection ??? ???????????? ???????????? ?????? ??? ??????, ?????? ??? ????????? InputStream ?????? */
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

    /* InputStream??? ???????????? ???????????? ?????? ??? ?????? */
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
