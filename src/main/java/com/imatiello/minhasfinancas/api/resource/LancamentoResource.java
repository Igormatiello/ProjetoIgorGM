package com.imatiello.minhasfinancas.api.resource;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imatiello.minhasfinancas.api.dto.AtualizaStatusDTO;
import com.imatiello.minhasfinancas.api.dto.LancamentoDTO;
import com.imatiello.minhasfinancas.exception.RegraNegocioException;
import com.imatiello.minhasfinancas.model.entity.Lancamento;
import com.imatiello.minhasfinancas.model.entity.Usuario;
import com.imatiello.minhasfinancas.model.enums.StatusLancamento;
import com.imatiello.minhasfinancas.model.enums.TipoLancamento;
import com.imatiello.minhasfinancas.service.LancamentoService;
import com.imatiello.minhasfinancas.service.UsuarioService;

import antlr.collections.List;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {

	private final LancamentoService service;
	private final UsuarioService usuarioService;
	
	
	@GetMapping
	public ResponseEntity buscar(
			@RequestParam (value = "descrição", required = false) String descricao,
			@RequestParam (value = "mês", required = false) Integer mes,
			@RequestParam (value = "ano", required = false) Integer ano,
			@RequestParam ( "usuario") Long idUsuario
				) {
		
		Lancamento lancamentoFiltro=   new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setAno(ano);
		lancamentoFiltro.setMes(mes);
		
		
		Optional<Usuario> usuario= usuarioService.obterPorId(idUsuario);
		if (usuario.isPresent()) {
			
			return ResponseEntity.badRequest().body
			("Não foi possível realizar a consulta. Usuário não encontrado para o Id informado.");
		}else {
			lancamentoFiltro.setUsuario(usuario.get());
		}
		
		List lancamentos= (List) service.buscar(lancamentoFiltro); //TODO mudança no original 
		return ResponseEntity.ok(lancamentos);
	}
	
	
	
	
	
	
	
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
		
		try {
			Lancamento entidade= converter(dto);
			
			entidade= service.salvar(entidade);
			return new ResponseEntity(entidade, HttpStatus.CREATED);
			
		}catch(RegraNegocioException e) {
			
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
		@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable ("id") Long id, @RequestBody LancamentoDTO dto) {
		
		return service.obterPorId(id).map(  entity ->{
			try {
				Lancamento lancamento= converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);		
				
				
			}catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
			
		
		}).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na Base de Dados.",HttpStatus.BAD_REQUEST) );
		
	}
		@PutMapping("{id}/atualiza-status")
		public ResponseEntity atualizarStatus(@PathVariable ("id") Long id, @RequestBody AtualizaStatusDTO dto   ) {
			
			return service.obterPorId(id).map(  entity ->{
				
				StatusLancamento statusLancamento = StatusLancamento.valueOf(dto.getStatus());
				
				if (statusLancamento== null) {
					
					return ResponseEntity.badRequest()
							.body("Não foi possível atualizar o status de lançamento, por favor envie um status válido.");
				}
				try {
					entity.setStatus(statusLancamento);
					service.atualizar(entity);
					return ResponseEntity.ok(entity);	
				}catch (RegraNegocioException e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
				
			}).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na Base de Dados.",HttpStatus.BAD_REQUEST) );
			
		}
		
		
		
		
		
		
		@DeleteMapping("{id}")
		public ResponseEntity deletar (@PathVariable ("id") Long id){
			
			return service.obterPorId(id).map(  entity ->{
				service.deletar(entity);
				return new ResponseEntity(HttpStatus.NO_CONTENT);
			}).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na Base de Dados."
					,HttpStatus.BAD_REQUEST) );
			
			
		}
	
	
	private Lancamento converter (LancamentoDTO dto) {
		
		Lancamento lancamento = new Lancamento();
		
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setId(dto.getId());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		
		Usuario usuario = usuarioService.obterPorId(dto.getUsuario())
		.orElseThrow(  ()-> new RegraNegocioException("Usuário não encontrado para o Id informado.")  );
		
		lancamento.setUsuario(usuario);
		
		if (dto.getTipo()!= null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));	
		}
		
		
		
		if (dto.getStatus()!= null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));	
		}
		
		
		
		return lancamento;
	}
	
	
	
}