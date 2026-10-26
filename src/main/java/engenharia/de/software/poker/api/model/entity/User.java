package engenharia.de.software.poker.api.model.entity;

import engenharia.de.software.poker.api.model.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = {"userName"}))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max =150)
    @Column(name = "username", nullable = false, length = 150)
    private String userName;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String email;

    @NotBlank
    @Column(name = "senha", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String password;

    @NotNull
    @Column(name = "role", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private UserRole role;
}
