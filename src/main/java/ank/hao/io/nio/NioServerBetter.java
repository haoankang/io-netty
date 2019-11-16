package ank.hao.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class NioServerBetter {

    private String ip;
    private int port;

    private AtomicBoolean flag = new AtomicBoolean(true);

    public void setFlag(Boolean flg) {
        this.flag.set(flg);
    }

    public NioServerBetter(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    void run(){
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(port));
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while(flag.get()){
                selector.select(1000);

                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeySet.iterator();
                while(iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    handleKey(selectionKey,selector);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void handleKey(SelectionKey selectionKey,Selector selector) throws IOException {
        if(selectionKey.isValid()){
            if(selectionKey.isAcceptable()){
                System.out.println("server accept key: "+selectionKey.toString());
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_READ);
            }else if(selectionKey.isReadable()){
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                System.out.println("server read ready: "+socketChannel);
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                while(socketChannel.read(byteBuffer)>0){
                    byteBuffer.flip();
                    System.out.println(new String(byteBuffer.array(),0,byteBuffer.limit(), StandardCharsets.UTF_8));
                }
                socketChannel.register(selector, SelectionKey.OP_WRITE);
            }else if(selectionKey.isWritable()){
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                System.out.println("server write ready: "+socketChannel);
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                byteBuffer.put("服务端反馈信息".getBytes(StandardCharsets.UTF_8));
                byteBuffer.flip();
                socketChannel.write(byteBuffer);
                socketChannel.register(selector, SelectionKey.OP_READ);
            }
        }
    }

    public static void main(String[] args) {
        new NioServerBetter("localhost", 9931).run();
    }
}
