package com.singtanglab.ktbworld.service.ticket;

import com.singtanglab.ktbworld.dto.ticket.TicketDetailResponse;
import com.singtanglab.ktbworld.dto.ticket.TicketListResponse;
import com.singtanglab.ktbworld.dto.ticket.TicketRequest;
import com.singtanglab.ktbworld.dto.ticket.TicketResponse;
import com.singtanglab.ktbworld.dto.ticket.TicketListResponse.TicketData;
import com.singtanglab.ktbworld.entity.Ticket;
import com.singtanglab.ktbworld.entity.User;
import com.singtanglab.ktbworld.entity.UserTicket;
import com.singtanglab.ktbworld.repository.ticket.TicketRepository;
import com.singtanglab.ktbworld.repository.user.UserRepository;
import com.singtanglab.ktbworld.repository.userTicket.UserTicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    private final UserTicketRepository userTicketRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TicketResponse createTicket(TicketRequest request) {
        try {
            User creator = userRepository.findById(request.creator())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.creator()));
            Ticket ticket;
            if (request.category().equalsIgnoreCase("세탁")) {
                int machineId = assignMachineId(request.start_time(), request.end_time());
                if (machineId == -1) {
                    throw new RuntimeException("All washing machines are booked at that time");
                }
                ticket = new Ticket(
                        creator,
                        request.category(),
                        "모집중",
                        request.title(),
                        request.description(),
                        request.is_limited(),
                        request.capacity(),
                        request.laundry_color(),
                        request.is_dry(),
                        machineId,
                        request.account(),
                        request.start_time(),
                        request.end_time()
                        );
            } else if (request.category().equalsIgnoreCase("택시")) {
                ticket = new Ticket(
                        creator,
                        request.category(),
                        "모집중",
                        request.title(),
                        request.description(),
                        request.is_limited(),
                        request.capacity(),
                        request.destination(),
                        request.account(),
                        request.start_time()
                );
            } else {
                ticket = new Ticket(
                        creator,
                        request.category(),
                        "모집중",
                        request.title(),
                        request.description(),
                        request.is_limited(),
                        request.capacity(),
                        request.account()
                );
            }

            Ticket savedTicket = ticketRepository.save(ticket);

            for (Integer userId : request.participant_users()) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
                UserTicket userTicket = UserTicket.builder()
                        .user(user)
                        .ticket(savedTicket)
                        .build();
                userTicketRepository.save(userTicket);
            }

            return new TicketResponse.Success("TICKET_CREATED_SUCCESS",new TicketResponse.Success.TicketData(savedTicket.getId()));
        } catch (Exception e) {
            log.error("Exception during ticket creation: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private int assignMachineId(LocalDateTime startTime, LocalDateTime endTime) {
        List<Integer> bookedMachineIds = ticketRepository.findBookedMachineIds(startTime, endTime);
        log.info("Booked machine IDs for the given time slot ({} - {}): {}", startTime, endTime, bookedMachineIds);
        if (!bookedMachineIds.contains(1)) {
            return 1;
        } else if (!bookedMachineIds.contains(2)) {
            return 2;
        } else {
            return -1;  // 모든 세탁기가 예약된 경우
        }
    }

    @Override
    @Transactional
    public TicketListResponse getTickets(String category, String filter) {
        List<Ticket> tickets;
        LocalDateTime now = LocalDateTime.now();

        if (category.equalsIgnoreCase("전체")) {
            tickets = ticketRepository.findAllTickets(now);
        } else if (category.equalsIgnoreCase("세탁")) {
            tickets = ticketRepository.findActiveLaundryTickets(now);
            tickets = tickets.stream()
                    .filter(ticket -> ticket.getEndTime().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());
        } else {
            tickets = ticketRepository.findAllTicketsByCategory(category);
        }

        if (filter.equalsIgnoreCase("모집중")) {
            tickets = tickets.stream()
                    .filter(ticket -> ticket.getStatus().equalsIgnoreCase("모집중"))
                    .collect(Collectors.toList());
        } else if (filter.equalsIgnoreCase("마감")) {
            tickets = tickets.stream()
                    .filter(ticket -> ticket.getStatus().equalsIgnoreCase("마감"))
                    .collect(Collectors.toList());
        }


        List<TicketData> ticketDataList = tickets.stream()
                .map(ticket -> new TicketData(
                        ticket.getUser().getId(),
                        ticket.getUser().getNickname(),
                        ticket.getId(),
                        ticket.getCategory(),
                        ticket.getTitle(),
                        ticket.getDescription(),
                        ticket.getUserTickets().stream().map(ut -> ut.getUser().getId().intValue()).collect(Collectors.toList()),
                        ticket.getCapacity(),
                        ticket.getStatus(),
                        (ticket.getStartTime()!=null&&!ticket.getStartTime().equals("")) ? (ticket.getStartTime().isBefore(LocalDateTime.now())  ? "빨래중" : "대기중") : "",
                        ticket.getCreatedAt(),
                        ticket.getStartTime(),
                        ticket.getEndTime(),
                        ticket.getMachineId(),
                        ticket.getLaundryColor(),
                        ticket.isDry(),
                        ticket.getDestination()
                ))
                .collect(Collectors.toList());

        return new TicketListResponse("TICKET_LIST_LOADED_SUCCESS", ticketDataList.size(), ticketDataList);
    }

    @Override
    @Transactional
    public TicketListResponse getUserTickets(String category, String filter, Long user_id) {
        List<Ticket> tickets;
        LocalDateTime now = LocalDateTime.now();

        if (category.equalsIgnoreCase("전체")) {
            tickets = ticketRepository.findAllTickets(now);
        } else if (category.equalsIgnoreCase("세탁")) {
            tickets = ticketRepository.findActiveLaundryTickets(now);
            tickets = tickets.stream()
                    .filter(ticket -> ticket.getEndTime().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());
        } else {
            tickets = ticketRepository.findAllTicketsByCategory(category);
        }

        if (filter.equalsIgnoreCase("모집중")) {
            tickets = tickets.stream()
                    .filter(ticket -> ticket.getStatus().equalsIgnoreCase("모집중"))
                    .collect(Collectors.toList());
        } else if (filter.equalsIgnoreCase("마감")) {
            tickets = tickets.stream()
                    .filter(ticket -> ticket.getStatus().equalsIgnoreCase("마감"))
                    .collect(Collectors.toList());
        }

        List<TicketData> ticketDataList = tickets.stream()
                .map(ticket -> new TicketData(
                        ticket.getUser().getId(),
                        ticket.getUser().getNickname(),
                        ticket.getId(),
                        ticket.getCategory(),
                        ticket.getTitle(),
                        ticket.getDescription(),
                        ticket.getUserTickets().stream().map(ut -> ut.getUser().getId().intValue()).collect(Collectors.toList()),
                        ticket.getCapacity(),
                        ticket.getStatus(),
                        (ticket.getStartTime()!=null&&!ticket.getStartTime().equals("")) ? (ticket.getStartTime().isBefore(LocalDateTime.now())  ? "빨래중" : "대기중") : "",
                        ticket.getCreatedAt(),
                        ticket.getStartTime(),
                        ticket.getEndTime(),
                        ticket.getMachineId(),
                        ticket.getLaundryColor(),
                        ticket.isDry(),
                        ticket.getDestination()
                ))
                .collect(Collectors.toList());
        ticketDataList=ticketDataList.stream()
                    .filter(ticket -> ticket.participant_users().contains(user_id.intValue()))
                    .collect(Collectors.toList());
        return new TicketListResponse("TICKET_LIST_LOADED_SUCCESS", ticketDataList.size(), ticketDataList);
    }


    @Override
    @Transactional
    public TicketDetailResponse getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));

        List<String> participantUsers = ticket.getUserTickets().stream()
                .map(ut -> ut.getUser().getNickname())
                .collect(Collectors.toList());

        LocalDateTime createdAt = ticket.getUserTickets().stream()
                .map(UserTicket::getCreatedAt)
                .findFirst()
                .orElse(ticket.getCreatedAt());

        TicketDetailResponse.TicketData data = new TicketDetailResponse.TicketData(
                ticket.getCategory(),
                ticket.getCategory().equalsIgnoreCase("세탁") ? ticket.getMachineId() : null,
                ticket.getStatus(),
                (ticket.getStartTime()!=null&&!ticket.getStartTime().equals("")) ? (ticket.getStartTime().isBefore(LocalDateTime.now())  ? "빨래중" : "대기중") : "",
                ticket.getTitle(),
                ticket.getDescription(),
                participantUsers,
                ticket.getAccount(),
                createdAt
        );

        return new TicketDetailResponse("TICKET_DETAIL_LOADED_SUCCESS", data);
    }

    @Override
    @Transactional
    public TicketResponse.Success closeTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));

        ticket.setStatus("마감");
        Ticket savedTicket = ticketRepository.save(ticket);

        return new TicketResponse.Success("TICKET_CLOSED_SUCCESS", new TicketResponse.Success.TicketData(savedTicket.getId()));
    }

    @Override
    @Transactional
    public TicketResponse.Success joinTicket(Long ticketId, Long userId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + ticketId));

        long participantCount = ticket.getUserTickets().size();
        if (participantCount >= ticket.getCapacity()) {
            throw new RuntimeException("The ticket is already full.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        UserTicket userTicket = UserTicket.builder()
                .user(user)
                .ticket(ticket)
                .build();

        userTicketRepository.save(userTicket);

        return new TicketResponse.Success("TICKET_JOINED_SUCCESS", new TicketResponse.Success.TicketData(ticket.getId()));
    }
}

