package com.NTG.mirathy.service;

import com.NTG.mirathy.DTOs.AuthResponse;
import com.NTG.mirathy.DTOs.LoginRequest;
import com.NTG.mirathy.DTOs.SignupRequest;

public interface Auth {
    AuthResponse login (LoginRequest loginRequest);
    SignupRequest signup (SignupRequest signupRequest);

}
