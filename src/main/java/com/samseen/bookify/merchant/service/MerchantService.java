package com.samseen.bookify.merchant.service;

import com.samseen.bookify.merchant.repository.MerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MerchantService {

    private final MerchantRepository merchantRepository;

    private final ReferenceService referenceService;

    public MerchantService(MerchantRepository merchantRepository, ReferenceService referenceService) {
        this.merchantRepository = merchantRepository;
        this.referenceService = referenceService;
    }


}
