package dev.wakandaacademy.produdoro.usuario.application.service;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioApplicationServiceTest {

    @InjectMocks
    UsuarioApplicationService usuarioApplicationService;

    @Mock
    UsuarioRepository usuarioRepository;


        @Test
        void deveAlterarStatusParaPausaCurta(){

            Usuario usuario = DataHelper.createUsuario();

            when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
            when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
            usuarioApplicationService.mudaStatusParaPausaCurta(usuario.getEmail(), usuario.getIdUsuario());

            verify(usuarioRepository, times(1)).salva(usuario);
            assertEquals(StatusUsuario.PAUSA_CURTA, usuario.getStatus());
        }

        @Test
        void naoDeveAlterarStatusParaPausaCurta() {

            Usuario usuario = DataHelper.createUsuario();
            UUID idUsuario = UUID.fromString("f81d4fae-7dec-4f34-a5e2-8c9559d13911");
            when(usuarioRepository.buscaUsuarioPorEmail((anyString()))).thenReturn(usuario);
            APIException e = assertThrows(APIException.class,
                    () -> usuarioApplicationService.mudaStatusParaPausaCurta(usuario.getEmail(), idUsuario));
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusException());
        }
}