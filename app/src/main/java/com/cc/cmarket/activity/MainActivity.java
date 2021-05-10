package com.cc.cmarket.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.cc.cmarket.R;
import com.cc.cmarket.fragment.MainFragment;
import com.cc.cmarket.fragment.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity
{

    BottomNavigationView mBottomView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomView=findViewById(R.id.bottomNavigationView);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragView, MainFragment.getInstance())
                .add(R.id.fragView, UserFragment.getInstance())
                .hide(UserFragment.getInstance())
                .show(MainFragment.getInstance())
                .commit();

        mBottomView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull
                    MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_index:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .show(MainFragment.getInstance())
                                .hide(UserFragment.getInstance())
                                .commit();
                        break;
                    case R.id.menu_user:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .show(UserFragment.getInstance())
                                .hide(MainFragment.getInstance())
                                .commit();
                        break;

                }
                return true;
            }
        });
    }
}
