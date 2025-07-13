package com.gustavoventieri.framework.driver.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.gustavoventieri.domain.entity.FriendshipDomain;
import org.gustavoventieri.domain.enums.RequestStatus;
import org.gustavoventieri.domain.exception.Conflict;
import org.gustavoventieri.domain.exception.InternalServerError;
import org.gustavoventieri.domain.exception.InvalidData;
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
    public List<FriendshipDomain> getFriendShipByStatus(UUID userId, RequestStatus status, boolean sent) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getChatRequestsByStatus'");
    }

    @Override
    public FriendshipDomain save(final FriendshipDomain friendshipDomain) {
        log.debug("Salvando amizade: {}", friendshipDomain);
        try {
            final Friendship friendshipSaved = friendshipOrm.save(FriendshipMapper.toEntityComplete(friendshipDomain));
            return FriendshipMapper.toDomainBasic(friendshipSaved);
        } catch (DataIntegrityViolationException e) {
            log.error("Conflito ao salvar amizade: {}", friendshipDomain, e);
            throw new Conflict("Conflict detected while saving user", e);
        } catch (IllegalArgumentException e) {
            log.error("Dados inválidos para salvar amizade: {}", friendshipDomain, e);
            throw new InvalidData("Invalid data provided for saving user", e);
        } catch (Exception e) {
            log.error("Erro interno ao salvar amizade: {}", friendshipDomain, e);
            throw new InternalServerError("Internal error occurred while saving user", e);
        }
    }

    @Override
    public Optional<FriendshipDomain> updateStatus(UUID requestId, RequestStatus status) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateStatus'");
    }

    @Override
    public Optional<FriendshipDomain> deleteById(UUID requestId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

    @Override
    public Optional<FriendshipDomain> findById(UUID requestId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public Optional<FriendshipDomain> findExisting(UUID userId, UUID friendId) {
        log.debug("Verificando existência de amizade entre {} e {}", userId, friendId);

        Optional<Friendship> friendship = friendshipOrm.findByUser_IdAndFriend_Id(userId, friendId);

        if (friendship.isEmpty()) {
            friendship = friendshipOrm.findByUser_IdAndFriend_Id(friendId, userId);
        }

        return friendship.map(FriendshipMapper::toDomainBasic);
    }

    @Override
    public Optional<FriendshipDomain> findAcceptedBetweenUsers(UUID userId, UUID friendId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAcceptedBetweenUsers'");
    }

}
