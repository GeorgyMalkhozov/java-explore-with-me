package ru.practicum.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsDTO {

    private String app;
    @NotBlank(message ="поле uri не должно быть пустым")
    private String uri;
    private Long hits;
}
