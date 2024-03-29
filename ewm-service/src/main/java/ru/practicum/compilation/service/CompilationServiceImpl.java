package ru.practicum.compilation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dao.CompilationDao;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final CompilationDao compilationDao;
    private final EventRepository eventRepository;

    @Autowired
    public CompilationServiceImpl(CompilationRepository compilationRepository, CompilationMapper compilationMapper,
                                  CompilationDao compilationDao, EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.compilationMapper = compilationMapper;
        this.compilationDao = compilationDao;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public CompilationDto addCompilation(NewCompilationDto dto) {
        Compilation compilation = compilationMapper.newCompilationDtoToCompilation(dto);
        enrichCompilationWithEventEntities(compilation, new ArrayList<>(dto.getEvents()));
        compilationDao.saveCompilation(compilation);
        return compilationMapper.compilationToCompilationDto(compilation);
    }

    public void deleteCompilation(Long id) {
        compilationDao.getCompilationById(id);
        compilationRepository.deleteById(id);
    }

    @Transactional
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationDto dto) {
        Compilation compilation = compilationDao.getCompilationById(compilationId);
        compilationMapper.updateCompilationFromUpdateCompilationDto(dto, compilation);
        enrichCompilationWithEventEntities(compilation, new ArrayList<>(dto.getEvents()));
        compilationDao.saveCompilation(compilation);
        return compilationMapper.compilationToCompilationDto(compilation);
    }

    public CompilationDto getCompilation(Long compilationId) {
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

    private void enrichCompilationWithEventEntities(Compilation compilation, List<Long> eventIds) {
        Set<Event> events = new HashSet<>(eventRepository.findAllByIdIn(eventIds));
        compilation.setEvents(events);
    }
}
