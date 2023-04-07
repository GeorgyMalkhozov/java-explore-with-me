package ru.practicum.location.mapper;


import org.mapstruct.Mapper;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    Location locationDtoToLocation(LocationDto dto);

    LocationDto locationToLocationDto(Location location);
}
