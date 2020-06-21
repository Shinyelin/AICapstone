package com.hansung.android.smart_parking;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CarRegisterActivity extends AppCompatActivity {
    private static String TAG = "car";
    EditText et_cnum, et_cowner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_register);

        et_cnum = (EditText) findViewById(R.id.et_cnum);
        et_cowner = (EditText) findViewById(R.id.et_cowner);

    }

    /*차량등록 완료 버튼 클릭 시 데이터베이스에 번호와 소유주명 저장*/
    public void car_register_submitClick(View view) {

        InsertData task = new InsertData();
        task.execute("http://49.50.175.183/insert_car.php",
                et_cnum.getText().toString(),
                et_cowner.getText().toString());
        Intent intent =
                new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(CarRegisterActivity.this, "Wait...", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "Post response -" + result);
        }

        @Override
        protected String doInBackground(String... params) {
            String cnum = (String) params[1];
            String cowner = (String) params[2];

            String serverURL = (String) params[0];
            String postParameters = "cnum=" + cnum + "&cowner=" + cowner;

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

