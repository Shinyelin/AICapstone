package com.hansung.android.smart_parking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import static com.hansung.android.smart_parking.ViewDatailActivity.result_piip;
/*추출된 글자가 잘못될 시 수정할 수 있는 액티비티*/
public class NumberModifyActivity extends AppCompatActivity {
    EditText ET_modiNum;
    String mJsonString = "";
    Bitmap data;
    ImageView IV_piimg2;
    private static String TAG = "ImageRoad2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_modify);
        ET_modiNum = (EditText) findViewById(R.id.et_modifynum);
        TextView TV_modi_piextract = (TextView) findViewById(R.id.tv_modi_piextract);
        TV_modi_piextract.setText(ViewDatailActivity.result_extract);
        TextView TV_modi_pitime = (TextView) findViewById(R.id.tv_modi_pitime);
        TV_modi_pitime.setText(ViewDatailActivity.result_time);
        IV_piimg2 = (ImageView) findViewById(R.id.iv_picam2);
        IV_piimg2.setImageResource(R.drawable.refresh);//썸네일
        NumberModifyActivity.GetData task = new NumberModifyActivity.GetData();
        task.execute(result_piip);

    }
    /*글자 수정과 벌금부과 동시에 진행*/
    public void onClick_modify_ok(View view) {
        NumberModifyActivity.Update_number task = new NumberModifyActivity.Update_number();
        task.execute("http://" + Data.IP_ADDRESS + "/update_extract.php", Data.aPlot, ViewDatailActivity.result_piname, ET_modiNum.getText().toString());
        NumberModifyActivity.Update_Penalty task_p = new NumberModifyActivity.Update_Penalty();
        task_p.execute("http://" + Data.IP_ADDRESS + "/update_penalty.php", ET_modiNum.getText().toString());
        Intent intent1 = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent1);
    }

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

                Toast.makeText(NumberModifyActivity.this, errorString,
                        Toast.LENGTH_SHORT).show();
            } else {

                mJsonString = result;

                showResult();

                //dataSetting();


            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];
            //String searchKeyword2 = params[1];


            String serverURL = "http://" + Data.IP_ADDRESS + "/loadImage.php";
            String postParameters = "id=" + searchKeyword1;


            try {

                StringBuilder sb = new StringBuilder();

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

                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                    while (true) {
                        String line = br.readLine();
                        if (line == null)
                            break;
                        sb.append(line + "\n");
                    }
                    br.close();
                }


                return sb.toString().trim();


            } catch (Exception e) {

                errorString = e.toString();

                return null;
            }

        }
    }

    private void showResult() {

        String TAG_JSON = "webnautes";


        try {

            JSONArray jArray = new JSONArray(mJsonString);


            JSONObject jsonObject = jArray.getJSONObject(0);
            data = StringToBitMap(jsonObject.getString("piImg"));
            Log.e("load_image", jsonObject.get("piImg").toString());
            IV_piimg2.setImageBitmap(data);


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }


    /* string을 bitmap으로 변경  */
    public static Bitmap StringToBitMap(String image) {
        Log.e("StringToBitMap", "StringToBitMap");
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            Log.e("StringToBitMap", "good");

            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
    /*추출된 글자 수정*/
    class Update_number extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(NumberModifyActivity.this, "Wait...", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

        }

        @Override
        protected String doInBackground(String... params) {
            String piplot = (String) params[1];
            String piname = (String) params[2];
            String piExtract = (String) params[3];

            String serverURL = (String) params[0];
            String postParameters = "piPlot=" + piplot + "&piName=" + piname + "&piExtract=" + piExtract;

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
                return new String("Error: " + e.getMessage());
            }
        }
    }
    /*벌금 업데이트*/
    class Update_Penalty extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(NumberModifyActivity.this, "Wait...", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

        }

        @Override
        protected String doInBackground(String... params) {
            String cnum = (String) params[1];

            String serverURL = (String) params[0];
            String postParameters = "cnum=" + cnum;

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
                return new String("Error: " + e.getMessage());
            }
        }
    }


}
