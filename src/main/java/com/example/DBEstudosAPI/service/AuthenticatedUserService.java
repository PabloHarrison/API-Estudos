package com.example.DBEstudosAPI.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthenticatedUserService {

    public UUID getCurrentUserId(){
        return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
