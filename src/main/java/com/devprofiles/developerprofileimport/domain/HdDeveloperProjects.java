package com.devprofiles.developerprofileimport.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "hd_developer_projects")
@Data
public class HdDeveloperProjects {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hd_developer_profile_id")
    private HdDeveloperProfile hdDeveloperProfile;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "hd_developer_projects_hd_technologies")
    private List<HdTechnologies> technologies;

    private String developerRole;

    private String projectName;

    @Column(columnDefinition = "TEXT")
    private String responsibility;

    private Long durationInMonths;

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


