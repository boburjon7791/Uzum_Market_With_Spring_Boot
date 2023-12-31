package com.example.demo;

import com.example.demo.entity.AuthUser;
import com.example.demo.entity.Role;
import com.example.demo.entity.Status;
import com.example.demo.enums.Gender;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.*;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@EnableScheduling
@EnableAsync
@EnableCaching
@SpringBootApplication
@RequiredArgsConstructor
public class DemoApplication {
	private final UserDataRepository userDataRepository;
	private final CacheManager cacheManager;
	private final ActivateCodesRepository activateCodesRepository;
	private final RoleRepository roleRepository;

	@PostConstruct
	public void init(){
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tashkent"));   // It will set UTC timezone
		System.out.println("Spring boot application running in UTC timezone :"+new Date());   // It will print UTC timezone
	}
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	public static void stop(){
		System.out.println("Application Failed");
	}
//	@Bean
	public CommandLineRunner runner1(){
		return args -> {
			try {
			Role customer = Role.builder()
					.name("CUSTOMER")
					.build();
			roleRepository.save(customer);

			Role seller = Role.builder()
					.name("SELLER")
					.build();
			roleRepository.save(seller);

			Role admin = Role.builder()
					.name("ADMIN")
					.build();
			roleRepository.save(admin);

			Role superAdmin = Role.builder()
					.name("SUPER_ADMIN")
					.build();
			roleRepository.save(superAdmin);
			}catch (Exception e){
				System.out.println(e.getMessage());
				System.out.println(e.getLocalizedMessage());
			}
		};
	}
//	@Bean
	public CommandLineRunner runner(StatusRepository statusRepository,
									AuthUserRepository authUserRepository){
		return args -> {
                try {
					HashSet<Role> roles = new HashSet<>(roleRepository.findAll());
					roles.forEach(System.out::println);
					AuthUser adminUser = AuthUser.builder()
							.email("admin123@mail.com")
							.active(true)
							.birthdate(LocalDate.of(
									2000, 5, 6
							))
							.gender(Gender.MALE)
							.firstName("Admin")
							.lastName("Root")
							.phone("+998999067760")
							.build();
					Set<Long> collect = roles.stream()
							.map(Role::getId)
							.collect(Collectors.toSet());
					collect.forEach(System.out::println);
					try {
						adminUser = authUserRepository.save(adminUser);
						adminUser.setRoles(roles);
						for (Role role : new ArrayList<>(roles)) {
							role.setAuthUsers(Set.of(adminUser));
						    roleRepository.save(role);
						}
					}catch (RuntimeException e){
						adminUser = authUserRepository.findByEmail(adminUser.getEmail())
								.orElseThrow(NotFoundException::new);
						adminUser.setRoles(roles);
						for (Role role : new ArrayList<>(roles)) {
							role.setAuthUsers(Set.of(adminUser));
							roleRepository.save(role);
						}
					}
				}catch (RuntimeException e){
					System.out.println(e.getMessage());
					System.out.println(e.getLocalizedMessage());
				}
				try {
					Status status = Status.builder().name("ORDER IS PREPARING").build();
					statusRepository.save(status);
				}catch (Exception ignore){}
		};
	}
	@Scheduled(cron = "0 0 * * * 1-6")
	public void deleteUserData(){
		try {
			userDataRepository.deleteExpireData();
			log.info("deleted old user infos");
		}catch (RuntimeException e){
			log.error("Error while deleting caches",e);
			throw e;
		}
	}
	@Scheduled(cron = "0 0,5,10,15,20,25,30,35,40,45,50,55 * * * *")
	public void deleteCaches(){
		try {
			cacheManager.getCacheNames()
					.forEach(s -> Objects.requireNonNull(cacheManager.
							getCache(Objects.requireNonNullElse(s, ""))).clear());
			log.info("deleted all caches");
		}catch (RuntimeException e){
			log.error("Error while deleting caches",e);
			throw e;
		}
	}
	@Scheduled(cron = "0 0 23 * * *")
	public void dailyCycle(){
		try {
			activateCodesRepository.deleteOldCodes();
			log.info("deleted old activation codes");
		}catch (RuntimeException e){
			String message = "Error processing the while deleting old activation codes method";
			log.error(message, e);
			throw e;
		}
	}
	@Bean
	public SecurityScheme createAPIKeyScheme() {
		return new SecurityScheme().type(SecurityScheme.Type.HTTP)
				.bearerFormat("JWT")
				.scheme("bearer");
	}

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI().addSecurityItem(new SecurityRequirement()
						.addList("Bearer Authentication"))
				.components(new Components().addSecuritySchemes(
						"Bearer Authentication", createAPIKeyScheme()))
				.info(new Info().title("Uzum Market - REST API")
						.description("Some custom description of API.")
						.version("1.0").contact(new Contact().name("Soliyev Boburjon")
								.email("http://localhost:8080").url("soliyevboburjon95@@gmail.com"))
						.license(new License().name("License of API")
								.url("API license URL")));
	}


	@Bean
	public GroupedOpenApi admin() {
		return GroupedOpenApi.builder()
				.group("admin")
				.pathsToMatch("/**")
				.build();
	}
	@Bean
	public GroupedOpenApi seller() {
		return GroupedOpenApi.builder()
				.group("seller")
				.pathsToMatch("/api.auth/**",
						"/api.product/**",
						"/api.delivery/get/**",
						"/api.comment/**",
						"/api.color/**",
						"/api.basket/**",
						"/api.basket/**",
						"/api.favourites/**",
						"/api.basket/**",
						"/api.order/**",
						"/api.payment.type/get/**",
						"/api.promo-code/**",
						"/api.multimedia/**",
						"/api.role/get/**",
						"/api.status/get/**",
						"/api.type/get/**")
				.build();
	}
	@Bean
	public GroupedOpenApi customer() {
		return GroupedOpenApi.builder()
				.group("customer")
				.pathsToMatch("/api.auth/**",
						"/api.product/get/**",
						"/api.delivery/get/**",
						"/api.comment/**",
						"/api.color/get/**",
						"/api.basket/**",
						"/api.favourites/**",
						"/api.order/save/**",
						"/api.order/get/**",
						"/api.payment.type/get/**",
						"/api.promo-code/get/**",
						"/api.role/get/**",
						"/api.multimedia/**",
						"/api.status/get/**",
						"/api.type/get/**")
				.build();
	}
	@Bean
	public ResourceLoader resourceLoader(){
		return new FileSystemResourceLoader();
	}
}

