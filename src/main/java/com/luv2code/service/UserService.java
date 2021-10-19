package com.luv2code.service;

import com.luv2code.entity.Role;
import com.luv2code.entity.User;
import com.luv2code.error.UserNotFoundException;
import com.luv2code.repository.RoleRepository;
import com.luv2code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> listAll() {

        return (List<User>) userRepo.findAll();
    }

    public List<Role> listRoles() {
        return (List<Role>) roleRepo.findAll();
    }

    public User saveUser(User user) {
        boolean isUpdateUser = (user.getId() != null);

        if(isUpdateUser) {
            if(user.getPassword().isEmpty()) {
                User existingUser = userRepo.findById(user.getId()).get();

                user.setPassword(existingUser.getPassword());
            }
            else {
                encodePassword(user);
            }
        }
        else {
            encodePassword(user);
        }
        return userRepo.save(user);
    }

    public void encodePassword(User user) {
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
    }

    public boolean isEmailUnique(Integer id, String email) {
        User userByEmail = userRepo.getUserByEmail(email);
        if(userByEmail == null) return true;
        boolean isCreateNew = (id == null);

        if(isCreateNew )  {
            if(userByEmail != null) {
                return false;
            }
        }
        else {
            if(userByEmail.getId() != id) {
                return false;
            }
        }
        return true;
    }

    public User getUserById(Integer id) throws UserNotFoundException {
        try {
            return userRepo.findById(id).get();
        }
        catch (NoSuchElementException ex) {
            throw new UserNotFoundException("Cound not find any user with ID " + id);
        }

    }

    public void deleteUserById(Integer id) throws UserNotFoundException {
        Long countUser = userRepo.countUserById(id);

        if(countUser == null || countUser == 0) {
            throw new UserNotFoundException("Cound not find any user with ID " + id);
        }
        userRepo.deleteById(id);
    }

    public void updateUserEnabledStatus(Integer id, boolean enabled) {
        userRepo.updateEnableStatus(id, enabled);
    }
}
