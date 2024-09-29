package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.tarefa.application.api.*;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

import java.util.List;
import java.util.UUID;

public interface TarefaService {
    TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest);

    Tarefa detalhaTarefa(String usuario, UUID idTarefa);

    void ativaTarefa(String usuarioEmail, UUID idTarefa);

    void editaTarefa(String usuario, TarefaEditaRequest tarefaEdita, UUID idTarefa);

    void deletaTodasTarefas(String emailUsuario, UUID idUsuario);

    List<TarefaListResponse> buscaTodasTarefasUsuario(String usuario, UUID idUsuario);

    void concluiTarefa(String usuario, UUID idTarefa);

    void mudaOrdemDaTarefa(String emailDoUsuario, UUID idTarefa, NovaPosicaoDaTarefaRequest novaPosicaoDaTarefaRequest);
}
