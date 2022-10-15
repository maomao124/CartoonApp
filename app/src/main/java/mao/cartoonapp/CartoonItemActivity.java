package mao.cartoonapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import mao.cartoonapp.adapter.CartoonItemListViewAdapter;
import mao.cartoonapp.application.MainApplication;
import mao.cartoonapp.constant.URLConstant;
import mao.cartoonapp.dao.CartoonFavoritesDao;
import mao.cartoonapp.dao.CartoonHistoryDao;
import mao.cartoonapp.entity.Cartoon;
import mao.cartoonapp.entity.CartoonHistory;
import mao.cartoonapp.entity.CartoonItem;
import mao.cartoonapp.service.CartoonService;

public class CartoonItemActivity extends AppCompatActivity
{

    private static final String TAG = "CartoonItemActivity";

    private ListView listView;
    private CartoonItemListViewAdapter cartoonItemListViewAdapter;
    private List<CartoonItem> cartoonItemList;
    private String id;
    private String name;
    private String author;
    private String imgUrl;
    private Button button_add_favorites;
    private Button button_start;
    private CountDownLatch countDownLatch;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartoon_item);

        listView = findViewById(R.id.ListView);
        button_add_favorites = findViewById(R.id.Button_add_favorites);
        button_start = findViewById(R.id.Button_start);

        countDownLatch = new CountDownLatch(1);

        Intent intent = getIntent();
        if (intent == null)
        {
            toastShow("无法获取数据");
            return;
        }
        Bundle bundle = intent.getExtras();
        id = bundle.getString("id");
        name = bundle.getString("name");
        author = bundle.getString("author");
        imgUrl = bundle.getString("imgUrl");
        if (id == null)
        {
            toastShow("无法获取数据");
            return;
        }
        //toastShow(name);
        TextView textView_name = findViewById(R.id.name);
        TextView textView_author = findViewById(R.id.author);
        ImageView imageView = findViewById(R.id.ImageView);
        textView_name.setText(name);
        textView_author.setText(author);
        MainApplication.getInstance().getThreadPool().submit(new Runnable()
        {
            @Override
            public void run()
            {
                Bitmap bitmap = MainApplication.getInstance().
                        loadImage(new Cartoon().setId(id).setImgUrl(imgUrl));
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        });

        MainApplication.getInstance().getThreadPool().submit(new Runnable()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "run: 发起请求");
                CartoonService cartoonService = MainApplication.getInstance().getCartoonService();
                cartoonItemList = cartoonService.getCartoonItem(Integer.parseInt(id));
                int size = cartoonItemList.size();
                Log.d(TAG, "run: 大小：" + size);
                Log.d(TAG, "run: 数据：\n" + cartoonItemList);
                try
                {
                    for (int i = 0; i < size; i++)
                    {
                        int textViewId = size - i;
                        cartoonItemList.get(i).setTextViewId(String.valueOf(textViewId));
                        //Log.d(TAG, "run: " + textViewId);
                    }
                    cartoonItemListViewAdapter = new CartoonItemListViewAdapter(CartoonItemActivity.this, cartoonItemList);
                    //加载历史记录
                    CartoonHistory cartoonHistory = CartoonHistoryDao.getInstance(CartoonItemActivity.this).queryById(id);
                    Log.d(TAG, "run: 历史记录：\n" + cartoonHistory);
                    cartoonItemListViewAdapter.setCartoonHistory(cartoonHistory);
                    countDownLatch.countDown();
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            cartoonItemListViewAdapter.notifyDataSetChanged();
                            listView.setAdapter(cartoonItemListViewAdapter);
                        }
                    });

                }
                catch (Exception e)
                {
                    Log.e(TAG, "run: ", e);
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                CartoonItem cartoonItem = cartoonItemList.get(position);
                String id2 = cartoonItem.getId();
                String html = URLConstant.baseUrl + CartoonItemActivity.this.id + "/" + id2 + ".html";
                Intent intent2 = new Intent(CartoonItemActivity.this, ContentActivity.class);
                intent2.putExtra("html", html);
                intent2.putExtra("name", name);
                intent2.putExtra("author", author);
                intent2.putExtra("imgUrl", imgUrl);
                Log.d(TAG, "onItemClick: html:" + html);
                startActivity(intent2);
            }
        });

        MainApplication.getInstance().getThreadPool().submit(new Runnable()
        {
            @Override
            public void run()
            {
                CartoonFavoritesDao cartoonFavoritesDao = CartoonFavoritesDao.getInstance(CartoonItemActivity.this);
                Cartoon cartoon = cartoonFavoritesDao.queryById(id);
                CartoonHistoryDao cartoonHistoryDao = CartoonHistoryDao.getInstance(CartoonItemActivity.this);
                CartoonHistory cartoonHistory = cartoonHistoryDao.queryById(id);
                Log.d(TAG, "run: cartoon:" + cartoon);
                Log.d(TAG, "run: cartoonHistory" + cartoonHistory);
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        handlerAddFavoritesButton(cartoon, button_add_favorites, name, author, imgUrl, cartoonFavoritesDao);

                        handlerStartButton(cartoonHistory, button_start, name, author, imgUrl);
                    }
                });
            }
        });

    }

    /**
     * 处理程序添加收藏夹按钮
     *
     * @param cartoon              卡通
     * @param button_add_favorites 按钮添加收藏夹
     * @param name                 名字
     * @param author               作者
     * @param imgUrl               img url
     * @param cartoonFavoritesDao  CartoonFavoritesDao
     */
    private void handlerAddFavoritesButton(Cartoon cartoon, Button button_add_favorites, String name, String author, String imgUrl, CartoonFavoritesDao cartoonFavoritesDao)
    {
        if (cartoon == null)
        {
            //收藏为空
            button_add_favorites.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Cartoon cartoon1 = new Cartoon()
                            .setId(id)
                            .setName(name)
                            .setAuthor(author)
                            .setImgUrl(imgUrl)
                            .setRemarks("");
                    boolean b = cartoonFavoritesDao.insert(cartoon1);
                    if (b)
                    {
                        toastShow("添加收藏成功");
                        button_add_favorites.setText("已添加到收藏");
                        button_add_favorites.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                toastShow("已经添加到收藏了哦(*^▽^*)\n" +
                                        "如果需要取消收藏，请到收藏页面长按取消");
                            }
                        });
                    }
                    else
                    {
                        toastShow("添加收藏失败(；´д｀)ゞ");
                    }
                }
            });
        }
        else
        {
            //收藏不为空
            button_add_favorites.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    toastShow("已经添加到收藏了哦(*^▽^*)\n" +
                            "如果需要取消收藏，请到收藏页面长按取消");
                }
            });
            button_add_favorites.setText("已添加到收藏");
        }
    }

    /**
     * 处理程序开始按钮
     *
     * @param cartoonHistory 卡通历史
     * @param button_start   按钮开始
     * @param name           名字
     * @param author         作者
     * @param imgUrl         img url
     */
    private void handlerStartButton(CartoonHistory cartoonHistory, Button button_start, String name, String author, String imgUrl)
    {
        if (cartoonHistory == null)
        {
            //没有历史记录，从第一章开始阅读
//            if (cartoonItemList.size() == 0)
//            {
//                toastShow("当前漫画没有章节");
//            }
//            else
            {
                button_start.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        CartoonItem cartoonItem;
                        try
                        {
                            cartoonItem = cartoonItemList.get(cartoonItemList.size() - 1);
                        }
                        catch (NullPointerException e)
                        {
                            toastShow("当前漫画没有章节");
                            return;
                        }
                        String id2 = cartoonItem.getId();
                        String html = URLConstant.baseUrl + CartoonItemActivity.this.id + "/" + id2 + ".html";
                        Intent intent2 = new Intent(CartoonItemActivity.this, ContentActivity.class);
                        intent2.putExtra("html", html);
                        intent2.putExtra("name", name);
                        intent2.putExtra("author", author);
                        intent2.putExtra("imgUrl", imgUrl);
                        Log.d(TAG, "onItemClick: html:" + html);
                        startActivity(intent2);
                    }
                });
            }
        }
        else
        {
            //有历史记录
            button_start.setText("继续阅读");
            button_start.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    String id2 = cartoonHistory.getId2();
                    String html = URLConstant.baseUrl + CartoonItemActivity.this.id + "/" + id2 + ".html";
                    Intent intent2 = new Intent(CartoonItemActivity.this, ContentActivity.class);
                    intent2.putExtra("html", html);
                    intent2.putExtra("name", name);
                    intent2.putExtra("author", author);
                    intent2.putExtra("imgUrl", imgUrl);
                    Log.d(TAG, "onItemClick: html:" + html);
                    startActivity(intent2);
                }
            });
        }
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
                CartoonHistoryDao cartoonHistoryDao = CartoonHistoryDao.getInstance(CartoonItemActivity.this);
                CartoonHistory cartoonHistory = cartoonHistoryDao.queryById(id);
                try
                {
                    countDownLatch.await();
                }
                catch (InterruptedException e)
                {
                    //不会被打断
                    Log.e(TAG, "run: ", e);
                }
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        cartoonItemListViewAdapter.setCartoonHistory(cartoonHistory);
                        cartoonItemListViewAdapter.notifyDataSetChanged();
                        handlerStartButton(cartoonHistory, button_start, name, author, imgUrl);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(1, 1, 1, "反转目录");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        if (id == 1)
        {
            //反转
            Collections.reverse(cartoonItemList);
            cartoonItemListViewAdapter.notifyDataSetChanged();
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