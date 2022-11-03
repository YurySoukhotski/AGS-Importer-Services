package com.example.eximporter.importer.service.js.checkin;

import com.example.eximporter.importer.service.js.JSService;
import org.springframework.stereotype.Service;

/**
 * Checkin files
 */
@Service
public class CheckinService extends JSService
{
	@Override
	protected String getScriptName()
	{
		return scriptConfiguration.getCheckinScriptName();
	}
}
