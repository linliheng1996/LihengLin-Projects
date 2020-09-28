package com.trojanmd.service;

import com.trojanmd.domain.BodySystem;
import com.trojanmd.domain.Disease;
import com.trojanmd.repository.DiseaseRepository;
import com.trojanmd.utils.MyBeanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DiseaseServiceImpl implements DiseaseService{

    @Autowired
    private DiseaseRepository diseaseRepository;

    @Override
    public Disease save(Disease disease) {
        if(disease.getId() == null){
            disease.setCreateTime(new Date());
        }
        disease.setUpdateTime(new Date());
        return diseaseRepository.save(disease);
    }

    @Override
    public Disease get(Long id) {
        return diseaseRepository.getOne(id);
    }

    @Override
    public Disease getByName(String name) {
        return diseaseRepository.findByName(name);
    }

    @Override
    public Page<Disease> listDiseases(Pageable pageable) {
        return diseaseRepository.findAll(pageable);
    }

    @Override
    public List<Disease> listDiseasesByBodySystem(BodySystem bodySystem) {
        return diseaseRepository.findAllByBodySystem(bodySystem);
    }

    @Override
    public List<Disease> listDiseases() {
        return diseaseRepository.findAll(Sort.by("name"));
    }

    @Override
    public List<Disease> listDiseases(String ids) {
        return diseaseRepository.findAllById(convertToList(ids));
    }

    private List<Long> convertToList(String ids) {
        List<Long> list = new ArrayList<>();
        if (ids != null && !ids.equals("")) {
            String[] idArray = ids.split(",");
            for (int i=0; i < idArray.length;i++) {
                list.add(Long.parseLong(idArray[i]));
            }
        }
        return list;
    }

    @Override
    public Disease update(Long id, Disease disease) {
        Disease d = diseaseRepository.getOne(id);
        BeanUtils.copyProperties(disease, d, MyBeanUtils.getNullPropertyNames(disease));
        d.setUpdateTime(new Date());
        return diseaseRepository.save(d);
    }

    @Override
    public void delete(Long id) {
        diseaseRepository.deleteById(id);
    }
}
