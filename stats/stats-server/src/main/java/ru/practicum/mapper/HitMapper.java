package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.HitDTO;
import ru.practicum.model.Hit;

@Mapper(componentModel = "spring")
public interface HitMapper {

    Hit hitDTOToHit(HitDTO dto);

    HitDTO hitToHitDTO(Hit hit);
}
