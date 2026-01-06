package com.NTG.mirathy.util;

import com.NTG.mirathy.Entity.User;
import com.NTG.mirathy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUtil {
    private  final UserService userService;

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken
                || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        String currentUserEmail =  authentication.getName();

        User currentUser =userService.getUserByEmail(currentUserEmail);
        return currentUser;
    }
}