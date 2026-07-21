package model.enums;

public enum ProductState {
    NEW("نو"),
    LIKE_NEW("در حد نو"),
    GOOD("خوب"),
    FAIR("قابل قبول"),
    DAMAGED("آسیب دیده"),
    REFURBISHED("بازسازی شده");

    private final String persianName;

    ProductState(String persianName) {
        this.persianName = persianName;
    }

    public String getPersianName() {
        return persianName;
    }

    public static ProductState fromPersianName(String persianName) {
        for (ProductState condition : values()) {
            if (condition.persianName.equals(persianName)) {
                return condition;
            }
        }
        return null;
    }
}