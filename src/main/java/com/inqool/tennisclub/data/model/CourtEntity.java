package com.inqool.tennisclub.data.model;

import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "court")
public class CourtEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "court_number", nullable = false, unique = true)
    private Integer courtNumber;

    @OneToMany(
            mappedBy = "court",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ReservationEntity> reservations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_surface_id", nullable = false)
    private CourtSurfaceEntity courtSurface;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
