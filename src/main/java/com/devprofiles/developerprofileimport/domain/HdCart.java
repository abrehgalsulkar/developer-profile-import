package com.devprofiles.developerprofileimport.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "hd_cart")
@Data
public class HdCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

        @Column(name = "customer_id")
    private Long customerId;

    private String guestId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "hd_cart_hd_developer_profiles")
    private List<HdDeveloperProfile> developers;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}


