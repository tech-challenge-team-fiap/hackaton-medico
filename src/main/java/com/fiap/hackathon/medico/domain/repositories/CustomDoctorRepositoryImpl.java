package com.fiap.hackathon.medico.domain.repositories;

import com.fiap.hackathon.medico.domain.entity.DoctorDB;
import com.fiap.hackathon.medico.domain.enums.Expertise;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomDoctorRepositoryImpl implements CustomDoctorRepository {
    private static String QUERY = "select d from DoctorDb d :where";

    private final EntityManager em;

    @Override
    public List<DoctorDB> findDoctorBy(Optional<Expertise> expertise, Optional<Long> distance) {
        List<Predicate> condition = new ArrayList<>();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DoctorDB> cq = cb.createQuery(DoctorDB.class);

        Root<DoctorDB> doctorRoot = cq.from(DoctorDB.class);

        expertise.ifPresent(value -> condition.add(cb.equal(doctorRoot.get("expertise"), value.name())));
        distance.ifPresent(value -> condition.add(cb.equal(doctorRoot.get("distance"), value.toString())));

        cq.where(condition.toArray(Predicate[]::new));

        TypedQuery<DoctorDB> query = em.createQuery(cq);
        return query.getResultList();
    }
}