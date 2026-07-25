package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith({MockitoExtension.class})
@MockitoSettings(
   strictness = Strictness.LENIENT
)
public class UserServiceTests {

   @InjectMocks
   private UserService service;

   @Mock
   private UserRepository repository;

   @Mock
   private CustomUserUtil userUtil;
   private String existingUsername, nonExistingUsername;

   private UserEntity user;
   private List<UserDetailsProjection> userDetailsList;

   public UserServiceTests() {
   }

   @BeforeEach
   void setUp() throws Exception {
      existingUsername = "maria@gmail.com";
      nonExistingUsername = "user@gmail.com";
      user = UserFactory.createUserEntity();

      userDetailsList = UserDetailsFactory.createCustomAdminUser(existingUsername);

      Mockito.when(userUtil.getLoggedUsername()).thenReturn(existingUsername);

      Mockito.when(repository.findByUsername(existingUsername)).thenReturn(Optional.of(user));
      Mockito.when(repository.findByUsername(nonExistingUsername)).thenReturn(Optional.empty());
	  
      Mockito.when(repository.searchUserAndRolesByUsername(existingUsername)).thenReturn(userDetailsList);
      Mockito.when(repository.searchUserAndRolesByUsername(nonExistingUsername)).thenReturn(List.of());
   }

   @Test
   public void authenticatedShouldReturnUserEntityWhenUserExists() {
      Mockito.when(userUtil.getLoggedUsername()).thenReturn(existingUsername);
      UserEntity result = service.authenticated();
      Assertions.assertNotNull(result);
      Assertions.assertEquals(result.getUsername(), existingUsername);
   }

   @Test
   public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
      Mockito.when(userUtil.getLoggedUsername()).thenReturn(nonExistingUsername);
      Assertions.assertThrows(UsernameNotFoundException.class, () -> service.authenticated());
   }

   @Test
   public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
      UserDetails result = service.loadUserByUsername(existingUsername);
      Assertions.assertNotNull(result);
      Assertions.assertEquals(result.getUsername(), existingUsername);
   }

   @Test
   public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
      Assertions.assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(nonExistingUsername));
   }
}
