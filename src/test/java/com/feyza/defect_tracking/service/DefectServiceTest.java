package com.feyza.defect_tracking.service;

import com.feyza.defect_tracking.dto.request.DefectCreateRequest;
import com.feyza.defect_tracking.dto.request.DefectUpdateRequest;
import com.feyza.defect_tracking.dto.response.DefectResponse;
import com.feyza.defect_tracking.entity.Defect;
import com.feyza.defect_tracking.entity.User;
import com.feyza.defect_tracking.enums.Priority;
import com.feyza.defect_tracking.enums.Role;
import com.feyza.defect_tracking.enums.Severity;
import com.feyza.defect_tracking.enums.Status;
import com.feyza.defect_tracking.exception.BusinessException;
import com.feyza.defect_tracking.repository.DefectRepository;
import com.feyza.defect_tracking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefectServiceTest {

    @Mock
    private DefectRepository defectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DefectService defectService;

    private User testerUser;
    private User developerUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        testerUser = new User();
        testerUser.setId(1L);
        testerUser.setUsername("tester");
        testerUser.setRole(Role.TESTER);

        developerUser = new User();
        developerUser.setId(2L);
        developerUser.setUsername("developer");
        developerUser.setRole(Role.DEVELOPER);

        adminUser = new User();
        adminUser.setId(3L);
        adminUser.setUsername("admin");
        adminUser.setRole(Role.ADMIN);

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    // 1. DEFECT OLUŞTURMA TESTLERİ
    @Test
    @DisplayName("Should set status to OPEN when defect is created")
    void createDefect_ShouldSetStatusToOpen() {
        when(authentication.getName()).thenReturn("tester");
        when(userRepository.findByUsername("tester")).thenReturn(Optional.of(testerUser));

        DefectCreateRequest request = new DefectCreateRequest();
        request.setTitle("Test Defect");
        request.setDescription("Description");
        request.setSeverity(Severity.HIGH);
        request.setPriority(Priority.HIGH);

        Defect savedDefect = new Defect();
        savedDefect.setId(1L);
        savedDefect.setTitle(request.getTitle());
        savedDefect.setDescription(request.getDescription());
        savedDefect.setSeverity(request.getSeverity());
        savedDefect.setPriority(request.getPriority());
        savedDefect.setStatus(Status.OPEN);
        savedDefect.setCreatedBy(testerUser);

        when(defectRepository.save(any(Defect.class))).thenReturn(savedDefect);

        DefectResponse response = defectService.createDefect(request);

        assertNotNull(response);
        assertEquals(Status.OPEN, response.getStatus());
        verify(defectRepository, times(1)).save(any(Defect.class));
    }

    // 2. STATUS GEÇİŞ TESTLERİ
    @Test
    @DisplayName("Should set status to ASSIGNED when defect is assigned to a developer")
    void assignDefect_ShouldChangeStatusToAssigned() {
        Defect defect = new Defect();
        defect.setId(1L);
        defect.setStatus(Status.OPEN);

        when(defectRepository.findById(1L)).thenReturn(Optional.of(defect));
        when(userRepository.findById(2L)).thenReturn(Optional.of(developerUser));
        when(defectRepository.save(any(Defect.class))).thenAnswer(i -> i.getArguments()[0]);

        DefectResponse response = defectService.assignDefect(1L, 2L);

        assertEquals(Status.ASSIGNED, response.getStatus());
        assertEquals(2L, response.getAssignedDeveloperId());
    }

    @Test
    @DisplayName("Should set status to FIXED when assigned developer resolves defect")
    void updateDefectStatus_AssignedDeveloper_ShouldSetStatusToFixed() {
        Defect defect = new Defect();
        defect.setId(1L);
        defect.setStatus(Status.ASSIGNED);
        defect.setAssignedDeveloper(developerUser);

        when(authentication.getName()).thenReturn("developer");
        when(userRepository.findByUsername("developer")).thenReturn(Optional.of(developerUser));
        when(defectRepository.findById(1L)).thenReturn(Optional.of(defect));
        when(defectRepository.save(any(Defect.class))).thenAnswer(i -> i.getArguments()[0]);

        DefectResponse response = defectService.updateDefectStatus(1L, Status.FIXED, "Resolution note");

        assertEquals(Status.FIXED, response.getStatus());
        assertEquals("Resolution note", response.getResolutionNote());
    }

    @Test
    @DisplayName("Should throw BusinessException on invalid status transition (OPEN -> CLOSED)")
    void updateDefectStatus_InvalidTransition_ShouldThrowException() {
        Defect defect = new Defect();
        defect.setId(1L);
        defect.setStatus(Status.OPEN);

        when(authentication.getName()).thenReturn("tester");
        when(userRepository.findByUsername("tester")).thenReturn(Optional.of(testerUser));
        when(defectRepository.findById(1L)).thenReturn(Optional.of(defect));

        assertThrows(BusinessException.class, () ->
                defectService.updateDefectStatus(1L, Status.CLOSED, null)
        );
    }

    // 3. YETKİ KONTROL TESTLERİ
    @Test
    @DisplayName("Should throw BusinessException when non-creator TESTER tries to update defect")
    void updateDefect_NonCreator_ShouldThrowException() {
        User otherTester = new User();
        otherTester.setId(88L);
        otherTester.setUsername("other_tester");

        Defect defect = new Defect();
        defect.setId(1L);
        defect.setCreatedBy(testerUser);

        when(authentication.getName()).thenReturn("other_tester");
        when(userRepository.findByUsername("other_tester")).thenReturn(Optional.of(otherTester));
        when(defectRepository.findById(1L)).thenReturn(Optional.of(defect));

        DefectUpdateRequest updateRequest = new DefectUpdateRequest();
        updateRequest.setDescription("Updated description");

        assertThrows(BusinessException.class, () ->
                defectService.updateDefect(1L, updateRequest)
        );
    }

    @Test
    @DisplayName("Should throw BusinessException when unassigned developer tries to fix defect")
    void updateDefectStatus_UnassignedDeveloper_ShouldThrowException() {
        User otherDeveloper = new User();
        otherDeveloper.setId(99L);
        otherDeveloper.setUsername("other_dev");

        Defect defect = new Defect();
        defect.setId(1L);
        defect.setStatus(Status.ASSIGNED);
        defect.setAssignedDeveloper(developerUser);

        when(authentication.getName()).thenReturn("other_dev");
        when(userRepository.findByUsername("other_dev")).thenReturn(Optional.of(otherDeveloper));
        when(defectRepository.findById(1L)).thenReturn(Optional.of(defect));

        assertThrows(BusinessException.class, () ->
                defectService.updateDefectStatus(1L, Status.FIXED, "Resolution note")
        );
    }

    @Test
    @DisplayName("Should delete defect successfully when called by Service")
    void deleteDefect_Success() {
        Defect defect = new Defect();
        defect.setId(1L);

        when(defectRepository.findById(1L)).thenReturn(Optional.of(defect));
        doNothing().when(defectRepository).delete(defect);

        assertDoesNotThrow(() -> defectService.deleteDefect(1L));
        verify(defectRepository, times(1)).delete(defect);
    }
}