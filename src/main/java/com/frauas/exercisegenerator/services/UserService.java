package com.frauas.exercisegenerator.services;

import com.frauas.exercisegenerator.documents.User;
import com.frauas.exercisegenerator.dtos.CreateUserDto;
import com.frauas.exercisegenerator.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User createUser(CreateUserDto userDto) {
        passwordEncoder = new BCryptPasswordEncoder();
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public void deleteUserByUsername(String username) {
        userRepository.deleteUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent() == false) {
            throw new UsernameNotFoundException("The Username " + username + " could not be found");
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (user.get().isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("admin"));
        }

        authorities.add(new SimpleGrantedAuthority("professor"));
        return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), authorities);
    }
}
