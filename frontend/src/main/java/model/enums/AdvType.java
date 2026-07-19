package model.enums;

public enum AdvType {
    PRODUCT {
        @Override
        public String getLabel() {
            return "محصول";
        }
    },
    SERVICE {
        @Override
        public String getLabel() {
            return "خدمت";
        }
    };

    public abstract String getLabel();
}