package mk.ukim.finki.wp.kol2023.g2.service.impl;

import mk.ukim.finki.wp.kol2023.g2.model.Director;
import mk.ukim.finki.wp.kol2023.g2.model.Genre;
import mk.ukim.finki.wp.kol2023.g2.model.Movie;
import mk.ukim.finki.wp.kol2023.g2.model.exceptions.InvalidDirectorIdException;
import mk.ukim.finki.wp.kol2023.g2.model.exceptions.InvalidMovieIdException;
import mk.ukim.finki.wp.kol2023.g2.repository.DirectorRepository;
import mk.ukim.finki.wp.kol2023.g2.repository.MovieRepository;
import mk.ukim.finki.wp.kol2023.g2.service.MovieService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {
    private final DirectorRepository directorRepository;
    private final MovieRepository movieRepository;

    public MovieServiceImpl(DirectorRepository directorRepository, MovieRepository movieRepository) {
        this.directorRepository = directorRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    public List<Movie> listAllMovies() {
        return this.movieRepository.findAll();
    }

    @Override
    public Movie findById(Long id) {
        return this.movieRepository.findById(id).orElseThrow(InvalidMovieIdException::new);
    }

    @Override
    public Movie create(String name, String description, Double rating, Genre genre, Long director) {
        Director director1 = this.directorRepository.findById(director).orElseThrow(InvalidDirectorIdException::new);
        Movie movie = new Movie(name, description, rating, genre, director1);
        this.movieRepository.save(movie);
        return movie;
    }

    @Override
    public Movie update(Long id, String name, String description, Double rating, Genre genre, Long director) {
        Movie movie = this.movieRepository.findById(id).orElseThrow(InvalidMovieIdException::new);
        Director director1 = this.directorRepository.findById(director).orElseThrow(InvalidDirectorIdException::new);
        movie.setName(name);
        movie.setDescription(description);
        movie.setRating(rating);
        movie.setGenre(genre);
        movie.setDirector(director1);
        this.movieRepository.save(movie);
        return movie;
    }

    @Override
    public Movie delete(Long id) {
        Movie movie = this.movieRepository.findById(id).orElseThrow(InvalidMovieIdException::new);
        this.movieRepository.delete(movie);
        return movie;
    }

    @Override
    public Movie vote(Long id) {
        Movie movie = this.movieRepository.findById(id).orElseThrow(InvalidMovieIdException::new);
        movie.setVotes(movie.getVotes()+1);
        this.movieRepository.save(movie);
        return movie;
    }

    @Override
    public List<Movie> listMoviesWithRatingLessThenAndGenre(Double rating, Genre genre) {
        if(rating!=null && genre!=null){
            return this.movieRepository.findAllByRatingIsLessThanAndGenreEquals(rating, genre);
        } else if(rating!=null){
            return this.movieRepository.findAllByRatingIsLessThan(rating);
        } else if(genre!=null){
            return this.movieRepository.findAllByGenreEquals(genre);
        }
        return this.movieRepository.findAll();
    }
}
