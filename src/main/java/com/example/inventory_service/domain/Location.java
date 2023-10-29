package com.example.inventory_service.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @Column(nullable = false)
    @NotEmpty
    private String locationId;

    private String description;

    @CreationTimestamp
    private LocalDateTime createdTime;

    @UpdateTimestamp
    private LocalDateTime lastUpdatedTime;

    public Location(String locationId) {
        this.locationId = locationId;
    }

    public Location(String locationId, String description) {
        this.locationId = locationId;
        this.description = description;
    }
}
