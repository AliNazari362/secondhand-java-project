public class Option {
    private String option;   // نام ویژگی (مثلاً "Ram")
    private String value;    // مقدار ویژگی (مثلاً "8GB")

    public Option() {}

    public Option(String option, String value) {
        this.option = option;
        this.value = value;
    }

    public String getOption() { return option; }
    public void setOption(String option) { this.option = option; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    @Override
    public String toString() {
        return option + " : " + value;
    }
}