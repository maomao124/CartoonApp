package mao.cartoonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import mao.cartoonapp.dao.CartoonUpdateDao;
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
                    for (Cartoon cartoon : cartoonList)
                    {
                        Bitmap bitmap = MainApplication.getInstance().loadImage(cartoon);
                        cartoon.setBitmap(bitmap);
                        //因为remarks字段更新比较频繁，应该在这里的时候更新一下请求后端，并更新sqlite数据库
                        //但是这里没有请求单个漫画信息的后端接口，没办法，所以这里将remarks字段清空
                        //cartoon.setRemarks("");

                        /*
                        if (!cartoon.getRemarks().contains("漫画已更新："))
                        {
                            cartoon.setRemarks("");
                        }*/
                    }
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

                CartoonFavoritesDao cartoonFavoritesDao = CartoonFavoritesDao.getInstance(FavoritesActivity.this);
                if (cartoon.getRemarks().contains("漫画已更新："))
                {
                    cartoonFavoritesDao.update(cartoon.setRemarks(cartoon.getRemarks().substring(6)));
                    cartoonListViewAdapter.notifyDataSetChanged();
                }
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cartoon cartoon = cartoonList.get(position);
                new AlertDialog.Builder(FavoritesActivity.this)
                        .setTitle("删除提示！")
                        .setMessage("是否将漫画”" + cartoon.getName() + "“删除？")
                        .setPositiveButton("确定删除", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                boolean delete = CartoonFavoritesDao.getInstance(FavoritesActivity.this).delete(cartoon.getId());
                                if (delete)
                                {
                                    toastShow("删除成功");
                                    cartoonList.remove(cartoon);
                                    cartoonListViewAdapter.notifyDataSetChanged();

                                    CartoonUpdateDao cartoonUpdateDao = CartoonUpdateDao.getInstance(FavoritesActivity.this);
                                    cartoonUpdateDao.delete(cartoon.getId());
                                }
                                else
                                {
                                    toastShow("删除失败");
                                }
                                //判断是否为0
                                if (cartoonList.size() == 0)
                                {
                                    textView.setVisibility(View.VISIBLE);
                                    listView.setVisibility(View.GONE);
                                }
                            }
                        })
                        .setNeutralButton("取消", null)
                        .create()
                        .show();
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