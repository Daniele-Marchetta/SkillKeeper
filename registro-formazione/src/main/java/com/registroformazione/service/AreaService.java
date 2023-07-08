package com.registroformazione.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.registroformazione.dto.AreaDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Area;
import com.registroformazione.repository.AreaRepository;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Service
@Log4j2
public class AreaService {
	@Autowired
	private AreaRepository repo;
	@Autowired
	private ModelMapper modelMapper;
	
	/**
	 * ritorna la lista di tutte le aree di lavoro
	 * se é presente almeno una . Solleva un'eccezione
	 * nel caso in cui non venga trovata nessuna area nel db
	 * 
	 * 
	 * @return lista di aree di lavoro
	 */
	public List<AreaDto> findAll() {
		List<Area> a = repo.findAll();
		if (a.isEmpty()) {
			throw new NoDataFoundException();
		}else {
			 return a.stream()
					 .map(this::convertEntityToDto)
					 .toList();
		}
	}
	
	/**
	 * ritorna la singola area di lavoro trovata prendendo
	 * come argomento l'id dell'area di riferimento.
	 * Solleva un'eccezione nel caso in cui la risorsa non venga trovata
	 * 
	 * 
	 * @param areaId  id dell'area di lavoro di riferimento
	 * @return singola area di lavoro
	 */
	public AreaDto findById(Integer areaId) {
		return convertEntityToDto(repo.findById(areaId).orElseThrow(()->new ResourceNotFoundException("area non trovata")));
	}
	
	/**
	 * ritorna l'area di lavoro creata inserita sul database.
	 * Solleva un'eccezione nel caso in ci siano conflitti durante 
	 * l'inserimento nel db
	 * 
	 * 
	 * @param a  area di lavoro
	 * @return area di lavoro inserita nel db 
	 */
	
	public AreaDto create(AreaDto a) {
		Area area = convertDtoToEntity(a);
		try{
			repo.save(area);
			return a;
		}catch (Exception e) {
			throw new DatabaseException("inserimento fallito conflitti : "+e.getMessage());
		}
	}
	
	/**
	 * ritorna l'area di lavoro aggiornata sul db.
	 * Effettua la modifica dell'area di lavoro specificata.
	 * Solleva un'eccezione nel caso in cui ci siano conflitti
	 * durante l'operazione di update oppure nel caso il record corrispondente
	 * all'area di lavoro indicata per la modifica non esiste.
	 * 
	 * 
	 * @param id  id dell'area di lavoro da modificare
	 * @param areaDto instanza area di lavoro modificato 
	 * @return  area di lavoro modificata
	 */
	
	public AreaDto update(Integer id,AreaDto areaDto) {
		Area a = repo.findById(id).orElseThrow(()->new ResourceNotFoundException("area non trovata"));
		Area area = convertDtoToEntity(areaDto);
		area.setId(a.getId());
			try {
				repo.save(area);
			}catch(Exception e) {
				throw new DatabaseException("inserimento fallito conflitti : "+e.getMessage());
			}
			log.debug("Area aggiornata correttamente: {}",area.toString());
			return areaDto;
		
	}
	
	/**
	 * elimina l'area di lavoro in base all'id fornito.
	 * Nel caso in cui l'area da eliminare non venga trovata,
	 * oppure sono presenti conflitti sul db, lancia un'eccezione
	 * 
	 * 
	 * @param id l'id del record da elliminare sul db
	 */
	
	public void delete(Integer id) {
		Area a = repo.findById(id).orElseThrow(()->new ResourceNotFoundException("Area non trovata"));
			try {
				repo.deleteById(id);
				}catch (Exception e) {
				throw new DatabaseException("delete fallita conflitti");
			}
			log.debug("Area cancellata correttamente : {}",a.toString());
	}
	
	/**
	 * ritorna il dto dell'instanza di area.
	 * Converte la classe area in areaDto
	 * 
	 *
	 * @param a
	 * @return  l'areaDto convertita
	 */
	
	public AreaDto convertEntityToDto(Area a) {

		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE).setAmbiguityIgnored(true);
		AreaDto areaDto;
		areaDto = modelMapper.map(a, AreaDto.class);
		return areaDto;
	}

	/**
	 * ritorna l'entità corrispondente alla classe dto.
	 * Converte areaDto in area
	 * 
	 * @param areaDto
	 * @return
	 */
	
	public Area convertDtoToEntity(AreaDto areaDto) {

		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE).setAmbiguityIgnored(true);
		Area area;
		area = modelMapper.map(areaDto, Area.class);
		return area;

	}
}
