package com.luv2code.security;

import com.luv2code.entity.User;
import com.luv2code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class WebUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

         User user =  userRepo.getUserByEmail(email);
         if(user != null) {
             return new WebUserDetails(user);
         }

         throw new UsernameNotFoundException("Cound not find user with email: " + email);
    }
}
