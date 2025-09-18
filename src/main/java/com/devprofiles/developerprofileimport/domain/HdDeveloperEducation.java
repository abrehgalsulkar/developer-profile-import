package com.devprofiles.developerprofileimport.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "hd_developer_education")
@Data
public class HdDeveloperEducation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hd_developer_profile_id")
    private HdDeveloperProfile hdDeveloperProfile;

    private String degreeName;

    private String institution;

    private String location;

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


