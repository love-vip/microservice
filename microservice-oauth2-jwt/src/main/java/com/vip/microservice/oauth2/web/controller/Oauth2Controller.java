package com.vip.microservice.oauth2.web.controller;

import com.vip.microservice.oauth2.service.RbacUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("oauth2")
public class Oauth2Controller {
    private final RbacUserService userService;

}
