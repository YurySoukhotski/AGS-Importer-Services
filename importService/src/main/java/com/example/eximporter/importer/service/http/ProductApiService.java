package com.example.eximporter.importer.service.http;

import com.example.eximporter.importer.model.api.Product;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;

/**
 * Service to work with product api
 */
@Service
public class ProductApiService extends BaseApiService
{
	private static final Logger log = LogManager.getLogger(ProductApiService.class);
	private static final String ERROR_API_MSG = "Server api error";
	
	/**
	 * Create new Product
	 * @param newProduct
	 *            product to create
	 * @return id of created product
	 * @throws RestException
	 */
	public Long createProduct(Product newProduct) throws RestException
	{
		ResponseEntity responseEntity;
		Long newProductID;
		try
		{
			responseEntity = doPost(Product.class, getEndPointURL(), newProduct);
			String[] splitURI = responseEntity.getHeaders().getLocation().toString().split(getEndPointURL(DELIMITER));
			if (splitURI.length == 2)
			{
				newProductID = Long.valueOf(splitURI[1]);
			}
			else
			{
				log.debug("Sent URL: " + Arrays.toString(splitURI));
				throw new RestException("ID of created object is wrong", HttpStatus.NO_CONTENT);
			}
		}
		catch (RestClientException e)
		{
			throw new RestException(ERROR_API_MSG, e);
		}
		return newProductID;
	}

	/**
	 * Update product
	 * @param newProduct
	 *            for update
	 * @throws RestException
	 */
	public void updateProduct(Product newProduct) throws RestException
	{
		try
		{
			doPatch(Product.class, getEndPointURL(DELIMITER, newProduct.getId().toString()), newProduct);
		}
		catch (RestClientException e)
		{
			throw new RestException(ERROR_API_MSG, e);
		}
	}

	/**
	 * Delete Product by id
	 * @param id
	 *            of the product to be removed
	 * @throws RestException
	 */
	public void deleteProduct(Long id) throws RestException
	{
		try
		{
			doDelete(Product.class, getEndPointURL(DELIMITER, id.toString()));
		}
		catch (RestClientException e)
		{
			throw new RestException(ERROR_API_MSG, e);
		}
	}

	/**
	 * Find product by id
	 * @param id
	 *            of product
	 * @return product
	 * @throws RestException
	 */
	public Product getProductById(Long id) throws RestException
	{
		try
		{
			ResponseEntity responseEntity = doGet(Product.class, getEndPointURL(DELIMITER, id.toString()));
			return (Product) responseEntity.getBody();
		}
		catch (RestClientException e)
		{
			throw new RestException(ERROR_API_MSG, e);
		}
	}

	/**
	 * Search any {@link Product} by product type and FPID attribute with it value
	 * @param productType
	 *            configured type name of product
	 * @param attributeFpID
	 *            name of FPID attribute
	 * @param fpIDValue
	 *            value of FPID attribute
	 * @return found products
	 * @throws RestException
	 */
	public Long[] searchProductsByFpID(String productType, String attributeFpID, String fpIDValue) throws RestException
	{
		String searchURL = getEndPointURL(QUERY_TEMPLATE, productType, AND, attributeFpID, EQ, fpIDValue);
		try
		{
			ResponseEntity responseEntity = doSearch(searchURL, Long[].class);
			return (Long[]) responseEntity.getBody();
		}
		catch (RestClientException e)
		{
			throw new RestException(ERROR_API_MSG, e);
		}
	}

	@Override
	protected String getUrl()
	{
		return apiConfiguration.getProductApiUrl();
	}
}
