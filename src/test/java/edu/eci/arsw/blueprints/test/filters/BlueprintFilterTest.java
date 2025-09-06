package edu.eci.arsw.blueprints.test.filters;

import edu.eci.arsw.blueprints.filters.impl.RedundancyFilter;
import edu.eci.arsw.blueprints.filters.impl.SubsamplingFilter;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.junit.Test;

import static org.junit.Assert.*;

public class BlueprintFilterTest {

    @Test
    public void testRedundancyFilterRemovesConsecutiveDuplicates() {
        RedundancyFilter filter = new RedundancyFilter();

        Point[] points = {
                new Point(0, 0),
                new Point(0, 0), // duplicado
                new Point(1, 1),
                new Point(1, 1), // duplicado
                new Point(2, 2)
        };

        Blueprint bp = new Blueprint("jeimy", "casa", points);
        Blueprint filtered = filter.applyFilter(bp);

        assertEquals("Debe remover duplicados consecutivos", 3, filtered.getPoints().size());

        assertEquals(0, filtered.getPoints().get(0).getX());
        assertEquals(0, filtered.getPoints().get(0).getY());

        assertEquals(1, filtered.getPoints().get(1).getX());
        assertEquals(1, filtered.getPoints().get(1).getY());

        assertEquals(2, filtered.getPoints().get(2).getX());
        assertEquals(2, filtered.getPoints().get(2).getY());
    }

    @Test
    public void testSubsamplingFilterRemovesEveryOtherPoint() {
        SubsamplingFilter filter = new SubsamplingFilter();

        Point[] points = {
                new Point(0, 0),
                new Point(1, 1),
                new Point(2, 2),
                new Point(3, 3),
                new Point(4, 4)
        };

        Blueprint bp = new Blueprint("jeimy", "apartamento", points);
        Blueprint filtered = filter.applyFilter(bp);

        assertEquals("Debe quedarse con 1 de cada 2 puntos", 3, filtered.getPoints().size());

        assertEquals(0, filtered.getPoints().get(0).getX());
        assertEquals(0, filtered.getPoints().get(0).getY());

        assertEquals(2, filtered.getPoints().get(1).getX());
        assertEquals(2, filtered.getPoints().get(1).getY());

        assertEquals(4, filtered.getPoints().get(2).getX());
        assertEquals(4, filtered.getPoints().get(2).getY());
    }
}
