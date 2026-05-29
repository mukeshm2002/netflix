package com.mukesh.netflix.service;


import com.mukesh.netflix.model.Movie;
import com.mukesh.netflix.model.Rating;
import com.mukesh.netflix.model.User;
import com.mukesh.netflix.model.Watchlist;
import com.mukesh.netflix.repository.MovieRepository;
import com.mukesh.netflix.repository.RatingRepository;
import com.mukesh.netflix.repository.UserRepository;
import com.mukesh.netflix.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserInteractionService {

    private final WatchlistRepository watchlistRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    // ஒரு படத்தை "My List"-ல் சேர்த்தல் (Watchlist)
    public Watchlist addToWatchlist(Long userId, Long movieId) {
        Optional<Watchlist> existing = watchlistRepository.findByUserIdAndMovieId(userId, movieId);
        if (existing.isPresent()) {
            return existing.get(); // ஏற்கனவே இருந்தால் அதையே திருப்பி அனுப்பும்
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie not found"));

        Watchlist watchlist = Watchlist.builder()
                .user(user)
                .movie(movie)
                .build();

        return watchlistRepository.save(watchlist);
    }

    // ஒரு படத்தை "My List"-ல் இருந்து நீக்குதல்
    public void removeFromWatchlist(Long userId, Long movieId) {
        watchlistRepository.deleteByUserIdAndMovieId(userId, movieId);
    }

    // பயனரின் "My List" பக்கத்தில் உள்ள அனைத்து படங்களையும் எடுத்தல்
    @Transactional(readOnly = true)
    public List<Watchlist> getUserWatchlist(Long userId) {
        return watchlistRepository.findByUserId(userId);
    }

    // ஒரு படத்திற்கு Like (2) அல்லது Dislike (1) ரேட்டிங் கொடுத்தல்
    public Rating rateMovie(Long userId, Long movieId, int ratingValue) {
        if (ratingValue != 1 && ratingValue != 2) {
            throw new IllegalArgumentException("Invalid rating value! Use 1 for Dislike, 2 for Like.");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie not found"));

        Rating rating = ratingRepository.findByUserIdAndMovieId(userId, movieId)
                .orElse(Rating.builder().user(user).movie(movie).build());

        rating.setValue(ratingValue); // புதிய மதிப்பை அப்டேட் செய்யும்
        return ratingRepository.save(rating);
    }

    // ஒரு குறிப்பிட்ட படத்திற்கு கிடைத்துள்ள மொத்த லைக்குகளின் எண்ணிக்கை
    @Transactional(readOnly = true)
    public long getMovieLikesCount(Long movieId) {
        return ratingRepository.countByMovieIdAndValue(movieId, 2); // 2 = Thumbs Up
    }
}
