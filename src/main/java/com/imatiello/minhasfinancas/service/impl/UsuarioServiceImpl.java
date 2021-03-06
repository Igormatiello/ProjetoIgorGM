package com.imatiello.minhasfinancas.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imatiello.minhasfinancas.exception.ErroAutenticacao;
import com.imatiello.minhasfinancas.exception.RegraNegocioException;
import com.imatiello.minhasfinancas.model.entity.Usuario;
import com.imatiello.minhasfinancas.model.repository.UsuarioRepository;
import com.imatiello.minhasfinancas.service.UsuarioService;



@Service
public class UsuarioServiceImpl implements UsuarioService {

	private UsuarioRepository repository;
	
	
	@Autowired
	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		
		Optional<Usuario>  usuario = repository.findByEmail(email);
		
		if (!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuario não encontrado para o email informado.");
		}
		if (!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacao("Senha invalida.");
		}
		
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		// TODO Auto-generated method stub
		boolean existe = repository.existsByEmail(email);
		if (existe) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com esse email.");
		}
	}

	@Override
	public Optional<Usuario> obterPorId(Long id) {
		
		
		
		
	
		
		
		
		return repository.findById(id);
	}

	
	
}
