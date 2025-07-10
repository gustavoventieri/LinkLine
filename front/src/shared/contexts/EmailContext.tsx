import { createContext, useContext, useState, ReactNode } from "react";

type EmailContextType = {
  email: string;
  setEmail: (email: string) => void;
  clearEmail: () => void;
};

const EmailContext = createContext<EmailContextType | undefined>(undefined);

export const EmailProvider = ({ children }: { children: ReactNode }) => {
  const [email, setEmail] = useState("");

  const clearEmail = () => setEmail("");

  return (
    <EmailContext.Provider value={{ email, setEmail, clearEmail }}>
      {children}
    </EmailContext.Provider>
  );
};

export const useEmail = (): EmailContextType => {
  const context = useContext(EmailContext);
  if (!context) {
    throw new Error("useEmail must be used within an EmailProvider");
  }
  return context;
};
