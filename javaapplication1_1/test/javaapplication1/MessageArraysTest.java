package javaapplication1;

import java.io.File;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the Part 3 arrays and searches, using the Part 3 test data.
 * The tests use their own JSON file so they never touch messages.json.
 */
public class MessageArraysTest {

    private static final String TEST_FILE = "test_messages.json";

    private static final String TEXT_1 = "Did you get the cake?";
    private static final String TEXT_2 = "Where are you? You are late! I have asked you to be on time.";
    private static final String TEXT_3 = "Yohoooo, I am at your gate.";
    private static final String TEXT_4 = "It is dinner time!";
    private static final String TEXT_5 = "Ok, I am leaving without you.";

    private Message message1;
    private Message message2;
    private Message message3;
    private Message message4;
    private Message message5;

    @Before
    public void setUp() {
        Message.setJsonFile(TEST_FILE);
        Message.clearAll();
        Message.setSender("TestUser");

        message1 = new Message("1234567890", 0, "+27834557896", TEXT_1);
        message2 = new Message("2345678901", 1, "+27838884567", TEXT_2);
        message3 = new Message("3456789012", 2, "+27834484567", TEXT_3);
        message4 = new Message("0838884567", 3, "0838884567", TEXT_4);
        message5 = new Message("5678901234", 4, "+27838884567", TEXT_5);
    }

    @After
    public void tearDown() {
        new File(TEST_FILE).delete();
        Message.setJsonFile("messages.json");
    }

    // Test messages 1-4 with their flags: Sent, Stored, Disregard, Sent
    private void populateMessages1To4() {
        message1.sentMessage(1);
        message2.sentMessage(3);
        message3.sentMessage(2);
        message4.sentMessage(1);
    }

    @Test
    public void testSentMessagesArrayPopulated() {
        populateMessages1To4();

        assertEquals(Arrays.asList(TEXT_1, TEXT_4), Message.getSentMessageTexts());
    }

    @Test
    public void testDisregardedMessagesArrayPopulated() {
        populateMessages1To4();

        assertEquals(1, Message.getDisregardedMessages().size());
        assertEquals(TEXT_3, Message.getDisregardedMessages().get(0).getMessage());
    }

    @Test
    public void testMessageIDAndHashArraysPopulated() {
        populateMessages1To4();

        assertEquals(4, Message.getMessageIDs().size());
        assertEquals(4, Message.getMessageHashes().size());
        assertTrue(Message.getMessageIDs().contains("0838884567"));
        assertTrue(Message.getMessageHashes().contains("12:0:DIDCAKE"));
    }

    @Test
    public void testDisplayLongestMessage() {
        populateMessages1To4();

        assertEquals(TEXT_2, Message.getLongestMessage());
    }

    @Test
    public void testSearchForMessageID() {
        populateMessages1To4();

        Message found = Message.searchByMessageID("0838884567");

        assertNotNull(found);
        assertEquals(TEXT_4, found.getMessage());
        assertEquals("0838884567", found.getRecipient());
    }

    @Test
    public void testSearchForMessageIDNotFound() {
        populateMessages1To4();

        assertNull(Message.searchByMessageID("9999999999"));
    }

    @Test
    public void testSearchMessagesForRecipient() {
        populateMessages1To4();
        message5.sentMessage(3);

        assertEquals(Arrays.asList(TEXT_2, TEXT_5),
                Message.searchByRecipient("+27838884567"));
    }

    @Test
    public void testDeleteMessageUsingHash() {
        populateMessages1To4();

        String hash = message2.getMessageHash();
        String result = Message.deleteByMessageHash(hash);

        assertEquals("Message \"" + TEXT_2 + "\" successfully deleted.", result);
        assertEquals(0, Message.getStoredMessages().size());
        assertFalse(Message.getMessageHashes().contains(hash));
    }

    @Test
    public void testDeleteMessageUsingUnknownHash() {
        populateMessages1To4();

        assertEquals("No stored message was found with that hash.",
                Message.deleteByMessageHash("XX:9:NONE"));
    }

    @Test
    public void testDisplayReport() {
        populateMessages1To4();

        String report = Message.getReport(Message.getSentMessages());

        assertTrue(report.contains(message1.getMessageHash()));
        assertTrue(report.contains("+27834557896"));
        assertTrue(report.contains(TEXT_1));
        assertTrue(report.contains(message4.getMessageHash()));
        assertTrue(report.contains(TEXT_4));
    }

    @Test
    public void testDisplaySenderAndRecipientOfStoredMessages() {
        populateMessages1To4();

        assertEquals("Sender: TestUser | Recipient: +27838884567\n",
                Message.displaySenderAndRecipient());
    }

    @Test
    public void testStoredMessagesReadFromJsonFile() {
        populateMessages1To4();
        message5.sentMessage(3);

        // Wipe the arrays in memory; the JSON file stays on disk
        Message.clearAll();
        int loaded = Message.loadStoredMessages();

        assertEquals(2, loaded);
        assertEquals(TEXT_2, Message.getStoredMessages().get(0).getMessage());
        assertEquals(TEXT_5, Message.getStoredMessages().get(1).getMessage());
        assertEquals("TestUser", Message.getStoredMessages().get(0).getSender());
    }
}