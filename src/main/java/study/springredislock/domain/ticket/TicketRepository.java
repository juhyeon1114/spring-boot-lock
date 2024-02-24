package study.springredislock.domain.ticket;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Ticket t where t.id = :id")
    Ticket findByIdWithPLock(Long id);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select t from Ticket t where t.id = :id")
    Ticket findByIdWithOLock(Long id);

}
