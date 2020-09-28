package com.trojanmd.service;

import com.trojanmd.domain.BodySystem;
import com.trojanmd.domain.Disease;
import com.trojanmd.domain.Symptom;
import com.trojanmd.repository.BodySystemRepository;
import com.trojanmd.utils.MyBeanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BodySystemServiceImpl implements BodySystemService{

    @Autowired
    BodySystemRepository bodySystemRepository;

    @Autowired
    DiseaseService diseaseService;

    @Override
    public BodySystem get(Long id) {
        return bodySystemRepository.getOne(id);
    }

    @Override
    public BodySystem getByName(String name) {
        return bodySystemRepository.findByName(name);
    }

    @Override
    public List<BodySystem> findAll() {
        return bodySystemRepository.findAll();
    }

    @Override
    public Page<BodySystem> listBodySystems(Pageable pageable) {
        return bodySystemRepository.findAll(pageable);
    }

    @Override
    public BodySystem save(BodySystem bodySystem) {
        return bodySystemRepository.save(bodySystem);
    }

    @Override
    public BodySystem update(Long id, BodySystem bodySystem) {
        BodySystem b = bodySystemRepository.getOne(id);
        BeanUtils.copyProperties(bodySystem, b, MyBeanUtils.getNullPropertyNames(bodySystem));
        return bodySystemRepository.save(b);
    }

    @Override
    public String delete(Long id) {
        List<Disease> diseases = diseaseService.listDiseasesByBodySystem(bodySystemRepository.getOne(id));
        if(diseases.isEmpty()){
            bodySystemRepository.deleteById(id);
        }
        else {
            return "error";
        }
        return "success";
    }
}
