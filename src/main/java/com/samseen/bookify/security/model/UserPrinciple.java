package com.samseen.bookify.security.model;

import com.samseen.bookify.merchant.entity.Merchant;
import com.samseen.bookify.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrinciple implements UserDetails {

    private Merchant merchant;
    private User user;

    public UserPrinciple(User user, Merchant merchant) {
        this.merchant = merchant;
        this.user = user;
    }

    private User getUser() {
        return user;
    }

    private Merchant getMerchant() {
        return merchant;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "UserPrinciple{" +
                "user=" + user +
                '}';
    }
}
