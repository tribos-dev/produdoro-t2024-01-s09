package dev.wakandaacademy.produdoro.tarefa.infra;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Repository
@Log4j2
@RequiredArgsConstructor
public class TarefaInfraRepository implements TarefaRepository {

	private final TarefaSpringMongoDBRepository tarefaSpringMongoDBRepository;

	@Override
	public Tarefa salva(Tarefa tarefa) {
		log.info("[inicia] TarefaInfraRepository - salva");
		try {
			tarefaSpringMongoDBRepository.save(tarefa);
		} catch (DataIntegrityViolationException e) {
			throw APIException.build(HttpStatus.BAD_REQUEST, "Tarefa já cadastrada", e);
		}
		log.info("[finaliza] TarefaInfraRepository - salva");
		return tarefa;
	}

	@Override
	public Optional<Tarefa> buscaTarefaPorId(UUID idTarefa) {
		log.info("[inicia] TarefaInfraRepository - buscaTarefaPorId");
		Optional<Tarefa> tarefaPorId = tarefaSpringMongoDBRepository.findByIdTarefa(idTarefa);
		log.info("[finaliza] TarefaInfraRepository - buscaTarefaPorId");
		return tarefaPorId;
	}


    @Override
    public void ativaTarefaUsuario(Tarefa tarefa) {
        List<Tarefa> tarefas = tarefaSpringMongoDBRepository.findAllByIdUsuarioAndStatusAtivacao(tarefa.getIdUsuario(),
                StatusAtivacaoTarefa.ATIVA);
        tarefas.stream().forEach(tarefaDesativada -> {
            tarefaDesativada.desativaTarefa();
            salva(tarefaDesativada);
        });
        tarefa.ativaTarefa();
    }

    @Override
    public List<Tarefa> buscaTarefaPorUsuario(UUID idUsuario) {
        log.info("[inicia] TarefaInfraRepository - buscaTarefaPorUdusrio");
        List<Tarefa> buscaTodasTarefas = tarefaSpringMongoDBRepository.findAllByIdUsuario(idUsuario);
        log.info("[finaliza] TarefaInfraRepository - buscaTarefaPorUdusrio");
        return buscaTodasTarefas;
    }

    @Override
    public void deletaTodasTarefas(List<Tarefa> tarefasUsario) {
        log.info("[inicia] TarefaInfraRepository - deletaTodasTarefas");
        tarefaSpringMongoDBRepository.deleteAll(tarefasUsario);
        log.info("[finaliza] TarefaInfraRepository - deletaTodasTarefas");

    }
}
