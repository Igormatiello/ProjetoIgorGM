package com.imatiello.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.imatiello.minhasfinancas.exception.RegraNegocioException;
import com.imatiello.minhasfinancas.model.entity.Lancamento;
import com.imatiello.minhasfinancas.model.entity.Usuario;
import com.imatiello.minhasfinancas.model.enums.StatusLancamento;
import com.imatiello.minhasfinancas.model.repository.LancamentoRepository;
import com.imatiello.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.imatiello.minhasfinancas.service.impl.LancamentoServiceImpl;

import junit.framework.Assert;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")




public class LancamentoServiceTest {

	
	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	
	@Test
	public void deveSalvarUmLancamento() {
		//cenario
		Lancamento lancamentoASalvar= LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo= LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		
		//execução
		Lancamento lancamento= service.salvar(lancamentoASalvar);
		
		
		//verificação
		
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
		
		
	}
	
	
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		
		//cenario
		Lancamento lancamentoASalvar= LancamentoRepositoryTest.criarLancamento();
		
				
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);
		
		//execução e verificação
				
		Assertions.catchThrowableOfType(() ->service.salvar(lancamentoASalvar), RegraNegocioException.class);
		
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
		
		
	}
	
	
	
	@Test
	public void deveAtualizarUmLancamento() {
		//cenario
		
		Lancamento lancamentoSalvo= LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		
		//execução
		service.atualizar(lancamentoSalvo);
		
		
		//verificação
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
		
		
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		
		//cenario
		Lancamento lancamento= LancamentoRepositoryTest.criarLancamento();
		
				
		
		//execução e verificação
				
		Assertions.catchThrowableOfType(() ->service.atualizar(lancamento),NullPointerException.class);
		
		Mockito.verify(repository, Mockito.never()).save(lancamento);
		
		
	}
	
	
	@Test
	public void deveDeletarUmLancamento() {
		
		//cenario
		Lancamento lancamento= LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		//execução
		service.deletar(lancamento);
		
		//verificação
		
		Mockito.verify(repository).delete(lancamento);
		
	}
	
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		

		//cenario
		Lancamento lancamento= LancamentoRepositoryTest.criarLancamento();
		
		
		//execução
		Assertions.catchThrowableOfType(() ->service.deletar(lancamento),NullPointerException.class);
		
		//verificação
		
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
		
	}
	
	
	@Test
	public void deveFiltrarLancamentos() {
		
		//cenario
		Lancamento lancamento= LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
	List<Lancamento> lista = Arrays.asList(lancamento);
	
	Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
	
	
	//execução
	
	List<Lancamento> resultado = service.buscar(lancamento);
	
	//verificações
	
	Assertions.assertThat(resultado)
	.isNotEmpty()
	.hasSize(1)
	.contains(lancamento);
	
	
	
	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		
		//cenario
		Lancamento lancamento= LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento statusNovo= StatusLancamento.EFETIVADO;
		
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
		
		
		//execução
		
		service.atulizarStatus(lancamento, statusNovo);
		
		//verificações
		
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(statusNovo);
		
		Mockito.verify(service).atualizar(lancamento);
		
		
		
	}
	
	
	@Test
	public void deveObterUmLancamentoPorID() {
		//cenário
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		//execucao
		Optional<Lancamento> resultado =  service.obterPorId(id);
		
		//verificacao
		Assertions.assertThat(resultado.isPresent());
	}
	
	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
		
		//cenario
		
		Long id =1l;
		
		Lancamento lancamento= LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		
		//execução
		Optional<Lancamento> resultado=	service.obterPorId(id);
		
		
		//verificações
		
		Assertions.assertThat(resultado.isPresent()).isFalse();
		
	}
	
	
	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		
		Lancamento lancamento= new Lancamento();
		
		Throwable erro=   Assertions.catchThrowable(  ()-> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe uma Descrição válida.");
		
		lancamento.setDescricao("");
		
		 erro=   Assertions.catchThrowable(  ()-> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe uma Descrição válida.");
		
		
		
		lancamento.setDescricao("salario");
		
		 erro=   Assertions.catchThrowable(  ()-> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe um Mês válido.");
		
		lancamento.setAno(0);
		
		erro=   Assertions.catchThrowable(  ()-> service.validar(lancamento) );
			Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
			.hasMessage("Informe um Mês válido.");
			
			lancamento.setAno(13);
		
		erro=   Assertions.catchThrowable(  ()-> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe um Mês válido.");
		
		
		lancamento.setMes(1);		
		
		 erro=   Assertions.catchThrowable(  ()-> service.validar(lancamento) );
			Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
			.hasMessage("Informe um Ano válido.");
			
			lancamento.setAno(202);
			
			erro=   Assertions.catchThrowable(  ()-> service.validar(lancamento) );
			Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
			.hasMessage("Informe um Ano válido.");
			
			
			lancamento.setAno(2020);
			
			erro=   Assertions.catchThrowable(  ()-> service.validar(lancamento) );
			Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
			.hasMessage("Informe um Usuário.");
			
			lancamento.setUsuario(new Usuario());
			
			
			erro=   Assertions.catchThrowable(  ()-> service.validar(lancamento) );
			Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
			.hasMessage("Informe um Usuário.");
			
			
			lancamento.getUsuario().setId(1l);
			
			erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
			Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
			.hasMessage("Informe um Valor Válido.");
			
			lancamento.setValor(BigDecimal.ZERO);
			
			erro=   Assertions.catchThrowable(  ()-> service.validar(lancamento) );
			Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
			.hasMessage("Informe um Valor Válido.");
			
			lancamento.setValor(BigDecimal.valueOf(1));
			
			erro=   Assertions.catchThrowable(  ()-> service.validar(lancamento) );
			Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
			.hasMessage("Informe um Tipo de Lançamento.");
			
			
	}
	
	
	
}
