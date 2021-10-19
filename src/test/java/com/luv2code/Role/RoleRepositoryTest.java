package com.luv2code.Role;
import com.luv2code.entity.Role;
import com.luv2code.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepo;

    @Test
    public void testCreateRole() {
        Role role1 = new Role("Salesperson", "Manager product price, customers, shipping, orders and sales report");
        Role role2 = new Role("Editor","Manager categories, brands, products, articles and menus");
        Role role3 = new Role("Shipper","View products, view orders, and update order status");
        Role role4 = new Role("Assistant","Manager questions and views");

        roleRepo.saveAll(List.of(role1, role2, role3, role4));
    }


}
