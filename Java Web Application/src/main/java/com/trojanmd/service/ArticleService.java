package com.trojanmd.service;

import com.trojanmd.domain.Article;
import com.trojanmd.domain.Disease;
import com.trojanmd.domain.Symptom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleService {

    Article get(Long id);

    Article getByTitle(String title);

    Page<Article> listArticles(Pageable pageable);

    List<Article> listArticlesByDisease(Disease disease);

    List<Article> listArticlesBySymptom(Symptom symptom);

    Article save(Article article);

    Article update(Long id, Article article);

    void delete(Long id);
}
