package com.example.eximporter.importer.service.js.notification;

import com.example.eximporter.importer.service.js.JSService;
import org.springframework.stereotype.Service;

@Service
public class NotificationService extends JSService {
	@Override
    protected String getScriptName() {
		return scriptConfiguration.getNotificationScriptName();
	}
}
