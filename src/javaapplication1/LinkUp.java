package javaapplication1;

import java.util.Scanner;

/**
 * LinkUp is the main class of the QuickChat application.
 * Part 1: registers a user and logs them in.
 * Part 2: lets a logged-in user send, discard or store messages.
 */
public class LinkUp {

    public static void main(String[] args) {

        // Declaration
        String Name;
        String surName;
        String userName;
        String Password;
        String phoneNumber;

        Scanner inputDevice = new Scanner(System.in);

        // Prompting the user for Name and Surname
        System.out.print("Please enter your Name: ");
        Name = inputDevice.nextLine();

        System.out.print("Please enter your Surname: ");
        surName = inputDevice.nextLine();

        // Create login object
        login Login = new login("", "", Name, surName);

        // Validating Username
        while (true) {

            System.out.print("Please enter your Username: ");
            userName = inputDevice.nextLine();

            if (Login.checkUserName(userName)) {

                System.out.println("Username successfully entered.");
                break;

            } else {

                System.out.println("Username is not correctly formatted.");
                System.out.println("Username must contain an underscore.");
                System.out.println("Username must not contain more than five characters.");
            }
        }

        // Validating Password
        while (true) {

            System.out.print("Please enter your Password: ");
            Password = inputDevice.nextLine();

            if (Login.checkUserPassword(Password)) {

                System.out.println("Password successfully captured.");
                break;

            } else {

                System.out.println("Password is not correctly formatted.");
                System.out.println("Password must contain at least eight characters.");
                System.out.println("Password must contain a capital letter.");
                System.out.println("Password must contain a number.");
                System.out.println("Password must contain a special character.");
            }
        }

        // Validating Phone Number
        while (true) {

            System.out.print("Please enter your Phone Number: ");
            phoneNumber = inputDevice.nextLine();

            if (Login.checkphoneNumber(phoneNumber)) {

                System.out.println("Phone Number successfully added.");
                break;

            } else {

                System.out.println("Phone number is incorrectly formatted.");
                System.out.println("Phone number must start with +27 and contain 12 characters.");
            }
        }

        // Registration successful
        System.out.println();
        System.out.println("You have registered successfully.");
        System.out.println("Welcome " + Name + " " + surName);

        // Store registered details
        Login = new login(userName, Password, Name, surName);

        System.out.println();
        System.out.println("Please login to your account.");

        // Loops until the credentials are correct, so the code below
        // only runs for a user who has logged in successfully
        Login.loginUser(inputDevice);

        // ===================== PART 2: QUICKCHAT =====================

        System.out.println();
        System.out.println("Welcome to QuickChat.");

        // Ask how many messages the user wants to enter
        int numberOfMessages = 0;
        while (numberOfMessages <= 0) {
            System.out.print("How many messages would you like to enter? ");
            try {
                numberOfMessages = Integer.parseInt(inputDevice.nextLine().trim());
            } catch (NumberFormatException e) {
                numberOfMessages = 0;
            }
            if (numberOfMessages <= 0) {
                System.out.println("Please enter a whole number greater than 0.");
            }
        }

        boolean running = true;
        boolean messagesEntered = false;

        // WHILE loop: keeps the application running until the user quits
        while (running) {

            System.out.println();
            System.out.println("========== QUICKCHAT MENU ==========");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.print("Choose an option: ");

            String option = inputDevice.nextLine().trim();

            switch (option) {

                case "1":
                    if (messagesEntered) {
                        System.out.println("You have already entered all "
                                + numberOfMessages + " messages.");
                        break;
                    }

                    // FOR loop: only the chosen number of messages can be entered
                    for (int i = 0; i < numberOfMessages; i++) {

                        System.out.println("\n===== Message " + (i + 1)
                                + " of " + numberOfMessages + " =====");

                        String messageID = Message.generateMessageID();

                        // Recipient: ask until the number is valid
                        String recipient;
                        while (true) {

                            System.out.print("Enter recipient cell number: ");
                            recipient = inputDevice.nextLine().trim();

                            Message check = new Message(messageID, i, recipient, "");
                            String result = check.checkRecipientCell();
                            System.out.println(result);

                            if (result.equals("Cell phone number successfully captured.")) {
                                break;
                            }
                        }

                        // Message text: ask until it is 250 characters or less
                        String messageText;
                        while (true) {

                            System.out.print("Enter your message: ");
                            messageText = inputDevice.nextLine();

                            if (messageText.length() <= 250) {
                                break;
                            }
                            System.out.println("Please enter a message of less than 250 characters.");
                        }

                        Message newMessage = new Message(messageID, i, recipient, messageText);
                        System.out.println("Message ID generated: " + messageID);
                        System.out.println("Message Hash: " + newMessage.createMessageHash());

                        // Send, disregard or store
                        System.out.println("\n1) Send Message");
                        System.out.println("2) Disregard Message");
                        System.out.println("3) Store Message to send later");

                        int choice = 0;
                        while (choice < 1 || choice > 3) {
                            System.out.print("Choose an option: ");
                            String entry = inputDevice.nextLine().trim();
                            choice = entry.matches("[123]") ? Integer.parseInt(entry) : 0;
                        }

                        System.out.println(newMessage.sentMessage(choice));

                        // Full details are shown only for messages that were sent
                        if (choice == 1) {
                            System.out.println(newMessage.getMessageDetails());
                        }
                    }

                    messagesEntered = true;
                    System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
                    break;

                case "2":
                    System.out.println("Coming Soon.");
                    break;

                case "3":
                    running = false;
                    System.out.println("Thank you for using QuickChat.");
                    break;

                default:
                    System.out.println("Invalid option. Please enter 1, 2 or 3.");
            }
        }

        inputDevice.close();
    }
}