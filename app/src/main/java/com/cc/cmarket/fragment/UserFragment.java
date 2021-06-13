package com.cc.cmarket.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.cc.cmarket.R;
import com.cc.cmarket.activity.LoginActivity;
import com.cc.cmarket.activity.ReleaseActivity;
import com.cc.cmarket.source.GlideEngine;
import com.cc.cmarket.source.Goods;
import com.cc.cmarket.source.ResponseObject;
import com.cc.cmarket.source.User;
import com.cc.cmarket.utils.OkhttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class UserFragment extends Fragment
{
    @BindView(R.id.me_quit)
    Button quitButton;
    private Unbinder unbinder;
    private static UserFragment fragment;
    Handler handler = new Handler();
    User me;
    DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

    @BindView(R.id.me_bg)
    ImageView backgroundImg;
    @BindView(R.id.me_avatar)
    ImageView avatarImg;
    @BindView(R.id.me_username)
    TextView usernameTv;
    @BindView(R.id.me_username_layout)
    LinearLayout usernameLayout;

    public static Fragment getInstance()
    {
        if (fragment == null)
        {
            synchronized (UserFragment.class)
            {
                if (fragment == null)
                {
                    fragment = new UserFragment();
                }
            }
        }
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (me == null)
        {
            getMe();
        }
    }

    private void getMe()
    {
        OkhttpUtils.get("/user/getMeInfo", null, new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                String json = response.body().string();
                Type type = new TypeToken<ResponseObject<User>>()
                {
                }.getType();
                ResponseObject<User> object = new Gson().fromJson(json, type);
                if (object == null || !object.getCode().equals("200"))
                {
                    //
                }
                me = object.getData();
                handler.post(() -> bindView());
            }
        });
    }

    private void bindView()
    {
        Glide.with(getActivity()).load(me.getAvatarUrl()).transition(withCrossFade(factory)).skipMemoryCache(true)
                .apply(bitmapTransform(new CropCircleTransformation())) //头像变圆
                .into(avatarImg);
        //加载背景
        Glide.with(getActivity()).load(me.getAvatarUrl()).apply(bitmapTransform(new BlurTransformation(50, 3))).into(backgroundImg);

        usernameTv.setText(me.getUserName());
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.me_avatar)
    public void avatarClick()
    {

    }

    @OnClick(R.id.me_username_layout)
    public void usernameClick()
    {

    }

    @OnClick(R.id.me_quit)
    public void quitClick()
    {
        OkhttpUtils.setToken("");
        SharedPreferences preferences = getActivity().getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}