package com.singtanglab.ktbworld.entity;

import com.singtanglab.ktbworld.BaseTimeEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import lombok.*;

@Entity
@Getter
@Table(name = "tickets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Ticket extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String category;

    private String status;
    private String title;
    private String description;

    @Column(name = "is_limited")
    private boolean isLimited;

    private int capacity;

    @Column(name = "laundry_color")
    private String laundryColor;

    @Column(name = "is_dry")
    private boolean isDry;

    private String destination;

    @Column(name = "machine_id")
    private int machineId;

    private String account;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<UserTicket> userTickets = new LinkedList<>();

    // 공통 생성자
    public Ticket(User user,String category, String status, String title, String description, boolean isLimited,
                  int capacity, String account) {
        this.user = user;
        this.category = category;
        this.status = status;
        this.title = title;
        this.description = description;
        this.isLimited = isLimited;
        this.capacity = capacity;
        this.account = account;
    }

    // 세탁 생성자
    public Ticket(User user,String category, String status, String title, String description, boolean isLimited,
                  int capacity, String laundryColor, boolean isDry, int machineId, String account,
                  LocalDateTime startTime, LocalDateTime endTime) {
        this.user = user;
        this.category = category;
        this.status = status;
        this.title = title;
        this.description = description;
        this.isLimited = isLimited;
        this.capacity = capacity;
        this.laundryColor = laundryColor;
        this.isDry = isDry;
        this.machineId = machineId;
        this.account = account;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // 택시 생성자
    public Ticket (User user, String category, String status, String title, String description, boolean isLimited,
                   int capacity, String destination, String account, LocalDateTime startTime) {
        this.user = user;
        this.category = category;
        this.status = status;
        this.title = title;
        this.description = description;
        this.isLimited = isLimited;
        this.capacity = capacity;
        this.destination = destination;
        this.account = account;
        this.startTime = startTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

