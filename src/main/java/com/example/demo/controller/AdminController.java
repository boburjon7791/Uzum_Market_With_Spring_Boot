package com.example.demo.controller;

import com.example.demo.dto.auth_user_dto.AuthUserGetDto;
import com.example.demo.exception.ForbiddenAccessException;
import com.example.demo.service.AdminService;
import com.example.demo.service.AuthUserService;
import com.example.demo.service.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api.admin")
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN','ADMIN')")
public class AdminController {
    private final AdminService adminService;
    private final AuthUserService authUserService;
    @PutMapping("/update-email")
    public void updateEmail(@RequestParam Map<String, String> param){
        String adminEmail = param.get("admin");
        String oldEmail = param.get("old");
        String newEmail = param.get("new");
        adminService.updateEmail(adminEmail,oldEmail,newEmail);
    }
    @GetMapping("/get-user-data/{id}")
    public ResponseEntity<AuthUserGetDto> get(@PathVariable String id,HttpServletRequest request){
        AuthUserGetDto getDto = authUserService.get(UUID.fromString(id));
        return ResponseEntity.ok(getDto);
    }
    @PutMapping("/add-role")
    public void addRole(@RequestParam String userId,
                           @RequestParam String role){
        if (role.equals("SUPER_ADMIN")) {
            throw new ForbiddenAccessException();
        }
            adminService.addRole(UUID.fromString(userId),role);
    }
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PutMapping("/add-role-special")
    public void addRole2(@RequestParam String userId,
                        @RequestParam String role){
        adminService.addRole(UUID.fromString(userId),role);
    }
    @PutMapping("/remove-role")
    public void removeRole(@RequestParam String userId,
                           @RequestParam String role){
        if (role.equals("SUPER_ADMIN")) {
            throw new ForbiddenAccessException();
        }
            adminService.removeRole(UUID.fromString(userId),role);
    }
    @GetMapping("/get-all-users-data")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<Page<AuthUserGetDto>> getUsers(@RequestParam String page,
                                                         @RequestParam String size){
            Page<AuthUserGetDto> users = authUserService
                    .users(PageRequest.of(Integer.parseInt(page), Integer.parseInt(size)));
            return ResponseEntity.ok(users);
    }
    @PutMapping("/update-activity")
    public void updateActivity(@RequestParam String userId,
                               @RequestParam Boolean activity){
            adminService.updateActivity(UUID.fromString(userId),activity);
    }
}
