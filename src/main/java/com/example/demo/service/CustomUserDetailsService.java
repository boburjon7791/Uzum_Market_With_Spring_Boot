package com.example.demo.service;

import com.example.demo.entity.AuthUser;
import com.example.demo.entity.Role;
import com.example.demo.exception.ForbiddenAccessException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthUserRepository authUserRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
            AuthUser authUser = authUserRepository.findByEmailAndActiveTrue(email)
                    .orElseThrow(NotFoundException::new);
            Set<String> collected = authUser.getRoles()
                    .stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());
            System.out.println("login process...");
            collected.forEach(System.out::println);
            if (collected.contains("SUPER_ADMIN")
                    || collected.contains("ADMIN")) {
                LocalTime now = LocalTime.now(ZoneId.of("Asia/Tashkent"));
                DayOfWeek nowDay = LocalDate.now().getDayOfWeek();
                Set<DayOfWeek> holidays = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
                if (9>now.getHour() || 18< now.getHour() ||
                        holidays.contains(nowDay)) {
                    throw new ForbiddenAccessException();
                }
            }
            return new CustomUserDetails(authUser);
        }
}
