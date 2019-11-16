package ank.hao.io.netty;

import java.util.concurrent.TimeUnit;

public class Demo {

    public static void main(String[] args) throws InterruptedException {
        int port = 8892;
        new Thread(new MyNettyServer(port)).start();
        TimeUnit.SECONDS.sleep(2);
        for(int i=0;i<1;i++){
            new Thread(new MyNettyClient(port)).start();
            TimeUnit.SECONDS.sleep(1);
        }


    }
}
