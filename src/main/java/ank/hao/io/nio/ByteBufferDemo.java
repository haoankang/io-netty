package ank.hao.io.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ByteBufferDemo {

    public static void main(String[] args) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put("你好".getBytes());
//        print(byteBuffer);
//        System.out.println(byteBuffer.get());
//
//        System.out.println(byteBuffer.position());
//
//        byteBuffer.flip();
//        System.out.println(byteBuffer.asCharBuffer().get(1));
//        print(byteBuffer);
//        byteBuffer.flip();
//        print(byteBuffer);

        FileChannel fileChannel = FileChannel.open(Paths.get("C:\\Users\\Administrator\\Documents\\test\\test.txt"), new StandardOpenOption[]{StandardOpenOption.READ});
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(1024);

        while(fileChannel.read(byteBuffer1)!=-1){
            byteBuffer1.flip();
            System.out.println(new String(byteBuffer1.array(),0,byteBuffer.limit(),"GBK"));
            byteBuffer1.clear();
        }
        //byteBuffer1.flip();

    }

    public static void print(ByteBuffer byteBuffer){
        System.out.println("byteBuffer capacity is: "+byteBuffer.capacity()
                +", position is: "+byteBuffer.position()+", limit is: "+byteBuffer.limit());

    }
}
