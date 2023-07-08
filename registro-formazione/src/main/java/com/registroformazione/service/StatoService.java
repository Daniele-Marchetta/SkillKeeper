package com.registroformazione.service;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.registroformazione.dto.StatoDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Stato;
import com.registroformazione.repository.StatoRepository;
import com.registroformazione.utils.Util;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class StatoService {
	
	@Autowired
	private StatoRepository repo; 
	@Autowired
	private ModelMapper modelMapper;
	private String errMess="Stato non trovato";
	
	

	/**
	 * ritorna la lista di tutte gli stati relativi alle certificazioni (eseguito/pianificato)
	 * se é presente almeno uno . Solleva un'eccezione
	 * nel caso in cui non venga trovato nessuno stato nel db
	 * 
	 * 
	 * @return lista di stati
	 */
	public List<StatoDto> findAll() {
		List<Stato> s = repo.findAll();
		if (s.isEmpty()) {
			throw new NoDataFoundException();
		}else {
			return s.stream().map(this::convertEntityToDto).toList();
		}
	}
	
	/**
	 * ritorna il singolo stato trovato prendendo
	 * come argomento l'id dello stato di riferimento.
	 * Solleva un'eccezione nel caso in cui la risorsa non venga trovata
	 * 
	 * 
	 * @param statoId id dello stato di riferimento
	 * @return singolo stato
	 */
	public StatoDto findById(Integer statoId) {
			return convertEntityToDto(repo.findById(statoId).orElseThrow(()-> new ResourceNotFoundException(errMess)));	

	}
	
	/**
	 * ritorna lo stato inserito sul db.
	 * Solleva un'eccezione nel caso in ci siano conflitti durante 
	 * l'inserimento nel db
	 * 
	 * 
	 * @param s stato da inserire
	 * @return stato inserita
	 */
	public StatoDto create(StatoDto s) {
		s.setNome(Util.formatString(s.getNome()));
		try{
			repo.save(convertDtoToEntity(s));
			return s;
		}catch (Exception e) {
			throw new DatabaseException("inserimento fallito conflitti : "+e.getMessage());
		}
	}
	
	/**
	 * ritorna lo stato aggiornato sul db.
	 * Effettua la modifica dello stato specificato.
	 * Solleva un'eccezione nel caso in cui ci siano conflitti
	 * durante l'operazione di update oppure nel caso il record corrispondente
	 * allo stato indicato per la modifica non esiste.
	 * 
	 * 
	 * @param id id dello stato da modificare
	 * @param statoDto stato modificato
	 * @return stato modificatostato
	 */
	public StatoDto update(Integer id,StatoDto statoDto) {
		Stato s = repo.findById(id).orElseThrow(()-> new ResourceNotFoundException(errMess));
			try {
				Stato stat= convertDtoToEntity(statoDto);
				stat.setId(s.getId());
				stat.setNome(Util.formatString(stat.getNome()));
				repo.save(stat);
				return statoDto;
			}catch(Exception e) {
				throw new DatabaseException("inserimento fallito conflitti : "+e.getMessage());
			}
	}
	
	/**
	 * elimina la stato in base all'id fornito.
	 * Nel caso in cui lo stato da eliminare non venga trovato,
	 * oppure sono presenti conflitti sul db, lancia un'eccezione
	 * 
	 * 
	 * @param id id dello stato da rimuovere
	 */
	public void delete(Integer id) {
		Stato s = repo.findById(id).orElseThrow(()-> new ResourceNotFoundException(errMess));
			try {
				repo.deleteById(id);
			}catch (Exception e) {
				throw new DatabaseException("delete fallita conflitti");
			}
			log.debug("Stato eliminato correttamente : {}",s.toString());
	}
	
	/**
	 * ritorna il dto dell'instanza stato.
	 * Converte la classe stato in statoDto
	 * 
	 *
	 * @param s stato
	 * @return  statoDto
	 */
	public StatoDto convertEntityToDto(Stato s) {

		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE).setAmbiguityIgnored(true);
		StatoDto statoDto;
		statoDto = modelMapper.map(s, StatoDto.class);
		return statoDto;
	}
	
	/**
	 * ritorna l'entità corrispondente alla classe dto.
	 * Converte statoDto in stato
	 * 
	 * @param s statoDto da convertire 
	 * @return  stato
	 */
	public Stato convertDtoToEntity(StatoDto s) {

		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE).setAmbiguityIgnored(true);
		Stato sta;
		sta = modelMapper.map(s, Stato.class);
		return sta;

	}

}
