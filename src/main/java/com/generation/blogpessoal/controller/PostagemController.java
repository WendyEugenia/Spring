package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;
import com.generation.blogpessoal.repository.TemaRepository;

import jakarta.validation.Valid;

@RestController		// Define que essa classe será uma Controladora
@RequestMapping("/postagens")	// Define quais urls essa classe irá gerenciar as requisições
@CrossOrigin(origins = "*", allowedHeaders = "*")	// Habilita requisições feitas de qualquer fonte externa 
public class PostagemController{

	@Autowired
	private PostagemRepository postagemRepository;
	/* Através da Injeção de Dep., o Spring cria uma instancia da interface 
	 * PostagemRepository, onde podemos utilizar os seus métodos sem a
	 * necessidade de criar um objeto.
	 * */
	
	@Autowired
	private TemaRepository temaRepository;
	
	@GetMapping // Execute esse método caso o Verbo Http seja o GET
	public ResponseEntity<List<Postagem>> getAll() {
		return ResponseEntity.ok(postagemRepository.findAll());
	}
	
	/* Execute esse método caso a requisição venha pelo endpoint
	 * /postagens/{id}, sendo id uma variavel de caminho e o Verbo Http seja o GET */
	@GetMapping("/{id}")
	public ResponseEntity<Postagem> getById(@PathVariable Long id){	// @PathVariable indica que a variavel ID vem pela url/endpoint
		return postagemRepository.findById(id)	// Faz uma busca por ID para pegar a Postagem
				.map(resp -> ResponseEntity.ok(resp))	// Se a busca retornou diferente de NULL, retorne a postagem 
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());	// Senão, retorna ao usuário o erro Não Encontrado
	}
	
	/* Execute esse método caso a requisição venha pelo endpoint
	 * /postagens/titulo/{titulo}, sendo titulo uma variavel de caminho e o Verbo Http seja o GET */
	@GetMapping("/titulo/{titulo}")
	public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo){	// @PathVariable indica que a variavel titulo vem pela url/endpoint
		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo));	// Executa o método de busca especifica passando como argumento a variavel
	}
	
	@PostMapping // Execute esse método caso o Verbo Http seja o POST
	public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem) { // Valida os campos e indica que o objeto postagem vem pelo corpo da requisição 
		return ResponseEntity.status(HttpStatus.CREATED)	// Retorna ao usuário o status e o objeto salvo
				.body(postagemRepository.save(postagem));	// Salva o objeto no banco
	}
	
	@PutMapping // Execute esse método caso o Verbo Http seja o PUT
	public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem) {
		return postagemRepository.findById(postagem.getId())	// Do objeto que veio na requisição, retire o ID e verifica se existe
				.map(resposta -> ResponseEntity.status(HttpStatus.OK)	// Retorna ao usuário o status e o objeto atualizado 
						.body(postagemRepository.save(postagem)))		// Atualiza o objeto no banco
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());	// // Retorna ao usuário o status Não Encontrado
	}
	
	@ResponseStatus(HttpStatus.NO_CONTENT)	// Indica o status a ser retornado caso o método seja executado com sucesso
	@DeleteMapping("/{id}")					// Execute esse método caso a requisição venha pelo endpoint /postagens/{id}, e o Verbo Http seja o DELETE
	public void delete(@PathVariable Long id) {	// @PathVariable indica que a variavel ID vem pela url/endpoint
		Optional<Postagem> postagem = postagemRepository.findById(id);	// Do ID que veio na url, faça uma busca para encontrar a postagem
		
		if(postagem.isEmpty())	// Se o objeto retornou NULL, lança a exceção e retorna ao usuário
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		postagemRepository.deleteById(id); // Senão, apaga do banco de dados
	}

	public TemaRepository getTemaRepository() {
		return temaRepository;
	}

	public void setTemaRepository(TemaRepository temaRepository) {
		this.temaRepository = temaRepository;
	}
	
}