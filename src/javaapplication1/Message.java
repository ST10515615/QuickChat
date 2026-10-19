package javaapplication1;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class Message {

    // Details of this message
    private String messageID;
    private int messageNumber;
    private String recipient;
    private String message;
    private String messageHash;

    // Messages shared across the whole program run
    private static ArrayList<Message> sentMessages = new ArrayList<>();
    private static ArrayList<Message> disregardedMessages = new ArrayList<>();
    private static ArrayList<Message> storedMessages = new ArrayList<>();

    // Total number of messages sent
    private static int totalMessages = 0;

    // File that stored messages are written to
    private static final String JSON_FILE = "messages.json";

    // CONSTRUCTOR
    public Message(String messageID, int messageNumber,
            String recipient, String message) {

        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.message = message;
        this.messageHash = createMessageHash();
    }

    // GENERATE MESSAGE ID: a random ten-digit number built digit by digit
    public static String generateMessageID() {

        Random random = new Random();
        String id = "";

        for (int i = 0; i < 10; i++) {
            id = id + random.nextInt(10);
        }

        return id;
    }

    // CHECK MESSAGE ID: must not be more than ten characters
    public boolean checkMessageID() {

        return messageID != null && messageID.length() <= 10;
    }

    // CHECK RECIPIENT CELL: starts with +27 and is 12 characters long
    public String checkRecipientCell() {

        if (recipient.startsWith("+27") && recipient.length() == 12) {
            return "Cell phone number successfully captured.";
        }

        return "Cell phone number is incorrectly formatted or does not "
                + "contain an international code. Please correct the "
                + "number and try again.";
    }

    // CHECK MESSAGE LENGTH: maximum of 250 characters
    public String checkMessageLength() {

        if (message.length() <= 250) {
            return "Message ready to send.";
        }

        int extra = message.length() - 250;
        return "Message exceeds 250 characters by " + extra
                + "; please reduce the size.";
    }

    // CREATE MESSAGE HASH: first two ID digits : message number : FIRSTLAST
    public String createMessageHash() {

        // First two digits of the message ID (substring)
        String firstTwoNumbers = messageID;
        if (messageID.length() >= 2) {
            firstTwoNumbers = messageID.substring(0, 2);
        }

        String firstWord = "";
        String lastWord = "";

        if (message != null && !message.trim().isEmpty()) {

            // Split the message into words on spaces
            String[] words = message.trim().split("\\s+");

            // Keep only letters and numbers (removes , ? ! etc.)
            firstWord = words[0].replaceAll("[^a-zA-Z0-9]", "");
            lastWord = words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "");
        }

        return (firstTwoNumbers + ":" + messageNumber + ":"
                + firstWord + lastWord).toUpperCase();
    }

    // SENT MESSAGE: 1 = send, 2 = disregard, 3 = store
    public String sentMessage(int choice) {

        switch (choice) {

            case 1:
                sentMessages.add(this);
                totalMessages++;
                return "Message successfully sent.";

            case 2:
                disregardedMessages.add(this);
                return "Press 0 to delete the message.";

            case 3:
                if (storeMessage()) {
                    return "Message successfully stored.";
                }
                return "Message could not be stored.";

            default:
                return "Invalid option.";
        }
    }

    // GET MESSAGE DETAILS: Message ID, Message Hash, Recipient, Message
    public String getMessageDetails() {

        return "\n==============================\n"
                + "Message ID: " + messageID + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + message + "\n"
                + "==============================";
    }

    // PRINT MESSAGES: all messages sent while the program is running
    public static String printMessages() {

        if (sentMessages.isEmpty()) {
            return "No messages have been sent.";
        }

        String all = "";
        for (Message sent : sentMessages) {
            all = all + sent.getMessageDetails() + "\n";
        }

        return all;
    }

    // RETURN TOTAL MESSAGES: how many messages have been sent
    public static int returnTotalMessages() {

        return totalMessages;
    }

    // STORE MESSAGE: adds this message to the stored list and writes
    // every stored message to messages.json
    // JSON layout follows the format described at https://www.json.org
    public boolean storeMessage() {

        storedMessages.add(this);

        StringBuilder json = new StringBuilder("[\n");

        for (int i = 0; i < storedMessages.size(); i++) {

            Message m = storedMessages.get(i);

            json.append("  {\n");
            json.append("    \"messageID\": \"").append(escapeJson(m.messageID)).append("\",\n");
            json.append("    \"messageNumber\": ").append(m.messageNumber).append(",\n");
            json.append("    \"messageHash\": \"").append(escapeJson(m.messageHash)).append("\",\n");
            json.append("    \"recipient\": \"").append(escapeJson(m.recipient)).append("\",\n");
            json.append("    \"message\": \"").append(escapeJson(m.message)).append("\"\n");
            json.append("  }");

            if (i < storedMessages.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("]");

        try (FileWriter writer = new FileWriter(JSON_FILE)) {
            writer.write(json.toString());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Makes text safe to place inside a JSON string
    private static String escapeJson(String text) {

        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}