package com.restaurant.config;

import com.restaurant.model.*;
import com.restaurant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final MenuItemRepository menuItemRepository;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() == 0) {
            initializeUsers();
        }
        if (customerRepository.count() == 0) {
            initializeCustomers();
        }
        if (menuItemRepository.count() == 0) {
            initializeMenuItems();
        }
    }

    private void initializeUsers() {
        // Manager
        User manager = User.builder()
            .username("mugisha_manager")
            .password("password123")
            .fullName("Jean Paul Mugisha")
            .role(User.UserRole.MANAGER)
            .phone("+250788123456")
            .email("mugisha@restaurant.rw")
            .isActive(true)
            .build();

        // Waiters
        User waiter1 = User.builder()
            .username("uwase_waiter")
            .password("password123")
            .fullName("Marie Uwase")
            .role(User.UserRole.WAITER)
            .phone("+250788234567")
            .email("uwase@restaurant.rw")
            .isActive(true)
            .build();

        User waiter2 = User.builder()
            .username("nkunda_waiter")
            .password("password123")
            .fullName("Eric Nkunda")
            .role(User.UserRole.WAITER)
            .phone("+250788345678")
            .email("nkunda@restaurant.rw")
            .isActive(true)
            .build();

        // Cashier
        User cashier = User.builder()
            .username("mukamana_cashier")
            .password("password123")
            .fullName("Grace Mukamana")
            .role(User.UserRole.CASHIER)
            .phone("+250788456789")
            .email("mukamana@restaurant.rw")
            .isActive(true)
            .build();

        userRepository.save(manager);
        userRepository.save(waiter1);
        userRepository.save(waiter2);
        userRepository.save(cashier);
    }

    private void initializeCustomers() {
        Customer customer1 = Customer.builder()
            .name("Patrick Habimana")
            .phone("+250788567890")
            .email("habimana@email.rw")
            .address("KG 15 Ave, Kigali")
            .build();

        Customer customer2 = Customer.builder()
            .name("Claudine Uwera")
            .phone("+250788678901")
            .email("uwera@email.rw")
            .address("KN 5 Rd, Kigali")
            .build();

        Customer customer3 = Customer.builder()
            .name("Samuel Niyonzima")
            .phone("+250788789012")
            .email("niyonzima@email.rw")
            .address("Huye District")
            .build();

        customer1 = customerRepository.save(customer1);
        customer2 = customerRepository.save(customer2);
        customer3 = customerRepository.save(customer3);

        // Add addresses
        Address address1 = Address.builder()
            .customer(customer1)
            .province("Kigali")
            .city("Kigali City")
            .district("Gasabo")
            .streetAddress("KG 15 Ave, Kimironko")
            .postalCode("KG001")
            .isDefault(true)
            .build();

        Address address2 = Address.builder()
            .customer(customer2)
            .province("Kigali")
            .city("Kigali City")
            .district("Kicukiro")
            .streetAddress("KN 5 Rd, Kicukiro")
            .postalCode("KG002")
            .isDefault(true)
            .build();

        Address address3 = Address.builder()
            .customer(customer3)
            .province("Southern")
            .city("Huye")
            .district("Huye")
            .streetAddress("Butare Town, Main Street")
            .postalCode("HY001")
            .isDefault(true)
            .build();

        addressRepository.save(address1);
        addressRepository.save(address2);
        addressRepository.save(address3);
    }

    private void initializeMenuItems() {
        // Traditional Rwandan dishes
        MenuItem isombe = MenuItem.builder()
            .name("Isombe")
            .description("Cassava leaves cooked with peanut sauce")
            .price(new BigDecimal("3500"))
            .category("Main Course")
            .isAvailable(true)
            .preparationTime(30)
            .build();

        MenuItem brochettes = MenuItem.builder()
            .name("Brochettes")
            .description("Grilled meat skewers (beef, goat, or chicken)")
            .price(new BigDecimal("5000"))
            .category("Main Course")
            .isAvailable(true)
            .preparationTime(25)
            .build();

        MenuItem ugali = MenuItem.builder()
            .name("Ugali")
            .description("Cornmeal porridge served with vegetables")
            .price(new BigDecimal("2000"))
            .category("Main Course")
            .isAvailable(true)
            .preparationTime(20)
            .build();

        MenuItem sambaza = MenuItem.builder()
            .name("Sambaza")
            .description("Fried small fish from Lake Kivu")
            .price(new BigDecimal("4000"))
            .category("Appetizer")
            .isAvailable(true)
            .preparationTime(15)
            .build();

        MenuItem ibirayi = MenuItem.builder()
            .name("Ibirayi")
            .description("Fried Irish potatoes")
            .price(new BigDecimal("1500"))
            .category("Side Dish")
            .isAvailable(true)
            .preparationTime(15)
            .build();

        MenuItem ikivuguto = MenuItem.builder()
            .name("Ikivuguto")
            .description("Traditional fermented milk")
            .price(new BigDecimal("1000"))
            .category("Beverage")
            .isAvailable(true)
            .preparationTime(5)
            .build();

        MenuItem urwagwa = MenuItem.builder()
            .name("Banana Juice")
            .description("Fresh banana juice")
            .price(new BigDecimal("1500"))
            .category("Beverage")
            .isAvailable(true)
            .preparationTime(5)
            .build();

        MenuItem matoke = MenuItem.builder()
            .name("Matoke")
            .description("Cooked plantains with sauce")
            .price(new BigDecimal("3000"))
            .category("Main Course")
            .isAvailable(true)
            .preparationTime(35)
            .build();

        menuItemRepository.save(isombe);
        menuItemRepository.save(brochettes);
        menuItemRepository.save(ugali);
        menuItemRepository.save(sambaza);
        menuItemRepository.save(ibirayi);
        menuItemRepository.save(ikivuguto);
        menuItemRepository.save(urwagwa);
        menuItemRepository.save(matoke);
    }
}
