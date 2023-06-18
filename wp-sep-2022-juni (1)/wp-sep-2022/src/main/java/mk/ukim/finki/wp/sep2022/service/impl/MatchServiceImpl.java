package mk.ukim.finki.wp.sep2022.service.impl;

import mk.ukim.finki.wp.sep2022.model.Match;
import mk.ukim.finki.wp.sep2022.model.MatchLocation;
import mk.ukim.finki.wp.sep2022.model.MatchType;
import mk.ukim.finki.wp.sep2022.model.exceptions.InvalidMatchIdException;
import mk.ukim.finki.wp.sep2022.model.exceptions.InvalidMatchLocationIdException;
import mk.ukim.finki.wp.sep2022.repository.MatchLocationRepository;
import mk.ukim.finki.wp.sep2022.repository.MatchRepository;
import mk.ukim.finki.wp.sep2022.service.MatchService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchServiceImpl implements MatchService {
    private final MatchLocationRepository matchLocationRepository;
    private final MatchRepository matchRepository;

    public MatchServiceImpl(MatchLocationRepository matchLocationRepository, MatchRepository matchRepository) {
        this.matchLocationRepository = matchLocationRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    public List<Match> listAllMatches() {
        return this.matchRepository.findAll();
    }

    @Override
    public Match findById(Long id) {
        return this.matchRepository.findById(id).orElseThrow(InvalidMatchIdException::new);
    }

    @Override
    public Match create(String name, String description, Double price, MatchType type, Long location) {
        MatchLocation matchLocation = this.matchLocationRepository.findById(location).orElseThrow(InvalidMatchLocationIdException::new);
        Match match = new Match(name, description, price,type,matchLocation);
        this.matchRepository.save(match);
        return match;
    }

    @Override
    public Match update(Long id, String name, String description, Double price, MatchType type, Long location) {
        Match match =  this.matchRepository.findById(id).orElseThrow(InvalidMatchIdException::new);
        MatchLocation matchLocation = this.matchLocationRepository.findById(location).orElseThrow(InvalidMatchLocationIdException::new);
        match.setName(name);
        match.setDescription(description);
        match.setPrice(price);
        match.setType(type);
        match.setLocation(matchLocation);
        this.matchRepository.save(match);
        return match;
    }

    @Override
    public Match delete(Long id) {
        Match match =  this.matchRepository.findById(id).orElseThrow(InvalidMatchIdException::new);
        this.matchRepository.delete(match);
        return match;
    }

    @Override
    public Match follow(Long id) {
        Match match =  this.matchRepository.findById(id).orElseThrow(InvalidMatchIdException::new);
        match.setFollows(match.getFollows()+1);
        matchRepository.save(match);
        return match;
    }

    @Override
    public List<Match> listMatchesWithPriceLessThanAndType(Double price, MatchType type) {
        if(price!=null && type!=null){
            return this.matchRepository.findAllByPriceLessThanAndTypeEquals(price, type);
        }
        else if(price!= null){
            return  this.matchRepository.findAllByPriceLessThan(price);
        }
        else if(type!=null){
            return  this.matchRepository.findAllByTypeEquals(type);
        }
        else
            return this.matchRepository.findAll();
    }
}
