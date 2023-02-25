package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HitDTO {

    private Integer id;
    @NotBlank(message = "поле app не должно быть пустым")
    private String app;
    @NotBlank(message = "поле uri не должно быть пустым")
    private String uri;
    @NotBlank(message = "поле ip не должно быть пустым")
    private String ip;
    @NotBlank(message = "поле timestamp не должно быть пустым")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;


}
