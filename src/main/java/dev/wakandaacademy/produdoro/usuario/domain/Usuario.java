package dev.wakandaacademy.produdoro.usuario.domain;

import java.util.UUID;

import javax.validation.constraints.Email;

import dev.wakandaacademy.produdoro.handler.APIException;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.handler.APIException;
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
@Log4j2
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

	public void mudaStatusParaFoco(UUID idUsuario) {
		log.info("[inicia] Usuario - mudaStatusParaFoco");
		validaUsuario(idUsuario);
		this.status = StatusUsuario.FOCO;
		log.info("[finaliza] Usuario - mudaStatusParaFoco");
	}

	public void validaUsuario(UUID idUsuario) {
		log.info("[inicia] Usuario - validaUsuario");
		if (!this.idUsuario.equals(idUsuario)) {
			log.info("[finaliza] APIException - validaUsuario");
			throw APIException.build(HttpStatus.UNAUTHORIZED,"credencial de autenticação não é válida.");
		}
		log.info("[finaliza] Usuario - validaUsuario");

	}

	public void mudaStatusParaPausaLonga(UUID idUsuario) {
		log.info("[inicia] Usuario - mudaStatusParaPausaLonga");
		validaUsuario(idUsuario);
		this.status = StatusUsuario.PAUSA_LONGA;
		log.info("[finaliza] Usuario - mudaStatusParaPausaLonga");
	}

	public void mudaStatusParaPausaCurta(UUID idUsuario) {
		log.info("[inicia] Usuario - mudaStatusParaPausaCurta");
		validaUsuario(idUsuario);
		this.status = StatusUsuario.PAUSA_CURTA;
		log.info("[finaliza] Usuario - mudaStatusParaPausaCurta");
	}

    public void emailDoUsuario(Usuario usuarioPorEmail) {
		if (!this.getIdUsuario().equals(usuarioPorEmail.getIdUsuario())){
			throw APIException.build(HttpStatus.UNAUTHORIZED,
					"Usuario(a) não autorizado(a) para a requisição solicitada.");
		}
    }

	public void mudaStatusPausaCurta() {
		this.status = StatusUsuario.PAUSA_CURTA;
	}

	public void verificaPausaCurta() {
		if (this.status.equals(StatusUsuario.PAUSA_CURTA)) {
			throw  APIException.build(HttpStatus.BAD_REQUEST, "Usuário já esta em PAUSA CURTA!");
		};
	}

	public void pertenceAoUsuario(Usuario usuarioPorEmail) {
		if (!this.idUsuario.equals(usuarioPorEmail.getIdUsuario())) {
			throw APIException.build(HttpStatus.UNAUTHORIZED, "Usuário(a) não autorizado(a) para a requisição solicitada!");
		}		
	}
}
