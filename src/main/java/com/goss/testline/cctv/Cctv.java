package com.goss.testline.cctv;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "cctv")
public class Cctv {

    private static final String IMG_URL_IS_NULL = "https://tw.live/assets/security-camera.svg";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cctv_id")
    private Long cctvId;

    @Nationalized
    @Column(name = "label")
    private String label;

    @Nationalized
    @Column(name = "uri")
    private String uri;

    @Nationalized
    @Column(name = "image_url")
    private String imageUrl;

    @Nationalized
    @Column(name = "parent_label")
    private String parentLabel;

    @Nationalized
    @Column(name = "parent_uri")
    private String parentUri;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Transient
    private List<Cctv> cctvList;

    public String getImageUrl() {
        return imageUrl == null ? IMG_URL_IS_NULL : imageUrl;
    }

    public boolean hasNext() {
        return this.cctvList != null && !this.cctvList.isEmpty();
    }

}

