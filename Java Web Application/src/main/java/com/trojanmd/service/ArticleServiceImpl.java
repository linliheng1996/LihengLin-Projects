package com.trojanmd.service;

import com.trojanmd.domain.Article;
import com.trojanmd.domain.Disease;
import com.trojanmd.domain.Symptom;
import com.trojanmd.repository.ArticleRepository;
import com.trojanmd.utils.MyBeanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService{

    @Autowired
    private ArticleRepository articleRepository;

    @Override
    public Article get(Long id) {
        return articleRepository.getOne(id);
    }

    @Override
    public Article getByTitle(String title) {
        return articleRepository.findByTitle(title);
    }

    @Override
    public Page<Article> listArticles(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    @Override
    public List<Article> listArticlesByDisease(Disease disease) {
        return articleRepository.findAllByDiseasesContains(disease);
    }

    @Override
    public List<Article> listArticlesBySymptom(Symptom symptom) {
        return articleRepository.findAllBySymptomsContains(symptom);
    }

    @Override
    public Article save(Article article) {
        if(article.getId() == null){
            article.setCreateTime(new Date());
        }
        article.setUpdateTime(new Date());
        return articleRepository.save(article);
    }

    @Override
    public Article update(Long id, Article article) {
        Article a = articleRepository.getOne(id);

        BeanUtils.copyProperties(article, a, MyBeanUtils.getNullPropertyNames(article));
        a.setUpdateTime(new Date());
        return articleRepository.save(a);
    }

    @Override
    public void delete(Long id) {
        articleRepository.deleteById(id);
    }
}
