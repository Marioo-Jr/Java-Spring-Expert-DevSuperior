package com.devsuperior.movieflix.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.movieflix.dto.MovieCardDTO;
import com.devsuperior.movieflix.dto.MovieDetailsDTO;
import com.devsuperior.movieflix.entities.Movie;
import com.devsuperior.movieflix.repositories.MoviesRepository;
import com.devsuperior.movieflix.services.exceptions.ResourceNotFoundException;

@Service
public class MovieService {

    @Autowired
    private MoviesRepository repository;

    @Transactional(readOnly = true)
	public MovieDetailsDTO findById(Long id) {
		Optional<Movie> obj = repository.findById(id);
		Movie entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new MovieDetailsDTO(entity);
	}
    @Transactional(readOnly = true)
    public Page<MovieCardDTO> findByGenre(Long genreId, Pageable pageable) {
    Page<Movie> page = repository.searchByGenre(genreId, pageable);
    return page.map(x -> new MovieCardDTO(x));
}
}
