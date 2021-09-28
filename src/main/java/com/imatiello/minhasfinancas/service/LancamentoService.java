package com.imatiello.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.Optional;

import com.imatiello.minhasfinancas.model.entity.Lancamento;
import com.imatiello.minhasfinancas.model.enums.StatusLancamento;

import antlr.collections.List;

public interface LancamentoService {

	
	Lancamento salvar(Lancamento lancamento);
	
	Lancamento atualizar(Lancamento lancamento);
	
	void deletar(Lancamento lancamento);
	
	java.util.List<Lancamento> buscar(Lancamento lancamentoFiltro);
	
	void atulizarStatus(Lancamento lancamento, StatusLancamento status);
	
	void validar(Lancamento lancamento);
	
	Optional<Lancamento> obterPorId(Long id);
	
	
	BigDecimal obterSaldoPorUsuario(Long id);
}
