package mk.ukim.finki.wp.kol2022.g2.service.impl;

import mk.ukim.finki.wp.kol2022.g2.model.Course;
import mk.ukim.finki.wp.kol2022.g2.model.Student;
import mk.ukim.finki.wp.kol2022.g2.model.StudentType;
import mk.ukim.finki.wp.kol2022.g2.model.exceptions.InvalidCourseIdException;
import mk.ukim.finki.wp.kol2022.g2.model.exceptions.InvalidStudentIdException;
import mk.ukim.finki.wp.kol2022.g2.repository.CourseRepository;
import mk.ukim.finki.wp.kol2022.g2.repository.StudentRepository;
import mk.ukim.finki.wp.kol2022.g2.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private PasswordEncoder passwordEncoder;

    public StudentServiceImpl(StudentRepository studentRepository, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
       // this.passwordEncoder = passwordEncoder;
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
        if (name==null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }

        List<Course> courses = this.courseRepository.findAllById(courseId);
        if(courses.isEmpty()) throw new InvalidCourseIdException();
        Student s = new Student(name, email,passwordEncoder.encode(password), type,courses, enrollmentDate);
        studentRepository.save(s);
        return s;
    }

    @Override
    public Student update(Long id, String name, String email, String password, StudentType type, List<Long> coursesId, LocalDate enrollmentDate) {
        Student s = this.studentRepository.findById(id).orElseThrow(InvalidStudentIdException::new);
        List<Course> courses= this.courseRepository.findAllById(coursesId);
        if(courses.isEmpty()) throw new InvalidCourseIdException();
        s.setName(name);
        s.setEmail(email);
        s.setPassword(passwordEncoder.encode(password));
        s.setType(type);
        s.setCourses(courses);
        s.setEnrollmentDate(enrollmentDate);
        studentRepository.save(s);

        return s;
    }

    @Override
    public Student delete(Long id) {
       Student s= studentRepository.findById(id).orElseThrow(InvalidStudentIdException::new);
       studentRepository.deleteById(id);
       return s;
    }

    @Override
    public List<Student> filter(Long courseId, Integer yearsOfStudying) {

        if(courseId != null && yearsOfStudying !=null){
           return studentRepository.findAllByCoursesAndEnrollmentDateBefore(courseRepository.findById(courseId).orElseThrow(InvalidCourseIdException::new), LocalDate.now().minusYears(yearsOfStudying));
        } else if( courseId != null){
            return  studentRepository.findAllByCoursesContaining(courseRepository.findById(courseId).orElseThrow(InvalidCourseIdException::new));
        } else if( yearsOfStudying != null){
            return studentRepository.findAllByEnrollmentDateBefore(LocalDate.now().minusYears(yearsOfStudying));
        }else
            return studentRepository.findAll();

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Student s = studentRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException(username));
        return new User(s.getEmail(), s.getPassword(), Stream.of(new SimpleGrantedAuthority(String.format("ROLE_%S", s.getType()))
        ).collect(Collectors.toList()));

    }
}
