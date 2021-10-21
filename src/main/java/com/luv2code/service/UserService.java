package com.luv2code.service;

import com.luv2code.entity.Role;
import com.luv2code.entity.User;
import com.luv2code.error.UserNotFoundException;
import com.luv2code.repository.RoleRepository;
import com.luv2code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserService {
    public static final Integer USER_PER_PAGE = 4;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> listAll() {

        return (List<User>) userRepo.findAll();
    }

    public Page<User> listByPage(Integer pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, USER_PER_PAGE, sort);

        if(keyword != null) {
            return userRepo.findAll(keyword, pageable);
        }

        return userRepo.findAll(pageable);
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
