package com.medmax.potholedetector.threads;

import android.os.Process;
import android.util.Log;

import com.medmax.potholedetector.config.AppSettings;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Max Medina on 2017-07-08.
 */

public class ThreadManager {

    private static final String LOG_TAG = ThreadManager.class.getSimpleName();
    private static ThreadManager sInstance = null;
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    private final ExecutorService mExecutorService;
    private final BlockingQueue<Runnable> mTaskQueue;
    private List<Future> mRunningTaskList;

    // The class is used as a singleton
    // Initialize the class
    static {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        sInstance = new ThreadManager();
    }

    private ThreadManager(){
        mTaskQueue = new LinkedBlockingQueue<Runnable>();
        mRunningTaskList = new ArrayList<>();

//        Log.e(LOG_TAG,"Available cores: " + NUMBER_OF_CORES);
        mExecutorService = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES*2, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mTaskQueue, new BackgroundThreadFactory());
    }

    public static ThreadManager getsInstance() {
        return sInstance;
    }

    // Add a callable to the queue, which will be executed by the next available thread in the pool
    public void addCallable(Callable callable){
        Future future = mExecutorService.submit(callable);
        mRunningTaskList.add(future);
    }

    /* Remove all tasks in the queue and stop all running threads
     * Notify UI thread about the cancellation
     */
    public void cancelAllTasks() {
        synchronized (this) {
            mTaskQueue.clear();
            for (Future task : mRunningTaskList) {
                if (!task.isDone()) {
                    task.cancel(true);
                }
            }
            mRunningTaskList.clear();
        }
    }

    private static class BackgroundThreadFactory implements ThreadFactory {
        public static final String LOG_TAG = BackgroundThreadFactory.class.getSimpleName();
        private static int sTag = 1;

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName(AppSettings.POTHOLE_LOG_THREAD + sTag++);
            thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);

            // A exception handler is created to log the exception from threads
            thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    Log.e(LOG_TAG, thread.getName() + " encountered an error: " + ex.getMessage());
                }
            });
            return thread;
        }
    }
}