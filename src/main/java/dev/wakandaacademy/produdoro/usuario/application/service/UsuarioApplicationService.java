package dev.wakandaacademy.produdoro.usuario.application.service;

import javax.validation.Valid;

import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import dev.wakandaacademy.produdoro.credencial.application.service.CredencialService;
import dev.wakandaacademy.produdoro.pomodoro.application.service.PomodoroService;
import dev.wakandaacademy.produdoro.usuario.application.api.UsuarioCriadoResponse;
import dev.wakandaacademy.produdoro.usuario.application.api.UsuarioNovoRequest;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class UsuarioApplicationService implements UsuarioService {
	private final PomodoroService pomodoroService;
	private final CredencialService credencialService;
	private final UsuarioRepository usuarioRepository;

	@Override
	public UsuarioCriadoResponse criaNovoUsuario(@Valid UsuarioNovoRequest usuarioNovo) {
		log.info("[inicia] UsuarioApplicationService - criaNovoUsuario");
		var configuracaoPadrao = pomodoroService.getConfiguracaoPadrao();
		credencialService.criaNovaCredencial(usuarioNovo);
		var usuario = new Usuario(usuarioNovo,configuracaoPadrao);
		usuarioRepository.salva(usuario);
		log.info("[finaliza] UsuarioApplicationService - criaNovoUsuario");
		return new UsuarioCriadoResponse(usuario);
	}


	@Override
	public UsuarioCriadoResponse buscaUsuarioPorId(UUID idUsuario) {
		log.info("[inicia] UsuarioApplicationService - buscaUsuarioPorId");
		Usuario usuario = usuarioRepository.buscaUsuarioPorId(idUsuario);
		log.info("[finaliza] UsuarioApplicationService - buscaUsuarioPorId");
		return new UsuarioCriadoResponse(usuario);
	}

	@Override
	public void mudaStatusParaFoco(String email, UUID idUsuario) {
		log.info("[inicia] UsuarioApplicationService - mudaStatusParaFoco");
		Usuario usuario = usuarioRepository.buscaUsuarioPorEmail(email);
		usuarioRepository.buscaUsuarioPorId(idUsuario);
		usuario.validaUsuario(idUsuario);
		usuario.mudaStatusParaFoco(idUsuario);
		usuarioRepository.salva(usuario);
		log.info("[finaliza] UsuarioApplicationService - mudaStatusParaFoco");

	}

	public void mudaStatusParaPausaLonga(String usuario, UUID idUsuario) {
		log.info("[inicia] UsuarioApplicationService - mudaStatusParaPausaLonga");
		Usuario usuarioEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
		usuarioRepository.buscaUsuarioPorId(idUsuario);
		usuarioEmail.mudaStatusParaPausaLonga(idUsuario);
		usuarioRepository.salva(usuarioEmail);
		log.info("[finaliza] UsuarioApplicationService - mudaStatusParaPausaLonga");
	}

	public void mudaStatusParaPausaCurta(String email, UUID idUsuario) {
		log.info("[inicia] UsuarioApplicationService - mudaStatusPausaCurta");
		usuarioRepository.buscaUsuarioPorId(idUsuario);
		Usuario usuarioPausaCurta = usuarioRepository.buscaUsuarioPorEmail(email);
		usuarioPausaCurta.validaUsuario(idUsuario);
		usuarioPausaCurta.verificaPausaCurta();
		usuarioPausaCurta.mudaStatusPausaCurta();
		usuarioRepository.salva(usuarioPausaCurta);
		log.info("[finaliza] UsuarioApplicationService - mudaStatusPausaCurta");
	}
}
