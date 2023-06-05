package com.samseen.bookify.merchant.service;

import com.samseen.bookify.core.response.Result;
import com.samseen.bookify.merchant.entity.Merchant;
import com.samseen.bookify.merchant.model.MerchantRequest;
import com.samseen.bookify.merchant.repository.MerchantRepository;
import com.samseen.bookify.security.model.UserPrinciple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class MerchantService {

    private final MerchantRepository merchantRepository;

    private final ReferenceService referenceService;

    public MerchantService(MerchantRepository merchantRepository, ReferenceService referenceService) {
        this.merchantRepository = merchantRepository;
        this.referenceService = referenceService;
    }

    public Result<Merchant> create(UserPrinciple principle,
                                   MerchantRequest merchantRequest) {

        Optional<Merchant> optionalMerchant = this.merchantRepository.findMerchantByCodeOrRcNumber(
                merchantRequest.getCode(), merchantRequest.getRcNumber()
        );

        if (optionalMerchant.isPresent()) {
            return Result.failure("Merchant already exists", null);
        }

        String accountId = referenceService.accountId(merchantRequest.getCode());
        Merchant merchant = new Merchant(merchantRequest);

        // Saving this independently in case I want the id generation to depend on the
        // creation of another object response
        merchant.setId(Long.valueOf(accountId));

        return Result.success(this.merchantRepository.save(merchant));
    }

    public Optional<Merchant> findById(Long id) {
        return this.merchantRepository.findById(id);
    }

    public Result<Page<Merchant>> getMerchants(int page, int size) {
        return Result.success(this.merchantRepository.findAll(PageRequest.of(page, size)));
    }
}
