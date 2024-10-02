package dev.wakandaacademy.produdoro.tarefa.application.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TarefaEditaRequest {

    @NotBlank(message = "A descrição da tarefa não pode ser nula ou vazia.")
    @Size(message = "A descrição da tarefa deve conter no mínimo 5 caracteres.", min = 5, max = 255)
    private String descricao;
}
