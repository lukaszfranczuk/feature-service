package com.capco.interview.features.security;

import com.capco.interview.features.repositories.UserRepository;
import com.capco.interview.features.repositories.entities.RoleEntity;
import com.capco.interview.features.repositories.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
        return new User(user.getGuid(), user.getPassword(), getAuthorities(user));
    }

    private static List<SimpleGrantedAuthority> getAuthorities(UserEntity user) {
        return user.getRoles().stream()
                .map(RoleEntity::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
