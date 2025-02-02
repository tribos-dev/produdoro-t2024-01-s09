package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaEditaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class TarefaApplicationService implements TarefaService {
	private final TarefaRepository tarefaRepository;
	private final UsuarioRepository usuarioRepository;

	@Override
	public TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest) {
		log.info("[inicia] TarefaApplicationService - criaNovaTarefa");
		int novaPosicao = tarefaRepository.contarTarefas(tarefaRequest.getIdUsuario());
		Tarefa tarefaCriada = tarefaRepository.salva(new Tarefa(tarefaRequest, novaPosicao));
		log.info("[finaliza] TarefaApplicationService - criaNovaTarefa");
		return TarefaIdResponse.builder().idTarefa(tarefaCriada.getIdTarefa()).build();
	}

	@Override
	public Tarefa detalhaTarefa(String usuario, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - detalhaTarefa");
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
		log.info("[usuarioPorEmail] {}", usuarioPorEmail);
		Tarefa tarefa = tarefaRepository.buscaTarefaPorId(idTarefa)
				.orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Tarefa não encontrada!"));
		tarefa.pertenceAoUsuario(usuarioPorEmail);
		log.info("[finaliza] TarefaApplicationService - detalhaTarefa");
		return tarefa;
	}

	@Override
	public void incrementaPomodoro(String usuarioEmail, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - incrementaPomodoro");
		Tarefa tarefa = detalhaTarefa(usuarioEmail, idTarefa);
		Usuario usuario = usuarioRepository.buscaUsuarioPorEmail(usuarioEmail);
		tarefa.incrementaPomodoroSeStatusEstiverEmFoco(tarefa, usuario);
		usuarioRepository.salva(usuario);
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] TarefaApplicationService - incrementaPomodoro");
	}

	@Override
	public void deletaTarefasConcluidas(String email, UUID idUsuario) {
		log.info("[inicia] TarefaApplicationService - deletaTarefasConcluidas");
		Usuario usarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(email);
		Usuario usuario = usuarioRepository.buscaUsuarioPorId(idUsuario);
		usuario.pertenceAoUsuario(usarioPorEmail);
		List<Tarefa> tarefasConcluidas = tarefaRepository.buscaTarefasConcluidas(usuario.getIdUsuario());
		if (tarefasConcluidas.isEmpty()) {
			throw APIException.build(HttpStatus.NOT_FOUND, "Usuário não possui nenhuma tarefa concluída!");
		}
		tarefaRepository.deletaVariasTarefas(tarefasConcluidas);
		List<Tarefa> tarefasDoUsuario = tarefaRepository.buscaTarefaPorUsuario(usuario.getIdUsuario());
		tarefaRepository.atualizaPosicaoDaTarefa(tarefasDoUsuario);
		log.info("[finaliza] TarefaApplicationService - deletaTarefasConcluidas");
	}

	@Override
	public void ativaTarefa(String usuarioEmail, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - ativaTarefa");
		Tarefa tarefa = detalhaTarefa(usuarioEmail, idTarefa);
		tarefaRepository.ativaTarefaUsuario(tarefa);
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] TarefaApplicationService - ativaTarefa");

	}

	@Override
	public List<TarefaListResponse> buscaTarefasUsuario(String usuario, UUID idUsuario) {
		log.info("[inicia] TarefaApplicationService - buscaTodasTarefasUsuario");
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
		usuarioRepository.buscaUsuarioPorId(idUsuario);
		usuarioPorEmail.validaUsuario(idUsuario);
		List<Tarefa> tarefas = tarefaRepository.buscaTarefaPorUsuario(idUsuario);
		log.info("[finaliza] TarefaApplicationService - buscaTodasTarefasUsuario");
		return TarefaListResponse.converte(tarefas);
	}

	@Override
	public void concluiTarefa(String usuario, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - concluiTarefa");
		Tarefa tarefa = this.detalhaTarefa(usuario, idTarefa);
		tarefa.concluiTarefa();
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] TarefaApplicationService - concluiTarefa");
	}

	@Override
	public void deletaTodasTarefas(String emailUsuario, UUID idUsuario) {
		log.info("[inicia] TarefaApplicationService - deletaTodasTarefas");
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(emailUsuario);
		log.info("[usuarioPorEmail] {}", usuarioPorEmail);
		Usuario usuario = usuarioRepository.buscaUsuarioPorId((idUsuario));
		usuario.emailDoUsuario(usuarioPorEmail);
		List<Tarefa> tarefasUsario = tarefaRepository.buscaTarefaPorUsuario(usuario.getIdUsuario());
		if (tarefasUsario.isEmpty()) {
			throw APIException.build(HttpStatus.BAD_REQUEST, "Usuário não possui tarefas(as) cadastrada(as)");
		}
		tarefaRepository.deletaTodasTarefas(tarefasUsario);
		log.info("[final] TarefaApplicationService - deletaTodasTarefas");

	}

	@Override
	public void editaTarefa(String usuario, TarefaEditaRequest tarefaEdita, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - editaTarefa");
		Tarefa tarefa = this.detalhaTarefa(usuario, idTarefa);
		tarefa.editaTarefa(tarefaEdita);
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] TarefaApplicationService - editaTarefa");
	}
}
