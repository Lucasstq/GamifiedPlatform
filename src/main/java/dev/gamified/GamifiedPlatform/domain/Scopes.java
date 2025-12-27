package dev.gamified.GamifiedPlatform.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_scopes")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Scopes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

}
