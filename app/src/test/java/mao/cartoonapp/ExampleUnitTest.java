package mao.cartoonapp;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.concurrent.locks.LockSupport;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest
{
    @Test
    public void addition_isCorrect()
    {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test()
    {
        Thread currentThread = Thread.currentThread();
        for (int i = 0; i < 20; i++)
        {
            int finalI = i;
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    System.out.println("其它线程完成" + finalI);
                    LockSupport.unpark(currentThread);
                }
            }).start();
            LockSupport.park();
            System.out.println("完成" + i);
        }
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}