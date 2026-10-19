package javaapplication1;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class LoginTest {

    private login Login;

    public LoginTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        Login = new login("kyl_1", "Ch&&sec@ke99!", "Kyle", "Smith"); // test user
    }

    @After
    public void tearDown() {
    }

    // Test of checkUserName method, of class login.
    @Test
    public void testCheckUserName() {
        assertTrue("Valid username should return true", Login.checkUserName("kyl_1"));
        assertFalse("Invalid username should return false", Login.checkUserName("kyle!!!!!!!"));
    }

    // Test of checkUserPassword method, of class login.
    @Test
    public void testCheckUserPassword() {
        assertTrue("Valid password should return true", Login.checkUserPassword("Ch&&sec@ke99!"));
        assertFalse("Invalid password should return false", Login.checkUserPassword("password"));
    }

    // Test of checkphoneNumber method, of class login.
    @Test
    public void testCheckPhoneNumber() {
        assertTrue("Valid phone should return true", Login.checkphoneNumber("+27838968976"));
        assertFalse("Invalid phone should return false", Login.checkphoneNumber("08966553"));
    }

    // Test of registerUser method, of class login.
    @Test
    public void testRegisterUser() {
        // Successful registration
        String success = Login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertEquals("Registration successful.", success);

        // Invalid username
        String invalidUser = Login.registerUser("kyle!!!!!!!", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertEquals("Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.", invalidUser);

        // Invalid password
        String invalidPass = Login.registerUser("kyl_1", "password", "+27838968976", "Kyle", "Smith");
        assertEquals("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.", invalidPass);

        // Invalid cell number
        String invalidCell = Login.registerUser("kyl_1", "Ch&&sec@ke99!", "08966553", "Kyle", "Smith");
        assertEquals("Cell phone number incorrectly formatted or does not contain international code.", invalidCell);
    }

    // Test of loginUser(String, String) method, of class login.
    @Test
    public void testLoginUser() {
        assertTrue("Correct credentials should return true", Login.loginUser("kyl_1", "Ch&&sec@ke99!"));
        assertFalse("Incorrect credentials should return false", Login.loginUser("wrong", "wrongpass"));
    }

    // Test of returnLoginStatus(String, String) method, of class login.
    @Test
    public void testReturnLoginStatus() {
        String success = Login.returnLoginStatus("kyl_1", "Ch&&sec@ke99!");
        assertEquals("Welcome Kyle Smith it is great to see you again.", success);

        String fail = Login.returnLoginStatus("wrong", "wrongpass");
        assertEquals("Username or password incorrect, please try again.", fail);
    }
}