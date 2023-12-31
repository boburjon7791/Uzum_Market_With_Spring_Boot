package com.example.demo.controller;

import com.example.demo.dto.type_dto.TypeCreateDto;
import com.example.demo.dto.type_dto.TypeGetDto;
import com.example.demo.dto.type_dto.TypeUpdateDto;
import com.example.demo.service.TypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api.type")
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN','ADMIN')")
public class TypeController {
    private final TypeService typeService;
    @PostMapping("/save")
    public ResponseEntity<TypeGetDto> save(@RequestBody @Valid TypeCreateDto dto){
        TypeGetDto saved = typeService.save(dto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
    @PutMapping("/update")
    @CachePut(key = "#dto.id",value = "types")
    public ResponseEntity<TypeGetDto> update(@RequestBody @Valid TypeUpdateDto dto){
        TypeGetDto updated = typeService.update(dto);
        return new ResponseEntity<>(updated,HttpStatus.NO_CONTENT);
    }
    @GetMapping("/get-type/{id}")
    @PreAuthorize("isAuthenticated()")
    @Cacheable(key = "#id",value = "types")
    public ResponseEntity<TypeGetDto> getOnlyOneType(@PathVariable Long id){
        TypeGetDto getDto = typeService.get(id);
        return ResponseEntity.ok(getDto);
    }
    @GetMapping("/get-many-types")
    @Cacheable(key = "#ids",value = "types")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TypeGetDto>> getManyTypes(@RequestBody Collection<Long> ids){
        List<TypeGetDto> allByIds = typeService.getAllByIds(ids);
        return ResponseEntity.ok(allByIds);
    }

    @GetMapping("/get-sub-types")
    @Cacheable(key = "root.methodName",value = "types")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TypeGetDto>> getSubTypes(){
        return ResponseEntity.ok(typeService.allSubTypes());
    }
    @GetMapping("/get-all-types")
    public ResponseEntity<Page<TypeGetDto>> getAllTypes(@RequestParam String page,
                                                        @RequestParam String size){
            Page<TypeGetDto> types = typeService.types
                    (PageRequest.of(Integer.parseInt(page), Integer.parseInt(size)));
            return ResponseEntity.ok(types);
    }

}
