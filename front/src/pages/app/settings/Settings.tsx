import { BaseLayout } from "../../../shared/layouts";
import { api } from "../../../shared/services";

export const Settings = () => {
  const handleCheckAuth = async () => {
    try {
      const response = await api.get("/auth/isAuth");

      console.log("Usuário autenticado:", response.data.userId);
      alert("Usuário autenticado: " + response.data.userId);
    } catch (err) {
      console.error("Erro de autenticação:", err);
      alert("Erro ao verificar autenticação");
    }
  };

  return (
    <BaseLayout showIcon>
      <div>
        <button onClick={handleCheckAuth}>Verificar autenticação</button>
      </div>
    </BaseLayout>
  );
};
