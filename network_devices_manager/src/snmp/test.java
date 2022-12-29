package snmp;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.net.*;
import java.util.*;
import static java.lang.System.out;

public class test {

	public static void main(String args[]) throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets))
            displayInterfaceInformation(netint);
    }

    static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        ArrayList<InetAddress> list = Collections.list(inetAddresses);
        if (list.size() > 0) {
            System.out.println("Display name: "+ netint.getDisplayName());
            System.out.println("Name: "+ netint.getName());
            for (InetAddress inetAddress : list) {
            	System.out.println("InetAddress: "+ inetAddress.getHostAddress());
            }
            out.printf("\n");
        }
     }
}
