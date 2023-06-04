package com.samseen.bookify.merchant.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MerchantRequest {

    private String name;

    @NotEmpty
    @Size(min = 5, message = "{validation.name.size.too_short}")
    private String rcNumber;

    @NotEmpty
    @Size(min = 4, max = 4)
    private String code;
}
