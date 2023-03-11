package ru.practicum.compilation.dao;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NoObjectsFoundException;
import ru.practicum.exceptions.ValidationException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class CompilationDao {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    public CompilationDao(CompilationRepository compilationRepository, EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
    }

    public void checkCompilationExist(Long compilationId) {
        if (!compilationRepository.findById(compilationId).isPresent()) {
            throw new NoObjectsFoundException("Компиляция с id = " + compilationId + " не существует");
        }
    }

    public Compilation getCompilationById(Long id) {
        checkCompilationExist(id);
        return compilationRepository.getById(id);
    }

    public void saveCompilation(Compilation compilation) {
        try {
            compilationRepository.save(compilation);
        } catch (TransactionSystemException e) {
            throw new ValidationException("поле title не может быть пустыми");
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("title должен быть уникальным");
        }
    }

    public void enrichCompilationWithEventEntities(Compilation compilation, List<Long> eventIds) {
        compilation.setEvents(new HashSet<>(eventRepository.findAllByIdIn(eventIds)));
    }
}
