import java.util.List;

public class PostData {
    private static PostData instance;
    private String title;
    private String body;
    private int p_id;
    private List<String> comments;

    private PostData() {}

    public static PostData getInstance() {
        if (instance == null) {
            instance = new PostData();
        }
        return instance;
    }

    // Getters and Setters for your data fields
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public int getP_id() { return p_id; }
    public void setP_id(int p_id) { this.p_id = p_id; }
    public List<String> getComments() { return comments; }
    public void setComments(List<String> comments) { this.comments = comments; }
}
