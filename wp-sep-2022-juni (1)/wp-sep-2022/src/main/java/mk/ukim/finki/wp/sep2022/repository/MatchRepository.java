package mk.ukim.finki.wp.sep2022.repository;

import mk.ukim.finki.wp.sep2022.model.Match;
import mk.ukim.finki.wp.sep2022.model.MatchType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findAllByPriceLessThan(Double price);
    List<Match> findAllByTypeEquals(MatchType matchType);
    List<Match> findAllByPriceLessThanAndTypeEquals(Double price, MatchType matchType);
}
