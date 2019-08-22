package com.wong.newcachedthreadpooldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 示例一：复用线程池中的线程
        final ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i = 0; i < 10; i++){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Log.d("ExecutorService",Thread.currentThread().getName());
                    // 测试结果
                    // 2019-08-22 15:52:11.058 9955-10062/? D/ExecutorService: pool-1-thread-2
                    //2019-08-22 15:52:11.058 9955-10061/? D/ExecutorService: pool-1-thread-1
                    //2019-08-22 15:52:11.058 9955-10062/? D/ExecutorService: pool-1-thread-2
                    //2019-08-22 15:52:11.060 9955-10064/? D/ExecutorService: pool-1-thread-4
                    //2019-08-22 15:52:11.060 9955-10063/? D/ExecutorService: pool-1-thread-3
                    //2019-08-22 15:52:11.060 9955-10062/? D/ExecutorService: pool-1-thread-2
                    //2019-08-22 15:52:11.060 9955-10065/? D/ExecutorService: pool-1-thread-5
                    //2019-08-22 15:52:11.060 9955-10061/? D/ExecutorService: pool-1-thread-1
                    //2019-08-22 15:52:11.060 9955-10061/? D/ExecutorService: pool-1-thread-1
                    //2019-08-22 15:52:11.061 9955-10066/? D/ExecutorService: pool-1-thread-6

                    // 通过输出可以看到有些线程执行完任务后会空闲下来，有新的任务提交时，会利用空闲线程执行
                }
            });
        }

        // 示例二：超过60秒未销毁的线程将会被终止和销毁
        ExecutorService executorService60 = Executors.newCachedThreadPool();
        System.out.println("executorService60 "+executorService60); // 线程池中没有线程
        for(int i = 0; i < 2;i++){
            executorService60.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        TimeUnit.MILLISECONDS.sleep(500);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Log.d("executorService60",Thread.currentThread().getName());
                }
            });
        }
        try {
            System.out.println("executorService60 "+executorService60);// 线程池中有2个线程
            TimeUnit.SECONDS.sleep(70);
            System.out.println("executorService60 "+executorService60);// 线程池中有0个线程，都超过60秒被销毁了

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 2019-08-22 16:22:24.949 17041-17041/com.wong.newcachedthreadpooldemo I/System.out: executorService60 java.util.concurrent.ThreadPoolExecutor@44a330d[Running, pool size = 0, active threads = 0, queued tasks = 0, completed tasks = 0]
        // 2019-08-22 16:22:24.951 17041-17041/com.wong.newcachedthreadpooldemo I/System.out: executorService60 java.util.concurrent.ThreadPoolExecutor@44a330d[Running, pool size = 2, active threads = 1, queued tasks = 0, completed tasks = 0]
        // 2019-08-22 16:22:25.451 17041-17155/com.wong.newcachedthreadpooldemo D/executorService60: pool-2-thread-1
        // 2019-08-22 16:22:25.451 17041-17156/com.wong.newcachedthreadpooldemo D/executorService60: pool-2-thread-2
        // 2019-08-22 16:23:34.953 17041-17041/com.wong.newcachedthreadpooldemo I/System.out: executorService60 java.util.concurrent.ThreadPoolExecutor@44a330d[Running, pool size = 0, active threads = 0, queued tasks = 0, completed tasks = 2]

        // 在控制台我们看到，输出的service对象里有一个属性pool size，他指的是线程池里的线程数，当过了60秒仍然没有任务来使用线程时，线程会自动释放

    }
}
