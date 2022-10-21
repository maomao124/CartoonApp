package mao.cartoonapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerTitleStrip;
import androidx.viewpager.widget.ViewPager;


import android.annotation.SuppressLint;
import android.app.AlertDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;


import java.io.File;

import mao.cartoonapp.adapter.CartoonViewPagerAdapter;

import mao.cartoonapp.application.MainApplication;
import mao.cartoonapp.constant.OtherConstant;
import mao.cartoonapp.constant.URLConstant;
import mao.cartoonapp.dao.CartoonFavoritesDao;
import mao.cartoonapp.dao.CartoonHistoryDao;
import mao.cartoonapp.dao.CartoonUpdateDao;


public class MainActivity extends AppCompatActivity
{

    /**
     * 标签
     */
    private static final String TAG = "MainActivity";


    /**
     * 网络接收器
     */
    private NetworkReceiver networkReceiver;

    /**
     * 退出时间
     */
    private long exitTime = 0;
    private CartoonFavoritesDao cartoonFavoritesDao;
    private CartoonHistoryDao cartoonHistoryDao;
    private CartoonUpdateDao cartoonUpdateDao;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toastShow("异步加载中，请稍后");

        ViewPager viewPager = findViewById(R.id.ViewPager);
        PagerTitleStrip pagerTitleStrip = findViewById(R.id.PagerTabStrip);
        pagerTitleStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        pagerTitleStrip.setTextColor(Color.rgb(200, 20, 255));

        CartoonViewPagerAdapter cartoonViewPagerAdapter = new CartoonViewPagerAdapter(this);
        viewPager.setAdapter(cartoonViewPagerAdapter);
        viewPager.setCurrentItem(1);


        cartoonFavoritesDao = CartoonFavoritesDao.getInstance(this);
        cartoonFavoritesDao.openReadConnection();
        cartoonFavoritesDao.openWriteConnection();
        cartoonHistoryDao = CartoonHistoryDao.getInstance(this);
        cartoonHistoryDao.openReadConnection();
        cartoonHistoryDao.openWriteConnection();
        cartoonUpdateDao = CartoonUpdateDao.getInstance(this);
        cartoonUpdateDao.openReadConnection();
        cartoonUpdateDao.openWriteConnection();

        networkReceiver = new NetworkReceiver();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkReceiver, intentFilter);

        MainApplication.getInstance().runUpdateThread(this);

        MainApplication.getInstance().startCartoonUpdate(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(1, 1, 1, "漫画搜索");
        menu.add(1, 2, 2, "历史记录");
        menu.add(1, 3, 3, "漫画收藏夹");
        menu.add(1, 4, 4, "软件说明");
        menu.add(1, 5, 5, "检查更新");
        menu.add(1, 6, 6, "给项目点赞");
        menu.add(1, 7, 7, "清除缓存");
        menu.add(1, 999, 999, "退出");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        String dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
        switch (id)
        {
            case 1:
                startActivity(new Intent(this, searchActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, CartoonHistoryActivity.class));
                break;
            case 3:
                startActivity(new Intent(this, FavoritesActivity.class));
                break;
            case 4:
                new AlertDialog.Builder(this)
                        .setTitle("说明")
                        .setMessage(OtherConstant.softwareDocumentation)
                        .setPositiveButton("我知道了", null)
                        .create()
                        .show();
                break;
            case 5:
                toastShow("后台检查更新中");
                MainApplication.getInstance().runUpdateThread(this);
                break;
            case 6:
                Uri uri = Uri.parse(URLConstant.projectUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case 7:
                new AlertDialog.Builder(this)
                        .setTitle("缓存删除提示")
                        .setMessage("删除缓存后，下次启动时需要重新加载图片，这将会产生流量消耗！\n" +
                                "是否继续？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                MainApplication.getInstance().getThreadPool().submit(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        boolean b = MainApplication.getInstance().deleteDirFile(new File(dir));
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                if (b)
                                                {
                                                    toastShow("缓存清除成功");
                                                }
                                                else
                                                {
                                                    toastShow("缓存清除失败");
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .setNeutralButton("取消", null)
                        .create()
                        .show();
                break;
            case 999:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.i(TAG, "onDestroy: 软件即将关闭！！！");
        cartoonFavoritesDao.closeConnection();
        cartoonHistoryDao.closeConnection();
        cartoonUpdateDao.closeConnection();
        unregisterReceiver(networkReceiver);

        Thread updateThread = MainApplication.getInstance().getUpdateThread();
        if (updateThread.isAlive())
        {
            //直接强行关闭线程
            try
            {
                updateThread.stop();
            }
            catch (Exception ignored)
            {

            }
        }
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


    private class NetworkReceiver extends BroadcastReceiver
    {

        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent == null)
            {
                return;
            }
            NetworkInfo networkInfo = intent.getParcelableExtra("networkInfo");
            if (networkInfo.getTypeName().equals("MOBILE") && networkInfo.getState() == NetworkInfo.State.CONNECTED)
            {
                toastShow("当前使用的是移动数据网络，请注意您的流量消耗∠( °ω°)／ ");
            }
            if (networkInfo.getState() == NetworkInfo.State.DISCONNECTED)
            {
                toastShow("断网啦ヽ(。>д<)ｐ");
            }
        }
    }

}