package com.espe.msvc.usuarios.msvc_usuarios;

import com.espe.msvc.usuarios.msvc_usuarios.models.entity.Usuario;
import com.espe.msvc.usuarios.msvc_usuarios.repositories.UsuarioRepository;
import com.espe.msvc.usuarios.msvc_usuarios.services.UsuarioServicelmpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioServicelmpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListar() {
        service.listar();
        verify(repository, times(1)).findAll();
    }

    @Test
    void testPorId() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        when(repository.findById(anyLong())).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = service.porId(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    void testGuardar() {
        Usuario usuario = new Usuario();
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario result = service.guardar(usuario);

        assertThat(result).isNotNull();
        verify(repository, times(1)).save(usuario);
    }

    @Test
    void testEliminar() {
        doNothing().when(repository).deleteById(anyLong());
        service.eliminar(1L);
        verify(repository, times(1)).deleteById(anyLong());
    }
}
