package engenharia.de.software.poker.api.model.entity;

import engenharia.de.software.poker.api.exception.TableFullException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "poker_tables")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PokerTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "pokerTable")
    private Match match;

    @ManyToMany
    @JoinTable(
            name = "table_players",
            joinColumns = @JoinColumn(name = "table_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<Player> players = new ArrayList<>();

    private int maxPlayers = 9;
    private int minPlayersToStart = 2;

    public boolean isAvailable() {
        return players.size() < maxPlayers;
    }

    public void addPlayer(Player player) {
        if (!isAvailable()) {
            throw new TableFullException("The table is already full.");
        }
        this.players.add(player);
    }
}
