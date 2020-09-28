package com.trojanmd.controller;

import com.trojanmd.domain.Article;
import com.trojanmd.service.ArticleService;
import com.trojanmd.service.DiseaseService;
import com.trojanmd.service.SymptomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private SymptomService symptomService;

    @GetMapping("/user/article/{id}")
    public String get(@PathVariable Long id, Model model){
        Article article = articleService.get(id);
        model.addAttribute("article", article);
        return "user/article";
    }

    @GetMapping("/admin/articles")
    public String listArticles(@PageableDefault(size = 10, sort = {"title"}) Pageable pageable,
                               Model model){
        model.addAttribute("page", articleService.listArticles(pageable));
        return "admin/articles";
    }

    @GetMapping("/admin/article-input")
    public String inputArticle(Model model){
        model.addAttribute("diseases", diseaseService.listDiseases());
        model.addAttribute("symptoms",symptomService.listSymptoms());
        model.addAttribute("article", new Article());

        return "admin/article-input";
    }

    @GetMapping("/admin/articles/{id}/input")
    public String inputArticle(@PathVariable Long id, Model model){
        Article article = articleService.get(id);
        model.addAttribute("article", article);
        model.addAttribute("diseases", diseaseService.listDiseases());
        model.addAttribute("symptoms",symptomService.listSymptoms());

        return "admin/article-input";
    }

    @PostMapping("/admin/article-add")
    public String addArticle(Article article,
                             @RequestParam String diseaseIds,
//                             @RequestParam String symptomIds,
                             @RequestParam("md-content") String content,
                             @RequestParam("md-content-html-code") String html,
                             RedirectAttributes redirectAttributes){
        article.setContent(content);
        article.setContentHtml(html);
        article.setDiseases(diseaseService.listDiseases(diseaseIds));
//        article.setSymptoms(symptomService.listSymptoms(symptomIds));

        Article a;

        if(article.getId() == null){
            if(articleService.getByTitle(article.getTitle()) != null){
                redirectAttributes.addFlashAttribute("errorMessage","Article already exists!");
                return "redirect:/admin/article-input";
            }

            a = articleService.save(article);
            if(a == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Fail to add new article!");
                return "redirect:/admin/article-input";
            }
            else{
                redirectAttributes.addFlashAttribute("successMessage", "Article added successfully!");

            }
        }
        else{
            a = articleService.update(article.getId(), article);
            if(a == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Fail to edit article!");
                return "redirect:/admin/article-input";
            }
            else{
                redirectAttributes.addFlashAttribute("successMessage", "Article updated successfully!");
            }
        }
        return "redirect:/admin/articles";
    }

    @GetMapping("/admin/articles/{id}/delete")
    public String deleteArticle(@PathVariable Long id, RedirectAttributes redirectAttributes){
        articleService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Delete successfully!");
        return "redirect:/admin/articles";
    }

}
