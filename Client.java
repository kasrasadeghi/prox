package com.kaz;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    String host = "18.216.152.27";
    int port = 5000;
    try {
      Socket socket = new Socket(host, port);
      PrintWriter socketWriter = new PrintWriter(socket.getOutputStream());
      Scanner socketReader = new Scanner(socket.getInputStream());

      new Thread(() -> {
        while(socketReader.hasNextLine()) {
          System.out.println(socketReader.nextLine());
        }
      }).start();

      Scanner inputReader = new Scanner(System.in);
      while (inputReader.hasNextLine()) {
        String line = inputReader.nextLine();
        socketWriter.println(line);
        socketWriter.flush();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
