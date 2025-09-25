package it.uniroma3.siw.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.Watch;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.repository.WatchRepository;

@ExtendWith(MockitoExtension.class)
class WatchServiceTest {

    @Mock
    private WatchRepository watchRepo;
    
    @Mock
    private ReviewRepository reviewRepo;
    
    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private WatchService watchService;

    private Watch testWatch;
    private Watch unavailableWatch;
    private Review testReview;

    @BeforeEach
    void setUp() {
        testWatch = new Watch();
        testWatch.setId(1L);
        testWatch.setName("test watch");
        testWatch.setBrand("test brand");
        testWatch.setYear(2024);
        testWatch.setPrice(100.0f);
        testWatch.setAvailability(true);

        unavailableWatch = new Watch();
        unavailableWatch.setId(2L);
        unavailableWatch.setName("unavailable watch");
        unavailableWatch.setBrand("test brand");
        unavailableWatch.setYear(2023);
        unavailableWatch.setPrice(200.0f);
        unavailableWatch.setAvailability(false);

        testReview = new Review();
        testReview.setId(1L);
        testReview.setStar_rating(5.00f);
        testReview.setWatch(testWatch);
    }

    // ========== TEST METODI AVAILABLE (per utenti normali) ==========

    @Test
    void testGetAvailableWatch_ById() {
        // Given
        when(watchRepo.findByIdAndAvailabilityTrue(1L)).thenReturn(Optional.of(testWatch));

        // When
        Watch result = watchService.getAvailableWatch(1L);

        // Then
        assertNotNull(result);
        assertEquals(testWatch.getId(), result.getId());
        assertTrue(result.isAvailability());
        verify(watchRepo).findByIdAndAvailabilityTrue(1L);
    }

    @Test
    void testGetAvailableWatch_ByNameBrandYear() {
        // Given
        when(watchRepo.findByNameAndBrandAndYearAndAvailabilityTrue("TEST WATCH", "TEST BRAND", 2024))
            .thenReturn(Optional.of(testWatch));

        // When
        Watch result = watchService.getAvailableWatch("test watch", "test brand", 2024);

        // Then
        assertNotNull(result);
        assertEquals(testWatch, result);
        verify(watchRepo).findByNameAndBrandAndYearAndAvailabilityTrue("TEST WATCH", "TEST BRAND", 2024);
    }

    @Test
    void testGetAvailableWatch_ByNameBrand() {
        // Given
        List<Watch> expectedWatches = Arrays.asList(testWatch);
        when(watchRepo.findByNameAndBrandAndAvailabilityTrue("TEST WATCH", "TEST BRAND"))
            .thenReturn(expectedWatches);

        // When
        List<Watch> result = watchService.getAvailableWatch("test watch", "test brand");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testWatch, result.get(0));
    }

    @Test
    void testGetAllAvailableWatchesBySearchBar() {
        // Given
        List<Watch> expectedWatches = Arrays.asList(testWatch);
        when(watchRepo.findAllAvailableBySearchBar("TEST")).thenReturn(expectedWatches);

        // When
        List<Watch> result = watchService.getAllAvailableWatchesBySearchBar("test");

        // Then
        assertEquals(1, result.size());
        assertEquals(testWatch, result.get(0));
        verify(watchRepo).findAllAvailableBySearchBar("TEST");
    }

    @Test
    void testGetAllAvailableWatches() {
        // Given
        List<Watch> expectedWatches = Arrays.asList(testWatch);
        when(watchRepo.findAllAvailable()).thenReturn(expectedWatches);

        // When
        Iterable<Watch> result = watchService.getAllAvailableWatches();

        // Then
        assertNotNull(result);
        verify(watchRepo).findAllAvailable();
    }

    @Test
    void testExistsByNameAndBrandAndYearAndAvailability() {
        // Given
        when(watchRepo.existsByNameAndBrandAndYearAndAvailabilityTrue("TEST WATCH", "TEST BRAND", 2024))
            .thenReturn(true);

        // When
        boolean exists = watchService.existsByNameAndBrandAndYearAndAvailability("test watch", "test brand", 2024);

        // Then
        assertTrue(exists);
    }

    @Test
    void testGetAllAvailableWatchesByBrand() {
        // Given
        List<Watch> expectedWatches = Arrays.asList(testWatch);
        when(watchRepo.findAllAvailableByBrand("TEST BRAND")).thenReturn(expectedWatches);

        // When
        List<Watch> result = watchService.getAllAvailableWatchesByBrand("test brand");

        // Then
        assertEquals(1, result.size());
        assertEquals(testWatch, result.get(0));
    }

    // ========== TEST METODI GENERALI (admin/user) ==========

    @Test
    void testGetWatch_AsAdmin() {
        // Given
        when(authenticationService.isAdmin()).thenReturn(true);
        when(watchRepo.findById(1L)).thenReturn(Optional.of(testWatch));

        // When
        Watch result = watchService.getWatch(1L);

        // Then
        assertNotNull(result);
        assertEquals(testWatch.getId(), result.getId());
        verify(watchRepo).findById(1L);
        verify(watchRepo, never()).findByIdAndAvailabilityTrue(any());
    }

    @Test
    void testGetWatch_AsUser() {
        // Given
        when(authenticationService.isAdmin()).thenReturn(false);
        when(watchRepo.findByIdAndAvailabilityTrue(1L)).thenReturn(Optional.of(testWatch));

        // When
        Watch result = watchService.getWatch(1L);

        // Then
        assertNotNull(result);
        assertEquals(testWatch.getId(), result.getId());
        verify(watchRepo, never()).findById(any());
        verify(watchRepo).findByIdAndAvailabilityTrue(1L);
    }

    @Test
    void testGetAllWatches_AsAdmin() {
        // Given
        List<Watch> allWatches = Arrays.asList(testWatch, unavailableWatch);
        when(authenticationService.isAdmin()).thenReturn(true);
        when(watchRepo.findAll()).thenReturn(allWatches);

        // When
        Iterable<Watch> result = watchService.getAllWatches();

        // Then
        assertNotNull(result);
        verify(watchRepo).findAll();
        verify(watchRepo, never()).findAllAvailable();
    }

    @Test
    void testGetAllWatches_AsUser() {
        // Given
        List<Watch> availableWatches = Arrays.asList(testWatch);
        when(authenticationService.isAdmin()).thenReturn(false);
        when(watchRepo.findAllAvailable()).thenReturn(availableWatches);

        // When
        Iterable<Watch> result = watchService.getAllWatches();

        // Then
        assertNotNull(result);
        verify(watchRepo, never()).findAll();
        verify(watchRepo).findAllAvailable();
    }

    // ========== TEST METODI ADMIN ==========

    @Test
    void testDeactivateWatch_AsAdmin() {
        // Given
        when(authenticationService.isAdmin()).thenReturn(true);
        when(watchRepo.findById(1L)).thenReturn(Optional.of(testWatch));

        // When
        watchService.deactivateWatch(1L);

        // Then
        assertFalse(testWatch.isAvailability());
        verify(watchRepo).findById(1L);
        verify(watchRepo).save(testWatch);
    }

    @Test
    void testDeactivateWatch_AsUser_ThrowsException() {
        // Given
        when(authenticationService.isAdmin()).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> watchService.deactivateWatch(1L)
        );
        
        assertEquals("non admin", exception.getMessage());
        verify(watchRepo, never()).findById(any());
        verify(watchRepo, never()).save(any());
    }

    @Test
    void testReactivateWatch_AsAdmin() {
        // Given
        unavailableWatch.setAvailability(false);
        when(authenticationService.isAdmin()).thenReturn(true);
        when(watchRepo.findById(2L)).thenReturn(Optional.of(unavailableWatch));

        // When
        watchService.reactivateWatch(2L);

        // Then
        assertTrue(unavailableWatch.isAvailability());
        verify(watchRepo).findById(2L);
        verify(watchRepo).save(unavailableWatch);
    }

    @Test
    void testReactivateWatch_AsUser_ThrowsException() {
        // Given
        when(authenticationService.isAdmin()).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> watchService.reactivateWatch(1L)
        );
        
        assertEquals("non admin", exception.getMessage());
    }

    // ========== TEST SAVE E NORMALIZZAZIONE ==========

    @Test
    void testSave_NormalizesNameAndBrand() {
        // Given
        testWatch.setName("  Test Watch  ");
        testWatch.setBrand("  Test Brand  ");

        // When
        watchService.save(testWatch);

        // Then
        verify(watchRepo).save(testWatch);
        assertEquals("TEST WATCH", testWatch.getName());
        assertEquals("TEST BRAND", testWatch.getBrand());
    }

    @Test
    void testSave_HandlesNullValues() {
        // Given
        testWatch.setName("test");
        testWatch.setBrand("brand");

        // When
        watchService.save(testWatch);

        // Then
        assertEquals("TEST", testWatch.getName());
        assertEquals("BRAND", testWatch.getBrand());
        verify(watchRepo).save(testWatch);
    }

    // ========== TEST REVIEW MANAGEMENT ==========


    @Test
    void testCalcAndSetAverageRating() {
        // Given
        when(reviewRepo.getAverageWatchRating(1L)).thenReturn(4.2f);

        // When
        watchService.calcAndSetAverageRating(testWatch);

        // Then
        assertEquals(4.2f, testWatch.getAverageRating());
        verify(reviewRepo).getAverageWatchRating(1L);
    }

    @Test
    void testCalcAndSetRatingCount() {
        // Given
        when(reviewRepo.countWatchRating(1L)).thenReturn(15);

        // When
        watchService.calcAndSetRatingCount(testWatch);

        // Then
        assertEquals(15, testWatch.getRatingCount());
        verify(reviewRepo).countWatchRating(1L);
    }

    

    // ========== TEST SEARCH E BRAND ==========

    @Test
    void testGetAllBrands() {
        // Given
        List<String> expectedBrands = Arrays.asList("ROLEX", "OMEGA", "SEIKO");
        when(watchRepo.findAllBrands()).thenReturn(expectedBrands);

        // When
        List<String> result = watchService.getAllBrands();

        // Then
        assertEquals(3, result.size());
        assertTrue(result.contains("ROLEX"));
        verify(watchRepo).findAllBrands();
    }

   

    @Test
    void testGetAllByBrand_AsUser() {
        // Given
        List<Watch> expectedWatches = Arrays.asList(testWatch);
        when(authenticationService.isAdmin()).thenReturn(false);
        when(watchRepo.findAllAvailableByBrand("TEST BRAND")).thenReturn(expectedWatches);

        // When
        List<Watch> result = watchService.getAllByBrand("test brand");

        // Then
        assertEquals(1, result.size());
        verify(watchRepo, never()).findAllByBrand(any());
    }

    // ========== TEST DELETE ==========

    @Test
    void testDelete() {
        // When
        watchService.delete(1L);

        // Then
        verify(watchRepo).deleteById(1L);
    }

    @Test
    void testDeleteWatch() {
        // When
        watchService.deleteWatch(1L);

        // Then
        verify(watchRepo).deleteById(1L);
    }

    // ========== TEST EXCEPTION HANDLING ==========

    @Test
    void testDeactivateWatch_WatchNotFound() {
        // Given
        when(authenticationService.isAdmin()).thenReturn(true);
        when(watchRepo.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> watchService.deactivateWatch(999L));
    }

    @Test
    void testAddReviewToWatchList_InvalidReview() {
        // Given
        testReview.setWatch(null);
        when(reviewRepo.findById(1L)).thenReturn(Optional.of(testReview));

        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> watchService.addReviewToWatchList(1L));
    }
}