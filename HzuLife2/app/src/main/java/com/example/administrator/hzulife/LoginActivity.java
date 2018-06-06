package com.example.administrator.hzulife;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.administrator.hzulife.model.bean.User;
import com.example.administrator.hzulife.util.OkHttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * <pre>
 *     author : 蔡文豪
 *     e-mail : 1261654234@qq.com
 *     time   : 2018/5/1
 *     desc   : 登录页面
 *     version: 1.0
 * </pre>
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private Button bn_Login;
    private EditText et_username;
    private EditText et_password;
    private Context context;
    private CheckBox auto_login;
    private TextView forget_pw;
    private SharedPreferences sp;
    private String username;
    private String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context=LoginActivity.this;
        initView();
        autoLogin();
    }

    /**
     * 登录
     */
    private  void login(){
        InputMethodManager imm = (InputMethodManager) context.getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_password.getWindowToken(), 0);
         username = et_username.getText().toString().trim();
         password = et_password.getText().toString().trim();
        if(username.equals("")||password.equals("")){
            Toast.makeText(context,"账号和密码不可以为空",Toast.LENGTH_SHORT).show();
        }else {
            connLogin();
        }

    }
    /**
     * 连接服务器
     */
    private void connLogin(){
        Map<String,String> map=new HashMap<>();
        map.put("username", username);
        map.put("password", password);
        OkHttpUtils.postDataAsync(map).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"服务器繁忙",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        User user= null;
                        try {
                            Gson gson = new Gson();
                            String json=response.body().string();
                            Log.i("-----------------数据2",json);
                            user = gson.fromJson(json,User.class);
                            if (user.getResult().equals("success")) {
                                if(auto_login.isChecked()){
                                    SharedPreferences.Editor editor_login=sp.edit();
                                    editor_login.putString("username",username);
                                    editor_login.putString("password",password);
                                    editor_login.putBoolean("flag",true);
                                    editor_login.commit();
                                }
                                Intent intent = new Intent();
                                intent.setClass(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "账号不存在或密码错误", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }


    /**
     * 初始化界面
     */
    private void initView(){
        bn_Login=(Button)findViewById(R.id.btn_login);
        et_username=(EditText)findViewById(R.id.username);
        et_password=(EditText)findViewById(R.id.password);
        auto_login=findViewById(R.id.auto_login);
        forget_pw=findViewById(R.id.forget_pw);
        forget_pw.setOnClickListener(this);
        bn_Login.setOnClickListener(this);
        sp=getSharedPreferences("user",MODE_PRIVATE);
    }

    /**
     * 自动登录
     */
    private void autoLogin(){
        if(auto_login.isChecked()&&sp.getBoolean("flag",false)){
            username= sp.getString("username","");//第一个参数为键，第二个为默认值
            password=sp.getString("password","");
            Intent intent=new Intent();
            intent.setClass(context,MainActivity.class);
            startActivity(intent);
            finish();
            }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                login();
                    break;
            case R.id.forget_pw:

                break;
                    default:
                        break;
        }
    }
}
