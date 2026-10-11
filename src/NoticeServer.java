import java.io.*;
import java.net.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NoticeServer {
    private static final int PORT = 5000;
    
    // DB Config - Update DB URL, user, and pass according to your MySQL setup
    private static final String DB_URL = "jdbc:mysql://localhost:3306/notice_board_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "mkiopl"; // adjust password if needed

    public static void main(String[] args) {
        System.out.println("Starting Notice Board Server on Port " + PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server running. Waiting for client connections...");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server Exception: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                String command = (String) in.readObject();

                if ("FETCH_NOTICES".equals(command)) {
                    List<Notice> list = fetchNoticesFromDB();
                    out.writeObject(list);
                } else if ("ADD_NOTICE".equals(command)) {
                    Notice notice = (Notice) in.readObject();
                    boolean success = saveNoticeToDB(notice);
                    out.writeObject(success ? "SUCCESS" : "FAILURE");
                }
            } catch (Exception e) {
                System.err.println("Client Handler Error: " + e.getMessage());
            }
        }

        // JDBC CRUD: FETCH
        private List<Notice> fetchNoticesFromDB() {
            List<Notice> list = new ArrayList<>();
            String query = "SELECT * FROM notices ORDER BY id DESC";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    list.add(new Notice(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("category"),
                            rs.getString("priority"),
                            rs.getString("content"),
                            rs.getDate("expiry_date").toLocalDate()
                    ));
                }
            } catch (SQLException e) {
                System.err.println("JDBC Fetch Error: " + e.getMessage());
            }
            return list;
        }

        // JDBC CRUD: INSERT
        private boolean saveNoticeToDB(Notice notice) {
            String query = "INSERT INTO notices (title, category, priority, content, expiry_date) VALUES (?, ?, ?, ?, ?)";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, notice.getTitle());
                pstmt.setString(2, notice.getCategory());
                pstmt.setString(3, notice.getPriority());
                pstmt.setString(4, notice.getContent());
                pstmt.setDate(5, Date.valueOf(notice.getExpiryDate()));

                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("JDBC Save Error: " + e.getMessage());
                return false;
            }
        }
    }
}