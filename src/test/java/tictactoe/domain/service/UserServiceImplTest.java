package tictactoe.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import tictactoe.datasource.model.UserEntity;
import tictactoe.datasource.repository.UserEntityRepository;
import tictactoe.domain.model.Role;
import tictactoe.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    private static final UUID ID = UUID.randomUUID();

    private UserEntityRepository repository;
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(UserEntityRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        service = new UserServiceImpl(repository, passwordEncoder);
    }

    @Test
    void registerEncodesPasswordAndSavesWithUserRole() {
        when(passwordEncoder.encode("secret")).thenReturn("encoded");

        User user = service.register("alice", "secret");

        assertEquals("alice", user.getLogin());
        assertEquals("encoded", user.getPassword());
        assertEquals(List.of(Role.USER), user.getRoles());
        verify(repository).save(any(UserEntity.class));
    }

    @Test
    void registerReturnsSavedUserWithUserId() {
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(repository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = service.register("alice", "secret");

        assertTrue(user.getId() != null);
    }

    @Test
    void findByLoginMapsEntityToDomain() {
        UserEntity entity = new UserEntity(ID, "alice", "encoded");
        entity.setRoles(List.of(Role.USER));
        when(repository.findByLogin("alice")).thenReturn(Optional.of(entity));

        Optional<User> user = service.findByLogin("alice");

        assertTrue(user.isPresent());
        assertEquals(ID, user.get().getId());
        assertEquals("alice", user.get().getLogin());
        assertEquals("encoded", user.get().getPassword());
    }

    @Test
    void findByLoginReturnsEmptyWhenNotFound() {
        when(repository.findByLogin("nobody")).thenReturn(Optional.empty());

        assertFalse(service.findByLogin("nobody").isPresent());
    }

    @Test
    void findByIdMapsEntityToDomain() {
        UserEntity entity = new UserEntity(ID, "alice", "encoded");
        entity.setRoles(List.of(Role.USER));
        when(repository.findById(ID)).thenReturn(Optional.of(entity));

        Optional<User> user = service.findById(ID);

        assertTrue(user.isPresent());
        assertEquals(ID, user.get().getId());
    }

    @Test
    void findByIdReturnsEmptyWhenNotFound() {
        UUID unknown = UUID.randomUUID();
        when(repository.findById(unknown)).thenReturn(Optional.empty());

        assertFalse(service.findById(unknown).isPresent());
    }
}