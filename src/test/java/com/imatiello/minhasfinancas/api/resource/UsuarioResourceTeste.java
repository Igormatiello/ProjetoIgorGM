package com.imatiello.minhasfinancas.api.resource;

import java.awt.PageAttributes.MediaType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imatiello.minhasfinancas.api.dto.UsuarioDTO;
import com.imatiello.minhasfinancas.exception.ErroAutenticacao;
import com.imatiello.minhasfinancas.exception.RegraNegocioException;
import com.imatiello.minhasfinancas.model.entity.Usuario;
import com.imatiello.minhasfinancas.service.LancamentoService;
import com.imatiello.minhasfinancas.service.UsuarioService;

@RunWith(SpringRunner.class)
@ActiveProfiles("teste")
@WebMvcTest(controllers = UsuarioResource.class)
@AutoConfigureMockMvc
public class UsuarioResourceTeste {

	
	
	static final String API="/api/usuarios";
	static final org.springframework.http.MediaType JSON = org.springframework.http.MediaType.APPLICATION_JSON;
	@Autowired
	MockMvc mvc;
	
	@MockBean
	UsuarioService service;
	
	@MockBean
	LancamentoService lancamentoService;
	
	@Test
	public void deveAutenticarUmUsuario() throws Exception{
		//cenario
		String email= "usuario@email.com";
		String senha= "123";
		
		UsuarioDTO dto= UsuarioDTO.builder()
						.email(email)
						.senha(senha)
						.build();
	
	
		Mockito.when(service.autenticar(email, senha)).thenThrow(ErroAutenticacao.class);
		
		
		String json= new ObjectMapper().writeValueAsString(dto);
	
		
		//execução e verificação
		
		 MockHttpServletRequestBuilder request= MockMvcRequestBuilders
												.post(API.concat("/autenticar"))		
												.accept(JSON)
												.contentType(JSON)
												.content(json);
		 
		 
		 mvc.perform(request)
		 .andExpect(MockMvcResultMatchers.status().isBadRequest()  );
												
	}
	
	@Test
	public void deveRetornarBadRequestAoObterErroDeAutenticacao() throws Exception{
		//cenario
		String email= "usuario@email.com";
		String senha= "123";
		
		UsuarioDTO dto= UsuarioDTO.builder()
						.email(email)
						.senha(senha)
						.build();
		
		Usuario usuario = Usuario.builder()
						  .id(1l)
						  .email(email)
						  .senha(senha)
						  .build();
	
		Mockito.when(service.autenticar(email, senha)).thenReturn(usuario);
		
		
		String json= new ObjectMapper().writeValueAsString(dto);
	
		
		//execução e verificação
		
		 MockHttpServletRequestBuilder request= MockMvcRequestBuilders
												.post(API.concat("/autenticar"))		
												.accept(JSON)
												.contentType(JSON)
												.content(json);
		 
		 
		 mvc.perform(request)
		 .andExpect(MockMvcResultMatchers.status().isOk()  )
		 .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId())   )
		 .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome())   )
		 .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail())   );
												
	}
	
	@Test
	public void deveCriarUmNovoUsuario() throws Exception{
		//cenario
		String email= "usuario@email.com";
		String senha= "123";
		
		UsuarioDTO dto= UsuarioDTO.builder()
						.email("usuario@email.com")
						.senha(senha)
						.build();
		Usuario usuario = Usuario.builder()
				  .id(1l)
				  .email(email)
				  .senha(senha)
				  .build();
	
		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		
		String json= new ObjectMapper().writeValueAsString(dto);
	
		
		//execução e verificação
		
		 MockHttpServletRequestBuilder request= MockMvcRequestBuilders
												.post(API)		
												.accept(JSON)
												.contentType(JSON)
												.content(json);
		 
		 
		 mvc.perform(request)
		 .andExpect(MockMvcResultMatchers.status().isCreated()  )
		 .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId())   )
		 .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome())   )
		 .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail())   );
												
	}
	
	
	@Test
	public void deveCRetornarBadRequestAoTentarCriarUmUsuarioInvalido() throws Exception{
		//cenario
		String email= "usuario@email.com";
		String senha= "123";
		
		UsuarioDTO dto= UsuarioDTO.builder()
						.email("usuario@email.com")
						.senha(senha)
						.build();
		
	
		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);
		
		
		String json= new ObjectMapper().writeValueAsString(dto);
	
		
		//execução e verificação
		
		 MockHttpServletRequestBuilder request= MockMvcRequestBuilders
												.post(API)		
												.accept(JSON)
												.contentType(JSON)
												.content(json);
		 
		 
		 mvc.perform(request)
		 .andExpect(MockMvcResultMatchers.status().isBadRequest());
												
	}
	
	
}
