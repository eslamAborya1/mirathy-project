package com.NTG.mirathy.DTOs.response;

import com.NTG.mirathy.Entity.Enum.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse{

    private String accessToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private Long userId;
    private String fullName;
    private String email;
    private Role role;
    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String message;
    private Boolean success;
}
