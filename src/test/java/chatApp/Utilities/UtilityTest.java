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
       assertFalse(isValidPassword("66666666"));
    }

    @Test
    void isValidName() {
    }

    @Test
    void isValidEmail() {
    }
}