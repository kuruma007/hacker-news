package com.example.hackerNews.repository;

import com.example.hackerNews.entity.NewsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<NewsEntity, Long> {
    @Query("SELECT DISTINCT news FROM NewsEntity news WHERE news.title LIKE %?1%")
    Page<NewsEntity> findByKeyword(@Param("keyword") String keyword, Pageable pagable);
}
