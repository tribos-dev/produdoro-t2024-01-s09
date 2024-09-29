package dev.wakandaacademy.produdoro.usuario.application.service;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioApplicationServiceTest {

    @InjectMocks
    UsuarioApplicationService usuarioApplicationService;

    @Mock
    UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Verifica se STATUS usuario mudou para PAUSA LONGA")
    void mudaStatusParaPausaLongaTest() {
        Usuario usuario = DataHelper.createUsuarioFOCO();
        when(usuarioRepository.salva(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
        usuarioApplicationService.mudaStatusParaPausaLonga(usuario.getEmail(), usuario.getIdUsuario());
        verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(usuario.getEmail());
        verify(usuarioRepository, times(1)).buscaUsuarioPorId(usuario.getIdUsuario());
        verify(usuarioRepository, times(1)).salva(usuario);
        assertEquals(StatusUsuario.PAUSA_LONGA, usuario.getStatus());
    }

    @Test
    @DisplayName("Verifica se usuário já está em PAUSA LONGA")
    void verificaSeUsuarioJaEstaEmPausaLonga() {
        Usuario usuario = DataHelper.createUsuario();
        APIException exception = assertThrows(APIException.class, usuario::validaSeUsuarioJaEstaEmPausaLonga);
        assertEquals("Usuário já esta em PAUSA LONGA", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
    }

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