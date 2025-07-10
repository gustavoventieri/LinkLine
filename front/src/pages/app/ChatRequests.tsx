import { useState, useEffect } from "react";
import {
  Box,
  Button,
  CircularProgress,
  IconButton,
  Typography,
  useMediaQuery,
  Theme,
  useTheme,
} from "@mui/material";
import {
  AccessTimeOutlined,
  CheckOutlined,
  CloseOutlined,
  Diversity3Rounded,
  SwapHoriz,
} from "@mui/icons-material";
import { BaseLayout } from "../../shared/layouts";
import { useAppThemeContext } from "../../shared/contexts";
import { api } from "../../shared/services";
import { RequestCard } from "../../shared/components";

interface Request {
  id: string;
  senderUsername: string;
  receiverUsername: string;
  status: "declined" | "pending" | "accepted";
  createdAt: string;
  updatedAt: string;
}
// ...importações permanecem as mesmas

export const ChatRequests = () => {
  const { themeName } = useAppThemeContext();
  const mdDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("md"));
  const smDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("sm"));
  const theme = useTheme();
  const [statusFilter, setStatusFilter] = useState<
    "declined" | "pending" | "accepted"
  >("pending");
  const [tab, setTab] = useState<0 | 1>(1);
  const [requests, setRequests] = useState<Request[]>([]);
  const [loading, setLoading] = useState(false);
  const [requestLoadingId, setRequestLoadingId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleSwapTab = () => setTab((prev) => (prev === 0 ? 1 : 0));

  const fetchRequests = async () => {
    setLoading(true);
    setError(null);
    try {
      const type = tab === 0 ? "sent" : "received";
      const response = await api.get(`/chat-request/${type}/${statusFilter}`);

      const requestsObject = response.data.chat_requests || {};
      console.log(requestsObject);
      const requestsArray: Request[] = Object.values(requestsObject).map(
        (req: any) => ({
          id: req.id,
          senderUsername: req.sender.username,
          receiverUsername: req.receiver.username,
          status: req.status.toLowerCase() as Request["status"],
          createdAt: req.createdAt || "",
          updatedAt: req.updatedAt || "",
        })
      );

      setRequests(requestsArray);
    } catch (err: any) {
      if (err.response.status) {
        setError(" No followers request found ");
        return;
      }
      setError("Erro ao buscar solicitações.");
      setRequests([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRequests();
  }, [statusFilter, tab]);

  const handleUpdateChatRequestStatus = async (
    username: string,
    chatRequestId: string,
    newStatus: "ACCEPTED" | "DECLINED"
  ) => {
    setRequestLoadingId(chatRequestId);

    try {
      const payload = { newStatus };

      const response = await api.put(
        `/chat-request/update/${chatRequestId}`,
        payload
      );

      if (response.status === 200 || response.status === 201) {
        if (newStatus === "ACCEPTED") {
          await handleCreatePrivateChat(username);
        }

        await fetchRequests(); // sempre faz após atualizar
      } else {
        console.warn(
          `Atualização de status falhou. Status HTTP: ${response.status}`
        );
      }
    } catch (error: any) {
      console.error(
        `Erro ao ${
          newStatus === "ACCEPTED" ? "aceitar" : "recusar"
        } solicitação:`,
        error?.response?.data || error.message || error
      );
    } finally {
      setRequestLoadingId(null);
    }
  };

  const handleCreatePrivateChat = async (username: string) => {
    try {
      await api.post("/private-chat/create", { username: username });
    } catch (error: any) {
      console.error(
        "Erro ao criar chat privado:",
        error?.response?.data || error.message || error
      );
    }
  };

  return (
    <BaseLayout showIcon>
      <Box display="flex" minHeight="100vh">
        {!mdDown && (
          <Box
            width="30%"
            height="100vh" // Garante que o Box ocupe a altura toda
            bgcolor={
              themeName === "light"
                ? theme.palette.background.paper
                : theme.palette.background.default
            }
            display="flex"
            flexDirection="column" // Importante para empilhar os filhos verticalmente
            p={2} // Adiciona um padding interno para afastar das bordas
          >
            <Box
              display="flex"
              flexDirection="column"
              alignItems="center"
              justifyContent="center"
              flexGrow={1}
              mb={10}
            >
              <Diversity3Rounded
                sx={{
                  width: 100,
                  height: 100,
                  color: themeName === "dark" ? "white" : "grey.400",
                }}
              />
              <Typography
                fontSize={16} // Um pouco menor que o título
                color={themeName === "dark" ? "grey.300" : "text.secondary"}
                textAlign="center" // Garante que o texto fique centralizado se quebrar linha
              >
                New friendships knocking at the door!
              </Typography>
            </Box>
          </Box>
        )}

        <Box
          flex={1}
          display="flex"
          flexDirection="column"
          bgcolor={
            themeName === "light"
              ? theme.palette.background.default
              : theme.palette.background.paper
          }
          p={smDown ? 1 : 2}
        >
          {/* Botões de aba superior */}
          <Box
            display="flex"
            justifyContent="center"
            alignItems="center"
            gap={smDown ? 2 : 10}
            mb={smDown ? 3 : 5}
            mt={smDown ? 2 : 1}
            flexWrap="wrap"
          >
            <Button
              variant={tab === 0 ? "contained" : "text"}
              onClick={() => setTab(0)}
              disableElevation
              sx={{
                textTransform: "none",
                fontSize: smDown ? 14 : 18,
                px: smDown ? 4 : 6,
                py: smDown ? 0.5 : 1,
                borderRadius: 2,
              }}
            >
              Sent
            </Button>

            <IconButton onClick={handleSwapTab}>
              <SwapHoriz
                sx={{ fontSize: smDown ? 24 : 35, color: "primary.main" }}
              />
            </IconButton>

            <Button
              variant={tab === 1 ? "contained" : "text"}
              onClick={() => setTab(1)}
              disableElevation
              sx={{
                textTransform: "none",
                fontSize: smDown ? 14 : 18,
                borderRadius: 2,
                px: smDown ? 2 : 6,
                py: smDown ? 0.5 : 1,
                ml: smDown ? 0.5 : 0,
              }}
            >
              Received
            </Button>
          </Box>

          <Box
            flexGrow={1}
            display="flex"
            flexDirection="column"
            // alignItems="center" // Removido para que a lista de cards use a largura total
          >
            {loading ? (
              <Box
                sx={{
                  textAlign: "center",
                  mt: 5,
                  flexGrow: 1,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                }}
              >
                <CircularProgress />
              </Box>
            ) : error ? (
              <Box
                sx={{
                  flexGrow: 1,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  mb: mdDown ? 0 : 10,
                }}
              >
                <Typography
                  color={themeName === "dark" ? "grey.300" : "grey.700"}
                  textAlign="center"
                  fontSize={smDown ? 14 : 16}
                >
                  {error}
                </Typography>
              </Box>
            ) : requests.length === 0 ? (
              <Box
                sx={{
                  flexGrow: 1,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  mb: 6,
                  px: 2,
                }}
              >
                <Typography
                  color={themeName === "dark" ? "grey.400" : "grey.600"}
                  fontSize={smDown ? 14 : 16}
                  textAlign="center"
                >
                  {tab === 0
                    ? `You didn't send ${statusFilter} requests.`
                    : `You didn't receive ${statusFilter} requests.`}
                </Typography>
              </Box>
            ) : (
              <Box
                sx={{
                  width: "100%",
                  display: "flex",
                  flexDirection: "column",
                  alignItems: "center",
                }}
              >
                {requests.map((req) => (
                  <RequestCard
                    avatarUrl={req.receiverUsername}
                    mdDown={mdDown}
                    smDown={smDown}
                    key={req.id}
                    username={
                      tab === 0 ? req.receiverUsername : req.senderUsername
                    }
                    status={req.status}
                    onAccept={() =>
                      handleUpdateChatRequestStatus(
                        tab === 0 ? req.receiverUsername : req.senderUsername,
                        req.id,
                        "ACCEPTED"
                      )
                    }
                    onDecline={() =>
                      handleUpdateChatRequestStatus(
                        tab === 0 ? req.receiverUsername : req.senderUsername,
                        req.id,
                        "DECLINED"
                      )
                    }
                    type={tab}
                    loading={requestLoadingId === req.id}
                  />
                ))}
              </Box>
            )}
          </Box>

          <Box
            display="flex"
            justifyContent={mdDown ? "flex-start" : "center"}
            alignItems={mdDown ? "flex-start" : "center"}
            flexDirection={mdDown ? "column" : "row"}
            flexWrap="wrap"
            gap={mdDown ? 1 : 2}
            mt="auto"
          >
            {["declined", "pending", "accepted"].map((status) => {
              const label = status[0].toUpperCase() + status.slice(1);
              const color =
                status === "declined"
                  ? "#E60000"
                  : status === "pending"
                  ? "#FFD52F"
                  : "#15FF00";
              const bg =
                status === "declined"
                  ? "rgba(255, 0, 0, 0.2)"
                  : status === "pending"
                  ? "rgba(248, 234, 37, 0.2)"
                  : "rgba(132, 248, 37, 0.2)";

              const Icon =
                status === "declined"
                  ? CloseOutlined
                  : status === "pending"
                  ? AccessTimeOutlined
                  : CheckOutlined;

              return (
                <Button
                  key={status}
                  onClick={() => setStatusFilter(status as Request["status"])}
                  variant={statusFilter === status ? "contained" : "outlined"}
                  startIcon={
                    <Icon
                      sx={{
                        color,
                        width: mdDown ? 20 : 30,
                        height: mdDown ? 20 : 30,
                      }}
                    />
                  }
                  sx={{
                    bgcolor: statusFilter === status ? bg : "transparent",
                    color,
                    border: `${bg} 2px solid`,
                    textTransform: "none",
                    fontSize: mdDown ? 10 : 16,
                    px: mdDown ? 2 : 5,
                    py: mdDown ? 0.5 : 0.7,
                    "&:hover": {
                      bgcolor: bg,
                    },
                    borderRadius: 2,
                    width: mdDown ? "50%" : "auto", // Para preencher a largura no mobile
                  }}
                >
                  {label}
                </Button>
              );
            })}
          </Box>
        </Box>
      </Box>
    </BaseLayout>
  );
};
