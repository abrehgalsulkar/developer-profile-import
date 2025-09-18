package com.devprofiles.developerprofileimport.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "hd_developer_certification")
@Data
public class HdDeveloperCertification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hd_developer_profile_id")
    private HdDeveloperProfile hdDeveloperProfile;

    private String certificateName;

    private String institutionLogo;

    private String completionYear;

    private Boolean isActive;

    private Boolean isDeleted;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @PrePersist
    public void prePersist() {
        isDeleted = Boolean.FALSE;
        isActive = Boolean.TRUE;
    }
}


