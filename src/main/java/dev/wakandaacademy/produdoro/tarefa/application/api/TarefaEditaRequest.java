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

    @Size(message = "A descrição da tarefa deve conter no  de 5 caracteres.", min = 5)
    @NotBlank(message = "A descrição da tarefa não pode ser nula ou vazia.")
    private String descricao;

}
