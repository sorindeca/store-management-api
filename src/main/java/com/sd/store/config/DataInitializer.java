package com.sd.store.config;

import com.sd.store.model.Product;
import com.sd.store.model.Role;
import com.sd.store.model.User;
import com.sd.store.repository.ProductRepository;
import com.sd.store.repository.RoleRepository;
import com.sd.store.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, 
                          ProductRepository productRepository, 
                          RoleRepository roleRepository, 
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public ApplicationRunner initData() {
        return args -> seedData();
    }

    @Transactional
    void seedData() {
        seedUsers();
        seedProducts();
        log.info("Data seeding completed successfully");
    }

    private void seedUsers() {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
        Role managerRole = roleRepository.findByName("ROLE_MANAGER").orElseThrow();
        Role employeeRole = roleRepository.findByName("ROLE_EMPLOYEE").orElseThrow();
        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow();

        createUserIfAbsent("admin", "admin123", "admin@store.com", Set.of(adminRole));
        createUserIfAbsent("manager", "manager123", "manager@store.com", Set.of(managerRole));
        createUserIfAbsent("employee", "employee123", "employee@store.com", Set.of(employeeRole));
        createUserIfAbsent("user", "user123", "user@store.com", Set.of(userRole));
    }

    private void createUserIfAbsent(String username, String rawPassword, String email, Set<Role> roles) {
        userRepository.findByUsername(username).ifPresentOrElse(
            u -> {}, 
            () -> {
                var u = new User(username, passwordEncoder.encode(rawPassword), email, roles);
                userRepository.save(u);
                log.info("Seeded user {}", username);
            }
        );
    }

    private void seedProducts() {
        createProductIfAbsent("Laptop", "Gaming laptop", new BigDecimal("7999.99"), 10, "Electronics");
        createProductIfAbsent("Smartphone", "Iphone", new BigDecimal("5699.99"), 15, "Electronics");
        createProductIfAbsent("Book", "Java programming guide", new BigDecimal("49.25"), 25, "Books");
        createProductIfAbsent("Office Chair", "Ergonomic office chair", new BigDecimal("1000.00"), 8, "Furniture");
        createProductIfAbsent("Coffee", "Coffee beans", new BigDecimal("40.50"), 50, "Food");
    }

    private void createProductIfAbsent(String name, String description, BigDecimal price, int quantity, String category) {
        productRepository.findByName(name).ifPresentOrElse(
            p -> {},
            () -> {
                productRepository.save(new Product(name, description, price, quantity, category));
                log.info("Seeded product {}", name);
            }
        );
    }
}
