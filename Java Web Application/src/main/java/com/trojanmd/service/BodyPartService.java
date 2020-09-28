package com.trojanmd.service;

import com.trojanmd.domain.BodyPart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BodyPartService {

    BodyPart get(Long id);

    BodyPart getByName(String name);

    List<BodyPart> findAll();

    Page<BodyPart> listBodyParts(Pageable pageable);

    BodyPart save(BodyPart bodyPart);

    BodyPart update(Long id, BodyPart bodyPart);

    String delete(Long id);
}
