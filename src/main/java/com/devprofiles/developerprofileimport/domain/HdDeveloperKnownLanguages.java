package com.devprofiles.developerprofileimport.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "hd_developer_known_languages")
@Data
public class HdDeveloperKnownLanguages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hd_languages_id")
    private HdLanguages hdLanguages;

    @ManyToOne
    @JoinColumn(name = "hd_language_proficiency_id")
    private HdLanguageProficiency hdLanguageProficiency;

    @ManyToOne
    @JoinColumn(name = "hd_developer_profile_id")
    private HdDeveloperProfile hdDeveloperProfile;

    private Boolean isDeleted;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @PrePersist
	public void prePersist() {
		isDeleted = Boolean.FALSE;
	}
}


