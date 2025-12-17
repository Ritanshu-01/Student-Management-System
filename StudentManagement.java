import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class StudentManagement extends JFrame {

    /* ================= FILES ================= */
    private static final String USER_FILE = "users.txt";
    private static final String STUDENT_FILE = "students.txt";

    /* ================= COLOR SCHEME ================= */
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color LIGHT_BG = new Color(236, 240, 241);
    private static final Color DARK_TEXT = new Color(44, 62, 80);

    /* ================= FONTS ================= */
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FIELD_FONT_BOLD = FIELD_FONT.deriveFont(Font.BOLD);

    /* ================= SESSION ================= */
    private String loggedInUser = null;

    /* ================= UI COMPONENTS ================= */
    private JTextField idField, nameField, mobileField, fnameField,
            fmobileField, emailField, feesField;
    private JComboBox<String> courseCombo;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel userLabel;

    /* ================= COURSE FEES MAPPING ================= */
    private static final Map<String, String> COURSE_FEES = new LinkedHashMap<>();

    static {
        COURSE_FEES.put("Select Course", "");
        COURSE_FEES.put("MCA", "700000");
        COURSE_FEES.put("BCA", "500000");
        COURSE_FEES.put("BBA", "600000");
        COURSE_FEES.put("MBA", "1200000");
        COURSE_FEES.put("B.COM", "400000");
        COURSE_FEES.put("M.COM", "600000");
        COURSE_FEES.put("B.TECH-CSE", "1000000");
        COURSE_FEES.put("B.TECH-AI", "1050000");
    }

    /* ================= CONSTRUCTOR ================= */
    public StudentManagement() {
        loginScreen();
    }

    /* ================= LOGIN SCREEN ================= */
    private void loginScreen() {
        JFrame frame = new JFrame("Student Management System - Login");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(LIGHT_BG);

        /* Card (small centered panel) */
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        cardPanel.setPreferredSize(new Dimension(520, 360));

        /* Header inside card */
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel titleLabel = new JLabel("Student Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        /* Form Panel inside card */
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(22, 28, 18, 28));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(LABEL_FONT);
        JTextField usernameField = new JTextField(20);
        usernameField.setFont(FIELD_FONT);
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(LABEL_FONT);
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(FIELD_FONT);
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(passwordField, gbc);

        /* Button Panel inside card */
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttonPanel.setBackground(Color.WHITE);

        JButton loginBtn = createStyledButton("LOGIN", SUCCESS_COLOR);
        JButton signupBtn = createStyledButton("SIGN UP", PRIMARY_COLOR);

        buttonPanel.add(loginBtn);
        buttonPanel.add(signupBtn);

        // Enter-key navigation: Enter in username -> focus password; Enter in password
        // -> submit
        usernameField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> loginBtn.doClick());

        // Make login button the default so Enter outside text fields will submit
        frame.getRootPane().setDefaultButton(loginBtn);

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter username and password",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (authenticate(username, password)) {
                loggedInUser = username;
                frame.dispose();
                dashboard();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Credentials!", "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        });

        signupBtn.addActionListener(e -> {
            frame.dispose();
            signupScreen();
        });

        cardPanel.add(headerPanel, BorderLayout.NORTH);
        cardPanel.add(formPanel, BorderLayout.CENTER);
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        mainPanel.add(cardPanel, c);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    /* ================= SIGNUP SCREEN ================= */
    private void signupScreen() {
        JFrame frame = new JFrame("Create New Account");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(true);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(LIGHT_BG);

        /* Card (small centered panel) */
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        cardPanel.setPreferredSize(new Dimension(550, 420));

        /* Header inside card */
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        /* Form Panel inside card */
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(18, 28, 15, 28));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create all 5 fields
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(LABEL_FONT);
        JTextField usernameField = new JTextField(20);
        usernameField.setFont(FIELD_FONT);
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JLabel contactLabel = new JLabel("Contact:");
        contactLabel.setFont(LABEL_FONT);
        JTextField contactField = new JTextField(20);
        contactField.setFont(FIELD_FONT);
        contactField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(LABEL_FONT);
        JTextField emailField = new JTextField(20);
        emailField.setFont(FIELD_FONT);
        emailField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(LABEL_FONT);
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(FIELD_FONT);
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JLabel repassLabel = new JLabel("Re-enter Password:");
        repassLabel.setFont(LABEL_FONT);
        JPasswordField repassField = new JPasswordField(20);
        repassField.setFont(FIELD_FONT);
        repassField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Add to form grid
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.35;
        formPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.35;
        formPanel.add(contactLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        formPanel.add(contactField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.35;
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.35;
        formPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.35;
        formPanel.add(repassLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        formPanel.add(repassField, gbc);

        /* Button Panel inside card */
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttonPanel.setBackground(Color.WHITE);

        JButton createBtn = createStyledButton("CREATE ACCOUNT", SUCCESS_COLOR);
        JButton backBtn = createStyledButton("BACK", new Color(155, 155, 155));

        buttonPanel.add(createBtn);
        buttonPanel.add(backBtn);

        // Enter-key navigation: username -> contact -> email -> password -> repassword
        // -> create
        usernameField.addActionListener(e -> contactField.requestFocus());
        contactField.addActionListener(e -> emailField.requestFocus());
        emailField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> repassField.requestFocus());
        repassField.addActionListener(e -> createBtn.doClick());

        // Make create button the default so Enter outside text fields will submit
        frame.getRootPane().setDefaultButton(createBtn);

        createBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String contact = contactField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String repassword = new String(repassField.getPassword());

            if (username.isEmpty() || contact.isEmpty() || email.isEmpty() || password.isEmpty()
                    || repassword.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields are required!",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!password.equals(repassword)) {
                JOptionPane.showMessageDialog(frame, "Passwords do not match!",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                repassField.setText("");
                passwordField.setText("");
                passwordField.requestFocus();
                return;
            }

            if (usernameExists(username)) {
                JOptionPane.showMessageDialog(frame, "Username already exists!",
                        "Duplicate User", JOptionPane.WARNING_MESSAGE);
                usernameField.setText("");
                usernameField.requestFocus();
                return;
            }

            try (FileWriter fw = new FileWriter(USER_FILE, true)) {
                fw.write(username + "," + contact + "," + email + "," + password + "\n");
                JOptionPane.showMessageDialog(frame, "Account Created Successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                new StudentManagement();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error creating account",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> {
            frame.dispose();
            new StudentManagement();
        });

        cardPanel.add(headerPanel, BorderLayout.NORTH);
        cardPanel.add(formPanel, BorderLayout.CENTER);
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        mainPanel.add(cardPanel, c);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    /* ================= AUTHENTICATE ================= */
    private boolean authenticate(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[0].equals(username) && parts[3].equals(password)) {
                    return true;
                }
            }
        } catch (IOException ignored) {
        }
        return false;
    }

    /* ================= CHECK DUPLICATE USERNAME ================= */
    private boolean usernameExists(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException ignored) {
        }
        return false;
    }

    /* ================= DASHBOARD ================= */
    private void dashboard() {
        setTitle("Student Management System - Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(LIGHT_BG);

        /* ========== TOP PANEL ========== */
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PRIMARY_COLOR);
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Student Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        userLabel = new JLabel(loggedInUser != null ? "👤 " + loggedInUser : "Guest");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setForeground(Color.WHITE);
        userLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showProfileMenu(userLabel);
            }
        });

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(userLabel, BorderLayout.EAST);

        /* ========== FORM PANEL ========== */
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(LIGHT_BG);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), "Student Details"));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField = createStyledTextField();
        nameField = createStyledTextField();
        mobileField = createStyledTextField();
        fnameField = createStyledTextField();
        fmobileField = createStyledTextField();
        emailField = createStyledTextField();
        feesField = createStyledTextField();
        feesField.setEditable(false);

        courseCombo = new JComboBox<>(COURSE_FEES.keySet().toArray(new String[0]));
        courseCombo.setFont(FIELD_FONT);
        courseCombo.setBackground(Color.WHITE);

        Component[] inputs = new Component[] { idField, nameField, mobileField, emailField,
                fnameField, fmobileField, courseCombo, feesField };

        for (int i = 0; i < inputs.length; i++) {
            final int idx = i;
            Component comp = inputs[i];
            if (comp instanceof JTextField) {
                JTextField tf = (JTextField) comp;
                if (idx < inputs.length - 1) {
                    Component next = inputs[idx + 1];
                    tf.addActionListener(e -> {
                        if (next instanceof JComboBox) {
                            ((JComboBox<?>) next).requestFocus();
                        } else {
                            ((JComponent) next).requestFocus();
                        }
                    });
                } else {
                    // Last field (fees) -> submit
                    tf.addActionListener(e -> {
                        showAddConfirmation();
                    });
                }
            } else if (comp instanceof JComboBox) {
                JComboBox<?> cb = (JComboBox<?>) comp;
                // When user selects a course or presses Enter on combo, move to fees field
                cb.addActionListener(e -> feesField.requestFocus());
                cb.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            feesField.requestFocus();
                        }
                    }
                });
            }
        }

        courseCombo.addActionListener(e -> {
            String selected = (String) courseCombo.getSelectedItem();
            String fee = COURSE_FEES.getOrDefault(selected, "");
            feesField.setText(fee);
        });

        addFormField(formPanel, gbc, "ID:", idField, 0, 0);
        addFormField(formPanel, gbc, "Name:", nameField, 1, 0);
        addFormField(formPanel, gbc, "Mobile:", mobileField, 2, 0);
        addFormField(formPanel, gbc, "Email:", emailField, 3, 0);
        addFormField(formPanel, gbc, "Father Name:", fnameField, 0, 2);
        addFormField(formPanel, gbc, "Father Mobile:", fmobileField, 1, 2);
        addFormField(formPanel, gbc, "Course:", courseCombo, 2, 2);
        addFormField(formPanel, gbc, "Fees:", feesField, 3, 2);

        /* ========== BUTTON PANEL ========== */
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(LIGHT_BG);
        buttonPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 18, 0));

        JButton addBtn = createStyledButton("[+] Add Student", SUCCESS_COLOR);
        JButton updateBtn = createStyledButton("[E] Update", SECONDARY_COLOR);
        JButton deleteBtn = createStyledButton("[X] Delete", DANGER_COLOR);
        JButton searchBtn = createStyledButton("[S] Search", PRIMARY_COLOR);
        JButton loadBtn = createStyledButton("[L] Load All", PRIMARY_COLOR);
        JButton clearBtn = createStyledButton("[C] Clear", new Color(155, 155, 155));

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(new JSeparator(JSeparator.VERTICAL));
        buttonPanel.add(searchBtn);
        buttonPanel.add(loadBtn);
        buttonPanel.add(clearBtn);

        /* ========== TABLE SETUP ========== */
        tableModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Mobile", "Father", "F.Mobile", "Email", "Course", "Fees" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(FIELD_FONT);
        table.setRowHeight(30);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(SECONDARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setBackground(Color.WHITE);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(PRIMARY_COLOR);
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 35));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), "Student Records"));

        /* ========== ADD COMPONENTS ========== */
        add(topPanel, BorderLayout.NORTH);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(LIGHT_BG);
        topContainer.add(formPanel, BorderLayout.CENTER);
        topContainer.add(buttonPanel, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.PAGE_START);
        add(scrollPane, BorderLayout.CENTER);

        /* ========== ACTION LISTENERS ========== */
        addBtn.addActionListener(e -> addStudent());
        // Make Add button default so Enter on other components can also activate it
        // when focused
        getRootPane().setDefaultButton(addBtn);
        updateBtn.addActionListener(e -> updateStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        searchBtn.addActionListener(e -> {
            String key = JOptionPane.showInputDialog(this, "Enter Student ID or Mobile to search:", "Search",
                    JOptionPane.QUESTION_MESSAGE);
            if (key != null && !key.trim().isEmpty()) {
                searchStudentByKey(key.trim());
            }
        });
        loadBtn.addActionListener(e -> loadStudents());
        clearBtn.addActionListener(e -> clearFields());

        loadStudents();
        setVisible(true);
    }

    /* ================= FORM FIELD HELPER ================= */
    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, Component field, int row,
            int column) {
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;

        JLabel lbl = new JLabel(label);
        lbl.setFont(LABEL_FONT);
        panel.add(lbl, gbc);

        gbc.gridx = column + 1;
        gbc.weightx = 2.0;
        panel.add(field, gbc);
    }

    /* ================= STYLED COMPONENTS ================= */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(BUTTON_FONT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(bgColor, 2));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 35));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.darker());
                btn.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
                btn.setBorder(BorderFactory.createLineBorder(bgColor, 2));
            }
        });

        return btn;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(FIELD_FONT);
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        field.setBackground(Color.WHITE);
        return field;
    }

    /* ================= PROFILE MENU ================= */
    private void showProfileMenu(JLabel userLabel) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(new Color(245, 245, 245));

        if (loggedInUser != null) {
            JMenuItem profileItem = new JMenuItem("Profile: " + loggedInUser);
            profileItem.setEnabled(false);
            profileItem.setFont(new Font("Segoe UI", Font.BOLD, 11));
            menu.add(profileItem);
            menu.addSeparator();

            JMenuItem logoutItem = new JMenuItem("Logout");
            logoutItem.setFont(BUTTON_FONT);
            logoutItem.addActionListener(e -> {
                dispose();
                new StudentManagement();
            });
            menu.add(logoutItem);
        } else {
            JMenuItem loginItem = new JMenuItem("Login");
            loginItem.setFont(BUTTON_FONT);
            loginItem.addActionListener(e -> {
                dispose();
                new StudentManagement();
            });
            menu.add(loginItem);

            JMenuItem signupItem = new JMenuItem("Sign Up");
            signupItem.setFont(BUTTON_FONT);
            signupItem.addActionListener(e -> {
                dispose();
                signupScreen();
            });
            menu.add(signupItem);
        }

        menu.show(userLabel, 0, userLabel.getHeight());
    }

    /* ================= STUDENT OPERATIONS ================= */
    private void addStudent() {
        String id = idField.getText().trim();
        String name = capitalizeFirstLetter(nameField.getText().trim());
        String mobile = mobileField.getText().trim();
        String fname = capitalizeFirstLetter(fnameField.getText().trim());
        String fmobile = fmobileField.getText().trim();
        String email = emailField.getText().trim();
        String course = (String) courseCombo.getSelectedItem();
        String fees = feesField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || mobile.isEmpty() || fname.isEmpty() ||
                fmobile.isEmpty() || email.isEmpty() || course.equals("Select Course") || fees.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (studentExists(id, email, mobile)) {
            JOptionPane.showMessageDialog(this,
                    "Student already exists (ID / Email / Mobile must be unique)", "Duplicate Entry",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (FileWriter fw = new FileWriter(STUDENT_FILE, true)) {
            fw.write(id + "," + name + "," + mobile + "," + fname + "," + fmobile + "," + email + "," + course
                    + "," + fees + "\n");
            JOptionPane.showMessageDialog(this, "✓ Student Added Successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadStudents();
            // select newly added row (by ID)
            for (int r = 0; r < tableModel.getRowCount(); r++) {
                Object val = tableModel.getValueAt(r, 0);
                if (val != null && val.toString().equals(id)) {
                    final int rowToSelect = r;
                    SwingUtilities.invokeLater(() -> {
                        table.setRowSelectionInterval(rowToSelect, rowToSelect);
                        table.scrollRectToVisible(table.getCellRect(rowToSelect, 0, true));
                    });
                    break;
                }
            }
            clearFields();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error adding student", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        String id = JOptionPane.showInputDialog(this, "Enter Student ID to update:", "Update Student",
                JOptionPane.QUESTION_MESSAGE);
        if (id == null || id.trim().isEmpty())
            return;

        id = id.trim();
        String[] currentData = getStudentData(id);
        if (currentData == null) {
            JOptionPane.showMessageDialog(this, "No student found with ID: " + id, "Not Found",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Show current data (storage order:
        // ID,Name,Mobile,FatherName,FatherMobile,Email,Course,Fees)
        StringBuilder currentInfo = new StringBuilder();
        currentInfo.append("Current Record:\n\n");
        currentInfo.append("ID: ").append(currentData[0]).append("\n");
        currentInfo.append("Name: ").append(currentData[1]).append("\n");
        currentInfo.append("Mobile: ").append(currentData[2]).append("\n");
        currentInfo.append("Father Name: ").append(currentData[3]).append("\n");
        currentInfo.append("Father Mobile: ").append(currentData[4]).append("\n");
        currentInfo.append("Email: ").append(currentData[5]).append("\n");
        currentInfo.append("Course: ").append(currentData[6]).append("\n");
        currentInfo.append("Fees: ").append(currentData[7]).append("\n\n");
        currentInfo.append("Select a field to update:");

        String[] options = { "Name", "Mobile", "Email", "Father Name", "Father Mobile", "Course", "Fees" };
        int choice = JOptionPane.showOptionDialog(this,
                currentInfo.toString(), "Update Student Record",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice < 0)
            return;

        // mapping: options index -> stored CSV index
        int[] map = { 1, 2, 5, 3, 4, 6, 7 };
        String existing = currentData[map[choice]];
        String newValue = JOptionPane.showInputDialog(this, "Enter new value for " + options[choice] + ":",
                existing);
        if (newValue == null || newValue.trim().isEmpty())
            return;

        final String finalId = id;
        final String finalNewValue = (choice == 0 || choice == 3) ? capitalizeFirstLetter(newValue.trim())
                : newValue.trim();
        final int finalFieldIndex = map[choice];

        rewriteFile(line -> {
            String[] parts = line.split(",");
            if (parts.length >= 1 && parts[0].equals(finalId)) {
                // Only update well-formed records (expecting 8 columns)
                if (parts.length >= 8) {
                    if (finalFieldIndex == 6) { // Course changed -> update course and auto-set fees
                        parts[6] = finalNewValue;
                        parts[7] = COURSE_FEES.getOrDefault(finalNewValue, parts[7]);
                    } else {
                        // guard index
                        if (finalFieldIndex >= 0 && finalFieldIndex < parts.length) {
                            parts[finalFieldIndex] = finalNewValue;
                        }
                    }
                    return String.join(",", parts);
                } else {
                    // malformed line: skip modification
                    return line;
                }
            }
            return line;
        });

        JOptionPane.showMessageDialog(this, "✓ Record Updated Successfully!", "Success",
                JOptionPane.INFORMATION_MESSAGE);
        loadStudents();
        clearFields();
    }

    private void deleteStudent() {
        String id = JOptionPane.showInputDialog(this, "Enter Student ID to delete:", "Delete Student",
                JOptionPane.QUESTION_MESSAGE);
        if (id == null || id.trim().isEmpty())
            return;

        if (!idExists(id.trim())) {
            JOptionPane.showMessageDialog(this, "No student found with ID: " + id, "Not Found",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this student?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        rewriteFile(line -> line.startsWith(id.trim() + ",") ? null : line);
        JOptionPane.showMessageDialog(this, "✓ Record Deleted Successfully!", "Success",
                JOptionPane.INFORMATION_MESSAGE);
        clearFields();
        loadStudents();
    }

    private void searchStudentByKey(String key) {
        tableModel.setRowCount(0);
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && (parts[0].equals(key) || parts[2].equals(key))) {
                    tableModel.addRow(parts);
                    found = true;
                }
            }
        } catch (IOException ignored) {
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "No student found with ID or Mobile: " + key, "Not Found",
                    JOptionPane.WARNING_MESSAGE);
            loadStudents();
        }
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        List<String[]> students = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 8) {
                    students.add(parts);
                }
            }
        } catch (IOException ignored) {
        }

        // Sort by ID (first column) numerically from smallest to largest
        students.sort((a, b) -> {
            try {
                int idA = Integer.parseInt(a[0]);
                int idB = Integer.parseInt(b[0]);
                return Integer.compare(idA, idB);
            } catch (NumberFormatException e) {
                return a[0].compareTo(b[0]); // Fallback to alphabetical if not numeric
            }
        });

        // Add sorted students to table
        for (String[] student : students) {
            tableModel.addRow(student);
        }
    }

    private void showAddConfirmation() {
        int result = JOptionPane.showConfirmDialog(this,
                "Do you want to add this student?",
                "Confirm Add",
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            addStudent();
        }
    }

    /* ================= HELPER METHODS ================= */
    private boolean studentExists(String id, String email, String mobile) {
        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && (parts[0].equals(id) || parts[2].equals(mobile) || parts[5].equals(email))) {
                    return true;
                }
            }
        } catch (IOException ignored) {
        }
        return false;
    }

    private boolean idExists(String id) {
        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(id + ",")) {
                    return true;
                }
            }
        } catch (IOException ignored) {
        }
        return false;
    }

    private void rewriteFile(Function<String, String> transformer) {
        try {
            List<String> lines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String transformed = transformer.apply(line);
                    if (transformed != null) {
                        lines.add(transformed);
                    }
                }
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(STUDENT_FILE))) {
                for (String line : lines) {
                    bw.write(line + "\n");
                }
            }
        } catch (IOException ignored) {
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        mobileField.setText("");
        fnameField.setText("");
        fmobileField.setText("");
        emailField.setText("");
        courseCombo.setSelectedIndex(0);
        feesField.setText("");
    }

    /* ================= UTILITY METHODS ================= */
    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty())
            return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    private String[] getStudentData(String id) {
        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 8 && parts[0].equals(id)) {
                    return parts;
                }
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    /* ================= MAIN ================= */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagement());
    }
}
