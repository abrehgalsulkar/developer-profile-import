package com.devprofiles.developerprofileimport.domain;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "hd_developer_profile")
@Data
public class HdDeveloperProfile {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	 	
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "hd_developer_profile_hd_availabilities")
    private List<HdAvailabilities> availabilities;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "hd_developer_profile_hd_work_locations")
    private List<HdWorkLocations> workLocations;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "hd_developer_profile_hd_overall_technologies")
    private List<HdTechnologies> overallExperienceSkills;

    @ManyToOne
    @JoinColumn(name = "designation_id")
    private HdDesignations designation;

    private String firstName;

    private String lastName;

    @Column(columnDefinition = "TEXT")
    private String about;

    private String jobTitle;

    private Long numberOfExperience;   // in years

    private Long totalWorkedHours;

    private Long totalProjectCompletion;

    private Double hourlyRate;	    

    private Boolean isVerified;

    private String introductionVideoUrl;

    private String resumeUrl;

    private String profilePictureUrl;
    
    private Double averageRating;
    
    private Long totalReview;
    
    @Column(columnDefinition = "TEXT")
    private String permanentAddress;
    
    @Column(columnDefinition = "TEXT")
    private String temporaryAddress;

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

