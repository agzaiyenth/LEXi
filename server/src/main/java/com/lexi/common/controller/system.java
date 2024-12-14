package com.lexi.common.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@Slf4j
@RequestMapping("/system")
public class system {

    @GetMapping("/status")
    public String getSystemStatus() {
        log.info("System is up and running!");
        return "System is up and running!";
    }
}