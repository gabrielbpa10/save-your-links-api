package com.save.link.api.service;

import com.save.link.api.model.entity.Link;
import com.save.link.api.model.repository.LinkRepository;
import com.save.link.api.util.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LinkServiceTest {

    @Mock
    LinkRepository linkRepository;

    @InjectMocks
    LinkService linkService;

    @Test
    public void deleteLinkById() {
        Long idLink = 1L;
        Link link = new Link();
        link.setId(idLink);
        when(linkRepository.findById(idLink)).thenReturn(Optional.of(link));

        linkService.deleteLink(idLink);

        verify(linkRepository).findById(idLink);
        verify(linkRepository).delete(link);
    }

    @Test
    public void deleteLinkByIdWhenLinkDoesNotExist() {
        Long idLink = 1L;
        when(linkRepository.findById(idLink)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> linkService.deleteLink(idLink)
        );

        assertEquals("Link não encontrado.", exception.getMessage());
        verify(linkRepository).findById(idLink);
        verifyNoMoreInteractions(linkRepository);
    }
}
