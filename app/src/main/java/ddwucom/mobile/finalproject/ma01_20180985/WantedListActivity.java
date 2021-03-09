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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class WantedListActivity extends AppCompatActivity {

    ListView lvList;
    String apiAddress;

    WantedListAdapter adapter;
    List<Wanted> resultList;

    String total;
    TextView startpg;
    TextView lastpg;
    int p = 1;
    int lp = 1;
    String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wanted_list);

        lvList = findViewById(R.id.lv_wanted);
        startpg = findViewById(R.id.tv_wanted_startpg);
        lastpg = findViewById(R.id.tv_wanted_lastpg);

        resultList = new ArrayList<Wanted>();
        adapter = new WantedListAdapter(this, (ArrayList<Wanted>) resultList);

        lvList.setAdapter(adapter);

        apiAddress = getResources().getString(R.string.wanted_url);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(WantedListActivity.this, WantedViewActivity.class);
                intent.putExtra("company", resultList.get(position).getCompany());
                intent.putExtra("title", resultList.get(position).getTitle());
                intent.putExtra("salTp", resultList.get(position).getSalTpNm());
                intent.putExtra("sal", resultList.get(position).getSal());
                intent.putExtra("region", resultList.get(position).getRegion());
                intent.putExtra("holiTp", resultList.get(position).getHolidayTpNm());
                intent.putExtra("minEdu", resultList.get(position).getMinEdubg());
                intent.putExtra("career", resultList.get(position).getCareer());
                intent.putExtra("closeDt", resultList.get(position).getCloseDt());
                intent.putExtra("url", resultList.get(position).getWantedMobileInfoUrl());
                intent.putExtra("jobsCd", resultList.get(position).getJobsCd());
                intent.putExtra("basicAddr", resultList.get(position).getBasicAddr());
                startActivity(intent);
            }
        });
    }


    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_wantedSearch:
                total = null;
                Intent intent = new Intent(this, WantedSearchActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.btn_wantedList_previous:
                p = p - 1;
                if (p == 0) {
                    p++;
                    Toast.makeText(this, "첫번째 페이지 입니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    new NetworkAsyncTask().execute(apiAddress + query + "&startPage=" + p);
                }
                startpg.setText(String.valueOf(p));
                break;
            case R.id.btn_wantedList_next:
                p = p + 1;
                if (p > lp) {
                    p--;
                    Toast.makeText(this, "마지막 페이지 입니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    new NetworkAsyncTask().execute(apiAddress + query + "&startPage=" + p);
                    startpg.setText(String.valueOf(p));
                }
                break;
            case R.id.btn_getStarList:
                Intent sintent = new Intent(this, StarListActivity.class);
                startActivity(sintent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    query = "";
                    HashMap<String, String> addr = new HashMap<String, String>();
                    String regionCode = data.getStringExtra("region_code");
                    if (regionCode != null) {
                        addr.put("region", regionCode);
                    }
                    String jobCode = data.getStringExtra("job_code");
                    if(jobCode != null) {
                        addr.put("occupation", jobCode);
                    }
                    ArrayList<String> eduList = new ArrayList<>();
                    ArrayList<String> holiList = new ArrayList<>();
                    if(data.getStringArrayListExtra("eduList") != null) {
                        eduList.addAll(data.getStringArrayListExtra("eduList"));
                        String edu = null;
                        for (int i = 0; i < eduList.size(); i++) {
                            if (edu != null) {
                                edu += eduList.get(i);
                            }
                            else {
                                edu = eduList.get(i);
                            }
                            if (i != eduList.size() - 1) {
                                edu += "|";
                            }
                        }
                        if(edu != null) {
                            addr.put("education", edu);
                        }
                    }
                    if(data.getStringArrayListExtra("holiList") != null) {
                        holiList.addAll(data.getStringArrayListExtra("holiList"));
                        String holi = null;
                        for (int i = 0; i < holiList.size(); i++) {
                            if (holi != null) {
                                holi += holiList.get(i);
                            }
                            else {
                                holi = holiList.get(i);
                            }
                            if (i != holiList.size() - 1) {
                                holi += "|";
                            }
                        }
                        if(holi != null) {
                            addr.put("holidayTp", holi);
                        }
                    }
                    Iterator<String> keys = addr.keySet().iterator();
                    while(keys.hasNext()){
                        String key = keys.next();
                        query = query + key + "=" + addr.get(key);
                        if (keys.hasNext())
                            query += "&";
                    }
                    if (!isOnline()) {
                        Toast.makeText(this, "네트워크를 사용가능하게 설정해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new NetworkAsyncTask().execute(apiAddress + query + "&startPage=" + p);
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
            progressDlg = ProgressDialog.show(WantedListActivity.this, "Wait", "Downloading...");     // 진행상황 다이얼로그 출력
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
            resultList.clear();        // 어댑터에 남아있는 이전 내용이 있다면 클리어

//          parser 생성 및 OpenAPI 수신 결과를 사용하여 parsing 수행
            WantedXmlParser parser = new WantedXmlParser();

            resultList.addAll(parser.parse1(result));
            adapter.notifyDataSetChanged();
            if (total == null) {
                total = parser.parse2(result);
                if (total == null) {
                    Toast.makeText(WantedListActivity.this, "정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                else if (total != null) {
                    lp = Integer.parseInt(total) / 20 + 1;
                }
                lastpg.setText(String.valueOf(lp));
            }
        }

        @Override
        protected void onCancelled(String msg) {
            super.onCancelled();
            progressDlg.dismiss();
            Toast.makeText(WantedListActivity.this, msg, Toast.LENGTH_SHORT).show();
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
