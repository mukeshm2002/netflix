package com.mukesh.netflix.repository;


import com.mukesh.netflix.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    // ஒரு பயனரின் வாட்ச்லிஸ்ட் படங்களை மட்டும் எடுக்க
    List<Watchlist> findByUserId(Long userId);

    // ஒரு பயனர் குறிப்பிட்ட ஒரு படத்தை ஏற்கனவே வாட்ச்லிஸ்ட்டில் சேர்த்துள்ளாரா என அறிய
    Optional<Watchlist> findByUserIdAndMovieId(Long userId, Long movieId);

    // வாட்ச்லிஸ்ட்டில் இருந்து ஒரு படத்தை நீக்க
    void deleteByUserIdAndMovieId(Long userId, Long movieId);
}
