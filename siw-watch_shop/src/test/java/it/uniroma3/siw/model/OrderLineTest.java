package it.uniroma3.siw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderLineTest {

    @Test
    void testIncreaseQuantityByOne() {
        OrderLine ol = new OrderLine();
        ol.setQuantity(2);

        ol.increaseQuantityByOne();

        assertEquals(3, ol.getQuantity());
    }

    @Test
    void testCalculateTotal() {
        OrderLine ol = new OrderLine();
        ol.setUnitPrice(100f);
        ol.setQuantity(3);

        assertEquals(300f, ol.calculateTotal());
    }

    @Test
    void testEqualsSameWatch() {
        Watch w1 = new Watch();
        w1.setBrand("Omega");
        w1.setName("Speedmaster");
        w1.setYear(2020);

        Watch w2 = new Watch();
        w2.setBrand("Omega");
        w2.setName("Speedmaster");
        w2.setYear(2020);

        OrderLine ol1 = new OrderLine();
        ol1.setWatch(w1);

        OrderLine ol2 = new OrderLine();
        ol2.setWatch(w2);

        // equals in Watch si basa su brand+name+year
        assertEquals(ol1, ol2);
        assertEquals(ol1.hashCode(), ol2.hashCode());
    }

    @Test
    void testNotEqualsDifferentWatch() {
        Watch w1 = new Watch();
        w1.setBrand("Omega");
        w1.setName("Speedmaster");
        w1.setYear(2020);

        Watch w2 = new Watch();
        w2.setBrand("Rolex");
        w2.setName("Daytona");
        w2.setYear(2020);

        OrderLine ol1 = new OrderLine();
        ol1.setWatch(w1);

        OrderLine ol2 = new OrderLine();
        ol2.setWatch(w2);

        assertNotEquals(ol1, ol2);
    }

    @Test
    void testEqualsNullWatch() {
        OrderLine ol1 = new OrderLine();
        OrderLine ol2 = new OrderLine();

        assertNotEquals(ol1, ol2); // Entrambi hanno watch null
    }
}
