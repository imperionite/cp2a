import Box from "@mui/material/Box";
import Container from "@mui/material/Container";

const Home = () => {
  return (
    <Container maxWidth="xl">
      <Box sx={{ bgcolor: "#cfe8fc" }}>
        <header>
          <h1>Welcome to MotorPH: Employee Management System</h1>
        </header>

        <section>
          <h2>Introduction</h2>
          <p>
            This is a proof-of-concept webpage design to augment and test the
            MotorPH backend REST API project for employee management system at{" "}
            <a href="https://github.com/imperionite/cp2a">
              {" "}
              https://github.com/imperionite/cp2a
            </a>
            .
          </p>
        </section>
      </Box>
    </Container>
  );
};

export default Home;