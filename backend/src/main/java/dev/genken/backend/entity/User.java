package dev.genken.backend.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.genken.backend.serialization.ReservationSerializer;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(nullable = false, unique = true)
    private String username;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonSerialize(contentUsing = ReservationSerializer.class)
    private List<Reservation> reservations;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public UUID getUuid() { return uuid; }

    public void setUuid(UUID uuid) { this.uuid = uuid; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public Role getRole() { return role; }

    public void setRole(Role role) { this.role = role; }

    public List<Reservation> getReservations() { return reservations; }
}
