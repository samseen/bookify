package com.samseen.bookify.merchant.repository;

import com.samseen.bookify.merchant.entity.Merchant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MerchantRepository extends PagingAndSortingRepository<Merchant, Long> {

    Optional<Merchant> findByRcNumber(String rcNumber);

    Optional<Merchant> findByAccountId(String accountId);

    Optional<Merchant> findMerchantByCodeOrRcNumber(String code, String rcNumber);

    @Query("SELECT m FROM Merchant m ORDER BY m.createdAt DESC")
    List<Merchant> getLastMerchant(Pageable pageable);
}
