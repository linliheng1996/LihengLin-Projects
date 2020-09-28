package com.trojanmd.controller;

import com.trojanmd.domain.BodyPart;
import com.trojanmd.domain.Disease;
import com.trojanmd.domain.Doctor;
import com.trojanmd.domain.Symptom;
import com.trojanmd.service.BodyPartService;
import com.trojanmd.service.DiseaseService;
import com.trojanmd.service.DoctorService;
import com.trojanmd.service.SymptomService;
import com.trojanmd.vo.SymptomQuery;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
public class SymptomController {

    @Autowired
    private SymptomService symptomService;

    @Autowired
    private BodyPartService bodyPartService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DiseaseService diseaseService;

//    @GetMapping("/test-input")
//    public String testInput(){
//        return "/admin/test-input";
//    }

//    @PostMapping("/test")
//    public String test(@RequestParam("test") String content,
//                       @RequestParam("md-content-html-code") String html,
//                       Model model){
//        System.out.println("content: " + content);
//        System.out.println("html: " + html);
//
//        model.addAttribute("content", content);
//        model.addAttribute("html", html);
//
//        return "/admin/test";
//    }


    @GetMapping("/admin/symptoms")
    public String listSymptoms(@PageableDefault(size = 10, sort = {"name"}) Pageable pageable,
                           Model model){
//        addToModel(model);
        model.addAttribute("page", symptomService.listSymptoms(pageable));
        return "admin/symptoms";
    }

    @GetMapping("/admin/symptom-input")
    public String inputSymptom(Model model){
        model.addAttribute("bodyParts", bodyPartService.findAll());
        model.addAttribute("symptom", new Symptom());
        model.addAttribute("diseases", diseaseService.listDiseases());
        return "admin/symptom-input";
    }

    @GetMapping("/admin/symptoms/{id}/input")
    public String editSymptom(@PathVariable Long id, Model model){
        Symptom symptom = symptomService.get(id);
        model.addAttribute("bodyParts", bodyPartService.findAll());
        model.addAttribute("symptom", symptom);
        model.addAttribute("diseases", diseaseService.listDiseases());
        return "admin/symptom-input";
    }

    @PostMapping("/admin/symptom-add")
    public String addSymptom(Symptom symptom,
                             @RequestParam String diseaseIds,
                             @RequestParam("md-content1") String content1,
                             @RequestParam("md-content1-html-code") String html1,
                             @RequestParam("md-content2") String content2,
                             @RequestParam("md-content2-html-code") String html2,
                             @RequestParam("md-content3") String content3,
                             @RequestParam("md-content3-html-code") String html3,
                             RedirectAttributes redirectAttributes){
//        System.out.println("body part name: "+symptom.getBodyPartName());
//        BodyPart bp = bodyPartService.getByName(symptom.getBodyPartName());
//        System.out.println("body part: "+bp.getName());
        symptom.setBodyPart(bodyPartService.getByName(symptom.getBodyPartName()));
        symptom.setDiseases(diseaseService.listDiseases(diseaseIds));
        symptom.setDescription(content1);
        symptom.setCause(content2);
        symptom.setTreatment(content3);
        symptom.setDescriptionHtml(html1);
        symptom.setCauseHtml(html2);
        symptom.setTreatmentHtml(html3);
        //First determine save or update operation
        Symptom s;
        //save
        if(symptom.getId() == null){
            //Determine if the symptom already exists
            if(symptomService.getByName(symptom.getName()) != null){
                redirectAttributes.addFlashAttribute("errorMessage","Symptom already exists!");
                return "redirect:/admin/symptom-input";
            }

            //Determine if the operation is successful
            s = symptomService.save(symptom);
            if(s == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Fail to add new symptom!");
                return "redirect:/admin/symptom-input";
            }
            else{
                redirectAttributes.addFlashAttribute("successMessage", "Symptom added successfully!");

            }
        }
        else{
            s = symptomService.update(symptom.getId(), symptom);
            if(s == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Fail to edit symptom!");
                return "redirect:/admin/symptom-input";
            }
            else{
                redirectAttributes.addFlashAttribute("successMessage", "Symptom updated successfully!");
            }
        }

//        System.out.println("s: " + s.getBodyPart().getName());
//        System.out.println("disease: " + s.getDiseases().get(0).getName());
        return "redirect:/admin/symptoms";
    }


    @GetMapping("/admin/symptoms/{id}/delete")
    public String deleteSymptom(@PathVariable Long id, RedirectAttributes redirectAttributes){
        symptomService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Delete successfully!");
        return "redirect:/admin/symptoms";
    }

    @GetMapping("/user/symptoms")
    public String searchForSymptoms(Model model){

        List<BodyPart> bodyParts = bodyPartService.findAll();
        model.addAttribute("bodyParts", bodyParts);
        return "user/search-for-symptom";
    }

    @ResponseBody
    @PostMapping("/user/symptoms/bodyPart")
    public List<Symptom> listSymptomsByBodyPart(@RequestBody JSONObject jsonObject) {
//        System.out.println(jsonObject.toString());
        String bodyPart = jsonObject.getString("bodyPart");
//        System.out.println("body part: " + bodyPart);

        return symptomService.listSymptomsByBodyPart(bodyPartService.getByName(bodyPart));
    }

    @GetMapping("/user/symptoms/{part}")
    public String listSymptomByBodyPart(@PathVariable String part,
                                        Model model){

//        SymptomQuery symptomQuery = new SymptomQuery();
//        symptomQuery.setPublished(true);
//        symptomQuery.setBodyPart(part);

        List<BodyPart> bodyParts = bodyPartService.findAll();
        List<Symptom> symptoms = symptomService.listSymptomsByBodyPart(bodyPartService.getByName(part));
        model.addAttribute("bodyParts", bodyParts);
        model.addAttribute("symptoms", symptoms);
        model.addAttribute("selected", part);
        return "user/search-for-symptom";
    }

    @GetMapping("/user/symptom/{id}")
    public String displaySymptom(@PathVariable Long id, Model model){
        Symptom symptom =  symptomService.get(id);
        List<Disease> diseases = symptom.getDiseases();
        model.addAttribute("symptom", symptom);
        model.addAttribute("diseases",diseases);
        return "user/symptom-profile";
    }

}
