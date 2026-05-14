package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.subscription.SubscriptionDTO;
import com.senla.ProductService.mapper.SubscriptionMapper;
import com.senla.ProductService.model.Product;
import com.senla.ProductService.model.ProductPrice;
import com.senla.ProductService.model.Shop;
import com.senla.ProductService.model.ShopBranch;
import com.senla.ProductService.model.Subscription;
import com.senla.ProductService.repository.SubscriptionRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private SubscriptionMapper subscriptionMapper;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private Subscription subscription;
    private Product product;
    private ShopBranch shopBranch;
    private Shop shop;
    private SubscriptionDTO subscriptionDTO;

    private final UUID userId = UUID.randomUUID();
    private final UUID productPriceId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        shop = new Shop();
        shop.setId(UUID.randomUUID());
        shop.setName("shop");

        shopBranch = new ShopBranch();
        shopBranch.setId(UUID.randomUUID());
        shopBranch.setShop(shop);

        product = new Product();
        product.setName("product");

        subscription = new Subscription();
        subscription.setId(UUID.randomUUID());
        subscription.setUserId(userId);
        subscriptionDTO = new SubscriptionDTO(subscription.getId(), UUID.randomUUID(), product.getName(),
                UUID.randomUUID(), shop.getName(), "address",
                100.00, 0, LocalDate.now()
        );
    }

    @Test
    void saveShouldThrowEntityExistsException() {
        when(subscriptionRepository.findByUserIdAndProductPriceId(userId, productPriceId))
                .thenReturn(true);

        Subscription existingSubscription = new Subscription();
        existingSubscription.setUserId(userId);

        ProductPrice productPrice = new ProductPrice();
        productPrice.setId(productPriceId);
        existingSubscription.setProductPrice(productPrice);

        assertThrows(EntityExistsException.class, () -> subscriptionService.save(existingSubscription));
    }

    @Test
    void saveShouldCallRepositorySaveMethod() {
        when(subscriptionRepository.findByUserIdAndProductPriceId(userId, productPriceId)).thenReturn(false);

        Subscription newSubscription = new Subscription();
        newSubscription.setUserId(userId);

        ProductPrice productPrice = new ProductPrice();
        productPrice.setId(productPriceId);
        productPrice.setProduct(product);
        productPrice.setShopBranch(shopBranch);
        newSubscription.setProductPrice(productPrice);

        subscriptionService.save(newSubscription);

        verify(subscriptionRepository).findByUserIdAndProductPriceId(userId, productPriceId);
        verify(subscriptionRepository).save(newSubscription);
    }

    @Test
    void findByProductPriceIdShouldReturnList() {
        List<Subscription> subscriptions = List.of(subscription);
        when(subscriptionRepository.findByProductPriceId(productPriceId)).thenReturn(subscriptions);

        List<Subscription> result = subscriptionService.findByProductPriceId(productPriceId);

        assertEquals(1, result.size());
        verify(subscriptionRepository).findByProductPriceId(productPriceId);
    }

    @Test
    void findByUserIdShouldReturnList() {
        List<Subscription> subscriptions = List.of(subscription);
        when(subscriptionRepository.findByUserId(userId)).thenReturn(subscriptions);

        List<Subscription> result = subscriptionService.findByUserId(userId);

        assertEquals(1, result.size());
        verify(subscriptionRepository).findByUserId(userId);
    }

    @Test
    void deleteByUserIdShouldDeleteAllUserSubscriptions() {
        List<Subscription> subscriptions = List.of(subscription);

        when(subscriptionRepository.findByUserId(userId)).thenReturn(subscriptions);
        doNothing().when(subscriptionRepository).delete(any(Subscription.class));

        subscriptionService.deleteByUserId(userId);

        verify(subscriptionRepository).findByUserId(userId);
        verify(subscriptionRepository, times(1)).delete(any(Subscription.class));
    }

    @Test
    void findAllShouldReturnUserSubscriptions() {
        try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            when(SecurityContextHolder.getContext()).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getDetails()).thenReturn(userId);

            List<Subscription> subscriptions = List.of(subscription);
            when(subscriptionRepository.findAllByUserId(userId)).thenReturn(subscriptions);
            when(subscriptionMapper.subscriptionToSubscriptionDTO(subscription)).thenReturn(subscriptionDTO);

            List<SubscriptionDTO> result = subscriptionService.findAll();

            assertEquals(1, result.size());
            assertEquals(subscriptionDTO, result.get(0));

            verify(subscriptionRepository).findAllByUserId(userId);
            verify(subscriptionMapper).subscriptionToSubscriptionDTO(subscription);
        }
    }

    @Test
    void findByIdIfExistsShouldReturnSubscription() {
        when(subscriptionRepository.findById(subscription.getId())).thenReturn(Optional.of(subscription));

        Subscription result = subscriptionService.findByIdIfExists(subscription.getId());

        assertEquals(subscription.getId(), result.getId());
        verify(subscriptionRepository).findById(subscription.getId());
    }

    @Test
    void findByIdIfExistsShouldThrowEntityNotFoundException() {
        when(subscriptionRepository.findById(subscription.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> subscriptionService.findByIdIfExists(subscription.getId()));
    }

    @Test
    void findByIdWithFetchShouldReturnSubscription() {
        when(subscriptionRepository.findByIdWithFetch(subscription.getId())).thenReturn(Optional.of(subscription));

        Subscription result = subscriptionService.findByIdWithFetch(subscription.getId());

        assertNotNull(result);
        verify(subscriptionRepository).findByIdWithFetch(subscription.getId());
    }

    @Test
    void deleteByIdShouldDeleteExistingSubscription() {
        when(subscriptionRepository.findById(subscription.getId())).thenReturn(Optional.of(subscription));
        doNothing().when(subscriptionRepository).delete(subscription);

        subscriptionService.deleteById(subscription.getId());

        verify(subscriptionRepository).findById(subscription.getId());
        verify(subscriptionRepository).delete(subscription);
    }
}