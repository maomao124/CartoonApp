package mao.cartoonapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import mao.cartoonapp.R;
import mao.cartoonapp.entity.Cartoon;
import mao.cartoonapp.entity.CartoonItem;

/**
 * Project name(项目名称)：CartoonApp
 * Package(包名): mao.cartoonapp.adapter
 * Class(类名): CartoonItemListViewAdapter
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/10/12
 * Time(创建时间)： 15:55
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class CartoonItemListViewAdapter extends BaseAdapter
{

    private final Context context;

    private final List<CartoonItem> cartoonItemList;

    public CartoonItemListViewAdapter(Context context, List<CartoonItem> cartoonItemList)
    {
        this.context = context;
        this.cartoonItemList = cartoonItemList;
    }

    @Override
    public int getCount()
    {
        return cartoonItemList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return cartoonItemList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        CartoonItemListViewHolder cartoonItemListViewHolder;
        if (convertView == null)
        {
            cartoonItemListViewHolder = new CartoonItemListViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cartoonitem, null);
            cartoonItemListViewHolder.textViewId = convertView.findViewById(R.id.id);
            cartoonItemListViewHolder.name = convertView.findViewById(R.id.name);
            convertView.setTag(cartoonItemListViewHolder);
        }
        else
        {
            cartoonItemListViewHolder = (CartoonItemListViewHolder) convertView.getTag();
        }
        CartoonItem cartoonItem = cartoonItemList.get(position);
        String textViewId = cartoonItem.getTextViewId();
        cartoonItemListViewHolder.textViewId.setText(textViewId);
        cartoonItemListViewHolder.name.setText(cartoonItem.getName());
        return convertView;
    }

    private static class CartoonItemListViewHolder
    {
        /**
         * 文本视图id
         */
        public TextView textViewId;

        /**
         * 名字
         */
        public TextView name;
    }
}
