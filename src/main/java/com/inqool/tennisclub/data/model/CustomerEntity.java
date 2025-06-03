package com.inqool.tennisclub.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Table(name = "customer")
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Invalid phone number")
    @Column(name = "phone_number", nullable = false, length = 20, unique = true)
    private String phoneNumber;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @OneToMany(mappedBy = "customer")
    private List<ReservationEntity> reservations;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
