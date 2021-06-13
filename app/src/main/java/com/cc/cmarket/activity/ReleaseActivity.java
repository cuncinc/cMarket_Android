package com.cc.cmarket.activity;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.cc.cmarket.R;
import com.cc.cmarket.source.GlideEngine;
import com.cc.cmarket.source.Goods;
import com.cc.cmarket.source.ResponseObject;
import com.cc.cmarket.utils.OkhttpUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.tools.StringUtils;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ReleaseActivity extends AppCompatActivity
{
    private Map<String, String> map = new HashMap<>();
    File file;
    Goods goods = new Goods();
    Handler handler = new Handler();

    @BindView(R.id.release_release)
    Button releaseButton;
    @BindView(R.id.release_desc)
    EditText descEdit;
    @BindView(R.id.release_img)
    ImageView releaseImg;
    @BindView(R.id.release_price)
    TextInputEditText priceEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
    }

    @OnClick({R.id.release_release, R.id.release_img})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.release_release:
                release();
                break;
            case R.id.release_img:
                selectFile();
                break;
            default:
                break;
        }
    }

    private void release()
    {
        String price = priceEdit.getText().toString();
        String desc = descEdit.getText().toString();
        if (desc.equals(""))
        {
            FancyToast.makeText(this, "商品描述不能为空", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            return;
        }
        else if (price.equals(""))
        {
            FancyToast.makeText(this, "价格不能为空", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            return;
        }
        else if (Double.parseDouble(price) == 0.0)
        {
            FancyToast.makeText(this, "价格不能为0", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            return;
        }


        map.put("goodsDesc", desc);
        map.put("price", price);
        map.put("category", "Default");

        OkhttpUtils.postPictureWithFile("/goods/release", map, null, new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                handler.post(() -> FancyToast
                        .makeText(ReleaseActivity.this, "商品发布失败", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show());
                return;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                String json = response.body().string();
                Type type = new TypeToken<ResponseObject<Goods>>()
                {
                }.getType();
                ResponseObject<Goods> object = new Gson().fromJson(json, type);
                if (object == null || !object.getCode().equals("200"))
                {
                    handler.post(() -> FancyToast
                            .makeText(ReleaseActivity.this, "发布失败"+object.getMessage(), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show());
                    return;
                }
                goods = object.getData();
                handler.post(() -> FancyToast
                        .makeText(ReleaseActivity.this, "商品发布成功", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show());
                finish();
            }
        });
    }

    private void selectFile()
    {
        PictureSelector.create(this).openGallery(PictureMimeType.ofImage()).isCamera(true).imageEngine(GlideEngine.createGlideEngine())
                .selectionMode(PictureConfig.SINGLE).forResult(new OnResultCallbackListener()
        {
            @Override
            public void onResult(List result)
            {
                if (result.size() != 0)
                {
                    LocalMedia localMedia = (LocalMedia)result.get(0);
                    int i = localMedia.getFileName().lastIndexOf(".");
                    map.put("filePath", localMedia.getRealPath()); // 获取到真实路径
                    //                            Log.d("111", "onResult: "+localMedia.getRealPath());
                    map.put("pictureName", localMedia.getFileName().substring(0, i)); //获取文件名

                    runOnUiThread(() -> FancyToast
                            .makeText(getApplicationContext(), "已选 " + localMedia.getFileName().substring(0, i), FancyToast.INFO,
                                    FancyToast.LENGTH_SHORT, false).show());

                    String path = localMedia.getRealPath();
                    Log.e("real", path);
                    //                    releaseImg.setImageURI(Uri.fromFile(new File(path)));

                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    releaseImg.setImageBitmap(bitmap);

                    file = new File(path);
                }
            }

            @Override
            public void onCancel()
            {
            }
        });
    }
}
