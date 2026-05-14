package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.product.CreateProductDTO;
import com.senla.ProductService.dto.product.ProductDTO;
import com.senla.ProductService.dto.product.ProductSearchDTO;
import com.senla.ProductService.dto.product.ProductUpdateDTO;
import com.senla.ProductService.exception.CsvImportException;
import com.senla.ProductService.mapper.ProductMapper;
import com.senla.ProductService.model.Brand;
import com.senla.ProductService.model.Product;
import com.senla.ProductService.model.ProductCategory;
import com.senla.ProductService.model.enums.ProductSortType;
import com.senla.ProductService.repository.ProductRepository;
import com.senla.ProductService.service.BrandService;
import com.senla.ProductService.service.ProductCategoryService;
import com.senla.ProductService.util.YandexCloudUtil;
import jakarta.persistence.EntityNotFoundException;
import org.apache.tomcat.util.http.InvalidParameterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private BrandService brandService;
    @Mock
    private ProductCategoryService productCategoryService;
    @Mock
    private YandexCloudUtil yandexCloudUtil;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDTO productDTO;
    private ProductUpdateDTO productUpdateDTO;
    private ProductSearchDTO productSearchDTO;
    private Brand brand;
    private ProductCategory category;

    private final UUID productId = UUID.randomUUID();
    private final UUID brandId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        brand = new Brand();
        brand.setId(brandId);
        brand.setName("brandName");

        category = new ProductCategory();
        category.setId(categoryId);
        category.setName("categoryName");

        product = new Product();
        product.setId(productId);
        product.setName("product");
        product.setDescription("description");
        product.setAmount(1.0);
        product.setUnit("unit");
        product.setBrand(brand);
        product.setProductCategory(category);
        product.setImageUrl("imageUrl");

        productDTO = new ProductDTO(productId, product.getName(), product.getDescription(), product.getAmount(),
                product.getUnit(), brandId, brand.getName(), categoryId, category.getName(), product.getImageUrl());

        productUpdateDTO = new ProductUpdateDTO("updatedProduct", "updatedSubscription",
                2.0, "updatedUnit", brandId, categoryId);

        productSearchDTO = new ProductSearchDTO(1, 10, null, null,
                null, ProductSortType.ID.getDisplayName(), true);
    }

    @Test
    void saveShouldSaveCallRepositoryMethodWithoutImage() {
        MultipartFile file = mock(MultipartFile.class);

        when(brandService.findByIdIfExists(brandId)).thenReturn(brand);
        when(productCategoryService.findByIdIfExists(categoryId)).thenReturn(category);
        when(productMapper.productDTOToProduct(productDTO)).thenReturn(product);
        when(productMapper.productToProductDTO(product)).thenReturn(productDTO);
        when(file.isEmpty()).thenReturn(true);

        productService.save(productDTO, file);

        verify(productRepository).save(product);
        verify(yandexCloudUtil, never()).saveImageToStorage(any(), anyString());
    }

    @Test
    void saveShouldCallRepositoryMethodWithImage() {
        MultipartFile file = mock(MultipartFile.class);

        when(brandService.findByIdIfExists(brandId)).thenReturn(brand);
        when(productCategoryService.findByIdIfExists(categoryId)).thenReturn(category);
        when(productMapper.productDTOToProduct(productDTO)).thenReturn(product);
        when(productMapper.productToProductDTO(product)).thenReturn(productDTO);

        when(file.isEmpty()).thenReturn(false);

        when(yandexCloudUtil.saveImageToStorage(file, "product_images/")).thenReturn("imageUrl");

        productService.save(productDTO, file);

        verify(yandexCloudUtil).saveImageToStorage(file, "product_images/");
        verify(productRepository).save(product);
    }

    @Test
    void findAllShouldReturnList() {
        when(productRepository.findAllWithPagination(productSearchDTO)).thenReturn(List.of(product));
        when(productMapper.productToProductDTO(product)).thenReturn(productDTO);

        List<ProductDTO> result = productService.findAll(productSearchDTO);

        assertEquals(1, result.size());
        assertEquals(productDTO, result.get(0));
    }

    @Test
    void findAllShouldThrowInvalidParameterException() {
        ProductSearchDTO invalidDto = new ProductSearchDTO(1, 10, null, null,
                null, "wrong_sort", true);

        assertThrows(InvalidParameterException.class, () -> productService.findAll(invalidDto));
    }

    @Test
    void findByIdShouldReturnProductDTO() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.productToProductDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.findById(productId);

        assertNotNull(result);
        assertEquals(productDTO, result);
    }

    @Test
    void findByIdIfExistsShouldReturnProduct() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Product result = productService.findByIdIfExists(productId);

        assertEquals(product, result);
    }

    @Test
    void findByIdIfExistsShouldThrowEntityNotFoundException() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.findByIdIfExists(productId));
    }

    @Test
    void deleteShouldDeleteProductAndImage() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        doNothing().when(yandexCloudUtil).deleteImage(product.getImageUrl());
        doNothing().when(productRepository).delete(product);

        productService.delete(productId);

        verify(yandexCloudUtil).deleteImage(product.getImageUrl());
        verify(productRepository).delete(product);
    }

    @Test
    void deleteShouldDeleteProductWithoutImage() {
        product.setImageUrl(null);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.delete(productId);

        verify(yandexCloudUtil, never()).deleteImage(any());
        verify(productRepository).delete(product);
    }

    @Test
    void updateShouldUpdateProduct() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(brandService.findByIdIfExists(brandId)).thenReturn(brand);
        when(productCategoryService.findByIdIfExists(categoryId)).thenReturn(category);
        when(productMapper.updateProductFromDTO(productUpdateDTO, product)).thenReturn(product);
        when(productMapper.productToProductDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.update(productId, productUpdateDTO);

        assertNotNull(result);

        verify(productRepository).update(product);
        verify(productMapper).updateProductFromDTO(productUpdateDTO, product);
    }

    @Test
    void updateImageShouldUpdateImage() {
        MultipartFile file = mock(MultipartFile.class);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(yandexCloudUtil.saveImageToStorage(file, "product_images/")).thenReturn("imageUrl");
        when(productMapper.productToProductDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.updateImage(productId, file);

        assertNotNull(result);
        assertEquals("imageUrl", product.getImageUrl());

        verify(yandexCloudUtil).deleteImage("imageUrl");
        verify(yandexCloudUtil).saveImageToStorage(file, "product_images/");
        verify(productRepository).update(product);
    }

    @Test
    void findByNameShouldReturnProduct() {
        when(productRepository.findByName(product.getName())).thenReturn(Optional.of(product));

        Product result = productService.findByName(product.getName());

        assertNotNull(result);
        assertEquals(product, result);

        verify(productRepository).findByName(product.getName());
    }

    @Test
    void findByNameShouldReturnNull() {
        when(productRepository.findByName(product.getName())).thenReturn(Optional.empty());

        Product result = productService.findByName(product.getName());

        assertNull(result);
    }

    @Test
    void findAllByIdShouldReturnMap() {
        Set<UUID> ids = Set.of(productId);
        Map<UUID, Product> productMap = Map.of(productId, product);

        when(productRepository.findAllById(ids)).thenReturn(productMap);

        Map<UUID, Product> result = productService.findAllById(ids);

        assertEquals(productMap, result);
        assertEquals(1, result.size());
    }

    @Test
    void importFromCsvShouldThrowExceptionForInvalidExtension() throws IOException {
        ClassPathResource resource = new ClassPathResource("csv/invalid_extension.txt");

        MultipartFile file = new MockMultipartFile("csv", resource.getFilename(),
                "text/plain", resource.getInputStream());

        assertThrows(InvalidParameterException.class, () -> productService.importFromCsv(file));
    }

    @Test
    void importFromCsvShouldThrowExceptionForEmptyFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("csv/empty.csv");

        MultipartFile file = new MockMultipartFile("csv", resource.getFilename(),
                "text/csv", resource.getInputStream());

        assertThrows(InvalidParameterException.class, () -> productService.importFromCsv(file));
    }

    @Test
    void importFromCsvShouldImportProducts() throws IOException {
        UUID newBrandId = UUID.fromString("366ea238-f9cd-430c-9ca7-6cede4652fa4");
        UUID newCategoryid = UUID.fromString("316e4b52-598d-44d9-b02b-6bcc37d08553");
        ClassPathResource resource = new ClassPathResource("csv/products.csv");

        MultipartFile file = new MockMultipartFile("csv", resource.getFilename(),
                "text/csv", resource.getInputStream());

        when(brandService.findAllById(anySet())).thenReturn(Map.of(newBrandId, brand));
        when(productCategoryService.findAllById(anySet())).thenReturn(Map.of(newCategoryid, category));
        when(productMapper.createProductDTOToProduct(any(CreateProductDTO.class))).thenReturn(product);
        doNothing().when(productRepository).saveList(anyList());
        doNothing().when(productRepository).updateList(anyList());

        productService.importFromCsv(file);

        verify(productRepository).saveList(anyList());
        verify(productRepository).updateList(anyList());
    }

    @Test
    void importFromCsvShouldThrowCsvImportException() throws IOException {
        ClassPathResource resource = new ClassPathResource("csv/invalid.csv");

        MultipartFile file = new MockMultipartFile("csv", resource.getFilename(),
                "text/csv", resource.getInputStream());

        assertThrows(CsvImportException.class, () -> productService.importFromCsv(file));
    }
}