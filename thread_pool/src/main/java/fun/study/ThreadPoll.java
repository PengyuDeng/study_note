package fun.study;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author bigdata_dpy
 */
public class ThreadPoll {

    /**
     * 核心线程数量
     */
    private int coreSize;

    /**
     * 最大线程数量
     */
    private int maxSize;

    /**
     * 任务队列
     */
    private BlockingQueue<Runnable> workQueue;

    /**
     * 正在运行的核心线程
     */
    private List<Thread> coreThreads = new ArrayList<>();

    /**
     * 正在运行的辅助线程
     */
    private List<Thread> supportThreads = new ArrayList<>();

    public ThreadPoll(int coreSize, int maxSize, BlockingQueue<Runnable> workQueue) {
        this.coreSize = coreSize;
        this.maxSize = maxSize;
        this.workQueue = workQueue;
    }

    public void execute(Runnable task) {
        if (coreThreads.size() < coreSize) {
            Thread thread = new CoreThread(task);
            coreThreads.add(thread);
            thread.start();
            return;
        }
        if (coreThreads.size() + supportThreads.size() < maxSize) {
            Thread thread = new Thread(() -> {
                task.run();
                while (true) {
                    try {
                        Runnable take = workQueue.poll(5, TimeUnit.MINUTES);
                        if (take == null) {
                            break;
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                supportThreads.remove(Thread.currentThread());
            });
            thread.start();
            supportThreads.add(thread);
            return;
        }

        workQueue.add(task);
    }


    class CoreThread extends Thread {

        private Runnable task;

        public CoreThread(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            task.run();
            while (true) {
                try {
                    Runnable take = workQueue.take();
                    take.run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
