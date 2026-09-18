package com.shubham.app.stocktrading.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.stocktrading.ideal.service.RestingOrderPriceStrategy;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderBookTest {

    private final OrderBook book = new OrderBook(1, new RestingOrderPriceStrategy());

    @Test
    void anOrderWithNoCrossingCounterpartyJustRestsAndProducesNoTrades() {
        List<Trade> trades = book.submit(new Order(0, 1, 1, Side.SELL, 100.0, 10));

        assertTrue(trades.isEmpty());
    }

    @Test
    void aCrossingOrderFullyFillsASingleRestingOrderAtTheRestingPrice() {
        book.submit(new Order(0, 2, 1, Side.SELL, 100.0, 10));

        List<Trade> trades = book.submit(new Order(1, 1, 1, Side.BUY, 100.0, 10));

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(100.0, trade.getPrice());
        assertEquals(10, trade.getUnits());
        assertEquals(1, trade.getBuyerId());
        assertEquals(2, trade.getSellerId());
    }

    // Regression test for practice/session-04-stock-trading.md's core gap:
    // price-time
    // priority across multiple resting price levels, with a partial fill on the
    // second level.
    @Test
    void aCrossingOrderConsumesTheBestPriceLevelFirstThenPartiallyFillsTheNextLevel() {
        book.submit(new Order(0, 2, 1, Side.SELL, 100.0, 10));
        book.submit(new Order(1, 3, 1, Side.SELL, 101.0, 5));

        List<Trade> trades = book.submit(new Order(2, 1, 1, Side.BUY, 101.0, 12));

        assertEquals(2, trades.size());
        assertEquals(100.0, trades.get(0).getPrice());
        assertEquals(10, trades.get(0).getUnits());
        assertEquals(101.0, trades.get(1).getPrice());
        assertEquals(2, trades.get(1).getUnits());
    }

    @Test
    void anOrderThatDoesNotCrossAnyRestingPriceJustRests() {
        book.submit(new Order(0, 2, 1, Side.SELL, 101.0, 5));

        List<Trade> trades = book.submit(new Order(1, 1, 1, Side.BUY, 50.0, 3));

        assertTrue(trades.isEmpty());
    }

    @Test
    void aPartiallyFilledRestingOrderStaysInTheBookForTheNextIncomingOrder() {
        book.submit(new Order(0, 2, 1, Side.SELL, 100.0, 10));
        book.submit(new Order(1, 1, 1, Side.BUY, 100.0, 4));

        List<Trade> secondTrades = book.submit(new Order(2, 3, 1, Side.BUY, 100.0, 6));

        assertEquals(1, secondTrades.size());
        assertEquals(6, secondTrades.get(0).getUnits());
    }

    @Test
    void concurrentCrossingOrdersEachMatchExactlyOneUnitWithNoLostOrDuplicatedFills() throws InterruptedException {
        int sellOrderCount = 20;
        for (int i = 0; i < sellOrderCount; i++) {
            book.submit(new Order(i, 100 + i, 1, Side.SELL, 50.0, 1));
        }

        ExecutorService executor = Executors.newFixedThreadPool(sellOrderCount);
        AtomicInteger totalTradedUnits = new AtomicInteger(0);

        for (int i = 0; i < sellOrderCount; i++) {
            int buyOrderId = 1000 + i;
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    List<Trade> trades = book.submit(new Order(buyOrderId, 1, 1, Side.BUY, 50.0, 1));
                    int unitsTraded = 0;
                    for (Trade trade : trades) {
                        unitsTraded += trade.getUnits();
                    }
                    totalTradedUnits.addAndGet(unitsTraded);
                }
            });
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(sellOrderCount, totalTradedUnits.get());
    }
}
