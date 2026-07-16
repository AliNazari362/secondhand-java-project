package model;

public class OptionDto {
    private String option;
    private String value;

    public OptionDto() {}

    public OptionDto(String option, String value) {
        this.option = option;
        this.value = value;
    }

    // ---------- Getters & Setters ----------
    public String getOption() { return option; }
    public void setOption(String option) { this.option = option; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}