package com.mukesh.netflix.controller;

import com.mukesh.netflix.model.Category;
import com.mukesh.netflix.model.Movie;
import com.mukesh.netflix.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MovieService movieService;

    // 1. அட்மின் டேஷ்போர்டு / படங்களின் பட்டியலைக் காட்டும் பக்கம்
    @GetMapping("/movies")
    public String listMovies(Model model) {
        // Thymeleaf-ல் லூப் செய்து காட்ட அனைத்து கேட்டகிரி மற்றும் படங்களை எடுக்கிறோம்
        List<Category> categories = movieService.getAllCategoriesWithMovies();
        model.addAttribute("categories", categories);
        return "admin/movie-list"; // templates/admin/movie-list.html பக்கத்திற்கு செல்லும்
    }

    // 2. புதிய படம் சேர்க்கும் ஃபார்ம் பக்கத்தைக் காட்டுதல்
    @GetMapping("/movies/add")
    public String showAddForm(Model model) {
        List<Category> categories = movieService.getAllCategoriesWithMovies();
        model.addAttribute("categories", categories);
        model.addAttribute("movie", new Movie()); // ஃபார்ம் பைண்டிங்கிற்காக காலி ஆப்ஜெக்ட்
        return "admin/movie-add"; // templates/admin/movie-add.html
    }

    // 3. ஃபார்ம் டேட்டா மற்றும் Cloudinary ஃபைல்களைப் பெற்றுச் சேமித்தல்
    @PostMapping("/movies/add")
    public String saveMovie(@RequestParam("title") String title,
                            @RequestParam("description") String description,
                            @RequestParam("imageFile") MultipartFile imageFile,
                            @RequestParam("bannerFile") MultipartFile bannerFile,
                            @RequestParam("videoFile") MultipartFile videoFile,
                            @RequestParam(value = "featured", defaultValue = "false") boolean featured,
                            @RequestParam("categoryId") Long categoryId,
                            Model model) {
        try {
            // நம் MovieService-ல் உள்ள புதுப்பிக்கப்பட்ட மெத்தடை அழைக்கிறோம்
            movieService.addMovie(title, description, imageFile, bannerFile, videoFile, featured, categoryId);
            return "redirect:/admin/movies?success=Movie+Added+Successfully";

        } catch (IOException e) {
            model.addAttribute("error", "Cloudinary Upload Failed: " + e.getMessage());
            return "admin/movie-add";
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "admin/movie-add";
        }
    }

    // 4. ஒரு படத்தை டெலீட் செய்தல்
    @PostMapping("/movies/delete/{id}")
    public String deleteMovie(@PathVariable("id") Long id) {
        try {
            movieService.deleteMovie(id);
            return "redirect:/admin/movies?deleted=Success";
        } catch (Exception e) {
            return "redirect:/admin/movies?error=" + e.getMessage();
        }
    }
}
