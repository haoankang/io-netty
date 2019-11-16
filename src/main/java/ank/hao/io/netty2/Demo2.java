package ank.hao.io.netty2;

import java.util.concurrent.TimeUnit;

public class Demo2 {

    public static void main(String[] args) throws InterruptedException {
        new Thread(new ProtocServer(8894)).start();
        TimeUnit.SECONDS.sleep(1);
        new Thread(new ProtocClient(8894)).start();
    }
}
