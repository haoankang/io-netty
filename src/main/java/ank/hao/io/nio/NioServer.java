package ank.hao.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioServer {

    private String ip;
    private int port;
    public static final ExecutorService executor = Executors.newFixedThreadPool(3);

    public NioServer(String ip,int port){
        this.ip = ip;
        this.port = port;

        System.out.println("test begin..");

        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            Selector selector = Selector.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            m: while(true){
                selector.select(1000);
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                //System.out.println(Thread.currentThread()+"---"+selectionKeySet.size());
                Iterator<SelectionKey> keyIterator = selectionKeySet.iterator();
                SelectionKey key = null;
                while(keyIterator.hasNext()){
                    key = keyIterator.next();
                    execute(key);
                    keyIterator.remove();
                }
                //System.out.println(Thread.currentThread()+"---"+selectionKeySet.size());

                //TimeUnit.SECONDS.sleep(10);
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    void execute(SelectionKey key){
        if(key.isValid()){
            if(key.isAcceptable()){
                System.out.println("---------------------------------------"+key);
                try {
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("连接客户端通道："+socketChannel);
                    //System.out.println("当前线程："+Thread.currentThread());
                    //注册写事件监听
//                    socketChannel.configureBlocking(false);
//                    socketChannel.register(key.selector(),SelectionKey.OP_READ);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new NioServer("localhost", 8910);
    }
}
