package com.mukesh.netflix.service;

import com.mukesh.netflix.model.Category;
import com.mukesh.netflix.model.Movie;
import com.mukesh.netflix.repository.CategoryRepository;
import com.mukesh.netflix.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieService {

    private final MovieRepository movieRepository;
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService; // Cloudinary சர்வீஸ் உள்ளிணைப்பு

    // ==========================================
    // 1. CATEGORY OPERATIONS (பிரிவுகள்)
    // ==========================================

    // புதிய திரைப்படப் பிரிவை உருவாக்குதல்
    @Transactional
    public Category createCategory(String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.save(Category.builder().name(name).build()));
    }

    // அனைத்து திரைப்படப் பிரிவுகளையும் எடுத்தல் (முகப்புப் பக்க வரிசைகளுக்காக)
    public List<Category> getAllCategoriesWithMovies() {
        return categoryRepository.findAll();
    }

    // ==========================================
    // 2. MOVIE ADD OPERATIONS WITH CLOUDINARY (படம் சேர்த்தல்)
    // ==========================================

    // புதிய திரைப்படத்தை Cloudinary ஃபைல் அப்லோடுடன் சேர்த்தல் (Admin Logic)
    @Transactional
    public Movie addMovie(String title, String description, MultipartFile imageFile,
                          MultipartFile bannerFile, MultipartFile videoFile, boolean featured, Long categoryId) throws IOException {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));

        // ஃபைல்களை Cloudinary-க்கு அப்லோட் செய்து பாதுகாப்பான URL-களைப் பெறுகிறோம்
        String thumbnailUrl = (imageFile != null && !imageFile.isEmpty())
                ? cloudinaryService.uploadImage(imageFile, "movies/thumbnails") : null;

        String bannerUrl = (bannerFile != null && !bannerFile.isEmpty())
                ? cloudinaryService.uploadImage(bannerFile, "movies/banners") : thumbnailUrl; // பேனர் இல்லை என்றால் தம்ப்நெயில் பயன்படுத்தப்படும்

        String videoUrl = (videoFile != null && !videoFile.isEmpty())
                ? cloudinaryService.uploadVideo(videoFile, "movies/videos") : null;

        Movie movie = Movie.builder()
                .title(title)
                .description(description)
                .thumbnailUrl(thumbnailUrl)
                .bannerUrl(bannerUrl)
                .videoUrl(videoUrl)
                .featured(featured)
                .category(category)
                .build();

        return movieRepository.save(movie);
    }

    // ஒரு குறிப்பிட்ட படத்தை நேரடியாக மாடல் ஆப்ஜெக்ட்டாகச் சேமிக்க (Fallback / API Logic)
    @Transactional
    public Movie saveMovie(Movie movie) {
        if (movie.getCategory() == null || movie.getCategory().getId() == null) {
            throw new IllegalArgumentException("The movie must belong to a valid category!");
        }

        Category category = categoryRepository.findById(movie.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + movie.getCategory().getId()));

        movie.setCategory(category);
        return movieRepository.save(movie);
    }

    // பல படங்களை ஒரே நேரத்துல ஆட் பண்ண (Bulk Add Movies for Testing)
    @Transactional
    public List<Movie> saveAllMovies(List<Movie> movies) {
        for (Movie movie : movies) {
            if (movie.getCategory() == null || movie.getCategory().getId() == null) {
                throw new IllegalArgumentException("All movies in the list must have a valid category ID!");
            }
            Category category = categoryRepository.findById(movie.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found for movie: " + movie.getTitle()));
            movie.setCategory(category);
        }
        return movieRepository.saveAll(movies);
    }

    // ==========================================
    // 3. MOVIE UPDATE & DELETE OPERATIONS (மாற்றுதல் மற்றும் நீக்குதல்)
    // ==========================================

    // இருக்கும் ஒரு படத்தின் விவரங்களை மாற்றுதல் (விருப்பப்பட்டால் புதிய ஃபைல்களுடன்)
    @Transactional
    public Movie updateMovie(Long movieId, Movie updatedMovieData, MultipartFile newImageFile,
                             MultipartFile newBannerFile, MultipartFile newVideoFile) throws IOException {

        Movie existingMovie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + movieId));

        // உரை விவரங்களை (Text Details) மாற்றுதல்
        existingMovie.setTitle(updatedMovieData.getTitle());
        existingMovie.setDescription(updatedMovieData.getDescription());
        existingMovie.setFeatured(updatedMovieData.isFeatured());

        // புதிய ஃபைல்கள் அனுப்பப்பட்டிருந்தால் மட்டும் Cloudinary-ல் அப்லோட் செய்து URL மாறும், இல்லையெனில் பழைய URL அப்படியே இருக்கும்
        if (newImageFile != null && !newImageFile.isEmpty()) {
            existingMovie.setThumbnailUrl(cloudinaryService.uploadImage(newImageFile, "movies/thumbnails"));
        }
        if (newBannerFile != null && !newBannerFile.isEmpty()) {
            existingMovie.setBannerUrl(cloudinaryService.uploadImage(newBannerFile, "movies/banners"));
        }
        if (newVideoFile != null && !newVideoFile.isEmpty()) {
            existingMovie.setVideoUrl(cloudinaryService.uploadVideo(newVideoFile, "movies/videos"));
        }

        // திரைப்படப் பிரிவு (Category) மாற்றப்பட்டிருந்தால்
        if (updatedMovieData.getCategory() != null && updatedMovieData.getCategory().getId() != null) {
            Category newCategory = categoryRepository.findById(updatedMovieData.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("New Category not found"));
            existingMovie.setCategory(newCategory);
        }

        return movieRepository.save(existingMovie);
    }

    // ஒரு படத்தை டேட்டாபேஸிலிருந்து நீக்குதல்
    @Transactional
    public void deleteMovie(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new RuntimeException("Movie not found with ID: " + movieId);
        }
        movieRepository.deleteById(movieId);
    }

    // ==========================================
    // 4. MOVIE FETCH OPERATIONS (படங்களை எடுத்தல்)
    // ==========================================

    // நெட்ஃபிக்ஸ் பெரிய பேனரில் காட்ட முதன்மைப் படங்களை (Featured Movies) எடுத்தல்
    public List<Movie> getFeaturedMovies() {
        return movieRepository.findByFeaturedTrue();
    }

    // குறிப்பிட்ட ஒரு திரைப்படத்தின் முழு விவரங்களை எடுத்தல் (Watch Player Screen)
    public Movie getMovieById(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + movieId));
    }

    // தேடுதல் வசதி (Search Functionality)
    public List<Movie> searchMovies(String query) {
        return movieRepository.findByTitleContainingIgnoreCase(query);
    }
}