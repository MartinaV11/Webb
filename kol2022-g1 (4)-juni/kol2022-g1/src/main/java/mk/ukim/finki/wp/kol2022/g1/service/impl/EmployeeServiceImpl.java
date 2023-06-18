package mk.ukim.finki.wp.kol2022.g1.service.impl;

import mk.ukim.finki.wp.kol2022.g1.model.Employee;
import mk.ukim.finki.wp.kol2022.g1.model.EmployeeType;
import mk.ukim.finki.wp.kol2022.g1.model.Skill;
import mk.ukim.finki.wp.kol2022.g1.model.exceptions.InvalidEmployeeIdException;
import mk.ukim.finki.wp.kol2022.g1.model.exceptions.InvalidSkillIdException;
import mk.ukim.finki.wp.kol2022.g1.repository.EmployeeRepository;
import mk.ukim.finki.wp.kol2022.g1.repository.SkillRepository;
import mk.ukim.finki.wp.kol2022.g1.service.EmployeeService;
;
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
public class EmployeeServiceImpl implements EmployeeService, UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final SkillRepository skillRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, SkillRepository skillRepository,  PasswordEncoder passwordEncoder) {
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
        return this.employeeRepository.findById(id).orElseThrow(InvalidSkillIdException::new);
    }

    @Override
    public Employee create(String name, String email, String password, EmployeeType type, List<Long> skillId, LocalDate employmentDate) {


        List<Skill> skills = skillRepository.findAllById(skillId);
        if(skills.isEmpty()){
            throw new InvalidSkillIdException();
        }
        Employee e= new Employee(name, email, passwordEncoder.encode(password), type, skills , employmentDate );
        this.employeeRepository.save(e);
        return e;
    }

    @Override
    public Employee update(Long id, String name, String email, String password, EmployeeType type, List<Long> skillId, LocalDate employmentDate) {
        Employee employee = employeeRepository.findById(id).orElseThrow(InvalidEmployeeIdException::new);
        List<Skill> skillList = skillRepository.findAllById(skillId);
        if(skillList.isEmpty()){
            throw new InvalidSkillIdException();
        }
        employee.setName(name);
        employee.setEmail(email);
        employee.setPassword(passwordEncoder.encode(password));
        employee.setType(type);
        employee.setSkills(skillList);
        employee.setEmploymentDate(employmentDate);

        //this.employeeRepository.save(employee);
        return employeeRepository.save(employee);
    }

    @Override
    public Employee delete(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(InvalidEmployeeIdException::new);
        this.employeeRepository.deleteById(id);
        return employee;
    }

    @Override
    public List<Employee> filter(Long skillId, Integer yearsOfService) {
        if(skillId !=null && yearsOfService!=null){
            Skill skill=skillRepository.getById(skillId);
            return  this.employeeRepository.findAllBySkillsContainingAndEmploymentDateBefore(skill, LocalDate.now().minusYears(yearsOfService));
        }

        else if (skillId != null) {
            Skill skill = skillRepository.getById(skillId);
            return this.employeeRepository.findAllBySkillsContaining(skill);
        }
       else if (yearsOfService != null){
            return this.employeeRepository.findAllByEmploymentDateBefore(LocalDate.now().minusYears(yearsOfService));
        }
        else {
            return this.employeeRepository.findAll();
        }

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee e = employeeRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException(username));
        return new User(e.getEmail(), e.getPassword(), Stream.of(new SimpleGrantedAuthority(String.format("ROLE_%S", e.getType())))
                .collect(Collectors.toList()));

    }
   /* @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee e = employeeRepository.findByEmail(username);
        return new User(
                e.getEmail(),
                e.getPassword(),
                Stream.of(new SimpleGrantedAuthority(String.format("ROLE_%S", e.getType()))).collect(Collectors.toList())
        );
    }*/
}
