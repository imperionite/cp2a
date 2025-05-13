package com.imperionite.cp2a.dtos;

public class UserResponseDTO {
    private Long id;
    private String username;
    private Boolean isActive;
    private Boolean isAdmin;

    public UserResponseDTO(Long id, String username, Boolean isActive, Boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.isActive = isActive;
        this.isAdmin = isAdmin;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }
}