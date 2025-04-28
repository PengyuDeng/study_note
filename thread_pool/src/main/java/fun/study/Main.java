package fun.study;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author bigdata_dpy
 */
public class Main {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()-> System.out.println(111));
        ThreadPoll threadPoll = new ThreadPoll(5, 7, new ArrayBlockingQueue<>(10));

    }
}
