import { useAtomValue } from "jotai";
import { Box, Container, Typography } from "@mui/material";
import { jwtAtom } from "../services/atoms";
import EmployeesTable from "./EmployeesTable";

const Employees = () => {
  const jwt = useAtomValue(jwtAtom);

  return (
    <Container>
      <Box sx={{ bgcolor: "#cfe8fc", p: 2, mt: 2 }}>
        <header>
          <Typography variant="h5">Employees List</Typography>
          <Typography variant="body1" color="success">
            Authenticated User: {jwt?.username}
          </Typography>
        </header>
        <section>
          <EmployeesTable isAdmin={jwt?.is_admin === "true"} />
        </section>
      </Box>
    </Container>
  );
};

export default Employees;
