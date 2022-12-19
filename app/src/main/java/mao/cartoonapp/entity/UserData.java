package mao.cartoonapp.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.List;

/**
 * Project name(项目名称)：CartoonApp
 * Package(包名): mao.cartoonapp.entity
 * Class(类名): UserData
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/12/18
 * Time(创建时间)： 20:39
 * Version(版本): 1.0
 * Description(描述)： 用户数据实体类
 */

public class UserData implements Serializable
{
    /**
     * 漫画收藏数据
     */
    private List<Cartoon> cartoonFavoritesData;

    /**
     * 卡通历史数据
     */
    private List<CartoonHistory> cartoonHistoryData;

    /**
     * 漫画更新数据
     */
    private List<CartoonUpdate> cartoonUpdateData;

    /**
     * 用户数据
     */
    public UserData()
    {

    }

    /**
     * 用户数据
     *
     * @param cartoonFavoritesData 漫画收藏数据
     * @param cartoonHistoryData   漫画历史数据
     * @param cartoonUpdateData    漫画更新数据
     */
    public UserData(List<Cartoon> cartoonFavoritesData, List<CartoonHistory> cartoonHistoryData, List<CartoonUpdate> cartoonUpdateData)
    {
        this.cartoonFavoritesData = cartoonFavoritesData;
        this.cartoonHistoryData = cartoonHistoryData;
        this.cartoonUpdateData = cartoonUpdateData;
    }

    /**
     * 漫画收藏数据
     *
     * @return {@link List}<{@link Cartoon}>
     */
    public List<Cartoon> getCartoonFavoritesData()
    {
        return cartoonFavoritesData;
    }

    /**
     * 漫画收藏数据集
     *
     * @param cartoonFavoritesData 漫画收藏数据
     * @return {@link UserData}
     */
    public UserData setCartoonFavoritesData(List<Cartoon> cartoonFavoritesData)
    {
        this.cartoonFavoritesData = cartoonFavoritesData;
        return this;
    }

    /**
     * 卡通历史数据
     *
     * @return {@link List}<{@link CartoonHistory}>
     */
    public List<CartoonHistory> getCartoonHistoryData()
    {
        return cartoonHistoryData;
    }

    /**
     * 卡通历史数据
     *
     * @param cartoonHistoryData 卡通历史数据
     * @return {@link UserData}
     */
    public UserData setCartoonHistoryData(List<CartoonHistory> cartoonHistoryData)
    {
        this.cartoonHistoryData = cartoonHistoryData;
        return this;
    }

    /**
     * 让漫画更新数据
     *
     * @return {@link List}<{@link CartoonUpdate}>
     */
    public List<CartoonUpdate> getCartoonUpdateData()
    {
        return cartoonUpdateData;
    }

    /**
     * 集漫画更新数据
     *
     * @param cartoonUpdateData 漫画更新数据
     * @return {@link UserData}
     */
    public UserData setCartoonUpdateData(List<CartoonUpdate> cartoonUpdateData)
    {
        this.cartoonUpdateData = cartoonUpdateData;
        return this;
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        UserData userData = (UserData) o;

        if (getCartoonFavoritesData() != null ? !getCartoonFavoritesData().equals(userData.getCartoonFavoritesData()) : userData.getCartoonFavoritesData() != null)
        {
            return false;
        }
        if (getCartoonHistoryData() != null ? !getCartoonHistoryData().equals(userData.getCartoonHistoryData()) : userData.getCartoonHistoryData() != null)
        {
            return false;
        }
        return getCartoonUpdateData() != null ? getCartoonUpdateData().equals(userData.getCartoonUpdateData()) : userData.getCartoonUpdateData() == null;
    }

    @Override
    public int hashCode()
    {
        int result = getCartoonFavoritesData() != null ? getCartoonFavoritesData().hashCode() : 0;
        result = 31 * result + (getCartoonHistoryData() != null ? getCartoonHistoryData().hashCode() : 0);
        result = 31 * result + (getCartoonUpdateData() != null ? getCartoonUpdateData().hashCode() : 0);
        return result;
    }

    @Override
    @SuppressWarnings("all")
    public String toString()
    {
        final StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("cartoonFavoritesData：").append(cartoonFavoritesData).append('\n');
        stringbuilder.append("cartoonHistoryData：").append(cartoonHistoryData).append('\n');
        stringbuilder.append("cartoonUpdateData：").append(cartoonUpdateData).append('\n');
        return stringbuilder.toString();
    }

    /**
     * 为json
     * 转换为json
     *
     * @return {@link String}
     */
    public String toJson()
    {
        return JSON.toJSONString(this, true);
    }

    /**
     * json对象
     * json转对象
     *
     * @param jsonString json字符串
     * @return {@link UserData}
     * @throws Exception 异常
     */
    public static UserData jsonToObject(String jsonString) throws Exception
    {
        return JSON.parseObject(jsonString, UserData.class);
    }
}
