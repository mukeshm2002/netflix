package com.mukesh.netflix.controller;

import com.mukesh.netflix.model.Category;
import com.mukesh.netflix.model.Movie;
import com.mukesh.netflix.service.MovieService;
import com.mukesh.netflix.service.UserInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MovieService movieService;
    private final UserInteractionService userInteractionService;

    // 1. நெட்ஃபிக்ஸ் முதன்மை முகப்புப் பக்கம் (Netflix Home Screen)
    @GetMapping("/home")
    public String index(@SessionAttribute(name = "userId", required = false) Long userId, Model model) {
        // குறிப்பு: தற்காலிகமாக லாகின் லாஜிக் இல்லை என்றால் டெஸ்டிங்கிற்காக பயனர் ID 1 என எடுத்துக் கொள்ளலாம்
        if (userId == null) {
            userId = 1L;
        }

        // முகப்புப் பக்கத்தின் மேல் பகுதியில் பெரியதாகக் காட்ட Featured படங்களை எடுக்கிறோம்
        List<Movie> featuredMovies = movieService.getFeaturedMovies();
        // ஒருவேளை Featured படம் எதுவும் இல்லை என்றால், முதல் படத்தை பேனராக வைக்க ஒரு பாதுகாப்பு லாஜிக் (Fallback)
        Movie heroBanner = !featuredMovies.isEmpty() ? featuredMovies.get(0) : null;

        // கீழ் வரிசைகளில் காட்ட அனைத்து பிரிவுகளையும் (அதனுள் இருக்கும் படங்களுடன்) எடுக்கிறோம்
        List<Category> categories = movieService.getAllCategoriesWithMovies();

        // Thymeleaf HTML பக்கத்திற்கு தரவுகளை அனுப்புதல்
        model.addAttribute("heroBanner", heroBanner);
        model.addAttribute("categories", categories);
        model.addAttribute("userId", userId);

        return "home"; // templates/home.html பக்கத்திற்குச் செல்லும்
    }

    // 2. வீடியோ ஸ்ட்ரீமிங் பிளேயர் பக்கம் (Watch Player Screen)
    @GetMapping("/watch/{id}")
    public String watchMovie(@PathVariable("id") Long id, Model model) {
        // குறிப்பிட்ட படத்தின் Cloudinary Video URL மற்றும் விவரங்களை எடுக்கிறோம்
        Movie movie = movieService.getMovieById(id);

        // அந்தப் படத்திற்கு கிடைத்துள்ள மொத்த லைக்குகளின் எண்ணிக்கை
        long likesCount = userInteractionService.getMovieLikesCount(id);

        model.addAttribute("movie", movie);
        model.addAttribute("likesCount", likesCount);

        return "player"; // templates/player.html (வீடியோ பிளேயர் பக்கம்)
    }

    // 3. திரைப்படங்களைத் தேடும் வசதி (Search Box Logic)
    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        List<Movie> searchResults = movieService.searchMovies(query);

        model.addAttribute("searchResults", searchResults);
        model.addAttribute("query", query);

        return "search-results"; // templates/search-results.html
    }
}
