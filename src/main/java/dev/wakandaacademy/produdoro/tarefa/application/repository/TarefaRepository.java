package dev.wakandaacademy.produdoro.tarefa.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

public interface TarefaRepository {

    Tarefa salva(Tarefa tarefa);

    Optional<Tarefa> buscaTarefaPorId(UUID idTarefa);

    void ativaTarefaUsuario(Tarefa tarefa);

    List<Tarefa> buscaTarefaPorUsuario(UUID idUsuario);

    void deletaTodasTarefas(List<Tarefa> tarefasUsario);

	List<Tarefa> buscaTarefasConcluidas(UUID idUsuario);

	void deletaVariasTarefas(List<Tarefa> tarefasConcluidas);

	void atualizaPosicaoDaTarefa(List<Tarefa> tarefasDoUsuario);

	int contarTarefas(UUID idUsuario);
}
