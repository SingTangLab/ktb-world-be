package com.singtanglab.ktbworld.repository.ticket;

import com.singtanglab.ktbworld.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT t FROM Ticket t WHERE t.category = :category ORDER BY t.createdAt DESC")
    List<Ticket> findAllTicketsByCategory(@Param("category") String category);

    @Query("SELECT t.machineId FROM Ticket t WHERE t.category = '세탁' AND t.startTime = :startTime AND t.endTime = :endTime")
    List<Integer> findBookedMachineIds(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT t FROM Ticket t WHERE (t.category = '세탁' AND t.endTime > :now) ORDER BY t.createdAt DESC")
    List<Ticket> findActiveLaundryTickets(@Param("now") LocalDateTime now);

    @Query("SELECT t FROM Ticket t WHERE (t.category = '세탁' and t.endTime > :now or t.category <> '세탁') ORDER BY t.createdAt DESC")
    List<Ticket> findAllTickets(@Param("now") LocalDateTime now);
}
