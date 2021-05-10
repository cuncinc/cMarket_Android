package com.cc.cmarket.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cc.cmarket.R;
import com.cc.cmarket.source.ResponseObject;
import com.cc.cmarket.utils.OkhttpUtils;
import com.google.gson.Gson;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = 30)
public class LoginActivity extends AppCompatActivity
{
    //要申请的权限
    String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,};

    @BindView(R.id.text_login_phone)
    EditText phoneEditText;
    @BindView(R.id.text_login_code)
    EditText codeEditText;
    @BindView(R.id.button_get_code)
    Button codeButton;
    @BindView(R.id.button_login)
    Button loginButton;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //授权
        initPermissions();
    }

    @OnTextChanged(value = R.id.text_login_phone, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void inputPhoneNumber(Editable editable)
    {
        String phone = editable.toString().trim();
        if (phone.length() == 11)
        {
            codeButton.setEnabled(true);
        }
        else
        {
            codeButton.setEnabled(false);
            loginButton.setEnabled(false);
        }
    }

    @OnTextChanged(value = R.id.text_login_code, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void inputAuthCode(Editable editable)
    {
        if (editable.toString().length() == 6)
        {
            loginButton.setEnabled(true);
        }
    }

    @OnClick(R.id.button_get_code)
    void code_click()
    {
        String phone = phoneEditText.getText().toString();
        codeButton.setEnabled(false);
        handler.postDelayed(() -> {
            if (!isFinishing())
            {
                codeButton.setEnabled(true);
            }
        }, 60 * 1000);
        OkhttpUtils.get("/user/sendSmsCode?phone=" + phone, null, new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                handler.post(
                        () -> FancyToast.makeText(LoginActivity.this, "验证码发送失败，请稍后重试", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false)
                                .show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                handler.post(() -> FancyToast.makeText(LoginActivity.this, "验证码已发送，请查收", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false)
                        .show());
            }
        });
    }

    @OnClick(R.id.button_login)
    void login_click()
    {
        String phone = phoneEditText.getText().toString();
        String code = codeEditText.getText().toString();
        Map<String, String> par = new HashMap<>();
        par.put("phone", phone);
        par.put("code", code);
        Gson gson = new Gson();
        String info = gson.toJson(par);

        OkhttpUtils.post("/user/loginByCode", info, new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                handler.post(() -> FancyToast.makeText(LoginActivity.this, "登录失败，请稍后重试", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false)
                        .show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
//                handler.post(() -> {
//                    try
//                    {
//                        Thread.sleep(1000);
//                    }
//                    catch (InterruptedException e)
//                    {
//                        e.printStackTrace();
//                    }
//                });
                ResponseBody body = response.body();
                assert body != null;
                String json = body.string();
                ResponseObject object = gson.fromJson(json, ResponseObject.class);

                if (object.getCode() == null || !object.getCode().equals("200"))
                {
                    handler.post(() -> FancyToast
                            .makeText(LoginActivity.this, object.getMessage(), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show());
                }
                else
                {
                    String token = (String)object.getData();
                    OkhttpUtils.setToken(token); //传递token
                    SharedPreferences.Editor editor = getSharedPreferences("login", MODE_PRIVATE).edit();
                    editor.putBoolean("isLogin", true);
                    editor.putString("token", token);
                    editor.apply();

                    handler.post(() -> {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });
                }
            }
        });
    }


    private void initPermissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            for (String permission : permissions)
            {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(LoginActivity.this, permission))
                {
                    ActivityCompat.requestPermissions(LoginActivity.this, permissions, 1);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    break;
                }
                else
                {
                    new android.app.AlertDialog.Builder(LoginActivity.this).setMessage("权限不足")
                            .setPositiveButton("重新授权", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    initPermissions();
                                }
                            }).setNegativeButton("退出应用", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            finish();
                        }
                    }).setCancelable(false).show();
                }
                break;
        }
    }
}
