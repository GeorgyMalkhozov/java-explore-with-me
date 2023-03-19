package ru.practicum.compilation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dao.CompilationDao;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final CompilationDao compilationDao;

    @Autowired
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
        return compilationMapper.compilationToCompilationDto(compilation);
    }

    public void deleteCompilation(Long id) {
        compilationDao.checkCompilationExist(id);
        compilationRepository.deleteById(id);
    }

    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationDto dto) {
        compilationDao.checkCompilationExist(compilationId);
        Compilation compilation = compilationDao.getCompilationById(compilationId);
        compilationMapper.updateCompilationFromUpdateCompilationDto(dto, compilation);
        compilationDao.enrichCompilationWithEventEntities(compilation, new ArrayList<>(dto.getEvents()));
        compilationDao.saveCompilation(compilation);
        return compilationMapper.compilationToCompilationDto(compilation);
    }

    public CompilationDto getCompilation(Long compilationId) {
        compilationDao.checkCompilationExist(compilationId);
        return compilationMapper.compilationToCompilationDto(compilationDao.getCompilationById(compilationId));
    }

    public List<CompilationDto> getCompilationsByFilter(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.findAll(PageRequest.of(from / size, size)).toList();
        } else {
            compilations = compilationRepository.findAllByPinnedOrderByIdDesc(pinned,
                    PageRequest.of(from / size, size)).toList();
        }

        return compilations.stream()
                .map(compilationMapper::compilationToCompilationDto)
                .collect(Collectors.toList());
    }
}
