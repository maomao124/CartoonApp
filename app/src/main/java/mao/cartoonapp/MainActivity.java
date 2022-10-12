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


        cartoonFavoritesDao = CartoonFavoritesDao.getInstance(this);
        cartoonFavoritesDao.openReadConnection();
        cartoonFavoritesDao.openWriteConnection();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(1, 1, 1, "历史记录");
        menu.add(1, 2, 2, "漫画收藏夹");
        menu.add(1, 3, 3, "软件说明");
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
                startActivity(new Intent(this, FavoritesActivity.class));
                break;
            case 3:
                new AlertDialog.Builder(this)
                        .setTitle("说明")
                        .setMessage("1.长按列表项可以添加漫画到收藏夹\n" +
                                "2.开发此软件目的是为了学习安卓\n" +
                                "3.收藏夹页面长按列表项可以取消收藏" +
                                "4.作者QQ：1296193245\n" +
                                "5.作者github：https://github.com/maomao124/")
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