package javaapplication1;

import java.io.File;import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Message {

    // Details of this message
    private String messageID;
    private int messageNumber;
    private String sender;
    private String recipient;
    private String message;
    private String messageHash;

    // The logged-in user: every new message records them as its sender
    private static String currentSender = "Unknown";

    // Arrays of messages, shared across the whole program run
    private static ArrayList<Message> sentMessages = new ArrayList<>();
    private static ArrayList<Message> disregardedMessages = new ArrayList<>();
    private static ArrayList<Message> storedMessages = new ArrayList<>();

    // Parallel arrays: position i in each belongs to the same message
    private static ArrayList<String> messageIDs = new ArrayList<>();
    private static ArrayList<String> messageHashes = new ArrayList<>();
    private static ArrayList<Message> allMessages = new ArrayList<>();

    // Total number of messages sent
    private static int totalMessages = 0;

    // File that stored messages are written to and read from
    private static String jsonFile = "messages.json";

    // CONSTRUCTORS
    public Message(String messageID, int messageNumber,
            String recipient, String message) {

        this(messageID, messageNumber, recipient, message, currentSender);
    }

    public Message(String messageID, int messageNumber,
            String recipient, String message, String sender) {

        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.message = message;
        this.sender = sender;
        this.messageHash = createMessageHash();
    }

    // GETTERS
    public String getMessageID() {
        return messageID;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
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
                addToArrays();
                totalMessages++;
                return "Message successfully sent.";

            case 2:
                disregardedMessages.add(this);
                addToArrays();
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

    // Adds this message to the parallel arrays (once only)
    private void addToArrays() {

        if (!allMessages.contains(this)) {
            allMessages.add(this);
            messageIDs.add(messageID);
            messageHashes.add(messageHash);
        }
    }

    // Removes this message from the parallel arrays
    private void removeFromArrays() {

        int index = allMessages.indexOf(this);

        if (index >= 0) {
            allMessages.remove(index);
            messageIDs.remove(index);
            messageHashes.remove(index);
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

    // ===================== ARRAYS AND SEARCHES =====================

    // Sets who is logged in, so new messages record the correct sender
    public static void setSender(String loggedInUser) {
        currentSender = loggedInUser;
    }

    // Changes the file used for storing messages 
    public static void setJsonFile(String fileName) {
        jsonFile = fileName;
    }

    // Empties every array and the total 
    public static void clearAll() {

        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageIDs.clear();
        messageHashes.clear();
        allMessages.clear();
        totalMessages = 0;
    }

    // Copies of the arrays, so other classes cannot change the originals
    public static ArrayList<Message> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public static ArrayList<Message> getDisregardedMessages() {
        return new ArrayList<>(disregardedMessages);
    }

    public static ArrayList<Message> getStoredMessages() {
        return new ArrayList<>(storedMessages);
    }

    public static ArrayList<String> getMessageIDs() {
        return new ArrayList<>(messageIDs);
    }

    public static ArrayList<String> getMessageHashes() {
        return new ArrayList<>(messageHashes);
    }

    // The text of every sent message
    public static ArrayList<String> getSentMessageTexts() {

        ArrayList<String> texts = new ArrayList<>();

        for (Message sent : sentMessages) {
            texts.add(sent.message);
        }

        return texts;
    }

    // a) Sender and recipient of all stored messages
    public static String displaySenderAndRecipient() {

        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }

        StringBuilder lines = new StringBuilder();

        for (Message stored : storedMessages) {
            lines.append("Sender: ").append(stored.sender)
                    .append(" | Recipient: ").append(stored.recipient)
                    .append("\n");
        }

        return lines.toString();
    }

    // b) The longest stored message
    public static String getLongestMessage() {

        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }

        Message longest = storedMessages.get(0);

        for (Message stored : storedMessages) {
            if (stored.message.length() > longest.message.length()) {
                longest = stored;
            }
        }

        return longest.message;
    }

    // c) Search for a message ID using the parallel arrays 
    public static Message searchByMessageID(String id) {

        int index = messageIDs.indexOf(id);

        if (index >= 0) {
            return allMessages.get(index);
        }

        return null;
    }

    // d) All sent or stored messages for one recipient
    public static ArrayList<String> searchByRecipient(String recipientNumber) {

        ArrayList<String> found = new ArrayList<>();

        for (Message sent : sentMessages) {
            if (sent.recipient.equals(recipientNumber)) {
                found.add(sent.message);
            }
        }

        for (Message stored : storedMessages) {
            if (stored.recipient.equals(recipientNumber)) {
                found.add(stored.message);
            }
        }

        return found;
    }

    // e) Delete a stored message using its message hash
    public static String deleteByMessageHash(String hash) {

        int index = messageHashes.indexOf(hash);

        if (index >= 0) {

            Message found = allMessages.get(index);

            if (storedMessages.contains(found)) {
                storedMessages.remove(found);
                found.removeFromArrays();
                saveStoredMessagesToFile();
                return "Message \"" + found.message + "\" successfully deleted.";
            }
        }

        return "No stored message was found with that hash.";
    }

    // f) Report listing the full details of every message in the given array
    public static String getReport(List<Message> messages) {

        if (messages.isEmpty()) {
            return "There are no messages to display.";
        }

        StringBuilder report = new StringBuilder();

        for (Message m : messages) {
            report.append("==============================\n")
                    .append("Message ID: ").append(m.messageID).append("\n")
                    .append("Message Hash: ").append(m.messageHash).append("\n")
                    .append("Sender: ").append(m.sender).append("\n")
                    .append("Recipient: ").append(m.recipient).append("\n")
                    .append("Message: ").append(m.message).append("\n");
        }

        report.append("==============================");

        return report.toString();
    }

    // ===================== JSON: STORING AND READING =====================

    // STORE MESSAGE: adds this message to the stored array and saves the file
    public boolean storeMessage() {

        storedMessages.add(this);
        addToArrays();

        return saveStoredMessagesToFile();
    }

    // Writes every stored message to the JSON file
    // JSON layout follows the format described at https://www.json.org
    private static boolean saveStoredMessagesToFile() {

        StringBuilder json = new StringBuilder("[\n");

        for (int i = 0; i < storedMessages.size(); i++) {

            Message m = storedMessages.get(i);

            json.append("  {\n");
            json.append("    \"messageID\": \"").append(escapeJson(m.messageID)).append("\",\n");
            json.append("    \"messageNumber\": ").append(m.messageNumber).append(",\n");
            json.append("    \"messageHash\": \"").append(escapeJson(m.messageHash)).append("\",\n");
            json.append("    \"sender\": \"").append(escapeJson(m.sender)).append("\",\n");
            json.append("    \"recipient\": \"").append(escapeJson(m.recipient)).append("\",\n");
            json.append("    \"message\": \"").append(escapeJson(m.message)).append("\"\n");
            json.append("  }");

            if (i < storedMessages.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("]");

        try {
            Files.write(new File(jsonFile).toPath(),
                    json.toString().getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Reads the JSON file back into the stored messages array.
    // Returns how many messages were loaded.
    // Reading a text file line by line with Files.readAllLines follows the Oracle
    // Java Tutorials (docs.oracle.com/javase/tutorial/essential/io/file.html);
    // the JSON layout and escape rules follow https://www.json.org
    public static int loadStoredMessages() {

        // Forget stored messages already in memory so none are loaded twice
        for (Message old : storedMessages) {
            old.removeFromArrays();
        }
        storedMessages.clear();

        File file = new File(jsonFile);

        if (!file.exists()) {
            return 0;
        }

        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

            String id = "";
            int number = 0;
            String senderName = "";
            String recipientNumber = "";

            for (String line : lines) {

                String trimmed = line.trim();

                if (trimmed.startsWith("\"messageID\"")) {
                    id = readTextValue(trimmed);
                } else if (trimmed.startsWith("\"messageNumber\"")) {
                    number = Integer.parseInt(readRawValue(trimmed));
                } else if (trimmed.startsWith("\"sender\"")) {
                    senderName = readTextValue(trimmed);
                } else if (trimmed.startsWith("\"recipient\"")) {
                    recipientNumber = readTextValue(trimmed);
                } else if (trimmed.startsWith("\"message\"")) {
                    // "message" is the last field, so the message is now complete
                    Message loaded = new Message(id, number, recipientNumber,
                            readTextValue(trimmed), senderName);
                    storedMessages.add(loaded);
                    loaded.addToArrays();
                }
            }
        } catch (IOException | NumberFormatException e) {
            
        }

        return storedMessages.size();
    }

    // Returns what comes after the colon on a JSON line, without the trailing comma
    private static String readRawValue(String line) {

        String value = line.substring(line.indexOf(':') + 1).trim();

        if (value.endsWith(",")) {
            value = value.substring(0, value.length() - 1);
        }

        return value;
    }

    // Same as readRawValue, but removes the quotes and undoes the escaping
    private static String readTextValue(String line) {

        String value = readRawValue(line);

        return unescapeJson(value.substring(1, value.length() - 1));
    }

    // Makes text safe to place inside a JSON string
    private static String escapeJson(String text) {

        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Turns escaped JSON text (\" \\ \n \r \t) back into normal text
    private static String unescapeJson(String text) {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {

            char current = text.charAt(i);

            if (current == '\\' && i + 1 < text.length()) {

                i++;
                char next = text.charAt(i);

                switch (next) {
                    case 'n':
                        result.append('\n');
                        break;
                    case 'r':
                        result.append('\r');
                        break;
                    case 't':
                        result.append('\t');
                        break;
                    default:
                        result.append(next);   // handles \" and \\
                }

            } else {
                result.append(current);
            }
        }

        return result.toString();
    }
}