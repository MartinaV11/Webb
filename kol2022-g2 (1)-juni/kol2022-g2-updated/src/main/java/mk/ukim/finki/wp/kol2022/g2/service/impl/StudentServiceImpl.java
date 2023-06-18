package mk.ukim.finki.wp.kol2022.g2.service.impl;

import mk.ukim.finki.wp.kol2022.g2.model.Course;
import mk.ukim.finki.wp.kol2022.g2.model.Student;
import mk.ukim.finki.wp.kol2022.g2.model.StudentType;
import mk.ukim.finki.wp.kol2022.g2.model.exceptions.InvalidCourseIdException;
import mk.ukim.finki.wp.kol2022.g2.model.exceptions.InvalidStudentIdException;
import mk.ukim.finki.wp.kol2022.g2.repository.CourseRepository;
import mk.ukim.finki.wp.kol2022.g2.repository.StudentRepository;
import mk.ukim.finki.wp.kol2022.g2.service.StudentService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StudentServiceImpl implements StudentService, UserDetailsService {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentServiceImpl(StudentRepository studentRepository, CourseRepository courseRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Student> listAll() {
        return this.studentRepository.findAll();
    }

    @Override
    public Student findById(Long id) {
        return this.studentRepository.findById(id).orElseThrow(InvalidStudentIdException::new);
    }

    @Override
    public Student create(String name, String email, String password, StudentType type, List<Long> courseId, LocalDate enrollmentDate) {
        List<Course> courses=this.courseRepository.findAllById(courseId);
        if(courses.isEmpty()){
            throw new InvalidCourseIdException();
        }
        Student student= new Student(name, email, passwordEncoder.encode(password), type, courses, enrollmentDate);
        this.studentRepository.save(student);
        return student;
    }

    @Override
    public Student update(Long id, String name, String email, String password, StudentType type, List<Long> coursesId, LocalDate enrollmentDate) {
        Student nov = this.studentRepository.findById(id).orElseThrow(InvalidStudentIdException::new);
        List<Course> courses=this.courseRepository.findAllById(coursesId);
        if(courses.isEmpty()){
            throw new InvalidCourseIdException();
        }
        nov.setName(name);
        nov.setEmail(email);
        nov.setPassword(passwordEncoder.encode(password));
        nov.setType(type);
        nov.setCourses(courses);
        nov.setEnrollmentDate(enrollmentDate);
        this.studentRepository.save(nov);


        return nov;
    }

    @Override
    public Student delete(Long id) {
        Student student = this.studentRepository.findById(id).orElseThrow(InvalidStudentIdException::new);
        this.studentRepository.delete(student);
        return student;
    }

    @Override
    public List<Student> filter(Long courseId, Integer yearsOfStudying) {
        if(courseId !=null && yearsOfStudying!=null){
            Course course= this.courseRepository.getById(courseId);
            return this.studentRepository.findAllByCoursesContainingAndEnrollmentDateBefore(course, LocalDate.now().minusYears(yearsOfStudying));
        } else if(courseId !=null){
            return this.studentRepository.findAllByCoursesContaining(courseRepository.findById(courseId).get());

        } else if(yearsOfStudying!=null){
            return this.studentRepository.findAllByEnrollmentDateBefore(LocalDate.now().minusYears(yearsOfStudying));
        }
        else
            return this.studentRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Student student= this.studentRepository.findStudentByEmail(username).orElseThrow(()->new UsernameNotFoundException(username));
        return new User(student.getEmail(), student.getPassword(), Stream.of(new SimpleGrantedAuthority(String.format("ROLE_%S", student.getType())))
                .collect(Collectors.toList()));

    }
}
