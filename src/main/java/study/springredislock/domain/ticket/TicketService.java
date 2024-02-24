package study.springredislock.domain.ticket;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public void ticketing(Long ticketId, Long quantity, Boolean lock) {
        Ticket ticket = lock
                ? ticketRepository.findByIdWithPLock(ticketId)
                : ticketRepository.findById(ticketId).orElseThrow();
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

}
