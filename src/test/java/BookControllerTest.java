import com.example.pr13.Pr13Application;
import com.example.pr13.entity.Book;
import com.example.pr13.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(classes = Pr13Application.class)
@AutoConfigureMockMvc
// Ця анотація автоматично робить MockMvc авторизованим, обходячи 403 Forbidden!
@WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll(); // Очищення бази перед кожним тестом
    }

    @Test
    public void testCreateBookSuccess() throws Exception {
        Map<String, Object> bookJson = new HashMap<>();
        bookJson.put("title", "Metro 2033");
        bookJson.put("author", "Dmitry Glukhovsky");
        bookJson.put("categoryId", null);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookJson)))
                .andExpect(status().isOk()) 
                .andExpect(content().string("Книгу успішно додано!"));

        boolean exists = bookRepository.findAll().stream()
                .anyMatch(book -> book.getTitle().equals("Metro 2033"));
        assertTrue(exists, "Книга повинна бути збережена в БД Docker");
    }

    @Test
    public void testGetBooksPageSuccess() throws Exception {
        Book savedBook = new Book();
        savedBook.setTitle("The Witcher");
        savedBook.setAuthor("Andrzej Sapkowski");
        bookRepository.save(savedBook);

        mockMvc.perform(get("/api/books")
                        .param("title", "The Witcher")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.content[0].title").value("The Witcher")) 
                .andExpect(jsonPath("$.content[0].author").value("Andrzej Sapkowski"));
    }
}