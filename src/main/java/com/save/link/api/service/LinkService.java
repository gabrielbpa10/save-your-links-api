package com.save.link.api.service;

import com.save.link.api.dto.LinkDTO;
import com.save.link.api.dto.form.LinkForm;
import com.save.link.api.model.entity.Link;
import com.save.link.api.model.entity.User;
import com.save.link.api.model.repository.LinkRepository;
import com.save.link.api.util.exception.BusinessException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class LinkService {
    private static final Logger log = LoggerFactory.getLogger(LinkService.class);

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private UserService userService;

    public void save(LinkForm linkForm) {
        try {
            Optional<User> userOptional = this.userService.getUserByEmail(linkForm.emailUser);
            User user = userOptional.orElse(null);
            Link link = new Link(user, linkForm.descTitle, linkForm.descURL);
            this.linkRepository.save(link);
        }  catch (Exception exception) {
            log.info("Erro em salvar um link: " + exception.getMessage());
            throw new BusinessException("Erro ao salvar um link, entrar em contato com o suporte.");
        }
    }

    public List<LinkDTO> findByURL(String filter) {
        return linkRepository.findByDescURLContaining(filter)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(link -> new LinkDTO(
                        link.getDescURL(),
                        link.getDescTitle()
                ))
                .toList();
    }
}
