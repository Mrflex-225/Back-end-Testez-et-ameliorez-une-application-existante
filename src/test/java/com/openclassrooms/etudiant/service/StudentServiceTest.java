package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.dto.StudentRequestDTO;
import com.openclassrooms.etudiant.dto.StudentResponseDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.mapper.StudentMapper;
import com.openclassrooms.etudiant.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {
    private static final Long STUDENT_ID = 1L;
    private static final String FIRST_NAME = "Alex";
    private static final String LAST_NAME = "Aka";
    private static final String EMAIL = "alex.aka@test.com";

    @Mock
    StudentRepository studentRepository;

    @Mock
    StudentMapper studentMapper;

    @InjectMocks
    StudentService studentService;

    /**
     * Vérifie qu'un étudiant peut être créé correctement.
     */
    @Test
    public void test_create_student() {
        // GIVEN
        StudentRequestDTO requestDTO = new StudentRequestDTO();
        requestDTO.setFirstName(FIRST_NAME);
        requestDTO.setLastName(LAST_NAME);
        requestDTO.setEmail(EMAIL);

        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);

        Student savedStudent = new Student();
        savedStudent.setId(STUDENT_ID);
        savedStudent.setFirstName(FIRST_NAME);
        savedStudent.setLastName(LAST_NAME);
        savedStudent.setEmail(EMAIL);

        StudentResponseDTO responseDTO = new StudentResponseDTO();
        responseDTO.setId(STUDENT_ID);
        responseDTO.setFirstName(FIRST_NAME);
        responseDTO.setLastName(LAST_NAME);
        responseDTO.setEmail(EMAIL);

        when(studentMapper.toEntity(requestDTO))
                .thenReturn(student);

        when(studentRepository.save(student))
                .thenReturn(savedStudent);

        when(studentMapper.toDto(savedStudent))
                .thenReturn(responseDTO);

        // WHEN
        StudentResponseDTO result =
                studentService.create(requestDTO);
        // THEN
        assertNotNull(result);
        assertEquals(STUDENT_ID, result.getId());
        assertEquals(FIRST_NAME, result.getFirstName());
        assertEquals(LAST_NAME, result.getLastName());
        assertEquals(EMAIL, result.getEmail());
    }

    /**
     * Vérifie que la liste de tous les étudiants est retournée.
     */
    @Test
    public void test_find_all_students() {

        // GIVEN
        Student student1 = new Student();
        student1.setId(1L);
        student1.setFirstName("John");
        student1.setLastName("Doe");
        student1.setEmail("john@test.com");

        Student student2 = new Student();
        student2.setId(2L);
        student2.setFirstName("Jane");
        student2.setLastName("Doe");
        student2.setEmail("jane@test.com");


        StudentResponseDTO dto1 = new StudentResponseDTO();
        dto1.setId(1L);
        dto1.setFirstName("John");
        dto1.setLastName("Doe");
        dto1.setEmail("john@test.com");

        StudentResponseDTO dto2 = new StudentResponseDTO();
        dto2.setId(2L);
        dto2.setFirstName("Jane");
        dto2.setLastName("Doe");
        dto2.setEmail("jane@test.com");


        when(studentRepository.findAll())
                .thenReturn(List.of(student1, student2));

        when(studentMapper.toDto(student1))
                .thenReturn(dto1);

        when(studentMapper.toDto(student2))
                .thenReturn(dto2);


        // WHEN
        List<StudentResponseDTO> result =
                studentService.findAll();


        // THEN
        assertNotNull(result);

        assertEquals(2, result.size());

        assertEquals(
                "John",
                result.get(0).getFirstName()
        );

        assertEquals(
                "Jane",
                result.get(1).getFirstName()
        );
    }


    /**
     * Vérifie qu'un étudiant peut être récupéré avec son identifiant.
     */
    @Test
    public void test_find_student_by_id() {

        // GIVEN
        Student student = new Student();
        student.setId(STUDENT_ID);
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);

        StudentResponseDTO responseDTO =
                new StudentResponseDTO();

        responseDTO.setId(STUDENT_ID);
        responseDTO.setFirstName(FIRST_NAME);
        responseDTO.setLastName(LAST_NAME);
        responseDTO.setEmail(EMAIL);


        when(studentRepository.findById(STUDENT_ID))
                .thenReturn(Optional.of(student));

        when(studentMapper.toDto(student))
                .thenReturn(responseDTO);


        // WHEN
        StudentResponseDTO result =
                studentService.findById(STUDENT_ID);


        // THEN
        assertNotNull(result);

        assertEquals(
                STUDENT_ID,
                result.getId()
        );

        assertEquals(
                FIRST_NAME,
                result.getFirstName()
        );

        assertEquals(
                LAST_NAME,
                result.getLastName()
        );

        assertEquals(
                EMAIL,
                result.getEmail()
        );
    }


    /**
     * Vérifie qu'un étudiant peut être modifié.
     */
    @Test
    public void test_update_student() {

        // GIVEN
        StudentRequestDTO requestDTO =
                new StudentRequestDTO();

        requestDTO.setFirstName("Jean");
        requestDTO.setLastName("Martin");
        requestDTO.setEmail("jean.martin@test.com");


        Student existingStudent =
                new Student();

        existingStudent.setId(STUDENT_ID);
        existingStudent.setFirstName(FIRST_NAME);
        existingStudent.setLastName(LAST_NAME);
        existingStudent.setEmail(EMAIL);


        Student updatedStudent =
                new Student();

        updatedStudent.setId(STUDENT_ID);
        updatedStudent.setFirstName("Jean");
        updatedStudent.setLastName("Martin");
        updatedStudent.setEmail("jean.martin@test.com");


        StudentResponseDTO responseDTO =
                new StudentResponseDTO();

        responseDTO.setId(STUDENT_ID);
        responseDTO.setFirstName("Jean");
        responseDTO.setLastName("Martin");
        responseDTO.setEmail("jean.martin@test.com");


        when(studentRepository.findById(STUDENT_ID))
                .thenReturn(Optional.of(existingStudent));

        when(studentRepository.save(existingStudent))
                .thenReturn(updatedStudent);

        when(studentMapper.toDto(updatedStudent))
                .thenReturn(responseDTO);


        // WHEN
        StudentResponseDTO result =
                studentService.update(
                        STUDENT_ID,
                        requestDTO
                );


        // THEN
        assertNotNull(result);

        assertEquals(
                STUDENT_ID,
                result.getId()
        );

        assertEquals(
                "Jean",
                result.getFirstName()
        );

        assertEquals(
                "Martin",
                result.getLastName()
        );

        assertEquals(
                "jean.martin@test.com",
                result.getEmail()
        );
    }


    /**
     * Vérifie que la suppression d'un étudiant existant
     * s'exécute correctement.
     */
    @Test
    public void test_delete_student() {

        // GIVEN
        Student student = new Student();

        student.setId(STUDENT_ID);
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);


        when(studentRepository.findById(STUDENT_ID))
                .thenReturn(Optional.of(student));


        // WHEN / THEN
        assertDoesNotThrow(
                () -> studentService.delete(STUDENT_ID)
        );
    }




}
