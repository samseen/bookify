package com.samseen.bookify.merchant.controller;

import com.samseen.bookify.core.limit.RateLimit;
import com.samseen.bookify.core.response.Result;
import com.samseen.bookify.merchant.entity.Merchant;
import com.samseen.bookify.merchant.model.MerchantRequest;
import com.samseen.bookify.merchant.service.MerchantService;
import com.samseen.bookify.security.controller.SecuredRestController;
import com.samseen.bookify.security.model.UserPrinciple;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/v1.0/merchant")
public class MerchantController implements SecuredRestController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @RateLimit(type = RateLimit.Type.IP, count = 10L, period = 1L)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    @PostMapping(path = "create")
    public Result<Merchant> create(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserPrinciple userPrinciple,
            @Valid @RequestBody MerchantRequest merchantRequest) {
        return merchantService.create(userPrinciple, merchantRequest);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    @Parameter(name = "userPrinciple", hidden = true)
    @GetMapping(path = "filter")
    public Result<Page<Merchant>> filter(@AuthenticationPrincipal UserPrinciple userPrinciple,
                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "size", defaultValue = "20") int size) {
        return merchantService.getMerchants(page, size);
    }
}
