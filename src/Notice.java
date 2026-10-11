import java.io.Serializable;
import java.time.LocalDate;

public class Notice implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String category;
    private String priority;
    private String content;
    private LocalDate expiryDate;

    public Notice(int id, String title, String category, String priority, String content, LocalDate expiryDate) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.priority = priority;
        this.content = content;
        this.expiryDate = expiryDate;
    }

    public Notice(String title, String category, String priority, String content, LocalDate expiryDate) {
        this(0, title, category, priority, content, expiryDate);
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getPriority() { return priority; }
    public String getContent() { return content; }
    public LocalDate getExpiryDate() { return expiryDate; }
}