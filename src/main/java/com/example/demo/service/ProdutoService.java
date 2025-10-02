package com.example.demo.service;

import com.example.demo.model.Produto;
import com.example.demo.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    // Criar um novo produto
    public Produto criarProduto(Produto produto) {
        return produtoRepository.save(produto);
    }

    // Buscar todos os produtos
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    // Buscar produto por ID
    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    // Buscar produto por nome exato
    public Optional<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNome(nome);
    }

    // Buscar produtos por categoria
    public List<Produto> buscarPorCategoria(String categoria) {
        return produtoRepository.findByCategoria(categoria);
    }

    // Buscar produtos por nome (contendo o texto)
    public List<Produto> buscarPorNomeContendo(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    // Buscar produtos perecíveis ou não perecíveis
    public List<Produto> buscarPorPerecibilidade(Boolean perecivel) {
        return produtoRepository.findByPerecivel(perecivel);
    }

    // Atualizar um produto
    public Produto atualizarProduto(Long id, Produto produtoAtualizado) {
        return produtoRepository.findById(id)
                .map(produto -> {
                    produto.setNome(produtoAtualizado.getNome());
                    produto.setCategoria(produtoAtualizado.getCategoria());
                    produto.setUnidadeMedida(produtoAtualizado.getUnidadeMedida());
                    produto.setPerecivel(produtoAtualizado.getPerecivel());
                    return produtoRepository.save(produto);
                })
                .orElseGet(() -> {
                    produtoAtualizado.setId(id);
                    return produtoRepository.save(produtoAtualizado);
                });
    }

    // Deletar um produto
    public void deletarProduto(Long id) {
        produtoRepository.deleteById(id);
    }

    // Verificar se um produto existe
    public boolean existePorId(Long id) {
        return produtoRepository.existsById(id);
    }

    // Verificar se produto com nome já existe
    public boolean existePorNome(String nome) {
        return produtoRepository.existsByNome(nome);
    }
}