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
 * Implementation of the user repository responsible for CRUD operations
 * involving the UserDomain entity using UserRepositoryOrm.
 */
@RequiredArgsConstructor
@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final UserRepositoryOrm userRepositoryOrm;

    /**
     * Finds a user by email.
     *
     * @param email Email of the user to be searched
     * @return Optional containing the user, if found
     * @throws BadRequest          if the email is invalid
     * @throws InternalServerError for unexpected internal errors
     */
    @Override
    public Optional<UserDomain> findByEmail(final String email) {
        log.debug("Searching for user by email: {}", email);
        try {
            final Optional<User> userOpt = userRepositoryOrm.findByEmail(email);
            return userOpt.map(UserMapper::toDomainBasic);
        } catch (IllegalArgumentException e) {
            log.error("Invalid email provided: {}", email, e);
            throw new BadRequest("Invalid email provided", e);
        } catch (Exception e) {
            log.error("Internal error occurred while searching for user by email: {}", email, e);
            throw new InternalServerError("Internal error occurred while finding user by email", e);
        }
    }

    /**
     * Finds a user by username.
     *
     * @param username Username of the user to be searched
     * @return Optional containing the user, if found
     * @throws BadRequest          if the username is invalid
     * @throws InternalServerError for unexpected internal errors
     */
    @Override
    public Optional<UserDomain> findByUsername(final String username) {
        log.debug("Searching for user by username: {}", username);
        try {
            final Optional<User> userOpt = userRepositoryOrm.findByUsername(username);
            return userOpt.map(UserMapper::toDomainBasic);
        } catch (IllegalArgumentException e) {
            log.error("Invalid username provided: {}", username, e);
            throw new BadRequest("Invalid username provided", e);
        } catch (Exception e) {
            log.error("Internal error occurred while searching for user by username: {}", username, e);
            throw new InternalServerError("Internal error occurred while finding user by username", e);
        }
    }

    /**
     * Finds a user by ID.
     *
     * @param userId UUID of the user to be searched
     * @return Optional containing the user, if found
     * @throws BadRequest          if the ID is invalid
     * @throws InternalServerError for unexpected internal errors
     */
    @Override
    public Optional<UserDomain> findById(final UUID userId) {
        log.debug("Searching for user by ID: {}", userId);
        try {
            final Optional<User> userOpt = userRepositoryOrm.findById(userId);
            return userOpt.map(UserMapper::toDomainBasic);
        } catch (IllegalArgumentException e) {
            log.error("Invalid ID provided: {}", userId, e);
            throw new BadRequest("Invalid user ID provided", e);
        } catch (Exception e) {
            log.error("Internal error occurred while searching for user by ID: {}", userId, e);
            throw new InternalServerError("Internal error occurred while finding user by ID", e);
        }
    }

    /**
     * Saves a new user in the database.
     *
     * @param user User to be saved
     * @return Saved user converted to UserDomain
     * @throws Conflict            if there is a data conflict (e.g., duplication)
     * @throws InvalidData         if the user data is invalid
     * @throws InternalServerError for unexpected internal errors
     */
    @Override
    public UserDomain save(final UserDomain user) {
        log.debug("Saving user: {}", user);
        try {
            final User userSaved = userRepositoryOrm.save(UserMapper.toEntityComplete(user));
            return UserMapper.toDomainBasic(userSaved);
        } catch (DataIntegrityViolationException e) {
            log.error("Conflict while saving user: {}", user, e);
            throw new Conflict("Conflict detected while saving user", e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid data to save user: {}", user, e);
            throw new InvalidData("Invalid data provided for saving user", e);
        } catch (Exception e) {
            log.error("Internal error occurred while saving user: {}", user, e);
            throw new InternalServerError("Internal error occurred while saving user", e);
        }
    }

    /**
     * Updates the password of the user identified by email.
     *
     * @param email    Email of the user
     * @param password New password to be set
     * @throws NotFound            if the user with the specified email is not found
     * @throws BadRequest          if the provided data is invalid
     * @throws InternalServerError for unexpected internal errors
     */
    @Override
    public void updatePasswordByEmail(final String email, final String password) {
        log.debug("Updating password for user with email: {}", email);
        try {
            userRepositoryOrm.updatePasswordByEmail(email, password);
        } catch (EmptyResultDataAccessException e) {
            log.error("User not found for email: {}", email, e);
            throw new NotFound("User with the specified email not found", e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid data for password update. Email: {}, password: {}", email, password, e);
            throw new BadRequest("Invalid email or password provided", e);
        } catch (Exception e) {
            log.error("Internal error occurred while updating password for email: {}", email, e);
            throw new InternalServerError("Internal error occurred while updating password", e);
        }
    }

    /**
     * Searches for users whose username contains the search term and excludes the
     * current user.
     *
     * @param searchTerm    Term for approximate username search
     * @param currentUserId UUID of the current user (to exclude from search)
     * @return List of users found
     * @throws BadRequest          if the search term is invalid
     * @throws InternalServerError for unexpected internal errors
     */
    @Override
    public List<UserDomain> searchByApproximateUsername(final String searchTerm, final UUID currentUserId) {
        log.debug("Searching users with username approximately matching '{}' excluding user with ID: {}", searchTerm,
                currentUserId);
        try {
            final List<User> users = userRepositoryOrm.findByUsernameContainingAndIdNot(searchTerm, currentUserId);
            return users.stream()
                    .map(UserMapper::toDomainBasic)
                    .toList();
        } catch (IllegalArgumentException e) {
            log.error("Invalid search term: {}", searchTerm, e);
            throw new BadRequest("Invalid search term provided", e);
        } catch (Exception e) {
            log.error("Internal error occurred while searching users by term: {}", searchTerm, e);
            throw new InternalServerError("Internal error occurred while searching users", e);
        }
    }

}
