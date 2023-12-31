package com.example.demo.controller;

import com.example.demo.dto.promo_code_dto.PromoCodeCreateDto;
import com.example.demo.dto.promo_code_dto.PromoCodeGetDto;
import com.example.demo.dto.promo_code_dto.PromoCodeUpdateDto;
import com.example.demo.service.PromoCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api.promo-code")
@PreAuthorize("hasAnyAuthority('SELLER','ADMIN','SUPER_ADMIN')")
public class PromoCodeController {
    private final PromoCodeService promoCodeService;
    @PostMapping("/save")
    public ResponseEntity<PromoCodeGetDto> save(@RequestBody @Valid PromoCodeCreateDto dto){
        return new ResponseEntity<>(promoCodeService.save(dto), HttpStatus.CREATED);
    }
    @PutMapping("/update")
    @CachePut(key = "dto.id",value = "promoCodes")
    public ResponseEntity<PromoCodeGetDto> update(@RequestBody @Valid PromoCodeUpdateDto dto){
        return new ResponseEntity<>(promoCodeService.update(dto),HttpStatus.NO_CONTENT);
    }
    @GetMapping("/get")
    @Cacheable(key = "#id",value = "promoCodes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PromoCodeGetDto> get(@RequestParam String id){
            return ResponseEntity.ok(promoCodeService.get(UUID.fromString(id)));
    }
    @CacheEvict(key = "#id",value = "promoCodes")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam String id){
            promoCodeService.delete(UUID.fromString(id));
            return ResponseEntity.noContent().build();
    }
    @GetMapping("/get-all-by-good")
    public ResponseEntity<Page<PromoCodeGetDto>> getByGood(@RequestParam Map<String, String> param){
            UUID goodId = UUID.fromString(param.get("id"));
            int page = Integer.parseInt(param.get("page"));
            int size = Integer.parseInt(param.get("size"));
            Page<PromoCodeGetDto> promoCodeGetDtoList = promoCodeService.promoCodes(goodId, PageRequest.of(page, size));
            return ResponseEntity.ok(promoCodeGetDtoList);
    }
    @GetMapping("/get-by-name/{name}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PromoCodeGetDto> getByName(@PathVariable String name){
            PromoCodeGetDto byName = promoCodeService.getByName(name);
            return ResponseEntity.ok(byName);
    }
}
