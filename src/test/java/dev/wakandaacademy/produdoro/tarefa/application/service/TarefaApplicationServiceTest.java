package dev.wakandaacademy.produdoro.tarefa.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

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

	public TarefaRequest getTarefaRequest() {
		TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
		return request;
	}

	@Test
	void deveListarTodasAsTarefas() {
		// Dado
		Usuario usuario = DataHelper.createUsuario();
		List<Tarefa> tarefas = DataHelper.createListTarefa();
		// Quando
		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
		when(tarefaRepository.buscaTodasTarefasUsuario(any())).thenReturn(tarefas);

		List<TarefaListResponse> resultado = tarefaApplicationService.buscaTodasTarefasUsuario(usuario.getEmail(),
				usuario.getIdUsuario());

		// Então
		verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(usuario.getEmail());
		verify(usuarioRepository, times(1)).buscaUsuarioPorId(usuario.getIdUsuario());
		verify(tarefaRepository, times(1)).buscaTodasTarefasUsuario(usuario.getIdUsuario());
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
}
