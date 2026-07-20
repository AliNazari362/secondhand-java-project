package utils;

public enum Pages {
    AD_DETAIL {
        @Override
        public String getTitle() {
            return "جزییات آگهی";
        }

        @Override
        public String getRoot() {
            return "ad-detail";
        }
    },
    USERS_LIST {
        @Override
        public String getTitle() {
            return "نمایش کاربران";
        }

        @Override
        public String getRoot() {
            return "users-list";
        }
    },
    CHAT {
        @Override
        public String getTitle() {
            return "گفت و گو";
        }

        @Override
        public String getRoot() {
            return "chat";
        }
    },
    EDIT_AD {
        @Override
        public String getTitle() {
            return "ویرایش آگهی";
        }

        @Override
        public String getRoot() {
            return "edit-ad";
        }
    },
    NEW_AD {
        @Override
        public String getTitle() {
            return "آگهی جدید";
        }

        @Override
        public String getRoot() {
            return "new-ad";
        }
    },
    LIST_ADS {
        @Override
        public String getTitle() {
            return "لیست آگهی ها";
        }

        @Override
        public String getRoot() {
            return "ads-list";
        }
    },
    LOGIN {
        @Override
        public String getTitle() {
            return "ورود به سامانه";
        }

        @Override
        public String getRoot() {
            return "login";
        }
    },
    REGISTER {
        @Override
        public String getTitle() {
            return "ثبت نام در سامانه";
        }

        @Override
        public String getRoot() {
            return "register";
        }
    };

    public abstract String getTitle();
    public abstract String getRoot();

}
