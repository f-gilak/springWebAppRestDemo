package com.fariba.restclient.service;

import com.fariba.restclient.model.Customer;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CustomerServiceRestClientImpl implements CustomerService {

    private RestTemplate restTemplate;
    private String crmRestUrl;
    private Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    public CustomerServiceRestClientImpl(
            RestTemplate theRestTemplate,
            @Value("${crm.rest.url}") String theUrl) {
        restTemplate = theRestTemplate;
        crmRestUrl = theUrl;
        logger.info("Loaded property: crm.rest.url="
                + crmRestUrl);
    }

    @Override
    public List<Customer> getCustomers() {
        logger.info("in getCustomers(): Calling REST API "
                + crmRestUrl);
// make REST call
        HttpEntity<String> request = new HttpEntity<String>(createHeaders());
        ResponseEntity<List<Customer>> responseEntity =
                restTemplate.exchange(crmRestUrl,
                        HttpMethod.GET, request,
                        new ParameterizedTypeReference<List<Customer>>() {
                        });
// get the list of customers from response
        List<Customer> customers = responseEntity.getBody();
        logger.info("in getCustomers(): customers" + customers);
        return customers;
    }

    @Override
    public void saveCustomer(Customer customer) {
        logger.info("in saveCustomer(): Calling REST API "
                + crmRestUrl);
        int employeeId = customer.getId();
        HttpEntity<Customer> request = new HttpEntity<>(customer, createHeaders());
// make REST call
        if (employeeId == 0) {
// add employee
            restTemplate.postForEntity(crmRestUrl, request,
                    String.class);
        } else {
// update employee
//            restTemplate.put(crmRestUrl, request);
            ResponseEntity<Customer> result = restTemplate.exchange(crmRestUrl , HttpMethod.PUT, request, Customer.class);

        }
        logger.info("in saveCustomer(): success");
    }

    @Override
    public Customer getCustomer(int id) {
        logger.info("in getCustomer(): Calling REST API "
                + crmRestUrl);
        HttpEntity<String> request = new HttpEntity<>(createHeaders());

//        Customer customer = restTemplate.getForObject(crmRestUrl + "/" + id, Customer.class, request);
        ResponseEntity<Customer> result = restTemplate.exchange(crmRestUrl + "/" + id, HttpMethod.GET, request, Customer.class);
        Customer customer = result.getBody();
        logger.info("in saveCustomer(): theCustomer="
                + customer);
        return customer;
    }

    @Override
    public void deleteCustomer(int id) {
        logger.info("in deleteCustomer(): Calling REST API "
                + crmRestUrl);
        HttpEntity<String> request = new HttpEntity<>(createHeaders());

// make REST call
//        restTemplate.delete(crmRestUrl + "/" + id);
        ResponseEntity<String> result = restTemplate.exchange(crmRestUrl + "/" + id, HttpMethod.DELETE, request, String.class);
        logger.info("in deleteCustomer(): deleted customer theId="
                + id + "*****" + result.getBody());
    }

    private HttpHeaders createHeaders() {
        return new HttpHeaders() {{
            String auth = getUserName() + ":" + getPassword();
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")));
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
        }};
    }

    private String getPassword() {
        return "fun123";
    }

    private String getUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}
