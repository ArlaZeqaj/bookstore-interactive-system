package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Roles {
    public static final String CREATE_BILL = "Create Bill";
    public static final String ADD_NEW_BOOKS = "Add new books";
    public static final String EDIT_BOOKS = "Edit books";
    public static final String DELETE_BOOKS = "Update books";
    public static final String VIEW_STATISTICS = "View statistics";
    private static List<String> librarianRoles = new ArrayList<>();
    private static List<String> managerRoles = new ArrayList<>();

    public static List<String> getLibrarianRoles() {
        addRole(librarianRoles, CREATE_BILL);
        return librarianRoles;
    }
    public static List<String> getManagerRoles() {
        addRole(librarianRoles, CREATE_BILL);
        addRole(librarianRoles, ADD_NEW_BOOKS);
        addRole(librarianRoles, EDIT_BOOKS);
        addRole(librarianRoles, DELETE_BOOKS);
        addRole(librarianRoles, VIEW_STATISTICS);
        return managerRoles;
    }
    public static boolean roleExists(List<String> list, String role) {
        return list.contains(role);
    }
    public static void addRole(List<String> list, String role) {
        list.add(role);
    }
}
