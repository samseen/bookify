package com.samseen.bookify.user.repository;

import com.samseen.bookify.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Page<User> findAllByEmail(String email, Pageable pageable);

    Page<User> findByEmail(@Param("email") String email, Pageable pageable);

    Page<User> findByMerchantId(Long merchantId, Pageable pageable);
}
