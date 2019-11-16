package ank.hao.io.bio;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient {

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            socket.connect(new InetSocketAddress(SocketServer.ip, SocketServer.port));


            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String send_msg = "sdgasdgas";
            bufferedWriter.write(send_msg);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            while((send_msg=bufferedReader.readLine())!=null){
                //System.out.println("client send msg: "+send_msg);
                bufferedWriter.write(send_msg);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }finally {
            System.out.println("client close");
            bufferedReader.close();
            socket.close();
        }

    }
}
