package dev.wakandaacademy.produdoro.usuario.domain;

import java.util.UUID;

import javax.validation.constraints.Email;

import dev.wakandaacademy.produdoro.handler.APIException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.pomodoro.domain.ConfiguracaoPadrao;
import dev.wakandaacademy.produdoro.usuario.application.api.UsuarioNovoRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Slf4j
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@Document(collection = "Usuario")
public class Usuario {
	@Id
	private UUID idUsuario;
	@Email
	@Indexed(unique = true)
	private String email;
	private ConfiguracaoUsuario configuracao;
	@Builder.Default
	private StatusUsuario status = StatusUsuario.FOCO;
	@Builder.Default
	private Integer quantidadePomodorosPausaCurta = 0;

	public Usuario(UsuarioNovoRequest usuarioNovo, ConfiguracaoPadrao configuracaoPadrao) {
		this.idUsuario = UUID.randomUUID();
		this.email = usuarioNovo.getEmail();
		this.status = StatusUsuario.FOCO;
		this.configuracao = new ConfiguracaoUsuario(configuracaoPadrao);
	}

    public void emailDoUsuario(Usuario usuarioPorEmail) {
		if (!this.getIdUsuario().equals(usuarioPorEmail.getIdUsuario())){
			throw APIException.build(HttpStatus.UNAUTHORIZED,
					"Usuario(a) não autorizado(a) para a requisição solicitada.");
		}
    }

	public void mudaStatusParaPausaCurta(UUID idUsuario) {
		log.info("[inicia] Usuario - mudaStatusParaPausaCurta");
		validaUsuario(idUsuario);
		this.status = StatusUsuario.PAUSA_CURTA;
		log.info("[finaliza] Usuario - mudaStatusParaPausaCurta");
	}


	public void mudaStatusParaFoco(UUID idUsuario) {
		log.info("[inicia] Usuario - mudaStatusParaFoco");
		validaUsuario(idUsuario);
		this.status = StatusUsuario.FOCO;
		log.info("[finaliza] Usuario - mudaStatusParaFoco");
	}

	public void mudaStatusParaPausaLonga(UUID idUsuario) {
		log.info("[inicia] Usuario - mudaStatusParaPausaLonga");
		validaUsuario(idUsuario);
		validaSeUsuarioJaEstaEmPausaLonga();
		this.status = StatusUsuario.PAUSA_LONGA;
		log.info("[finaliza] Usuario - mudaStatusParaPausaLonga");
	}

	public void validaSeUsuarioJaEstaEmPausaLonga() {
		log.info("[inicia] Usuario - validaSeUsuarioJaEstaEmPausaLonga");
		if (this.status.equals(StatusUsuario.PAUSA_LONGA)) {
			log.info("[finaliza] APIException - validaSeUsuarioJaEstaEmPausaLonga");
			throw APIException.build(HttpStatus.BAD_REQUEST, "Usuário já esta em PAUSA LONGA");
		}
		log.info("[finaliza] Usuario - validaSeUsuarioJaEstaEmPausaLonga");
	}

	public void validaUsuario(UUID idUsuario) {
		log.info("[inicia] Usuario - validaUsuario");
		if (!this.idUsuario.equals(idUsuario)) {
			log.info("[finaliza] APIException - validaUsuario");
			throw APIException.build(HttpStatus.UNAUTHORIZED, "O usuário não é dono dessa credencial de autenticação.");
		}
		log.info("[finaliza] Usuario - validaUsuario");
	}

	public void verificaPausaCurta() {
		if (this.status.equals(StatusUsuario.PAUSA_CURTA)) {
			throw  APIException.build(HttpStatus.BAD_REQUEST, "Usuário já esta em PAUSA CURTA!");
		}
	}

	public void mudaStatusPausaCurta() {
		this.status = StatusUsuario.PAUSA_CURTA;
	}
}
