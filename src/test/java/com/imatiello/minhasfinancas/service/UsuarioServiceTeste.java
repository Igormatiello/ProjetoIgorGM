package com.imatiello.minhasfinancas.service;

import java.util.Optional;

import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.imatiello.minhasfinancas.exception.ErroAutenticacao;
import com.imatiello.minhasfinancas.exception.RegraNegocioException;
import com.imatiello.minhasfinancas.model.entity.Usuario;
import com.imatiello.minhasfinancas.model.repository.UsuarioRepository;
import com.imatiello.minhasfinancas.service.impl.UsuarioServiceImpl;

import net.bytebuddy.dynamic.scaffold.TypeInitializer.None;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTeste {

	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
	
	
	
	@Test(expected= Test.None.class)
	public void deveSalvarUmUsuario() {
		//cenario
		
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder().id(1l).nome("nome").email("email@emai.com").senha("senha").build();
		
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		
		//ação
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		
		//verificação
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@emai.com");// TODO problemas
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
	}
	
	@Test(expected = RegraNegocioException.class)
	public void naoDeveCadastrarUmUsuarioComEmailJaCadatrado() {
		
		//cenario
		String email= "email@email.com";
		Usuario usuario= Usuario.builder().email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		
		//Ação
		service.salvarUsuario(usuario);	
		
		
		//verificação
		Mockito.verify(repository, Mockito.never()).save(usuario);
	}
	
	
	
	@Test(expected= Test.None.class)
	public void deveAutenticarUmUsuarioComSucesso() {
		
		//cenario
		String email= "email@email.com";
		String senha= "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		
		//ação
		Usuario result = service.autenticar(email, senha);
		
		
		//verificação
		
		Assertions.assertThat(result).isNotNull();
		
	}
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		
		//cenario
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		//ação
		 Throwable exception = Assertions.catchThrowable ( () ->  service.autenticar("email@email.com", "senha"));
		
		//verificação
		 Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuario não encontrado para o email informado.");
	}
	@Test
	public void deveLancarErroQuandoNaoSenhaNaoBater() {
		//cenario
		String senha="senha";
		Usuario usuario = Usuario.builder().email("email@emai.com").senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		//ação
		 Throwable exception = Assertions.catchThrowable ( () -> service.autenticar("email@email.com", "123"));
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha invalida.");
		
	}
	
	
	@Test(expected=Test.None.class)
	public void deveValidarEmail() {
		//cenario
		
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		
		
		
		//ação
		service.validarEmail("email@email.com");
		
	}
	
	@Test(expected = RegraNegocioException.class)
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		//cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//ação
		service.validarEmail("email@email.com");
		
		
	}
}
