package edu.spbu.client_server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain
{
    public static void main(String[] args) throws IOException
    {
        ServerSocket sc = new ServerSocket(8080);
        while (true) {
            Socket s = sc.accept();
            Server m = new Server(s);
            m.readInputStream();
            m.writeOutputStream();
            m.sc.close();

        }
    }
}
