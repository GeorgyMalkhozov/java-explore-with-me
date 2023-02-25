package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.model.Hit;
import ru.practicum.dto.HitDTO;

@Component
public class HitMapper {

    public Hit hitDTOToHit(HitDTO dto) {
        return Hit.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(dto.getTimestamp())
                .build();
    }

   public HitDTO hitToHitDTO(Hit hit) {
       return HitDTO.builder()
               .id(hit.getId())
               .app(hit.getApp())
               .uri(hit.getUri())
               .ip(hit.getIp())
               .timestamp(hit.getTimestamp())
               .build();
   }
}
