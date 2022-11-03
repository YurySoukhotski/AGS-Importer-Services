package com.example.eximporter.importer.dao;

import com.example.eximporter.importer.model.extended.ParkedPeo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public interface ParkedPeoRepository extends CrudRepository<ParkedPeo, Long>
{
}
