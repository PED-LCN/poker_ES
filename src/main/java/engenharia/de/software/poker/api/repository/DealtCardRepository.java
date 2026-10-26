package engenharia.de.software.poker.api.repository;

import engenharia.de.software.poker.api.model.entity.DealtCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DealtCardRepository extends JpaRepository<DealtCard, Long> {
    List<DealtCard> findByRoundIdAndUserIdOrderByDealOrderAsc(Long roundId, Long userId);
}