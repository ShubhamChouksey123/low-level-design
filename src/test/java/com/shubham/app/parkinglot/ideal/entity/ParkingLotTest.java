package com.shubham.app.parkinglot.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.parkinglot.ideal.exception.ParkingFullException;
import com.shubham.app.parkinglot.ideal.service.FirstAvailableSpotAllocationStrategy;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParkingLotTest {

    @Test
    void claimVacantSpotReturnsADifferentSpotEachTimeUntilFull() {
        ParkingLot lot = new ParkingLot(List.of(new Spot(0), new Spot(1)), new FirstAvailableSpotAllocationStrategy());

        Spot first = lot.claimVacantSpot();
        Spot second = lot.claimVacantSpot();

        assertTrue(first.getId() != second.getId());
        assertThrows(ParkingFullException.class, lot::claimVacantSpot);
    }

    @Test
    void releasingASpotMakesItClaimableAgain() {
        ParkingLot lot = new ParkingLot(List.of(new Spot(0)), new FirstAvailableSpotAllocationStrategy());
        Spot claimed = lot.claimVacantSpot();

        lot.releaseSpot(claimed);

        Spot claimedAgain = lot.claimVacantSpot();
        assertEquals(claimed.getId(), claimedAgain.getId());
    }

    @Test
    void concurrentClaimsNeverDoubleBookTheSameSpot() throws InterruptedException {
        int spotCount = 20;
        List<Spot> spots = java.util.stream.IntStream.range(0, spotCount).mapToObj(Spot::new).toList();
        ParkingLot lot = new ParkingLot(spots, new FirstAvailableSpotAllocationStrategy());

        ExecutorService executor = Executors.newFixedThreadPool(spotCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger fullCount = new AtomicInteger(0);

        for (int i = 0; i < spotCount * 2; i++) {
            executor.submit(() -> {
                try {
                    lot.claimVacantSpot();
                    successCount.incrementAndGet();
                } catch (ParkingFullException e) {
                    fullCount.incrementAndGet();
                }
            });
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(spotCount, successCount.get());
        assertEquals(spotCount, fullCount.get());
        assertTrue(spots.stream().noneMatch(Spot::isVacant));
    }
}
