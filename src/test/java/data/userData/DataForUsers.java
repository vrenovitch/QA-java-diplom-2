package data.userData;

public class DataForUsers {

    public static int generateRandomNumber() {
        return (int) (Math.random() * 10000) + 1;
    }

    public static String generateRandomMail() {
        int number = generateRandomNumber();
        return "amongus" + number + "@ya.ru";
    }

    public static String generateRandomPass() {
        int number = generateRandomNumber();
        return "pass" + number;
    }

    public static String generateRandomName() {
        String firstName;
        int a = (int) (Math.random() * 5) + 1;
        if (a == 1) {
            firstName = "Borov";
        } else if (a == 2) {
            firstName = "Bivis";
        } else if (a == 3) {
            firstName = "Kizyak";
        } else if (a == 4) {
            firstName = "Jma";
        } else {
            firstName = "Shpikachka";
        }
        return firstName;
    }

    public class UsersContants {
        public static final String CREATE_USER = "/api/auth/register";
        public static final String LOGIN_USER = "/api/auth/login";
        public static final String USER = "/api/auth/user";
    }
}
