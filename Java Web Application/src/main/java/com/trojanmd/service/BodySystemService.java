package com.trojanmd.service;

import com.trojanmd.domain.BodySystem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BodySystemService {

    BodySystem get(Long id);

    BodySystem getByName(String name);

    List<BodySystem> findAll();

    Page<BodySystem> listBodySystems(Pageable pageable);

    BodySystem save(BodySystem bodySystem);

    BodySystem update(Long id, BodySystem bodySystem);

    String delete(Long id);

}
