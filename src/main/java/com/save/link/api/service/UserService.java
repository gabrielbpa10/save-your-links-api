package com.save.link.api.service;

import com.save.link.api.model.dto.form.UserForm;
import com.save.link.api.model.entity.User;
import com.save.link.api.model.repository.UserRepository;
import com.save.link.api.util.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AzureCadastroService azureCadastroService;

    public void save(UserForm userForm) {
        User user = new User(userForm.name(), userForm.lastName(), userForm.email().trim());

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException("Não foi possível cadastrar uma nova conta, pois já existe conta de e-mail cadastrado.");
        }
    }

    @Transactional
    public void atualizarIdEntreAzure(String email, String idEntreAzure) {
        if (idEntreAzure == null || idEntreAzure.isBlank()) {
            throw new BusinessException("O Azure não retornou o ID do usuário.");
        }
        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new BusinessException("Usuário do cadastro não encontrado."));
        if (user.getIdEntreAzure() != null && !user.getIdEntreAzure().equals(idEntreAzure)) {
            throw new BusinessException("O usuário já está vinculado a outra conta Azure.");
        }
        user.setIdEntreAzure(idEntreAzure);
        userRepository.saveAndFlush(user);
    }

    public Optional<User> getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public void hasEmailCadastrado(String email) {
        if(getUserByEmail(email).isPresent()) {
            throw new BusinessException("Não foi possível cadastrar uma nova conta, pois já existe conta de e-mail cadastrado.");
        }
    }
}
