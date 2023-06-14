package com.example.simplerestapp.service;
import com.example.simplerestapp.dto.CustomerDTO;
import com.example.simplerestapp.entity.Customer;
import com.example.simplerestapp.exception.ResourceNotFoundException;
import com.example.simplerestapp.mappers.CustomerMapper;
import com.example.simplerestapp.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(CustomerMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return CustomerMapper.INSTANCE.toDTO(customer);
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        Customer customer = CustomerMapper.INSTANCE.toEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return CustomerMapper.INSTANCE.toDTO(savedCustomer);
    }

    public CustomerDTO updateCustomer(Long id, CustomerDTO updatedCustomerDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        BeanUtils.copyProperties(updatedCustomerDTO, existingCustomer, "id");

        Customer savedCustomer = customerRepository.save(existingCustomer);
        return CustomerMapper.INSTANCE.toDTO(savedCustomer);
    }

    public CustomerDTO partiallyUpdateCustomer(Long id, CustomerDTO updatedCustomerDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        if (updatedCustomerDTO.getName() != null) {
            existingCustomer.setName(updatedCustomerDTO.getName());
        }
        if (updatedCustomerDTO.getEmail() != null) {
            existingCustomer.setEmail(updatedCustomerDTO.getEmail());
        }

        Customer savedCustomer = customerRepository.save(existingCustomer);
        return CustomerMapper.INSTANCE.toDTO(savedCustomer);
    }

    public void deleteCustomer(Long id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isPresent()) {
            customerRepository.delete(optionalCustomer.get());
        } else {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
    }

    public HttpHeaders buildCustomerHeaders(Long id) {
        CustomerDTO customer = getCustomerById(id);
        if (customer == null) {
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Customer-ID", String.valueOf(customer.getId()));
        headers.set("Customer-Name", customer.getName());
        headers.set("Customer-Email", customer.getEmail());

        return headers;
    }

}