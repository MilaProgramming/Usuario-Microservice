package com.espe.msvc.usuarios.msvc_usuarios;

import com.espe.msvc.usuarios.msvc_usuarios.models.entity.Usuario;
import com.espe.msvc.usuarios.msvc_usuarios.services.UsuarioService;
import com.espe.msvc.usuarios.msvc_usuarios.controllers.UsuarioController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class UsuarioControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService service;

    @InjectMocks
    private UsuarioController controller;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testListar() throws Exception {
        // Implement your test
        mockMvc.perform(MockMvcRequestBuilders.get("/api/usuarios/listar")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDetalle() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        when(service.porId(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/usuarios/detalle/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDetalleNotFound() throws Exception {
        when(service.porId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/usuarios/detalle/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGuardar() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L); // This will be ignored because it's typically auto-generated
        usuario.setNombre("John Doe");
        usuario.setEmail("john.doe@example.com");
        usuario.setPassword("password123");

        // Mock the service to return the usuario with all fields
        when(service.guardar(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/usuarios/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"John Doe\",\"email\":\"john.doe@example.com\",\"password\":\"password123\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1)) // Note: `id` should be ignored or removed if auto-generated
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value("John Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("password123"));
    }


    @Test
    public void testEditar() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L); // Auto-generated, can be excluded from JSON
        usuario.setNombre("John Doe");
        usuario.setEmail("john.doe@example.com");
        usuario.setPassword("password123");

        // Mock the service to return the usuario with all fields
        when(service.porId(1L)).thenReturn(Optional.of(usuario));
        when(service.guardar(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/usuarios/editar/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"John Doe\",\"email\":\"john.doe@example.com\",\"password\":\"password123\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1)) // Note: `id` should be excluded if auto-generated
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value("John Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("password123"));
    }

    @Test
    public void testEditarNotFound() throws Exception {
        // Mock the service to return an empty Optional
        when(service.porId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/usuarios/editar/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"John Doe\",\"email\":\"john.doe@example.com\",\"password\":\"password123\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    public void testEliminar() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(service.porId(1L)).thenReturn(Optional.of(usuario));
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/usuarios/eliminar/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testEliminarNotFound() throws Exception {
        when(service.porId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/usuarios/eliminar/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
