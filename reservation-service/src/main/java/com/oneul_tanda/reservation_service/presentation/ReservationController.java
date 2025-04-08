package com.oneul_tanda.reservation_service.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    @GetMapping("/healthcheck")
    public String healthCheck(){
        return "reservation healthcheck";
    }

}
