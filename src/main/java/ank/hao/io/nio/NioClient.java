package ank.hao.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioClient {

    private int port;

    private int port_server;

    public NioClient(int port, int port_server){
        this.port = port;
        this.port_server = port_server;
        try {
            SocketChannel socketChannel = SocketChannel.open();
            //socketChannel.bind(new InetSocketAddress(port));
            socketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            //socketChannel.register(selector, SelectionKey.OP_CONNECT);

            //socketChannel.connect(new InetSocketAddress(port_server));

            if(socketChannel.connect(new InetSocketAddress(port_server))){
                socketChannel.register(selector, SelectionKey.OP_READ);
                //socketChannel.finishConnect();
            }else {
                socketChannel.register(selector, SelectionKey.OP_CONNECT);
            }

            while(true){
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();
                while(selectionKeyIterator.hasNext()){
                    SelectionKey selectionKey = selectionKeyIterator.next();
                    selectionKeyIterator.remove();
                    doo(selectionKey);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void doo(SelectionKey selectionKey){
        if(selectionKey.isValid()){
            if(selectionKey.isConnectable()){

                System.out.println("************");
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                try {
                    socketChannel.finishConnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("连接服务端通道："+socketChannel.toString());

            }
        }
    }

    public static void main(String[] args) {
        new NioClient(8911,8910);
    }
}