package com.gustavoventieri.framework.adapter.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsible for user-related actions, such as updating password.
 */
@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
@SecurityRequirement(name = "cookieAuth")
@Slf4j
@Validated
public class UserController {


       
}
