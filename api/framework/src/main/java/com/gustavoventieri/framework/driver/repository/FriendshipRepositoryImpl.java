package com.gustavoventieri.framework.driver.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.gustavoventieri.domain.entity.FriendshipDomain;
import org.gustavoventieri.domain.enums.RequestStatus;
import org.gustavoventieri.domain.exception.Conflict;
import org.gustavoventieri.domain.exception.InternalServerError;
import org.gustavoventieri.domain.exception.NotFound;
import org.gustavoventieri.domain.repository.FriendshipRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.gustavoventieri.framework.adapter.mapper.FriendshipMapper;
import com.gustavoventieri.framework.driver.repository.client.FriendshipOrm;
import com.gustavoventieri.framework.entity.Friendship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FriendshipRepositoryImpl implements FriendshipRepository {

    private final FriendshipOrm friendshipOrm;

    @Override
    public void save(final FriendshipDomain friendshipDomain) {
        log.debug("Salvando amizade: {}", friendshipDomain);
        try {
            friendshipOrm.save(FriendshipMapper.toEntityComplete(friendshipDomain));
        } catch (DataIntegrityViolationException e) {
            log.error("Conflito ao salvar amizade: {}", friendshipDomain, e);
            throw new Conflict("Conflict detected while saving user", e);
        } catch (Exception e) {
            log.error("Erro interno ao salvar amizade: {}", friendshipDomain, e);
            throw new InternalServerError("Internal error occurred while saving user", e);
        }
    }

    @Override
    public void updateStatus(final UUID requestId, final RequestStatus status) {
        try {
            log.debug("Verificando existência da amizade {}", requestId);

            final Friendship friendship = friendshipOrm.findById(requestId)
                    .orElseThrow(() -> new NotFound("Amizade não encontrada"));

            friendship.setStatus(status);

            friendshipOrm.save(friendship);

            log.info("Status da amizade com ID {} atualizado para {}", requestId, status);

        } catch (final Exception e) {
            log.error("Erro interno ao atualizar o status da amizade {}", requestId, e);
            throw new InternalServerError("Internal error occurred while updating friendship status", e);
        }
    }

    @Override
    public Optional<FriendshipDomain> deleteById(UUID requestId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

    @Override
    public Optional<FriendshipDomain> findById(UUID requestId) {
        try {
            log.debug("Verificando existência da amizade  {}", requestId);

            Optional<Friendship> friendship = friendshipOrm.findById(requestId);

            return friendship.map(FriendshipMapper::toDomainBasic);

        } catch (Exception e) {
            log.error("Erro interno ao buscar a existência de amizade  {}", requestId, e);
            throw new InternalServerError("Internal error occurred while fetching friendship", e);
        }
    }

    @Override
    public Optional<FriendshipDomain> findExisting(UUID userId1, UUID userId2, List<RequestStatus> statuses) {
        try {
            log.debug("Verificando existência de amizade entre {} e {}", userId1, userId2);

            Optional<Friendship> friendship = friendshipOrm.findByUsersAndStatuses(userId1, userId2, statuses);

            return friendship.map(FriendshipMapper::toDomainBasic);

        } catch (Exception e) {
            log.error("Erro interno ao buscar amizade entre: {} {}", userId1, userId2, e);
            throw new InternalServerError("Internal error occurred while fetching friendship", e);
        }
    }

    @Override
    public List<FriendshipDomain> getAllByUserId(UUID userId) {
        try {
            log.debug("Buscando amizades e solicitações do user: {}", userId);
            List<Friendship> friendships = friendshipOrm.findAllByUser_IdOrFriend_IdOrderByCreatedAtDesc(userId,
                    userId);
            return friendships.stream()
                    .map(FriendshipMapper::toDomainComplete)
                    .toList();
        } catch (Exception e) {
            log.error("Erro interno ao buscar amizades do user: {}", userId, e);
            throw new InternalServerError("Internal error occurred while finding friendships", e);
        }
    }

}
