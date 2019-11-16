package ank.hao.io.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.util.Calendar;

public class MyNettyServer implements Runnable {

    public static void main(String[] args) {
        new Thread(new MyNettyServer(8893)).start();
    }

    private int port;

    public MyNettyServer(int port){
        this.port = port;
    }

    public void run(){

        System.out.println("server start..");
        //配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChildChannelHandler3());
            //绑定端口，同步等待成功
            ChannelFuture f = b.bind("localhost",port).sync();
            //等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

            System.out.println("server stop..");
        }
    }
}

class ChildChannelHandler3 extends ChannelInitializer<SocketChannel>{

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
        socketChannel.pipeline().addLast(new FixedLengthFrameDecoder(5));
        socketChannel.pipeline().addLast(new StringDecoder());
        socketChannel.pipeline().addLast(new TimeServerHandler3());
    }
}

class TimeServerHandler3 extends ChannelInboundHandlerAdapter {

    private int count = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        System.out.println("The time server receive order: "+body+", the count is "+(++count));

        String currentTime = "QUERY".equalsIgnoreCase(body)?Calendar.getInstance().getTime().toString():"BAD ORDER";
        currentTime = currentTime.concat("%$#");
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.writeAndFlush(resp);
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

class ChildChannelHandler2 extends ChannelInitializer<SocketChannel>{

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ByteBuf buf = Unpooled.copiedBuffer("*&^".getBytes());
        socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,buf));
        socketChannel.pipeline().addLast(new StringDecoder());
        socketChannel.pipeline().addLast(new TimeServerHandler2());
    }
}

class TimeServerHandler2 extends ChannelInboundHandlerAdapter {

    private int count = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        System.out.println("The time server receive order: "+body+", the count is "+(++count));

        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)?Calendar.getInstance().getTime().toString():"BAD ORDER";
        currentTime = currentTime.concat("%$#");
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.writeAndFlush(resp);
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

class ChildChannelHandler extends ChannelInitializer<SocketChannel>{

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
        socketChannel.pipeline().addLast(new StringDecoder());
        socketChannel.pipeline().addLast(new TimeServerHandler());
    }
}

class TimeServerHandler extends ChannelInboundHandlerAdapter {

    private int count = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buf = (ByteBuf) msg;
//        byte[] bytes = new byte[buf.readableBytes()];
//        buf.readBytes(bytes);
//        String body = new String(bytes, StandardCharsets.UTF_8);
        String body = (String) msg;
        System.out.println("The time server receive order: "+body+", the count is "+(++count));

        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)?Calendar.getInstance().getTime().toString():"BAD ORDER";
        currentTime = currentTime.concat(System.getProperty("line.separator"));
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.writeAndFlush(resp);
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