package org.example;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class Main extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public Main() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Login System");
        setSize(300, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        getContentPane().add(panel);
        panel.setLayout(null);

        JLabel lblUsername = new JLabel("아이디 :");
        lblUsername.setBounds(30, 30, 80, 25);
        panel.add(lblUsername);

        usernameField = new JTextField();
        usernameField.setBounds(120, 30, 150, 25);
        panel.add(usernameField);
        usernameField.setColumns(10);

        JLabel lblPassword = new JLabel("비밀번호 :");
        lblPassword.setBounds(30, 70, 80, 25);
        panel.add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setBounds(120, 70, 150, 25);
        panel.add(passwordField);

        JButton btnLogin = new JButton("로그인 ");
        btnLogin.setBounds(30, 120, 100, 25);
        panel.add(btnLogin);
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                try (Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/DB_23summer?serverTimezone=UTC", "root", "wkdrkdms01!")) {

                    String query = "SELECT * FROM users WHERE ID = ? AND PW = ?";
                    String queryadmin="SELECT * FROM admin WHERE ID = ? AND PW = ?";
                    PreparedStatement ad = conn.prepareStatement(queryadmin);
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    ad.setString(1, username);
                    ad.setString(2, password);
                    ResultSet rs = pstmt.executeQuery();
                    ResultSet admin = ad.executeQuery();
                    if (rs.next() || admin.next()) {
                        dispose();
                        showProfileOptions(username,password);
                    } else {
                        JOptionPane.showMessageDialog(null, "존재하지 않는 아이디, 비밀번호입니다 !    ");
                    }
                } catch (SQLException ex) {
                    System.out.println("SQLException: " + ex.getMessage());
                }
            }
        });

        JButton btnRegister = new JButton("회원가입 ");
        btnRegister.setBounds(170, 120, 100, 25);
        panel.add(btnRegister);
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RegistrationForm regForm = new RegistrationForm();
                regForm.setVisible(true);
            }
        });
    }

    private void showProfileOptions(String username,String password) {
        if (username.equals("gaeun") && password.equals("12345")) {
            AdminMode adminMode = new AdminMode();
            adminMode.setVisible(true);
        } else {
            ProfileOptions profileOptions = new ProfileOptions(username);
            profileOptions.setVisible(true);
        }
    }


    public static void main(String[] args) {
        Main loginSystem = new Main();
        loginSystem.setVisible(true);
    }
}

class RegistrationForm extends JFrame {
    private JTextField usernameField;
    private JTextField nameField;
    private JPasswordField password12Field;
    private JPasswordField confirmPasswordField;
    private JSpinner ageSpinner;
    private JSpinner dateSpinner;
    private JTextField phoneField;
    private JButton checkButton;
    private JButton pwButton;
    private JRadioButton maleRadioButton;
    private JRadioButton femaleRadioButton;
    private JButton btnSubmit;


    public RegistrationForm() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Registration Form");
        setSize(330, 450);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        getContentPane().add(panel);
        panel.setLayout(null);

        JLabel name = new JLabel("이름 :");
        name.setBounds(35, 30, 80, 25);
        panel.add(name);

        nameField = new JTextField();
        nameField.setBounds(120, 30, 150, 25);
        panel.add(nameField);


        JLabel lblUsername = new JLabel("아이디 :");
        lblUsername.setBounds(35, 60, 80, 25);
        panel.add(lblUsername);

        usernameField = new JTextField();
        usernameField.setBounds(120, 60, 150, 25);
        panel.add(usernameField);

        JLabel idcon = new JLabel("아이디 중복 확인 필수! ");
        idcon.setForeground(Color.red);
        idcon.setBounds(35, 90, 150, 25);
        panel.add(idcon);

        checkButton = new JButton("중복 확인 ");
        checkButton.setBounds(170, 90, 100, 25);
        panel.add(checkButton);


        JLabel pw = new JLabel("비밀번호 :");
        pw.setBounds(35, 120, 80, 20);
        panel.add(pw);

        password12Field = new JPasswordField();
        password12Field.setBounds(120, 120, 150, 25);
        panel.add(password12Field);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(120, 120, 150, 25);
        panel.add(passwordField);

        JLabel reconfirm = new JLabel("재확인 : ");
        reconfirm.setBounds(35, 150, 110, 25);
        panel.add(reconfirm);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(120, 150, 150, 25);
        panel.add(confirmPasswordField);

        JLabel pw1 = new JLabel("비밀번호 5자리 이상 필수!  ");
        pw1.setForeground(Color.red);
        pw1.setBounds(35, 180, 200, 25);
        panel.add(pw1);

        pwButton = new JButton("확인 ");
        pwButton.setBounds(190, 180, 80, 25);
        panel.add(pwButton);

        JLabel age = new JLabel("나이 :");
        age.setBounds(35, 210, 80, 25);
        panel.add(age);

        ageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        ageSpinner.setBounds(120, 210, 150, 25);
        panel.add(ageSpinner);

        ButtonGroup genderGroup = new ButtonGroup();

        JLabel gender = new JLabel("성별 :");
        gender.setBounds(35, 240, 80, 25);
        panel.add(gender);

        maleRadioButton = new JRadioButton("남자 ");
        maleRadioButton.setBounds(120, 240, 70, 25);
        panel.add(maleRadioButton);
        genderGroup.add(maleRadioButton);

        femaleRadioButton = new JRadioButton("여자 ");
        femaleRadioButton.setBounds(180, 240, 90, 25);
        panel.add(femaleRadioButton);
        genderGroup.add(femaleRadioButton);

        JLabel birth = new JLabel("생일 :");
        birth.setBounds(35, 270, 80, 25);
        panel.add(birth);

        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setBounds(120, 270, 150, 25);
        panel.add(dateSpinner);

        JLabel phone = new JLabel("전화번호 :");
        phone.setBounds(35, 300, 80, 25);
        panel.add(phone);

        phoneField = new JTextField();
        phoneField.setBounds(120, 300, 150, 25);
        panel.add(phoneField);

        JLabel job = new JLabel("직업 :");
        job.setBounds(35, 330, 80, 25);
        panel.add(job);

        String[] occupations = { "학생", "대학원생", "직장인", "기타" };
        JComboBox<String> occupationComboBox = new JComboBox<>(occupations);
        occupationComboBox.setBounds(120, 330, 150, 25);
        panel.add(occupationComboBox);

        btnSubmit = new JButton("가입하기 ");
        btnSubmit.setBounds(110, 380, 100, 25);
        panel.add(btnSubmit);

        pwButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String confirmPassword = new String(confirmPasswordField.getPassword());
                if(password12Field.getText().length()>=5) {
                    pw1.setText("확인 완료 ");
                    pw1.setForeground(Color.blue);
                }else {
                    pw1.setText("5글자 이상 필요! ");
                    pw1.setForeground(Color.red);
                }
                if (!password12Field.getText().equals(confirmPassword)) {
                    pw1.setText("비밀번호가 일치하지 않음! ");
                    pw1.setForeground(Color.red);
                    return;
                }
            }
        });

        checkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();

                try (Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/DB_23summer?serverTimezone=UTC", "root", "wkdrkdms01!")) {

                    String query = "SELECT * FROM users WHERE ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, username);
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        idcon.setText("사용중인 아이디 입니다! ");
                        idcon.setForeground(Color.red);
                    } else {
                        idcon.setText("사용 가능한 아이디 입니다! ");
                        idcon.setForeground(Color.blue);
                    }
                } catch (SQLException ex) {
                    System.out.println("SQLException: " + ex.getMessage());
                }
            }
        });

        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (maleRadioButton.isSelected() == femaleRadioButton.isSelected()) {
                    JOptionPane.showMessageDialog(null, "성별을 선택해주세요 !");
                    return;
                }
                String name = nameField.getText();
                String username = usernameField.getText();
                String password = new String(password12Field.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                int age = (int)ageSpinner.getValue();
                String gender = maleRadioButton.isSelected() ? "Male" : "Female";
                Date birthday = ((SpinnerDateModel)dateSpinner.getModel()).getDate();
                String phone = phoneField.getText();
                String occupation = (String) occupationComboBox.getSelectedItem();

                try (Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/DB_23summer?serverTimezone=UTC", "root", "wkdrkdms01!")) {

                    String query = "INSERT INTO users (Name, ID, PW, Age, Gender, Birthday, Phone, Occupation) VALUES (?,?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, name);
                    pstmt.setString(2, username);
                    pstmt.setString(3, password);
                    pstmt.setInt(4, age);
                    pstmt.setString(5, gender);
                    pstmt.setDate(6, new java.sql.Date(birthday.getTime()));
                    pstmt.setString(7, phone);
                    pstmt.setString(8, occupation);
                    pstmt.executeUpdate();


                    JOptionPane.showMessageDialog(null, "회원가입 완료!");
                    dispose();
                } catch (SQLException ex) {
                    System.out.println("SQLException: " + ex.getMessage());
                }
            }
        });
    }
}

class ChangeInfoForm extends JFrame {
    private final String username;
    private JTextField nameField;
    private JPasswordField password123Field;
    private JPasswordField passconField;
    private JLabel jobLabel;
    private JTextField jobField;
    private JButton pwButton;
    private JSpinner ageSpinner;
    private JSpinner dateSpinner;
    private JComboBox<String> occupationComboBox;
    private JTextField phone1Field;
    private JRadioButton maleRadioButton1;
    private JRadioButton femaleRadioButton1;
    Date currentDate = new Date();

    public ChangeInfoForm(String username) {
        this.username = username;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("개인정보 변경");
        setSize(330, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        getContentPane().add(panel);
        panel.setLayout(null);

        JLabel nameLabel = new JLabel("이름 : ");
        nameLabel.setBounds(35, 30, 80, 25);
        panel.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(120, 30, 150, 25);
        panel.add(nameField);

        JLabel passwordLabel = new JLabel("비밀번호 :");
        passwordLabel.setBounds(35, 60, 80, 20);
        panel.add(passwordLabel);

        password123Field = new JPasswordField();
        password123Field.setBounds(120, 60, 150, 25);
        panel.add(password123Field);

        JLabel passwordconLabel = new JLabel("재확인 :");
        passwordconLabel.setBounds(35, 90, 80, 20);
        panel.add(passwordconLabel);

        passconField = new JPasswordField();
        passconField.setBounds(120, 90, 150, 25);
        panel.add(passconField);

        JLabel pw1 = new JLabel("비밀번호 5자리 이상 필수!  ");
        pw1.setForeground(Color.red);
        pw1.setBounds(35, 120, 200, 25);
        panel.add(pw1);

        pwButton = new JButton("확인 ");
        pwButton.setBounds(190, 120, 80, 25);
        panel.add(pwButton);

        JLabel ageLabel = new JLabel("나이 :");
        ageLabel.setBounds(35, 150, 80, 25);
        panel.add(ageLabel);

        ageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        ageSpinner.setBounds(120, 150, 150, 25);
        panel.add(ageSpinner);

        ButtonGroup genderGroup = new ButtonGroup();

        JLabel genderLabel = new JLabel("성별 :");
        genderLabel.setBounds(35, 180, 80, 25);
        panel.add(genderLabel);

        maleRadioButton1 = new JRadioButton("남자 ");
        maleRadioButton1.setBounds(120, 180, 70, 25);
        panel.add(maleRadioButton1);
        genderGroup.add(maleRadioButton1);

        femaleRadioButton1 = new JRadioButton("여자 ");
        femaleRadioButton1.setBounds(180, 180, 90, 25);
        panel.add(femaleRadioButton1);
        genderGroup.add(femaleRadioButton1);

        JLabel birth = new JLabel("생일 :");
        birth.setBounds(35, 210, 80, 25);
        panel.add(birth);

        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setBounds(120, 210, 150, 25);
        panel.add(dateSpinner);

        JLabel phone = new JLabel("전화번호 :");
        phone.setBounds(35, 240, 80, 25);
        panel.add(phone);

        phone1Field = new JTextField();
        phone1Field.setBounds(120, 240, 150, 25);
        panel.add(phone1Field);

        JLabel job = new JLabel("직업 :");
        job.setBounds(35, 270, 80, 25);
        panel.add(job);

        String[] occupations = { "학생", "대학원생", "직장인", "기타" };
        occupationComboBox = new JComboBox<>(occupations);
        occupationComboBox.setBounds(120, 270, 150, 25);
        panel.add(occupationComboBox);


        pwButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String confirmPassword = new String(passconField.getPassword());
                if(password123Field.getText().length()>=5) {
                    pw1.setText("확인 완료 ");
                    pw1.setForeground(Color.blue);
                }else {
                    pw1.setText("5글자 이상 필요! ");
                    pw1.setForeground(Color.red);
                }
                if (!password123Field.getText().equals(confirmPassword)) {
                    pw1.setText("비밀번호가 일치하지 않음! ");
                    pw1.setForeground(Color.red);
                    return;
                }
            }
        });

        JButton saveButton = new JButton("저장");
        saveButton.setBounds(30, 320, 100, 25);
        panel.add(saveButton);
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String password = new String(password123Field.getPassword());
                int age = (int) ageSpinner.getValue();
                String gender = maleRadioButton1.isSelected() ? "Male" : "Female";
                Date birthday = ((SpinnerDateModel)dateSpinner.getModel()).getDate();
                String phone = phone1Field.getText();
                String occupation = (String) occupationComboBox.getSelectedItem();
                updateUserInfo(name, password, age, gender, phone, birthday, occupation);
                dispose();
            }
        });

        JButton cancelButton = new JButton("취소");
        cancelButton.setBounds(170, 320, 100, 25);
        panel.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        loadUserInfo();
    }

    private void loadUserInfo() {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/DB_23summer?serverTimezone=UTC", "root", "wkdrkdms01!")) {

            String query = "SELECT Name, Age, Gender, Phone, Birthday, Occupation FROM users WHERE ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            System.out.println(pstmt.toString());

            if (rs.next()) {
                String name = rs.getString("Name");
                int age = rs.getInt("Age");
                String gender = maleRadioButton1.isSelected() ? "Male" : "Female";
                String phone = rs.getString("Phone");
                Date birthday = rs.getDate("Birthday");
                String occupation = rs.getString("Occupation");

                nameField.setText(name);
                ageSpinner.setValue(age);

                if (gender.equals("Male"))
                    maleRadioButton1.setSelected(true);
                if (gender.equals("Female"))
                    femaleRadioButton1.setSelected(true);

                phone1Field.setText(phone);
                dateSpinner.setValue(birthday);
                occupationComboBox.setSelectedItem(occupation);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }


    private void updateUserInfo(String name, String password, int age, String gender, String phone, Date birth, String occupation) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/DB_23summer?serverTimezone=UTC", "root", "wkdrkdms01!")) {

            String query = "UPDATE users SET Name = ?, PW = ?, Age = ?, Gender = ?, Phone = ?, Birthday = ? , Occupation =? WHERE ID = ?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, password);
            pstmt.setInt(3, age);
            pstmt.setString(4, gender);
            pstmt.setString(5, phone1Field.getText());
            pstmt.setDate(6, new java.sql.Date(((Date) dateSpinner.getValue()).getTime()));
            pstmt.setString(7, occupation);
            pstmt.setString(8, username);
            System.out.println(pstmt.toString());
            pstmt.executeUpdate();


            JOptionPane.showMessageDialog(null, "수정이 완료되었습니다!");
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }
}

class ProfileOptions extends JFrame {
    private final String username;

    public ProfileOptions(String username) {
        this.username = username;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Profile Options");
        setSize(300, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        getContentPane().add(panel);
        panel.setLayout(null);

        JButton btnChangeInfo = new JButton("개인정보 변경 ");
        btnChangeInfo.setBounds(30, 30, 220, 25);
        panel.add(btnChangeInfo);
        btnChangeInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ChangeInfoForm changeInfoForm = new ChangeInfoForm(username);
                changeInfoForm.setVisible(true);
            }
        });

        JButton btnDeleteAccount = new JButton("계정 탈퇴 ");
        btnDeleteAccount.setBounds(30, 70, 220, 25);
        panel.add(btnDeleteAccount);
        btnDeleteAccount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(null, "정말로 삭제하시겠습니까 ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    deleteAccount(username);
                    dispose();
                }
            }
        });

        JButton btnLogout = new JButton("로그아웃 ");
        btnLogout.setBounds(30, 110, 220, 25);
        panel.add(btnLogout);
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                Main loginSystem = new Main();
                loginSystem.setVisible(true);
            }
        });
    }

    private void deleteAccount(String username) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/DB_23summer?serverTimezone=UTC", "root", "wkdrkdms01!")) {

            String query = "DELETE FROM users WHERE ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "계정이 삭제되었습니다! ");
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }
}
class AdminMode extends JFrame {

    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JTable table;

    public AdminMode() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Admin Mode");
        setSize(820, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        getContentPane().add(panel);
        panel.setLayout(null);

        JLabel lblTitle = new JLabel("사용자 현황 ");
        lblTitle.setBounds(30, 30, 220, 25);
        panel.add(lblTitle);

        searchField = new JTextField();
        searchField.setBounds(30, 60, 200, 25);
        panel.add(searchField);

        JPanel chartPanel = new JPanel();
        chartPanel.setBounds(300, 200,1,1);
        panel.add(chartPanel);

        JButton searchButton = new JButton("검색");
        searchButton.setBounds(240, 60, 80, 25);
        panel.add(searchButton);

        JButton deleteButton = new JButton("삭제");
        deleteButton.setBounds(330, 60, 80, 25);
        panel.add(deleteButton);

        JButton chartButton = new JButton("차트 보기");
        chartButton.setBounds(420, 60, 80, 25);
        panel.add(chartButton);

        chartButton.addActionListener(e -> {
            showChartSelection();
        });

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 특정 열 편집 가능하도록 설정
                return column == 0 ||  column == 1 || column == 2 || column == 3 || column == 4 || column == 5 || column == 6 || column == 7 ;
            }
        };
        table = new JTable(tableModel);

        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 130, 750, 200);
        panel.add(scrollPane);

        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Password");
        tableModel.addColumn("Age");
        tableModel.addColumn("Gender");
        tableModel.addColumn("Birthday");
        tableModel.addColumn("Phone");
        tableModel.addColumn("Occupation");

        tableModel.addRow(new Object[]{"------------", "------------", "------------", "------------", "------------", "------------", "------------",  "------------"});
        table.setAutoCreateRowSorter(true);

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/DB_23summer?serverTimezone=UTC", "root", "wkdrkdms01!")) {

            String query = "SELECT * FROM users";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String id = rs.getString("ID");
                String name = rs.getString("Name");
                String password = rs.getString("PW");
                int age = rs.getInt("Age");
                String gender = rs.getString("Gender");
                Date birthday = rs.getDate("Birthday");
                String phone = rs.getString("Phone");
                String occupation = rs.getString("Occupation");

                tableModel.addRow(new Object[]{id, name, password, age, gender, birthday, phone, occupation});
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }

        searchButton.addActionListener(e -> {
            String searchQuery = searchField.getText().trim();
            searchUsers(searchQuery);
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String id = table.getValueAt(selectedRow, 0).toString();
                deleteUserData(id);
                tableModel.removeRow(selectedRow);
            }
        });

        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean stopCellEditing() {
                int selectedRow = table.getSelectedRow();
                int selectedColumn = 0; // ID 열
                if (selectedRow != -1 && selectedColumn != -1) {
                    String id = table.getValueAt(selectedRow, 0).toString();
                    String columnName = table.getColumnName(selectedColumn);
                    String value = ((JTextField) getComponent()).getText();
                    updateUserData(id, columnName, value);
                }
                return super.stopCellEditing();
            }
        });


        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean stopCellEditing() {
                int selectedRow = table.getSelectedRow();
                int selectedColumn = 1; //Name
                if (selectedRow != -1 && selectedColumn != -1) {
                    String id = table.getValueAt(selectedRow, 0).toString();
                    String columnName = table.getColumnName(selectedColumn);
                    String value = ((JTextField) getComponent()).getText();
                    updateUserData(id, columnName, value);
                }
                return super.stopCellEditing();
            }
        });

        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean stopCellEditing() {
                int selectedRow = table.getSelectedRow();
                int selectedColumn = 2; //Name
                if (selectedRow != -1 && selectedColumn != -1) {
                    String id = table.getValueAt(selectedRow, 0).toString();
                    String columnName = table.getColumnName(selectedColumn);
                    String value = ((JTextField) getComponent()).getText();
                    updateUserData(id, columnName, value);
                }
                return super.stopCellEditing();
            }
        });

        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean stopCellEditing() {
                int selectedRow = table.getSelectedRow();
                int selectedColumn = 3; //Name
                if (selectedRow != -1 && selectedColumn != -1) {
                    String id = table.getValueAt(selectedRow, 0).toString();
                    String columnName = table.getColumnName(selectedColumn);
                    String value = ((JTextField) getComponent()).getText();
                    updateUserData(id, columnName, value);
                }
                return super.stopCellEditing();
            }
        });

        table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean stopCellEditing() {
                int selectedRow = table.getSelectedRow();
                int selectedColumn = 4; //Name
                if (selectedRow != -1 && selectedColumn != -1) {
                    String id = table.getValueAt(selectedRow, 0).toString();
                    String columnName = table.getColumnName(selectedColumn);
                    String value = ((JTextField) getComponent()).getText();
                    updateUserData(id, columnName, value);
                }
                return super.stopCellEditing();
            }
        });

        table.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean stopCellEditing() {
                int selectedRow = table.getSelectedRow();
                int selectedColumn = 5; //Name
                if (selectedRow != -1 && selectedColumn != -1) {
                    String id = table.getValueAt(selectedRow, 0).toString();
                    String columnName = table.getColumnName(selectedColumn);
                    String value = ((JTextField) getComponent()).getText();
                    updateUserData(id, columnName, value);
                }
                return super.stopCellEditing();
            }
        });

        table.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean stopCellEditing() {
                int selectedRow = table.getSelectedRow();
                int selectedColumn = 6; //Name
                if (selectedRow != -1 && selectedColumn != -1) {
                    String id = table.getValueAt(selectedRow, 0).toString();
                    String columnName = table.getColumnName(selectedColumn);
                    String value = ((JTextField) getComponent()).getText();
                    updateUserData(id, columnName, value);
                }
                return super.stopCellEditing();
            }
        });

        table.getColumnModel().getColumn(7).setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean stopCellEditing() {
                int selectedRow = table.getSelectedRow();
                int selectedColumn = 7; //Name
                if (selectedRow != -1 && selectedColumn != -1) {
                    String id = table.getValueAt(selectedRow, 0).toString();
                    String columnName = table.getColumnName(selectedColumn);
                    String value = ((JTextField) getComponent()).getText();
                    updateUserData(id, columnName, value);
                }
                return super.stopCellEditing();
            }
        });

    }
    private void searchUsers(String query) {
        tableModel.setRowCount(0);

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/DB_23summer?serverTimezone=UTC", "root", "wkdrkdms01!")) {

            String search = "SELECT * FROM users WHERE Name LIKE ? OR ID LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(search);
            pstmt.setString(1, "%" + query + "%");
            pstmt.setString(2, "%" + query + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String id = rs.getString("ID");
                String name = rs.getString("Name");
                String password = rs.getString("PW");
                int age = rs.getInt("Age");
                String gender = rs.getString("Gender");
                Date birthday = rs.getDate("Birthday");
                String phone = rs.getString("Phone");
                String occupation = rs.getString("Occupation");

                tableModel.addRow(new Object[]{id, name, password, age, gender, birthday, phone, occupation});
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }

    private void deleteUserData(String id) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/DB_23summer?serverTimezone=UTC", "root", "wkdrkdms01!")) {

            String deleteQuery = "DELETE FROM users WHERE ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(deleteQuery);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }

    private void updateUserData(String id, String columnName, String value) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/DB_23summer?serverTimezone=UTC", "root", "wkdrkdms01!")) {

            String updateQuery = "UPDATE users SET " + columnName + " = ? WHERE ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(updateQuery);
            pstmt.setString(1, value);
            pstmt.setString(2, id);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }

    private Map<String, Integer> getOccupationCounts() {
        Map<String, Integer> occupationCounts = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/DB_23summer?serverTimezone=UTC", "root", "wkdrkdms01!")) {

            String query = "SELECT Occupation, COUNT(*) AS Count FROM users GROUP BY Occupation";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String occupation = rs.getString("Occupation");
                int count = rs.getInt("Count");
                occupationCounts.put(occupation, count);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }

        return occupationCounts;
    }

    private Map<String, Integer> getGenderCounts() {
        Map<String, Integer> genderCounts = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/DB_23summer?serverTimezone=UTC", "root", "wkdrkdms01!")) {

            String query = "SELECT Gender, COUNT(*) AS Count FROM users GROUP BY Gender";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String gender = rs.getString("Gender");
                int count = rs.getInt("Count");
                genderCounts.put(gender, count);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }

        return genderCounts;
    }

    private Map<String, Integer> getBirthdayCounts() {
        Map<String, Integer> birthCounts = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/DB_23summer?serverTimezone=UTC", "root", "wkdrkdms01!")) {

            String query = "SELECT MONTH(birthday) AS month, COUNT(*) AS count FROM users GROUP BY MONTH(birthday)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int month = rs.getInt("month");
                int count = rs.getInt("count");
                birthCounts.put(String.valueOf(month), count);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }

        return birthCounts;
    }

    private void showChartSelection() {
        JFrame chartSelectionFrame = new JFrame("차트 선택");
        chartSelectionFrame.setSize(300, 200);
        chartSelectionFrame.setLocationRelativeTo(null);
        chartSelectionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel chartSelectionPanel = new JPanel();
        chartSelectionPanel.setLayout(new GridLayout(2, 2));

        JButton genderChartButton = new JButton("성별 차트 보기");
        genderChartButton.addActionListener(e -> {
            showChart("gender");
            chartSelectionFrame.dispose();
        });
        chartSelectionPanel.add(genderChartButton);

        JButton occupationChartButton = new JButton("직업별 차트 보기");
        occupationChartButton.addActionListener(e -> {
            showChart("occupation");
            chartSelectionFrame.dispose();
        });
        chartSelectionPanel.add(occupationChartButton);

        JButton birthdayChartButton = new JButton("생일 월별 차트 보기");
        birthdayChartButton.addActionListener(e -> {
            showChart("birthday");
            chartSelectionFrame.dispose();
        });
        chartSelectionPanel.add(birthdayChartButton);

        JButton allChartButton = new JButton("전 차트 보기");
        allChartButton.addActionListener(e -> {
            showChart("combined");
            chartSelectionFrame.dispose();
        });
        chartSelectionPanel.add(allChartButton);

        chartSelectionFrame.add(chartSelectionPanel);
        chartSelectionFrame.setVisible(true);
    }

    private void showChart(String type) {
        if (type.equals("gender")) {
            Map<String, Integer> genderCounts = getGenderCounts();
            DefaultPieDataset dataset = new DefaultPieDataset();

            for (Map.Entry<String, Integer> entry : genderCounts.entrySet()) {
                dataset.setValue(entry.getKey(), entry.getValue());
            }

            JFreeChart chart = ChartFactory.createPieChart("성별 분포", dataset, true, true, false);
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}({1})"));

            ChartFrame frame = new ChartFrame("성별 분포 차트", chart);
            frame.setSize(500, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }  else if (type.equals("occupation")) {
            Map<String, Integer> occupationCounts = getOccupationCounts();
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            for (Map.Entry<String, Integer> entry : occupationCounts.entrySet()) {
                dataset.setValue(entry.getValue(), "직업별", entry.getKey());
            }

            JFreeChart chart = ChartFactory.createBarChart("직업별 분포", "직업", "인원수", dataset,
                    PlotOrientation.VERTICAL, true, true, false);
            CategoryPlot plot = chart.getCategoryPlot();
            BarRenderer renderer = (BarRenderer) plot.getRenderer();

            renderer.setSeriesPaint(0, new Color(0, 123, 255));
            ChartFrame frame = new ChartFrame("직업별 분포 차트", chart);
            frame.setSize(500, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } else if (type.equals("birthday")) {
            Map<String, Integer> birthCounts = getBirthdayCounts();
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            for (Map.Entry<String, Integer> entry : birthCounts.entrySet()) {
                dataset.setValue(entry.getValue(), "월별", entry.getKey());
            }

            JFreeChart chart = ChartFactory.createLineChart("생일 월별 분포", "월", "인원수", dataset,
                    PlotOrientation.VERTICAL, true, true, false);
            CategoryPlot plot = chart.getCategoryPlot();
            LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
            renderer.setSeriesStroke(0, new BasicStroke(2.0f));

            ChartFrame frame = new ChartFrame("생일 월별 분포 차트", chart);
            frame.setSize(500, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } else if (type.equals("combined")) {
            Map<String, Integer> genderCounts = getGenderCounts();
            Map<String, Integer> occupationCounts = getOccupationCounts();
            Map<String, Integer> birthCounts = getBirthdayCounts();

            DefaultCategoryDataset genderDataset = new DefaultCategoryDataset();
            for (Map.Entry<String, Integer> entry : genderCounts.entrySet()) {
                genderDataset.setValue(entry.getValue(), "성별별", entry.getKey());
            }

            DefaultCategoryDataset occupationDataset = new DefaultCategoryDataset();
            for (Map.Entry<String, Integer> entry : occupationCounts.entrySet()) {
                occupationDataset.setValue(entry.getValue(), "직업별", entry.getKey());
            }

            DefaultCategoryDataset birthDataset = new DefaultCategoryDataset();
            for (Map.Entry<String, Integer> entry : birthCounts.entrySet()) {
                birthDataset.setValue(entry.getValue(), "월별", entry.getKey());
            }

            JFreeChart genderChart = ChartFactory.createBarChart("성별 분포", "성별", "인원수", genderDataset,
                    PlotOrientation.VERTICAL, true, true, false);
            CategoryPlot genderPlot = genderChart.getCategoryPlot();
            BarRenderer genderRenderer = (BarRenderer) genderPlot.getRenderer();
            genderRenderer.setSeriesPaint(0, Color.BLUE);

            JFreeChart occupationChart = ChartFactory.createBarChart("직업별 분포", "직업", "인원수", occupationDataset,
                    PlotOrientation.VERTICAL, true, true, false);
            CategoryPlot occupationPlot = occupationChart.getCategoryPlot();
            BarRenderer occupationRenderer = (BarRenderer) occupationPlot.getRenderer();
            occupationRenderer.setSeriesPaint(0, Color.RED);

            JFreeChart birthChart = ChartFactory.createLineChart("생일 월별 분포", "월", "인원수", birthDataset,
                    PlotOrientation.VERTICAL, true, true, false);
            CategoryPlot birthPlot = birthChart.getCategoryPlot();
            LineAndShapeRenderer birthRenderer = (LineAndShapeRenderer) birthPlot.getRenderer();
            birthRenderer.setSeriesStroke(0, new BasicStroke(2.0f));

            CombinedDomainCategoryPlot combinedPlot = new CombinedDomainCategoryPlot();
            combinedPlot.add(genderPlot, 1);
            combinedPlot.add(occupationPlot, 1);
            combinedPlot.add(birthPlot, 1);
            combinedPlot.setGap(10.0);

            JFreeChart combinedChart = new JFreeChart("전체 분포 차트", JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, true);
            combinedChart.setBackgroundPaint(Color.white);

            ChartFrame frame = new ChartFrame("전체 분포 차트", combinedChart);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }
}