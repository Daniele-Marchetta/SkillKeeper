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
import com.registroformazione.dto.PersonaDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.FilterException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.filters.builders.PersonaPredicatesBuilder;
import com.registroformazione.model.Persona;
import com.registroformazione.repository.PersonaRepository;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class PersonaService {

    @Autowired
    private PersonaRepository repo;
    @Autowired
    private ModelMapper modelMapper;
    private String errorMessage = "Persona non trovata";

    /**
     * Cerca tutte le persone nel database, è possibile filtrarle in base ad uno
     * qualsiasi degli attributi e paginare i risultati
     * 
     * @param search   stringa contenente le informazioni necessarie per il filtro
     *                 da utilizzare (nomeAttributo operatore e valore) si possono
     *                 concatenare le query con una virgola. operatori : (><:)
     *                 esempio:(nome:marco,cognome:rossi)
     * @param offset   numero di pagina da vedere
     * @param pageSize quanti risultati per pagina
     * @return lista di persone che rispettano il filtro
     */
    public Page<PersonaDto> findAll(String search, Integer offset, Integer pageSize) {
        PersonaPredicatesBuilder builder = new PersonaPredicatesBuilder();
        Pattern pattern = Pattern.compile("(\\w+?)([:<>])(\\w+?),");
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
        }
        BooleanExpression exp = builder.build();
        if (exp == null) {
            throw new FilterException("L'operazione del criterio ricevuto non è previsto: ");
        }

        Page<Persona> p = repo.findAll(exp, PageRequest.of(offset - 1, pageSize).withSort(Sort.by("id").descending()));
        if (!p.isEmpty()) {
            return p.map(this::convertEntityToDto);
        }
        throw new NoDataFoundException();
    }

    /**
     * Effettua la ricerca di una singola persona in base al suo id
     * 
     * @param personaId id della persona
     * @return ritorna la singola persona
     */
    public PersonaDto findById(Integer personaId) {
        return convertEntityToDto(
                repo.findById(personaId).orElseThrow(() -> new ResourceNotFoundException(errorMessage)));
    }

    /**
     * Inserisce nel database i dati relativi ad una persona
     * 
     * @param p i dati della persona
     * @return ritorna il dto della persona inserita nel database
     */
    public PersonaDto create(PersonaDto p) {
        try {
            repo.save(convertDtoToEntity(p));
            return p;
        } catch (Exception e) {
            throw new DatabaseException("inserimento fallito conflitti : " + e.getMessage());
        }
    }

    /**
     * Fa l'update dei dati di una persona nel database a partire dal suo id
     * 
     * @param id         l'id della persona da modificare
     * @param personaDto i dati da modificare
     * @return il dto della persona modificata
     */
    public PersonaDto update(Integer id, PersonaDto personaDto) {
        Persona p = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException(errorMessage));
        try {
            Persona pers = convertDtoToEntity(personaDto);
            pers.setId(p.getId());
            repo.save(pers);
            return personaDto;
        } catch (Exception e) {
            throw new DatabaseException("inserimento fallito conflitti : " + e.getMessage());
        }
    }

    /**
     * Effettua la delete di una persona in base al suo id
     * 
     * @param id l'id da cancellare
     */
    public void delete(Integer id) {
        Persona p = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException(errorMessage));
        try {
            repo.deleteById(id);
        } catch (Exception e) {
            throw new DatabaseException("delete fallita conflitti" + e.getMessage());
        }
        log.debug("Persona cancellata correttamente : {}", p.toString());
    }

    /**
     * Converte l'entità persona nel suo corrispettivo dto
     * 
     * @param p entità persona
     * @return dto della persona
     */
    public PersonaDto convertEntityToDto(Persona p) {

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE).setAmbiguityIgnored(true);
        PersonaDto personaDto;
        personaDto = modelMapper.map(p, PersonaDto.class);
        return personaDto;
    }

    /**
     * Effettua la conversion da dto a entity di una persona
     * 
     * @param p il dto della persona
     * @return l'entità persona
     */
    public Persona convertDtoToEntity(PersonaDto p) {

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE).setAmbiguityIgnored(true);
        Persona pers;
        pers = modelMapper.map(p, Persona.class);
        return pers;

    }
}
