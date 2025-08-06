package com.gustavoventieri.framework.driver.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.gustavoventieri.domain.entity.ChatDomain;
import org.gustavoventieri.domain.exception.InternalServerError;
import org.gustavoventieri.domain.exception.NotFound;
import org.gustavoventieri.domain.repository.ChatRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import com.gustavoventieri.framework.adapter.mapper.ChatMapper;
import com.gustavoventieri.framework.driver.repository.client.ChatOrm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementação da interface {@link ChatRepository} usando a camada ORM.
 * Responsável pelo gerenciamento da persistência e recuperação de entidades de
 * Chat.
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class ChatRepositoryImpl implements ChatRepository {

    private final ChatOrm chatOrm;

    /**
     * Encontra um chat privado existente entre dois usuários.
     *
     * @param userId1 UUID do primeiro usuário.
     * @param userId2 UUID do segundo usuário.
     * @return um Optional contendo o ChatDomain se encontrado, ou vazio caso
     *         contrário.
     * @throws InternalServerError se ocorrer um erro interno.
     */
    @Override
    public Optional<ChatDomain> findExistingChat(final UUID userId1, final UUID userId2) {
        log.debug("Procurando por chat existente entre os usuários {} e {}", userId1, userId2);
        try {
            // Utiliza o método de busca robusto que garante exatamente 2 participantes
            final List<UUID> userIds = List.of(userId1, userId2);
            return chatOrm.findPrivateChatByParticipants(userIds, 2L).map(ChatMapper::toDomainBasic);
        } catch (final Exception e) {
            log.error("Erro interno ao procurar por chat existente entre os usuários {} e {}", userId1, userId2, e);
            throw new InternalServerError("Erro ao procurar por chat existente", e);
        }
    }

    /**
     * Recupera todos os chats de um usuário específico.
     *
     * @param userId UUID do usuário.
     * @return uma lista de ChatDomain.
     * @throws InternalServerError se ocorrer um erro interno.
     */
    @Override
    public List<ChatDomain> findAllByUserId(final UUID userId) {
        log.debug("Buscando todos os chats para o usuário {}", userId);
        try {
            // Corrigido para corresponder à query derivada correta para a entidade User
            return chatOrm.findAllByParticipants_Id(userId)
                    .stream()
                    .map(ChatMapper::toDomainComplete)
                    .collect(Collectors.toList());
        } catch (final Exception e) {
            log.error("Erro interno ao buscar todos os chats para o usuário {}", userId, e);
            throw new InternalServerError("Erro ao buscar os chats do usuário", e);
        }
    }

    /**
     * Salva um novo chat no banco de dados.
     *
     * @param chatDomain o domínio do chat a ser salvo.
     * @throws InternalServerError se ocorrer um erro interno.
     */
    @Override
    public void save(final ChatDomain chatDomain) {
        log.debug("Salvando o chat: {}", chatDomain);
        try {
            chatOrm.save(ChatMapper.toEntityComplete(chatDomain));
        } catch (final Exception e) {
            log.error("Erro interno ao salvar o chat: {}", chatDomain, e);
            throw new InternalServerError("Erro ao salvar o chat", e);
        }
    }

    
    /**
     * Deleta um chat pelo seu ID. Graças ao CascadeType.ALL e à gestão do
     * ManyToMany pelo JPA, este único método deleta o chat, as mensagens
     * associadas e as referências na tabela de junção de participantes.
     *
     * @param chatId UUID do chat.
     * @throws NotFound            se o chat não for encontrado.
     * @throws InternalServerError se ocorrer um erro interno.
     */
    @Override
    public void deleteChatById(final UUID chatId) {
        log.debug("Deletando o chat com ID: {}", chatId);
        try {
            chatOrm.deleteById(chatId);
            log.info("Chat com ID {} deletado com sucesso.", chatId);
        } catch (final EmptyResultDataAccessException e) {
            log.warn("Tentativa de deletar um chat não existente com ID: {}", chatId);
            throw new NotFound("Chat não encontrado para o ID: " + chatId, e);
        } catch (final Exception e) {
            log.error("Erro interno ao deletar o chat com ID: {}", chatId, e);
            throw new InternalServerError("Erro ao deletar o chat", e);
        }
    }

    /**
     * Encontra um chat pelo seu ID, incluindo participantes e mensagens.
     *
     * @param chatId UUID do chat.
     * @return um Optional contendo o ChatDomain com todos os detalhes, se
     *         encontrado.
     * @throws InternalServerError se ocorrer um erro interno.
     */
    @Override
    public Optional<ChatDomain> findByIdWithParticipantsAndMessages(final UUID chatId) {
        log.debug("Buscando chat com ID {} incluindo participantes e mensagens.", chatId);
        try {
            return chatOrm.findById(chatId).map(ChatMapper::toDomainComplete);
        } catch (final Exception e) {
            log.error("Erro interno ao buscar o chat com ID {}:", chatId, e);
            throw new InternalServerError("Erro ao buscar o chat", e);
        }
    }

    // Os métodos deleteMessagesByChatId e deleteParticipantsByChatId foram
    // removidos
    // pois sua funcionalidade agora é tratada automaticamente pelo JPA devido à
    // configuração da entidade Chat. Recomenda-se removê-los também da interface
    // ChatRepository.
}