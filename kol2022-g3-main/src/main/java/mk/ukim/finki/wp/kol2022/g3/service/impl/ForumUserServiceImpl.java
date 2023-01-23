package mk.ukim.finki.wp.kol2022.g3.service.impl;

import mk.ukim.finki.wp.kol2022.g3.model.ForumUser;
import mk.ukim.finki.wp.kol2022.g3.model.ForumUserType;
import mk.ukim.finki.wp.kol2022.g3.model.Interest;
import mk.ukim.finki.wp.kol2022.g3.model.exceptions.InvalidForumUserIdException;
import mk.ukim.finki.wp.kol2022.g3.model.exceptions.InvalidInterestIdException;
import mk.ukim.finki.wp.kol2022.g3.repository.ForumUserRepository;
import mk.ukim.finki.wp.kol2022.g3.repository.InterestRepository;
import mk.ukim.finki.wp.kol2022.g3.service.ForumUserService;
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
public class ForumUserServiceImpl implements ForumUserService, UserDetailsService {
    private final ForumUserRepository forumUserRepository;
    private final InterestRepository interestRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ForumUserServiceImpl(ForumUserRepository forumUserRepository, InterestRepository interestRepository) {

        this.forumUserRepository = forumUserRepository;
        this.interestRepository = interestRepository;
    }

    @Override
    public List<ForumUser> listAll() {
        return this.forumUserRepository.findAll();
    }

    @Override
    public ForumUser findById(Long id) {
        return this.forumUserRepository.findById(id).orElseThrow(InvalidForumUserIdException::new);
    }

    @Override
    public ForumUser create(String name, String email, String password, ForumUserType type, List<Long> interestId, LocalDate birthday) {
        List<Interest> interests=interestRepository.findAllById(interestId);
        ForumUser user= new ForumUser(name, email, passwordEncoder.encode(password), type, interests, birthday);
        this.forumUserRepository.save(user);
        return user;
    }

    @Override
    public ForumUser update(Long id, String name, String email, String password, ForumUserType type, List<Long> interestId, LocalDate birthday) {
        ForumUser user = this.forumUserRepository.findById(id).orElseThrow(InvalidForumUserIdException::new);
        List<Interest> interests = interestRepository.findAllById(interestId);
        if(interests.isEmpty()){
            throw new IllegalArgumentException();
        }
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setType(type);
        user.setInterests(interests);
        user.setBirthday(birthday);
        this.forumUserRepository.save(user);

        return user;
    }

    @Override
    public ForumUser delete(Long id) {
        ForumUser user = this.forumUserRepository.findById(id).orElseThrow(InvalidForumUserIdException::new);
        this.forumUserRepository.delete(user);
        return user;
    }

    @Override
    public List<ForumUser> filter(Long interestId, Integer age) {
        if(interestId!= null && age !=null){
            return forumUserRepository.findAllByInterestsAndAndBirthdayBefore(interestRepository.findById(interestId).orElseThrow(InvalidInterestIdException::new),
                    LocalDate.now().minusYears(age));
        } else if(interestId!=null){
            return forumUserRepository.findAllByInterestsContaining(interestRepository.findById(interestId).orElseThrow(InvalidInterestIdException::new));
        } else if(age!=null){
            return forumUserRepository.findAllByBirthdayBefore(LocalDate.now().minusYears(age));
        }
        else return forumUserRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ForumUser u= forumUserRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException(username));
        return new User(u.getEmail(), u.getPassword(), Stream.of(new SimpleGrantedAuthority(String.format("ROLE_%S", u.getType()))).collect(Collectors.toList()));
    }
}
