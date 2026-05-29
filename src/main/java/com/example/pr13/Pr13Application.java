package com.example.pr13;

import com.example.pr13.entity.Book;
import com.example.pr13.entity.Category;
import com.example.pr13.entity.User;
import com.example.pr13.repository.UserRepository;
import com.example.pr13.service.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class Pr13Application {

    public static void main(String[] args) {
        SpringApplication.run(Pr13Application.class, args);
    }

    @Bean
    public CommandLineRunner demo(BookService bookService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Створення тестового адміністратора для перевірки JWT та видалення
            if (userRepository.findByEmail("admin@mail.com").isEmpty()) {
                User admin = new User();
                admin.setEmail("admin@mail.com");
                admin.setPassword(passwordEncoder.encode("password123"));
                admin.setRole("ROLE_ADMIN"); // Вказуємо роль для доступу до DELETE
                userRepository.save(admin);
                System.out.println("\n--- Адмін створений: admin@mail.com / password123 (ROLE_ADMIN) ---");
            }

            // 2. Додавання тестових даних (категорія та книга) з перевіркою, щоб не заважати інтеграційним тестам
            // Перевіряємо, чи в базі взагалі порожньо, перед тим як ініціалізувати демо-дані
            if (bookService.getAllBooks(null, null, PageRequest.of(0, 1)).isEmpty()) {
                Category fantasy = new Category();
                fantasy.setName("Fantasy");
                bookService.saveCategory(fantasy);

                Book b2 = new Book();
                b2.setTitle("The Witcher");
                b2.setAuthor("Andrzej Sapkowski");
                b2.setCategory(fantasy);

                try {
                    // Твоя перевірка на дублікати всередині saveBook повністю спрацює тут
                    bookService.saveBook(b2);
                    System.out.println("Результат: Книгу 'The Witcher' успішно додано під час первинної ініціалізації.");
                } catch (RuntimeException e) {
                    System.out.println("Результат: " + e.getMessage());
                }
            }

            // 3. Вивід списку для перевірки бази розробки
            System.out.println("\n--- Поточний список книг у системі ---");
            bookService.getAllBooks(null, null, PageRequest.of(0, 10))
                    .forEach(book -> System.out.println("Книга: " + book.getTitle() + " | Category ID: " + (book.getCategory() != null ? book.getCategory().getId() : "null")));
        };
    }
}