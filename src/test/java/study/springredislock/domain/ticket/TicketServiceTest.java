package study.springredislock.domain.ticket;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class TicketServiceTest {

    @Autowired
    TicketService ticketService;

    @Autowired
    TicketRepository ticketRepository;

    private Long TICKET_ID = null;
    private final Integer CONCURRENT_COUNT = 100;

    @BeforeEach
    public void before() {
        log.info("1000개의 티켓 생성");
        Ticket ticket = Ticket.create(1000L);
        Ticket saved = ticketRepository.saveAndFlush(ticket);
        TICKET_ID = saved.getId();
    }

    @AfterEach
    public void after() {
        ticketRepository.deleteAll();
    }

    private void ticketingTest(Consumer<Void> action) throws InterruptedException {
        Long originQuantity = ticketRepository.findById(TICKET_ID).orElseThrow().getQuantity();

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_COUNT);

        for (int i = 0; i < CONCURRENT_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    action.accept(null);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Ticket ticket = ticketRepository.findById(TICKET_ID).orElseThrow();
        assertEquals(originQuantity - CONCURRENT_COUNT, ticket.getQuantity());
    }

    @Test
    @DisplayName("동시에 100명의 티켓팅 : 동시성 이슈")
    public void badTicketingTest() throws Exception {
        ticketingTest((_no) -> ticketService.ticketing(TICKET_ID, 1L));
    }

//    @Test
//    @DisplayName("동시에 100명의 티켓팅 : Pessimistic lock")
//    public void pessimisticTicketingTest() throws Exception {
//        ticketingTest((_no) -> ticketService.pessimisticTicketing(TICKET_ID, 1L));
//    }
//
//    @Test
//    @DisplayName("동시에 100명의 티켓팅 : Optimistic lock")
//    public void optimisticTicketingTest() throws Exception {
//        ticketingTest((_no) -> ticketService.optimisticTicketing(TICKET_ID, 1L));
//    }

    @Test
    @DisplayName("동시에 100명의 티켓팅 : 분산락")
    public void redissonTicketingTest() throws Exception {
        ticketingTest((_no) -> ticketService.redissonTicketing(TICKET_ID, 1L));
    }

}