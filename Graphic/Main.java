package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Stack;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
public class Main {
    JFrame frame;
    DrawPanel drawPanel;
    JButton drawLineButton, drawCircleButton, drawRectangleButton, drawPolylineButton, drawFreeformButton, drawSprayButton;
    JButton chooseColorButton, chooseStrokeButton, undoButton, redoButton;
    JButton drawEraserButton, drawClearButton;
    Color currentColor = Color.BLACK;
    float currentStroke = 1.0f;
    String currentText = "";
    String patternText = "";
    float dash[] = {15,5f};
    DrawType currentDrawType = null;
    int i;
    int n=0;
    Stack<BufferedImage> undoStack = new Stack<>();
    Stack<BufferedImage> redoStack = new Stack<>();

    enum DrawType { LINE, CIRCLE, RECTANGLE, POLYLINE, FREEFORM, SPRAY, ERASER, CLEAR, TEXT, PATTERN, MOSAIC}

    public static void main(
            String[] args) {
        new Main().start();
    }

    public void start() {
          frame = new JFrame("그림판");
          frame.setSize(1200, 700);
          JMenuBar menu = new JMenuBar();
          frame.setJMenuBar(menu);
        JMenu fileMenu = new JMenu("파일");
        JMenuItem saveMenuItem = new JMenuItem("저장");
        JMenuItem openMenuItem = new JMenuItem("불러오기");

        saveMenuItem.addActionListener(e -> drawPanel.saveImage());
        openMenuItem.addActionListener(e -> drawPanel.openImage());

        fileMenu.add(saveMenuItem);
        fileMenu.add(openMenuItem);
        menu.add(fileMenu);

        JMenu attributeMenu = new JMenu("스타일");
        JMenuItem textMenuItem = new JMenuItem(("텍스트"));
        JMenuItem LineMenuItem = new JMenuItem(("실선"));
        JMenuItem DotLineMenuItem = new JMenuItem(("점선"));
        JMenuItem PatternMenuItem = new JMenuItem(("패턴"));
        JMenuItem GradientMenuItem = new JMenuItem(("그라데이션"));

        textMenuItem.addActionListener(e -> {
            currentDrawType = DrawType.TEXT;
            currentText = JOptionPane.showInputDialog("Enter the text:");
        });

        LineMenuItem.addActionListener((e -> n=0));

        DotLineMenuItem.addActionListener((e -> n=1));

        PatternMenuItem.addActionListener(e -> {
            currentDrawType = DrawType.PATTERN;
            patternText = JOptionPane.showInputDialog("Enter the pattern:");
        });

        GradientMenuItem.addActionListener(e -> {
            drawPanel.setGradientColor1(JColorChooser.showDialog(drawPanel, "Choose a color", Color.BLACK));
            drawPanel.setGradientColor2(JColorChooser.showDialog(drawPanel, "Choose another color", Color.BLACK));
            drawPanel.setGradientMode(true);
        });

        attributeMenu.add(textMenuItem);
        attributeMenu.add(LineMenuItem);
        attributeMenu.add(DotLineMenuItem);
        attributeMenu.add(PatternMenuItem);
        attributeMenu.add(GradientMenuItem);
        menu.add(attributeMenu);

        menu.setVisible(true);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        drawPanel = new DrawPanel();
        contentPane.add(drawPanel, BorderLayout.CENTER);
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(255, 253, 208));
        controlPanel.setLayout(new GridLayout(2, 6));

        drawLineButton = new JButton("Draw Line");
        drawLineButton.addActionListener(e -> currentDrawType = DrawType.LINE);

        drawCircleButton = new JButton("Draw Circle");
        drawCircleButton.addActionListener(e -> currentDrawType = DrawType.CIRCLE);

        drawRectangleButton = new JButton("Draw Rectangle");
        drawRectangleButton.addActionListener(e -> currentDrawType = DrawType.RECTANGLE);

        drawPolylineButton = new JButton("Draw Polyline");
        i = 0;
        drawPolylineButton.addActionListener(e -> currentDrawType = DrawType.POLYLINE);

        drawFreeformButton = new JButton("Draw Freeform");
        drawFreeformButton.addActionListener(e -> currentDrawType = DrawType.FREEFORM);

        drawSprayButton = new JButton(("Draw Spray"));
        drawSprayButton.addActionListener((e-> currentDrawType= DrawType.SPRAY));

        chooseColorButton = new JButton(("Choose a color"));
        chooseColorButton.addActionListener((e -> currentColor = JColorChooser.showDialog(frame, "Choose a color", currentColor)));

        chooseStrokeButton = new JButton(("Choose Stroke"));
        chooseStrokeButton.addActionListener((e -> currentStroke = Float.parseFloat(JOptionPane.showInputDialog("Enter Stroke"))));

        drawEraserButton = new JButton("Eraser");
        drawEraserButton.addActionListener(e -> currentDrawType = DrawType.ERASER);

        drawClearButton = new JButton("Clear");
        drawClearButton.addActionListener(e -> {
            currentDrawType = DrawType.CLEAR;
            drawPanel.clearImage();
        });

        undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> {
            if (!undoStack.isEmpty()) {
                redoStack.push(drawPanel.image);
                drawPanel.image = undoStack.pop();
                drawPanel.repaint();
            }
        });

        redoButton = new JButton("Redo");
        redoButton.addActionListener(e -> {
            if (!redoStack.isEmpty()) {
                undoStack.push(drawPanel.image);
                drawPanel.image = redoStack.pop();
                drawPanel.repaint();
            }
        });

        controlPanel.add(drawLineButton);
        controlPanel.add(drawCircleButton);
        controlPanel.add(drawRectangleButton);
        controlPanel.add(drawPolylineButton);
        controlPanel.add(drawFreeformButton);
        controlPanel.add(drawSprayButton);

        controlPanel.add(chooseColorButton);
        controlPanel.add(chooseStrokeButton);
        controlPanel.add(undoButton);
        controlPanel.add(redoButton);
        controlPanel.add(drawEraserButton);
        controlPanel.add(drawClearButton);

        contentPane.add(controlPanel, BorderLayout.NORTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    class DrawPanel extends JPanel {
        private Point start;
        private Point lastPoint;
        private BufferedImage image;
        private Point points[] = new Point[100000];
        private int pointCount = 0;
        private Color gradientColor1 = null;
        private Color gradientColor2 = null;
        private boolean isGradientMode = false;
        public void setGradientColor1(Color color) {
            this.gradientColor1 = color;
        }
        public void setGradientColor2(Color color) {
            this.gradientColor2 = color;
        }
        public void setGradientMode(boolean isGradientMode) {
            this.isGradientMode = isGradientMode;
        }

        public DrawPanel() {
            image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    undoStack.push(copyImage(image));
                    start = new Point(e.getX(), e.getY());
                    lastPoint = start;
                }

                public void mouseReleased(MouseEvent e) {
                    Graphics2D g2 = (Graphics2D) image.getGraphics();
                    g2.setColor(currentColor);
                    g2.setStroke(new BasicStroke(currentStroke));
                    if (currentDrawType == DrawType.TEXT) {
                        g2.setFont(new Font("Time", Font.PLAIN,(int)currentStroke));
                        g2.drawString(currentText, e.getX(), e.getY());
                    }
                    if (currentDrawType == DrawType.PATTERN) {
                        g2.setFont(new Font("Time", Font.PLAIN,(int)currentStroke));
                        g2.drawString(patternText, e.getX(), e.getY());
                    }
                    if(n==1)
                        g2.setStroke(new BasicStroke(currentStroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1, dash, 0));
                    if(isGradientMode) {
                        g2.setPaint(new GradientPaint(start.x, start.y, gradientColor1, e.getX(), e.getY(), gradientColor2)); //시작점 x,y, 시작점 사용할 컬러,끝점 x,y,끝점 사용할 컬러
                    } else {
                        g2.setColor(currentColor);
                    }switch (currentDrawType) {
                            case LINE:
                                g2.drawLine(start.x, start.y, e.getX(), e.getY());
                                break;
                            case CIRCLE:
                                g2.drawOval(Math.min(start.x, e.getX()), Math.min(start.y, e.getY()), Math.abs(start.x - e.getX()), Math.abs(start.y - e.getY()));
                                break;
                            case RECTANGLE:
                                g2.drawRect(Math.min(start.x, e.getX()), Math.min(start.y, e.getY()), Math.abs(start.x - e.getX()), Math.abs(start.y - e.getY()));
                                break;
                            case POLYLINE:
                                if (pointCount < 1) {
                                    points[pointCount] = new Point(e.getX(), e.getY());
                                    pointCount++;
                                } else {
                                    g2.drawLine(points[pointCount - 1].x, points[pointCount - 1].y, e.getX(), e.getY());
                                    points[pointCount] = new Point(e.getX(), e.getY());
                                    pointCount++;
                                }
                                break;
                            case FREEFORM:
                                g2.drawLine(start.x, start.y, e.getX(), e.getY());
                                break;
                            case SPRAY:
                                spray(g2, e.getX(), e.getY());
                                break;
                        }
                        g2.dispose();
                        repaint();
                    }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    if (currentDrawType == DrawType.PATTERN) {
                        Graphics2D g2 = (Graphics2D) image.getGraphics();
                        g2.setFont(new Font("Time", Font.PLAIN,(int)currentStroke));
                        g2.setColor(currentColor);
                        g2.setStroke(new BasicStroke(currentStroke));
                        g2.drawString(patternText, e.getX(), e.getY());
                        g2.dispose();
                        repaint();
                    }
                    if (isGradientMode && (currentDrawType == DrawType.POLYLINE || currentDrawType == DrawType.FREEFORM)) {
                        Graphics2D g2 = (Graphics2D) image.getGraphics();
                        g2.setColor(currentColor);
                        g2.setStroke(new BasicStroke(currentStroke));

                        GradientPaint gp = new GradientPaint(0, 0, gradientColor1, getWidth(), getHeight(), gradientColor2);
                        g2.setPaint(gp);

                        if (currentDrawType == DrawType.FREEFORM) {
                            g2.drawLine(start.x, start.y, e.getX(), e.getY());
                            start = new Point(e.getX(), e.getY());
                        }
                        g2.dispose();
                        repaint();
                    }
                    if (currentDrawType == DrawType.POLYLINE || currentDrawType == DrawType.FREEFORM || currentDrawType == DrawType.ERASER || currentDrawType == DrawType.SPRAY) {
                        Graphics2D g2 = (Graphics2D) image.getGraphics();
                        g2.setColor(currentColor);
                        g2.setStroke(new BasicStroke(currentStroke));
                        if (currentDrawType == DrawType.FREEFORM) {
                            g2.drawLine(start.x, start.y, e.getX(), e.getY());
                            start = new Point(e.getX(), e.getY());
                        }
                        if (currentDrawType == DrawType.ERASER) {
                            g2.setColor(Color.WHITE);
                            g2.setStroke(new BasicStroke(currentStroke));
                            g2.drawLine(start.x, start.y, e.getX(), e.getY());
                            start = new Point(e.getX(), e.getY());
                            g2.dispose();
                            repaint();
                        } else if (currentDrawType == DrawType.SPRAY) {
                            spray(g2, e.getX(), e.getY());
                        }
                        g2.dispose();
                        repaint();
                    }
                }
            });
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
            if (image == null || image.getWidth() != getWidth() || image.getHeight() != getHeight()) {
                image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            }
            if (currentDrawType != DrawType.CLEAR) {
                g.drawImage(image, 0, 0, this);
            }
        }

        private BufferedImage copyImage(BufferedImage source) {
            BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
            Graphics g = copy.getGraphics();
            g.drawImage(source, 0, 0, null);
            g.dispose();
            return copy;
        }

        private void spray(Graphics2D g2, int x, int y) {
            int sprayRadius = (int)currentStroke; // 최대 반경
            int density = 100; // 점의 개수

            // density 횟수만큼 반복하면서 스프레이 효과
            for (int i = 0; i < density; i++) {
                double angle = Math.random() * 2 * Math.PI; //각도 생성
                double distance = Math.random() * sprayRadius; //거리 생성

                // 생성된 각도와 거리를 사용하여 점의 오프셋
                int offsetX = (int) (distance * Math.cos(angle));
                int offsetY = (int) (distance * Math.sin(angle));

                // 원래 위치 (x, y)에 오프셋을 더하여 새 위치를 결정
                int newX = x + offsetX;
                int newY = y + offsetY;
                g2.fillOval(newX,newY,2,2);
            }
        }

        public void clearImage() {
            Graphics2D g2 = (Graphics2D) image.getGraphics();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, image.getWidth(), image.getHeight());
            undoStack.push(copyImage(image));
            redoStack.clear();
            g2.dispose();
            repaint();
        }

        private void saveImage() {
            JFileChooser fileChooser = new JFileChooser(); //파일창 생성
            FileNameExtensionFilter filter = new FileNameExtensionFilter("이미지 파일", "png", "jpg", "jpeg"); //파일 선택 대화상자에 표시될 파일 필터
            fileChooser.setFileFilter(filter); // 이미지만 받음
            int result = fileChooser.showSaveDialog(frame); //선택한 파일의 경로와 이름을 반환
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile(); // 선택한 파일 객체를 file 변수에 할당
                try {
                    ImageIO.write(image, "png", file);
                    JOptionPane.showMessageDialog(frame, "그림이 성공적으로 저장되었습니다.");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, "그림을 저장하는 도중 오류가 발생했습니다.");
                    e.printStackTrace();
                }
            }
        }

        private void openImage() {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("이미지 파일", "png", "jpg", "jpeg");
            fileChooser.setFileFilter(filter);
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    BufferedImage loadedImage = ImageIO.read(file);
                    BufferedImage resizedImage = new BufferedImage(drawPanel.getWidth(), drawPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = resizedImage.createGraphics();
                    g.drawImage(loadedImage, 0, 0, drawPanel.getWidth(), drawPanel.getHeight(), null);
                    g.dispose();
                    drawPanel.image = resizedImage;
                    drawPanel.repaint();
                    JOptionPane.showMessageDialog(frame, "그림이 성공적으로 불러와졌습니다.");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, "그림을 불러오는 도중 오류가 발생했습니다.");
                    e.printStackTrace();
                }
            }
        }
    }
}