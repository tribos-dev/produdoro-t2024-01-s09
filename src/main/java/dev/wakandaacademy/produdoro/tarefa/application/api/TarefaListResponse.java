package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.UUID;

import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;

public class TarefaListResponse {
	private UUID idTarefa;
	private String descricao;
	private UUID idUsuario;
	private UUID idArea;
	private UUID idProjeto;
	private StatusTarefa status;
}