package com.restaurant.service;

import com.restaurant.dto.request.CustomerRequest;
import com.restaurant.exception.BadRequestException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.model.Customer;
import com.restaurant.model.Location;
import com.restaurant.repository.CustomerRepository;
import com.restaurant.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final LocationRepository locationRepository;

    @Transactional
    public Customer createCustomer(CustomerRequest request) {
        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone number already exists");
        }

        Location village = null;
        if (request.getVillageId() != null) {
            village = locationRepository.findById(request.getVillageId())
                .orElseThrow(() -> new ResourceNotFoundException("Village not found"));

            if (village.getType() != Location.LocationType.VILLAGE) {
                throw new BadRequestException("Location must be a VILLAGE");
            }
        }

        Customer customer = Customer.builder()
            .name(request.getName())
            .phone(request.getPhone())
            .email(request.getEmail())
            .village(village)
            .streetAddress(request.getStreetAddress())
            .build();

        return customerRepository.save(customer);
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    public Page<Customer> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    // Province-based query implementation
    public List<Customer> getCustomersByProvince(String province) {
        return customerRepository.findByProvince(province);
    }

    public Page<Customer> getCustomersByProvince(String province, Pageable pageable) {
        return customerRepository.findByProvince(province, pageable);
    }

    public Page<Customer> getCustomersByDistrict(String district, Pageable pageable) {
        return customerRepository.findByDistrict(district, pageable);
    }

    public Page<Customer> getCustomersBySector(String sector, Pageable pageable) {
        return customerRepository.findBySector(sector, pageable);
    }

    public Page<Customer> getCustomersByCell(String cell, Pageable pageable) {
        return customerRepository.findByCell(cell, pageable);
    }

    public Page<Customer> getCustomersByVillage(String village, Pageable pageable) {
        return customerRepository.findByVillage(village, pageable);
    }

    @Transactional
    public Customer updateCustomer(Long id, CustomerRequest request) {
        Customer customer = getCustomerById(id);

        if (!customer.getPhone().equals(request.getPhone()) && 
            customerRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone number already exists");
        }

        Location village = null;
        if (request.getVillageId() != null) {
            village = locationRepository.findById(request.getVillageId())
                .orElseThrow(() -> new ResourceNotFoundException("Village not found"));

            if (village.getType() != Location.LocationType.VILLAGE) {
                throw new BadRequestException("Location must be a VILLAGE");
            }
        }

        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setVillage(village);
        customer.setStreetAddress(request.getStreetAddress());

        return customerRepository.save(customer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }
}
