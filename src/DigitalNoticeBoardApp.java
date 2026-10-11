import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Advanced Java - Phase-I Project Submission
 * Topic: Digital Notice Board System
 * Syllabus: Java Collections Framework, Event Handling, Swing Components, Layout Managers
 */
public class DigitalNoticeBoardApp extends JFrame {

    // --- Data Model ---
    public enum Priority {
        HIGH(1), MEDIUM(2), LOW(3);
        private final int rank;
        Priority(int rank) { this.rank = rank; }
        public int getRank() { return rank; }
    }

    public static class Notice implements Comparable<Notice> {
        private static int idCounter = 101;
        private final int id;
        private final String title;
        private final String category;
        private final Priority priority;
        private final String content;
        private final LocalDate expiryDate;

        public Notice(String title, String category, Priority priority, String content, LocalDate expiryDate) {
            this.id = idCounter++;
            this.title = title;
            this.category = category;
            this.priority = priority;
            this.content = content;
            this.expiryDate = expiryDate;
        }

        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getCategory() { return category; }
        public Priority getPriority() { return priority; }
        public String getContent() { return content; }
        public LocalDate getExpiryDate() { return expiryDate; }

        @Override
        public int compareTo(Notice other) {
            return Integer.compare(this.priority.getRank(), other.priority.getRank());
        }
    }

    // --- In-Memory Java Collections ---
    private final List<Notice> masterNoticeList = new ArrayList<>();
    private final PriorityQueue<Notice> urgentNoticeQueue = new PriorityQueue<>();

    // --- GUI Components ---
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private JLabel tickerLabel;

    // Admin Panel Form Fields
    private JTextField txtTitle;
    private JComboBox<String> comboCategory;
    private JComboBox<Priority> comboPriority;
    private JTextArea txtContent;
    private JTextField txtExpiryDate;

    // Display Panel Components
    private JTable noticeTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> comboFilterCategory;

    public DigitalNoticeBoardApp() {
        setTitle("Campus Digital Notice Board System - Phase I");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize Pre-loaded Data
        seedInitialData();

        // Build Interface Panels
        add(createHeaderAndTickerPanel(), BorderLayout.NORTH);
        add(createMainCardPanel(), BorderLayout.CENTER);
        add(createNavigationPanel(), BorderLayout.SOUTH);

        // Refresh UI Views
        refreshNoticeTable("ALL");
        updateUrgentTicker();
    }

    private JPanel createHeaderAndTickerPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 41, 59));

        JLabel titleLabel = new JLabel("DIGITAL NOTICE BOARD SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JPanel tickerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tickerPanel.setBackground(new Color(225, 29, 72));

        JLabel tickerTitle = new JLabel(" URGENT NOTICE: ");
        tickerTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        tickerTitle.setForeground(Color.WHITE);

        tickerLabel = new JLabel("No Urgent Announcements");
        tickerLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tickerLabel.setForeground(Color.WHITE);

        tickerPanel.add(tickerTitle);
        tickerPanel.add(tickerLabel);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(tickerPanel, BorderLayout.SOUTH);
        return headerPanel;
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navPanel.setBackground(new Color(241, 245, 249));

        JButton btnViewDisplay = new JButton("View Public Notice Board");
        JButton btnViewAdmin = new JButton("Open Admin Panel");

        btnViewDisplay.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnViewAdmin.setFont(new Font("SansSerif", Font.BOLD, 13));

        btnViewDisplay.addActionListener(e -> cardLayout.show(mainContentPanel, "DISPLAY_PANEL"));
        btnViewAdmin.addActionListener(e -> cardLayout.show(mainContentPanel, "ADMIN_PANEL"));

        navPanel.add(btnViewDisplay);
        navPanel.add(btnViewAdmin);
        return navPanel;
    }

    private JPanel createMainCardPanel() {
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        mainContentPanel.add(createDisplayPanel(), "DISPLAY_PANEL");
        mainContentPanel.add(createAdminPanel(), "ADMIN_PANEL");

        return mainContentPanel;
    }

    private JPanel createDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter by Category: "));

        comboFilterCategory = new JComboBox<>(new String[]{"ALL", "Academic", "Exams", "Events", "Placement", "General"});
        comboFilterCategory.addActionListener(e -> {
            String selectedCategory = (String) comboFilterCategory.getSelectedItem();
            refreshNoticeTable(selectedCategory);
        });
        filterPanel.add(comboFilterCategory);

        String[] columns = {"ID", "Title", "Category", "Priority", "Content", "Expiry Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        noticeTable = new JTable(tableModel);
        noticeTable.setRowHeight(26);
        noticeTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        noticeTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        noticeTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        noticeTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        noticeTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        noticeTable.getColumnModel().getColumn(4).setPreferredWidth(350);
        noticeTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(noticeTable);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(" Post New Institutional Notice "));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Notice Title:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtTitle = new JTextField(30);
        formPanel.add(txtTitle, gbc);

        // Category
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        comboCategory = new JComboBox<>(new String[]{"Academic", "Exams", "Events", "Placement", "General"});
        formPanel.add(comboCategory, gbc);

        // Priority
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Priority Level:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        comboPriority = new JComboBox<>(Priority.values());
        formPanel.add(comboPriority, gbc);

        // Expiry Date
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Expiry Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtExpiryDate = new JTextField(LocalDate.now().plusDays(7).toString());
        formPanel.add(txtExpiryDate, gbc);

        // Content
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Notice Content:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtContent = new JTextArea(4, 30);
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(txtContent), gbc);

        // Submit Button
        gbc.gridx = 1; gbc.gridy = 5; gbc.gridwidth = 1;
        JButton btnSubmit = new JButton("Publish Notice");
        btnSubmit.setBackground(new Color(16, 185, 129));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 13));

        btnSubmit.addActionListener(e -> handleNoticeSubmission());

        formPanel.add(btnSubmit, gbc);
        panel.add(formPanel, BorderLayout.NORTH);

        return panel;
    }

    private void handleNoticeSubmission() {
        String title = txtTitle.getText().trim();
        String category = (String) comboCategory.getSelectedItem();
        Priority priority = (Priority) comboPriority.getSelectedItem();
        String content = txtContent.getText().trim();
        String dateStr = txtExpiryDate.getText().trim();

        if (title.isEmpty() || content.isEmpty() || dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate expiryDate = LocalDate.parse(dateStr);
            Notice notice = new Notice(title, category, priority, content, expiryDate);

            masterNoticeList.add(notice);
            urgentNoticeQueue.add(notice);

            refreshNoticeTable((String) comboFilterCategory.getSelectedItem());
            updateUrgentTicker();

            txtTitle.setText("");
            txtContent.setText("");
            JOptionPane.showMessageDialog(this, "Notice Published Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            cardLayout.show(mainContentPanel, "DISPLAY_PANEL");

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Date Format. Please use YYYY-MM-DD.", "Date Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshNoticeTable(String filterCategory) {
        tableModel.setRowCount(0);

        for (Notice notice : masterNoticeList) {
            if (filterCategory.equals("ALL") || notice.getCategory().equalsIgnoreCase(filterCategory)) {
                tableModel.addRow(new Object[]{
                        notice.getId(),
                        notice.getTitle(),
                        notice.getCategory(),
                        notice.getPriority(),
                        notice.getContent(),
                        notice.getExpiryDate()
                });
            }
        }
    }

    private void updateUrgentTicker() {
        if (!urgentNoticeQueue.isEmpty()) {
            Notice topNotice = urgentNoticeQueue.peek();
            tickerLabel.setText("[" + topNotice.getCategory() + "] " + topNotice.getTitle() + " - " + topNotice.getContent());
        } else {
            tickerLabel.setText("No Urgent Announcements");
        }
    }

    private void seedInitialData() {
        Notice n1 = new Notice("Mid-Term Exam Schedule Released", "Exams", Priority.HIGH, "Mid-term exams start from 15th Oct.", LocalDate.now().plusDays(10));
        Notice n2 = new Notice("Annual Cultural Fest Registration", "Events", Priority.LOW, "Register for cultural events at room 204.", LocalDate.now().plusDays(12));
        Notice n3 = new Notice("Placement Drive - Tech Corp", "Placement", Priority.HIGH, "Resume submission deadline tomorrow at 5 PM.", LocalDate.now().plusDays(2));

        masterNoticeList.add(n1);
        masterNoticeList.add(n2);
        masterNoticeList.add(n3);

        urgentNoticeQueue.add(n1);
        urgentNoticeQueue.add(n2);
        urgentNoticeQueue.add(n3);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DigitalNoticeBoardApp().setVisible(true);
        });
    }
}