package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class OthelloGameServer {
    private static final int PORT = 8000;
    private static List<ObjectOutputStream> outputStreams = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server is running...");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Connected");

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            outputStreams.add(output);

            PlayerHandler playerHandler = new PlayerHandler(socket, output);
            playerHandler.start();
        }
    }

    private static class PlayerHandler extends Thread {
        private Socket socket;
        private ObjectInputStream input;
        private ObjectOutputStream output;

        public PlayerHandler(Socket socket, ObjectOutputStream output) {
            this.socket = socket;
            this.output = output;
        }

        public void run() {
            try {
                input = new ObjectInputStream(socket.getInputStream());

                while (true) {
                    int[][] boardState = (int[][]) input.readObject();
                    processBoardState(boardState);

                    for (ObjectOutputStream clientOutput : outputStreams) {
                        if (clientOutput != output) {
                            clientOutput.writeObject(boardState);
                            clientOutput.flush();
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Player disconnected: " + e);
            } finally {
                try {
                    outputStreams.remove(output);
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Couldn't close a socket, what's going on?");
                }
            }
        }

        private void processBoardState(int[][] boardState) {
            for (int row = 0; row < boardState.length; row++) {
                for (int col = 0; col < boardState[row].length; col++) {
                    System.out.print(boardState[row][col] + " ");
                }
                System.out.println();
            }
        }
    }
}

