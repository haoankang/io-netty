package ank.socket.demo;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Demo1 {

    public static void main(String[] args) throws SocketException {
        int m=0;
        Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
        while(enumeration.hasMoreElements()){
            m++;
            NetworkInterface networkInterface = enumeration.nextElement();
            System.out.println("networkInterface name: "+networkInterface.getName()+",isLoopback: "+ networkInterface.isLoopback());
        }
        System.out.println(m);
    }
}
