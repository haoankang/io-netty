package ank.hao.io.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.StandardCharsets;

public class MyNettyClient implements Runnable {

    private int port;

    public MyNettyClient(int port){
        this.port = port;
    }

    public void run(){

        System.out.println("client start..");
        //客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ClientChannelHandler());
            //发起异步连接
            ChannelFuture f = bootstrap.connect("localhost",port).sync();
            //等待客户端连接关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            System.out.println("client stop..");
        }
    }
}

class ClientChannelHandler2 extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ByteBuf buf = Unpooled.copiedBuffer("%$#".getBytes());
        socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,buf));
        socketChannel.pipeline().addLast(new StringDecoder());
        socketChannel.pipeline().addLast(new TimeClientHandler2());
    }
}

class TimeClientHandler2 extends ChannelInboundHandlerAdapter {
    private int count=0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        for(int i=0;i<100;i++){
            String msg = "QUERY TIME ORDER".concat("*&^");
            ByteBuf byteBuf = Unpooled.copiedBuffer(msg.getBytes(StandardCharsets.UTF_8));
            ctx.writeAndFlush(byteBuf);
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buf = (ByteBuf) msg;
//        byte[] req = new byte[buf.readableBytes()];
//        buf.readBytes(req);
//        String body = new String(req, StandardCharsets.UTF_8);
        String body = (String) msg;
        System.out.println("NOW IS : "+body+", the count is "+(++count));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}

class ClientChannelHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
        socketChannel.pipeline().addLast(new StringDecoder());
        socketChannel.pipeline().addLast(new TimeClientHandler());
    }
}

class TimeClientHandler extends ChannelInboundHandlerAdapter {
    private int count=0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        for(int i=0;i<100;i++){
            String msg = "QUERY TIME ORDER".concat(System.getProperty("line.separator"));
            ByteBuf byteBuf = Unpooled.copiedBuffer(msg.getBytes(StandardCharsets.UTF_8));
            ctx.writeAndFlush(byteBuf);
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buf = (ByteBuf) msg;
//        byte[] req = new byte[buf.readableBytes()];
//        buf.readBytes(req);
//        String body = new String(req, StandardCharsets.UTF_8);
        String body = (String) msg;
        System.out.println("NOW IS : "+body+", the count is "+(++count));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}