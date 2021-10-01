package com.imatiello.minhasfinancas.model.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.imatiello.minhasfinancas.model.entity.Lancamento;
import com.imatiello.minhasfinancas.model.entity.Usuario;
import com.imatiello.minhasfinancas.model.enums.TipoLancamento;

public interface LancamentoRepository extends JpaRepository< Lancamento, Long>{

	Lancamento save(Lancamento lancamento);

	@Query(value = "select sum(l.valor) from Lancamento l join l.usuario u "
			+ "where u.id = :idUsuario and l.tipo=:tipo group by u")
	BigDecimal obterSaldoPorTipoLancamentoEUsuario
	( @Param("idUsuario") Long idUsuaior, @Param("tipo") TipoLancamento tipo  );

	void delete(Lancamento lancamento);
	
}
