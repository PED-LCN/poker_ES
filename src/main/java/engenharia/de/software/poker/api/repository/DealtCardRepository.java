package engenharia.de.software.poker.api.repository;

import engenharia.de.software.poker.api.model.entity.DealtCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealtCardRepository extends JpaRepository<DealtCard, Long> {
    List<DealtCard> findByRoundIdAndPlayerIdOrderByDealOrderAsc(Long roundId, Long playerId);
}