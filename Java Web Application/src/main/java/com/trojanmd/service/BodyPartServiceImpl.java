package com.trojanmd.service;

import com.trojanmd.domain.BodyPart;
import com.trojanmd.domain.Symptom;
import com.trojanmd.repository.BodyPartRepository;
import com.trojanmd.utils.MyBeanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BodyPartServiceImpl implements BodyPartService{

    @Autowired
    private BodyPartRepository bodyPartRepository;

    @Autowired
    private SymptomService symptomService;

    @Override
    public BodyPart get(Long id) {
        return bodyPartRepository.getOne(id);
    }

    @Override
    public BodyPart getByName(String name) {
        return bodyPartRepository.findByName(name);
    }

    @Override
    public List<BodyPart> findAll() {
        return bodyPartRepository.findAll();
    }

    @Override
    public Page<BodyPart> listBodyParts(Pageable pageable) {
        return bodyPartRepository.findAll(pageable);
    }

    @Override
    public BodyPart save(BodyPart bodyPart) {
        return bodyPartRepository.save(bodyPart);
    }

    @Override
    public BodyPart update(Long id, BodyPart bodyPart) {
        BodyPart b = bodyPartRepository.getOne(id);
        BeanUtils.copyProperties(bodyPart, b, MyBeanUtils.getNullPropertyNames(bodyPart));
        return bodyPartRepository.save(b);
    }

    @Override
    public String delete(Long id) {
        List<Symptom> symptoms = symptomService.listSymptomsByBodyPart(bodyPartRepository.getOne(id));
        if(symptoms.isEmpty()) {
            bodyPartRepository.deleteById(id);
        }
        else {
            return "error";
        }
        return "success";
    }
}
