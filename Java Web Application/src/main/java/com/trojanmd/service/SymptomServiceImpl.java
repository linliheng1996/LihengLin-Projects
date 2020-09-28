package com.trojanmd.service;

import com.trojanmd.domain.BodyPart;
import com.trojanmd.domain.Symptom;
import com.trojanmd.repository.SymptomRepository;
import com.trojanmd.utils.MyBeanUtils;
import com.trojanmd.vo.SymptomQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class SymptomServiceImpl implements SymptomService{

    @Autowired
    private SymptomRepository symptomRepository;

    @Override
    public Symptom save(Symptom symptom) {
        if(symptom.getId() == null){
            symptom.setCreateTime(new Date());
        }
        symptom.setUpdateTime(new Date());
        return symptomRepository.save(symptom);
    }

    @Override
    public Symptom get(Long id) {
        return symptomRepository.getOne(id);
    }

    @Override
    public Symptom getByName(String name) {
        return symptomRepository.findByName(name);
    }

    @Override
    public Page<Symptom> listSymptoms(Pageable pageable) {
        return symptomRepository.findAll(pageable);
    }

    @Override
    public Page<Symptom> listSymptoms(Pageable pageable, SymptomQuery symptomQuery) {
        return symptomRepository.findAll(new Specification<Symptom>() {
            @Override
            public Predicate toPredicate(Root<Symptom> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (!"".equals(symptomQuery.getBodyPart()) && symptomQuery.getBodyPart() != null) {
                    predicates.add(cb.equal(root.<String>get("bodyPart"), symptomQuery.getBodyPart()));
                }
                if (symptomQuery.isPublished()) {
                    predicates.add(cb.equal(root.<Boolean>get("published"), symptomQuery.isPublished()));
                }
                if (predicates.size() == 0) {
                    return null;
                }
                Predicate[] p = new Predicate[predicates.size()];
                return cb.and(predicates.toArray(p));
            }
        },pageable);
    }

    @Override
    public List<Symptom> listSymptomsByBodyPart(BodyPart bodyPart) {
        return symptomRepository.findAllByBodyPart(bodyPart);
    }

    @Override
    public List<Symptom> listSymptoms() {
        return symptomRepository.findAll(Sort.by("name"));
    }

    @Override
    public List<Symptom> listSymptoms(String ids) {
        return symptomRepository.findAllById(convertToList(ids));
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
    public Symptom update(Long id, Symptom symptom) {
        Symptom s = symptomRepository.getOne(id);

        BeanUtils.copyProperties(symptom, s, MyBeanUtils.getNullPropertyNames(symptom));
        s.setUpdateTime(new Date());
        return symptomRepository.save(s);
    }

    @Override
    public void delete(Long id) {
        symptomRepository.deleteById(id);
    }
}
