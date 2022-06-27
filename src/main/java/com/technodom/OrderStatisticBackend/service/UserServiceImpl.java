package com.technodom.OrderStatisticBackend.service;

import com.technodom.OrderStatisticBackend.entity.Role;
import com.technodom.OrderStatisticBackend.entity.User;
import com.technodom.OrderStatisticBackend.repo.RoleRepository;
import com.technodom.OrderStatisticBackend.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByIin(username);
        if (user==null) {
            throw new UsernameNotFoundException("User not found");
        }else {
            return user;
        }
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String userName, String roleName) {
        User user = userRepository.findByIin(userName);
        Role role = roleRepository.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public User getUser(String userName) {
        return userRepository.findByIin(userName);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

}
