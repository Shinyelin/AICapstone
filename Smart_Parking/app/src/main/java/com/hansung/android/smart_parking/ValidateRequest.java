package com.hansung.android.smart_parking;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

//해당 클래스는 URL에 POST방식으로 파라미터 전송
//여기서는 아이디값이 이미 가입된 아이디인지 검증
public class ValidateRequest extends StringRequest {
    final static private String URL = "http://"+Data.IP_ADDRESS+"/UserValidate.php";
    private Map<String, String> parameters;

    public ValidateRequest(String aId, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        //해당 URL에 POST방식으로 파라미터들을 전송함
        parameters = new HashMap<>();
        parameters.put("aId", aId);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError{
        return parameters;
    }
}
