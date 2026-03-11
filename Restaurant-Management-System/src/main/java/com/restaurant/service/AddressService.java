package com.restaurant.service;

import com.restaurant.dto.request.AddressRequest;
import com.restaurant.exception.BadRequestException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.model.Address;
import com.restaurant.model.Customer;
import com.restaurant.model.Location;
import com.restaurant.repository.AddressRepository;
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
public class AddressService {
    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    private final LocationRepository locationRepository;

    @Transactional
    public Address createAddress(AddressRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Location village = locationRepository.findById(request.getVillageId())
            .orElseThrow(() -> new ResourceNotFoundException("Village not found"));

        // Validate that it's actually a village
        if (village.getType() != Location.LocationType.VILLAGE) {
            throw new BadRequestException("Location must be a VILLAGE");
        }

        Address address = Address.builder()
            .customer(customer)
            .village(village)
            .streetAddress(request.getStreetAddress())
            .postalCode(request.getPostalCode())
            .isDefault(request.getIsDefault())
            .build();

        return addressRepository.save(address);
    }

    public List<Address> getAddressesByCustomerId(Long customerId) {
        return addressRepository.findByCustomerId(customerId);
    }

    public Page<Address> getAddressesByProvince(String provinceName, Pageable pageable) {
        return addressRepository.findByVillage_Parent_Parent_Parent_Parent_Name(provinceName, pageable);
    }

    public Address getAddressById(Long id) {
        return addressRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }

    @Transactional
    public Address updateAddress(Long id, AddressRequest request) {
        Address address = getAddressById(id);

        Location village = locationRepository.findById(request.getVillageId())
            .orElseThrow(() -> new ResourceNotFoundException("Village not found"));

        if (village.getType() != Location.LocationType.VILLAGE) {
            throw new BadRequestException("Location must be a VILLAGE");
        }

        address.setVillage(village);
        address.setStreetAddress(request.getStreetAddress());
        address.setPostalCode(request.getPostalCode());
        address.setIsDefault(request.getIsDefault());

        return addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new ResourceNotFoundException("Address not found");
        }
        addressRepository.deleteById(id);
    }
}
