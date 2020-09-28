package com.trojanmd.controller;

import com.trojanmd.domain.BodyPart;
import com.trojanmd.service.BodyPartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BodyPartController {

    @Autowired
    private BodyPartService bodyPartService;

    @GetMapping("/admin/body-parts")
    public String listBodyParts(@PageableDefault(size = 10, sort = {"name"}) Pageable pageable,
                                Model model){
        model.addAttribute("page",bodyPartService.listBodyParts(pageable));
        return "admin/bodyParts";
    }

    @GetMapping("/admin/body-part-input")
    public String inputBodyPart(Model model){
        model.addAttribute("bodyPart", new BodyPart());
        return "admin/bodyPart-input";
    }

    @GetMapping("/admin/body-parts/{id}/input")
    public String editBodyPart(@PathVariable Long id, Model model){
        BodyPart bodyPart = bodyPartService.get(id);
        model.addAttribute("bodyPart", bodyPart);
        return "admin/bodyPart-input";
    }

    @PostMapping("/admin/body-part-add")
    public String addBodyPart(BodyPart bodyPart,
                              RedirectAttributes redirectAttributes){
        //First determine save or update operation
        BodyPart b;
        //save
        if(bodyPart.getId() == null){
            //Determine if the symptom already exists
            if(bodyPartService.getByName(bodyPart.getName()) != null){
                redirectAttributes.addFlashAttribute("errorMessage","Body part already exists!");
                return "redirect:/admin/body-part-input";
            }

            //Determine if the operation is successful
            b = bodyPartService.save(bodyPart);
            if(b == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Fail to add new body part!");
                return "redirect:/admin/body-part-input";
            }
            else{//update
                redirectAttributes.addFlashAttribute("successMessage", "Body part added successfully!");

            }
        }
        else{
            b = bodyPartService.update(bodyPart.getId(), bodyPart);
            if(b == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Fail to edit body part!");
                return "redirect:/admin/body-part-input";
            }
            else{
                redirectAttributes.addFlashAttribute("successMessage", "Body part updated successfully!");
            }
        }

        return "redirect:/admin/body-parts";
    }

    @GetMapping("/admin/body-parts/{id}/delete")
    public String deleteBodyPart(@PathVariable Long id, RedirectAttributes redirectAttributes){
        String msg = bodyPartService.delete(id);
        if(msg == "success") redirectAttributes.addFlashAttribute("successMessage","Delete successfully!");
        else redirectAttributes.addFlashAttribute("errorMessage","Body part already assigned to some symptoms, fail to delete!");
        return "redirect:/admin/body-parts";
    }


}
