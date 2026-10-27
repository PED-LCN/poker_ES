package engenharia.de.software.poker.api.repository;

import engenharia.de.software.poker.api.model.entity.Match;
import engenharia.de.software.poker.api.model.entity.MatchState;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByState(MatchState state);
}
