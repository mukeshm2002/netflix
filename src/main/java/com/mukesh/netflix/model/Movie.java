package com.mukesh.netflix.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT") // பெரிய விளக்கங்களை பிழையின்றிச் சேமிக்க
    private String description;

    @Column(name = "thumbnail_url", nullable = false)
    private String thumbnailUrl; // ஹோம் ஸ்கிரீன் வரிசைகளில் காட்டும் சிறிய போஸ்டர் படம்

    @Column(name = "banner_url")
    private String bannerUrl;    // முகப்புப் பக்கத்தின் மேல் பகுதியில் காட்டும் பெரிய விளம்பரப் படம்

    @Column(name = "video_url", nullable = false)
    private String videoUrl;     // வீடியோ ஸ்ட்ரீம் ஆவதற்கான லிங்க்

    @Builder.Default
    private boolean featured = false; // இது நெட்ஃபிக்ஸ் பரிந்துரைக்கும் முதன்மைப் படமா (Banner Movie) என அறிய

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
