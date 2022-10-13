package mao.cartoonapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerTitleStrip;
import androidx.viewpager.widget.ViewPager;


import android.annotation.SuppressLint;
import android.app.AlertDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;


import mao.cartoonapp.adapter.CartoonViewPagerAdapter;

import mao.cartoonapp.dao.CartoonFavoritesDao;
import mao.cartoonapp.dao.CartoonHistoryDao;


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

        networkReceiver = new NetworkReceiver();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkReceiver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(1, 1, 1, "漫画搜索");
        menu.add(1, 2, 2, "历史记录");
        menu.add(1, 3, 3, "漫画收藏夹");
        menu.add(1, 4, 4, "软件说明");
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
                        .setMessage("" +
                                "      ,-.,-.  \n" +
                                "     (  (  (        \n" +
                                "      \\  )  ) _..-.._   \n" +
                                "     __)/ ,','       `.\n" +
                                "   ,\"     `.     ,--.  `.     \n" +
                                " ,\"   @        .'    `   \\\n" +
                                "(Y            (           ;''.\n" +
                                " `--.____,     \\          ,  ; \n" +
                                " ((_ ,----' ,---'      _,'_,'    \n" +
                                "     (((_,- (((______,-'" +
                                "\n" +
                                "1.长按列表项可以添加漫画到收藏夹\n" +
                                "2.搜索页面长按列表项也可以收藏\n" +
                                "3.历史记录页面默认按最近观看的漫画时间降序排序\n" +
                                "4.历史记录页面因为拿不到正在观看的章节名称，没有显示该字段，除非解析html\n" +
                                "5.收藏夹页面因为后端没有通过id获取漫画信息的请求接口，还有漫画更新比较频繁，" +
                                "所以最后一章节字段没办法显示，也没法推送通知发送漫画更新消息\n" +
                                "6.收藏夹页面长按列表项可以取消收藏\n" +
                                "7.右上角的菜单可以使用\n" +
                                "8.开发此软件目的是为了学习安卓\n" +
                                "9.作者QQ：1296193245\n" +
                                "10.作者github：https://github.com/maomao124/" +
                                "\n\n" +
                                "当前版本：v1.1" +
                                "\n\n\n" +
                                "版本更新说明：" +
                                "\n\n" +
                                "v1.1：\n" +
                                "1.优化搜索页面结果显示，还是异步加载，但是分成了两阶段\n" +
                                "2.主题由安卓默认颜色更改成#00ccff(天蓝色)\n" +
                                "")
                        .setPositiveButton("我知道了", null)
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
        cartoonFavoritesDao.closeConnection();
        cartoonHistoryDao.closeConnection();
        unregisterReceiver(networkReceiver);
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