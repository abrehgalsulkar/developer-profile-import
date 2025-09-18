package com.devprofiles.developerprofileimport.domain;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "hd_developer_family_member_details")
@Data
public class HdDeveloperFamilyMemberDetails {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hd_developer_profile_id")
    private HdDeveloperProfile hdDeveloperProfile;

    private String name;

    private String relation;
    
    private String contactNumber;
    
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

