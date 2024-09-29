package dev.wakandaacademy.produdoro.tarefa.domain;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaEditaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.extern.log4j.Log4j2;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpStatus;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Document(collection = "Tarefa")
@Log4j2
public class Tarefa {
	@Id
	private UUID idTarefa;
	@NotBlank
	private String descricao;
	@Indexed
	private UUID idUsuario;
	@Indexed
	private UUID idArea;
	@Indexed
	private UUID idProjeto;
	private StatusTarefa status;
	private StatusAtivacaoTarefa statusAtivacao;
	private int contagemPomodoro;

	public Tarefa(TarefaRequest tarefaRequest) {
		this.idTarefa = UUID.randomUUID();
		this.idUsuario = tarefaRequest.getIdUsuario();
		this.descricao = tarefaRequest.getDescricao();
		this.idArea = tarefaRequest.getIdArea();
		this.idProjeto = tarefaRequest.getIdProjeto();
		this.status = StatusTarefa.A_FAZER;
		this.statusAtivacao = StatusAtivacaoTarefa.INATIVA;
		this.contagemPomodoro = 1;
	}

	public void pertenceAoUsuario(Usuario usuarioPorEmail) {
		if (!this.idUsuario.equals(usuarioPorEmail.getIdUsuario())) {
			throw APIException.build(HttpStatus.UNAUTHORIZED, "Usuário não é dono da Tarefa solicitada!");
		}
	}

	public void incrementaPomodoroSeStatusEstiverEmFoco(Tarefa tarefa, Usuario usuario) {
		log.info("[inicia] Tarefa - incrementaPomodoroSeStatusEstiverEmFoco");
		pertenceAoUsuario(usuario);
		if(!usuario.getStatus().equals(StatusUsuario.FOCO)){
			ativaTarefa();
			usuario.mudaStatusParaFoco(usuario.getIdUsuario());
		} else {
			tarefa.incrementaPomodoro();
			alteraStatusPorQuantidadeDePomodoros(tarefa, usuario);
		}
		log.info("[finaliza] Tarefa - incrementaPomodoroSeStatusEstiverEmFoco");
	}

	private void alteraStatusPorQuantidadeDePomodoros(Tarefa tarefa, Usuario usuario) {
		log.info("[inicia] Tarefa - alteraStatusPorQuantidadeDePomodoros");
		int totalDePomodoros = tarefa.getContagemPomodoro();
		if (totalDePomodoros % 4 == 0) {
			usuario.mudaStatusParaPausaLonga(usuario.getIdUsuario());
		} else {
			usuario.mudaStatusParaPausaCurta(usuario.getIdUsuario());
		}
		log.info("[finaliza] Tarefa - alteraStatusPorQuantidadeDePomodoros");
	}

	private int incrementaPomodoro() {
		return ++contagemPomodoro;
	}

	public void ativaTarefa() {
		if (this.statusAtivacao.equals(StatusAtivacaoTarefa.INATIVA)) {
			this.statusAtivacao = StatusAtivacaoTarefa.ATIVA;
		}
	}

	public void desativaTarefa() {
		this.statusAtivacao = StatusAtivacaoTarefa.INATIVA;

	}

	public void editaTarefa(TarefaEditaRequest tarefaEdita) {
		this.descricao = tarefaEdita.getDescricao();
	}

	public void concluiTarefa() {
		this.status = StatusTarefa.CONCLUIDA;
	}
}
