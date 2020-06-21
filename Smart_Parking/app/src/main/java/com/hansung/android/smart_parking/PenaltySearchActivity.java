package com.hansung.android.smart_parking;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
/*벌금조회 액티비티*/
public class PenaltySearchActivity extends AppCompatActivity {
    EditText ET_cnum;
    TextView TV_cowner, TV_cpenalty;
    String mJsonString = "";
    private static String TAG = "Penalty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penalty_search);
        ET_cnum = (EditText) findViewById(R.id.et_cnum);
        TV_cowner = (TextView) findViewById(R.id.tv_cowner);
        TV_cpenalty = (TextView) findViewById(R.id.tv_cpenatly);


    }
    /*조회할 번호 입력 후 벌금 조회*/
    public void search_penalty(View view) {
        PenaltySearchActivity.GetData task = new PenaltySearchActivity.GetData();
        task.execute(ET_cnum.getText().toString());

    }
    /*조회된 벌금을 가져옴*/
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

                Toast.makeText(PenaltySearchActivity.this, errorString,
                        Toast.LENGTH_SHORT).show();
            } else {

                mJsonString = result;

                showResult();


            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];


            String serverURL = "http://" + Data.IP_ADDRESS + "/query_penalty.php";
            String postParameters = "cnum=" + searchKeyword1;


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
        String TAG_COWNER = "cowner";
        String TAG_CPENALTY = "cpenalty";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String cowner = item.getString(TAG_COWNER);
                String cpenalty = item.getString(TAG_CPENALTY);

                TV_cowner.setText(cowner);
                TV_cpenalty.setText(cpenalty);
            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
}
