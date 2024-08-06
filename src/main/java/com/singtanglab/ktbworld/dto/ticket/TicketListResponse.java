package com.singtanglab.ktbworld.dto.ticket;

import java.time.LocalDateTime;
import java.util.List;

public record TicketListResponse(String message, int result_count, List<TicketData> tickets) {
    public record TicketData(
            Long ticket_id,
            String category,
            String title,
            String description,
            List<Integer> participant_users,
            int capacity,
            String status,
            LocalDateTime created_at,
            LocalDateTime start_time,
            LocalDateTime end_time,
            int machine_id,
            String laundry_color,
            boolean is_dry,
            String destination
    ) {}
}
