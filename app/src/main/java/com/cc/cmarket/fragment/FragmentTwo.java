package com.cc.cmarket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cc.cmarket.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentTwo extends Fragment {

    private static FragmentTwo fragment;


    public static Fragment getInstance(){
        if (fragment==null){
            synchronized (FragmentTwo.class){
                if (fragment==null){
                    fragment=new FragmentTwo();
                }
            }
        }
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_second,container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}

