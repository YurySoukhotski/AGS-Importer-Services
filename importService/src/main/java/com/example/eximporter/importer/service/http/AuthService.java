package com.example.eximporter.importer.service.http;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

/**
 * Service to work with auth api
 */
@Service
public class AuthService extends BaseApiService {
    private static final Logger log = LogManager.getLogger(AuthService.class);
    private static final String ERROR_API_MSG = "Server api error";
    private String tokenString;


    public void getToken() throws RestException {
        ResponseEntity responseEntity;
        try {
            responseEntity = doTokenRequest(getEndPointURL(DELIMITER, "login"));
            if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                log.info("Get token - OK");
                this.tokenString = String.valueOf(responseEntity.getBody());
            } else {
                throw new RestException("Error getting token", HttpStatus.UNAUTHORIZED);
            }
        } catch (RestClientException e) {
            log.error("Error getting token", e);
            throw new RestException(ERROR_API_MSG, HttpStatus.UNAUTHORIZED);
        }

    }

    public Boolean validateToken() {
        ResponseEntity responseEntity;
        try {
            responseEntity = doGet(String.class, getEndPointURL(DELIMITER, "validate"));
            if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                log.info("Token is valid");
                return true;
            } else {
                log.error("Non valid token");
                return false;
            }
        } catch (RestClientException e) {
            log.error("Non valid token",e);
            return false;
        }

    }

    @Override
    protected String getUrl() {
        return apiConfiguration.getAuthPath();
    }

    public String getTokenString() {
        return tokenString;
    }
}
