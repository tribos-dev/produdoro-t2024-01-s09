package dev.wakandaacademy.produdoro.tarefa.application.repository;

import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarefaRepository {

    Tarefa salva(Tarefa tarefa);
    Optional<Tarefa> buscaTarefaPorId(UUID idTarefa);
	List<Tarefa> buscaTodasTarefasUsuario(String usuario, UUID idUsuario);
	List<Tarefa> buscaTarefasConcluidas(UUID idUsuario);
	void deletaVariasTarefas(List<Tarefa> tarefasConcluidas);
	List<Tarefa> visualizaTodasAsTarefa(UUID idUsuario);
	void atualizaPosicaoDaTarefa(List<Tarefa> tarefasDoUsuario);
}
