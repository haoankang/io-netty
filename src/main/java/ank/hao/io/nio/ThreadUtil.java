package ank.hao.io.nio;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtil {

    public static ExecutorService executors = Executors.newFixedThreadPool(3);
}
