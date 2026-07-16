package model.request;

public class ImageRequest {
    private String path;

    public ImageRequest() {}

    public ImageRequest(String path) {
        this.path = path;
    }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}