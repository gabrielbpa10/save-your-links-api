package com.save.link.api.service;

import com.save.link.api.model.dto.form.UserForm;
import com.save.link.api.model.entity.User;
import com.save.link.api.model.repository.UserRepository;
import com.save.link.api.util.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void save(UserForm userForm) {
        User user = new User(userForm.name(), userForm.lastName(), userForm.email());

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException("Já existe este usuário já cadastrado.");
        }
    }

    public Optional<User> getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }
}
