package com.feyza.defect_tracking.config;

import com.feyza.defect_tracking.entity.Defect;
import com.feyza.defect_tracking.entity.User;
import com.feyza.defect_tracking.enums.Priority;
import com.feyza.defect_tracking.enums.Role;
import com.feyza.defect_tracking.enums.Severity;
import com.feyza.defect_tracking.enums.Status;
import com.feyza.defect_tracking.repository.DefectRepository;
import com.feyza.defect_tracking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DefectRepository defectRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {

        if (userRepository.count() == 0) {

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("12345");
            admin.setRole(Role.ADMIN);

            User tester = new User();
            tester.setUsername("tester");
            tester.setPassword("12345");
            tester.setRole(Role.TESTER);

            User developer = new User();
            developer.setUsername("developer");
            developer.setPassword("12345");
            developer.setRole(Role.DEVELOPER);

            userRepository.save(admin);
            userRepository.save(tester);
            userRepository.save(developer);

            System.out.println("Sample users created successfully.");
        }

        if (defectRepository.count() == 0) {

            User defaultUser = userRepository.findByUsername("tester")
                    .orElseThrow();

            Defect defect1 = new Defect();
            defect1.setTitle("Login Page Button Crash");
            defect1.setDescription("Login button does not respond when clicked on mobile browsers.");
            defect1.setSeverity(Severity.CRITICAL);
            defect1.setPriority(Priority.HIGH);
            defect1.setStatus(Status.OPEN);
            defect1.setCreatedBy(defaultUser);
            defect1.setCreatedDate(LocalDateTime.now());
            defect1.setUpdatedDate(LocalDateTime.now());

            Defect defect2 = new Defect();
            defect2.setTitle("Profile Picture NullPointer Error");
            defect2.setDescription("Throws NullPointerException when user deletes their profile picture.");
            defect2.setSeverity(Severity.HIGH);
            defect2.setPriority(Priority.MEDIUM);
            defect2.setStatus(Status.ASSIGNED);
            defect2.setCreatedBy(defaultUser);
            defect2.setCreatedDate(LocalDateTime.now());
            defect2.setUpdatedDate(LocalDateTime.now());

            Defect defect3 = new Defect();
            defect3.setTitle("Typo in Footer Links");
            defect3.setDescription("The word 'Contact Us' is misspelled as 'Contat Us' in footer.");
            defect3.setSeverity(Severity.LOW);
            defect3.setPriority(Priority.LOW);
            defect3.setStatus(Status.OPEN);
            defect3.setCreatedBy(defaultUser);
            defect3.setCreatedDate(LocalDateTime.now());
            defect3.setUpdatedDate(LocalDateTime.now());

            Defect defect4 = new Defect();
            defect4.setTitle("Payment Gateway Timeout");
            defect4.setDescription("Credit card transactions time out after 30 seconds of inactivity.");
            defect4.setSeverity(Severity.CRITICAL);
            defect4.setPriority(Priority.HIGH);
            defect4.setStatus(Status.OPEN);
            defect4.setCreatedBy(defaultUser);
            defect4.setCreatedDate(LocalDateTime.now());
            defect4.setUpdatedDate(LocalDateTime.now());

            Defect defect5 = new Defect();
            defect5.setTitle("Excel Export Memory Leak");
            defect5.setDescription("Exporting 10k+ rows causes OutOfMemoryError on the server.");
            defect5.setSeverity(Severity.HIGH);
            defect5.setPriority(Priority.HIGH);
            defect5.setStatus(Status.CLOSED);
            defect5.setCreatedBy(defaultUser);
            defect5.setCreatedDate(LocalDateTime.now());
            defect5.setUpdatedDate(LocalDateTime.now());

            defectRepository.save(defect1);
            defectRepository.save(defect2);
            defectRepository.save(defect3);
            defectRepository.save(defect4);
            defectRepository.save(defect5);

            System.out.println("Database pre-populated with sample data successfully.");
        }
    }
}
