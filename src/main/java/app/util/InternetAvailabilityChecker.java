package app.util;

import app.tray.notification.NotificationType;
import app.tray.notification.TrayNotification;
import javafx.util.Duration;

import java.io.IOException;
import java.net.*;

public class InternetAvailabilityChecker {
    public static boolean isInternetAvailable() throws IOException
    {
        return isHostAvailable("google.com") || isHostAvailable("amazon.com")
                || isHostAvailable("facebook.com")|| isHostAvailable("apple.com");
    }

    private static boolean isHostAvailable(String hostName) throws IOException
    {
        Socket socket = new Socket();
        try {
            int port = 80;
            InetSocketAddress socketAddress = new InetSocketAddress(hostName, port);
            socket.connect(socketAddress, 3000);

            return true;
        }
        catch(UnknownHostException | NoRouteToHostException e)
        {
            return false;
        }

    }
    public static String getLocalIp() throws SocketException {
        DatagramSocket socket = new DatagramSocket();
        try{
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return socket.getLocalAddress().getHostAddress();
    }
}