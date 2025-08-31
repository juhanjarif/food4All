package model;

public class SessionManager {

    private static User currentUser;

    // set the current logged in user details
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    // get the current logged in user details
    public static User getCurrentUser() {
        return currentUser;
    }

    // logout
    public static void clearSession() {
        currentUser = null;
    }

    // check if user logged in
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
