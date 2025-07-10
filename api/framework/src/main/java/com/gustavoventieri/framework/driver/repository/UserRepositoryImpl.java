package com.gustavoventieri.framework.driver.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.gustavoventieri.domain.entity.UserDomain;
import org.gustavoventieri.domain.exception.BadRequest;
import org.gustavoventieri.domain.exception.Conflict;
import org.gustavoventieri.domain.exception.InternalServerError;
import org.gustavoventieri.domain.exception.InvalidData;
import org.gustavoventieri.domain.exception.NotFound;
import org.gustavoventieri.domain.repository.UserRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import com.gustavoventieri.framework.adapter.mapper.UserMapper;
import com.gustavoventieri.framework.driver.repository.client.UserRepositoryOrm;
import com.gustavoventieri.framework.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementação do repositório de usuários, responsável por operações CRUD
 * envolvendo a entidade UserDomain utilizando UserRepositoryOrm.
 */
@RequiredArgsConstructor
@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {


    private final UserRepositoryOrm userRepositoryOrm;

    /**
     * Busca um usuário pelo email.
     *
     * @param email Email do usuário a ser buscado
     * @return Optional contendo o usuário, se encontrado
     * @throws BadRequest se o email for inválido
     * @throws InternalServerError para erros internos inesperados
     */
    @Override
    public Optional<UserDomain> findByEmail(final String email) {
        log.debug("Buscando usuário pelo email: {}", email);
        try {
            final Optional<User> userOpt = userRepositoryOrm.findByEmail(email);
            return userOpt.map(UserMapper::toDomainBasic);
        } catch (IllegalArgumentException e) {
            log.error("Email inválido fornecido: {}", email, e);
            throw new BadRequest("Invalid email provided", e);
        } catch (Exception e) {
            log.error("Erro interno ao buscar usuário pelo email: {}", email, e);
            throw new InternalServerError("Internal error occurred while finding user by email", e);
        }
    }

    /**
     * Busca um usuário pelo username.
     *
     * @param username Username do usuário a ser buscado
     * @return Optional contendo o usuário, se encontrado
     * @throws BadRequest se o username for inválido
     * @throws InternalServerError para erros internos inesperados
     */
    @Override
    public Optional<UserDomain> findByUsername(final String username) {
        log.debug("Buscando usuário pelo username: {}", username);
        try {
            final Optional<User> userOpt = userRepositoryOrm.findByUsername(username);
            return userOpt.map(UserMapper::toDomainBasic);
        } catch (IllegalArgumentException e) {
            log.error("Username inválido fornecido: {}", username, e);
            throw new BadRequest("Invalid username provided", e);
        } catch (Exception e) {
            log.error("Erro interno ao buscar usuário pelo username: {}", username, e);
            throw new InternalServerError("Internal error occurred while finding user by username", e);
        }
    }

    /**
     * Busca um usuário pelo ID.
     *
     * @param userId UUID do usuário a ser buscado
     * @return Optional contendo o usuário, se encontrado
     * @throws BadRequest se o ID for inválido
     * @throws InternalServerError para erros internos inesperados
     */
    @Override
    public Optional<UserDomain> findById(final UUID userId) {
        log.debug("Buscando usuário pelo ID: {}", userId);
        try {
            final Optional<User> userOpt = userRepositoryOrm.findById(userId);
            return userOpt.map(UserMapper::toDomainBasic);
        } catch (IllegalArgumentException e) {
            log.error("ID inválido fornecido: {}", userId, e);
            throw new BadRequest("Invalid user ID provided", e);
        } catch (Exception e) {
            log.error("Erro interno ao buscar usuário pelo ID: {}", userId, e);
            throw new InternalServerError("Internal error occurred while finding user by ID", e);
        }
    }

    /**
     * Salva um novo usuário no banco de dados.
     *
     * @param user Usuário a ser salvo
     * @return Usuário salvo convertido para UserDomain
     * @throws Conflict se houver conflito de dados (ex: duplicidade)
     * @throws InvalidData se os dados do usuário forem inválidos
     * @throws InternalServerError para erros internos inesperados
     */
    @Override
    public UserDomain save(final UserDomain user) {
        log.debug("Salvando usuário: {}", user);
        try {
            final User userSaved = userRepositoryOrm.save(UserMapper.toEntityComplete(user));
            return UserMapper.toDomainBasic(userSaved);
        } catch (DataIntegrityViolationException e) {
            log.error("Conflito ao salvar usuário: {}", user, e);
            throw new Conflict("Conflict detected while saving user", e);
        } catch (IllegalArgumentException e) {
            log.error("Dados inválidos para salvar usuário: {}", user, e);
            throw new InvalidData("Invalid data provided for saving user", e);
        } catch (Exception e) {
            log.error("Erro interno ao salvar usuário: {}", user, e);
            throw new InternalServerError("Internal error occurred while saving user", e);
        }
    }

    /**
     * Atualiza a senha do usuário identificado pelo email.
     *
     * @param email Email do usuário
     * @param password Nova senha a ser definida
     * @throws NotFound se o usuário com o email especificado não for encontrado
     * @throws BadRequest se os dados fornecidos forem inválidos
     * @throws InternalServerError para erros internos inesperados
     */
    @Override
    public void updatePasswordByEmail(final String email, final String password) {
        log.debug("Atualizando senha para usuário com email: {}", email);
        try {
            userRepositoryOrm.updatePasswordByEmail(email, password);
        } catch (EmptyResultDataAccessException e) {
            log.error("Usuário não encontrado para email: {}", email, e);
            throw new NotFound("User with the specified email not found", e);
        } catch (IllegalArgumentException e) {
            log.error("Dados inválidos para atualização de senha. Email: {}, password: {}", email, password, e);
            throw new BadRequest("Invalid email or password provided", e);
        } catch (Exception e) {
            log.error("Erro interno ao atualizar senha para email: {}", email, e);
            throw new InternalServerError("Internal error occurred while updating password", e);
        }
    }

    /**
     * Busca usuários cujo username contenha o termo de busca e que não seja o usuário atual.
     *
     * @param searchTerm Termo para busca aproximada no username
     * @param currentUserId UUID do usuário atual (para excluir da busca)
     * @return Lista de usuários encontrados
     * @throws BadRequest se o termo de busca for inválido
     * @throws InternalServerError para erros internos inesperados
     */
    @Override
    public List<UserDomain> searchByApproximateUsername(final String searchTerm, final UUID currentUserId) {
        log.debug("Buscando usuários com username aproximado a '{}' excluindo usuário com ID: {}", searchTerm, currentUserId);
        try {
            final List<User> users = userRepositoryOrm.findByUsernameContainingAndIdNot(searchTerm, currentUserId);
            return users.stream()
                    .map(UserMapper::toDomainBasic)
                    .toList();
        } catch (IllegalArgumentException e) {
            log.error("Termo de busca inválido: {}", searchTerm, e);
            throw new BadRequest("Invalid search term provided", e);
        } catch (Exception e) {
            log.error("Erro interno ao buscar usuários pelo termo: {}", searchTerm, e);
            throw new InternalServerError("Internal error occurred while searching users", e);
        }
    }

}
