package com.hansung.android.smart_parking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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
/*지정주차 공간에 대한 상세 페이지*/
public class ViewDatailActivity extends AppCompatActivity {

    static String result_time, result_extract;
    static String result_piname, result_piip;
    Bitmap data;
    String mJsonString = "";
    static ImageView IV_piimg;
    Drawable refresh;
    private static String TAG = "ImageRoad";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Intent resultintent = getIntent();
        result_piip = resultintent.getExtras().getString("PIID", "아이디");
        result_piname = resultintent.getExtras().getString("PINAME", "이름");
        result_time = resultintent.getExtras().getString("PITIME", "시간");
        result_extract = resultintent.getExtras().getString("PIEXTRACT", "추출");
        TextView TV_piextract = (TextView) findViewById(R.id.tv_piextract);
        TV_piextract.setText(result_extract);
        TextView TV_pitime = (TextView) findViewById(R.id.tv_pitime);
        TV_pitime.setText(result_time);
        IV_piimg = (ImageView) findViewById(R.id.iv_picam);
        IV_piimg.setImageResource(R.drawable.refresh);//썸네일
        ViewDatailActivity.GetData task = new ViewDatailActivity.GetData();
        task.execute(result_piip);
    }
    /*일치 클릭 시 벌금 업데이트*/
    public void onClick_view_ok(View view) {
        ViewDatailActivity.Update_Penalty task_p = new ViewDatailActivity.Update_Penalty();
        task_p.execute("http://" + Data.IP_ADDRESS + "/update_penalty.php", result_extract);
        Intent intent1 = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent1);

    }
    /*불일치 클릭 시 번호 수정 액티비티로 이동*/

    public void onClick_view_notok(View view) {
        Intent intent = new Intent(this, NumberModifyActivity.class);
        finish();
        startActivity(intent);
    }
    /*이미지 데이터 가져옴*/
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

                Toast.makeText(ViewDatailActivity.this, errorString,
                        Toast.LENGTH_SHORT).show();
            } else {

                mJsonString = result;

                showResult();


            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];


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
            IV_piimg.setImageBitmap(data);


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }


    /**
     * string을 bitmap으로 바꿔서 표시할 수 있도록 함
     */
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
    /*벌금 업데이트*/
    class Update_Penalty extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ViewDatailActivity.this, "Wait...", null, true, true);
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
