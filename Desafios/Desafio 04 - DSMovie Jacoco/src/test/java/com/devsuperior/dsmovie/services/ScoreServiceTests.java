package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith({MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class ScoreServiceTests {

   @InjectMocks
   private ScoreService service;

   @Mock
   private UserService userService;

   @Mock
   private MovieRepository movieRepository;

   @Mock
   private ScoreRepository scoreRepository;

   private long existingMovieId, nonExistingMovieId;
   
   private MovieEntity movie;
   private UserEntity user;
   private ScoreEntity score;
   private ScoreDTO scoreDTO;

   public ScoreServiceTests() {
   }

   @BeforeEach
   void setUp() throws Exception {
      existingMovieId = 1L;
      nonExistingMovieId = 2L;

      score = ScoreFactory.createScoreEntity();
      movie = score.getId().getMovie();
      user = score.getId().getUser();

      scoreDTO = new ScoreDTO(existingMovieId, score.getValue());

      Mockito.when(userService.authenticated()).thenReturn(user);

      Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movie));
      Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());
      Mockito.when(movieRepository.save((MovieEntity)ArgumentMatchers.any())).thenReturn(movie);

      Mockito.when(scoreRepository.saveAndFlush((ScoreEntity)ArgumentMatchers.any())).thenReturn(score);
   }

   @Test
   public void saveScoreShouldReturnMovieDTO() {
      MovieDTO result = service.saveScore(scoreDTO);
      Assertions.assertNotNull(result);
      Assertions.assertEquals(result.getId(), movie.getId());
      Assertions.assertEquals(result.getScore(), score.getValue());
   }

   @Test
   public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
      scoreDTO = new ScoreDTO(nonExistingMovieId, (double)4.5F);
      Assertions.assertThrows(ResourceNotFoundException.class, () -> service.saveScore(scoreDTO));
   }
}