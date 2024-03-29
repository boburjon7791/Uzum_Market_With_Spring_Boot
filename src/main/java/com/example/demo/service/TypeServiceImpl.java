package com.example.demo.service;

import com.example.demo.dto.type_dto.TypeCreateDto;
import com.example.demo.dto.type_dto.TypeGetDto;
import com.example.demo.dto.type_dto.TypeUpdateDto;
import com.example.demo.entity.Type;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.TypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static com.example.demo.mapper.TypeMapper.TYPE_MAPPER;

@Slf4j
@Service
@RequiredArgsConstructor
public class TypeServiceImpl implements TypeService {
    private final TypeRepository typeRepository;

    @Override
    public TypeGetDto save(TypeCreateDto dto) {
            Type type = TYPE_MAPPER.toEntity(dto,typeRepository);
            Type saved = typeRepository.save(type);
            TypeGetDto dto1 = TYPE_MAPPER.toDto(saved);
            log.info("{} created",dto1);
            return dto1;
    }

    @Override
    public TypeGetDto update(TypeUpdateDto dto) {
            Type type = TYPE_MAPPER.toEntity(dto,typeRepository);
            typeRepository.updateType(type.getName(), type.getId(), type.getSub(), type.getRoot());
            Type found = typeRepository.findById(type.getId()).orElseThrow(NotFoundException::new);
            TypeGetDto dto1 = TYPE_MAPPER.toDto(found);
            log.info("{} updated", dto1);
            return dto1;
    }


    @Override
    public TypeGetDto get(Long id) {
            Type type = typeRepository.findById(id).orElseThrow(NotFoundException::new);
            TypeGetDto dto = TYPE_MAPPER.toDto(type);
            log.info("{} gave",dto);
            return dto;
    }

    @Override
    public List<TypeGetDto> getAllByIds(Collection<Long> ids) {
            return typeRepository.findAllById(ids)
                    .stream()
                    .map(TYPE_MAPPER::toDto)
                    .toList();
    }

    @Override
    public Page<TypeGetDto> types(Pageable pageable) {
            Page<Type> all = typeRepository.findAll(pageable);
            return TYPE_MAPPER.toDto(all);
    }

    @Override
    public List<TypeGetDto> allSubTypes() {
            List<Type> types = typeRepository.allSubTypes();
            return types.stream()
                    .map(TYPE_MAPPER::toDto)
                    .toList();
    }
}
