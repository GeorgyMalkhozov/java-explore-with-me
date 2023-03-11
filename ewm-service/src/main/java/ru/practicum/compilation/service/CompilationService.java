package ru.practicum.compilation.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dao.CompilationDao;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.exceptions.ConflictException;

import java.util.ArrayList;

@Service
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final CompilationDao compilationDao;

    public CompilationService(CompilationRepository compilationRepository, CompilationMapper compilationMapper,
                              CompilationDao compilationDao) {
        this.compilationRepository = compilationRepository;
        this.compilationMapper = compilationMapper;
        this.compilationDao = compilationDao;
    }

    public CompilationDto addCompilation(NewCompilationDto dto) {
        Compilation compilation = compilationMapper.newCompilationDtoToCompilation(dto);
        compilationDao.enrichCompilationWithEventEntities(compilation, new ArrayList<>(dto.getEvents()));
        compilationDao.saveCompilation(compilation);
        return compilationMapper.CompilationToCompilationDto(compilation);
    }
}
