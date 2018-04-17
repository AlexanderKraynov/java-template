package edu.spbu.client_server;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.*;

import java.nio.file.*;
import java.net.Socket;

public class Server
{

        public Socket sc;
        private InputStream inputStream;
        private OutputStream outputStream;
        private String fileName;

         public Server(Socket clientSocket) throws IOException
         {
            this.sc = clientSocket;
            this.inputStream = clientSocket.getInputStream();
            this.outputStream = clientSocket.getOutputStream();
            this.fileName = "";
         }
    public void readInputStream() throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String s = br.readLine();
        String[] s1 = s.split(" ");
        String s2 = s1[1];
        if (s2.length()>0) {
            fileName = s2.substring(1, s2.length());
        }else fileName = "";

    }
    public void writeOutputStream() throws IOException
    {
        File file = new File(fileName);
        if (file.exists()) {
            String s = new String(Files.readAllBytes(Paths.get(fileName)));
            String response = "HTTP/1.1 200 OK\r\n" + "Content-Type:text/html\r\n\r\n" + s;
            outputStream.write(response.getBytes());
        } else {
            outputStream.write("<html><h2>404</h2></html>".getBytes());
            outputStream.flush();
        }
    }
}
