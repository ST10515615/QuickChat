package javaapplication1;

import java.util.Scanner;


public class login {

    // Variables to store registered details
    private String storedUsername;
    private String storedPassworg;
    private String storedName;
    private String storedSurname;

    // Constructor
    public login(String registeredUsername, String registeredPassword,
            String Name, String surName) {

        storedUsername = registeredUsername;
        storedPassworg = registeredPassword;
        storedName = Name;
        storedSurname = surName;
    }

    // Check Username
    public boolean checkUserName(String userName) {

        return userName.length() <= 5
                && userName.contains("_");
    }

    // Check Password
    public boolean checkUserPassword(String Password) {

        // Password must contain at least 8 characters
        if (Password.length() < 8) {
            return false;
        }

        boolean hasCapitalLetter = false;
        boolean hasSpecialCharacter = false;
        boolean hasNumber = false;

        // Check each character in the password
        for (int i = 0; i < Password.length(); i++) {

            char ch = Password.charAt(i);

            // Check for capital letter
            if (Character.isUpperCase(ch)) {
                hasCapitalLetter = true;
            }

            // Check for number
            if (Character.isDigit(ch)) {
                hasNumber = true;
            }

            // Check for special character
            if (!Character.isLetterOrDigit(ch)) {
                hasSpecialCharacter = true;
            }
        }

        return hasCapitalLetter
                && hasSpecialCharacter
                && hasNumber;
    }

    // Check Phone Number
    public boolean checkphoneNumber(String phoneNumber) {

        return phoneNumber.startsWith("+27")
                && phoneNumber.length() == 12;
    }

    // Register User
    public String registerUser(String userName, String Password,
            String phoneNumber, String Name, String surName) {

        // Check username
        if (!checkUserName(userName)) {

            return "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.";
        }

        // Check password
        if (!checkUserPassword(Password)) {

            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }

        // Check phone number
        if (!checkphoneNumber(phoneNumber)) {

            return "Cell phone number incorrectly formatted or does not contain international code.";
        }

        return "Registration successful.";
    }

    // Login Method (console version, used by LinkUp)
    public void loginUser(Scanner inputDevice) {

        boolean loginSuccessful = false;

        System.out.println();
        System.out.println("======= LOGIN PAGE =======");

        while (!loginSuccessful) {

            System.out.print("Please enter your username: ");
            String enteredUsername = inputDevice.nextLine();

            System.out.print("Please enter your password: ");
            String enteredPassword = inputDevice.nextLine();

            // Check username and password
            if (enteredUsername.equals(storedUsername)
                    && enteredPassword.equals(storedPassworg)) {

                System.out.println("Login successful!");

                loginSuccessful = true;

                System.out.println("Welcome back " + storedName + " "
                        + storedSurname
                        + ", it is great to see you again.");

            } else {

                System.out.println("Username or password is incorrect.");
                System.out.println("Try again.");
            }
        }
    }

    // Login Method (testable version: takes credentials, returns true/false)
    public boolean loginUser(String enteredUsername, String enteredPassword) {

        return enteredUsername.equals(storedUsername)
                && enteredPassword.equals(storedPassworg);
    }

    // Return Login Status (told whether the login worked)
    public String returnLoginStatus(String loginUser,
            boolean loginSuccessful) {

        if (loginSuccessful) {

            return "Welcome back " + storedName + " "
                    + storedSurname
                    + ", it is great to see you again.";

        } else {

            return "Username or password incorrect, please try again.";
        }
    }

    // Return Login Status (checks the credentials itself)
    public String returnLoginStatus(String enteredUsername, String enteredPassword) {

        if (loginUser(enteredUsername, enteredPassword)) {

            return "Welcome " + storedName + " " + storedSurname
                    + " it is great to see you again.";

        } else {

            return "Username or password incorrect, please try again.";
        }
    }
}