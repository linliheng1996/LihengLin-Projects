package com.trojanmd.repository;

import com.trojanmd.domain.BodyPart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodyPartRepository extends JpaRepository<BodyPart,Long> {
    BodyPart findByName(String name);
}
