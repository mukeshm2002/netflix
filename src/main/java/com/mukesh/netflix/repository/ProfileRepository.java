package com.mukesh.netflix.repository;


import com.mukesh.netflix.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    // ஒரு குறிப்பிட்ட யூசரின் அனைத்து ப்ரொஃபைல்களையும் எடுக்க
    List<Profile> findByUserId(Long userId);
}
