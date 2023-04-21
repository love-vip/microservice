package com.vip.microservice.oauth2.web.controller;

import com.vip.microservice.commons.base.wrapper.WrapMapper;
import com.vip.microservice.commons.base.wrapper.Wrapper;
import com.vip.microservice.oauth2.service.RbacUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("oauth2")
public class Oauth2Controller {
    private final RbacUserService userService;
    @PostMapping("update/password")
    public Wrapper<?> updatePassword(Principal principal, String password) {
        userService.updatePassword(principal.getName(), password);
        return WrapMapper.ok();
    }

}
