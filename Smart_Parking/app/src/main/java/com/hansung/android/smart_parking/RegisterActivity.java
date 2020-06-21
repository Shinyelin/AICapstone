package com.hansung.android.smart_parking;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/*회원가입 액티비티*/
public class RegisterActivity extends AppCompatActivity {

    private static String TAG = "admin";
    private AlertDialog dialog;
    private boolean validate = false; //아이디 검증
    private boolean pChoice = false; //근무지 선택
    String aplot="";
    EditText et_aid, et_apwd, et_apwd_confirm, getEt_aid_confirm;
    ImageView setImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_aid = (EditText)findViewById(R.id.et_aid);
        et_apwd = (EditText)findViewById(R.id.et_apwd);
        et_apwd_confirm = (EditText)findViewById(R.id.et_apwd_confirm);
        setImage = (ImageView)findViewById(R.id.setImage);

        //비밀번호 일치 여부를 V, X 로 판단
        et_apwd_confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(et_apwd.getText().toString().equals(et_apwd_confirm.getText().toString())){
                    setImage.setImageResource(R.drawable.yes);
                }else{
                    setImage.setImageResource(R.drawable.no);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        //라디오 버튼으로 주차장 선택 가능
        final Button workplacebtn = (Button)findViewById(R.id.btn_aplot);
        workplacebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] WorkPlace = new String[]{"han","한성대학교 주차장","신촌 현대백화점 주차장","올림픽공원 주차장"
                        ,"창천프라자 주차장","세학 민영 주차장"};
                final int[] selectedIndex={0};

                AlertDialog.Builder maindialog = new AlertDialog.Builder(RegisterActivity.this);
                maindialog.setTitle("근무장소를 선택하세요")
                        .setSingleChoiceItems(WorkPlace
                                , 0
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        selectedIndex[0] = which;
                                    }
                                })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(RegisterActivity.this, WorkPlace[selectedIndex[0]],
                                        Toast.LENGTH_LONG).show();
                                workplacebtn.setText(WorkPlace[selectedIndex[0]]);
                                aplot =WorkPlace[selectedIndex[0]];

                            }
                        }).create().show();

            }
        });

        //회원가입 시 아이디가 사용가능한지 검증하는 부분
        final Button validateButton = (Button)findViewById(R.id.validateButton);
        validateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view2){
                final String aId = et_aid.getText().toString();
                if(validate){
                    return;
                }
                if(aId.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("아이디를 입력해주세요.")
                            .setPositiveButton("OK", null)
                            .create();
                    dialog.show();
                    return;
                }

                //검증 시작
                Response.Listener<String> responseListener = new
                        Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    Toast.makeText(RegisterActivity.this, response,
                                            Toast.LENGTH_LONG).show();
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if(success) { //사용할 수 있는 아이디라면
                                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                        dialog = builder.setMessage("아이디를 사용할 수 있습니다.")
                                                .setPositiveButton("OK", null)
                                                .create();
                                        dialog.show();
                                        et_aid.setEnabled(false); //아이디값을 바꿀 수 없도록 함
                                        validate = true; //검증완료
                                    } else{ //사용할 수 없는 아이디라면
                                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                        dialog = builder.setMessage("이미 사용중인 아이디입니다.")
                                                .setNegativeButton("OK", null)
                                                .create();
                                        dialog.show();
                                    }
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }; //Response.Listener 완료

                //Volley 라이브러리를 이용해서 실제 서버와 통신을 구현하는 부분
                ValidateRequest validateRequest = new ValidateRequest(aId, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validateRequest);
            }
        });


        //회원가입 버튼이 눌렸을 때 (아이디 검증)
        final Button submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //조건 모두 충족 시, 데이터베이스에 아이디와 패스워드 저장
                RegisterActivity.InsertData_admin task = new RegisterActivity.InsertData_admin();
                task.execute("http://49.50.175.183/insert_admin.php",et_aid.getText().toString(),et_apwd.getText().toString(),aplot);
                Intent intent1 = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent1);

                //ID 중복체크를 하지 않은 경우
                if(!validate){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("아이디 중복체크를 해주세요")
                            .setNegativeButton("Ok", null)
                            .create();
                    dialog.show();
                    return;
                }

                //한 칸이라도 빠뜨렸을 경우
                if(et_aid.equals("")||et_apwd.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("Empty text exist")
                            .setNegativeButton("OK", null)
                            .create();
                    dialog.show();
                    return;
                }

                //비밀번호가 일치하지 않은 경우
                if(!et_apwd.getText().toString().equals(et_apwd_confirm.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    et_apwd.setText("");
                    et_apwd_confirm.setText("");
                    et_apwd.requestFocus();
                    return;
                }

            }
        });

    }


    public void onClick_addplace(View view){
        Intent intent1 = new Intent(this,AddPlaceActivity.class);
        startActivity(intent1);
    }

    class InsertData_admin extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            progressDialog = ProgressDialog.show(RegisterActivity.this,"Wait...",null,true,true);
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG,"Post response -"+result);
        }

        @Override
        protected String doInBackground(String... params){
            String aId = (String)params[1];
            String aPwd = (String)params[2];
            String aPlot = (String)params[3];

            String serverURL = (String)params[0];
            String postParameters = "aId="+aId+"&aPwd="+aPwd+"&aPlot="+aPlot;

            try{
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
                Log.d(TAG, "POST response code - "+responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode==HttpURLConnection.HTTP_OK){
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line=bufferedReader.readLine())!=null){
                    sb.append(line);
                }

                bufferedReader.close();

                return  sb.toString();


            }catch (Exception e){
                Log.d(TAG,"InsertData : Error", e);
                return new String("Error: "+e.getMessage());
            }
        }
    }

}
