package com.example.demo.dto.good_dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class GoodCreateDto {
    @NotNull
    @Positive
    public Long color_id;

    @NotNull
    @Positive
    public Long type_id;

    @NotBlank
    public String name;

    @NotBlank
    public String description;

    @PositiveOrZero
    @NotNull
    public Double price;

    @NotNull
    public List<String> images;

    public String videoPath;

    @PositiveOrZero
    public Integer count;

    @Builder.Default
    public Double discountPrice=0d;
}
