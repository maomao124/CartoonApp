package mao.cartoonapp.entity;

/**
 * Project name(项目名称)：CartoonApp
 * Package(包名): mao.cartoonapp.entity
 * Class(类名): CartoonUpdate
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/10/21
 * Time(创建时间)： 15:29
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class CartoonUpdate
{
    /**
     * 漫画的id
     */
    private String id;

    /**
     * 漫画目录的章节数量
     */
    private int itemCount;

    public CartoonUpdate()
    {

    }

    public CartoonUpdate(String id, int itemCount)
    {
        this.id = id;
        this.itemCount = itemCount;
    }

    public String getId()
    {
        return id;
    }

    public CartoonUpdate setId(String id)
    {
        this.id = id;
        return this;
    }

    public int getItemCount()
    {
        return itemCount;
    }

    public CartoonUpdate setItemCount(int itemCount)
    {
        this.itemCount = itemCount;
        return this;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CartoonUpdate that = (CartoonUpdate) o;

        if (getItemCount() != that.getItemCount()) return false;
        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode()
    {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + getItemCount();
        return result;
    }

    @Override
    @SuppressWarnings("all")
    public String toString()
    {
        final StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("id：").append(id).append('\n');
        stringbuilder.append("itemCount：").append(itemCount).append('\n');
        return stringbuilder.toString();
    }
}
