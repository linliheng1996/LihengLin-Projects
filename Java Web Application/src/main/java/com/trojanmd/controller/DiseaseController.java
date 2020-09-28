package com.trojanmd.controller;

import com.trojanmd.domain.*;
import com.trojanmd.service.ArticleService;
import com.trojanmd.service.BodySystemService;
import com.trojanmd.service.DiseaseService;
import com.trojanmd.service.DoctorService;
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

import java.util.List;

@Controller
public class DiseaseController {

    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private BodySystemService bodySystemService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private ArticleService articleService;

    @GetMapping("/admin/diseases")
    public String diseases(@PageableDefault(size = 10, sort = {"name"}) Pageable pageable,
                           Model model){
        model.addAttribute("page", diseaseService.listDiseases(pageable));
        return "admin/diseases";
    }

    @GetMapping("/admin/disease-input")
    public String inputDisease(Model model){
        model.addAttribute("bodySystems", bodySystemService.findAll());
        model.addAttribute("disease", new Disease());
        return "admin/disease-input";
    }

    @GetMapping("/admin/diseases/{id}/input")
    public String editDisease(@PathVariable Long id, Model model){
        Disease disease = diseaseService.get(id);
        model.addAttribute("disease", disease);
        model.addAttribute("bodySystems", bodySystemService.findAll());
        return "admin/disease-input";
    }

    @PostMapping("/admin/disease-add")
    public String addDisease(Disease disease,
                             @RequestParam("md-content1") String content1,
                             @RequestParam("md-content1-html-code") String html1,
                             @RequestParam("md-content2") String content2,
                             @RequestParam("md-content2-html-code") String html2,
                             @RequestParam("md-content3") String content3,
                             @RequestParam("md-content3-html-code") String html3,
                             @RequestParam("md-content4") String content4,
                             @RequestParam("md-content4-html-code") String html4,
                             RedirectAttributes redirectAttributes){
        disease.setBodySystem(bodySystemService.getByName(disease.getBodySystemName()));
        disease.setDescription(content1);
        disease.setCause(content2);
        disease.setTreatment(content3);
        disease.setPrevention(content4);
        disease.setDescriptionHtml(html1);
        disease.setCauseHtml(html2);
        disease.setTreatmentHtml(html3);
        disease.setPreventionHtml(html4);
        //First determine save or update operation
        Disease d;
        //save
        if(disease.getId() == null){
            //Determine if the symptom already exists
            if(diseaseService.getByName(disease.getName()) != null){
                redirectAttributes.addFlashAttribute("errorMessage","Symptom already exists!");
                return "redirect:/admin/disease-input";
            }

            //Determine if the operation is successful
            d = diseaseService.save(disease);
            if(d == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Fail to add new symptom!");
                return "redirect:/admin/disease-input";
            }
            else{
                redirectAttributes.addFlashAttribute("successMessage", "Symptom added successfully!");

            }
        }
        else{//update
            d = diseaseService.update(disease.getId(), disease);
            if(d == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Fail to edit symptom!");
                return "redirect:/admin/disease-input";
            }
            else{
                redirectAttributes.addFlashAttribute("successMessage", "Symptom updated successfully!");
            }
        }

        return "redirect:/admin/diseases";
    }

    @GetMapping("/admin/diseases/{id}/delete")
    public String deleteDisease(@PathVariable Long id, RedirectAttributes attributes){
        diseaseService.delete(id);
        attributes.addFlashAttribute("successMessage", "Delete successfully!");
        return "redirect:/admin/diseases";
    }

    @GetMapping("/user/diseases")
    public String searchForDiseases(Model model){
        List<BodySystem> bodySystems = bodySystemService.findAll();
        model.addAttribute("bodySystems",bodySystems);
        return "user/search-for-disease";
    }

    @GetMapping("/user/diseases/{systemId}")
    public String listDiseaseByBodySystem(@PathVariable Long systemId,
                                          Model model){
        List<BodySystem> bodySystems = bodySystemService.findAll();
        List<Disease> diseases = diseaseService.listDiseasesByBodySystem(bodySystemService.get(systemId));
        model.addAttribute("bodySystems", bodySystems);
        model.addAttribute("diseases",diseases);
        model.addAttribute("selected",systemId);

        return "user/search-for-disease";
    }

    @GetMapping("/user/disease/{id}")
    public String displayDisease(@PathVariable Long id, Model model){
        Disease disease = diseaseService.get(id);
        List<Doctor> doctors = doctorService.listDoctorsByDisease(disease);
        List<Doctor> sub = doctors.subList(0,Math.min(doctors.size(), 3));
        List<Article> articles = articleService.listArticlesByDisease(disease);
        model.addAttribute("disease",disease);
        model.addAttribute("doctors", sub);
        model.addAttribute("doctorsNum", doctors.size());
        model.addAttribute("articles", articles);
        return "user/disease-profile";
    }

}
