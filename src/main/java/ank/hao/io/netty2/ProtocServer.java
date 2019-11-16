package ank.hao.io.netty2;

import ank.hao.io.protobuf.code.AddressBookProtos.*;
import ank.hao.io.protobuf.code.TreeProtos.*;
import com.google.protobuf.MessageLite;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class ProtocServer implements Runnable{

    private int port;

    public ProtocServer(int port){
        this.port = port;
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChildChannelHandler());

            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline().addLast(new ProtobufDecoder(AddressBook.getDefaultInstance()));
            socketChannel.pipeline().addLast(new ProtobufEncoder());
            socketChannel.pipeline().addLast(new ServerHandler());
        }
    }

    class ServerHandler extends ChannelInboundHandlerAdapter{
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            AddressBook addressBook = (AddressBook) msg;
            System.out.println("server receive msg: --------------");
            System.out.println(addressBook);

            Tree tree = buildTree();
            ctx.writeAndFlush(tree);
        }

        Tree buildTree(){
            Tree.Builder tree = Tree.newBuilder();
            tree.setName("tree1").setAge(113).setTreeType(Tree.TreeType.YANGSHU);

            Tree.Leaf.Builder leaf = Tree.Leaf.newBuilder();
            leaf.setLeafId(11102L).setLeafType(Tree.LeafType.RED);
            tree.addLeaf(leaf.build());
            leaf.setLeafId(11103L).setLeafType(Tree.LeafType.YELLOW);
            tree.addLeaf(leaf.build());

            return tree.build();
        }
    }
}
