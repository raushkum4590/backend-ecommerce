package com.example.demo.service;

import com.example.demo.entity.Address;
import com.example.demo.entity.User;
import com.example.demo.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public List<Address> getUserAddresses(User user) {
        return addressRepository.findByUser(user);
    }

    public Address getAddressById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
    }

    public Address getDefaultAddress(User user) {
        return addressRepository.findByUserAndIsDefaultTrue(user)
                .orElse(null);
    }

    @Transactional
    public Address addAddress(User user, Address address) {
        address.setUser(user);

        // If this is the first address or marked as default, make it default
        List<Address> existingAddresses = addressRepository.findByUser(user);
        if (existingAddresses.isEmpty() || address.getIsDefault()) {
            // Remove default flag from other addresses
            existingAddresses.forEach(addr -> {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            });
            address.setIsDefault(true);
        }

        return addressRepository.save(address);
    }

    @Transactional
    public Address updateAddress(Long id, Address addressDetails) {
        Address address = getAddressById(id);
        address.setFullName(addressDetails.getFullName());
        address.setPhoneNumber(addressDetails.getPhoneNumber());
        address.setAddressLine1(addressDetails.getAddressLine1());
        address.setAddressLine2(addressDetails.getAddressLine2());
        address.setCity(addressDetails.getCity());
        address.setState(addressDetails.getState());
        address.setPostalCode(addressDetails.getPostalCode());
        address.setCountry(addressDetails.getCountry());

        return addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }

    @Transactional
    public Address setDefaultAddress(User user, Long addressId) {
        Address address = getAddressById(addressId);

        if (!address.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Address does not belong to user");
        }

        // Remove default flag from all user addresses
        List<Address> userAddresses = addressRepository.findByUser(user);
        userAddresses.forEach(addr -> {
            addr.setIsDefault(false);
            addressRepository.save(addr);
        });

        // Set this address as default
        address.setIsDefault(true);
        return addressRepository.save(address);
    }
}
