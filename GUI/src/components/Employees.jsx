import { useAtomValue } from "jotai";
import { Box, Container } from "@mui/material";
import { jwtAtom } from "../services/atoms";
import EmployeesTable from "./EmployeesTable";

const Employees = () => {
  const jwt = useAtomValue(jwtAtom);

  return (
    <Container>
      <Box sx={{ bgcolor: "#cfe8fc", p: 2, mt: 2 }}>
        <header>
          <h2>Employees List</h2>
        </header>
        <section>
          <EmployeesTable isAdmin={jwt?.is_admin === "true"} />
        </section>
      </Box>
    </Container>
  );
};

export default Employees;
