package com.hcmute.utezbe.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyController {
    @GetMapping("/dummy")
    public String dummy() {
        return "Dummy Page  ";
    }
}
