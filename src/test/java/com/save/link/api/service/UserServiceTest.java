package com.save.link.api.service;

import com.save.link.api.model.entity.User;
import com.save.link.api.model.repository.UserRepository;
import com.save.link.api.util.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock UserRepository repository;
    @InjectMocks UserService service;

    @Test
    void updatesExistingUserWithoutCreatingAnotherAccount() {
        User user = new User("Ana", "Silva", "ana@example.com");
        user.setId(12L);
        when(repository.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        service.atualizarIdEntreAzure(" ana@example.com ", "azure-id");
        assertEquals("azure-id", user.getIdEntreAzure());
        assertEquals(12L, user.getId());
        verify(repository).saveAndFlush(user);
    }

    @Test
    void refusesToOverwriteAnotherAzureIdentity() {
        User user = new User("Ana", "Silva", "ana@example.com");
        user.setIdEntreAzure("existing-id");
        when(repository.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        assertThrows(BusinessException.class, () -> service.atualizarIdEntreAzure("ana@example.com", "other-id"));
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void rejectsMissingAzureId() {
        assertThrows(BusinessException.class, () -> service.atualizarIdEntreAzure("ana@example.com", ""));
        verifyNoInteractions(repository);
    }
}
