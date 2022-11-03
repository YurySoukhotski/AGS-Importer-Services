package com.example.eximporter.importer.service.parking;

import com.example.eximporter.importer.dao.ParkedPeoRepository;
import com.example.eximporter.importer.model.extended.ParkedPeo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to work with Parked Peo
 */
@Service
public class ParkedPeoService
{
	@Autowired
	private ParkedPeoRepository parkedPeoRepository;

	@Autowired
	private ParkedPeoProcessor parkedPeoProcessor;
	private Logger logger = LoggerFactory.getLogger(ParkedPeoProcessor.class);

	/**
	 * Save or Update parked peo
	 * @param parkedPeo parked peo from db
	 *
	 */
	public void saveParkedPeo(ParkedPeo parkedPeo)
	{
		parkedPeoRepository.save(parkedPeo);
	}

	/**
	 * Check and Process all parked peos
	 */
	public void checkParkedPeos(StepExecution stepExecution)
	{
		List<ParkedPeo> parkedPeos = (List<ParkedPeo>) getParkedPeos();
		if (!parkedPeos.isEmpty())
		{
			parkedPeoProcessor.processParkedPeos(parkedPeos, stepExecution);
		}
		else
		{
			logger.info("Parked peos not found ");
		}
	}

	/**
	 * Delete parked peo
	 * @param parkedPeo peo to delete
	 */
	void deleteParkedPeo(ParkedPeo parkedPeo)
	{
		parkedPeoRepository.delete(parkedPeo);
	}

	/**
	 * Return all parked peos
	 * @return list parked peo
	 */
	Iterable<ParkedPeo> getParkedPeos()
	{
		return parkedPeoRepository.findAll();
	}
}
