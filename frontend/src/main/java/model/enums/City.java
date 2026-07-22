package model.enums;

public enum City {

    TEHRAN("تهران"),
    ISFAHAN("اصفهان"),
    SHIRAZ("شیراز"),
    MASHHAD("مشهد"),
    TABRIZ("تبریز"),
    AHVAZ("اهواز"),
    KERMAN("کرمان"),
    RASHT("رشت"),
    YAZD("یزد"),
    QOM("قم"),
    KARAJ("کرج"),
    OTHER("سایر");

    private final String persianName;

    City(String persianName) {
        this.persianName = persianName;
    }

    public String getPersianName() {
        return persianName;
    }

    public static City fromPersianName(String persianName) {
        for (City city : values()) {
            if (city.persianName.equals(persianName)) {
                return city;
            }
        }
        return null;
    }

}