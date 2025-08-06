package com.gustavoventieri.framework.driver.repository.client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gustavoventieri.framework.entity.Chat;

/**
 * Interface de repositório do Spring Data JPA para a entidade {@link Chat}.
 * Fornece métodos CRUD básicos e queries customizadas para manipulação de dados
 * de Chat.
 */
@Repository
public interface ChatOrm extends JpaRepository<Chat, UUID> {

    /**
     * Encontra todos os chats nos quais um usuário específico é participante.
     * O Spring Data JPA gera a query navegando pela coleção 'participants' e
     * filtrando pelo 'id' da entidade User.
     *
     * @param userId O ID do usuário.
     * @return Uma lista de chats associados ao usuário.
     */
    List<Chat> findAllByParticipants_Id(UUID userId);

    /**
     * Encontra um chat que contenha exatamente dois participantes específicos (e
     * mais ninguém).
     * Ideal para encontrar chats privados.
     *
     * @param userIds          Uma lista contendo os dois UUIDs dos usuários.
     * @param participantCount O número de participantes esperado (neste caso, 2).
     * @return um Optional contendo o Chat, se encontrado.
     */
    @Query("SELECT c FROM Chat c WHERE SIZE(c.participants) = :participantCount " +
            "AND (SELECT COUNT(p) FROM c.participants p WHERE p.id IN :userIds) = :participantCount")
    Optional<Chat> findPrivateChatByParticipants(
            @Param("userIds") List<UUID> userIds,
            @Param("participantCount") long participantCount);

}