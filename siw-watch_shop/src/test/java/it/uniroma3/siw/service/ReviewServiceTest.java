package it.uniroma3.siw.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.Watch;
import it.uniroma3.siw.repository.ReviewRepository;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepo;
    
    @Mock
    private WatchService watchService;

    @InjectMocks
    private ReviewService reviewService;

    private Review testReview;
    private User testUser;
    private Watch testWatch;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        // Setup User
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setSurname("Test Surname");

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("Another User");

        // Setup Watch
        testWatch = new Watch();
        testWatch.setId(1L);
        testWatch.setName("TEST WATCH");
        testWatch.setBrand("TEST BRAND");
        testWatch.setYear(2024);

        // Setup Review
        testReview = new Review();
        testReview.setId(1L);
        testReview.setStar_rating(5.00f);
        testReview.setText("Excellent watch!");
        testReview.setWatch(testWatch);
        testReview.setUser(testUser);
        testReview.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetReviewById() {
        // Given
        when(reviewRepo.findById(1L)).thenReturn(Optional.of(testReview));

        // When
        Review result = reviewService.getReviewById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testReview.getId(), result.getId());
        assertEquals(testReview.getText(), result.getText());
        verify(reviewRepo).findById(1L);
    }

    @Test
    void testGetAllWatchReview() {
        // Given
        List<Review> expectedReviews = Arrays.asList(testReview);
        when(watchService.getWatch(1L)).thenReturn(testWatch);
        when(reviewRepo.findAllByWatch(testWatch)).thenReturn(expectedReviews);

        // When
        List<Review> result = reviewService.getAllWatchReview(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReview, result.get(0));
        verify(watchService).getWatch(1L);
        verify(reviewRepo).findAllByWatch(testWatch);
    }

    @Test
    void testGetAllWatchReview_WatchNotFound() {
        // Given
        when(watchService.getWatch(999L)).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reviewService.getAllWatchReview(999L)
        );
        
        assertEquals("orologio non trovato nel sistema", exception.getMessage());
        verify(watchService).getWatch(999L);
        verify(reviewRepo, never()).findAllByWatch(any());
    }

    @Test
    void testGetAllWatchReviewExclutedCurrentUserReview() {
        // Given
        Review anotherReview = new Review();
        anotherReview.setUser(anotherUser);
        List<Review> expectedReviews = Arrays.asList(anotherReview);
        
        when(reviewRepo.findAllByWatchAndUserNot(testWatch, testUser)).thenReturn(expectedReviews);

        // When
        List<Review> result = reviewService.getAllWatchReviewExclutedCurrentUserReview(testWatch, testUser);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(anotherReview, result.get(0));
        verify(reviewRepo).findAllByWatchAndUserNot(testWatch, testUser);
    }

    @Test
    void testGetAllWatchReviewExclutedCurrentUserReview_NullParameters() {
        // When & Then (test watch null)
        IllegalArgumentException exception1 = assertThrows(
            IllegalArgumentException.class,
            () -> reviewService.getAllWatchReviewExclutedCurrentUserReview(null, testUser)
        );
        assertEquals("orologio od utent non trovati nel sistema", exception1.getMessage());

        // When & Then (test user null)
        IllegalArgumentException exception2 = assertThrows(
            IllegalArgumentException.class,
            () -> reviewService.getAllWatchReviewExclutedCurrentUserReview(testWatch, null)
        );
        assertEquals("orologio od utent non trovati nel sistema", exception2.getMessage());
    }

    @Test
    void testAddReview() {
        // Given
        Review newReview = new Review();
        newReview.setStar_rating(4.00f);
        newReview.setText("Good watch");
        
        when(watchService.getWatch(1L)).thenReturn(testWatch);

        // When
        reviewService.addReview(newReview, 1L, testUser);

        // Then
        assertEquals(testWatch, newReview.getWatch());
        assertEquals(testUser, newReview.getUser());
        assertNotNull(newReview.getCreatedAt());
        
        verify(watchService).getWatch(1L);
        verify(reviewRepo).save(newReview);
        verify(watchService).addReviewToWatchList(newReview.getId());
    }

    @Test
    void testAddReview_NullReview() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reviewService.addReview(null, 1L, testUser)
        );
        
        assertEquals("review nulla", exception.getMessage());
        verify(reviewRepo, never()).save(any());
        verify(watchService, never()).addReviewToWatchList(any());
    }

    @Test
    void testAddReview_NullWatchId() {
        // Given
        Review newReview = new Review();
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reviewService.addReview(newReview, null, testUser)
        );
        
        assertEquals("review nulla", exception.getMessage());
    }

    @Test
    void testGetWatchAverageRating() {
        // Given
        Float expectedRating = 4.5f;
        when(reviewRepo.getAverageWatchRating(1L)).thenReturn(expectedRating);

        // When
        Float result = reviewService.getWatchAverageRating(1L);

        // Then
        assertEquals(expectedRating, result);
        verify(reviewRepo).getAverageWatchRating(1L);
    }

    @Test
    void testCountWatchRating() {
        // Given
        Integer expectedCount = 10;
        when(reviewRepo.countWatchRating(1L)).thenReturn(expectedCount);

        // When
        Integer result = reviewService.countWatchRating(1L);

        // Then
        assertEquals(expectedCount, result);
        verify(reviewRepo).countWatchRating(1L);
    }

    @Test
    void testHasUserReviewedWatch_True() {
        // Given
        when(reviewRepo.existsByWatchIdAndUserId(1L, 1L)).thenReturn(true);

        // When
        boolean result = reviewService.hasUserReviewedWatch(testWatch, testUser);

        // Then
        assertTrue(result);
        verify(reviewRepo).existsByWatchIdAndUserId(1L, 1L);
    }

    @Test
    void testHasUserReviewedWatch_False() {
        // Given
        when(reviewRepo.existsByWatchIdAndUserId(1L, 1L)).thenReturn(false);

        // When
        boolean result = reviewService.hasUserReviewedWatch(testWatch, testUser);

        // Then
        assertFalse(result);
        verify(reviewRepo).existsByWatchIdAndUserId(1L, 1L);
    }

    @Test
    void testGetUserReviewedWatch_Exists() {
        // Given
        when(reviewRepo.existsByWatchIdAndUserId(1L, 1L)).thenReturn(true);
        when(reviewRepo.findByWatchIdAndUserId(1L, 1L)).thenReturn(testReview);

        // When
        Review result = reviewService.getUserReviewedWatch(testWatch, testUser);

        // Then
        assertNotNull(result);
        assertEquals(testReview, result);
        verify(reviewRepo).existsByWatchIdAndUserId(1L, 1L);
        verify(reviewRepo).findByWatchIdAndUserId(1L, 1L);
    }

    @Test
    void testGetUserReviewedWatch_DoesNotExist() {
        // Given
        when(reviewRepo.existsByWatchIdAndUserId(1L, 1L)).thenReturn(false);

        // When
        Review result = reviewService.getUserReviewedWatch(testWatch, testUser);

        // Then
        assertNull(result);
        verify(reviewRepo).existsByWatchIdAndUserId(1L, 1L);
        verify(reviewRepo, never()).findByWatchIdAndUserId(any(), any());
    }

    @Test
    void testDeleteReview() {
        // When
        reviewService.deleteReview(testReview);

        // Then
        verify(watchService).deleteReviewFromWatchList(1L);
        verify(reviewRepo).delete(testReview);
        verify(watchService).calcAndSetAverageRating(testWatch);
        verify(watchService).calcAndSetRatingCount(testWatch);
        verify(watchService).save(testWatch);
    }

    @Test
    void testEditReview() {
        // Given
        Review formReview = new Review();
        formReview.setText("Updated review text");
        formReview.setStar_rating(3.00f);
        
        when(reviewRepo.findById(1L)).thenReturn(Optional.of(testReview));
        
        LocalDateTime beforeEdit = testReview.getCreatedAt();

        // When
        reviewService.editReview(1L, formReview);

        // Then
        assertEquals("Updated review text", testReview.getText());
        assertEquals(3, testReview.getStar_rating());
        assertNotEquals(beforeEdit, testReview.getCreatedAt()); // dovrebbe essere aggiornato
        
        verify(reviewRepo).findById(1L);
        verify(reviewRepo).save(testReview);
    }

    @Test
    void testSave() {
        // When
        reviewService.save(testReview);

        // Then
        verify(reviewRepo).save(testReview);
    }

    // ========== TEST INTEGRATION SCENARIOS ==========

    @Test
    void testCompleteReviewWorkflow() {
        // Scenario: Un utente aggiunge una review, poi la modifica, poi la elimina
        
        // 1. Aggiunta review
        Review newReview = new Review();
        newReview.setStar_rating(4.00f);
        newReview.setText("Initial review");
        
        when(watchService.getWatch(1L)).thenReturn(testWatch);
        when(reviewRepo.save(any())).thenAnswer(invocation -> {
            Review review = invocation.getArgument(0);
            review.setId(2L);
            return review;
        });
        
        reviewService.addReview(newReview, 1L, testUser);
        
        // Verifica aggiunta
        assertEquals(testWatch, newReview.getWatch());
        assertEquals(testUser, newReview.getUser());
        assertNotNull(newReview.getCreatedAt());
        
        // 2. Modifica review
        Review editForm = new Review();
        editForm.setText("Updated review");
        editForm.setStar_rating(5.00f);
        
        when(reviewRepo.findById(2L)).thenReturn(Optional.of(newReview));
        
        reviewService.editReview(2L, editForm);
        
        // Verifica modifica
        assertEquals("Updated review", newReview.getText());
        assertEquals(5, newReview.getStar_rating());
        
        // 3. Eliminazione review
        reviewService.deleteReview(newReview);
        
        // Verifica eliminazione
        verify(watchService).deleteReviewFromWatchList(2L);
        verify(reviewRepo).delete(newReview);
        verify(watchService).calcAndSetAverageRating(testWatch);
        verify(watchService).calcAndSetRatingCount(testWatch);
    }

    @Test
    void testMultipleUsersReviews() {
        // Given
        Review userReview = new Review();
        userReview.setUser(testUser);
        userReview.setStar_rating(5.00f);
        
        Review anotherUserReview = new Review();
        anotherUserReview.setUser(anotherUser);
        anotherUserReview.setStar_rating(3.00f);
        
        List<Review> allReviews = Arrays.asList(userReview, anotherUserReview);
        List<Review> excludingUser = Arrays.asList(anotherUserReview);
        
        when(watchService.getWatch(1L)).thenReturn(testWatch);
        when(reviewRepo.findAllByWatch(testWatch)).thenReturn(allReviews);
        when(reviewRepo.findAllByWatchAndUserNot(testWatch, testUser)).thenReturn(excludingUser);
        when(reviewRepo.existsByWatchIdAndUserId(1L, 1L)).thenReturn(true);
        when(reviewRepo.findByWatchIdAndUserId(1L, 1L)).thenReturn(userReview);

        // Test get all reviews
        List<Review> allResult = reviewService.getAllWatchReview(1L);
        assertEquals(2, allResult.size());
        
        // Test get reviews excluding current user
        List<Review> excludedResult = reviewService.getAllWatchReviewExclutedCurrentUserReview(testWatch, testUser);
        assertEquals(1, excludedResult.size());
        assertEquals(anotherUserReview, excludedResult.get(0));
        
        // Test user has reviewed
        assertTrue(reviewService.hasUserReviewedWatch(testWatch, testUser));
        
        // Test get user's review
        Review userReviewResult = reviewService.getUserReviewedWatch(testWatch, testUser);
        assertEquals(userReview, userReviewResult);
    }

    @Test
    void testReviewStatisticsFlow() {
        // Given
        when(reviewRepo.getAverageWatchRating(1L)).thenReturn(4.2f);
        when(reviewRepo.countWatchRating(1L)).thenReturn(15);

        // When
        Float avgRating = reviewService.getWatchAverageRating(1L);
        Integer count = reviewService.countWatchRating(1L);

        // Then
        assertEquals(4.2f, avgRating);
        assertEquals(15, count);
        
        verify(reviewRepo).getAverageWatchRating(1L);
        verify(reviewRepo).countWatchRating(1L);
    }
}