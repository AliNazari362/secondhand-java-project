package model;

public class OptionResponseDto {
    private Long id;
    private String option;
    private String value;

    public OptionResponseDto() {}

    public OptionResponseDto(Long id, String option, String value) {
        this.id = id;
        this.option = option;
        this.value = value;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOption() { return option; }
    public void setOption(String option) { this.option = option; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}