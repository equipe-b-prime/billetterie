package com.billetterie.billetterie.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
/**
 * Représente l'evenement qui se deroule
 */
import java.time.LocalDateTime;
@Entity
@Table(name = "evenement",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"titre", "horaire"})
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Evenement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime horaire;
    @Column(name = "placement_libre")
    private Boolean placementLibre;
    @ManyToOne
    @JoinColumn(name = "salle_id")
    private Salle salle;
}
