package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import dev.wakandaacademy.produdoro.config.security.service.TokenService;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.service.TarefaService;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequiredArgsConstructor
public class TarefaRestController implements TarefaAPI {
	private final TarefaService tarefaService;
	private final TokenService tokenService;

	public TarefaIdResponse postNovaTarefa(TarefaRequest tarefaRequest) {
		log.info("[inicia]  TarefaRestController - postNovaTarefa  ");
		TarefaIdResponse tarefaCriada = tarefaService.criaNovaTarefa(tarefaRequest);
		log.info("[finaliza]  TarefaRestController - postNovaTarefa");
		return tarefaCriada;
	}

	@Override
	public TarefaDetalhadoResponse detalhaTarefa(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - detalhaTarefa");
		String usuario = getUsuarioByToken(token);
		Tarefa tarefa = tarefaService.detalhaTarefa(usuario, idTarefa);
		log.info("[finaliza] TarefaRestController - detalhaTarefa");
		return new TarefaDetalhadoResponse(tarefa);
	}

	@Override
	public void incrementaPomodoro(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - incrementaPomodoro");
		String usuarioEmail = getUsuarioByToken(token);
		tarefaService.incrementaPomodoro(usuarioEmail, idTarefa);
		log.info("[finaliza] TarefaRestController - incrementaPomodoro");
	}

	public void ativaTarefa(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - ativaTarefa");
		String usuarioEmail = getUsuarioByToken(token);
		tarefaService.ativaTarefa(usuarioEmail, idTarefa);
		log.info("[finaliza] TarefaRestController - ativaTarefa");
	}

	@Override
	public void deletaTodasSuasTarefas(String token, UUID idUsuario) {
		log.info("[inicia] TarefaRestController - deletaTodasSuasTarefas");
		String emailUsuario = getUsuarioByToken(token);
		tarefaService.deletaTodasTarefas(emailUsuario, idUsuario);
		log.info("[finaliza] TarefaRestController - deletaTodasSuasTarefas");

	}

	@Override
	public void concluiTarefa(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - concluiTarefa");
		String usuario = this.getUsuarioByToken(token);
		tarefaService.concluiTarefa(usuario, idTarefa);
		log.info("[finaliza] TarefaRestController - concluiTarefa");
	}

	private String getUsuarioByToken(String token) {
		log.debug("[token] {}", token);
		String usuario = tokenService.getUsuarioByBearerToken(token)
				.orElseThrow(() -> APIException.build(HttpStatus.UNAUTHORIZED, token));
		log.info("[usuario] {}", usuario);
		return usuario;
	}

	@Override
	public void editaTarefa(String token, TarefaEditaRequest tarefaEdita, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - editaTarefa");
		String usuario = getUsuarioByToken(token);
		tarefaService.editaTarefa(usuario, tarefaEdita, idTarefa);
		log.info("[finaliza] TarefaRestController - editaTarefa");
	}

	@Override
	public List<TarefaListResponse> getTodasTarefasUsuario(String token, UUID idUsuario) {
		log.info("[inicia] TarefaRestController - getTodasTarefasUsuario");
		String usuario = getUsuarioByToken(token);
		List<TarefaListResponse> tarefas = tarefaService.buscaTarefasUsuario(usuario, idUsuario);
		log.info("[finaliza] TarefaRestController - getTodasTarefasUsuario");
		return tarefas;
	}

	@Override
	public void deletaTarefasConcluidas(String token, UUID idUsuario) {
		log.info("[inicia] TarefaRestController - deletaTarefasConcluidas");
		String email = getUsuarioByToken(token);
		tarefaService.deletaTarefasConcluidas(email, idUsuario);
		log.info("[finaliza] TarefaRestController - deletaTarefasConcluidas");
	}
}
