package com.cc.cmarket.source;

import android.graphics.Bitmap;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

public class Data
{
    public Bitmap img;
    public String imgurl;
    @DrawableRes
    public int imgRes;
    public String title;

    public Data(@DrawableRes int imgRes, String title)
    {
        this.imgRes = imgRes;
        this.title = title;
    }
}
