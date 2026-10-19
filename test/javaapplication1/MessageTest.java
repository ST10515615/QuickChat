package javaapplication1;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the Message class, using the test data from the
 * Part 2 assignment.
 */
public class MessageTest {

    // Test Data for Message 1
    private static final String RECIPIENT_1 = "+27718693002";
    private static final String TEXT_1 = "Hi Mike, can you join us for dinner tonight?";

    // Test Data for Message 2
    private static final String RECIPIENT_2 = "08575975889";
    private static final String TEXT_2 = "Hi Keegan, did you receive the payment?";

    private Message message1;
    private Message message2;

    @Before
    public void setUp() {
        // Fixed IDs starting with "00" so the hash is predictable
        message1 = new Message("0012345678", 0, RECIPIENT_1, TEXT_1);
        message2 = new Message("0198765432", 1, RECIPIENT_2, TEXT_2);
    }

    // ---------- Message length (250 characters) ----------

    @Test
    public void testMessageLengthSuccess() {
        assertEquals("Message ready to send.", message1.checkMessageLength());
    }

    @Test
    public void testMessageLengthFailure() {
        // Build a message that is 260 characters long (10 too many)
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 260; i++) {
            longText.append("a");
        }
        Message tooLong = new Message("0012345678", 0, RECIPIENT_1, longText.toString());

        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                tooLong.checkMessageLength());
    }

    // ---------- Recipient number ----------

    @Test
    public void testRecipientCellSuccess() {
        assertEquals("Cell phone number successfully captured.",
                message1.checkRecipientCell());
    }

    @Test
    public void testRecipientCellFailure() {
        assertEquals("Cell phone number is incorrectly formatted or does not "
                + "contain an international code. Please correct the number "
                + "and try again.", message2.checkRecipientCell());
    }

    // ---------- Message hash ----------

    @Test
    public void testMessageHashMessage1() {
        assertEquals("00:0:HITONIGHT", message1.createMessageHash());
    }

    @Test
    public void testMessageHashLoop() {

        // Each position in the arrays is one test case
        String[] ids = {"0012345678", "0198765432", "2345678901"};
        int[] numbers = {0, 1, 2};
        String[] texts = {TEXT_1, TEXT_2, "Hello"};
        String[] expected = {"00:0:HITONIGHT", "01:1:HIPAYMENT", "23:2:HELLOHELLO"};

        for (int i = 0; i < ids.length; i++) {
            Message m = new Message(ids[i], numbers[i], RECIPIENT_1, texts[i]);
            assertEquals("Hash test " + (i + 1) + " failed",
                    expected[i], m.createMessageHash());
        }
    }

    // ---------- Message ID ----------

    @Test
    public void testGeneratedMessageIDIsTenDigits() {

        // Generate several IDs, since they are random
        for (int i = 0; i < 20; i++) {
            String id = Message.generateMessageID();
            assertEquals(10, id.length());
            assertTrue("ID should be digits only: " + id, id.matches("\\d{10}"));
        }
    }

    @Test
    public void testCheckMessageID() {
        assertTrue(message1.checkMessageID());

        Message tooLongID = new Message("12345678901", 0, RECIPIENT_1, TEXT_1);
        assertFalse(tooLongID.checkMessageID());
    }

    // ---------- Send, disregard, store ----------

    @Test
    public void testSentMessageSend() {
        assertEquals("Message successfully sent.", message1.sentMessage(1));
    }

    @Test
    public void testSentMessageDisregard() {
        assertEquals("Press 0 to delete the message.", message2.sentMessage(2));
    }

    @Test
    public void testSentMessageStore() {
        // Also writes the message to messages.json
        assertEquals("Message successfully stored.", message1.sentMessage(3));
    }

    // ---------- Totals and details ----------

    @Test
    public void testReturnTotalMessages() {

        // The total is shared by all tests, so compare against the start value
        int before = Message.returnTotalMessages();

        message1.sentMessage(1);   // sent: total goes up by 1
        message2.sentMessage(2);   // disregarded: total stays the same

        assertEquals(before + 1, Message.returnTotalMessages());
    }

    @Test
    public void testMessageDetailsOrder() {
        String details = message1.getMessageDetails();

        int idPosition = details.indexOf("Message ID:");
        int hashPosition = details.indexOf("Message Hash:");
        int recipientPosition = details.indexOf("Recipient:");
        int messagePosition = details.indexOf("Message: ");

        // Order must be: ID, Hash, Recipient, Message
        assertTrue(idPosition >= 0);
        assertTrue(idPosition < hashPosition);
        assertTrue(hashPosition < recipientPosition);
        assertTrue(recipientPosition < messagePosition);
        assertTrue(details.contains("00:0:HITONIGHT"));
    }

    @Test
    public void testPrintMessagesShowsSentMessage() {
        message1.sentMessage(1);
        assertTrue(Message.printMessages().contains(TEXT_1));
    }
}