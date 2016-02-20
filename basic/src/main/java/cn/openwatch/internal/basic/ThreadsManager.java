package cn.openwatch.internal.basic;

import android.os.Looper;
import android.os.Process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class ThreadsManager {

    private static ExecutorService defaultExecutor;

    private ThreadsManager() {
    }

    private static ExecutorService createThreadPool(final int processPriority, final boolean isDaemon) {
        ThreadFactory factory = new ThreadFactory() {

            @Override
            public Thread newThread(final Runnable r) {
                // TODO Auto-generated method stub
                Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Process.setThreadPriority(processPriority);
                        r.run();
                    }
                });
                t.setDaemon(isDaemon);
                return t;
            }
        };

        return Executors.newCachedThreadPool(factory);
    }

    public static void setDefaultThreadPool(ExecutorService defaultThreadPool) {
        ThreadsManager.defaultExecutor = defaultThreadPool;
    }

    public static ExecutorService getDefaultThreadPool() {
        if (defaultExecutor == null) {
            synchronized (ThreadsManager.class) {
                if (defaultExecutor == null)
                    defaultExecutor = createThreadPool(Process.THREAD_PRIORITY_BACKGROUND, false);
            }
        }
        return defaultExecutor;
    }

    /**
     * 若当前调用在子线程 则直接执行 否则在线程池中执行
     *
     * @param command runnable
     */
    public static void execute(Runnable command) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // 在主线程则在线程池中执行
            getDefaultThreadPool().execute(command);
        } else {
            command.run();
        }
    }

}
