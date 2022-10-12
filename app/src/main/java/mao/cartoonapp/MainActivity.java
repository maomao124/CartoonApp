package mao.cartoonapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //startActivity(new Intent(this, ContentActivity.class));

        toastShow("加载中，请稍后");


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


}