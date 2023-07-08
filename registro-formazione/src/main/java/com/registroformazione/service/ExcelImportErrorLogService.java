package com.registroformazione.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import com.registroformazione.dto.ExcelImportErrorLogDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.ExcelImportErrorLog;
import com.registroformazione.repository.ExcelImportErrorLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ExcelImportErrorLogService {

    private final ExcelImportErrorLogRepository repo;
    private static final String RESOURCE_NOT_FOUND_MESSAGE = "Log non trovato";
    
    
    /**
     * ritorna la lista di tutti i log
     * se é presente almeno uno . Solleva un'eccezione
     * nel caso in cui non venga trovato nessun log nel db
     * 
     * 
     * @return lista di logs
     */
    public List<ExcelImportErrorLogDto> findAll() {
        List<ExcelImportErrorLog> logs = repo.findAll();
        if (logs.isEmpty()) {
            throw new NoDataFoundException();
        }else {
                return  logs.stream().map(this::convertEntityToDto).toList();
        }
    }
    
    /**
     * ritorna la singola excelImportErrorLog trovata prendendo
     * come argomento l'id della excelImportErrorLog di riferimento.
     * Solleva un'eccezione nel caso in cui la risorsa non venga trovata
     * 
     * 
     * @param logId id della excelImportErrorLog di riferimento
     * @return singola excelImportErrorLog
     */
    public ExcelImportErrorLogDto findById(Integer logId) {
            return convertEntityToDto(repo.findById(logId).orElseThrow(()-> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE))); 
    }
    
    /**
     * ritorna la excelImportErrorLog inserita sul database.
     * Solleva un'eccezione nel caso in ci siano conflitti durante 
     * l'inserimento nel db
     * 
     * 
     * @param loggerDto excelImportErrorLog da inserire
     * @return excelImportErrorLogDto inserita
     */
    public ExcelImportErrorLog create(ExcelImportErrorLogDto loggerDto) {
        try{
            return repo.save(convertDtoToEntity(loggerDto));
        }catch (Exception e) {
            throw new DatabaseException("inserimento fallito conflitti : "+e.getMessage());
        }
    }
    
    /**
     * ritorna la excelImportErrorLog aggiornata sul db.
     * Effettua la modifica della excelImportErrorLog specificata.
     * Solleva un'eccezione nel caso in cui ci siano conflitti
     * durante l'operazione di update oppure nel caso il record corrispondente
     * alla excelImportErrorLog indicata per la modifica non esiste.
     * 
     * 
     * @param id id della excelImportErrorLog da modificare
     * @param excelImportErrorLogDto excelImportErrorLog modificata
     * @return excelImportErrorLogDto modificata
     */
    public ExcelImportErrorLog update(Integer id,ExcelImportErrorLogDto excelImportErrorLogDto) {
        ExcelImportErrorLog logger = repo.findById(id).orElseThrow(()-> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
            try {
                ExcelImportErrorLog excelImportErrorLog = convertDtoToEntity(excelImportErrorLogDto);
                excelImportErrorLog.setId(logger.getId());
                return repo.save(excelImportErrorLog);
            }catch(Exception e) {
                throw new DatabaseException("inserimento fallito conflitti : "+e.getMessage());
            }
    }
    
    /**
     * elimina la excelImportErrorLog in base all'id fornito.
     * Nel caso in cui la excelImportErrorLog da eliminare non venga trovata,
     * oppure sono presenti conflitti sul db, lancia un'eccezione
     * 
     * 
     * @param id id della excelImportErrorLog da rimuovere
     */
    public void delete(Integer id) {
        ExcelImportErrorLog logger = repo.findById(id).orElseThrow(()-> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
            try {
                repo.deleteById(id);

            }catch (Exception e) {
                throw new DatabaseException("delete fallita conflitti");
            }   
            log.debug("excelImportErrorLog eliminata correttamente {}",logger.toString());
    }
    
    /**
     * ritorna il dto dell'instanza excelImportErrorLog.
     * Converte la classe excelImportErrorLog in excelImportErrorLogDto
     * 
     *
     * @param logger excelImportErrorLog
     * @return  excelImportErrorLogDto
     */
    public ExcelImportErrorLogDto convertEntityToDto(final ExcelImportErrorLog logger) {
        ExcelImportErrorLogDto loggerDto = new ExcelImportErrorLogDto();
        loggerDto.setFile(logger.getFile());
        loggerDto.setStato(logger.getStato());
        return loggerDto;
    }
        
    /**
     * ritorna l'entità corrispondente alla classe dto.
     * Converte excelImportErrorLogDto in excelImportErrorLog
     * 
     * @param loggerDto excelImportErrorLogDto da convertire 
     * @return  excelImportErrorLog
     */
    public ExcelImportErrorLog convertDtoToEntity(final ExcelImportErrorLogDto loggerDto) {
        ExcelImportErrorLog logger = new ExcelImportErrorLog();
        logger.setStato(loggerDto.getStato());
        logger.setFile(loggerDto.getFile());
        return logger;
    }

}
