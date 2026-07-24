package com.devsuperior.movieflix.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.movieflix.dto.ReviewDTO;
import com.devsuperior.movieflix.entities.Review;
import com.devsuperior.movieflix.repositories.MoviesRepository;
import com.devsuperior.movieflix.repositories.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository repository;

    @Autowired
    private MoviesRepository movieRepository;
    
    @Autowired
    private AuthService authService;

   @Transactional
    public ReviewDTO insert(ReviewDTO dto) {

        Review review = new Review();

        review.setText(dto.getText());
        review.setMovie(movieRepository.getReferenceById(dto.getMovieId()));
        review.setUser(authService.authenticated());

        review = repository.save(review);
        return new ReviewDTO(review);
    }
}