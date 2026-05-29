package com.example.pr13.repository;

import com.example.pr13.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    List<Book> findByCategoryName(String categoryName);


    List<Book> findByTitleAndAuthor(String title, String author);

    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', CAST(:title as String), '%'))) AND " +
           "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', CAST(:author as String), '%')))")
    Page<Book> findByFilter(@Param("title") String title, 
                            @Param("author") String author, 
                            Pageable pageable);
}