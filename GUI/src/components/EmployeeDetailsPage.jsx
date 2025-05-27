import { useEffect, lazy } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useAtomValue } from "jotai";
import { jwtAtom } from "../services/atoms";
import {
  Typography,
  Button,
  Container,
  Card,
  CardContent,
  Grid,
} from "@mui/material";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import { toast } from "react-hot-toast";

import { useFetchByEmployeeNumber } from "../services/hooks";

const Loader = lazy(() => import("./Loader"));

const EmployeeDetailsPage = () => {
  const { employeeNumber } = useParams();
  const jwt = useAtomValue(jwtAtom);
  const navigate = useNavigate();

  const {
    data: employeeData,
    isLoading,
    isError,
  } = useFetchByEmployeeNumber(jwt?.access, employeeNumber);

  useEffect(() => {
    if (isError) toast.error("Failed to load employee data.");
  }, [isError]);

  if (!employeeData) return null;

  if (isLoading) return <Loader />;

  const employee = employeeData;

  // console.log(employeeNumber)

  return (
    <Container sx={{ mt: 4 }}>
      <Button
        startIcon={<ArrowBackIcon />}
        variant="outlined"
        onClick={() => navigate(-1)}
        sx={{ mb: 2 }}
      >
        Back to List
      </Button>

      <Typography variant="h4" gutterBottom>
        Employee #{employee.employeeNumber} - {employee.firstName}{" "}
        {employee.lastName}
      </Typography>
      <Typography variant="body1" color="success">
        Authenticated User: {jwt?.username}
      </Typography>

      <Grid container spacing={3} mt={2}>
        {/* Personal Info Section */}
        <Grid sx={{ xs: 12, md: 6 }}>
          {" "}
          {/* Using `sx` prop instead of `xs` and `md` directly */}
          <Card variant="outlined">
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Personal Information
              </Typography>
              <Typography>
                <strong>Name:</strong> {employee.firstName} {employee.lastName}
              </Typography>
              <Typography>
                <strong>Birthday:</strong> {employee.birthday}
              </Typography>
              <Typography>
                <strong>Address:</strong> {employee.address}
              </Typography>
              <Typography>
                <strong>Phone Number:</strong> {employee.phoneNumber}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        {/* Government IDs Section */}
        <Grid sx={{ xs: 12, md: 6 }}>
          <Card variant="outlined">
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Government IDs
              </Typography>
              <Typography>
                <strong>SSS:</strong> {employee.sss}
              </Typography>
              <Typography>
                <strong>PhilHealth:</strong> {employee.philhealth}
              </Typography>
              <Typography>
                <strong>TIN:</strong> {employee.tin}
              </Typography>
              <Typography>
                <strong>Pag-IBIG:</strong> {employee.pagibig}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        {/* Compensation Section */}
        <Grid sx={{ xs: 12, md: 6 }}>
          <Card variant="outlined">
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Compensation
              </Typography>
              <Typography>
                <strong>Basic Salary:</strong>{" "}
                {employee.basicSalary.toLocaleString()}
              </Typography>
              <Typography>
                <strong>Rice Subsidy:</strong>{" "}
                {employee.riceSubsidy.toLocaleString()}
              </Typography>
              <Typography>
                <strong>Phone Allowance:</strong>{" "}
                {employee.phoneAllowance.toLocaleString()}
              </Typography>
              <Typography>
                <strong>Clothing Allowance:</strong>{" "}
                {employee.clothingAllowance.toLocaleString()}
              </Typography>
              <Typography>
                <strong>Hourly Rate:</strong>{" "}
                {employee.hourlyRate.toLocaleString()}
              </Typography>
              <Typography>
                <strong>Gross Semi-Monthly Rate:</strong>{" "}
                {employee.grossSemiMonthlyRate.toLocaleString()}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        {/* Position and Status Section */}
        <Grid sx={{ xs: 12, md: 6 }}>
          <Card variant="outlined">
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Position and Status
              </Typography>
              <Typography>
                <strong>Position:</strong> {employee.position}
              </Typography>
              <Typography>
                <strong>Status:</strong> {employee.status}
              </Typography>
              <Typography>
                <strong>Immediate Supervisor:</strong>{" "}
                {employee.immediateSupervisor}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default EmployeeDetailsPage;
