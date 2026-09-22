package com.save.link.api.service;

import com.save.link.api.dto.form.UserForm;
import com.save.link.api.model.entity.User;
import com.save.link.api.model.repository.UserRepository;
import com.save.link.api.util.exception.BusinessException;
import com.save.link.api.util.response.UtilResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void save(UserForm userForm) {
        User user = new User(userForm.name(), userForm.lastName(), userForm.email());

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException("Já existe esse usuário já cadastrado.");
        }
    }
}
