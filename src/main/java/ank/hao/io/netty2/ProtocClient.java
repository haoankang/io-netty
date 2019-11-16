package ank.hao.io.netty2;

import ank.hao.io.protobuf.code.AddressBookProtos;
import ank.hao.io.protobuf.code.AddressBookProtos.*;
import ank.hao.io.protobuf.code.TreeProtos;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ProtocClient implements Runnable{

    private int port;

    public ProtocClient(int port){
        this.port = port;
    }

    @Override
    public void run() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChildChannelHandler());

            ChannelFuture future = bootstrap.connect(new InetSocketAddress(port)).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {

            socketChannel.pipeline().addLast(new ProtobufDecoder(TreeProtos.Tree.getDefaultInstance()));
            socketChannel.pipeline().addLast(new ProtobufEncoder());
            socketChannel.pipeline().addLast(new ClientHandler());
        }
    }

    class ClientHandler extends ChannelInboundHandlerAdapter{
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            String path = "src/main/resources/john";
            AddressBook addressBook = AddressBook.parseFrom(new FileInputStream(path));

            ctx.writeAndFlush(addressBook);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            System.out.println("client receive msg: ------------");
            TreeProtos.Tree tree = (TreeProtos.Tree) msg;
            System.out.println(tree);
        }
    }
}
