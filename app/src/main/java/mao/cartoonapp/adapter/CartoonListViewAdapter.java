package mao.cartoonapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mao.cartoonapp.R;
import mao.cartoonapp.entity.Cartoon;

/**
 * Project name(项目名称)：CartoonApp
 * Package(包名): mao.cartoonapp.adapter
 * Class(类名): CartoonListViewAdapter
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/10/12
 * Time(创建时间)： 13:48
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class CartoonListViewAdapter extends BaseAdapter
{

    /**
     * 上下文
     */
    private final Context context;

    /**
     * 漫画列表
     */
    private final List<Cartoon> cartoonList;

    /**
     * remark TextView默认的颜色
     */
    private final int remarksDefaultColor;


    public CartoonListViewAdapter(Context context, List<Cartoon> cartoonList)
    {
        this.context = context;
        this.cartoonList = cartoonList;

        View view = LayoutInflater.from(context).inflate(R.layout.item_listview_cartoon, null);
        TextView textView = view.findViewById(R.id.remarks);
        remarksDefaultColor = textView.getTextColors().getDefaultColor();
    }

    @Override
    public int getCount()
    {
        return cartoonList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return cartoonList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        CartoonListViewHolder cartoonListViewHolder;
        if (convertView == null)
        {
            cartoonListViewHolder = new CartoonListViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_cartoon, null);
            cartoonListViewHolder.image = convertView.findViewById(R.id.image);
            cartoonListViewHolder.name = convertView.findViewById(R.id.name);
            cartoonListViewHolder.author = convertView.findViewById(R.id.author);
            cartoonListViewHolder.remarks = convertView.findViewById(R.id.remarks);
            convertView.setTag(cartoonListViewHolder);
        }
        else
        {
            cartoonListViewHolder = (CartoonListViewHolder) convertView.getTag();
        }
        Cartoon cartoon = cartoonList.get(position);
        cartoonListViewHolder.image.setImageBitmap(cartoon.getBitmap());
        cartoonListViewHolder.name.setText(cartoon.getName());
        cartoonListViewHolder.author.setText(cartoon.getAuthor());
        cartoonListViewHolder.remarks.setText(cartoon.getRemarks());
        if (cartoon.getRemarks().contains("漫画已更新："))
        {
            cartoonListViewHolder.remarks.setTextColor(Color.rgb(255, 140, 200));
        }
        else
        {
            cartoonListViewHolder.remarks.setTextColor(this.remarksDefaultColor);
        }
        return convertView;
    }


    private static class CartoonListViewHolder
    {
        /**
         * 图像
         */
        public ImageView image;
        /**
         * 名字
         */
        public TextView name;
        /**
         * 作者
         */
        public TextView author;

        /**
         * 最后一章节
         */
        public TextView remarks;
    }

}
