package chatApp.Utilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static chatApp.Utilities.Utility.isValidPassword;
import static org.junit.jupiter.api.Assertions.*;

class UtilityTest {
    private Utility utility ;
    @BeforeEach
    void beforeEach(){
        utility = new Utility();
    }

    @Test
    void test_isValidPassword() {
       assertFalse(isValidPassword("wslkt84hr94ekl"));
    }
    @Test
    void test_isInvalidPassword() {
        assertFalse(isValidPassword(null));
    }

    @Test
    void isValidName() {
        assertFalse(Utility.isValidName("dani9"));
    }

    @Test
    void isInValidName() {
        assertFalse(Utility.isValidName(null));
    }

    @Test
    void isValidEmail() {
        assertFalse(Utility.isValidEmail("ee"));
    }
    @Test
    void isInvalidEmail() {
        assertFalse(Utility.isValidEmail(null));
    }
}