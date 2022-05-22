package com.trilobyte.pelisdb.security;

import com.trilobyte.pelisdb.entities.AuthUserEntity;
import com.trilobyte.pelisdb.repository.AuthUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MovieUserDetailsService implements UserDetailsService {

    @Autowired
    private AuthUserRepository userDao;

    @Override
    public UserDetails loadUserByUsername(final String email) {
        final AuthUserEntity userFind =
                userDao
                        .findOneByEmail(email)
                        .orElseThrow(
                                () -> {
                                    throw new UsernameNotFoundException(
                                            new StringBuilder("User not found with email : ").append(email).toString());
                                });

        final String username = userFind.getEmail();
        // XXX ignore password in Challenge
        final String pwd = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("");

        final Set<GrantedAuthority> authorities =
                userFind
                        .getRoles()
                        .stream()
                        .map(r -> new SimpleGrantedAuthority(new StringBuilder("ROLE_").append(r.getRole()).toString()))
                        .collect(Collectors.toSet());
        return new MovieUserPrincipal(username, pwd, authorities);
    }
}
