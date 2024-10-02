package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaEditaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

import java.util.List;
import java.util.UUID;

public interface TarefaService {
    TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest);

    Tarefa detalhaTarefa(String usuario, UUID idTarefa);

	List<TarefaListResponse> buscaTarefasUsuario(String usuario, UUID idUsuario);

	void deletaTarefasConcluidas(String email, UUID idUsuario);

    void incrementaPomodoro(String usuarioEmail, UUID idTarefa);

    void ativaTarefa(String usuarioEmail, UUID idTarefa);

    void editaTarefa(String usuario, TarefaEditaRequest tarefaEdita, UUID idTarefa);

    void deletaTodasTarefas(String emailUsuario, UUID idUsuario);


    void concluiTarefa(String usuario, UUID idTarefa);
}
