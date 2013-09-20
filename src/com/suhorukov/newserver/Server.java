package com.suhorukov.newserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Date;


import com.suhorukov.newserver.util.HTMLGenerator.HTMLGenerator;


public class Server
{
    File serverRoot;
    ServerSocket serverSocket;

    public Server(int port, File root){
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        serverRoot = root;
    }

    public void listen(){
        Socket client = null;
        try {
            client = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

            return;
        }

        if (client != null){
        //Поток для работы с клиентом

        final Socket finalClient = client;
        new Thread(new Runnable() {
            @Override
            public void run() {

                //Socket client = null;
                OutputStream sockOut = null;
                InputStream sockIn = null;
                try {
                    //client = serverSocket.accept();
                    sockOut = finalClient.getOutputStream();
                    sockIn = finalClient.getInputStream();
                    int ch;
                    StringBuilder requestBuilder = new StringBuilder();

                    //Читаем запрос клиента из сокета
                    while ((ch = sockIn.read()) != -1 ){
                        requestBuilder.append((char)ch);
                        if (requestBuilder.toString().contains("\r\n\r\n")){
                            break;
                        }
                    }

                    //запрос в виде строки
                    String request = requestBuilder.toString().trim();

                    String path = request.split(" ")[1];
                    path = URLDecoder.decode(path, "UTF-8");
                    File resource = new File(serverRoot.toString() + path);
                    String headers = generateHeaders(resource);

                    //Запросы, поддерживаемые сервером
                    switch (request.substring(0, request.indexOf(" "))){

                        case "GET":
                            if (resource.isFile()){
                                FileInputStream fileReader = new FileInputStream(resource);

                                byte[] buff = new byte[4096];
                                sockOut.write(headers.getBytes());
                                while ((ch = fileReader.read(buff)) != -1){
                                    sockOut.write(buff);
                                }
                                break;
                            }
                            HTMLGenerator gen = new HTMLGenerator(resource);
                            String page = gen.generateHTMLList();

                            sockOut.write(headers.getBytes());
                            sockOut.write(page.getBytes("UTF-8"));
                            break;
                        case "HEAD":
                            sockOut.write(headers.getBytes());
                            break;
                        default:
                            System.out.println("Тип запроса не поддерживается.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } finally {
                    if (finalClient != null){
                        try {
                            sockOut.close();
                            sockIn.close();
                            finalClient.close();

                        } catch (IOException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                }
            }
        }).start();

        }
    }

    private String generateHeaders(File resource){
        StringBuilder headersBuilder = new StringBuilder();

        if (resource.exists()){
            headersBuilder.append("HTTP/1.1 200 OK\r\n");
        } else {
            headersBuilder.append("HTTP/1.1 404 Not Found\r\n\r\n");
            return "";
        }

        headersBuilder.append("Date: ").append(new Date().toString()).append("\r\n");
        headersBuilder.append("Cache-Control: no-cache").append("\r\n");
        if (resource.isFile()){
            headersBuilder.append("Content-Length: ").append(resource.length()).append("\r\n");
            headersBuilder.append("filename=\"").append(resource.getName()).append("\"\r\n");
        }
        if (resource.isDirectory()){
            headersBuilder.append("Content-Type: text/html").append("\r\n");
        }
        headersBuilder.append("Server: JavaServ").append("\r\n");
        headersBuilder.append("Last-Modified: ").append(new Date(resource.lastModified()).toString()).append("\r\n");
        headersBuilder.append("Connection: close").append("\r\n");

        headersBuilder.append("\r\n");
        return headersBuilder.toString();
    }
}
