package com.samseen.bookify.security.service;

import com.samseen.bookify.merchant.entity.Merchant;
import com.samseen.bookify.merchant.service.MerchantService;
import com.samseen.bookify.security.model.UserPrinciple;
import com.samseen.bookify.user.entity.User;
import com.samseen.bookify.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class UserDetailsServiceImpl implements UserDetailsService {

    private MerchantService merchantService;

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(MerchantService merchantService, UserRepository userRepository) {
        this.merchantService = merchantService;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Merchant merchant = null;
        if (user.getRole() != User.Role.SUPER_ADMIN) {
            Optional<Merchant> optionalMerchant = merchantService.findById(user.getMerchantId());
            merchant = optionalMerchant.get();
        }
        return new UserPrinciple(user, merchant);
    }
}
