package com.example.eximporter.importer.service.js;

import com.example.isy3suite.plugin.functions.WorkflowJavascriptWebservice;
import com.example.eximporter.importer.configuration.ScriptConfiguration;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public abstract class JSService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(JSService.class);
	
	private static final String PARAMETER_DELIMITER = ";";
	private static final String EQUAL = "=";
	@Autowired
	protected ScriptConfiguration scriptConfiguration;
	
	public void call(Map<JSParameter, String> parameterToValueMap)
	{
		try
		{
			URL url = new URL(scriptConfiguration.getWorkflowJsWebserviceUrl());
			QName qname = new QName("http://functions.plugin.isy3suite.example.com/", "WorkflowJavascriptWebserviceService");
			Service service = Service.create(url, qname);
			WorkflowJavascriptWebservice workflowJavascriptWebservice = service.getPort(WorkflowJavascriptWebservice.class);
			String parametersString = createParametersString(parameterToValueMap);
			workflowJavascriptWebservice.execute(getScriptName(), parametersString);
		}
		catch (Exception e)
		{
			LOGGER.error("Problems of notification", e);
		}
	}

	private String createParametersString(Map<JSParameter, String> parameterToValueMap)
	{
		List<String> parameters =new ArrayList<>();
		parameters.add("MandatorId=" + scriptConfiguration.getMandatorId());
		if (MapUtils.isNotEmpty(parameterToValueMap))
		{
			List<String> collect = parameterToValueMap.entrySet().stream().map(entry -> entry.getKey() + EQUAL + entry.getValue()).collect(Collectors.toList());
			parameters.addAll(collect);
		}
		return parameters.stream().collect(Collectors.joining(PARAMETER_DELIMITER));
	}
	
	protected abstract String getScriptName();
}
