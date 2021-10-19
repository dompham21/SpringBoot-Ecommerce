package com.luv2code.user;

import com.luv2code.entity.Role;
import com.luv2code.entity.User;
import com.luv2code.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;
    @Test
    public void testCreateUser() {
        User user1 =  new User("dom@gmail.com","1234","Dom","Pham");
        Role admin = new Role(1);
        Role editor = new Role(3);
        user1.addRole(admin);
        user1.addRole(editor);

        User savedUser = userRepository.save(user1);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllUser() {
        List<User> listUsers = (List<User>) userRepository.findAll();
        listUsers.forEach(e -> System.out.println(e));
    }

    @Test
    public void testGetById(){

        User user =  userRepository.findById(2).get();
        System.out.println(user);
        assertThat(user.getId()).isGreaterThan(0);

    }

}
