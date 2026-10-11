import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class NoticeClientApp extends JFrame {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;

    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private JLabel tickerLabel;

    private JTextField txtTitle;
    private JComboBox<String> comboCategory;
    private JComboBox<String> comboPriority;
    private JTextArea txtContent;
    private JTextField txtExpiryDate;

    private JTable noticeTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> comboFilterCategory;

    public NoticeClientApp() {
        setTitle("Institutional Digital Notice Board - Phase I & II Integrated System");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createHeaderAndTickerPanel(), BorderLayout.NORTH);
        add(createMainCardPanel(), BorderLayout.CENTER);
        add(createNavigationPanel(), BorderLayout.SOUTH);

        refreshNoticeTable("ALL");
    }

    private JPanel createHeaderAndTickerPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 41, 59));

        JLabel titleLabel = new JLabel("INSTITUTIONAL DIGITAL NOTICE BOARD SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JPanel tickerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tickerPanel.setBackground(new Color(225, 29, 72));

        JLabel tickerTitle = new JLabel(" LATEST ANNOUNCEMENT: ");
        tickerTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        tickerTitle.setForeground(Color.WHITE);

        tickerLabel = new JLabel("Connecting to Institutional Server...");
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
        JButton btnViewDisplay = new JButton("View Public Notice Board");
        JButton btnViewAdmin = new JButton("Open Admin Panel");

        btnViewDisplay.addActionListener(e -> {
            refreshNoticeTable((String) comboFilterCategory.getSelectedItem());
            cardLayout.show(mainContentPanel, "DISPLAY_PANEL");
        });
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

        comboFilterCategory = new JComboBox<>(new String[]{"ALL", "Administration", "Compliance", "Events", "IT & Systems", "General"});
        comboFilterCategory.addActionListener(e -> refreshNoticeTable((String) comboFilterCategory.getSelectedItem()));
        filterPanel.add(comboFilterCategory);

        String[] columns = {"ID", "Title", "Category", "Priority", "Content", "Expiry Date"};
        tableModel = new DefaultTableModel(columns, 0);

        noticeTable = new JTable(tableModel);
        noticeTable.setRowHeight(26);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(noticeTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(" Post Institutional Announcement "));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Notice Title:"), gbc);
        gbc.gridx = 1; txtTitle = new JTextField(25); formPanel.add(txtTitle, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; comboCategory = new JComboBox<>(new String[]{"Administration", "Compliance", "Events", "IT & Systems", "General"}); formPanel.add(comboCategory, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Priority Level:"), gbc);
        gbc.gridx = 1; comboPriority = new JComboBox<>(new String[]{"HIGH", "MEDIUM", "LOW"}); formPanel.add(comboPriority, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Expiry Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; txtExpiryDate = new JTextField(LocalDate.now().plusDays(7).toString()); formPanel.add(txtExpiryDate, gbc);

        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Notice Content:"), gbc);
        gbc.gridx = 1; txtContent = new JTextArea(4, 25); formPanel.add(new JScrollPane(txtContent), gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        JButton btnSubmit = new JButton("Publish Notice to Server");
        btnSubmit.addActionListener(e -> publishNoticeToServer());
        formPanel.add(btnSubmit, gbc);

        panel.add(formPanel, BorderLayout.NORTH);
        return panel;
    }

    private void publishNoticeToServer() {
        try {
            String title = txtTitle.getText().trim();
            String category = (String) comboCategory.getSelectedItem();
            String priority = (String) comboPriority.getSelectedItem();
            String content = txtContent.getText().trim();
            LocalDate expiry = LocalDate.parse(txtExpiryDate.getText().trim());

            Notice notice = new Notice(title, category, priority, content, expiry);

            try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.writeObject("ADD_NOTICE");
                out.writeObject(notice);

                String response = (String) in.readObject();
                if ("SUCCESS".equals(response)) {
                    JOptionPane.showMessageDialog(this, "Notice saved to SQL Database via Server!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    txtTitle.setText("");
                    txtContent.setText("");
                    refreshNoticeTable("ALL");
                    cardLayout.show(mainContentPanel, "DISPLAY_PANEL");
                }
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Date Format.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Server Connection Failed: " + ex.getMessage(), "Network Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private void refreshNoticeTable(String filterCategory) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject("FETCH_NOTICES");
            List<Notice> list = (List<Notice>) in.readObject();

            tableModel.setRowCount(0);
            for (Notice n : list) {
                if (filterCategory.equals("ALL") || n.getCategory().equalsIgnoreCase(filterCategory)) {
                    tableModel.addRow(new Object[]{n.getId(), n.getTitle(), n.getCategory(), n.getPriority(), n.getContent(), n.getExpiryDate()});
                }
            }

            if (!list.isEmpty()) {
                Notice latest = list.get(0);
                tickerLabel.setText("[" + latest.getCategory() + "] " + latest.getTitle() + " - " + latest.getContent());
            }

        } catch (Exception e) {
            tickerLabel.setText("Offline Mode - Cannot connect to NoticeServer");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NoticeClientApp().setVisible(true));
    }
}