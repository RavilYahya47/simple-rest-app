package com.example.simplerestapp.service;

import com.example.simplerestapp.dto.CustomerDTO;
import com.example.simplerestapp.entity.Customer;
import com.example.simplerestapp.exception.ResourceNotFoundException;
import com.example.simplerestapp.mappers.CustomerMapper;
import com.example.simplerestapp.repository.CustomerRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CustomerService {
    CustomerRepository customerRepository;

    public List<CustomerDTO> getAllCustomers() {
        log.info("Action.Log.CustomerService.getAllCustomers: Fetching all customers");
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOs = customers.stream()
                .map(CustomerMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
        log.info("Action.Log.CustomerService.getAllCustomers: Fetched {} customers", customerDTOs.size());
        return customerDTOs;
    }

    public CustomerDTO getCustomerById(Long id) {
        log.info("Action.Log.CustomerService.getCustomerById: Fetching customer with id: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        CustomerDTO customerDTO = CustomerMapper.INSTANCE.toDTO(customer);
        log.info("Action.Log.CustomerService.getCustomerById: Fetched customer: {}", customerDTO);
        return customerDTO;
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        log.info("Action.Log.CustomerService.createCustomer: Creating customer: {}", customerDTO);
        Customer customer = CustomerMapper.INSTANCE.toEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        CustomerDTO savedCustomerDTO = CustomerMapper.INSTANCE.toDTO(savedCustomer);
        log.info("Action.Log.CustomerService.createCustomer: Created customer: {}", savedCustomerDTO);
        return savedCustomerDTO;
    }

    public CustomerDTO updateCustomer(Long id, CustomerDTO updatedCustomerDTO) {
        log.info("Action.Log.CustomerService.updateCustomer: Updating customer with id: {}", id);
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        BeanUtils.copyProperties(updatedCustomerDTO, existingCustomer, "id");

        Customer savedCustomer = customerRepository.save(existingCustomer);
        CustomerDTO savedCustomerDTO = CustomerMapper.INSTANCE.toDTO(savedCustomer);
        log.info("Action.Log.CustomerService.updateCustomer: Updated customer: {}", savedCustomerDTO);
        return savedCustomerDTO;
    }

    public CustomerDTO partiallyUpdateCustomer(Long id, CustomerDTO updatedCustomerDTO) {
        log.info("Action.Log.CustomerService.partiallyUpdateCustomer:  Partially updating customer with id: {}", id);
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        if (updatedCustomerDTO.getName() != null) {
            existingCustomer.setName(updatedCustomerDTO.getName());
        }
        if (updatedCustomerDTO.getEmail() != null) {
            existingCustomer.setEmail(updatedCustomerDTO.getEmail());
        }

        Customer savedCustomer = customerRepository.save(existingCustomer);
        CustomerDTO savedCustomerDTO = CustomerMapper.INSTANCE.toDTO(savedCustomer);
        log.info("Action.Log.CustomerService.partiallyUpdateCustomer: Partially updated customer: {}", savedCustomerDTO);
        return savedCustomerDTO;
    }

    public void deleteCustomer(Long id) {
        log.info("Action.Log.CustomerService.getAllCustomers: Deleting customer with id: {}", id);
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isPresent()) {
            customerRepository.delete(optionalCustomer.get());
            log.info("Action.Log.CustomerService.deleteCustomer: Customer with id {} deleted", id);
        } else {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
    }

    public HttpHeaders buildCustomerHeaders(Long id) {
        log.info("Action.Log.CustomerService.buildCustomerHeaders: Fetching customer info in headers with id: {}", id);
        CustomerDTO customer = getCustomerById(id);
        if (customer == null) {
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Customer-ID", String.valueOf(customer.getId()));
        headers.set("Customer-Name", customer.getName());
        headers.set("Customer-Email", customer.getEmail());
        log.info("Action.Log.CustomerService.buildCustomerHeaders: Fetched customer info in headers with id: {}", id);

        return headers;
    }

}
