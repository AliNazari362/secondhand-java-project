package model.enums;

public enum AdvStatus {
    PENDING {
        @Override
        public String getLabel() {
            return "در انتظار تایید";
        }
    },
    ACTIVE {
        @Override
        public String getLabel() {
            return "فعال";
        }
    },
    REJECTED {
        @Override
        public String getLabel() {
            return "تعلیق شده";
        }
    },
    SOLD {
        @Override
        public String getLabel() {
            return "غیرفعال";
        }
    },
    DELETED {
        @Override
        public String getLabel() {
            return "حذف شده";
        }
    };

    public abstract String getLabel();
}