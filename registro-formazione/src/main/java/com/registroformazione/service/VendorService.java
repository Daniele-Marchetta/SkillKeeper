package com.registroformazione.service;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.registroformazione.dto.VendorDto;
import com.registroformazione.exceptions.DatabaseException;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.exceptions.ResourceNotFoundException;
import com.registroformazione.model.Vendor;
import com.registroformazione.repository.VendorRepository;
import com.registroformazione.utils.Util;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class VendorService {

	@Autowired
	private VendorRepository repo;
	@Autowired
	private ModelMapper modelMapper;
	private String errMess = "Vendor non trovato";

	/**
	 * ritorna la lista di tutti i vendors
	 * se é presente almeno uno . Solleva un'eccezione
	 * nel caso in cui non venga trovato nessun vendor nel db
	 * 
	 * 
	 * @return lista di vendors
	 */
	public List<VendorDto> findAll() {
		List<Vendor> v = repo.findAll();
		if (v.isEmpty()) {
			throw new NoDataFoundException();
		} else {
			return v.stream().map(this::convertEntityToDto).toList();
		}
	}
	/**
	 * ritorna il singolo vendor trovato prendendo
	 * come argomento l'id del vendor di riferimento.
	 * Solleva un'eccezione nel caso in cui la risorsa non venga trovata
	 * 
	 * 
	 * @param vendorId id del vendor di riferimento
	 * @return singolo vendor
	 */
	public VendorDto findById(Integer vendorId) {
      return convertEntityToDto(repo.findById(vendorId).orElseThrow(()-> new ResourceNotFoundException(errMess)));	
	}
	
	/**
	 * ritorna il vendor inserito sul database.
	 * Solleva un'eccezione nel caso in ci siano conflitti durante 
	 * l'inserimento nel db
	 * 
	 * 
	 * @param vendorDto vendor da inserire
	 * @return vendorDto inserito nel db 
	 */
	public VendorDto create(VendorDto vendorDto) {
		try {
			vendorDto.setNome(Util.formatString(vendorDto.getNome()));
			repo.save(convertDtoToEntity(vendorDto));
			return  vendorDto;
		} catch (Exception e) {
			throw new DatabaseException("inserimento fallito conflitti : " + e.getMessage());
		}
	}

	/**
	 * ritorna il vendor aggiornato sul db.
	 * Effettua la modifica del vendor specificato.
	 * Solleva un'eccezione nel caso in cui ci siano conflitti
	 * durante l'operazione di update oppure nel caso il record corrispondente
	 * del cc indicato per la modifica non esiste.
	 * 
	 * 
	 * @param id  id del vendor da modificare
	 * @param vendorDto instanza del vendor modificato 
	 * @return vendor modificato
	 */
	public VendorDto update(Integer id, VendorDto vendorDto) {
		Vendor v = repo.findById(id).orElseThrow(()-> new ResourceNotFoundException(errMess));
			try {
				Vendor ven = convertDtoToEntity(vendorDto);
				ven.setId(v.getId());
				ven.setNome(Util.formatString(ven.getNome()));
				repo.save(ven);
				return vendorDto;
			} catch (Exception e) {
				throw new DatabaseException("inserimento fallito conflitti : " + e.getMessage());
			}
	}
	/**
	 * elimina il vendor in base all'id fornito.
	 * Nel caso in cui il vendor da eliminare non venga trovato,
	 * oppure sono presenti conflitti sul db, lancia un'eccezione
	 * 
	 * 
	 * @param id l'id del record da eliminare sul db
	 */
	public void delete(Integer id) {
		Vendor v = repo.findById(id).orElseThrow(()-> new ResourceNotFoundException(errMess));
			try {
				repo.deleteById(id);
			} catch (Exception e) {
				throw new DatabaseException("delete fallita conflitti");
			}
			log.debug("Vendor eliminato correttamente : {}",v.toString());
	}
	/**
	 * ritorna il dto dell'instanza vendor.
	 * Converte la classe Vendor in VendorDto
	 * 
	 *
	 * @param v Vendor
	 * @return  VendorDto
	 */
	public VendorDto convertEntityToDto(Vendor v) {

		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE).setAmbiguityIgnored(true);
		VendorDto vendorDto;
		vendorDto = modelMapper.map(v, VendorDto.class);
		return vendorDto;
	}
	
	/**
	 * ritorna l'entità corrispondente alla classe dto.
	 * Converte VendorDto in vendor
	 * 
	 * @param VendorDto vendorDto da convertire 
	 * @return  vendor
	 */
	public Vendor convertDtoToEntity(VendorDto v) {

		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE).setAmbiguityIgnored(true);
		Vendor ven;
		ven = modelMapper.map(v, Vendor.class);
		return ven;

	}

}
