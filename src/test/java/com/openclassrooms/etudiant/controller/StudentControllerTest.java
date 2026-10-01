package com.openclassrooms.etudiant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.etudiant.dto.StudentRequestDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.entities.User;
import com.openclassrooms.etudiant.repository.StudentRepository;
import com.openclassrooms.etudiant.repository.UserRepository;
import com.openclassrooms.etudiant.service.JwtService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.springframework.test.web.servlet.MockMvc;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@Testcontainers
public class StudentControllerTest {

    @Container
    private static final MySQLContainer<?> MYSQL_CONTAINER =
            new MySQLContainer<>("mysql:8.0");

    @DynamicPropertySource
    static void configureProperties(
            DynamicPropertyRegistry registry
    ) {

        registry.add(
                "spring.datasource.url",
                MYSQL_CONTAINER::getJdbcUrl
        );

        registry.add(
                "spring.datasource.username",
                MYSQL_CONTAINER::getUsername
        );

        registry.add(
                "spring.datasource.password",
                MYSQL_CONTAINER::getPassword
        );
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;


    private String token;


    @BeforeEach
    void setUp() {

        /*
         * On crée un utilisateur réel dans la base de test
         * pour générer un JWT valide.
         */
        User user = new User();

        user.setFirstName("Test");
        user.setLastName("User");
        user.setLogin("testuser");

        user.setPassword(
                passwordEncoder.encode("password")
        );

        User savedUser =
                userRepository.save(user);

        token =
                jwtService.generateToken(savedUser);
    }


    @AfterEach
    void cleanDatabase() {
        studentRepository.deleteAll();
        userRepository.deleteAll();
    }


    /**
     * Vérifie qu'un étudiant peut être créé.
     */
    @Test
    public void test_create_student()
            throws Exception {

        // GIVEN
        StudentRequestDTO request =
                new StudentRequestDTO();

        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@test.com");


        // WHEN / THEN
        mockMvc.perform(
                        post("/api/students")

                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )

                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )

                                .content(
                                        objectMapper
                                                .writeValueAsString(request)
                                )
                )

                .andDo(print())

                .andExpect(
                        status().isCreated()
                )

                .andExpect(
                        jsonPath("$.firstName")
                                .value("John")
                )

                .andExpect(
                        jsonPath("$.lastName")
                                .value("Doe")
                )

                .andExpect(
                        jsonPath("$.email")
                                .value("john.doe@test.com")
                );
    }


    /**
     * Vérifie que la liste des étudiants
     * peut être récupérée.
     */
    @Test
    public void test_get_all_students()
            throws Exception {

        // GIVEN
        Student student1 =
                new Student();

        student1.setFirstName("John");
        student1.setLastName("Doe");
        student1.setEmail("john@test.com");


        Student student2 =
                new Student();

        student2.setFirstName("Jane");
        student2.setLastName("Doe");
        student2.setEmail("jane@test.com");


        studentRepository.save(student1);

        studentRepository.save(student2);


        // WHEN / THEN
        mockMvc.perform(
                        get("/api/students")

                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )

                .andDo(print())

                .andExpect(
                        status().isOk()
                )

                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )

                .andExpect(
                        jsonPath("$[0].firstName")
                                .exists()
                )

                .andExpect(
                        jsonPath("$[1].firstName")
                                .exists()
                );
    }


    /**
     * Vérifie qu'un étudiant peut être
     * récupéré avec son identifiant.
     */
    @Test
    public void test_get_student_by_id()
            throws Exception {

        // GIVEN
        Student student =
                new Student();

        student.setFirstName("John");
        student.setLastName("Doe");
        student.setEmail("john@test.com");


        Student savedStudent =
                studentRepository.save(student);


        // WHEN / THEN
        mockMvc.perform(
                        get(
                                "/api/students/{id}",
                                savedStudent.getId()
                        )

                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )

                .andDo(print())

                .andExpect(
                        status().isOk()
                )

                .andExpect(
                        jsonPath("$.id")
                                .value(savedStudent.getId())
                )

                .andExpect(
                        jsonPath("$.firstName")
                                .value("John")
                )

                .andExpect(
                        jsonPath("$.lastName")
                                .value("Doe")
                )

                .andExpect(
                        jsonPath("$.email")
                                .value("john@test.com")
                );
    }


    /**
     * Vérifie qu'un étudiant peut être modifié.
     */
    @Test
    public void test_update_student()
            throws Exception {

        // GIVEN
        Student student =
                new Student();

        student.setFirstName("John");
        student.setLastName("Doe");
        student.setEmail("john@test.com");


        Student savedStudent =
                studentRepository.save(student);


        StudentRequestDTO request =
                new StudentRequestDTO();

        request.setFirstName("Jean");
        request.setLastName("Martin");
        request.setEmail("jean@test.com");


        // WHEN / THEN
        mockMvc.perform(
                        put(
                                "/api/students/{id}",
                                savedStudent.getId()
                        )

                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )

                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )

                                .content(
                                        objectMapper
                                                .writeValueAsString(request)
                                )
                )

                .andDo(print())

                .andExpect(
                        status().isOk()
                )

                .andExpect(
                        jsonPath("$.firstName")
                                .value("Jean")
                )

                .andExpect(
                        jsonPath("$.lastName")
                                .value("Martin")
                )

                .andExpect(
                        jsonPath("$.email")
                                .value("jean@test.com")
                );
    }


    /**
     * Vérifie qu'un étudiant peut être supprimé.
     */
    @Test
    public void test_delete_student()
            throws Exception {

        // GIVEN
        Student student =
                new Student();

        student.setFirstName("John");
        student.setLastName("Doe");
        student.setEmail("john@test.com");


        Student savedStudent =
                studentRepository.save(student);


        // WHEN / THEN
        mockMvc.perform(
                        delete(
                                "/api/students/{id}",
                                savedStudent.getId()
                        )

                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )

                .andDo(print())

                .andExpect(
                        status().isNoContent()
                );
    }

}
