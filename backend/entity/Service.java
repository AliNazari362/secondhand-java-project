public class Service extends Adv {
    public enum ServiceType {
        HOURLY,
        DAILY,
        WEEKLY,
        MONTHLY,
        ANNUAL,
        FIXED
    }

    private String specialCategory;   // دسته‌بندی خاص خدمات
    private double costOfPart;         // هزینه هر بخش
    private ServiceType typeOfPart;    // نوع خدمات (ساعتی، روزانه، ...)

    public Service() {
        super();
        this.setAdvType(AdvType.SERVICE);
    }

    public Service(String description, User user, String fullName, City city,
                   String specialCategory, double costOfPart, ServiceType typeOfPart) {
        super(description, AdvType.SERVICE, user, fullName, city);
        this.specialCategory = specialCategory;
        this.costOfPart = costOfPart;
        this.typeOfPart = typeOfPart;
    }

    // Getter و Setter
    public String getSpecialCategory() { return specialCategory; }
    public void setSpecialCategory(String specialCategory) { this.specialCategory = specialCategory; }

    public double getCostOfPart() { return costOfPart; }
    public void setCostOfPart(double costOfPart) { this.costOfPart = costOfPart; }

    public ServiceType getTypeOfPart() { return typeOfPart; }
    public void setTypeOfPart(ServiceType typeOfPart) { this.typeOfPart = typeOfPart; }

    @Override
    public String toString() {
        return "Service{" +
                "id=" + getId() +
                ", fullName='" + getFullName() + '\'' +
                ", specialCategory='" + specialCategory + '\'' +
                ", costOfPart=" + costOfPart +
                ", typeOfPart=" + typeOfPart +
                ", status=" + getStatus() +
                '}';
    }
}