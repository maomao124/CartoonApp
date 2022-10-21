package mao.cartoonapp.constant;

/**
 * Project name(项目名称)：CartoonApp
 * Package(包名): mao.cartoonapp.constant
 * Class(类名): OtherConstant
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/10/21
 * Time(创建时间)： 13:07
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class OtherConstant
{
    /**
     * js代码，目的是屏蔽一些广告和一些额外的元素
     */
    public static final String js = "function getClass(parent,sClass)\n" +
            "{\n" +
            "\tvar aEle=parent.getElementsByTagName('div');\n" +
            "\tvar aResult=[];\n" +
            "\tvar i=0;\n" +
            "\tfor(i<0;i<aEle.length;i++)\n" +
            "\t{\n" +
            "\t\tif(aEle[i].className==sClass)\n" +
            "\t\t{\n" +
            "\t\t\taResult.push(aEle[i]);\n" +
            "\t\t}\n" +
            "\t};\n" +
            "\treturn aResult;\n" +
            "}\n" +
            "\n" +
            "function hideOther() \n" +
            "{\n" +
            "\tgetClass(document,'guide-download')[0].style.display='none';\n" +
            "\tgetClass(document,'xxtop')[0].style.display='none';\n" +
            "\tgetClass(document,'list3_1 similar clearfix mt10')[0].style.display='none';\n" +
            "\tgetClass(document,'read-end')[0].style.display='none';\n" +
            "\tgetClass(document,'comment-box hide')[0].style.display='none';\n" +
            "\tgetClass(document,'comment-input-box')[0].style.display='none';\n" +
            "\tdocument.getElementById('adhtml').style.display='none';\n" +
            "}";


    public static final String softwareDocumentation = "" +
            "      ,-.,-.  \n" +
            "     (  (  (        \n" +
            "      \\  )  ) _..-.._   \n" +
            "     __)/ ,','       `.\n" +
            "   ,\"     `.     ,--.  `.     \n" +
            " ,\"   @        .'    `   \\\n" +
            "(Y            (           ;''.\n" +
            " `--.____,     \\          ,  ; \n" +
            " ((_ ,----' ,---'      _,'_,'    \n" +
            "     (((_,- (((______,-'" +
            "\n" +
            "1.长按列表项可以添加漫画到收藏夹\n" +
            "2.搜索页面长按列表项也可以收藏\n" +
            "3.历史记录页面默认按最近观看的漫画时间降序排序\n" +
            "4.历史记录页面因为拿不到正在观看的章节名称，没有显示该字段，除非解析html\n" +
            "5.收藏夹页面因为后端没有通过id获取漫画信息的请求接口，还有漫画更新比较频繁，" +
            "所以最后一章节字段没办法显示，也没法推送通知发送漫画更新消息\n" +
            "6.收藏夹页面长按列表项可以取消收藏\n" +
            "7.右上角的菜单可以使用\n" +
            "8.开发此软件目的是为了学习安卓\n" +
            "9.作者QQ：1296193245\n" +
            "10.作者github：https://github.com/maomao124/" +
            "\n\n" +
            "当前版本：v1.5" +
            "\n\n\n" +
            "版本更新说明：" +
            "\n\n" +
            "\n" +
            "\n" +
            "v1.5:\n" +
            "2022/10/21\n" +
            "1.优化线程池，增加救急线程的数量，减少核心线程的数量\n" +
            "2.由于图片保存在缓存目录，该目录的文件经常会被操作系统删除，重新加载需要消耗流量，所以我将图片的缓存目录由缓存目录更改到了普通目录，由用户自己管理\n" +
            "3.主菜单添加删除缓存的功能\n" +
            "4.搜索页面的输入框添加按回车键即可开启搜索的功能，点击回车后自动关闭输入法\n" +
            "5.搜索页面输入的关键字必须至少两个字" +
            "6.更改漫画观看页面历史记录追踪逻辑加载的优先级，优化代码\n" +
            "7.漫画观看页面添加正在加载的提示\n" +
            "" +
            "" +
            "\n" +
            "\n" +
            "\n" +
            "v1.4：\n" +
            "2022/10/15\n" +
            "1.修复进入漫画目录页面有一定概率闪退的问题，并发安全问题，使用JUC的CountDownLatch解决此并发问题" +
            "\n\n\n" +
            "v1.3：\n" +
            "2022/10/15\n" +
            "1.修复点击给项目点赞的菜单选项后会退出程序的问题\n" +
            "2.优化后台更新服务\n" +
            "\n" +
            "\n" +
            "v1.2：\n" +
            "2022/10/15\n" +
            "1.添加观看完成后的逻辑，观看完成(读完最新章节)后返回到目录，点击到其它页面也会返回到目录\n" +
            "2.给漫画目录页面的大图也添加本地缓存，优先从本地缓存去取，而不是直接从资源服务器上加载，" +
            "之前调错方法了，参数和返回值都一样\n" +
            "3.漫画目录页面支持显示正在观看的章节目录了，用其它颜色标注\n" +
            "4.软件添加软件更新检查功能，本人没有后端服务器和公网ip，使用的是github上的服务器，通过解析html来实现更新\n" +
            "5.主菜单页面添加一个给项目点赞的菜单选项" +
            "\n" +
            "\n\n" +
            "v1.1：\n" +
            "2022/10/13\n" +
            "1.优化搜索页面结果显示，还是异步加载，但是分成了两阶段\n" +
            "2.主题由安卓默认颜色更改成#00ccff(天蓝色)\n" +
            "3.漫画详情页面底部添加了两个按钮\n" +
            "4.漫画详情页面添加了漫画加入到收藏功能\n" +
            "5.漫画详情页面添加了开始阅读或者继续阅读功能\n" +
            "6.历史记录页面添加了最近阅读时间显示，原为remark字段\n" +
            "7.添加了在历史记录页面长按列表项进入漫画详情页面的功能";

}
