package com.singtanglab.ktbworld.service.ticket;

import com.singtanglab.ktbworld.dto.ticket.TicketDetailResponse;
import com.singtanglab.ktbworld.dto.ticket.TicketRequest;
import com.singtanglab.ktbworld.dto.ticket.TicketResponse;
import com.singtanglab.ktbworld.dto.ticket.TicketListResponse;

import java.util.List;

public interface TicketService {
    TicketResponse createTicket(TicketRequest request);
    TicketListResponse getTickets(String category, String filter);
    TicketListResponse getUserTickets(String category, String filter, Long user_id);
    TicketDetailResponse getTicketById(Long id);
    TicketResponse.Success closeTicket(Long id);
    TicketResponse.Success joinTicket(Long ticket_id, Long user_id);
}
