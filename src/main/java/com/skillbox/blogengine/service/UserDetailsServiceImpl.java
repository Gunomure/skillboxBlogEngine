package com.skillbox.blogengine.service;

import com.skillbox.blogengine.controller.exception.EntityNotFoundException;
import com.skillbox.blogengine.model.User;
import com.skillbox.blogengine.orm.UserRepository;
import com.skillbox.blogengine.security.SecurityUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("!!!!!! select by email: " + email);
        User user = userRepository.findByEmail(email)
//        User user = userRepository.findById(2)
                .orElseThrow(() -> new EntityNotFoundException("AAAAAAAAAAAAa"));
        System.out.println("!!!!!" + user.toString());
        return SecurityUser.fromUser(user);
    }
}
