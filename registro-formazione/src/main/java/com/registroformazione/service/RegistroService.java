package com.registroformazione.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.registroformazione.dto.RegistroDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.FilterException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.filters.builders.RegistroPredicatesBuilder;
import com.registroformazione.model.Registro;
import com.registroformazione.repository.RegistroRepository;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class RegistroService {

    @Autowired
    private RegistroRepository repo;
    @Autowired
    private ModelMapper modelMapper;

    private static final String RESOURCE_NOT_FOUND_MESSAGE = "registro non trovato";

    /**
     * Effettua la ricerca di tutti le righe di registro nel database filtrando e
     * pagoinando in base ai parametri ricevuti
     * 
     * @param search   stringa con dati per filtrare
     * @param offset   numero di pagina da visualizzare
     * @param pageSize numero di risultati poer pagina
     * @return lista di righe di registro che rispettano i filtri
     */
    public Page<RegistroDto> findAll(String search, Integer offset, Integer pageSize) {
        RegistroPredicatesBuilder builder = new RegistroPredicatesBuilder();
        Pattern pattern = Pattern.compile("(\\w+?)([:<>])(\\w+?),");
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
        }
        BooleanExpression exp = builder.build();
        if (exp == null) {
            throw new FilterException("L'operazione del criterio ricevuto non è previsto: ");
        }

        Page<Registro> r = repo.findAll(exp, PageRequest.of(offset - 1, pageSize).withSort(Sort.by("id").descending()));
        if (!r.isEmpty()) {
            return r.map(this::convertEntityToDto);
        }
        throw new NoDataFoundException();
    }

    /**
     * Effettua la ricerca di una singola riga di registro
     * 
     * @param registroId id della riga da cercare
     * @return la linea di registro con tale id
     */
    public RegistroDto findById(Integer registroId) {
        return convertEntityToDto(
                repo.findById(registroId).orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE)));
    }

    public RegistroDto create(RegistroDto r) {
        try {
            repo.save(convertDtoToEntity(r));
            return r;
        } catch (Exception e) {
            throw new DatabaseException("inserimento fallito conflitti : " + e.getMessage());
        }
    }

    /**
     * Effettua l'update di una singola riga di registro
     * 
     * @param id          id da modificare
     * @param registroDto dati da modificare
     * @return i dati modificati
     */
    public RegistroDto update(Integer id, RegistroDto registroDto) {
        Registro r = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
        try {
            Registro res = convertDtoToEntity(registroDto);
            res.setId(r.getId());
            repo.save(res);
            return registroDto;
        } catch (Exception e) {
            throw new DatabaseException("inserimento fallito conflitti : " + e.getMessage());
        }
    }

    /**
     * Effettua la delete di una singola riga di registro
     * 
     * @param id id da cancellare
     */
    public void delete(Integer id) {
        Registro r = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
        try {
            repo.deleteById(id);
        } catch (Exception e) {
            throw new DatabaseException("delete fallita conflitti");
        }
        log.debug("Registro eliminato correttamente : {}", r.toString());

    }

    /**
     * Converte da entità registro a corrispettivo dto
     * 
     * @param r entità registro
     * @return dto registro
     */
    public RegistroDto convertEntityToDto(Registro r) {

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE).setAmbiguityIgnored(true);
        RegistroDto registroDto;
        registroDto = modelMapper.map(r, RegistroDto.class);
        return registroDto;
    }

    /**
     * Converte da dto registro alla corrispettiva entità
     * 
     * @param r dto registro
     * @return entità registro
     */
    public Registro convertDtoToEntity(RegistroDto r) {

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE).setAmbiguityIgnored(true);
        Registro reg;
        reg = modelMapper.map(r, Registro.class);
        return reg;

    }

}
