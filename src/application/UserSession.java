package application;

import model.entities.Seller; // Ou User, dependendo da sua entidade de login

public class UserSession {

    private static UserSession instance;
    private Seller loggedUser;

    private UserSession(Seller user) {
        this.loggedUser = user;
    }

    public static void initSession(Seller user) {
        if (instance == null) {
            instance = new UserSession(user);
        }
    }

    public static UserSession getInstance() {
        return instance;
    }

    public Seller getLoggedUser() {
        return loggedUser;
    }

    public static void cleanSession() {
        instance = null;
    }

    public boolean isAdmin() {
        return loggedUser != null && "ADMIN".equalsIgnoreCase(loggedUser.getRole());
    }	
}