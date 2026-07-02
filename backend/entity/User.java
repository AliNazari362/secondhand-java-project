import java.util.ArrayList;
import java.util.List;

public class User extends Person {
    private UserStatus userStatus;
    private String fullName;
    private List<Adv> favorites;      // آگهی‌های علاقه‌مندی
    private List<Chatroom> rooms;     // چت‌روم‌های کاربر
    private List<Adv> userAdv;        // آگهی‌هایی که کاربر ایجاد کرده

    public User() {
        super();
        this.favorites = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.userAdv = new ArrayList<>();
    }

    public User(String username, String password, String email, String phoneNumber,
                UserType userType, UserStatus userStatus, String fullName) {
        super(username, password, email, phoneNumber, userType);
        this.userStatus = userStatus;
        this.fullName = fullName;
        this.favorites = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.userAdv = new ArrayList<>();
    }

    // Getter و Setter
    public UserStatus getUserStatus() { return userStatus; }
    public void setUserStatus(UserStatus userStatus) { this.userStatus = userStatus; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public List<Adv> getFavorites() { return favorites; }
    public void setFavorites(List<Adv> favorites) { this.favorites = favorites; }
    public void addFavorite(Adv adv) { this.favorites.add(adv); }
    public void removeFavorite(Adv adv) { this.favorites.remove(adv); }

    public List<Chatroom> getRooms() { return rooms; }
    public void setRooms(List<Chatroom> rooms) { this.rooms = rooms; }
    public void addRoom(Chatroom room) { this.rooms.add(room); }

    public List<Adv> getUserAdv() { return userAdv; }
    public void setUserAdv(List<Adv> userAdv) { this.userAdv = userAdv; }
    public void addUserAdv(Adv adv) { this.userAdv.add(adv); }
    public void removeUserAdv(Adv adv) { this.userAdv.remove(adv); }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", fullName='" + fullName + '\'' +
                ", userStatus=" + userStatus +
                ", userType=" + getUserType() +
                ", favoritesCount=" + favorites.size() +
                ", roomsCount=" + rooms.size() +
                ", userAdvCount=" + userAdv.size() +
                '}';
    }
}