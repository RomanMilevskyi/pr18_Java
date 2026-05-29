package com.example.pr13.service;

import com.example.pr13.Pr13Application;
import com.example.pr13.entity.Book;
import com.example.pr13.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

// 1. Окремий тестовий клас, анотований @SpringBootTest
@SpringBootTest(classes = Pr13Application.class)
// 5. Використання окремого тестового профілю для ізольованості даних
@ActiveProfiles("test")
public class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    // 5. Очищення бази даних перед кожним тестом для забезпечення повної ізоляції
    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    // 3. Тест для методу створення нової книги
    @Test
    void testCreateBookSuccess() {
        Book book = new Book();
        book.setTitle("S.T.A.L.K.E.R. 2: Heart of Chornobyl");
        book.setAuthor("GSC Game World");

        assertDoesNotThrow(() -> bookService.saveBook(book));

        boolean exists = bookRepository.findAll().stream()
                .anyMatch(b -> b.getTitle().equals("S.T.A.L.K.E.R. 2: Heart of Chornobyl"));
        assertTrue(exists, "Книга повинна успішно зберегтися в ізольованій БД у Docker");
    }

    // 3. Тест для перевірки валідації (дублікати не створюються двічі)
    @Test
    void testCreateDuplicateBookThrowsException() {
        Book book1 = new Book();
        book1.setTitle("Dune");
        book1.setAuthor("Frank Herbert");
        bookService.saveBook(book1);

        Book book2 = new Book();
        book2.setTitle("Dune");
        book2.setAuthor("Frank Herbert");

        // Перевіряємо, що сервіс викидає помилку RuntimeException при спробі дублювання
        assertThrows(RuntimeException.class, () -> {
            bookService.saveBook(book2);
        }, "Сервіс повинен заблокувати створення дубліката книги");
    }

    // 4. Перевірка транзакційності (при помилці збереження дані ролбекаються і не потрапляють в базу)
    @Test
    void testTransactionRollbackOnValidationFailure() {
        Book originalBook = new Book();
        originalBook.setTitle("Unique Book");
        originalBook.setAuthor("Author");
        bookService.saveBook(originalBook);

        Book duplicateBook = new Book();
        duplicateBook.setTitle("Unique Book"); // Така сама назва викличе помилку валідації
        duplicateBook.setAuthor("Author");

        try {
            bookService.saveBook(duplicateBook);
        } catch (RuntimeException e) {
            // Помилку валідації перехоплено, транзакція скасовується
        }

        // Перевіряємо, що в базі залишилася лише одна книга (дублікат не з'явився завдяки Rollback)
        long count = bookRepository.count();
        assertEquals(1, count, "База даних не повинна містити даних з помилкової транзакції");
    }
}