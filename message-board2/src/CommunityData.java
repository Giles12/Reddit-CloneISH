import java.util.List;

public class CommunityData {
    private static CommunityData instance;
    private String Community_Name;
    private List<String> posts;

    private CommunityData() {}

    public static CommunityData getInstance() {
        if (instance == null) {
            instance = new CommunityData();
        }
        return instance;
    }

    // Getters and Setters for your data fields
    public String getName() { return Community_Name; }
    public void setName(String Community_Name) { this.Community_Name = Community_Name; }
    public List<String> getPosts() { return posts; }
    public void setPosts(List<String> posts) { this.posts = posts; }
}
