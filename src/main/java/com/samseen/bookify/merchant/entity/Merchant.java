package com.samseen.bookify.merchant.entity;

import com.samseen.bookify.merchant.model.MerchantRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "merchant")
@Data
public class Merchant implements Serializable {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "rc_number")
    private String rcNumber;

    @Column(name = "code")
    private String code;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    public Merchant(MerchantRequest merchantRequest) {
        this.name = merchantRequest.getName();
        this.rcNumber = merchantRequest.getRcNumber();
        this.code = merchantRequest.getCode();
    }
}
