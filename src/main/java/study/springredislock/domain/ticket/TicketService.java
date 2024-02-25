package study.springredislock.domain.ticket;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.springredislock.common.RedissonLock;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public void ticketing(Long ticketId, Long quantity) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();
        ticket.decrease(quantity);
        ticketRepository.saveAndFlush(ticket);
    }

    public void pessimisticTicketing(Long ticketId, Long quantity) {
        Ticket ticket = ticketRepository.findByIdWithPLock(ticketId);
        ticket.decrease(quantity);
        ticketRepository.saveAndFlush(ticket);
    }

    public void optimisticTicketing(Long ticketId, Long quantity) {
        try {
            Ticket ticket = ticketRepository.findByIdWithOLock(ticketId);
            ticket.decrease(quantity);
            ticketRepository.saveAndFlush(ticket);
        } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            log.info("Version 충돌. 롤백 또는 재시도");
        }
    }

//    @RedissonLock(value = "'ticketing-' + #ticketId")
    @RedissonLock(value = "#ticketId")
    public void redissonTicketing(Long ticketId, Long quantity) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();
        ticket.decrease(quantity);
        ticketRepository.saveAndFlush(ticket);
    }

}
