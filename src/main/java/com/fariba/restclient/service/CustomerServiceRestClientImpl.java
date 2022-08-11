package com.fariba.restclient.service;

import com.fariba.restclient.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        ResponseEntity<List<Customer>> responseEntity =
                restTemplate.exchange(crmRestUrl,
                        HttpMethod.GET, null,
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
// make REST call
        if (employeeId == 0) {
// add employee
            restTemplate.postForEntity(crmRestUrl, customer,
                    String.class);
        } else {
// update employee
            restTemplate.put(crmRestUrl, customer);
        }
        logger.info("in saveCustomer(): success");
    }

    @Override
    public Customer getCustomer(int id) {
        logger.info("in getCustomer(): Calling REST API "
                + crmRestUrl);
        Customer customer = restTemplate.getForObject(crmRestUrl + "/" + id, Customer.class);
        logger.info("in saveCustomer(): theCustomer="
                + customer);
        return customer;
    }

    @Override
    public void deleteCustomer(int id) {
        logger.info("in deleteCustomer(): Calling REST API "
                + crmRestUrl);
// make REST call
        restTemplate.delete(crmRestUrl + "/" + id);
        logger.info("in deleteCustomer(): deleted customer theId="
                + id);
    }

}
