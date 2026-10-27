package engenharia.de.software.poker.api.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "matches")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MatchState state;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "table_id", referencedColumnName = "id")
    private PokerTable pokerTable;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private Player creator;

    public void start() {
        this.state = MatchState.IN_PROGRESS;
    }
}
