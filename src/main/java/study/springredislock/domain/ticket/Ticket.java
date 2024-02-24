package study.springredislock.domain.ticket;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private Long quantity;

    public static Ticket create(Long quantity) {
        Ticket entity = new Ticket();
        entity.setQuantity(quantity);
        return entity;
    }

    public void decrease(Long quantity) {
        long q = this.quantity - quantity;
        this.quantity = q < 0 ? 0L : q;
    }

}
