package it.uniroma3.siw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WatchTest {

    @Test
    void testEqualsSameAttributes() {
        Watch w1 = new Watch();
        w1.setBrand("Omega");
        w1.setName("Speedmaster");
        w1.setYear(2020);

        Watch w2 = new Watch();
        w2.setBrand("Omega");
        w2.setName("Speedmaster");
        w2.setYear(2020);

        assertEquals(w1, w2);
        assertEquals(w1.hashCode(), w2.hashCode());
    }

    @Test
    void testNotEqualsDifferentBrand() {
        Watch w1 = new Watch();
        w1.setBrand("Omega");
        w1.setName("Speedmaster");
        w1.setYear(2020);

        Watch w2 = new Watch();
        w2.setBrand("Rolex");
        w2.setName("Speedmaster");
        w2.setYear(2020);

        assertNotEquals(w1, w2);
    }

    @Test
    void testIsAvailable() {
        Watch watch = new Watch();
        watch.setStock(3);

        assertTrue(watch.isAvailable());

        watch.setStock(0);
        assertFalse(watch.isAvailable());
    }

    @Test
    void testIncrementStock() {
        Watch watch = new Watch();
        watch.setStock(1);
        watch.incrementStock();

        assertEquals(2, watch.getStock());
    }

    @Test
    void testDecrementStock() {
        Watch watch = new Watch();
        watch.setStock(2);
        watch.decrementStock();

        assertEquals(1, watch.getStock());
    }

    @Test
    void testDecrementStockWhenZero() {
        Watch watch = new Watch();
        watch.setStock(0);
        watch.decrementStock();

        assertEquals(0, watch.getStock(), "Stock should not go negative");
    }
}
