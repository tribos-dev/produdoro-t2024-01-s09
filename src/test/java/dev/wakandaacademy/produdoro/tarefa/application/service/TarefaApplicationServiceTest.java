package dev.wakandaacademy.produdoro.tarefa.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import static org.mockito.ArgumentMatchers.anyString;

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
		when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request, 0));

		TarefaIdResponse response = tarefaApplicationService.criaNovaTarefa(request);

		assertNotNull(response);
		assertEquals(TarefaIdResponse.class, response.getClass());
		assertEquals(UUID.class, response.getIdTarefa().getClass());
	}

	@Test
	void deveRetornarTarefaAtiva() {
		Tarefa tarefa = DataHelper.createTarefa();
		Usuario usuario = DataHelper.createUsuario();
		when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
		when(tarefaRepository.buscaTarefaPorId(tarefa.getIdTarefa())).thenReturn(Optional.of(tarefa));
		tarefaApplicationService.ativaTarefa(usuario.getEmail(), tarefa.getIdTarefa());
		verify(tarefaRepository).ativaTarefaUsuario(tarefa);
		verify(tarefaRepository, times(1)).salva(tarefa);
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

		List<TarefaListResponse> resultado = tarefaApplicationService.buscaTarefasUsuario(usuario.getEmail(),
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

		APIException e = assertThrows(APIException.class,
				() -> tarefaApplicationService.buscaTarefasUsuario("emailinvalido@gmail.com", usuario.getIdUsuario()));

		assertEquals(HttpStatus.BAD_REQUEST, e.getStatusException());
		assertEquals("Usuario não encontrado!", e.getMessage());
	}

	public TarefaRequest getTarefaRequest() {
		TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
		return request;
	}

	@Test
	@DisplayName("Verifica se incrementou pomodoro")
	void incrementaPomodoroTest() {
		Usuario usuario = DataHelper.createUsuarioFOCO();
		Tarefa tarefa = DataHelper.createTarefaPorIdUsuario(usuario.getIdUsuario());
		int contagemPomodoroAntes = tarefa.getContagemPomodoro();

		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));
		tarefaApplicationService.incrementaPomodoro(usuario.getEmail(), tarefa.getIdTarefa());

		int contagemPomodoroDepois = tarefa.getContagemPomodoro();
		verify(tarefaRepository, times(1)).salva(any());
		assertEquals(contagemPomodoroAntes + 1, contagemPomodoroDepois);
	}

	@Test
	@DisplayName("incrementaPomodoro não encontra tarefa, NOT_FOUND")
	void incrementaPomodoroJogaExceptionSeNaoEncontraTarefa() {
		Usuario usuario = DataHelper.createUsuarioFOCO();
		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		assertThrows(APIException.class,
				() -> tarefaApplicationService.incrementaPomodoro(usuario.getEmail(), UUID.randomUUID()));
	}

	@Test
	@DisplayName("incrementaPomodoro fornece token nao autorizado, UNAUTHORIZED")
	void incrementaPomodoroJogaExceptionSeRetornaTokenNaoAutorizado() {
		Usuario usuario = DataHelper.createUsuario();
		Usuario usuario2 = DataHelper.createUsuarioFOCO();
		Tarefa tarefa = DataHelper.createTarefaPorIdUsuario(usuario2.getIdUsuario());

		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));
		assertThrows(APIException.class,
				() -> tarefaApplicationService.incrementaPomodoro(usuario.getEmail(), tarefa.getIdTarefa()));
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

	@Test
	@DisplayName("Deleta tarefas concluidas")
	void deletaTarefasConcluidas_comDadosValidos_sucesso() {
		Usuario usuario = DataHelper.createUsuario();
		List<Tarefa> tarefasConcluidas = DataHelper.createTarefasConcluidas();
		List<Tarefa> tarefas = DataHelper.createListTarefa();
		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
		when(tarefaRepository.buscaTarefasConcluidas(any())).thenReturn(tarefasConcluidas);
		when(tarefaRepository.buscaTarefaPorUsuario(any())).thenReturn(tarefas);

		tarefaApplicationService.deletaTarefasConcluidas(usuario.getEmail(), usuario.getIdUsuario());

		verify(tarefaRepository, times(1)).deletaVariasTarefas(tarefasConcluidas);
		verify(tarefaRepository, times(1)).atualizaPosicaoDaTarefa(tarefas);
	}

	@Test
	@DisplayName("Deleta tarefas concluidas quando email for inexistente")
	void deletaTarefasConcluidas_comEmailInexistente_retornaAPIException() {
		String email = "emailinvalido@gmail.com";
		when(usuarioRepository.buscaUsuarioPorEmail(any()))
				.thenThrow(APIException.build(HttpStatus.BAD_REQUEST, "Usuario não encontrado!"));

		assertThrows(APIException.class,
				() -> tarefaApplicationService.deletaTarefasConcluidas(email, UUID.randomUUID()));

		verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(email);
	}
}
