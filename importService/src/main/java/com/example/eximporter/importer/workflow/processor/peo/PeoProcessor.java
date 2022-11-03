package com.example.eximporter.importer.workflow.processor.peo;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.eximporter.importer.model.extended.ExtendedPeo;
import com.example.eximporter.importer.model.xml.peo.Placement;
import com.example.eximporter.importer.service.converter.peo.PeoConverter;

/**
 * Process placement to convert into Peo
 */
@Component("peoProcessor")
public class PeoProcessor implements ItemProcessor<Placement, ExtendedPeo>
{
    @Autowired
    private PeoConverter peoConverter;

    @Override
    public ExtendedPeo process(Placement placement) throws Exception
    {
        return peoConverter.convert(placement);
    }
}
