package com.espe.msvc.usuarios.msvc_usuarios;
import com.espe.msvc.usuarios.msvc_usuarios.models.entity.Usuario;
import com.espe.msvc.usuarios.msvc_usuarios.services.UsuarioService;
import com.espe.msvc.usuarios.msvc_usuarios.controllers.UsuarioController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    @Mock
    private UsuarioService service;

    @InjectMocks
    private UsuarioController controller;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListar() {
        List<Usuario> usuarios = Collections.singletonList(new Usuario());
        when(service.listar()).thenReturn(usuarios);

        ResponseEntity<List<Usuario>> response = controller.listar();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).listar();
    }

    @Test
    void testDetalle() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        when(service.porId(anyLong())).thenReturn(Optional.of(usuario));

        ResponseEntity<Usuario> response = controller.detalle(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(service, times(1)).porId(anyLong());
    }

    @Test
    void testGuardar() {
        Usuario usuario = new Usuario();
        when(service.guardar(any(Usuario.class))).thenReturn(usuario);
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = controller.guardar(usuario, bindingResult);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(service, times(1)).guardar(any(Usuario.class));
    }

    @Test
    void testEditar() {
        Usuario usuario = new Usuario();
        usuario.setNombre("John");
        Optional<Usuario> usuarioOptional = Optional.of(usuario);
        when(service.porId(anyLong())).thenReturn(usuarioOptional);
        when(service.guardar(any(Usuario.class))).thenReturn(usuario);
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = controller.editar(usuario, 1L, bindingResult);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(service, times(1)).porId(anyLong());
        verify(service, times(1)).guardar(any(Usuario.class));
    }

    @Test
    void testEliminar() {
        Usuario usuario = new Usuario();
        when(service.porId(anyLong())).thenReturn(Optional.of(usuario));
        doNothing().when(service).eliminar(anyLong());

        ResponseEntity<Void> response = controller.eliminar(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service, times(1)).porId(anyLong());
        verify(service, times(1)).eliminar(anyLong());
    }
}
