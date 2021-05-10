package com.cc.cmarket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.cc.cmarket.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment
{

    private static MainFragment fragment;
    ViewPager mViewpager;
    TabLayout mTabs;
    static List<Fragment> frags;

    static
    {
        frags = new ArrayList<>();
        frags.add(FragmentOne.getInstance());
        frags.add(FragmentTwo.getInstance());
    }

    public static Fragment getInstance()
    {
        if (fragment == null)
        {
            synchronized (MainFragment.class)
            {
                if (fragment == null)
                {
                    fragment = new MainFragment();
                }
            }
        }
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        //把Fragment添加到列表中，以Tab展示
        return inflater.inflate(R.layout.fragment_main, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mTabs = view.findViewById(R.id.tabs);
        mViewpager = view.findViewById(R.id.mviewpager);
        mViewpager.setAdapter(new FragmentPagerAdapter(getFragmentManager())
        {
            @NonNull
            @Override
            public Fragment getItem(int position)
            {
                return frags.get(position);
            }

            @Override
            public int getCount()
            {
                return frags.size();
            }


            @Nullable
            @Override
            public CharSequence getPageTitle(int position)
            {
                return frags.get(position).getClass().getName();
            }
        });
        //        mViewpager.setOffscreenPageLimit(5);
        mTabs.setupWithViewPager(mViewpager);//与TabLayout绑定
    }
}