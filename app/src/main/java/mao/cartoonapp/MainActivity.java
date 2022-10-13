package mao.cartoonapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerTitleStrip;
import androidx.viewpager.widget.ViewPager;


import android.app.AlertDialog;

import android.content.Intent;
import android.graphics.Color;
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
                        .setMessage("1.长按列表项可以添加漫画到收藏夹\n" +
                                "2.搜索页面长按列表项也可以收藏\n" +
                                "3.开发此软件目的是为了学习安卓\n" +
                                "4.收藏夹页面长按列表项可以取消收藏\n" +
                                "5.作者QQ：1296193245\n" +
                                "6.作者github：https://github.com/maomao124/")
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