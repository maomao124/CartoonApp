package mao.cartoonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import mao.cartoonapp.adapter.CartoonListViewAdapter;
import mao.cartoonapp.application.MainApplication;
import mao.cartoonapp.dao.CartoonFavoritesDao;
import mao.cartoonapp.entity.Cartoon;

public class FavoritesActivity extends AppCompatActivity
{

    private CartoonListViewAdapter cartoonListViewAdapter;
    private List<Cartoon> cartoonList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        toastShow("异步加载中");

        ListView listView = findViewById(R.id.ListView);
        TextView textView = findViewById(R.id.TextView);

        MainApplication.getInstance().getThreadPool().submit(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    CartoonFavoritesDao cartoonFavoritesDao = CartoonFavoritesDao.getInstance(FavoritesActivity.this);
                    cartoonList = cartoonFavoritesDao.queryAll();
                    if (cartoonList.size() == 0)
                    {
                        return;
                    }
                    cartoonListViewAdapter = new CartoonListViewAdapter(FavoritesActivity.this, cartoonList);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            textView.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                            listView.setAdapter(cartoonListViewAdapter);
                            toastShow("加载完成");
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
                            new AlertDialog.Builder(FavoritesActivity.this)
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
                Intent intent = new Intent(FavoritesActivity.this, CartoonItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", id1);
                bundle.putString("name", name);
                bundle.putString("author", author);
                bundle.putString("imgUrl", imgUrl);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {

                return true;
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