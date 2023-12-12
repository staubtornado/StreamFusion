package de.streamfusion.controllers;


import de.streamfusion.models.User;
import de.streamfusion.models.Video;
import de.streamfusion.services.AuthenticationService;
import de.streamfusion.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.NoSuchElementException;

@Controller
public class SearchController {
    private final SearchService searchService;
    private final AuthenticationService authenticationService;

    @Autowired
    public SearchController(SearchService searchService, AuthenticationService authenticationService) {
        this.searchService = searchService;
        this.authenticationService = authenticationService;
    }

    @GetMapping(value = "/search")
    public ModelAndView search(
            @RequestParam(value = "query") String searchQuery,
            @RequestHeader(name = "Cookie", required = false) String cookies
    ) {
        ModelAndView modelAndView = new ModelAndView("searchResults");
        ArrayList<Video> videos = new ArrayList<>();
        User user = null;

        try {
            user = this.authenticationService.getUserFromToken(
                    AuthenticationService.extractTokenFromCookie(cookies)
            );
        } catch (IllegalArgumentException ignored) {}

        try {
            videos = this.searchService.searchVideo(searchQuery);
        } catch (NoSuchElementException ignored){}

        modelAndView.addObject("search", searchQuery);
        modelAndView.addObject("user", user);
        modelAndView.addObject("resultingVideos", videos);
        return modelAndView;
    }
}
