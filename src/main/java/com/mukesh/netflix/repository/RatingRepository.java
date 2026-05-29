package com.mukesh.netflix.repository;


import com.mukesh.netflix.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    // ஒரு பயனர் ஒரு குறிப்பிட்ட படத்திற்கு கொடுத்த ரேட்டிங்கை எடுக்க
    Optional<Rating> findByUserIdAndMovieId(Long userId, Long movieId);

    // ஒரு குறிப்பிட்ட படத்திற்கு கிடைத்துள்ள மொத்த லைக்குகளின் எண்ணிக்கை (value = 2 என்பது Thumbs Up)
    long countByMovieIdAndValue(Long movieId, int value);
}
