package ru.practicum.compilation.mapper;


import org.mapstruct.*;
import org.springframework.validation.annotation.Validated;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.user.mapper.UserMapper;

@Mapper(uses = {CategoryMapper.class, UserMapper.class, LocationMapper.class, EventMapper.class},
        componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "events", ignore = true)
    Compilation newCompilationDtoToCompilation(NewCompilationDto dto);

    CompilationDto CompilationToCompilationDto(Compilation compilation);

    @Mapping(target = "events", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCompilationFromUpdateCompilationDto(UpdateCompilationDto updateCompilationDto,
                                                   @MappingTarget @Validated Compilation compilation);
}
