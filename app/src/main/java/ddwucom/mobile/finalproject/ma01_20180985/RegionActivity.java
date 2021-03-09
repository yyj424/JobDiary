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

public class RegionActivity extends AppCompatActivity {

    ListView lvList1;
    ListView lvList2;
    String apiAddress;

    ArrayAdapter<Region> adapter1;
    ArrayAdapter<Region> adapter2;

    List<Region> resultList1;
    List<Region> resultList2;
    TextView name;
    String superCd = null;
    String code = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region);

        name = findViewById(R.id.tv_selected_region_name);
        lvList1 = (ListView)findViewById(R.id.lv_superRegion);
        lvList2 = (ListView)findViewById(R.id.lv_region);

        resultList1 = new ArrayList<Region>();
        resultList2 = new ArrayList<Region>();

        adapter1 = new ArrayAdapter<Region>(this, android.R.layout.simple_list_item_1, resultList1);
        lvList1.setAdapter(adapter1);
        adapter2 = new ArrayAdapter<Region>(this, android.R.layout.simple_list_item_1, resultList2);
        lvList2.setAdapter(adapter2);

        apiAddress = getResources().getString(R.string.region_url);

        lvList1.setOnItemClickListener(listener1);
        lvList2.setOnItemClickListener(listener2);

        if (!isOnline()) {
            Toast.makeText(RegionActivity.this, "네트워크를 사용가능하게 설정해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        new RegionActivity.NetworkAsyncTask().execute(apiAddress);
    }

    AdapterView.OnItemClickListener listener1 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            resultList2.clear();
            name.setText(resultList1.get(position).getName());
            superCd = resultList1.get(position).getSuperCd();
            code = resultList1.get(position).getSuperCd();

            if(code.equals("00000")) {
                adapter2.clear();
            }
            resultList2.addAll(resultList1.get(position).getrList());
            adapter2.notifyDataSetChanged();
        }
    };

    AdapterView.OnItemClickListener listener2 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            name.setText(resultList2.get(position).getName());
            code = resultList2.get(position).getCode();
        }
    };

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_region_select_complete:
                Intent intent = new Intent();
                intent.putExtra("region_name", name.getText().toString());
                intent.putExtra("region_code", code);
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
            progressDlg = ProgressDialog.show(RegionActivity.this, "Wait", "Downloading...");     // 진행상황 다이얼로그 출력
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
            adapter1.clear();        // 어댑터에 남아있는 이전 내용이 있다면 클리어

//          parser 생성 및 OpenAPI 수신 결과를 사용하여 parsing 수행
            RegionXmlParser parser = new RegionXmlParser();
             resultList1 = parser.parse1(result);
             if (resultList1 == null) {       // 올바른 결과를 수신하지 못하였을 경우 안내
                 Toast.makeText(RegionActivity.this, "리스트1 수신 실패", Toast.LENGTH_SHORT).show();
             } else if (!resultList1.isEmpty()) {
                 adapter1.addAll(resultList1);     // 리스트뷰에 연결되어 있는 어댑터에 parsing 결과 ArrayList 를 추가
             }
        }

        @Override
        protected void onCancelled(String msg) {
            super.onCancelled();
            progressDlg.dismiss();
            Toast.makeText(RegionActivity.this, msg, Toast.LENGTH_SHORT).show();
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
