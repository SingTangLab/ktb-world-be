package com.singtanglab.ktbworld.controller;

import com.singtanglab.ktbworld.dto.ticket.*;
import com.singtanglab.ktbworld.service.ticket.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@RequestBody TicketRequest request) {
        try {
            TicketResponse response = ticketService.createTicket(request);
            return ResponseEntity.status(201).body(response);
        } catch (RuntimeException e) {
            TicketResponse.Fail errorResponse = new TicketResponse.Fail("TICKET_CREATED_FAIL", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    @GetMapping
    public ResponseEntity<TicketListResponse> getTickets(@RequestParam String category, @RequestParam String filter) {
        try {
            TicketListResponse response = ticketService.getTickets(category, filter);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(new TicketListResponse("TICKET_LIST_LOAD_FAIL", 0, null));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<TicketListResponse> getUserTickets(@RequestParam String category, @RequestParam String filter, @RequestParam Long user_id) {
        try {
            TicketListResponse response = ticketService.getUserTickets(category, filter, user_id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(new TicketListResponse("TICKET_LIST_LOAD_FAIL", 0, null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailResponse> getTicketById(@PathVariable Long id) {
        try {
            TicketDetailResponse response = ticketService.getTicketById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(new TicketDetailResponse("TICKET_DETAIL_LOAD_FAIL", null));
        }
    }

    @PostMapping("/close/{id}")
    public ResponseEntity<TicketResponse> closeTicket(@PathVariable Long id) {
        try {
            TicketResponse.Success response = ticketService.closeTicket(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            TicketResponse.Fail errorResponse = new TicketResponse.Fail("TICKET_CLOSED_FAIL", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/join")
    public ResponseEntity<TicketResponse> joinTicket(@RequestBody JoinTicketRequest request) {
        try {
            TicketResponse.Success response = ticketService.joinTicket(request.ticket_id(), request.user_id());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            TicketResponse.Fail errorResponse = new TicketResponse.Fail("TICKET_JOIN_FAIL", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

}
