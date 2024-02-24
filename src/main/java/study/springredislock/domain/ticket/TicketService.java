package study.springredislock.domain.ticket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
