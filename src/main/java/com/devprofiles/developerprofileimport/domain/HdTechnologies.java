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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "hd_technologies")
@Getter
@Setter
public class HdTechnologies {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String technology;
	
    private Boolean isDeleted;

    private Boolean isActive;

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

