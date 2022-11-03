package com.example.eximporter.importer.dao;

import com.example.eximporter.importer.model.xml.project.AdmediumPage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public interface ParkedPageRepository extends CrudRepository<AdmediumPage, Long>
{
}
