package com.squadb.workassistantapi.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
public class TestController {

    @GetMapping
    public String isOk() throws UnknownHostException {
        return String.format("Ok (ip address: %s)", InetAddress.getLocalHost());
    }
}
