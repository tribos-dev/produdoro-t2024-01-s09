package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/tarefa")
public interface TarefaAPI {

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    TarefaIdResponse postNovaTarefa(@RequestBody @Valid TarefaRequest tarefaRequest);

    @GetMapping("/{idTarefa}")
    @ResponseStatus(code = HttpStatus.OK)
    TarefaDetalhadoResponse detalhaTarefa(@RequestHeader(name = "Authorization", required = true) String token,
            @PathVariable UUID idTarefa);

    @PatchMapping("/ativa-tarefa")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void ativaTarefa(@RequestHeader(name = "Authorization", required = true) String token,
            @RequestParam UUID idTarefa);

    @GetMapping("/listar-tarefas/{idUsuario}")
    @ResponseStatus(code = HttpStatus.OK)
    List<TarefaListResponse> getTodasTarefasUsuario(
            @RequestHeader(name = "Authorization", required = true) String token,
            @PathVariable UUID idUsuario);

    @GetMapping("/conclui-tarefa/{idTarefa}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void concluiTarefa(@RequestHeader(name = "Authorization", required = true) String token,
            @PathVariable UUID idTarefa);

    @DeleteMapping("/usuario/{idUsuario}/limpar-todas-as-tarefas")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void deletaTodasSuasTarefas(@RequestHeader(name = "Authorization", required = true) String token,
            @PathVariable UUID idUsuario);

    @PatchMapping("/edita-tarefa/{idTarefa}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void editaTarefa(@RequestHeader(name = "Authorization", required = true) String token,
            @RequestBody @Valid TarefaEditaRequest tarefaEdita, @PathVariable UUID idTarefa);
}
