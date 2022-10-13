package mao.cartoonapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mao.cartoonapp.adapter.CartoonListViewAdapter;
import mao.cartoonapp.application.MainApplication;
import mao.cartoonapp.constant.URLConstant;
import mao.cartoonapp.dao.CartoonHistoryDao;
import mao.cartoonapp.entity.Cartoon;
import mao.cartoonapp.entity.CartoonHistory;

public class CartoonHistoryActivity extends AppCompatActivity
{

    /**
     * 标签
     */
    private static final String TAG = "CartoonHistoryActivity";
    private ListView listView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartoon_history);


        listView = findViewById(R.id.ListView);
        textView = findViewById(R.id.TextView);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        MainApplication.getInstance().getThreadPool().submit(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    CartoonHistoryDao cartoonHistoryDao = CartoonHistoryDao.getInstance(CartoonHistoryActivity.this);
                    List<CartoonHistory> cartoonHistoryList = cartoonHistoryDao.queryAll();
                    Log.d(TAG, "run: 历史记录：" + cartoonHistoryList);
                    if (cartoonHistoryList.size() == 0)
                    {
                        return;
                    }

                    List<Cartoon> cartoonList = new ArrayList<>(cartoonHistoryList.size());
                    for (CartoonHistory cartoonHistory : cartoonHistoryList)
                    {
                        Cartoon cartoon = new Cartoon()
                                .setId(cartoonHistory.getId1())
                                .setName(cartoonHistory.getName())
                                .setAuthor(cartoonHistory.getAuthor())
                                .setRemarks("")
                                .setImgUrl(cartoonHistory.getImgUrl());
                        Bitmap bitmap = MainApplication.getInstance().loadImage(cartoon);
                        cartoon.setBitmap(bitmap);
                        cartoonList.add(cartoon);
                    }
                    CartoonListViewAdapter cartoonListViewAdapter =
                            new CartoonListViewAdapter(CartoonHistoryActivity.this, cartoonList);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            textView.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                            listView.setAdapter(cartoonListViewAdapter);
                        }
                    });
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            CartoonHistory cartoonHistory = cartoonHistoryList.get(position);
                            String id1 = cartoonHistory.getId1();
                            String id2 = cartoonHistory.getId2();
                            String html = URLConstant.baseUrl + id1 + "/" + id2 + ".html";
                            Intent intent2 = new Intent(CartoonHistoryActivity.this, ContentActivity.class);
                            intent2.putExtra("html", html);
                            intent2.putExtra("name", cartoonHistory.getName());
                            intent2.putExtra("author", cartoonHistory.getAuthor());
                            intent2.putExtra("imgUrl", cartoonHistory.getImgUrl());
                            Log.d(TAG, "onItemClick: html:" + html);
                            startActivity(intent2);
                        }
                    });
                }
                catch (Exception e)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            new AlertDialog.Builder(CartoonHistoryActivity.this)
                                    .setTitle("错误")
                                    .setMessage("异常内容：\n" + e)
                                    .setPositiveButton("我知道了", null)
                                    .create()
                                    .show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(1, 1, 1, "清空历史记录");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        if (id == 1)
        {
            new AlertDialog.Builder(CartoonHistoryActivity.this)
                    .setTitle("提示")
                    .setMessage("是否清空历史记录？")
                    .setPositiveButton("确定清除", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            MainApplication.getInstance().getThreadPool().submit(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    try
                                    {
                                        CartoonHistoryDao cartoonHistoryDao =
                                                CartoonHistoryDao.getInstance(CartoonHistoryActivity.this);
                                        boolean b = cartoonHistoryDao.deleteAll();
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                if (b)
                                                {
                                                    toastShow("清空成功");
                                                }
                                                else
                                                {
                                                    toastShow("清空失败");
                                                }
                                            }
                                        });
                                    }
                                    catch (Exception e)
                                    {
                                        Log.e(TAG, "run: ", e);
                                    }
                                }
                            });
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create()
                    .show();
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