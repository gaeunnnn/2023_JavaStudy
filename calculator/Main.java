package org.example;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public class Main extends JFrame implements ActionListener {

    private JTextArea inputSpace;
    private String num = "";
    private ArrayList<String> equation = new ArrayList<>();
    private Stack<String> stack = new Stack<>();
    private JLabel backgroundLabel;
    private Random random;

    public Main() {
        random = new Random();
        setLayout(null);
        setTitle("장가은's 계산기");
        setSize(400, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        backgroundLabel = new JLabel();
        backgroundLabel.setBounds(10, 10, 370, 200);
        add(backgroundLabel);
        loadImage();// 이미지 로드

        setVisible(true);

        // 값 입력 창
        inputSpace = new JTextArea();
        inputSpace.setEditable(false);
        inputSpace.setBackground(Color.WHITE);
        inputSpace.setFont(new Font("Helvetica", Font.PLAIN, 50));
        inputSpace.setBounds(10, 10, 370, 200);
        inputSpace.setText("0");
        inputSpace.setLineWrap(true);
        inputSpace.setWrapStyleWord(true);
        inputSpace.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        JScrollPane scrollPane = new JScrollPane(inputSpace);
        scrollPane.setBounds(10, 10, 370, 200);

        // 버튼
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4, 10, 10));
        buttonPanel.setBounds(10, 220, 370, 340);
        String button_names[] = {"C", "÷", "+", "=", "Ran", "^", "(-)", ".", "7", "8", "9", "x", "4", "5", "6", "-", "1", "2", "3", "0"};
        JButton buttons[] = new JButton[button_names.length];
        setVisible(true);

        for (int i = 0; i < button_names.length; i++) {
            buttons[i] = new JButton(button_names[i]);
            buttons[i].setFont(new Font("Helvetica", Font.PLAIN, 20));

            if (button_names[i].equals("C")) {
                buttons[i].setBackground(Color.pink);
            } else if ((i >= 8 && i <= 10) || (i >= 12 && i <= 14) || (i >= 16 && i <= 18)) {
                buttons[i].setBackground(Color.white);
            } else if (i ==4) {
                buttons[i].setBackground(Color.orange);
            } else {
                buttons[i].setBackground(Color.lightGray);
            }

            buttons[i].setForeground(Color.black);
            buttons[i].setBorderPainted(false);

            buttonPanel.add(buttons[i]);
            buttons[i].setOpaque(true);

            buttons[i].addActionListener(this);
        }

        add(inputSpace);
        add(buttonPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Main();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String buttonText = e.getActionCommand();

        if (Character.isDigit(buttonText.charAt(0))) {
            num += buttonText;
            inputSpace.setText(num);
        }

        if (buttonText.equals("+") || buttonText.equals("-") || buttonText.equals("x") || buttonText.equals("÷") || buttonText.equals("^")) {
            if (!num.isEmpty()) {
                equation.add(num);
                num = "";
            }

            if (!stack.isEmpty() && isOperator(buttonText) && compareOperators(buttonText, stack.peek())) {
                // 연속해서 연산자가 입력된 경우 두 번째부터는 입력을 받지 않음
                return;
            }

            stack.push(buttonText);
        }

        if (buttonText.equals("C")) {
            equation.clear();
            num = "";
            inputSpace.setText("0");
            Play("/Users/jang-ga-eun/Documents/clear.wav");
        }

        if (buttonText.equals(".")) {
            if (num.isEmpty()) {
                num = "0.";
            } else if (!num.contains(".")) {
                num += ".";
            }
            inputSpace.setText(num);
        }

        if (buttonText.equals("Ran")) {
            if (!num.isEmpty()) {
                int maxNumber = Integer.parseInt(num);
                int randomNumber = random.nextInt(maxNumber) + 1;
                inputSpace.setText(String.valueOf(randomNumber));
                Play("/Users/jang-ga-eun/Documents/random.wav");
            }
        }

        if (buttonText.equals("(-)")) {
            if (num.isEmpty()) {
                num = "-";
                inputSpace.setText(num);
            }
        }

        if (buttonText.equals("=")) {
            if (!num.isEmpty()) {
                equation.add(num);
                num = "";
            }
            while (!stack.isEmpty()) {
                equation.add(stack.pop());
            }
            BigDecimal result = calculateResult();
            inputSpace.setText(result.toString());
            equation.clear();
            num = result.toString();
            inputSpace.revalidate();
            inputSpace.repaint();
            Play("/Users/jang-ga-eun/Documents/cal.wav");

        }
    }

    private boolean isOperator(String operator) {
        return operator.equals("+") || operator.equals("-") || operator.equals("x") || operator.equals("÷") || operator.equals("^");
    }

    private int getPrecedence(String operator) {
        if (operator.equals("^")) {
            return 3;
        } else if (operator.equals("x") || operator.equals("÷")) {
            return 2;
        } else if (operator.equals("+") || operator.equals("-")) {
            return 1;
        } else {
            return 0;
        }
    }

    private boolean compareOperators(String op1, String op2) {
        int precedence1 = getPrecedence(op1);
        int precedence2 = getPrecedence(op2);
        return precedence1 <= precedence2;
    }

    private BigDecimal calculateResult() {
        Stack<BigDecimal> stack = new Stack<>();
        for (String token : equation) {
            if (isOperator(token)) {
                BigDecimal operand2 = stack.pop();
                BigDecimal operand1 = stack.pop();
                BigDecimal result = performOperation(token, operand1, operand2);
                stack.push(result);
            } else {
                stack.push(new BigDecimal(token));
            }
        }
        return stack.pop();
    }

    private BigDecimal performOperation(String operator, BigDecimal operand1, BigDecimal operand2) {
        if (operator.equals("+")) {
            return operand1.add(operand2);
        } else if (operator.equals("-")) {
            return operand1.subtract(operand2);
        } else if (operator.equals("x")) {
            return operand1.multiply(operand2);
        } else if (operator.equals("÷")) {
            if (operand2.equals(BigDecimal.ZERO)) {
                inputSpace.setText("Invalid division by zero");
                return null;
            } else {
                // 나눗셈 연산 수정
                return operand1.divide(operand2, 10, RoundingMode.HALF_UP);
            }
        } else if (operator.equals("^")) {
            return operand1.pow(operand2.intValue());
        } else {
            return BigDecimal.ZERO;
        }
    }

    private void loadImage() {
        try {
            BufferedImage image = ImageIO.read(new File("/Users/jang-ga-eun/Documents/1234.jpg"));
            ImageIcon icon = new ImageIcon(image);
            backgroundLabel.setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Play(String fileName)
    {
        try
        {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(fileName));
            Clip clip = AudioSystem.getClip();
            clip.stop();
            clip.open(ais);
            clip.start();
        }
        catch (Exception ex)
        {
        }
    }
}