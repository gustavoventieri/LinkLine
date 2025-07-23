import { createContext, useContext, useState } from "react";

interface UsernameContextType {
  username: string;
  setUsername: (username: string) => void;
}

const UsernameContext = createContext<UsernameContextType>({
  username: "",
  setUsername: () => {},
});

export const UsernameProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [username, setUsername] = useState("");

  return (
    <UsernameContext.Provider value={{ username, setUsername }}>
      {children}
    </UsernameContext.Provider>
  );
};

export const useUsername = () => useContext(UsernameContext);
