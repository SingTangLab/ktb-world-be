package com.singtanglab.ktbworld.dto.ticket;

import java.time.LocalDateTime;
import java.util.List;

public record TicketDetailResponse(String message, TicketData data) {
    public record TicketData(
            String category,
            Integer machine_id,
            String status,
            String laundry_status,
            String title,
            String description,
            List<String> participant_user,
            String account,
            LocalDateTime created_at
    ) {}
}
