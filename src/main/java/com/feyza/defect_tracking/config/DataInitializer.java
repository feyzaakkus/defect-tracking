package com.feyza.defect_tracking.config;

import com.feyza.defect_tracking.entity.Defect;
import com.feyza.defect_tracking.enums.Priority;
import com.feyza.defect_tracking.enums.Severity;
import com.feyza.defect_tracking.enums.Status;
import com.feyza.defect_tracking.repository.DefectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DefectRepository defectRepository;

    @Override
    public void run(String... args) {

        if (defectRepository.count() == 0) {

            Defect defect1 = new Defect();
            defect1.setTitle("Login Page Button Crash");
            defect1.setDescription("Login button does not respond when clicked on mobile browsers.");
            defect1.setSeverity(Severity.CRITICAL);
            defect1.setPriority(Priority.HIGH);
            defect1.setStatus(Status.OPEN);
            defect1.setCreatedDate(LocalDateTime.now());

            Defect defect2 = new Defect();
            defect2.setTitle("Profile Picture NullPointer Error");
            defect2.setDescription("Throws NullPointerException when user deletes their profile picture.");
            defect2.setSeverity(Severity.HIGH);
            defect2.setPriority(Priority.MEDIUM);
            defect2.setStatus(Status.IN_PROGRESS);
            defect2.setCreatedDate(LocalDateTime.now());

            Defect defect3 = new Defect();
            defect3.setTitle("Typo in Footer Links");
            defect3.setDescription("The word 'Contact Us' is misspelled as 'Contat Us' in footer.");
            defect3.setSeverity(Severity.LOW);
            defect3.setPriority(Priority.LOW);
            defect3.setStatus(Status.OPEN);
            defect3.setCreatedDate(LocalDateTime.now());

            Defect defect4 = new Defect();
            defect4.setTitle("Payment Gateway Timeout");
            defect4.setDescription("Credit card transactions time out after 30 seconds of inactivity.");
            defect4.setSeverity(Severity.CRITICAL);
            defect4.setPriority(Priority.HIGH);
            defect4.setStatus(Status.OPEN);
            defect4.setCreatedDate(LocalDateTime.now());

            Defect defect5 = new Defect();
            defect5.setTitle("Excel Export Memory Leak");
            defect5.setDescription("Exporting 10k+ rows causes OutOfMemoryError on the server.");
            defect5.setSeverity(Severity.HIGH);
            defect5.setPriority(Priority.HIGH);
            defect5.setStatus(Status.CLOSED);
            defect5.setCreatedDate(LocalDateTime.now());

            defectRepository.save(defect1);
            defectRepository.save(defect2);
            defectRepository.save(defect3);
            defectRepository.save(defect4);
            defectRepository.save(defect5);

            System.out.println("Database pre-populated with 5 sample defects successfully.");
        }
    }
}

