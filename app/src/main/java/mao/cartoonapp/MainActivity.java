package mao.cartoonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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
    private ListView listView;
    private CartoonListViewAdapter cartoonListViewAdapter;
    private List<Cartoon> cartoonList;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //startActivity(new Intent(this, ContentActivity.class));

        toastShow("加载中，请稍后");

        listView = findViewById(R.id.ListView);


        MainApplication.getInstance().getThreadPool().submit(new Runnable()
        {
            @Override
            public void run()
            {

                CartoonService cartoonService = MainApplication.getInstance().getCartoonService();
                cartoonList = cartoonService.getCartoonList(URLConstant.rankUrl2, URLConstant.rankUrl2Type);
                Log.d(TAG, "run: 大小：" + cartoonList.size());
                Log.d(TAG, "run: 请求完成：\n" + cartoonList);
                cartoonListViewAdapter = new CartoonListViewAdapter(MainActivity.this, cartoonList);
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        cartoonListViewAdapter.notifyDataSetChanged();
                        listView.setAdapter(cartoonListViewAdapter);
                    }
                });
                for (Cartoon cartoon : cartoonList)
                {
                    Log.d(TAG, "run: " + cartoon.getImgUrl());
                    Bitmap bitmap = MainApplication.getInstance().loadImage(cartoon);
                    cartoon.setBitmap(bitmap);
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        cartoonListViewAdapter.notifyDataSetChanged();
                        listView.setAdapter(cartoonListViewAdapter);
                    }
                });
                Log.d(TAG, "run: 完成");
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cartoon cartoon = cartoonList.get(position);
                String id1 = cartoon.getId();
                String name = cartoon.getName();
                String author = cartoon.getAuthor();
                String imgUrl = cartoon.getImgUrl();
                Intent intent = new Intent(MainActivity.this, CartoonItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", id1);
                bundle.putString("name", name);
                bundle.putString("author", author);
                bundle.putString("imgUrl", imgUrl);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

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