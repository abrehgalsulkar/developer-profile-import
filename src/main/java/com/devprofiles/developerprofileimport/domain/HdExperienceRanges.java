package com.devprofiles.developerprofileimport.domain;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "hd_experience_ranges")
@Data
public class HdExperienceRanges {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long minimumExperience;
	
	private Long maximumExperience;
	
	private String experienceRangeLabel;
	
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

