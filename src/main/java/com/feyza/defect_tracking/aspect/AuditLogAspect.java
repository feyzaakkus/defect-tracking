package com.feyza.defect_tracking.aspect;

import com.feyza.defect_tracking.annotation.LogExecution;
import com.feyza.defect_tracking.entity.AuditLog;
import com.feyza.defect_tracking.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;

    @Around("@annotation(logExecution)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint, LogExecution logExecution) throws Throwable {
        Object result = joinPoint.proceed();

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = (authentication != null && authentication.isAuthenticated())
                    ? authentication.getName()
                    : "ANONYMOUS";

            Long entityId = extractEntityId(joinPoint.getArgs(), result);

            AuditLog log = AuditLog.builder()
                    .user(username)
                    .action(logExecution.action())
                    .entityType(logExecution.entityType())
                    .entityId(entityId)
                    .actionDate(LocalDateTime.now())
                    .build();

            auditLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("Audit log error: " + e.getMessage());
        }

        return result;
    }

    private Long extractEntityId(Object[] args, Object result) {
        if (args != null) {
            for (Object arg : args) {
                if (arg instanceof Long) {
                    return (Long) arg;
                }
            }
        }
        if (result != null) {
            try {
                Method getIdMethod = result.getClass().getMethod("getId");
                Object idObj = getIdMethod.invoke(result);
                if (idObj instanceof Long) {
                    return (Long) idObj;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
