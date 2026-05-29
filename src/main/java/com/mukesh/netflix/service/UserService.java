package com.mukesh.netflix.service;


import com.mukesh.netflix.model.Profile;
import com.mukesh.netflix.model.User;
import com.mukesh.netflix.repository.ProfileRepository;
import com.mukesh.netflix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    // புதிய பயனர் கணக்கு உருவாக்குதல் (Registration)
    public User registerUser(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered!");
        }
        User user = User.builder()
                .email(email)
                .password(password) // குறிப்பு: பின்னாளில் Spring Security சேர்க்கும்போது இதை Encode செய்ய வேண்டும்
                .enabled(true)
                .build();
        return userRepository.save(user);
    }

    // மின்னஞ்சல் மூலம் பயனரைக் கண்டறிதல் (Login)
    public Optional<User> loginUser(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> user.isEnabled() && user.getPassword().equals(password));
    }

    // ஒரு பயனரின் கீழ் புதிய நெட்ஃபிக்ஸ் ப்ரொஃபைல் (Profile) உருவாக்குதல்
    public Profile createProfile(Long userId, String name, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // நெட்ஃபிக்ஸ் லாஜிக் படி ஒரு கணக்கிற்கு அதிகபட்சம் 5 ப்ரொஃபைல்கள் தான் இருக்க வேண்டும்
        if (user.getProfiles() != null && user.getProfiles().size() >= 5) {
            throw new RuntimeException("Maximum 5 profiles allowed per account!");
        }

        Profile profile = Profile.builder()
                .name(name)
                .avatarUrl(avatarUrl == null ? "default_avatar.png" : avatarUrl)
                .user(user)
                .build();
        return profileRepository.save(profile);
    }

    // ஒரு பயனரின் கீழ் உள்ள அனைத்து ப்ரொஃபைல்களையும் எடுத்தல் (Who's Watching Screen)
    @Transactional(readOnly = true)
    public List<Profile> getProfilesByUser(Long userId) {
        return profileRepository.findByUserId(userId);
    }
}
