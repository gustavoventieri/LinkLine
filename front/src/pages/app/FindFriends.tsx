import {
  Box,
  Typography,
  useMediaQuery,
  Theme,
  useTheme,
  TextField,
  InputAdornment,
  CircularProgress,
  Snackbar,
  Alert,
} from "@mui/material";
import { Diversity3Rounded, Search } from "@mui/icons-material";
import { BaseLayout } from "../../shared/layouts";
import { useAppThemeContext } from "../../shared/contexts";
import { useState, useEffect, useCallback, useRef } from "react";
import { api } from "../../shared/services";
import { UserFoundCard, UserRelationshipStatus } from "../../shared/components";

interface PotentialFriend {
  username: string;
  avatarUrl?: string;
  friendshipStatus: UserRelationshipStatus;
}

function getRandomLetter() {
  const letters = "mg";
  return letters.charAt(Math.floor(Math.random() * letters.length));
}

export const FindFriends = () => {
  const { themeName } = useAppThemeContext();
  const theme = useTheme();
  const mdDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("md"));
  const smDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("sm"));

  // Estados originais
  const [searchTerm, setSearchTerm] = useState<string>("");
  const [searchResults, setSearchResults] = useState<PotentialFriend[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const debounceTimeout = useRef<ReturnType<typeof setTimeout> | null>(null);
  const debounce = <F extends (...args: any[]) => any>(
    func: F,
    delay: number
  ) => {
    return (...args: Parameters<F>): void => {
      if (debounceTimeout.current) clearTimeout(debounceTimeout.current);
      debounceTimeout.current = setTimeout(() => func(...args), delay);
    };
  };

  const fetchPotentialFriends = async (currentSearchTerm: string) => {
    if (!currentSearchTerm.trim()) {
      setSearchResults([]);
      setError(null);
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const response = await api.post("/user/search", {
        searchTerm: currentSearchTerm,
        limit: 10,
      });
      if (response.status === 200) {
        setSearchResults(response.data);
      } else {
        setSearchResults([]);
        setError("Erro ao buscar.");
      }
    } catch (err: any) {
      setSearchResults([]);
      setError(err.message || "Ocorreu um erro inesperado.");
    } finally {
      setLoading(false);
    }
  };

  const debouncedFetchFriends = useCallback(
    debounce(fetchPotentialFriends, 500),
    []
  );

  useEffect(() => {
    debouncedFetchFriends(searchTerm);
  }, [searchTerm, debouncedFetchFriends]);

  // Busca automática do lado esquerdo
  const [autoFriends, setAutoFriends] = useState<PotentialFriend[]>([]);
  const [loadingAutoFriends, setLoadingAutoFriends] = useState(false);

  useEffect(() => {
    const fetchAutoFriends = async () => {
      setLoadingAutoFriends(true);
      try {
        const letter = getRandomLetter();
        const response = await api.post("/user/search", {
          searchTerm: letter,
          limit: 10,
        });
        if (response.status === 200) {
          setAutoFriends(response.data);
        } else {
          setAutoFriends([]);
        }
      } catch {
        setAutoFriends([]);
      } finally {
        setLoadingAutoFriends(false);
      }
    };
    fetchAutoFriends();
  }, []);

  const filteredAutoFriends = autoFriends.filter(
    (u) => u.friendshipStatus !== "ACCEPTED"
  );

  const handleFriendshipAction = async (
    username: string,
    status: UserRelationshipStatus
  ) => {
    try {
      if (status === null) {
        await api.post("/chat-request/create", { username });
        fetchPotentialFriends(searchTerm);
      }
    } catch (err: any) {
      setErrorMessage(err.response?.data?.error || "Erro inesperado");
      setSnackbarOpen(true);
    }
  };

  return (
    <BaseLayout showIcon>
      <Box display="flex" height="100vh">
        {/* ✅ Lado Esquerdo: lista automática */}
        {!mdDown && (
          <Box
            width="30%"
            height="100%"
            bgcolor={
              themeName === "light"
                ? theme.palette.background.paper
                : theme.palette.background.default
            }
            display="flex"
            flexDirection="column"
            p={2}
            sx={{ borderRight: `1px solid ${theme.palette.divider}` }}
          >
            <Box
              display="flex"
              flexDirection="column"
              alignItems="center"
              mb={2}
              gap={1}
            >
              <Diversity3Rounded
                sx={{
                  width: 80,
                  height: 80,
                  color:
                    themeName === "dark"
                      ? theme.palette.primary.main
                      : theme.palette.primary.dark,
                }}
              />
              <Typography
                variant="h6"
                color={themeName === "dark" ? "grey.300" : "text.secondary"}
                textAlign="center"
              >
                Pessoas que você deve conhecer
              </Typography>
            </Box>

            {loadingAutoFriends ? (
              <Box display="flex" justifyContent="center" mt={4}>
                <CircularProgress />
              </Box>
            ) : filteredAutoFriends.length === 0 ? (
              <Typography
                variant="body2"
                color={theme.palette.text.secondary}
                textAlign="center"
                mt={4}
              >
                Nenhuma pessoa sugerida
              </Typography>
            ) : (
              <Box
                display={"flex"}
                flexDirection={"column"}
                justifyContent={"center"}
                alignItems={"center"}
                sx={{ overflowY: "auto", mt: 2 }}
              >
                {filteredAutoFriends.map((user) => (
                  <UserFoundCard
                    key={user.username}
                    username={user.username}
                    avatarUrl={user.avatarUrl}
                    mdDown={mdDown}
                    smDown={smDown}
                    relationshipStatus={user.friendshipStatus}
                    onAddFriend={() =>
                      handleFriendshipAction(
                        user.username,
                        user.friendshipStatus
                      )
                    }
                  />
                ))}
              </Box>
            )}
          </Box>
        )}

        <Box
          width={mdDown ? "100%" : "70%"}
          display="flex"
          height="100%"
          flexDirection="column"
          bgcolor={
            themeName === "light"
              ? theme.palette.background.default
              : theme.palette.background.paper
          }
          sx={{ pt: smDown ? 2 : 3 }}
          gap={3}
        >
          {/* Campo de busca */}
          <Box display="flex" justifyContent="center" width="100%">
            <TextField
              variant="outlined"
              placeholder="Find a new friend"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start" sx={{ ml: 1 }}>
                    <Search sx={{ color: "primary.main" }} />
                  </InputAdornment>
                ),
              }}
              sx={{
                width: mdDown ? "100%" : "80%",
                boxShadow: themeName === "light" ? 1 : 0,
                borderRadius: "10px",
                mx: smDown ? 1 : 3,
                "& .MuiOutlinedInput-root": {
                  borderRadius: "10px",
                  height: "60px",
                  backgroundColor:
                    themeName === "light"
                      ? "white"
                      : theme.palette.background.default,
                  "& fieldset": { borderColor: "transparent" },
                  "&:hover fieldset": {
                    borderColor: theme.palette.action.hover,
                  },
                  "&.Mui-focused fieldset": {
                    borderColor: "primary.main",
                    borderWidth: "1px",
                  },
                },
                "& .MuiInputBase-input": {
                  paddingTop: "12px",
                  paddingBottom: "12px",
                },
              }}
            />
          </Box>

          {/* Lista de resultados */}
          <Box
            width="100%"
            display="flex"
            flexDirection="column"
            alignItems="center"
            flexGrow={1}
            height="80vh"
          >
            {loading ? (
              <CircularProgress />
            ) : error ? (
              <Typography variant="body1" color="error">
                {error}
              </Typography>
            ) : (
              <Box
                width="100%"
                display="flex"
                flexDirection={"column"}
                justifyContent="center"
                alignItems={"center"}
                px={smDown ? 1 : mdDown ? 3 : 0}
              >
                {searchResults.map((user) => (
                  <UserFoundCard
                    mdDown={mdDown}
                    relationshipStatus={user.friendshipStatus}
                    key={user.username}
                    username={user.username}
                    avatarUrl={user.avatarUrl}
                    smDown={smDown}
                    onAddFriend={() =>
                      handleFriendshipAction(
                        user.username,
                        user.friendshipStatus
                      )
                    }
                  />
                ))}
              </Box>
            )}
          </Box>
        </Box>

        {/* Snackbar */}
        <Snackbar
          open={snackbarOpen}
          autoHideDuration={6000}
          onClose={() => setSnackbarOpen(false)}
          anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
        >
          <Alert
            onClose={() => setSnackbarOpen(false)}
            severity="error"
            sx={{ width: "100%" }}
          >
            {errorMessage}
          </Alert>
        </Snackbar>
      </Box>
    </BaseLayout>
  );
};
