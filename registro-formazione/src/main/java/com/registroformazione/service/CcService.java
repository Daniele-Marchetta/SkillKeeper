package com.registroformazione.service;

import com.registroformazione.dto.CcDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Cc;
import com.registroformazione.repository.CcRepository;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**Servizio per la gestione delle chiamate del controller di centrom competenze.
 *
 */
@Service
@Log4j2
public class CcService {

  @Autowired
private CcRepository repo;

  @Autowired
private ModelMapper modelMapper;

  private static final String RESOURCE_NOT_FOUND_MESSAGE = "cc non trovato";

  /**
* ritorna la lista di tutte i centri di competenza se é presente almeno uno .
* Solleva un'eccezione nel caso in cui non venga trovato nessun cc nel db

*@return lista di centri di competenza
*/
  public List<CcDto> findAll() {
    List<Cc> c = repo.findAll();
    if (c.isEmpty()) {
      throw new NoDataFoundException();
    } else {
      return c.stream().map(this::convertEntityToDto).toList();
    }
  }

	/**
	 * ritorna il singolo centro di competenza trovato prendendo come argomento l'id
	 * del cc di riferimento. Solleva un'eccezione nel caso in cui la risorsa non
	 * venga trovata
	 * 
	 * 
	 * @param ccId id del centro di competenza di riferimento
	 * @return singolo centro di competenza
	 */
	public CcDto findById(Integer ccId) {
		return convertEntityToDto(
				repo.findById(ccId).orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE)));
	}

	/**
	 * ritorna il centro di competenza inserito sul database. Solleva un'eccezione
	 * nel caso in ci siano conflitti durante l'inserimento nel db
	 * 
	 * 
	 * 
	 * @param c centro di competenza
	 * @return centro di competenza inserito nel db
	 */
	public CcDto create(CcDto c) {
		try {
			repo.save(convertDtoToEntity(c));
			return c;
		} catch (Exception e) {
			throw new DatabaseException("inserimento fallito conflitti : " + e.getMessage());
		}
	}

	/**
	 * ritorna il centro di competenza aggiornato sul db. Effettua la modifica del
	 * centro di competenza specificato. Solleva un'eccezione nel caso in cui ci
	 * siano conflitti durante l'operazione di update oppure nel caso il record
	 * corrispondente del cc indicato per la modifica non esiste.
	 * 
	 * 
	 * @param id    id del cc da modificare
	 * @param ccDto instanza del cc modificato
	 * @return cc modificato
	 */
	public CcDto update(Integer id, CcDto ccDto) {
		Cc c = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
		try {
			Cc cc = convertDtoToEntity(ccDto);
			cc.setId(c.getId());
			repo.save(cc);
			return ccDto;
		} catch (Exception e) {
			throw new DatabaseException("inserimento fallito conflitti : " + e.getMessage());
		}
	}

	/**
	 * elimina il centro di competenza in base all'id fornito. Nel caso in cui il cc
	 * da eliminare non venga trovato, oppure sono presenti conflitti sul db, lancia
	 * un'eccezione
	 * 
	 * 
	 * @param id l'id del record da elliminare sul db
	 */
	public void delete(Integer id) {
		Cc c = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
		try {
			repo.deleteById(id);
		} catch (Exception e) {
			throw new DatabaseException("delete fallita conflitti");
		}
		log.debug("Cc elliminato correttamente: " + c.toString());
	}

	/**
	 * ritorna il dto dell'instanza centro di competenza. Converte la classe Cc in
	 * ccDto
	 * 
	 *
	 * @param c Cc
	 * @return CcDto
	 */
	public CcDto convertEntityToDto(Cc c) {

		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE).setAmbiguityIgnored(true);
		CcDto ccDto;
		ccDto = modelMapper.map(c, CcDto.class);
		return ccDto;
	}

	/**
	 * ritorna l'entità corrispondente alla classe dto. Converte CcDto in cc
	 * 
	 * @param ccDto ccDto da convertire
	 * @return cc
	 */
	public Cc convertDtoToEntity(CcDto ccDto) {

		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE).setAmbiguityIgnored(true);
		Cc cc;
		cc = modelMapper.map(ccDto, Cc.class);
		return cc;

	}
}
