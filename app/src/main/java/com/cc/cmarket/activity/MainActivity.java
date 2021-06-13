package com.cc.cmarket.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.cc.cmarket.R;
import com.cc.cmarket.fragment.FragmentOne;
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

        getSupportActionBar().hide();
        mBottomView = findViewById(R.id.bottomNavigationView);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragView, FragmentOne.getInstance())
                .add(R.id.fragView, UserFragment.getInstance())
                .hide(UserFragment.getInstance()).show(FragmentOne.getInstance()).commit();

        mBottomView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.menu_release:
                        Intent intent = new Intent(MainActivity.this, ReleaseActivity.class);
                        startActivity(intent);
//                        break;
                    case R.id.menu_market:
                        getSupportFragmentManager().beginTransaction().show(FragmentOne.getInstance()).hide(UserFragment.getInstance())
                                .commit();
                        break;
                    case R.id.menu_me:
                        getSupportFragmentManager().beginTransaction().show(UserFragment.getInstance()).hide(FragmentOne.getInstance())
                                .commit();
                        break;

                }
                return true;
            }
        });
    }
}
