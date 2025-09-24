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

import it.uniroma3.siw.model.OrderLine;
import it.uniroma3.siw.model.Watch;
import it.uniroma3.siw.repository.OrderLineRepository;

@ExtendWith(MockitoExtension.class)
class OrderLineServiceTest {

    @Mock
    private OrderLineRepository orderLineRepository;

    @InjectMocks
    private OrderLineService orderLineService;

    private OrderLine testOrderLine;
    private Watch testWatch;

    @BeforeEach
    void setUp() {
        // Setup Watch
        testWatch = new Watch();
        testWatch.setId(1L);
        testWatch.setName("TEST WATCH");
        testWatch.setBrand("TEST BRAND");
        testWatch.setDescription("Test Description");
        testWatch.setYear(2024);
        testWatch.setPrice(100.0f);
        testWatch.setImagePath("test-image.jpg");

        // Setup OrderLine
        testOrderLine = new OrderLine();
        testOrderLine.setId(1L);
        testOrderLine.setQuantity(2);
        testOrderLine.setUnitPrice(100.0f);
        testOrderLine.setWatch(testWatch);
        testOrderLine.setWatchName("TEST WATCH");
        testOrderLine.setWatchBrand("TEST BRAND");
        testOrderLine.setWatchYear(2024);
    }

    @Test
    void testGetAllLines() {
        // Given
        List<OrderLine> expectedLines = Arrays.asList(testOrderLine);
        when(orderLineRepository.findAll()).thenReturn(expectedLines);

        // When
        List<OrderLine> result = orderLineService.getAllLines();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOrderLine, result.get(0));
        verify(orderLineRepository).findAll();
    }

    @Test
    void testSave() {
        // When
        orderLineService.save(testOrderLine);

        // Then
        verify(orderLineRepository).save(testOrderLine);
    }

    @Test
    void testDelete() {
        // When
        orderLineService.delete(testOrderLine);

        // Then
        verify(orderLineRepository).delete(testOrderLine);
    }

    @Test
    void testFindById() {
        // Given
        when(orderLineRepository.findById(1L)).thenReturn(Optional.of(testOrderLine));

        // When
        Optional<OrderLine> result = orderLineService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testOrderLine, result.get());
        verify(orderLineRepository).findById(1L);
    }

    @Test
    void testGetById_ExistingId() {
        // Given
        when(orderLineRepository.findById(1L)).thenReturn(Optional.of(testOrderLine));

        // When
        OrderLine result = orderLineService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testOrderLine, result);
        verify(orderLineRepository).findById(1L);
    }

    @Test
    void testGetById_NonExistingId_ThrowsException() {
        // Given
        when(orderLineRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> orderLineService.getById(999L)
        );
        
        assertTrue(exception.getMessage().contains("OrderLine con ID 999 non trovata"));
        verify(orderLineRepository).findById(999L);
    }

    @Test
    void testCreateOrderLineFromWatch_ValidWatch() {
        // When
        OrderLine result = orderLineService.createOrderLineFromWatch(testWatch, 3);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getQuantity());
        assertEquals(100.0f, result.getUnitPrice());
        assertEquals(testWatch, result.getWatch());
        
        // Verifica che i dati snapshot siano stati copiati
        assertEquals("TEST WATCH", result.getWatchName());
        assertEquals("TEST BRAND", result.getWatchBrand());
        assertEquals("Test Description", result.getWatchDescription());
        assertEquals(2024, result.getWatchYear());
        assertEquals("test-image.jpg", result.getWatchImagePath());
        
        // Verifica il calcolo del prezzo totale
        assertEquals(300.0f, result.getTotalPrice());
    }

    @Test
    void testCreateOrderLineFromWatch_NullWatch_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderLineService.createOrderLineFromWatch(null, 1)
        );
        
        assertTrue(exception.getMessage().contains("Watch non può essere null"));
    }

    @Test
    void testIncreaseQuantity() {
        // Given
        when(orderLineRepository.findById(1L)).thenReturn(Optional.of(testOrderLine));
        int initialQuantity = testOrderLine.getQuantity();

        // When
        orderLineService.increaseQuantity(1L);

        // Then
        assertEquals(initialQuantity + 1, testOrderLine.getQuantity());
        verify(orderLineRepository).findById(1L);
        verify(orderLineRepository).save(testOrderLine);
    }

    @Test
    void testDecreaseQuantity() {
        // Given
        when(orderLineRepository.findById(1L)).thenReturn(Optional.of(testOrderLine));
        int initialQuantity = testOrderLine.getQuantity();

        // When
        orderLineService.decreaseQuantity(1L);

        // Then
        assertEquals(initialQuantity - 1, testOrderLine.getQuantity());
        verify(orderLineRepository).findById(1L);
        verify(orderLineRepository).save(testOrderLine);
    }

    @Test
    void testDecreaseQuantity_QuantityZero_StaysZero() {
        // Given
        testOrderLine.setQuantity(0);
        when(orderLineRepository.findById(1L)).thenReturn(Optional.of(testOrderLine));

        // When
        orderLineService.decreaseQuantity(1L);

        // Then
        assertEquals(0, testOrderLine.getQuantity()); // Non dovrebbe andare sotto zero
        verify(orderLineRepository).save(testOrderLine);
    }

    @Test
    void testShouldRemoveOrderLine_QuantityOne_ReturnsTrue() {
        // Given
        testOrderLine.setQuantity(1);

        // When
        boolean result = orderLineService.shouldRemoveOrderLine(testOrderLine);

        // Then
        assertTrue(result);
    }

    @Test
    void testShouldRemoveOrderLine_QuantityZero_ReturnsTrue() {
        // Given
        testOrderLine.setQuantity(0);

        // When
        boolean result = orderLineService.shouldRemoveOrderLine(testOrderLine);

        // Then
        assertTrue(result);
    }

    @Test
    void testShouldRemoveOrderLine_QuantityGreaterThanOne_ReturnsFalse() {
        // Given
        testOrderLine.setQuantity(2);

        // When
        boolean result = orderLineService.shouldRemoveOrderLine(testOrderLine);

        // Then
        assertFalse(result);
    }

    @Test
    void testDeleteById() {
        // Given
        when(orderLineRepository.findById(1L)).thenReturn(Optional.of(testOrderLine));

        // When
        orderLineService.deleteById(1L);

        // Then
        verify(orderLineRepository).findById(1L);
        verify(orderLineRepository).delete(testOrderLine);
    }

    @Test
    void testDeleteById_NonExistingId_ThrowsException() {
        // Given
        when(orderLineRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> orderLineService.deleteById(999L)
        );
        
        verify(orderLineRepository).findById(999L);
        verify(orderLineRepository, never()).delete(any(OrderLine.class));
    }

    // Test per verificare il comportamento dei metodi dell'OrderLine
    @Test
    void testOrderLineTotalPrice() {
        // Given
        testOrderLine.setQuantity(3);
        testOrderLine.setUnitPrice(25.50f);

        // When
        Float totalPrice = testOrderLine.getTotalPrice();

        // Then
        assertEquals(76.50f, totalPrice, 0.01f);
    }

    @Test
    void testOrderLineTotalPrice_NullValues() {
        // Given
        testOrderLine.setQuantity(null);
        testOrderLine.setUnitPrice(null);

        // When
        Float totalPrice = testOrderLine.getTotalPrice();

        // Then
        assertEquals(0.0f, totalPrice);
    }

    @Test
    void testOrderLineIncreaseQuantityByOne() {
        // Given
        testOrderLine.setQuantity(5);

        // When
        testOrderLine.increaseQuantityByOne();

        // Then
        assertEquals(6, testOrderLine.getQuantity());
    }

    @Test
    void testOrderLineDecreaseQuantity() {
        // Given
        testOrderLine.setQuantity(5);

        // When
        testOrderLine.decreaseQuantity();

        // Then
        assertEquals(4, testOrderLine.getQuantity());
    }

    @Test
    void testOrderLineDecreaseQuantity_AlreadyZero() {
        // Given
        testOrderLine.setQuantity(0);

        // When
        testOrderLine.decreaseQuantity();

        // Then
        assertEquals(0, testOrderLine.getQuantity()); // Non dovrebbe andare sotto zero
    }
}