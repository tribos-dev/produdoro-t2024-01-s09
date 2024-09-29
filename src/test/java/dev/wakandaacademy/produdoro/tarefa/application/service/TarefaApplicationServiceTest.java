package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaEditaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaApplicationServiceTest {

    // @Autowired
    @InjectMocks
    TarefaApplicationService tarefaApplicationService;

    // @MockBean
    @Mock
    TarefaRepository tarefaRepository;

    @Mock
    UsuarioRepository usuarioRepository;

    @Test
    void deveRetornarIdTarefaNovaCriada() {
        TarefaRequest request = getTarefaRequest();
        when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request));

        TarefaIdResponse response = tarefaApplicationService.criaNovaTarefa(request);

        assertNotNull(response);
        assertEquals(TarefaIdResponse.class, response.getClass());
        assertEquals(UUID.class, response.getIdTarefa().getClass());
    }

    @Test
    void deveDeletarTodasTarefasDoUsuario() {
        Usuario usuario = DataHelper.createUsuario();
        List<Tarefa> tarefas = DataHelper.createListTarefa();
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorUsuario(any())).thenReturn(tarefas);
        tarefaApplicationService.deletaTodasTarefas(usuario.getEmail(), usuario.getIdUsuario());
        verify(tarefaRepository, times(1)).deletaTodasTarefas(tarefas);
    }

    @Test
    void deveListarTodasAsTarefas() {
        // Dado
        Usuario usuario = DataHelper.createUsuario();
        List<Tarefa> tarefas = DataHelper.createListTarefa();
        // Quando
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorUsuario(any())).thenReturn(tarefas);

        List<TarefaListResponse> resultado = tarefaApplicationService.buscaTodasTarefasUsuario(usuario.getEmail(),
                usuario.getIdUsuario());

        // Então
        verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(usuario.getEmail());
        verify(usuarioRepository, times(1)).buscaUsuarioPorId(usuario.getIdUsuario());
        verify(tarefaRepository, times(1)).buscaTarefaPorUsuario(usuario.getIdUsuario());
        assertEquals(resultado.size(), 8);
    }

    @Test
    void naoDeveBuscarTodasTarefasPorUsuario() {
        Usuario usuario = DataHelper.createUsuario();
        when(usuarioRepository.buscaUsuarioPorEmail(any()))
                .thenThrow(APIException.build(HttpStatus.BAD_REQUEST, "Usuario não encontrado!"));

        APIException e = assertThrows(APIException.class, () -> tarefaApplicationService
                .buscaTodasTarefasUsuario("emailinvalido@gmail.com", usuario.getIdUsuario()));

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusException());
        assertEquals("Usuario não encontrado!", e.getMessage());
    }

    public TarefaRequest getTarefaRequest() {
        TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
        return request;
    }

    @Test
    @DisplayName("Deve editar a tarefa")
    void deveEditarTarefa() {
        // Dado
        Usuario usuario = DataHelper.createUsuario();
        Tarefa tarefa = DataHelper.createTarefa();
        TarefaEditaRequest tarefaEdita = DataHelper.getTarefaEditaRequest();
        // Quando
        when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));
        tarefaApplicationService.editaTarefa(usuario.getEmail(), tarefaEdita, tarefa.getIdTarefa());
        // Entao
        verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(usuario.getEmail());
        verify(tarefaRepository, times(1)).buscaTarefaPorId(tarefa.getIdTarefa());
        verify(tarefaRepository, times(1)).salva(tarefa);
        assertEquals("Tarefa de teste", tarefa.getDescricao());
    }

    @Test
    @DisplayName("Não deve editar a tarefa")
    void naoDeveEditarTarefa() {
        // Dado
        UUID idTarefaInvalida = UUID.randomUUID();
        String usuario = "usuarioqualquerum@hotmail.com";
        TarefaEditaRequest tarefaEdita = DataHelper.getTarefaEditaRequest();
        // Quando
        when(tarefaRepository.buscaTarefaPorId(idTarefaInvalida)).thenReturn(Optional.empty());
        assertThrows(APIException.class,
                () -> tarefaApplicationService.editaTarefa(usuario, tarefaEdita, idTarefaInvalida));
        // Entao
        verify(tarefaRepository, times(1)).buscaTarefaPorId(idTarefaInvalida);
    }

    @Test
    @DisplayName("Deve concluir tarefa")
    void deveConcluirTarefa() {
        // Dado
        Usuario usuario = DataHelper.createUsuario();
        Tarefa tarefa = DataHelper.createTarefa();
        // Quando
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));
        tarefaApplicationService.concluiTarefa(usuario.getEmail(), tarefa.getIdTarefa());
        // Entao
        assertEquals(tarefa.getStatus(), StatusTarefa.CONCLUIDA);
    }

    @Test
    @DisplayName("Não deve concluir tarefa")
    void naoDeveConcluirTarefa() {
        // Dado
        Tarefa tarefa = DataHelper.createTarefa();
        // Quando
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenThrow(APIException.class);
        // Entao
        assertThrows(APIException.class,
                () -> tarefaApplicationService.concluiTarefa("emailqualquerum@hotmail.com", tarefa.getIdTarefa()));
    }

}
