package com.devsuperior.dscommerce.services;

import static org.mockito.ArgumentMatchers.any;

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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.dto.ProductMinDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscommerce.tests.ProductFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    private Long existingProductId, nonExistingId,nonExistingProductId,dependentProductId;
    private String productName;
    private Product product;
    private ProductDTO productDTO;
    private PageImpl<Product> page;

    
    @BeforeEach
    void setUp(){
        existingProductId = 1L;
        nonExistingId = 2L;
        productName = "ps5";
        nonExistingProductId = 3L;
        dependentProductId = 4L;
        product = ProductFactory.createProduct(productName);

        productDTO = new ProductDTO(product);

        page = new PageImpl<>(List.of(product));


        Mockito.lenient().when(repository.findById(existingProductId)).thenReturn(Optional.of(product));
        Mockito.lenient().when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
        Mockito.lenient().when(repository.searchByName(any(),(Pageable)any())).thenReturn(page);
        Mockito.lenient().when(repository.save(any())).thenReturn(product);
        Mockito.lenient().when(repository.getReferenceById(existingProductId)).thenReturn(product);
        Mockito.lenient().when(repository.getReferenceById(nonExistingProductId)).thenThrow(EntityNotFoundException.class);

        Mockito.lenient().when(repository.existsById(existingProductId)).thenReturn(true);
        Mockito.lenient().when(repository.existsById(dependentProductId)).thenReturn(true);
        Mockito.lenient().when(repository.existsById(nonExistingProductId)).thenReturn(false);

        Mockito.lenient().doNothing().when(repository).deleteById(existingProductId);
        Mockito.lenient().doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentProductId);

    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists(){

        ProductDTO result = service.findById(existingProductId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingProductId);
        Assertions.assertEquals(result.getName(), product.getName());

    }

    @Test
    public void findByIdShouldReturnResourceNotFoundExceptionWhenIdDoesNotExists(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
    }

    @Test
    public void findAllShouldReturnPagedProductMinDTO(){

        Pageable pageable = PageRequest.of(0, 12);
        Page<ProductMinDTO> result = service.findAll(productName, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getSize(), 1);
        Assertions.assertEquals(result.iterator().next().getName(), productName);

    }

    @Test
    public void insertShouldReturnProductDTO(){

        ProductDTO result = service.insert(productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), product.getId());


    }

    @Test
    public void UpdateShouldReturnProductDTOWhenIdExists(){

        ProductDTO result = service.update(existingProductId, productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getName(), productDTO.getName());
        Assertions.assertEquals(result.getId(), productDTO.getId());
    }

    @Test
    public void UpdateShouldReturnResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingProductId, productDTO);
        });
    }


    @Test
    public void deleteShouldDoNothingWhenIdExists(){
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingProductId);
        });
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists(){
       Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }



    @Test
    public void ShouldReturnDataBaseExceptionWhenIdDOesNotExists(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingProductId);
        });
    }

}
