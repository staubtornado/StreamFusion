package de.streamfusion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VideoController {
    @GetMapping("/video")
    public String video(@RequestParam(name="id") long id, Model model) {
        model.addAttribute("id", id);
        return "video";
    }
}
