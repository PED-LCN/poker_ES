package engenharia.de.software.poker.api.repository;

import engenharia.de.software.poker.api.model.entity.PokerRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokerRoundRepository extends JpaRepository<PokerRound, Long> {
}