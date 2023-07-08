package com.registroformazione.service;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.registroformazione.dto.CompetenzaDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Competenza;
import com.registroformazione.repository.CompetenzaRepository;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class CompetenzaService {
	
	@Autowired
	private CompetenzaRepository repo;
	@Autowired
	private ModelMapper modelMapper;
	
	private static final String RESOURCE_NOT_FOUND_MESSAGE="Competenza non trovata";
	
	
	/**
	 * ritorna la lista di tutte le competenze
	 * se é presente almeno una . Solleva un'eccezione
	 * nel caso in cui non venga trovata nessuna competenza nel db
	 * 
	 * 
	 * @return lista di competenze
	 */
	public List<CompetenzaDto> findAll() {
		List<Competenza> c = repo.findAll();
		if (c.isEmpty()) {
			throw new NoDataFoundException();
		}else {
				return	c.stream().map(this::convertEntityToDto).toList();
		}
	}
	
	/**
	 * ritorna la singola competenza trovata prendendo
	 * come argomento l'id della competenza di riferimento.
	 * Solleva un'eccezione nel caso in cui la risorsa non venga trovata
	 * 
	 * 
	 * @param competenzaId id della competenza di riferimento
	 * @return singola competenza
	 */
	public CompetenzaDto findById(Integer competenzaId) {
			return convertEntityToDto(repo.findById(competenzaId).orElseThrow(()-> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE)));	
	}
	
	/**
	 * ritorna la competenza inserita sul database.
	 * Solleva un'eccezione nel caso in ci siano conflitti durante 
	 * l'inserimento nel db
	 * 
	 * 
	 * @param c competenza da inserire
	 * @return competenza inserita
	 */
	public CompetenzaDto create(CompetenzaDto c) {
		try{
			repo.save(convertDtoToEntity(c));
			return c;
		}catch (Exception e) {
			throw new DatabaseException("inserimento fallito conflitti : "+e.getMessage());
		}
	}
	
	/**
	 * ritorna la competenza aggiornata sul db.
	 * Effettua la modifica della competenza specificata.
	 * Solleva un'eccezione nel caso in cui ci siano conflitti
	 * durante l'operazione di update oppure nel caso il record corrispondente
	 * alla competenza indicata per la modifica non esiste.
	 * 
	 * 
	 * @param id id della competenza da modificare
	 * @param competenzaDto competenza modificata
	 * @return competenza modificata
	 */
	public CompetenzaDto update(Integer id,CompetenzaDto competenzaDto) {
		Competenza c = repo.findById(id).orElseThrow(()-> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
			try {
				Competenza competenza = convertDtoToEntity(competenzaDto);
				competenza.setId(c.getId());
				repo.save(competenza);
				return competenzaDto;
			}catch(Exception e) {
				throw new DatabaseException("inserimento fallito conflitti : "+e.getMessage());
			}
	}
	
	/**
	 * elimina la competenza in base all'id fornito.
	 * Nel caso in cui la competenza da eliminare non venga trovata,
	 * oppure sono presenti conflitti sul db, lancia un'eccezione
	 * 
	 * 
	 * @param id id della competenza da rimuovere
	 */
	public void delete(Integer id) {
		Competenza c = repo.findById(id).orElseThrow(()-> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
			try {
				repo.deleteById(id);

			}catch (Exception e) {
				throw new DatabaseException("delete fallita conflitti");
			}	
			log.debug("competenza eliminata correttamente {}",c.toString());
	}
	
	/**
	 * ritorna il dto dell'instanza competenza.
	 * Converte la classe competenza in competenzaDto
	 * 
	 *
	 * @param c competenza
	 * @return  competenzaDto
	 */
	public CompetenzaDto convertEntityToDto(Competenza c) {

		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE).setAmbiguityIgnored(true);
		CompetenzaDto competenzaDto;
		competenzaDto = modelMapper.map(c, CompetenzaDto.class);
		return competenzaDto;
	}
		
	/**
	 * ritorna l'entità corrispondente alla classe dto.
	 * Converte competenzaDto in competenza
	 * 
	 * @param cDto competenzaDto da convertire 
	 * @return  competenza
	 */
	public Competenza convertDtoToEntity(CompetenzaDto cDto) {

		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE).setAmbiguityIgnored(true);
		Competenza competenza;
		competenza = modelMapper.map(cDto, Competenza.class);
		return competenza;

	}

}
