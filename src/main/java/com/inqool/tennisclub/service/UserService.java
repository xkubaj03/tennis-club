package com.inqool.tennisclub.service;

import com.inqool.tennisclub.data.model.UserEntity;
import com.inqool.tennisclub.data.model.enums.AuthorizationType;
import com.inqool.tennisclub.data.repository.UserRepository;
import com.inqool.tennisclub.exceptions.NonUniqueFieldException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Log4j2
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity create(UserEntity user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new NonUniqueFieldException("User with username " + user.getUsername() + " already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));

        Set<AuthorizationType> roles = user.getAuthType().getAllRoles();
        Set<AuthorizationType> expandedRoles =
                roles.stream().flatMap(role -> role.getAllRoles().stream()).collect(Collectors.toSet());

        List<SimpleGrantedAuthority> authorities = expandedRoles.stream()
                .map(role -> new SimpleGrantedAuthority(role.toSpringRole()))
                .toList();

        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
