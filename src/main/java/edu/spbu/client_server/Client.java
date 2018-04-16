package edu.spbu.client_server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client
{
    private Socket sc;
    private InputStream inputStream;
    private OutputStream outputStream;
    public Client(Socket sc) throws IOException
    {
        this.sc = sc;
        this.inputStream = sc.getInputStream();
        this.outputStream = sc.getOutputStream();
    }
    public void writeOutputStream() throws IOException
    {
        String s = "GET /server_client.html HTTP/1.0\r\n\r\n";
        outputStream.write(s.getBytes());
        outputStream.flush();
    }
    public void readInputStream() throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String s = br.readLine();
        while (s!=null){
            System.out.println(s);
            s = br.readLine();
        }
    }
}
