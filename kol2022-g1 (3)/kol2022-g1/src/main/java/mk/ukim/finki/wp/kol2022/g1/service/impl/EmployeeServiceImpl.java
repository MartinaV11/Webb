/*package mk.ukim.finki.wp.kol2022.g1.service.impl;

import mk.ukim.finki.wp.kol2022.g1.model.Employee;
import mk.ukim.finki.wp.kol2022.g1.model.EmployeeType;
import mk.ukim.finki.wp.kol2022.g1.model.Skill;
import mk.ukim.finki.wp.kol2022.g1.model.exceptions.InvalidEmployeeIdException;
import mk.ukim.finki.wp.kol2022.g1.model.exceptions.InvalidSkillIdException;
import mk.ukim.finki.wp.kol2022.g1.repository.EmployeeRepository;
import mk.ukim.finki.wp.kol2022.g1.repository.SkillRepository;
import mk.ukim.finki.wp.kol2022.g1.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Service
public class EmployeeServiceImpl implements EmployeeService, UserDetailsService {
    private final EmployeeRepository employeeRepository;
    private final SkillRepository skillRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, SkillRepository skillRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.skillRepository = skillRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Employee> listAll() {
        return this.employeeRepository.findAll();
    }

    @Override
    public Employee findById(Long id) {
        return this.employeeRepository.findById(id).orElseThrow(InvalidEmployeeIdException::new);
    }

    @Override
    public Employee create(String name, String email, String password, EmployeeType type, List<Long> skillId, LocalDate employmentDate) {
        String encryptedPassword=this.passwordEncoder.encode(password);
        List<Skill> skills=this.skillRepository.findAllById(skillId);
        Employee employee=new Employee(name,email,encryptedPassword,type,skills,employmentDate);

        return this.employeeRepository.save(employee);
    }

    @Override
    public Employee update(Long id, String name, String email, String password, EmployeeType type, List<Long> skillId, LocalDate employmentDate) {
        Employee employee=this.employeeRepository.findById(id).orElseThrow(InvalidEmployeeIdException::new);
        employee.setName(name);
        employee.setEmail(email);
        employee.setPassword(passwordEncoder.encode(password));
        employee.setType(type);
        List<Skill> skills=this.skillRepository.findAllById(skillId);
        if (skills.isEmpty()) throw new InvalidSkillIdException();
        employee.setSkills(skills);
        employee.setEmploymentDate(employmentDate);
        return this.employeeRepository.save(employee);
    }

    @Override
    public Employee delete(Long id) {
        Employee employee=this.findById(id);
        this.employeeRepository.delete(employee);
        return employee;
    }

    @Override
    public List<Employee> filter(Long skillId, Integer yearsOfService) {
        List<Employee> results;
        if (skillId == null && yearsOfService == null) {
            results = employeeRepository.findAll();
        } else if (skillId != null && yearsOfService != null) {
            results = employeeRepository.findAllBySkillsContainingAndEmploymentDateAfter(
                    skillRepository.getById(skillId),
                    LocalDate.now().minusYears(yearsOfService)
            );
        } else if (skillId != null) {
            results = employeeRepository.findAllBySkillsContaining(skillRepository.getById(skillId));
        } else {
            results = employeeRepository.findAllByEmploymentDateAfter(LocalDate.now().minusYears(yearsOfService));
        }
        return results;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //return (UserDetails) this.userRepository.findByUsername(username).orElseThrow(InvalidUsernameException::new);
        Employee employee = this.employeeRepository.findByEmail(username);
        return new User(
                employee.getEmail(),
                employee.getPassword(),
                Stream.of(new SimpleGrantedAuthority(String.format("ROLE_%S", employee.getType()))).collect(Collectors.toList())
        );

        /*UserDetails userDetails=new org.springframework.security.core.userdetails.User(employee.getEmail(),
                employee.getPassword(), Stream.of(new SimpleGrantedAuthority(employee.getType().toString())).collect(Collectors.toList()));
        return userDetails;*/
 //   }
//}

package mk.ukim.finki.wp.kol2022.g1.service.impl;

import mk.ukim.finki.wp.kol2022.g1.model.Employee;
import mk.ukim.finki.wp.kol2022.g1.model.EmployeeType;
import mk.ukim.finki.wp.kol2022.g1.model.Skill;
import mk.ukim.finki.wp.kol2022.g1.model.exceptions.InvalidEmployeeIdException;
import mk.ukim.finki.wp.kol2022.g1.model.exceptions.InvalidSkillIdException;
import mk.ukim.finki.wp.kol2022.g1.repository.EmployeeRepository;
import mk.ukim.finki.wp.kol2022.g1.repository.SkillRepository;
import mk.ukim.finki.wp.kol2022.g1.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EmployeeServiceImpl implements EmployeeService, UserDetailsService {
    private final EmployeeRepository employeeRepository;
    private final SkillRepository skillRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, SkillRepository skillRepository) {
        this.employeeRepository = employeeRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public List<Employee> listAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee findById(Long id) {
        return employeeRepository.findById(id).orElseThrow(InvalidEmployeeIdException::new);
    }

    @Override
    public Employee create(String name, String email, String password, EmployeeType type, List<Long> skillId, LocalDate employmentDate) {
        List<Skill> skillList = skillRepository.findAllById(skillId);
        if (skillList.isEmpty()) throw new InvalidSkillIdException();
        return employeeRepository.save(new Employee(name, email, passwordEncoder.encode(password), type, skillList, employmentDate));
    }

    @Override
    public Employee update(Long id, String name, String email, String password, EmployeeType type, List<Long> skillId, LocalDate employmentDate) {
        Employee employee = employeeRepository.findById(id).orElseThrow(InvalidEmployeeIdException::new);
        List<Skill> skills = skillRepository.findAllById(skillId);
        employee.setName(name);
        employee.setEmail(email);
        employee.setPassword(passwordEncoder.encode(password));
        employee.setType(type);
        employee.setSkills(skills);
        employee.setEmploymentDate(employmentDate);
        return employeeRepository.save(employee);
/*
        Employee employee = this.findById(id);
        employee.setName(name);
        employee.setEmail(email);
        employee.setPassword(passwordEncoder.encode(password));
        employee.setType(type);
        List<Skill> skills = this.skillRepository.findAllById(skillId);
        employee.setSkills(skills);
        employee.setEmploymentDate(employmentDate);

        return this.employeeRepository.save(employee);*/
    }

    @Override
    public Employee delete(Long id) {
        Employee old = employeeRepository.findById(id).orElseThrow(InvalidEmployeeIdException::new);
        employeeRepository.delete(old);
        return old;
    }

    @Override
    public List<Employee> filter(Long skillId, Integer yearsOfService) {
        List<Employee> results;
       if (skillId != null && yearsOfService != null) {
            results = employeeRepository.findAllBySkillsContainingAndEmploymentDateBefore(
                    skillRepository.getById(skillId),
                    LocalDate.now().minusYears(yearsOfService)
            );
        }
           else if (skillId != null ) {
            results = employeeRepository.findAllBySkillsContaining(skillRepository.getById(skillId));
        } else if(yearsOfService!=null) {
            results = employeeRepository.findAllByEmploymentDateBefore(LocalDate.now().minusYears(yearsOfService));
        } else{
               results=employeeRepository.findAll();
        }

        return results;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee e = employeeRepository.findByEmail(username);
        return new User(
                e.getEmail(),
                e.getPassword(),
                Stream.of(new SimpleGrantedAuthority(String.format("ROLE_%S", e.getType()))).collect(Collectors.toList())
        );
    }
}

