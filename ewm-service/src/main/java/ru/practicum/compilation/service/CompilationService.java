package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {

    CompilationDto addCompilation(NewCompilationDto dto);

    void deleteCompilation(Long id);

    CompilationDto updateCompilation(Long compilationId, UpdateCompilationDto dto);

    CompilationDto getCompilation(Long compilationId);

    List<CompilationDto> getCompilationsByFilter(Boolean pinned, Integer from, Integer size);
}
