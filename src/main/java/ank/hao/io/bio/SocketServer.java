package ank.hao.io.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {

    public static final String ip = "127.0.0.1";
    public static final int port = 8911;
    public static final ExecutorService executor = Executors.newFixedThreadPool(3);

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(ip,port));

        while(true){
            Socket socket = serverSocket.accept();
            executor.execute(new ServerHandle(socket));
        }
    }
}

class ServerHandle implements Runnable{

    private Socket socket;

    public ServerHandle(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("client connect.."+socket.toString());

        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String msg;
            while(true){
                msg=bufferedReader.readLine();
                if((msg) != null){
                    System.out.println("server accept msg: " + msg);
                }else {
                    break;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
