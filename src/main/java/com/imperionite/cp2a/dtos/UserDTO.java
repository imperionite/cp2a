package com.imperionite.cp2a.dtos;

import lombok.*;

// UserDto (Data Transfer Object for User)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
}
