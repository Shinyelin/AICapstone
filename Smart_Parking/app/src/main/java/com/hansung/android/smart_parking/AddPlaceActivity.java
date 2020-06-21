package com.hansung.android.smart_parking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class AddPlaceActivity extends AppCompatActivity {
    private static String TAG = "place";
    EditText et_pname, et_padd, et_pregno, et_disno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        et_pname = (EditText) findViewById(R.id.et_pname); //주차장 이름
        et_padd = (EditText) findViewById(R.id.et_padd); //주차장 주소
        et_pregno = (EditText) findViewById(R.id.et_pregno);
        et_disno = (EditText) findViewById(R.id.et_disno);

    }
    /*데이터베이스에 주차장 추가 실행*/
    public void onClick_addplace_submit(View view) {

        AddPlaceActivity.InsertData_place task =
                new AddPlaceActivity.InsertData_place();
        task.execute("http://49.50.175.183/insert_place.php",
                et_pname.getText().toString(),
                et_padd.getText().toString(),
                et_pregno.getText().toString(),
                et_disno.getText().toString());
        Intent intent =
                new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    //데이터베이스에 주차장 추가 코드
    class InsertData_place extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(AddPlaceActivity.this, "Wait...", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "Post response -" + result);
        }

        @Override
        protected String doInBackground(String... params) {
            String pName = (String) params[1];
            String pAdd = (String) params[2];
            String pregNo = (String) params[3];
            String disNo = (String) params[4];

            String serverURL = (String) params[0];
            String postParameters = "pName=" + pName + "&pAdd=" + pAdd + "&pregNo=" + pregNo + "&disNo=" + disNo;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();


            } catch (Exception e) {
                Log.d(TAG, "InsertData : Error", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }
}
