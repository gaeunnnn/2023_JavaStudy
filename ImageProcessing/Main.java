package org.example;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.AlphaComposite;

public class Main extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main();
            }
        });
    }
    private BufferedImage originalImage;
    private BufferedImage processedImage;
    private JLabel originalImageLabel;
    private JLabel processedImageLabel;
    private Rectangle zoomRectangle; // 확대 사각형의 좌표와 크기를 저장할 변수
    private File compositeImageFile;
    private JSlider brightnessSlider;

    public Main() {
        super("Image Processing Program");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        originalImageLabel = new JLabel();  // 왼쪽 이미지
        processedImageLabel = new JLabel(); // 오른쪽 이미지

        // 메뉴바
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("파일");
        JMenuItem loadImageItem = new JMenuItem("이미지 불러오기");
        JMenuItem saveImageItem = new JMenuItem("이미지 저장하기");
        fileMenu.add(loadImageItem);
        fileMenu.add(saveImageItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // 흑백 처리 버튼
        JButton grayscaleButton = new JButton("흑백");
        grayscaleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (originalImage != null) {
                    removeRectangle();
                    processedImage = convertToGrayscale(originalImage);
                    processedImageLabel.setIcon(new ImageIcon(processedImage));
                }
            }
        });

        // 돋보기 버튼
        JButton zoomButton = new JButton("돋보기");
        zoomButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (originalImage != null) {
                    removeRectangle();
                    originalImageLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            int x = e.getX(); // 마우스 클릭 위치의 x 좌표
                            int y = e.getY(); // 마우스 클릭 위치의 y 좌표

                            // 이미지 내에서의 마우스 위치
                            int imgX = x;
                            int imgY = y;

                            // 이미지 내에서 클릭한 위치가 이미지 범위 내에 있는지 확인
                            if (imgX >= 0 && imgY >= 0 && imgX < originalImage.getWidth() && imgY < originalImage.getHeight()) {
                                // 사각형 좌표 설정
                                int zoomSize = 100; // 확대할 영역의 크기
                                zoomRectangle = new Rectangle(imgX - zoomSize / 2, imgY - zoomSize / 2, zoomSize, zoomSize);

                                // 오른쪽 이미지에 확대된 이미지 표시
                                processedImage = originalImage.getSubimage(zoomRectangle.x, zoomRectangle.y, zoomRectangle.width, zoomRectangle.height);
                                processedImageLabel.setIcon(new ImageIcon(processedImage.getScaledInstance(processedImageLabel.getWidth(), processedImageLabel.getHeight(), Image.SCALE_SMOOTH)));

                                // 빨간 사각형 테두리 그리기
                                BufferedImage tempImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                                Graphics2D g = tempImage.createGraphics();
                                g.drawImage(originalImage, 0, 0, null);
                                g.setColor(Color.RED);
                                g.setStroke(new BasicStroke(3));
                                g.drawRect(zoomRectangle.x, zoomRectangle.y, zoomRectangle.width, zoomRectangle.height);
                                g.dispose();
                                originalImageLabel.setIcon(new ImageIcon(tempImage));
                            }
                        }
                    });
                }
            }
        });

        JButton edgeButton = new JButton("Edge Detection");
        edgeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (originalImage != null) {
                    removeRectangle();
                    processedImage = applyEdgeDetection(originalImage);
                    processedImageLabel.setIcon(new ImageIcon(processedImage));
                }
            }
        });

        JButton compositeButton = new JButton("이미지 합성");
        compositeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (originalImage != null) {
                    JFileChooser fileChooser = new JFileChooser();
                    int result = fileChooser.showOpenDialog(Main.this);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        compositeImageFile = fileChooser.getSelectedFile();
                        removeRectangle();
                        try {
                            BufferedImage compositeImage = ImageIO.read(compositeImageFile);
                            processedImage = compositeImages(originalImage, compositeImage);
                            processedImageLabel.setIcon(new ImageIcon(processedImage));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        JButton contrastButton = new JButton("대비");
        contrastButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (originalImage != null) {
                    removeRectangle();
                    processedImage = adjustContrast(originalImage, 1.5f);
                    processedImageLabel.setIcon(new ImageIcon(processedImage));
                }
            }
        });

        JLabel label = new JLabel();
        label.setText("  밝기 : ");
        brightnessSlider = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
        brightnessSlider.setMajorTickSpacing(25);
        brightnessSlider.setPaintTicks(true);
        brightnessSlider.setPaintLabels(true);
        brightnessSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (originalImage != null) {
                    removeRectangle();
                    processedImage = adjustBrightness(originalImage, brightnessSlider.getValue());
                    processedImageLabel.setIcon(new ImageIcon(processedImage));
                }
            }
        });

        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 253, 208));
        buttonPanel.add(grayscaleButton);
        buttonPanel.add(zoomButton);
        buttonPanel.add(edgeButton);
        buttonPanel.add(compositeButton);
        buttonPanel.add(contrastButton);
        buttonPanel.add(label);
        buttonPanel.add(brightnessSlider);

        loadImageItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FileDialog fileDialog = new FileDialog(Main.this, "이미지 불러오기", FileDialog.LOAD);
                fileDialog.setFilenameFilter(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".gif");
                    }
                });
                fileDialog.setVisible(true);
                String filename = fileDialog.getFile();
                String directory = fileDialog.getDirectory();
                if (filename != null) {
                    File file = new File(directory, filename);
                    try {
                        originalImage    = ImageIO.read(file);
                        originalImageLabel.setIcon(new ImageIcon(originalImage));
                        // 프레임의 크기를 이미지의 크기에 맞게 변경
                        // 버튼 패널의 높이와 여백을 고려하여 프레임의 높이를 조정
                        int frameWidth = originalImage.getWidth() * 2 + getInsets().left + getInsets().right;
                        int frameHeight = originalImage.getHeight() + buttonPanel.getHeight() + getInsets().top + getInsets().bottom;
                        setSize(frameWidth, frameHeight);
                        setLocationRelativeTo(null); // 화면 중앙에 위치시킴

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        saveImageItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FileDialog fileDialog = new FileDialog(Main.this, "이미지 저장", FileDialog.SAVE);
                fileDialog.setFilenameFilter(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".jpg");
                    }
                });
                fileDialog.setVisible(true);
                String filename = fileDialog.getFile();
                String directory = fileDialog.getDirectory();
                if (filename != null) {
                    File file = new File(directory, filename);
                    try {
                        ImageIO.write(processedImage, "jpg", file); // processedImage를 JPG 형식으로 파일로 저장
                        JOptionPane.showMessageDialog(Main.this, "이미지가 성공적으로 저장되었습니다.");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(Main.this, "이미지 저장 중 오류가 발생했습니다.");
                        ex.printStackTrace();
                    }
                }
            }
        });

        JButton smoothingButton = new JButton("Blur");
        smoothingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (originalImage != null) {
                    removeRectangle();
                    processedImage = applyBlur(originalImage);
                    processedImageLabel.setIcon(new ImageIcon(processedImage));
                }
            }
        });

        buttonPanel.add(smoothingButton);

        // 화면 구성
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(new Color(255, 253, 208));
        imagePanel.setLayout(new GridLayout(1, 2));
        imagePanel.add(originalImageLabel);
        imagePanel.add(processedImageLabel);

        add(buttonPanel, BorderLayout.NORTH);
        add(imagePanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private BufferedImage convertToGrayscale(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage grayImage = new BufferedImage(
                width, height, BufferedImage.TYPE_BYTE_GRAY); // 전체 흑백 이미지 생성

        // 픽셀별로 순회하며 RGB 값을 평균하여 그레이스케일로 변환
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y)); // 현재 픽셀의 RGB 값을 가져옴
                int r = color.getRed(); // 빨강 추출
                int g = color.getGreen(); // 그린 추출
                int b = color.getBlue(); // 블루 추출
                int grayValue = (r + g + b) / 3; // 흑백 값을 계산
                int grayRgb = (grayValue << 16) | (grayValue << 8) | grayValue;
                grayImage.setRGB(x, y, grayRgb);
            }
        }
        return grayImage;
    }

    private BufferedImage applyEdgeDetection(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage edgeImage = new BufferedImage(
                width, height, BufferedImage.TYPE_BYTE_GRAY); // edge detection 적용된 이미지 생성

        int[][] sobelX = {             //가로 방향 엣지 감지 커널
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}
        };

        int[][] sobelY = {             //세로 방향 엣지 감지 커널
                {-1, -2, -1},
                {0, 0, 0},
                {1, 2, 1}
        };

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                float pixelX = 0.0f;
                float pixelY = 0.0f;

                for(int i = -1; i <= 1; i++){
                    for(int j = -1; j <= 1; j++){
                        int rgb = image.getRGB(x + i, y + j);
                        float val = (rgb >> 16) & 0xff;
                        pixelX += sobelX[i + 1][j + 1] * val;
                        pixelY += sobelY[i + 1][j + 1] * val;
                    }
                }

                int magnitude = (int) Math.sqrt((pixelX * pixelX) + (pixelY * pixelY));
                magnitude = Math.min(magnitude, 255);

                int newPixel = 0xff000000 | (magnitude << 16) | (magnitude << 8) | magnitude;
                edgeImage.setRGB(x, y, newPixel);
            }
        }
        return edgeImage;
    }

    private void removeRectangle() {
        // 사각형 테두리 제거
        if (zoomRectangle != null) {
            originalImageLabel.setIcon(new ImageIcon(originalImage));
            zoomRectangle = null;
        }
    }

    private BufferedImage compositeImages(BufferedImage img1, BufferedImage img2){
        int width = Math.max(img1.getWidth(),img2.getWidth());
        int height = Math.max(img1.getHeight(),img2.getHeight());

        BufferedImage compositeImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = compositeImage.createGraphics();

        g.drawImage(img1,0,0,null);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));

        g.drawImage(img2,0,0,null);

        g.dispose();

        return compositeImage;
    }

    private BufferedImage adjustContrast(BufferedImage image, float scaleFactor) {
        BufferedImage contrastImage = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB); // 대비 이미지 생성

        // 각 픽셀에 대하여
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y)); // 현재 픽셀의 RGB 값을 가져옴
                int r = color.getRed(); // 빨강 추출
                int g = color.getGreen(); // 그린 추출
                int b = color.getBlue(); // 블루 추출

                // RGB 값을 조정하여 대비를 증가
                r = (int) ((r - 128) * scaleFactor + 128);
                g = (int) ((g - 128) * scaleFactor + 128);
                b = (int) ((b - 128) * scaleFactor + 128);

                // 값을 0과 255 사이의 범위로 조정
                r = Math.max(0, Math.min(255, r));
                g = Math.max(0, Math.min(255, g));
                b = Math.max(0, Math.min(255, b));

                contrastImage.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }

        return contrastImage;
    }

    private BufferedImage adjustBrightness(BufferedImage image, int value) {
        BufferedImage adjustedImage = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        // 밝기 조절
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();

                // 밝기 값에 대해 조정
                r += value;
                g += value;
                b += value;

                // 값을 0과 255 사이의 범위로 조정
                r = Math.max(0, Math.min(255, r));
                g = Math.max(0, Math.min(255, g));
                b = Math.max(0, Math.min(255, b));

                adjustedImage.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }

        return adjustedImage;
    }

    private BufferedImage applyBlur(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage blurImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // smoothing이 적용된 이미지 생성

        // 각 픽셀에 대하여
        for (int y = 2; y < height - 2; y++) {
            for (int x = 2; x < width - 2; x++) {
                int sumR = 0, sumG = 0, sumB = 0;

                // 주변 픽셀을 순회하며 RGB 값을 더함
                for (int i = -2; i <= 2; i++) {
                    for (int j = -2; j <= 2; j++) {
                        if (x + i >= 0 && x + i < width && y + j >= 0 && y + i < height) {
                            Color color = new Color(image.getRGB(x + i, y + j));
                            sumR += color.getRed();
                            sumG += color.getGreen();
                            sumB += color.getBlue();
                        }
                    }
                }

                // 평균 RGB 값을 계산
                int avgR = sumR / 25;
                int avgG = sumG / 25;
                int avgB = sumB / 25;

                blurImage.setRGB(x, y, new Color(avgR, avgG, avgB).getRGB());
            }
        }
        return blurImage;
    }
}


