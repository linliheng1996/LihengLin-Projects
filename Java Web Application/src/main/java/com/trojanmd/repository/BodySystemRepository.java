package com.trojanmd.repository;

import com.trojanmd.domain.BodySystem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodySystemRepository extends JpaRepository<BodySystem, Long> {
    BodySystem findByName(String name);
}
