package com.example.eximporter.importer.service.http;

import com.example.eximporter.importer.configuration.ApiConfiguration;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Basic HTTP operations with REST api server
 */
@Service
public abstract class BaseApiService {
    @Autowired
    protected ApiConfiguration apiConfiguration;
    public static final String DELIMITER = "/";
    public static final String EQ = " = ";
    public static final String QUERY_TEMPLATE = "?query=TYPE=";
    public static final String AND = " AND ";

    @Autowired
    private AuthService authService;

    /**
     * Do PATH request some object by url with id of object
     *
     * @param classObj class of received Object
     * @param url      url to make request
     * @param objBody  body of request
     * @return response from server
     */
    ResponseEntity doPatch(Class classObj, String url, Object objBody) {
        return doRequest(HttpMethod.PATCH, classObj, url, objBody);
    }

    /**
     * Do DELETE request object by url with id of object
     *
     * @param classObj class of received Object
     * @param url      url to make request
     * @return response from server
     */
    ResponseEntity doDelete(Class classObj, String url) {
        return doRequest(HttpMethod.DELETE, classObj, url, null);
    }

    /**
     * Do search request object by url with parameters
     *
     * @param classObj class of received Object
     * @param query    url with parameters
     * @return response from server
     */
    ResponseEntity doSearch(String query, Class classObj) {
        return doRequest(HttpMethod.GET, classObj, query, null);
    }

    /**
     * Do GET request
     *
     * @param classObj class of received Object
     * @param url      url to make request
     * @return response from server
     */
    ResponseEntity doGet(Class classObj, String url) {
        return doRequest(HttpMethod.GET, classObj, url, null);
    }


    /**
     * Do POST request
     *
     * @param classObj class of received Object
     * @param url      url to make request
     * @param objBody  body of request
     * @return response from server
     */
    ResponseEntity doPost(Class classObj, String url, Object objBody) {
        return doRequest(HttpMethod.POST, classObj, url, objBody);
    }

    /**
     * Build url for request
     *
     * @param urlVars string parameters to union in request
     * @return string value of request
     */
    String getEndPointURL(String... urlVars) {
        StringBuilder builder = new StringBuilder();
        builder.append(getUrl());
        for (String arg : urlVars) {
            builder.append(arg);
        }
        return builder.toString();
    }

    protected abstract String getUrl();

    /**
     * Do abstract request
     *
     * @param httpMethod HTTP method of request
     * @param classObj   class of received Object
     * @param url        url to make request
     * @param objBody    body of request
     * @return response from server
     */
    private ResponseEntity doRequest(HttpMethod httpMethod, Class classObj, String url, Object objBody) {
        HttpEntity httpEntity = new HttpEntity(objBody, buildTokenHeaders(authService.getTokenString()));
       HttpClient client = HttpClients.createDefault();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
        return restTemplate.exchange(url, httpMethod, httpEntity, classObj);
    }


    public ResponseEntity doTokenRequest(String url) {
        HttpEntity httpEntity = new HttpEntity(null, buildHeaders());
        HttpClient client = HttpClients.createDefault();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
    }


    /**
     * Build http headers
     *
     * @return header
     */
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic " + apiConfiguration.getApiBasicAuth());
        return headers;
    }

    /**
     * Build http headers with token
     * @return header
     */
    private HttpHeaders buildTokenHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
        return headers;
    }
}
