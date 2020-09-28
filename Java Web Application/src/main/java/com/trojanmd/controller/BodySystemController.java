package com.trojanmd.controller;

import com.trojanmd.domain.BodySystem;
import com.trojanmd.service.BodySystemService;
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
public class BodySystemController {

    @Autowired
    private BodySystemService bodySystemService;

    @GetMapping("/admin/body-systems")
    public String listBodySystems(@PageableDefault(size = 10, sort = {"name"}) Pageable pageable,
                                Model model){
        model.addAttribute("page",bodySystemService.listBodySystems(pageable));
        return "admin/bodySystems";
    }

    @GetMapping("/admin/body-system-input")
    public String inputBodySystem(Model model){
        model.addAttribute("bodySystem", new BodySystem());
        return "admin/bodySystem-input";
    }

    @GetMapping("/admin/body-systems/{id}/input")
    public String editBodySystem(@PathVariable Long id, Model model){
        BodySystem BodySystem = bodySystemService.get(id);
        model.addAttribute("bodySystem", BodySystem);
        return "admin/bodySystem-input";
    }

    @PostMapping("/admin/body-system-add")
    public String addBodySystem(BodySystem bodySystem,
                              RedirectAttributes redirectAttributes){
        //First determine save or update operation
        BodySystem b;
        //save
        if(bodySystem.getId() == null){
            //Determine if the symptom already exists
            if(bodySystemService.getByName(bodySystem.getName()) != null){
                redirectAttributes.addFlashAttribute("errorMessage","Body system already exists!");
                return "redirect:/admin/body-system-input";
            }

            //Determine if the operation is successful
            b = bodySystemService.save(bodySystem);
            if(b == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Fail to add new body system!");
                return "redirect:/admin/body-system-input";
            }
            else{//update
                redirectAttributes.addFlashAttribute("successMessage", "Body system added successfully!");

            }
        }
        else{
            b = bodySystemService.update(bodySystem.getId(), bodySystem);
            if(b == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Fail to edit body system!");
                return "redirect:/admin/body-system-input";
            }
            else{
                redirectAttributes.addFlashAttribute("successMessage", "Body system updated successfully!");
            }
        }

        return "redirect:/admin/body-systems";
    }

    @GetMapping("/admin/body-systems/{id}/delete")
    public String deleteBodySystem(@PathVariable Long id, RedirectAttributes redirectAttributes){
        String msg = bodySystemService.delete(id);
        if(msg == "success") redirectAttributes.addFlashAttribute("successMessage","Delete successfully!");
        else redirectAttributes.addFlashAttribute("errorMessage","Body system already assigned to some diseases, fail to delete!");
        return "redirect:/admin/body-systems";
    }


}
