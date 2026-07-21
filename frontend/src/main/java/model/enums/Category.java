package model.enums;

public enum Category {
    ELECTRONICS("الکترونیک"),
    VEHICLE("وسیله نقلیه"),
    FURNITURE("مبلمان"),
    BOOKS("کتاب"),
    CLOTHING("پوشاک"),
    REAL_ESTATE("املاک"),
    SPORTS("ورزشی"),
    TOYS("اسباب بازی"),
    OTHER("سایر");

    private final String persianName;

    Category(String persianName) {
        this.persianName = persianName;
    }

    public String getPersianName() {
        return persianName;
    }

    public static Category fromPersianName(String persianName) {
        for (Category category : values()) {
            if (category.persianName.equals(persianName)) {
                return category;
            }
        }
        return null;
    }
}