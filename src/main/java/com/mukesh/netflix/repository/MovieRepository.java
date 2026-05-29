package com.mukesh.netflix.repository;


import com.mukesh.netflix.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // ஒரு குறிப்பிட்ட பிரிவில் (Action, Comedy) உள்ள படங்களை எடுக்க
    List<Movie> findByCategoryId(Long categoryId);

    // நெட்ஃபிக்ஸ் பெரிய பேனரில் காட்டுவதற்காக Featured படங்களை மட்டும் எடுக்க
    List<Movie> findByFeaturedTrue();

    // படத்தின் தலைப்பை வைத்து தேட (Search Bar லாஜிக்கிற்கு)
    List<Movie> findByTitleContainingIgnoreCase(String title);
}
