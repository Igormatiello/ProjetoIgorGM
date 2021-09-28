package com.imatiello.minhasfinancas.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Entity;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.imatiello.minhasfinancas.model.entity.Lancamento;
import com.imatiello.minhasfinancas.model.enums.StatusLancamento;
import com.imatiello.minhasfinancas.model.enums.TipoLancamento;

import net.bytebuddy.asm.Advice.Local;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRespositoryTest {

	
	@Autowired
	LancamentoRepository repository;
	
	
	@Autowired
	TestEntityManager entityManager;
	
	
	
	@Test
	
	public void deveSalvarUmLancamento() {
		
		Lancamento lancamento= Lancamento.builder()
								.ano(2019)
								.mes(1)
								.descricao("Lançamento qualquer")
								.valor(BigDecimal.valueOf(10))
								.tipo(TipoLancamento.RECEITA)
								.status(StatusLancamento.PENDENTE)
								.dataCadastro(LocalDate.now())
								.build();
		
		lancamento =  repository.save(lancamento);
		Assertions.assertThat(lancamento.getId()).isNotNull();
	}
	
	
	@Test
	
	public void deletarUmLancamento() {
		
		
		
		
		Lancamento lancamento= criarLancamento();
		entityManager.persist(lancamento);
		
		
		lancamento=	entityManager.find(Lancamento.class, lancamento.getId());
		
		
		repository.delete(lancamento);
	
	Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
	Assertions.assertThat(lancamentoInexistente.getId()).isNull();
	}
	
	
	public static Lancamento criarLancamento() {
		return Lancamento.builder()
									.ano(2019)
									.mes(1)
									.descricao("lancamento qualquer")
									.valor(BigDecimal.valueOf(10))
									.tipo(TipoLancamento.RECEITA)
									.status(StatusLancamento.PENDENTE)
									.dataCadastro(LocalDate.now())
									.build();
	}
	
	
	
}
