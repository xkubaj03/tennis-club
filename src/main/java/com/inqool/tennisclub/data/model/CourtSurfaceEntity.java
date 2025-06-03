package com.inqool.tennisclub.data.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "court_surface")
public class CourtSurfaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "surface_name", nullable = false)
    private String surfaceName;

    @Column(name = "surface_description", nullable = true)
    private String surfaceDescription;

    @Column(name = "cost_per_minute", nullable = false)
    private BigDecimal costPerMinute;

    @OneToMany(
            mappedBy = "courtSurface",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<CourtEntity> courts;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
