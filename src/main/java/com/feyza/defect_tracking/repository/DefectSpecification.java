package com.feyza.defect_tracking.repository;

import com.feyza.defect_tracking.dto.request.DefectFilterRequest;
import com.feyza.defect_tracking.entity.Defect;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DefectSpecification {

    public static Specification<Defect> filterDefects(DefectFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getSeverity() != null) {
                predicates.add(criteriaBuilder.equal(root.get("severity"), filter.getSeverity()));
            }

            if (filter.getPriority() != null) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), filter.getPriority()));
            }

            if (filter.getAssignedDeveloperId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("assignedDeveloper").get("id"), filter.getAssignedDeveloperId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
