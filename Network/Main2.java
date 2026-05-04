package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static org.example.Main.isturned;
import static org.example.Main.s;

public class Main2 extends JFrame {
//    static boolean s=false;
    private boolean enable=true;
    private boolean isStart = true;
    private static final String SERVER_ADDRESS = "192.168.0.10";
    private static final int SERVER_PORT = 8000;

    private static Socket socket;
    private static ObjectOutputStream output;
    private static ObjectInputStream input;
    private static final int BOARD_SIZE = 8;
    private JPanelSquare[][] board;
    private Color currentPlayerColor;
    private JButton startButton;
    private JButton restartButton;
    private JLabel currentPlayerLabel;
    private JLabel blackCountLabel;
    private JLabel whiteCountLabel;
    private JButton onButton;
    private boolean gameOver;

    public Main2() {
        setTitle("Othello Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        mainPanel.setPreferredSize(new Dimension(500, 480));
        mainPanel.setBackground(new Color(254, 252, 204));
        JPanel northPadding = new JPanel();
        northPadding.setBackground(new Color(254, 252, 204));
        northPadding.setPreferredSize(new Dimension(0, 50));
        JPanel eastPadding = new JPanel();
        eastPadding.setPreferredSize(new Dimension(50, 0));
        eastPadding.setBackground(new Color(254, 252, 204));
        JPanel westPadding = new JPanel();
        westPadding.setPreferredSize(new Dimension(50, 0));
        westPadding.setBackground(new Color(254, 252, 204));

        blackCountLabel = new JLabel("검정색: 0");
        whiteCountLabel = new JLabel("흰색: 0");
        northPadding.setLayout(new BorderLayout());
        northPadding.add(blackCountLabel);
        northPadding.add(whiteCountLabel, BorderLayout.EAST);
        blackCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        whiteCountLabel.setFont(new Font("Arial", Font.BOLD, 14));

        add(northPadding, BorderLayout.NORTH);
        add(eastPadding, BorderLayout.EAST);
        add(westPadding, BorderLayout.WEST);

        currentPlayerLabel = new JLabel();
        updateCurrentPlayerLabel();

        board = new JPanelSquare[BOARD_SIZE][BOARD_SIZE];
        currentPlayerColor = Color.BLACK;

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = new JPanelSquare(row, col);
                mainPanel.add(board[row][col]);
            }
        }
        board[3][3].placeStone(Color.WHITE);
        board[3][4].placeStone(Color.BLACK);
        board[4][3].placeStone(Color.BLACK);
        board[4][4].placeStone(Color.WHITE);

        startButton = new JButton("시작");
        restartButton = new JButton("재시작");

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enableBoard(true);
                startButton.setEnabled(false);
                isStart = false;
            }
        });

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetBoard();
                sendBoardState();
            }
        });

        onButton = new JButton("On");
        onButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getActionCommand().equals("On")) {
                    onButton.setText("Off");
                    enable=false;
                }else if(e.getActionCommand().equals("Off")){
                    onButton.setText("On");
                    enable=true;
                }
                updateCanPlaceStones(enable);
            }
        });


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(restartButton);
        buttonPanel.add(currentPlayerLabel);
        buttonPanel.add(onButton);
        buttonPanel.setBackground(new Color(254, 252, 204));
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        enableBoard(false);
        connectToServer();
        sendBoardState();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    receiveBoardState();
                }
            }
        }).start();

        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(!s);
                if(s)
                    return;
                if (!startButton.isEnabled() ) {
                    int x = e.getX();
                    int y = e.getY();
                    Component clickedComponent = mainPanel.getComponentAt(x, y);


                    if (clickedComponent instanceof JPanelSquare) {
                        JPanelSquare clickedSquare = (JPanelSquare) clickedComponent;
                        int row = clickedSquare.getRow();
                        int col = clickedSquare.getCol();

                        if (canPlaceStone(row, col, currentPlayerColor)) {
                            board[row][col].placeStone(currentPlayerColor);
                            flipStones(row, col, currentPlayerColor);
                            currentPlayerColor = (currentPlayerColor == Color.BLACK) ? Color.WHITE : Color.BLACK;
                            if (!hasValidMove(currentPlayerColor)) {
                                currentPlayerColor = (currentPlayerColor == Color.BLACK) ? Color.WHITE : Color.BLACK;
                            }
                            updateCurrentPlayerLabel();
                            updateCanPlaceStones(enable);
                            sendBoardState();
                            s=!s;
                        }
                    }
                    checkGameOver();
                }
            }
        });
        currentPlayerLabel.setBackground(Color.WHITE);
        currentPlayerLabel.setForeground(Color.BLACK);
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private static void connectToServer() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendBoardState() {
        try {
            int[][] boardState = new int[BOARD_SIZE][BOARD_SIZE];
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    if (board[row][col].getColor() == Color.BLACK) {
                        boardState[row][col] = 1;
                    } else if (board[row][col].getColor() == Color.WHITE) {
                        boardState[row][col] = -1;
                    } else {
                        boardState[row][col] = 0;
                    }
                }
            }
            output.writeObject(boardState);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveBoardState() {
        s=!s;
        try {
            int[][] boardState = (int[][]) input.readObject();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    for (int row = 0; row < BOARD_SIZE; row++) {
                        for (int col = 0; col < BOARD_SIZE; col++) {
                            if (boardState[row][col] == 1) {
                                board[row][col].placeStone(Color.BLACK);
                            } else if (boardState[row][col] == -1) {
                                board[row][col].placeStone(Color.WHITE);
                            } else {
                                board[row][col].clearStone();
                            }
                        }
                    }

                    if(!isStart && !isturned) {
                        if (currentPlayerColor == Color.black)
                            currentPlayerColor = Color.white;
                        else currentPlayerColor = Color.black;
                        updateStoneCounts();
                        updateCanPlaceStones(enable);
                        updateCurrentPlayerLabel();
                    }
                }
            });
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateStoneCounts() {
        int blackCount = 0;
        int whiteCount = 0;

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Color stoneColor = board[row][col].getColor();

                if (stoneColor == Color.BLACK) {
                    blackCount++;
                } else if (stoneColor == Color.WHITE) {
                    whiteCount++;
                }
            }
        }

        blackCountLabel.setText("  검정색: " + blackCount +"개");
        whiteCountLabel.setText("흰색: " + whiteCount +"개  ");
    }

    private void updateCurrentPlayerLabel() {
        if (currentPlayerColor==null || currentPlayerColor == Color.BLACK) {
            currentPlayerLabel.setText("현재 차례: Black");
        } else {
            currentPlayerLabel.setText("현재 차례: White");
        }
    }

    private boolean hasValidMove(Color color) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (canPlaceStone(row, col, color)) {
                    return true;
                }
            }
        }
        if(!checkGameOver()){
            JOptionPane.showMessageDialog(this, "더 이상 둘 곳이 없습니다. 다시 차례가 돌아옵니다.");
            s=!s;
            isturned=true;
        }else isturned=false;
        return false;
    }

    private void updateCanPlaceStones(boolean enable) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                boolean canPlace = canPlaceStone(row, col, currentPlayerColor);
                board[row][col].setCanPlaceStoneHere(enable && canPlace);
            }
        }
    }

    private void enableBoard(boolean enable) {   // 게임 칸 활성화,비활성화
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if((row == 3 && col == 3) || (row == 3 && col == 4) ||
                        (row == 4 && col == 3) || (row == 4 && col == 4)) {
                    continue;
                }
                board[row][col].setEnabled(enable);
            }
        }
        restartButton.setEnabled(enable);
        updateCanPlaceStones(enable);
    }

    private void resetBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col].clearStone();
            }
        }
        gameOver = false;
        // 가운데 4개의 돌을 원래의 색상으로 복구
        board[3][3].placeStone(Color.WHITE);
        board[3][4].placeStone(Color.BLACK);
        board[4][3].placeStone(Color.BLACK);
        board[4][4].placeStone(Color.WHITE);

        currentPlayerColor = Color.BLACK;
        enableBoard(false);
        startButton.setEnabled(true);
        updateCurrentPlayerLabel();
    }
    private void flipStones(int row, int col, Color playerColor) {
        Color opponentColor = (playerColor == Color.BLACK) ? Color.WHITE : Color.BLACK; // 상대방이 화이트면 블랙
        int[] dr = {-1, -1, 0, 1, 1, 1, 0, -1}; //상,하,좌,우,대각선 방향으로 돌을 확인
        int[] dc = {0, 1, 1, 1, 0, -1, -1, -1};

        for (int i = 0; i < 8; i++) {
            int currentRow = row + dr[i];  //현재 확인하려는 방향에 대해 첫 번째 칸의 위치를 계산
            int currentCol = col + dc[i];
            boolean hasOpponentStonesInBetween = false; //현재 플레이어의 돌과 상대 플레이어의 돌 사이에 상대 플레이어의 돌이 있는지 확인

            while (currentRow >= 0 && currentRow < BOARD_SIZE &&
                    currentCol >= 0 && currentCol < BOARD_SIZE) {
                Color currentSquareColor = board[currentRow][currentCol].getColor(); //현재 확인하는 칸의 돌 색깔
                if (currentSquareColor == null) {
                    break; //현재 확인하는 칸에 돌이 없다면 이 방향으로 더 이상 확인할 필요가 없으므로 루프를 종료
                } else if (currentSquareColor == opponentColor) {
                    hasOpponentStonesInBetween = true;
                    //현재 확인하는 칸의 돌이 상대 플레이어의 돌이라면, 현재 플레이어의 돌과 상대 플레이어의 돌 사이에 상대 플레이어의 돌이 있다는 것을 표시
                } else if (currentSquareColor == playerColor) {
                    if (hasOpponentStonesInBetween) {
                        int flipRow = row;
                        int flipCol = col;
                        while (flipRow != currentRow || flipCol != currentCol) {
                            board[flipRow][flipCol].placeStone(playerColor);
                            flipRow += dr[i];
                            flipCol += dc[i];
                        }
                    }
                    break;
                }
                currentRow += dr[i];
                currentCol += dc[i];
                updateStoneCounts();
            }
        }
    }

    private boolean canPlaceStone(int row, int col, Color playerColor) {
        if (board[row][col].getColor() != null) {
            return false;
        }
        Color opponentColor = (playerColor == Color.BLACK) ? Color.WHITE : Color.BLACK;
        int[] dr = {-1, -1, 0, 1, 1, 1, 0, -1};
        int[] dc = {0, 1, 1, 1, 0, -1, -1, -1};

        for (int i = 0; i < 8; i++) {
            int currentRow = row + dr[i];
            int currentCol = col + dc[i];
            boolean hasOpponentStonesInBetween = false;

            while (currentRow >= 0 && currentRow < BOARD_SIZE &&
                    currentCol >= 0 && currentCol < BOARD_SIZE) {
                Color currentSquareColor = board[currentRow][currentCol].getColor();

                if (currentSquareColor == null) {
                    break;
                } else if (currentSquareColor == opponentColor) {
                    hasOpponentStonesInBetween = true;
                } else if (currentSquareColor == playerColor) {
                    if (hasOpponentStonesInBetween) {
                        return true;
                    } else {
                        break;
                    }
                }

                currentRow += dr[i];
                currentCol += dc[i];
            }
        }
        return false;
    }

    private boolean checkGameOver() {
        if (gameOver) {
            return true;
        }
        int blackCount = 0, whiteCount = 0, emptyCount = 0;
        boolean blackCanMove = false, whiteCanMove = false;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Color color = board[row][col].getColor();
                if (color == null) {
                    emptyCount++;
                    if (!blackCanMove && canPlaceStone(row, col, Color.BLACK)) {
                        blackCanMove = true;
                    }
                    if (!whiteCanMove && canPlaceStone(row, col, Color.WHITE)) {
                        whiteCanMove = true;
                    }
                } else if (color == Color.BLACK) {
                    blackCount++;
                } else {
                    whiteCount++;
                }
            }
        }
        if (blackCount == 0 || whiteCount == 0 || emptyCount == 0 || (!blackCanMove && !whiteCanMove)) {
            gameOver = true;
            String message;
            if (blackCount > whiteCount) {
                message = "검정색 승!";
            } else if (whiteCount > blackCount) {
                message = "흰색 승!";
            } else {
                message = "무승부!";
            }
            JOptionPane.showMessageDialog(this, message);
            restartButton.setEnabled(true);
        }
        return gameOver;
    }


    public static void main(String[] args)  throws IOException {
        connectToServer();
        SwingUtilities.invokeLater(Main::new);
    }

    private class JPanelSquare extends JPanel {
        private boolean canPlaceStoneHere;
        private final int row;
        private final int col;
        private Color stoneColor;

        public JPanelSquare(int row, int col) {
            this.row = row;
            this.col = col;
            stoneColor = null;

            setPreferredSize(new Dimension(50, 50)); // 각 칸의 크기
        }

        public int getRow() {  //각 칸의 위치 정보

            return row;
        }

        public int getCol() {

            return col;
        }

        public boolean isEmpty() {
            return stoneColor == null;
        }

        public void placeStone(Color color) {
            stoneColor = color;
            repaint();
            updateStoneCounts();
        }

        public Color getColor() {

            return stoneColor;
        }

        public void clearStone() {
            stoneColor = null;
            repaint();
        }

        public void setCanPlaceStoneHere(boolean canPlaceStone) {
            canPlaceStoneHere = canPlaceStone;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (canPlaceStoneHere) {
                g.setColor(Color.RED);
                g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            if (stoneColor != null) {
                g.setColor(Color.BLACK); // 테두리 색상
                g.drawOval(5, 5, getWidth() - 10, getHeight() - 10); // 테두리 그리기
                g.setColor(stoneColor);
                g.fillOval(5, 5, getWidth() - 10, getHeight() - 10);
            }
        }
    }
}


