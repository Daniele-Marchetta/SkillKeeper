package com.registroformazione.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.registroformazione.dto.AttivitaDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Attivita;
import com.registroformazione.repository.AttivitaRepository;

import lombok.extern.log4j.Log4j2;


@Service
@Log4j2
public class AttivitaService {
	
	@Autowired
	AttivitaRepository repo;
	@Autowired
	private ModelMapper modelMapper;
	
	
	/**
	 * ritorna la lista di tutte le attività 
	 * se é presente almeno una . Solleva un'eccezione
	 * nel caso in cui non venga trovata nessuna attività nel db
	 * 
	 * 
	 * @return lista di attività
	 */
	public List<AttivitaDto> findAll() {
		List<Attivita> a = repo.findAll();
		if (a.isEmpty()) {
			throw new NoDataFoundException();
		}else {
			return  a.stream().map(this::convertEntityToDto).toList();
			
		}
	}
	
	
	/**
	 * ritorna la singola attività trovata prendendo
	 * come argomento l'id dell'attività di riferimento.
	 * Solleva un'eccezione nel caso in cui la risorsa non venga trovata
	 * 
	 * 
	 * @param attivitaId id del'attività di riferimento
	 * @return singola attività
	 */
	public AttivitaDto findById(Integer attivitaId) {
		return convertEntityToDto(repo.findById(attivitaId).orElseThrow(()->new ResourceNotFoundException("attivita non trovato")));	
	}
	
	
	/**
	 * ritorna l'attività inserita sul database.
	 * Solleva un'eccezione nel caso in ci siano conflitti durante 
	 * l'inserimento nel db
	 * 
	 * 
	 * @param a attività da inserire
	 * @return attività inserita
	 */
	public AttivitaDto create(AttivitaDto a) {
		Attivita attivita = convertDtoToEntity(a);
		try{
			repo.save(attivita);
			return a;
		}catch (Exception e) {
			throw new DatabaseException("inserimento fallito conflitti : " +e.getMessage());
		}
	}
	
	
	/**
	 * ritorna l'attività aggiornata sul db.
	 * Effettua la modifica dell'attività specificata.
	 * Solleva un'eccezione nel caso in cui ci siano conflitti
	 * durante l'operazione di update oppure nel caso il record corrispondente
	 * all'attività indicata per la modifica non esiste.
	 * 
	 * 
	 * @param id id dell'attività da modificate
	 * @param attivitaDto attività modificata
	 * @return attività modificata
	 */
	public AttivitaDto update(Integer id,AttivitaDto attivitaDto) {
		Attivita a = repo.findById(id).orElseThrow(()->new ResourceNotFoundException("attivita non trovato"));
			try {
				Attivita attivita = convertDtoToEntity(attivitaDto);
				attivita.setId(a.getId());
				repo.save(attivita);
				return attivitaDto;
			}catch(Exception e) {
				throw new DatabaseException("inserimento fallito conflitti : "+e.getMessage());
			}
		}
	
	
	/**
	 * elimina l'attività in base all'id fornito.
	 * Nel caso in cui l'attività da eliminare non venga trovata,
	 * oppure sono presenti conflitti sul db, lancia un'eccezione
	 * 
	 * 
	 * @param id id dell'attività da rimuovere
	 */
	public void delete(Integer id) {
		Attivita a = repo.findById(id).orElseThrow(()->new ResourceNotFoundException("Attivita non trovata"));
			try {
				repo.deleteById(id);
			}catch (Exception e) {
				throw new DatabaseException("delete fallita conflitti");
			}
			log.debug("attività cancellata correttamente : {}",a.toString());
		}
	
	
	
	/**
	 * ritorna il dto dell'instanza di attività.
	 * Converte la classe attivita in attivitaDto
	 * 
	 *
	 * @param a attivita
	 * @return  attivitaDto
	 */
	public AttivitaDto convertEntityToDto(Attivita a) {

		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE).setAmbiguityIgnored(true);
		AttivitaDto attivitaDto ;
		attivitaDto = modelMapper.map(a, AttivitaDto.class);
		return attivitaDto;
	}

	
	/**
	 * ritorna l'entità corrispondente alla classe dto.
	 * Converte attivitaDto in attivita
	 * 
	 * @param attivitaDto attivitaDto da convertire 
	 * @return  attivita
	 */
	public Attivita convertDtoToEntity(AttivitaDto attivitaDto) {

		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE).setAmbiguityIgnored(true);
		Attivita attivita ;
		attivita = modelMapper.map(attivitaDto, Attivita.class);
		return attivita;

	}

}
