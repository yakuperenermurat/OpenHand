package com.openhand.openhand.entities;

import com.openhand.openhand.enums.Role;
import jakarta.persistence.*;
        import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Column(unique = true, nullable = false)
    private String phone;

    @ManyToMany
    @JoinTable(
            name = "blocked_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "blocked_user_id")
    )
    private List<User> blockedUsers = new ArrayList<>(); // Engellenen kullanıcılar

    @Column(name = "suspended_until")
    private LocalDateTime suspendedUntil; // Hesap askıya alınma bitiş tarihi (null ise süresiz)

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER; // Varsayılan olarak USER atanır

    private String location; // Mahalle/Semt bilgisi için

    private Double rating = 0.0; // Başlangıç değeri 0.0 olarak belirlenir. // Kullanıcı puanı

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feedback> feedbacks; // Kullanıcının aldığı geri bildirimler.

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Kullanıcının aktif olup olmadığını kontrol eden metot
    public boolean isActive() {
        return suspendedUntil == null || suspendedUntil.isBefore(LocalDateTime.now());
    }
}
