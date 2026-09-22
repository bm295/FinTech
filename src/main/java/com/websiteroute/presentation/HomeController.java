package com.websiteroute.presentation;

import com.websiteroute.application.GetFlightAltitudeDashboardQuery;
import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final GetFlightAltitudeDashboardQuery query;
    public HomeController(GetFlightAltitudeDashboardQuery query) { this.query = query; }
    @GetMapping({"/", "/home"}) public String index(Model model) { model.addAttribute("dashboard", query.execute()); return "index"; }
    @GetMapping("/privacy") public String privacy() { return "privacy"; }
}
