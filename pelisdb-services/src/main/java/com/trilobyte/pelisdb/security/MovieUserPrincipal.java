package com.trilobyte.pelisdb.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class MovieUserPrincipal implements UserDetails {

    private static final long serialVersionUID = 2135921614334026127L;

    private final String username;

    private final String password;

    private final Collection<? extends GrantedAuthority> authorities;

    public MovieUserPrincipal(
            final String username,
            final String password,
            final Collection<? extends GrantedAuthority> authorities ) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
