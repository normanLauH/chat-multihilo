package mx.ucol.server;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class App {
    private static class KeyboardReader implements Runnable {
        DataOutputStream outputStream;
        String inputData = "";
        Scanner keyboardInput = new Scanner(System.in);

        private KeyboardReader(DataOutputStream outputStream) {
            this.outputStream = outputStream;
        }

        public void run() {
            try {
                while (inputData != null) {
                    System.out.print("Say something: ");
                    inputData = keyboardInput.nextLine();
                    outputStream.writeUTF(inputData);

                    if(inputData.equals("bye")) {
                        break;
                    }
                }

                keyboardInput.close();
            } catch (IOException e) {
                System.err.print(e);
            }
        }
    }

    private static class InputReader implements Runnable {
        DataInputStream inputStream;
        String inputData = "";

        private InputReader(DataInputStream inputStream) {
            this.inputStream = inputStream;
        }

        public void run() {
            try {
                while((inputData = inputStream.readUTF()) != null) {
                    System.out.println("\nReceived: " + inputData);

                    if(inputData.equals("bye")) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.print(e);
            }
        }
    }

    public static void main(String[] args) {
        ServerSocket serverSocket;
        Socket socket;
        int port = 3000;
        DataInputStream inputStream;
        DataOutputStream outputStream;

        try {
            serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            Thread inputReader = new Thread(new InputReader(inputStream));
            Thread keyboardReader = new Thread(new KeyboardReader(outputStream));
            inputReader.start();
            keyboardReader.start();

            inputReader.join();
            keyboardReader.join();

            serverSocket.close();
        } catch (IOException e) {
            System.err.print(e);
        } catch (InterruptedException e) {
            System.err.print(e);
        }
    }
}
