
package com.gustavoventieri.framework.adapter.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gustavoventieri.domain.dto.response.PrivateChatResponseDTO;
import org.gustavoventieri.domain.service.ChatService;
import org.gustavoventieri.domain.utils.JWTUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gustavoventieri.framework.adapter.dto.request.chat.CreatePrivateChatRequestDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsável por gerenciar as operações relacionadas a chats.
 */
@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final JWTUtils jwtUtils;

    /**
     * Cria um novo chat privado entre o usuário autenticado e um usuário alvo.
     *
     * @param request     O DTO contendo o nome de usuário do alvo.
     * @param httpRequest A requisição HTTP para extrair o ID do usuário do cookie.
     * @return Uma resposta com status 201 (Created) em caso de sucesso.
     */
    @PostMapping("/private/create")
    public ResponseEntity<String> createPrivateChat(@Valid @RequestBody CreatePrivateChatRequestDTO request,
            HttpServletRequest httpRequest) {
        final UUID currentUserId = jwtUtils.getUserIdFromCookie(httpRequest);
        log.info("Usuário {} iniciando a criação de um chat privado com {}", currentUserId, request.targetUsername());

        chatService.createPrivateChat(currentUserId, request.targetUsername());

        log.info("Chat privado criado com sucesso para o usuário {}", currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Chat Created");
    }

    /**
     * Busca todos os chats privados do usuário autenticado.
     *
     * @param httpRequest A requisição HTTP para extrair o ID do usuário do cookie.
     * @return Um mapa contendo a lista de chats do usuário.
     */
    @GetMapping("/private/getAll")
    public ResponseEntity<Map<String, List<PrivateChatResponseDTO>>> getPrivateChats(HttpServletRequest httpRequest) {
        final UUID userId = jwtUtils.getUserIdFromCookie(httpRequest);
        log.info("Buscando chats privados para o usuário {}", userId);

        final Map<String, List<PrivateChatResponseDTO>> chats = chatService.getPrivateChats(userId);

        log.info("Busca de chats para o usuário {} concluída", userId);
        return ResponseEntity.status(HttpStatus.OK).body(chats);
    }

    /**
     * Busca os detalhes de um chat específico pelo seu ID.
     *
     * @param chatId      O ID do chat a ser buscado.
     * @param httpRequest A requisição HTTP (usada para validação de permissões, se
     *                    necessário).
     * @return Um mapa contendo os detalhes do chat.
     */
    @GetMapping("/get/{chatId}")
    public ResponseEntity<Map<String, Object>> getChatById(@PathVariable UUID chatId, HttpServletRequest httpRequest) {
        // Opcional: validar se o usuário logado pertence a este chat antes de retornar
        // as informações.
        final UUID userId = jwtUtils.getUserIdFromCookie(httpRequest);
        log.info("Usuário {} buscando detalhes do chat {}", userId, chatId);

        final Map<String, Object> chatDetails = chatService.getChatById(chatId);

        log.info("Detalhes do chat {} encontrados", chatId);
        return ResponseEntity.ok(chatDetails);
    }

    /**
     * Deleta um chat privado.
     *
     * @param chatId      O ID do chat a ser deletado.
     * @param httpRequest A requisição HTTP (usada para validação de permissões, se
     *                    necessário).
     * @return Uma mensagem de confirmação de exclusão.
     */
    @DeleteMapping("/private/delete/{chatId}")
    public ResponseEntity<Map<String, String>> deletePrivateChat(@PathVariable UUID chatId,
            HttpServletRequest httpRequest) {
        // Opcional: Adicionar lógica para garantir que apenas um participante pode
        // deletar o chat.
        final UUID userId = jwtUtils.getUserIdFromCookie(httpRequest);
        log.info("Usuário {} tentando deletar o chat {}", userId, chatId);

        final Map<String, String> response = chatService.deletePrivateChat(chatId);

        log.info("Chat {} deletado com sucesso pelo usuário {}", chatId, userId);
        return ResponseEntity.ok(response);
    }

}