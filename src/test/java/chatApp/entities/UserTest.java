package chatApp.entities;

import chatApp.repository.UserRepository;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class UserTest {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void testCreateUser() {
        User user = new User();
        //unique email, change and check if inserted
        user.setEmail("shai@gmail.com");
        user.setPassword("1234");
        user.setName("shai");

        User savedUser = userRepo.save(user);

        User existUser = testEntityManager.find(User.class, savedUser.getId());

        assertThat(user.getEmail()).isEqualTo(existUser.getEmail());
    }

    @Test
    public void testCreateUserTwo() {
        User user = new User();
        //unique email, change and check if inserted
        user.setEmail("taltal@gmail.com");
        user.setPassword("1234");
        user.setName("tal");

        User savedUser = userRepo.save(user);

        User existUser = testEntityManager.find(User.class, savedUser.getId());

        assertThat(user.getEmail()).isEqualTo(existUser.getEmail());
    }

    @Test
    public void testCreateUserThree() {
        User user = new User();
        //unique email, change and check if inserted
        user.setEmail("hen@gmail.com");
        user.setPassword("1234");
        user.setName("hen");

        User savedUser = userRepo.save(user);

        User existUser = testEntityManager.find(User.class, savedUser.getId());

        assertThat(user.getEmail()).isEqualTo(existUser.getEmail());
    }

    @Test
    void setId() {
    }

    @Test
    void setName() {
    }

    @Test
    void setEmail() {
    }

    @Test
    void setPassword() {
    }
}