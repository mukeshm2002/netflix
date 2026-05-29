package com.mukesh.netflix.repository;

import com.mukesh.netflix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // மின்னஞ்சல் மூலம் பயனரைக் கண்டறிய (Login மற்றும் Register லாஜிக்கிற்கு)
    Optional<User> findByEmail(String email);

    // மின்னஞ்சல் ஏற்கனவே உள்ளதா எனச் சரிபார்க்க
    boolean existsByEmail(String email);
}
