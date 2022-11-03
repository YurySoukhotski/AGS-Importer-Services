package com.example.eximporter.importer.service.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Exception while work with REST API
 */
public class RestException extends Exception
{
	private HttpStatus httpStatus;

	public RestException(String message)
	{
		super(message);
	}

	public RestException(String message, Throwable cause)
	{
		super(message, cause);
		if (cause instanceof HttpClientErrorException)
		{
			this.httpStatus = ( (HttpClientErrorException) cause ).getStatusCode();
		}
	}

	public RestException(String message, HttpStatus httpStatus)
	{
		super(message);
		this.httpStatus = httpStatus;
	}

	public HttpStatus getHttpStatus()
	{
		return httpStatus;
	}
}
