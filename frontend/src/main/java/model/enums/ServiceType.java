package model.enums;

public enum ServiceType {
    HOURLY("ساعتی"),
    DAILY("روزانه"),
    WEEKLY("هفتگی"),
    MONTHLY("ماهانه"),
    ANNUAL("سالانه"),
    FIXED("ثابت");

    private final String persianName;

    ServiceType(String persianName) {
        this.persianName = persianName;
    }

    public String getPersianName() {
        return persianName;
    }

    public static ServiceType fromPersianName(String persianName) {
        for (ServiceType type : values()) {
            if (type.persianName.equals(persianName)) {
                return type;
            }
        }
        return null;
    }
}