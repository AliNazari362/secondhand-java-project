public class Image {
    private String path;   // مسیر فایل تصویر

    public Image() {}

    public Image(String path) {
        this.path = path;
    }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    @Override
    public String toString() {
        return "Image{path='" + path + "'}";
    }
}