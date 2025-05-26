// import { lazy } from "react";
import { useAtomValue } from "jotai";
import { Box, Container } from "@mui/material";

import { jwtAtom } from "../services/atoms";
import EmployeesAdmin from "./EmployeesAdmin";
import EmployeesRegular from "./EmployeesRegular";

const Employees = () => {
  const jwt = useAtomValue(jwtAtom);

  //console.log(jwt.is_admin);
  return (
    <Container maxWidth="xl">
      <Box sx={{ bgcolor: "#cfe8fc", p: 2, mt: 2 }}>
        <header>
          <h2>Employess List</h2>
        </header>
        <section>
          {jwt?.is_admin === "true" ? <EmployeesAdmin /> : <EmployeesRegular />}
        </section>
      </Box>
    </Container>
  );
};

export default Employees;
