import { useEffect, lazy } from "react";
import { useAtom, useAtomValue } from "jotai";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from "@mui/material";
import { toast } from "react-hot-toast";

import { employeePartialDetailsAtom } from "../services/atoms";
import { useEmployeePartialDetails } from "../services/hooks";
import { jwtAtom } from "../services/atoms";
const Loader = lazy(() => import("./Loader"));

const EmployeesAdmin = () => {
  const [employees, setEmployees] = useAtom(employeePartialDetailsAtom);
const jwt = useAtomValue(jwtAtom)
  const {
    data: employeeData,
    isLoading,
    isError,
    error,
  } = useEmployeePartialDetails(jwt?.is_admin);

  // Update Jotai atom when data changes
  useEffect(() => {
    if (employeeData) {
      setEmployees(employeeData);
    }
  }, [employeeData, setEmployees]);

  if (isLoading) return <Loader />;
  if (isError) return toast.error(error.message);

  return (
    <TableContainer component={Paper} sx={{ mt: 4, maxHeight: 400 }}>
      <Table stickyHeader aria-label="employee partial details table">
        <TableHead>
          <TableRow>
            <TableCell>Employee Number</TableCell>
            <TableCell>First Name</TableCell>
            <TableCell>Last Name</TableCell>
            <TableCell>SSS</TableCell>
            <TableCell>PhilHealth</TableCell>
            <TableCell>TIN</TableCell>
            <TableCell>Pag-IBIG</TableCell>
            <TableCell>Username</TableCell>
            <TableCell>Admin</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {employees.map((emp) => (
            <TableRow key={emp.id}>
              <TableCell>{emp.employeeNumber}</TableCell>
              <TableCell>{emp.firstName}</TableCell>
              <TableCell>{emp.lastName}</TableCell>
              <TableCell>{emp.sss}</TableCell>
              <TableCell>{emp.philhealth}</TableCell>
              <TableCell>{emp.tin}</TableCell>
              <TableCell>{emp.pagibig}</TableCell>
              <TableCell>{emp.user?.username}</TableCell>
              <TableCell>{emp.user?.isAdmin ? "Yes" : "No"}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default EmployeesAdmin
