package com.example.pr13.service;

import com.example.pr13.entity.Book;
import com.example.pr13.entity.Category;
import com.example.pr13.repository.BookRepository;
import com.example.pr13.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Page<Book> getAllBooks(String title, String author, Pageable pageable) {
        return bookRepository.findByFilter(title, author, pageable);
    }

    @Override
    public void saveBook(Book book) {

        List<Book> existingBooks = bookRepository.findByTitleAndAuthor(book.getTitle(), book.getAuthor());
        if (!existingBooks.isEmpty()) {
            throw new RuntimeException("Книга з такою назвою та автором вже існує!");
        }
        bookRepository.save(book);
    }

    @Override
    public void saveCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public List<Book> getBooksByCategory(String categoryName) {
        return bookRepository.findByCategoryName(categoryName);
    }
}