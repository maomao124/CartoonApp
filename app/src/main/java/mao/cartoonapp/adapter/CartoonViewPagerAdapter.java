package mao.cartoonapp.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

import mao.cartoonapp.CartoonItemActivity;
import mao.cartoonapp.MainActivity;
import mao.cartoonapp.R;
import mao.cartoonapp.application.MainApplication;
import mao.cartoonapp.constant.URLConstant;
import mao.cartoonapp.dao.CartoonFavoritesDao;
import mao.cartoonapp.entity.Cartoon;
import mao.cartoonapp.service.CartoonService;

/**
 * Project name(项目名称)：CartoonApp
 * Package(包名): mao.cartoonapp.adapter
 * Class(类名): CartoonViewPagerAdapter
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/10/12
 * Time(创建时间)： 19:20
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class CartoonViewPagerAdapter extends PagerAdapter
{

    private final Activity activity;

    /**
     * 标签
     */
    private static final String TAG = "CartoonViewPagerAdapter";

    private List<Cartoon> cartoonList1;
    private List<Cartoon> cartoonList2;
    private List<Cartoon> cartoonList3;
    private List<Cartoon> cartoonList4;
    private List<Cartoon> cartoonList5;
    private List<Cartoon> cartoonList6;

    private final ListView[] listViewList;

    private final LinearLayout[] loadingList;

    private final LinearLayout[] linearLayoutList;

    private final CartoonListViewAdapter[] cartoonListViewAdapterList;

    private final String[] titleList =
            {
                    URLConstant.rank1Name,
                    URLConstant.rank2Name,
                    URLConstant.rank3Name,
                    URLConstant.rank4Name,
                    URLConstant.rank5Name,
                    URLConstant.rank6Name,
            };


    public CartoonViewPagerAdapter(Activity activity)
    {
        this.activity = activity;


        listViewList = new ListView[6];
        cartoonListViewAdapterList = new CartoonListViewAdapter[6];
        loadingList = new LinearLayout[6];
        linearLayoutList = new LinearLayout[6];

        //初始化linearLayoutList
        for (int i = 0; i < linearLayoutList.length; i++)
        {
            linearLayoutList[i] = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.item_pageview, null);
        }

        //初始化listViewList
        for (int i = 0; i < listViewList.length; i++)
        {
            listViewList[i] = linearLayoutList[i].findViewById(R.id.ListView);
        }

        //初始化loadingList
        for (int i = 0; i < loadingList.length; i++)
        {
            loadingList[i] = linearLayoutList[i].findViewById(R.id.loading);
        }


        MainApplication.getInstance().getThreadPool().submit(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    //先初始化第2组
                    load2(activity);
                    //加载其它页，并发加载
                    MainApplication.getInstance().getThreadPool().submit(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                load1(activity);
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "run: ", e);
                                activity.runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        loadingList[0].setVisibility(View.GONE);
                                        listViewList[0].setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }
                    });
                    MainApplication.getInstance().getThreadPool().submit(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                load3(activity);
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "run: ", e);
                                activity.runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        loadingList[2].setVisibility(View.GONE);
                                        listViewList[2].setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }
                    });
                    MainApplication.getInstance().getThreadPool().submit(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                load4(activity);
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "run: ", e);
                                activity.runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        loadingList[3].setVisibility(View.GONE);
                                        listViewList[3].setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }
                    });
                    MainApplication.getInstance().getThreadPool().submit(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                load5(activity);
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "run: ", e);
                                activity.runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        loadingList[4].setVisibility(View.GONE);
                                        listViewList[4].setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }
                    });
                    MainApplication.getInstance().getThreadPool().submit(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                load6(activity);
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "run: ", e);
                                activity.runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        loadingList[5].setVisibility(View.GONE);
                                        listViewList[5].setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }
                    });
                }
                catch (Exception e)
                {
                    Log.e(TAG, "run: ", e);
                    //只提示用户第二组报错，其它组的异常不提示
                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            loadingList[1].setVisibility(View.GONE);
                            listViewList[1].setVisibility(View.VISIBLE);
                            new AlertDialog.Builder(activity)
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

        listViewList[0].setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cartoon cartoon = cartoonList1.get(position);
                String id1 = cartoon.getId();
                String name = cartoon.getName();
                String author = cartoon.getAuthor();
                String imgUrl = cartoon.getImgUrl();
                Intent intent = new Intent(activity, CartoonItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", id1);
                bundle.putString("name", name);
                bundle.putString("author", author);
                bundle.putString("imgUrl", imgUrl);
                intent.putExtras(bundle);
                activity.startActivity(intent);
            }
        });

        listViewList[1].setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cartoon cartoon = cartoonList2.get(position);
                String id1 = cartoon.getId();
                String name = cartoon.getName();
                String author = cartoon.getAuthor();
                String imgUrl = cartoon.getImgUrl();
                Intent intent = new Intent(activity, CartoonItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", id1);
                bundle.putString("name", name);
                bundle.putString("author", author);
                bundle.putString("imgUrl", imgUrl);
                intent.putExtras(bundle);
                activity.startActivity(intent);
            }
        });

        listViewList[2].setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cartoon cartoon = cartoonList3.get(position);
                String id1 = cartoon.getId();
                String name = cartoon.getName();
                String author = cartoon.getAuthor();
                String imgUrl = cartoon.getImgUrl();
                Intent intent = new Intent(activity, CartoonItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", id1);
                bundle.putString("name", name);
                bundle.putString("author", author);
                bundle.putString("imgUrl", imgUrl);
                intent.putExtras(bundle);
                activity.startActivity(intent);
            }
        });

        listViewList[3].setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cartoon cartoon = cartoonList4.get(position);
                String id1 = cartoon.getId();
                String name = cartoon.getName();
                String author = cartoon.getAuthor();
                String imgUrl = cartoon.getImgUrl();
                Intent intent = new Intent(activity, CartoonItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", id1);
                bundle.putString("name", name);
                bundle.putString("author", author);
                bundle.putString("imgUrl", imgUrl);
                intent.putExtras(bundle);
                activity.startActivity(intent);
            }
        });

        listViewList[4].setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cartoon cartoon = cartoonList5.get(position);
                String id1 = cartoon.getId();
                String name = cartoon.getName();
                String author = cartoon.getAuthor();
                String imgUrl = cartoon.getImgUrl();
                Intent intent = new Intent(activity, CartoonItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", id1);
                bundle.putString("name", name);
                bundle.putString("author", author);
                bundle.putString("imgUrl", imgUrl);
                intent.putExtras(bundle);
                activity.startActivity(intent);
            }
        });

        listViewList[5].setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cartoon cartoon = cartoonList6.get(position);
                String id1 = cartoon.getId();
                String name = cartoon.getName();
                String author = cartoon.getAuthor();
                String imgUrl = cartoon.getImgUrl();
                Intent intent = new Intent(activity, CartoonItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", id1);
                bundle.putString("name", name);
                bundle.putString("author", author);
                bundle.putString("imgUrl", imgUrl);
                intent.putExtras(bundle);
                activity.startActivity(intent);
            }
        });


        listViewList[0].setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cartoon cartoon = cartoonList1.get(position);
                new AlertDialog.Builder(activity)
                        .setTitle("提示")
                        .setMessage("是否将漫画”" + cartoon.getName() + "“加入到收藏夹？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                boolean insert = CartoonFavoritesDao.getInstance(activity).insert(cartoon);
                                if (insert)
                                {
                                    toastShow("加入成功");
                                }
                                else
                                {
                                    toastShow("加入失败");
                                }
                            }
                        })
                        .setNeutralButton("否", null)
                        .create()
                        .show();
                return true;
            }
        });

        listViewList[1].setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cartoon cartoon = cartoonList2.get(position);
                new AlertDialog.Builder(activity)
                        .setTitle("提示")
                        .setMessage("是否将漫画”" + cartoon.getName() + "“加入到收藏夹？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                boolean insert = CartoonFavoritesDao.getInstance(activity).insert(cartoon);
                                if (insert)
                                {
                                    toastShow("加入成功");
                                }
                                else
                                {
                                    toastShow("加入失败");
                                }
                            }
                        })
                        .setNeutralButton("否", null)
                        .create()
                        .show();
                return true;
            }
        });

        listViewList[2].setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cartoon cartoon = cartoonList3.get(position);
                new AlertDialog.Builder(activity)
                        .setTitle("提示")
                        .setMessage("是否将漫画”" + cartoon.getName() + "“加入到收藏夹？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                boolean insert = CartoonFavoritesDao.getInstance(activity).insert(cartoon);
                                if (insert)
                                {
                                    toastShow("加入成功");
                                }
                                else
                                {
                                    toastShow("加入失败");
                                }
                            }
                        })
                        .setNeutralButton("否", null)
                        .create()
                        .show();
                return true;
            }
        });

        listViewList[3].setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cartoon cartoon = cartoonList4.get(position);
                new AlertDialog.Builder(activity)
                        .setTitle("提示")
                        .setMessage("是否将漫画”" + cartoon.getName() + "“加入到收藏夹？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                boolean insert = CartoonFavoritesDao.getInstance(activity).insert(cartoon);
                                if (insert)
                                {
                                    toastShow("加入成功");
                                }
                                else
                                {
                                    toastShow("加入失败");
                                }
                            }
                        })
                        .setNeutralButton("否", null)
                        .create()
                        .show();
                return true;
            }
        });

        listViewList[4].setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cartoon cartoon = cartoonList5.get(position);
                new AlertDialog.Builder(activity)
                        .setTitle("提示")
                        .setMessage("是否将漫画”" + cartoon.getName() + "“加入到收藏夹？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                boolean insert = CartoonFavoritesDao.getInstance(activity).insert(cartoon);
                                if (insert)
                                {
                                    toastShow("加入成功");
                                }
                                else
                                {
                                    toastShow("加入失败");
                                }
                            }
                        })
                        .setNeutralButton("否", null)
                        .create()
                        .show();
                return true;
            }
        });

        listViewList[5].setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cartoon cartoon = cartoonList6.get(position);
                new AlertDialog.Builder(activity)
                        .setTitle("提示")
                        .setMessage("是否将漫画”" + cartoon.getName() + "“加入到收藏夹？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                boolean insert = CartoonFavoritesDao.getInstance(activity).insert(cartoon);
                                if (insert)
                                {
                                    toastShow("加入成功");
                                }
                                else
                                {
                                    toastShow("加入失败");
                                }
                            }
                        })
                        .setNeutralButton("否", null)
                        .create()
                        .show();
                return true;
            }
        });

    }

    /**
     * load1
     *
     * @param activity 活动
     */
    private void load1(Activity activity)
    {
        int pageNumber = 0;
        CartoonService cartoonService = MainApplication.getInstance().getCartoonService();
        cartoonList1 = cartoonService.getCartoonList(URLConstant.rankUrl1, URLConstant.rankUrl1Type);
        Log.d(TAG, "run: 大小：" + cartoonList1.size());
        Log.d(TAG, "run: 请求完成：\n" + cartoonList1);
        cartoonListViewAdapterList[pageNumber] = new CartoonListViewAdapter(activity, cartoonList1);
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                cartoonListViewAdapterList[pageNumber].notifyDataSetChanged();
                listViewList[pageNumber].setAdapter(cartoonListViewAdapterList[pageNumber]);
                loadingList[pageNumber].setVisibility(View.GONE);
                listViewList[pageNumber].setVisibility(View.VISIBLE);
            }
        });
        for (Cartoon cartoon : cartoonList1)
        {
            Log.d(TAG, "run: " + cartoon.getImgUrl());
            Bitmap bitmap = MainApplication.getInstance().loadImage(cartoon);
            cartoon.setBitmap(bitmap);
        }

        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                cartoonListViewAdapterList[pageNumber].notifyDataSetChanged();
                listViewList[pageNumber].setAdapter(cartoonListViewAdapterList[pageNumber]);
            }
        });
        Log.d(TAG, "run: 第1页加载完成");
    }


    /**
     * load2
     *
     * @param activity 活动
     */
    private void load2(Activity activity)
    {
        int pageNumber = 1;
        CartoonService cartoonService = MainApplication.getInstance().getCartoonService();
        cartoonList2 = cartoonService.getCartoonList(URLConstant.rankUrl2, URLConstant.rankUrl2Type);
        Log.d(TAG, "run: 大小：" + cartoonList2.size());
        Log.d(TAG, "run: 请求完成：\n" + cartoonList2);
        cartoonListViewAdapterList[pageNumber] = new CartoonListViewAdapter(activity, cartoonList2);
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                cartoonListViewAdapterList[pageNumber].notifyDataSetChanged();
                listViewList[pageNumber].setAdapter(cartoonListViewAdapterList[pageNumber]);
                loadingList[pageNumber].setVisibility(View.GONE);
                listViewList[pageNumber].setVisibility(View.VISIBLE);
            }
        });
        for (Cartoon cartoon : cartoonList2)
        {
            Log.d(TAG, "run: " + cartoon.getImgUrl());
            Bitmap bitmap = MainApplication.getInstance().loadImage(cartoon);
            cartoon.setBitmap(bitmap);
        }

        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                cartoonListViewAdapterList[pageNumber].notifyDataSetChanged();
                listViewList[pageNumber].setAdapter(cartoonListViewAdapterList[pageNumber]);
            }
        });
        Log.d(TAG, "run: 第2页加载完成");
    }

    /**
     * load3
     *
     * @param activity 活动
     */
    private void load3(Activity activity)
    {
        int pageNumber = 2;
        CartoonService cartoonService = MainApplication.getInstance().getCartoonService();
        cartoonList3 = cartoonService.getCartoonList(URLConstant.rankUrl3, URLConstant.rankUrl3Type);
        Log.d(TAG, "run: 大小：" + cartoonList3.size());
        Log.d(TAG, "run: 请求完成：\n" + cartoonList3);
        cartoonListViewAdapterList[pageNumber] = new CartoonListViewAdapter(activity, cartoonList3);
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                cartoonListViewAdapterList[pageNumber].notifyDataSetChanged();
                listViewList[pageNumber].setAdapter(cartoonListViewAdapterList[pageNumber]);
                loadingList[pageNumber].setVisibility(View.GONE);
                listViewList[pageNumber].setVisibility(View.VISIBLE);
            }
        });
        for (Cartoon cartoon : cartoonList3)
        {
            Log.d(TAG, "run: " + cartoon.getImgUrl());
            Bitmap bitmap = MainApplication.getInstance().loadImage(cartoon);
            cartoon.setBitmap(bitmap);
        }

        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                cartoonListViewAdapterList[pageNumber].notifyDataSetChanged();
                listViewList[pageNumber].setAdapter(cartoonListViewAdapterList[pageNumber]);
            }
        });
        Log.d(TAG, "run: 第3页加载完成");
    }


    /**
     * load4
     *
     * @param activity 活动
     */
    private void load4(Activity activity)
    {
        int pageNumber = 3;
        CartoonService cartoonService = MainApplication.getInstance().getCartoonService();
        cartoonList4 = cartoonService.getCartoonList(URLConstant.rankUrl4, URLConstant.rankUrl4Type);
        Log.d(TAG, "run: 大小：" + cartoonList4.size());
        Log.d(TAG, "run: 请求完成：\n" + cartoonList4);
        cartoonListViewAdapterList[pageNumber] = new CartoonListViewAdapter(activity, cartoonList4);
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                cartoonListViewAdapterList[pageNumber].notifyDataSetChanged();
                listViewList[pageNumber].setAdapter(cartoonListViewAdapterList[pageNumber]);
                loadingList[pageNumber].setVisibility(View.GONE);
                listViewList[pageNumber].setVisibility(View.VISIBLE);
            }
        });
        for (Cartoon cartoon : cartoonList4)
        {
            Log.d(TAG, "run: " + cartoon.getImgUrl());
            Bitmap bitmap = MainApplication.getInstance().loadImage(cartoon);
            cartoon.setBitmap(bitmap);
        }

        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                cartoonListViewAdapterList[pageNumber].notifyDataSetChanged();
                listViewList[pageNumber].setAdapter(cartoonListViewAdapterList[pageNumber]);
            }
        });
        Log.d(TAG, "run: 第4页加载完成");
    }


    /**
     * load5
     *
     * @param activity 活动
     */
    private void load5(Activity activity)
    {
        int pageNumber = 4;
        CartoonService cartoonService = MainApplication.getInstance().getCartoonService();
        cartoonList5 = cartoonService.getCartoonList(URLConstant.rankUrl5, URLConstant.rankUrl5Type);
        Log.d(TAG, "run: 大小：" + cartoonList5.size());
        Log.d(TAG, "run: 请求完成：\n" + cartoonList5);
        cartoonListViewAdapterList[pageNumber] = new CartoonListViewAdapter(activity, cartoonList5);
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                cartoonListViewAdapterList[pageNumber].notifyDataSetChanged();
                listViewList[pageNumber].setAdapter(cartoonListViewAdapterList[pageNumber]);
                loadingList[pageNumber].setVisibility(View.GONE);
                listViewList[pageNumber].setVisibility(View.VISIBLE);
            }
        });
        for (Cartoon cartoon : cartoonList5)
        {
            Log.d(TAG, "run: " + cartoon.getImgUrl());
            Bitmap bitmap = MainApplication.getInstance().loadImage(cartoon);
            cartoon.setBitmap(bitmap);
        }

        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                cartoonListViewAdapterList[pageNumber].notifyDataSetChanged();
                listViewList[pageNumber].setAdapter(cartoonListViewAdapterList[pageNumber]);
            }
        });
        Log.d(TAG, "run: 第5页加载完成");
    }


    /**
     * load6
     *
     * @param activity 活动
     */
    private void load6(Activity activity)
    {
        int pageNumber = 5;
        CartoonService cartoonService = MainApplication.getInstance().getCartoonService();
        cartoonList6 = cartoonService.getCartoonList(URLConstant.rankUrl6, URLConstant.rankUrl6Type);
        Log.d(TAG, "run: 大小：" + cartoonList6.size());
        Log.d(TAG, "run: 请求完成：\n" + cartoonList6);
        cartoonListViewAdapterList[pageNumber] = new CartoonListViewAdapter(activity, cartoonList6);
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                cartoonListViewAdapterList[pageNumber].notifyDataSetChanged();
                listViewList[pageNumber].setAdapter(cartoonListViewAdapterList[pageNumber]);
                loadingList[pageNumber].setVisibility(View.GONE);
                listViewList[pageNumber].setVisibility(View.VISIBLE);
            }
        });
        for (Cartoon cartoon : cartoonList6)
        {
            Log.d(TAG, "run: " + cartoon.getImgUrl());
            Bitmap bitmap = MainApplication.getInstance().loadImage(cartoon);
            cartoon.setBitmap(bitmap);
        }

        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                cartoonListViewAdapterList[pageNumber].notifyDataSetChanged();
                listViewList[pageNumber].setAdapter(cartoonListViewAdapterList[pageNumber]);
            }
        });
        Log.d(TAG, "run: 第6页加载完成");
    }


    @Override
    public int getCount()
    {
        return 6;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object)
    {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
        Log.d(TAG, "instantiateItem: 页面初始化：" + position);
        //container.addView(listViewList[position]);
        container.addView(linearLayoutList[position]);
        return linearLayoutList[position];
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
    {
        Log.d(TAG, "instantiateItem: 页面销毁：" + position);
        //container.removeView(listViewList[position]);
        container.removeView(linearLayoutList[position]);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        return titleList[position];
    }

    /**
     * 显示消息
     *
     * @param message 消息
     */
    private void toastShow(String message)
    {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

}
