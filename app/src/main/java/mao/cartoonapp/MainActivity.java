package mao.cartoonapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerTitleStrip;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import mao.cartoonapp.adapter.CartoonListViewAdapter;
import mao.cartoonapp.adapter.CartoonViewPagerAdapter;
import mao.cartoonapp.application.MainApplication;
import mao.cartoonapp.constant.URLConstant;
import mao.cartoonapp.entity.Cartoon;
import mao.cartoonapp.entity.ImageLoadResult;
import mao.cartoonapp.service.CartoonService;

public class MainActivity extends AppCompatActivity
{

    /**
     * 标签
     */
    private static final String TAG = "MainActivity";


    /**
     * 退出时间
     */
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toastShow("加载中，请稍后");

        ViewPager viewPager = findViewById(R.id.ViewPager);
        PagerTitleStrip pagerTitleStrip = findViewById(R.id.PagerTabStrip);
        pagerTitleStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        pagerTitleStrip.setTextColor(Color.rgb(200, 20, 255));

        CartoonViewPagerAdapter cartoonViewPagerAdapter = new CartoonViewPagerAdapter(this);
        viewPager.setAdapter(cartoonViewPagerAdapter);
        viewPager.setCurrentItem(1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(1, 1, 1, "历史记录");
        menu.add(1, 2, 2, "收藏夹");
        menu.add(1, 999, 999, "退出");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case 1:
                toastShow("未实现历史记录功能，也不想实现");
                break;
            case 2:
                toastShow("功能未实现");
                break;
            case 999:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 显示消息
     *
     * @param message 消息
     */
    private void toastShow(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed()
    {
        if ((System.currentTimeMillis() - exitTime) > 2000)
        {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        }
        else
        {
            super.onBackPressed();
        }
    }
}