package com.trojanmd.repository;

import com.trojanmd.domain.Article;
import com.trojanmd.domain.Disease;
import com.trojanmd.domain.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findAllByDiseasesContains(Disease disease);
    List<Article> findAllBySymptomsContains(Symptom symptom);
    Article findByTitle(String title);
}
