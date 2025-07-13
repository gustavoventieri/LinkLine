import { useEffect, useState } from "react";
import {
  Box,
  CircularProgress,
  Typography,
  InputBase,
  List,
  ListItem,
  ListItemAvatar,
  Avatar,
  ListItemText,
  useMediaQuery,
  Theme,
  useTheme,
  IconButton,
  TextField,
  InputAdornment,
} from "@mui/material";
import {
  AttachFile,
  Send,
  MoreVert as MoreVertIcon,
  ArrowBack,
} from "@mui/icons-material";
import SearchIcon from "@mui/icons-material/Search";
import { BaseLayout } from "../../shared/layouts";
import { api } from "../../shared/services";
import { useAppThemeContext } from "../../shared/contexts";
import { useNavigate } from "react-router-dom";

type ChatData = {
  chatId: string;
  createdAt: string;
  participant: {
    chatNameForMember: string;
    avatarUrl?: string;
    userId: string;
  };
};

type Message = {
  id: string;
  senderId: string;
  text: string;
  createdAt: string;
};

export const Chats = () => {
  const { themeName } = useAppThemeContext();
  const theme = useTheme();
  const [chats, setChats] = useState<ChatData[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [selectedChat, setSelectedChat] = useState<ChatData | null>(null);
  const [userId, setUserId] = useState<string | null>(null);
  const [messages, setMessages] = useState<Message[]>([]);
  const [loadingMessages, setLoadingMessages] = useState(false);
  const [newMessage, setNewMessage] = useState("");
  const [showIcon, setShowIcon] = useState<boolean>(true);
  const mdDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("md"));
  const smDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("sm"));
  const navigate = useNavigate();

  useEffect(() => {
    const getAllChats = async () => {
      try {
        setChats([]);
      } catch (err) {
        console.error("Erro ao buscar chats:", err);
        setChats([]);
      } finally {
        setLoading(false);
      }
    };
    getAllChats();
  }, []);

  useEffect(() => {
    const getUserId = async () => {
      try {
        const response = await api.get("/auth/isAuth");
        if (response.data.userId) setUserId(response.data.userId);
      } catch (err) {
        navigate("/login");
        console.error("Erro ao autenticar:", err);
      }
    };
    getUserId();
  }, []);

  const loadMessages = async (chatId: string) => {
    setLoadingMessages(true);
    try {
      setMessages([]);
    } catch (error) {
      console.error("Erro ao carregar mensagens:", error);
      setMessages([]);
    } finally {
      setLoadingMessages(false);
    }
  };

  const handleGoBack = () => {
    setSelectedChat(null);
    setShowIcon(true);
  };

  const handleSelectChat = (chat: ChatData) => {
    setSelectedChat(chat);
    setShowIcon(false);
    loadMessages(chat.chatId);
  };

  const filteredChats = chats.filter((chat) =>
    chat.participant?.chatNameForMember
      ?.toLowerCase()
      .includes(search.toLowerCase())
  );

  const handleSendMessage = async () => {
    if (!newMessage.trim() || !selectedChat) return;

    try {
      const response = await api.post(
        `/private-chats/send/${selectedChat.chatId}`,
        { text: newMessage }
      );

      const sentMessage = response.data.message;
      setMessages((prev) => [...prev, sentMessage]);
      setNewMessage("");
    } catch (err) {
      console.error("Erro ao enviar mensagem:", err);
    }
  };

  return (
    <BaseLayout showIcon={showIcon}>
      <Box display="flex" height="100%" minHeight="100vh">
        {/* Sidebar */}
        {(!mdDown || !selectedChat) && (
          <Box
            width={mdDown ? "100%" : "30%"}
            display="flex"
            flexDirection="column"
            px={2}
            pt={mdDown ? 2 : 4}
          >
            <Box
              sx={{
                p: "12px 4px",
                display: "flex",
                boxShadow: 3,
                alignItems: "center",
                mb: mdDown ? 2 : 5,
                borderRadius: 2,
                backgroundColor: theme.palette.background.paper,
              }}
            >
              <SearchIcon sx={{ ml: 2 }} color="primary" />
              <InputBase
                sx={{ ml: 2 }}
                placeholder="Search"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
            </Box>

            {loading ? (
              <Box display="flex" justifyContent="center" mt={2}>
                <CircularProgress size={24} />
              </Box>
            ) : filteredChats.length === 0 ? (
              <Box
                display="flex"
                justifyContent="center"
                alignItems="center"
                mb={smDown ? 8 : 10}
                flex={1}
              >
                <Typography variant="body1" color="text.secondary">
                  Nenhum chat encontrado :(
                </Typography>
              </Box>
            ) : (
              <List disablePadding>
                {filteredChats.map((chat) => (
                  <ListItem
                    key={chat.chatId}
                    component="button"
                    sx={{
                      width: "100%",
                      height: smDown ? 80 : 85,
                      borderRadius: 3,
                      color: themeName === "light" ? "black" : "primary.text",
                      bgcolor:
                        selectedChat?.chatId === chat.chatId
                          ? "rgba(25, 118, 210, 0.2)"
                          : "transparent",
                      "&:hover": {
                        bgcolor:
                          selectedChat?.chatId === chat.chatId
                            ? "hsla(210, 78.70%, 46.10%, 0.50)"
                            : themeName === "light"
                            ? "rgba(0, 0, 0, 0.04)"
                            : "rgba(255, 255, 255, 0.05)",
                      },
                      border: "none",
                    }}
                    onClick={() => handleSelectChat(chat)}
                    disabled={selectedChat?.chatId === chat.chatId}
                  >
                    <ListItemAvatar>
                      <Avatar
                        sx={{
                          width: smDown ? 50 : 60,
                          height: smDown ? 50 : 60,
                        }}
                        src={chat.participant.avatarUrl}
                      >
                        <Typography
                          fontSize={20}
                          fontWeight="bold"
                          color={
                            themeName === "dark" ? "white" : "primary.secondary"
                          }
                        >
                          {chat.participant.chatNameForMember.charAt(0)}
                        </Typography>
                      </Avatar>
                    </ListItemAvatar>
                    <ListItemText
                      sx={{ ml: 3 }}
                      primary={
                        <Typography
                          fontSize={16}
                          color={
                            themeName === "dark" ? "white" : "primary.secondary"
                          }
                        >
                          {chat.participant.chatNameForMember}
                        </Typography>
                      }
                    />
                  </ListItem>
                ))}
              </List>
            )}
          </Box>
        )}

        {/* Área de mensagens */}
        {(!mdDown || selectedChat) && (
          <Box
            flex={1}
            display="flex"
            flexDirection="column"
            px={mdDown ? 0 : 2}
          >
            {/* Header do chat */}
            {selectedChat && (
              <Box
                sx={{ backgroundColor: theme.palette.background.paper }}
                display="flex"
                alignItems="center"
                justifyContent="space-between"
                mt={mdDown ? 0 : 3}
                px={mdDown ? 0 : 2}
                py={2}
                boxShadow={3}
                borderRadius={mdDown ? 0 : 2}
              >
                <Box display="flex" alignItems="center">
                  {mdDown && (
                    <IconButton onClick={handleGoBack}>
                      <ArrowBack />
                    </IconButton>
                  )}
                  <Avatar
                    src={selectedChat.participant.avatarUrl}
                    sx={{ ml: mdDown ? 0.5 : 0 }}
                  >
                    <Typography
                      fontSize={16}
                      fontWeight="bold"
                      color={
                        themeName === "dark" ? "white" : "primary.secondary"
                      }
                    >
                      {selectedChat.participant.chatNameForMember.charAt(0)}
                    </Typography>
                  </Avatar>
                  <Typography
                    ml={2}
                    fontWeight={700}
                    sx={{ color: theme.palette.text.primary }}
                  >
                    {selectedChat.participant.chatNameForMember}
                  </Typography>
                </Box>
                <IconButton>
                  <MoreVertIcon />
                </IconButton>
              </Box>
            )}

            {/* Mensagens */}
            <Box flex={1} p={2} overflow="auto">
              {loadingMessages ? (
                <Box display="flex" justifyContent="center">
                  <CircularProgress size={30} />
                </Box>
              ) : !selectedChat ? (
                <Typography align="center" color="textSecondary" mt={4}>
                  Selecione um chat para começar a conversar
                </Typography>
              ) : (
                messages.map((msg) => (
                  <Box
                    key={msg.id}
                    alignSelf={
                      msg.senderId === userId ? "flex-end" : "flex-start"
                    }
                    bgcolor={
                      msg.senderId === userId
                        ? theme.palette.primary.main
                        : theme.palette.grey[300]
                    }
                    color={msg.senderId === userId ? "white" : "black"}
                    px={2}
                    py={1}
                    borderRadius={2}
                    maxWidth="60%"
                    mb={1}
                  >
                    <Typography>{msg.text}</Typography>
                    <Typography
                      variant="caption"
                      display="block"
                      textAlign="right"
                    >
                      {new Date(msg.createdAt).toLocaleTimeString([], {
                        hour: "2-digit",
                        minute: "2-digit",
                      })}
                    </Typography>
                  </Box>
                ))
              )}
            </Box>

            {/* Campo de envio */}
            {selectedChat && (
              <Box
                display="flex"
                py={2}
                px={mdDown ? 1 : 0}
                pl={mdDown ? 2.5 : 0}
              >
                <TextField
                  fullWidth
                  sx={{
                    backgroundColor: theme.palette.background.paper,
                    borderRadius: 2,
                  }}
                  placeholder="Digite uma mensagem"
                  value={newMessage}
                  onChange={(e) => setNewMessage(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === "Enter") {
                      e.preventDefault();
                      handleSendMessage();
                    }
                  }}
                  InputProps={{
                    endAdornment: (
                      <InputAdornment position="end">
                        <IconButton>
                          <AttachFile />
                        </IconButton>
                        <IconButton onClick={handleSendMessage}>
                          <Send sx={{ color: theme.palette.primary.main }} />
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                />
              </Box>
            )}
          </Box>
        )}
      </Box>
    </BaseLayout>
  );
};
