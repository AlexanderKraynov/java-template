package edu.spbu.client_server;
import java.io.IOException;
import java.net.*;

public class ClientMain
{
    public static void main(String[] args) throws IOException
    {
        Socket clientSocket = new Socket(InetAddress.getByName("localhost"), 8080);
        Client client = new Client(clientSocket);
        client.writeOutputStream();
        client.readInputStream();
    }

}
