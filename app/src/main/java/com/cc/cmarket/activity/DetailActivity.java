package com.cc.cmarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.cc.cmarket.R;
import com.cc.cmarket.source.Goods;
import com.cc.cmarket.source.ResponseObject;
import com.cc.cmarket.utils.OkhttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class DetailActivity extends AppCompatActivity
{

    Goods goods;
    Handler handler = new Handler();

    @BindView(R.id.detail_avatar)
    ImageView avatarImg;
    @BindView(R.id.detail_username)
    TextView usernameTv;
    @BindView(R.id.detail_price)
    TextView priceTv;
    @BindView(R.id.detail_desc)
    TextView descTv;
    @BindView(R.id.detail_img)
    ImageView goodsImg;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        initData();
    }

    void initData()
    {
        Intent intent = getIntent();
        String goodsId = intent.getStringExtra("goodsId");
        if (goodsId == null)
        {
            FancyToast.makeText(this, "不能打开此页面", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            finish();
        }
        OkhttpUtils.get("/goods/getDisplayGoodsById?goodsId="+goodsId, null, new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                String json = response.body().string();
                Type type = new TypeToken<ResponseObject<Goods>>()
                {
                }.getType();
                ResponseObject<Goods> object = new Gson().fromJson(json, type);
                goods = object.getData();

                handler.post(()->bindView());
            }
        });
    }

    void bindView()
    {
        DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
        descTv.setText(goods.getGoodsDesc());
        priceTv.setText(goods.getPrice());
        Glide.with(this).load(goods.getPicUrl()).into(goodsImg);
        Glide.with(this).load(goods.getAvatarUrl()).transition(withCrossFade(factory)).skipMemoryCache(true)
                .apply(bitmapTransform(new CropCircleTransformation())) //头像变圆
                .into(avatarImg);
        usernameTv.setText(goods.getUserName());
    }
}
