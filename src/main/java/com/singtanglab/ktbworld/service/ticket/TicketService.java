package com.singtanglab.ktbworld.service.ticket;

import com.singtanglab.ktbworld.dto.ticket.TicketDetailResponse;
import com.singtanglab.ktbworld.dto.ticket.TicketRequest;
import com.singtanglab.ktbworld.dto.ticket.TicketResponse;
import com.singtanglab.ktbworld.dto.ticket.TicketListResponse;

import java.util.List;

public interface TicketService {
    TicketResponse createTicket(TicketRequest request);
    TicketListResponse getTickets(String category, String filter);
    TicketDetailResponse getTicketById(Long id);
    TicketResponse.Success closeTicket(Long id);
}
