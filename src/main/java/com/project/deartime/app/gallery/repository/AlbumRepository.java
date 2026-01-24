package com.project.deartime.app.gallery.repository;

import com.project.deartime.app.domain.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Modifying
    @Query("update Album a set a.coverPhoto = null where a.coverPhoto.id = :photoId")
    void clearCoverPhoto(@Param("photoId") Long photoId);

    Optional<Album> findFirstByTitleAndUserIdOrderByCreatedAtAsc(String title, Long userId);

    List<Album> findAllByUserId(Long userId);

    @Query("SELECT a FROM Album a WHERE a.user.id = :userId " +
            "ORDER BY CASE WHEN a.title = '즐겨찾기' THEN 0 ELSE 1 END ASC, " +
            "a.updatedAt DESC, a.id DESC")
    List<Album> findAllByUserIdWithOrdering(@Param("userId") Long userId);
}
