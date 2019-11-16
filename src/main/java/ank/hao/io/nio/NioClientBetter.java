package ank.hao.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class NioClientBetter {

    private int port;

    private volatile boolean flag = true;

    public NioClientBetter(int port){
        this.port = port;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void execute(){
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            if(socketChannel.connect(new InetSocketAddress(port))){
                socketChannel.finishConnect();
                socketChannel.register(selector, SelectionKey.OP_WRITE);
            }else{
                socketChannel.register(selector, SelectionKey.OP_CONNECT);
            }

            while(flag){
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey selectionKey = null;
                while(iterator.hasNext()){
                    selectionKey = iterator.next();
                    iterator.remove();
                    handleClient(selectionKey, selector);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void handleClient(SelectionKey selectionKey, Selector selector) throws IOException {
        if(selectionKey.isValid()){
            if(selectionKey.isConnectable()){
                System.out.println("client connect key: "+selectionKey.toString());
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                socketChannel.finishConnect();
                socketChannel.register(selector, SelectionKey.OP_WRITE);
            }else if(selectionKey.isWritable()){
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                System.out.println("client write ready: "+socketChannel);
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                byteBuffer.put("只是第一个测试.".getBytes(StandardCharsets.UTF_8));
                byteBuffer.flip();
                socketChannel.write(byteBuffer);
                socketChannel.register(selector, SelectionKey.OP_READ);
            }else if(selectionKey.isReadable()){
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                System.out.println("client read ready: "+socketChannel);
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                while(socketChannel.read(byteBuffer)>0){
                    byteBuffer.flip();
                    System.out.println(new String(byteBuffer.array(),0,byteBuffer.limit(), StandardCharsets.UTF_8));
                }
                socketChannel.register(selector, SelectionKey.OP_WRITE);

                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                flag = false;
            }
        }
    }

    public static void main(String[] args) {
        new NioClientBetter(9931).execute();
    }
}
