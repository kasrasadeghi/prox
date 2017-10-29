package sam.prox;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.math.BigInteger;
import java.util.Random;
import sam.prox.Utils;

public class Start {
  public static void main(String[] args) {
    int numBits = 200;
    try {
      FileReader fr = new FileReader("/Users/samgunn/my_ws/java/sources/sam/prox/localinfo.txt");
      BufferedReader br = new BufferedReader(fr);
      BigInteger P = new BigInteger(br.readLine());
      BigInteger p = BigInteger.probablePrime(numBits, new Random());
      BigInteger q = BigInteger.probablePrime(numBits, new Random());
      BigInteger pq = p.multiply(q);
      BigInteger phipq = p.subtract(BigInteger.valueOf(1)).multiply(q.subtract(BigInteger.valueOf(1)));
      BigInteger e = BigInteger.probablePrime(numBits, new Random());
      BigInteger d = e.modInverse(phipq);
      System.out.println(P);

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
          // generate a message of type 0 using this pq
          String line = inputReader.nextLine();
          if (line.substring(0,line.indexOf(" ")).equals("friend")) {
            BigInteger tpq = new BigInteger(line.substring(line.indexOf(" ")+1));
            BigInteger[] msg = new BigInteger[6];
            msg[0] = e;
            msg[1] = pq.modPow(d,pq);
            msg[2] = tpq;
            msg[3] = BigInteger.valueOf(0);
            msg[4] = Utils.randRelPrime(numBits, P.subtract(BigInteger.valueOf(1)));
            BigInteger a = Utils.randRelPrime(numBits, P.subtract(BigInteger.valueOf(1)));
            msg[5] = msg[4].modPow(a,P);
            System.out.println("Hello");
          }
          fr.close();
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
