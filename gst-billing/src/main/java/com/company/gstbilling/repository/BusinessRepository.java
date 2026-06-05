package com.company.gstbilling.repository;

import com.company.gstbilling.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {

    Optional<Business> findByGstNumber(String gstNumber);

    boolean existsByGstNumber(String gstNumber);
}
