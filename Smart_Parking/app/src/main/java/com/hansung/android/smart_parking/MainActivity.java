package com.hansung.android.smart_parking;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Button btn_reset;
    private ArrayList<PicamData> piArrayList_p, piArrayList_d;
    private PicamAdapter_p piAdapter_p;
    private PicamAdapter_d piAdapter_d;
    private GridView mListView;
    private GridView mListView2;

    private static String TAG = "PorD";
    int pord = 0;  //0이면 d 1이면 p

    String mJsonString = "";
    long now;
    Date date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* 위젯과 멤버변수 참조 획득 */
        mListView = (GridView) findViewById(R.id.listView);
        mListView2 = (GridView) findViewById(R.id.listView2);
        TextView tv_parkname = (TextView) findViewById(R.id.park_name);
        tv_parkname.setText(Data.aPlot);
        TextView tv_parkaddr = (TextView) findViewById(R.id.park_address);
        tv_parkaddr.setText(Data.aAddress);
        btn_reset = (Button) findViewById(R.id.park_reset);


        now = System.currentTimeMillis();
        date = new Date(now);
        SimpleDateFormat sdfnow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String strnow = sdfnow.format(date);
        TextView tv_updatetime = (TextView) findViewById(R.id.park_update_time);
        tv_updatetime.setText(strnow);
        piArrayList_p = new ArrayList<>();
        piAdapter_p = new PicamAdapter_p(this, piArrayList_p);
        MainActivity.GetData task = new MainActivity.GetData();
        task.execute(Data.aPlot, "P");
        piArrayList_d = new ArrayList<>();
        piAdapter_d = new PicamAdapter_d(this, piArrayList_d);
        MainActivity.GetData task2 = new MainActivity.GetData();
        task2.execute(Data.aPlot, "D");

        mListView.setAdapter(piAdapter_p);
        mListView2.setAdapter(piAdapter_d);
        /*버튼 클릭 시 상세페이지로 이동*/
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent2 = new Intent(MainActivity.this, ViewDatailActivity.class);
                intent2.putExtra("PIID", piArrayList_p.get(position).get_piId());
                intent2.putExtra("PINAME", piArrayList_p.get(position).get_piName());
                intent2.putExtra("PITIME", piArrayList_p.get(position).get_piTime());
                intent2.putExtra("PIEXTRACT", piArrayList_p.get(position).get_piExtract());
                finish();
                startActivity(intent2);
            }
        });
        mListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent2 = new Intent(MainActivity.this, ViewDatailActivity.class);
                intent2.putExtra("PIID", piArrayList_d.get(position).get_piId());
                intent2.putExtra("PINAME", piArrayList_d.get(position).get_piName());
                intent2.putExtra("PITIME", piArrayList_d.get(position).get_piTime());
                intent2.putExtra("PIEXTRACT", piArrayList_d.get(position).get_piExtract());

                finish();
                startActivity(intent2);
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);
    }
    /*우측 상단의 버튼메뉴*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_penalty) {
            Intent intent1 = new Intent(this, PenaltySearchActivity.class); //벌금 조회 페이지로 이동
            startActivity(intent1);
            return true;
        }
        if (id == R.id.menu_register) {
            Intent intent1 = new Intent(this, CarRegisterActivity.class);   //차량 등록 페이지로 이동
            startActivity(intent1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void car_register(View view) {
        Intent intent1 = new Intent(this, CarRegisterActivity.class);
        startActivity(intent1);
    }

    public void onClick_reset(View view) {
        Intent intent1 = new Intent(this, MainActivity.class);  //새로고침
        finish();
        startActivity(intent1);
    }
    /*앱에서 클릭된 지정주차 공간의 데이터를 가져오는 코드*/
    private class GetData extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            Log.d(TAG, "response - " + result);

            if (result == null) {

                Toast.makeText(MainActivity.this, errorString,
                        Toast.LENGTH_SHORT).show();
            } else {

                mJsonString = result;

                showResult();


            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];
            String searchKeyword2 = params[1];


            String serverURL = "http://" + Data.IP_ADDRESS + "/query_piInfo.php";
            String postParameters = "piPlot=" + searchKeyword1 + "&piPorD=" + searchKeyword2;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private void showResult() {

        String TAG_JSON = "webnautes";
        String TAG_PIID = "id";
        String TAG_PINAME = "piName";
        String TAG_PITIME = "piTime";
        String TAG_PIEXTRACT = "piExtract";
        String TAG_PITF = "piTF";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String piid = item.getString(TAG_PIID);
                String piname = item.getString(TAG_PINAME);
                String pitime = item.getString(TAG_PITIME);
                String piextract = item.getString(TAG_PIEXTRACT);
                String pitf = item.getString(TAG_PITF);
                PicamData picamData = new PicamData();

                // picamData.setMember_cnum(cnum);
                picamData.set_piId(piid);
                picamData.set_piName(piname);
                picamData.set_piTime(pitime);
                picamData.set_piExtract(piextract);
                picamData.set_piTF(pitf);
                if (pord == 0) {

                    piArrayList_p.add(picamData);

                    piAdapter_p.notifyDataSetChanged();
                }


                if (pord == 1) {
                    piArrayList_d.add(picamData);

                    piAdapter_d.notifyDataSetChanged();
                }


            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }
        pord++;
    }

}
