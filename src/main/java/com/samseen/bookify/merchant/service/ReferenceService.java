package com.samseen.bookify.merchant.service;

import com.samseen.bookify.merchant.repository.MerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReferenceService {

    private MerchantRepository merchantRepository;

    public ReferenceService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public String accountId(String merchantCode) {
        Long count = 2000L + this.merchantRepository.getLastMerchant(Pageable.ofSize(1))
                .stream().findFirst()
                .map(chn -> chn.getId()).orElse(0L);
        return String.join("", merchantCode.toUpperCase(),
                StringUtils.leftPad(String.valueOf(count), 11 - merchantCode.length(), "0")
        );
    }

    public String code(int length) {
        return RandomStringUtils.randomAlphanumeric(length).toUpperCase();
    }
}
