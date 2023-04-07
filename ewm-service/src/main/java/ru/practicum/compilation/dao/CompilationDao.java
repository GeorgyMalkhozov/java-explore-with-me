package ru.practicum.compilation.dao;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NoObjectsFoundException;
import ru.practicum.exceptions.ValidationException;

@Component
public class CompilationDao {

    private final CompilationRepository compilationRepository;

    public CompilationDao(CompilationRepository compilationRepository) {
        this.compilationRepository = compilationRepository;
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

    public Compilation getCompilationById(Long id) {
        return compilationRepository.findById(id).orElseThrow(() ->
                new NoObjectsFoundException("Компиляция с id = " + id + " не существует"));
    }
}
