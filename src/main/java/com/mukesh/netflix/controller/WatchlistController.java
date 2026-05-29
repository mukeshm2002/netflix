package com.mukesh.netflix.controller;

import com.mukesh.netflix.model.Watchlist;
import com.mukesh.netflix.service.UserInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final UserInteractionService userInteractionService;

    // 1. பயனரின் "My List" பக்கத்தைக் காட்டுதல்
    @GetMapping
    public String showWatchlist(@SessionAttribute(name = "userId", required = false) Long userId, Model model) {
        if (userId == null) userId = 1L; // Fallback for testing

        List<Watchlist> watchlist = userInteractionService.getUserWatchlist(userId);
        model.addAttribute("watchlist", watchlist);
        return "watchlist"; // templates/watchlist.html
    }

    // 2. ஒரு படத்தை வாட்ச்லிஸ்ட்டில் சேர்த்தல்
    @PostMapping("/add")
    public String addToWatchlist(@SessionAttribute(name = "userId", required = false) Long userId,
                                 @RequestParam("movieId") Long movieId) {
        if (userId == null) userId = 1L;

        userInteractionService.addToWatchlist(userId, movieId);
        return "redirect:/home?addedToWatchlist=true";
    }

    // 3. வாட்ச்லிஸ்ட்டில் இருந்து ஒரு படத்தை நீக்குதல்
    @PostMapping("/remove")
    public String removeFromWatchlist(@SessionAttribute(name = "userId", required = false) Long userId,
                                      @RequestParam("movieId") Long movieId) {
        if (userId == null) userId = 1L;

        userInteractionService.removeFromWatchlist(userId, movieId);
        return "redirect:/watchlist?removed=true";
    }
}
