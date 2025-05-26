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
import { employeesAtom } from "../services/atoms";
import { useEmployeeBasicInfo, useEmployeePartialDetails } from "../services/hooks";
import { jwtAtom } from "../services/atoms";
const Loader = lazy(() => import("./Loader"));

const EmployeesTable = ({ isAdmin }) => {
  const [employees, setEmployees] = useAtom(employeesAtom);
  const jwt = useAtomValue(jwtAtom);

  // Choose the right data fetcher based on isAdmin
  const useFetchData = isAdmin ? useEmployeePartialDetails : useEmployeeBasicInfo;
  const {
    data: employeeData,
    isLoading,
    isError,
    error,
  } = useFetchData(isAdmin ? jwt?.is_admin : jwt?.access);

  useEffect(() => {
    if (employeeData) {
      setEmployees(employeeData);
    }
  }, [employeeData, setEmployees]);

  if (isLoading) return <Loader />;
  if (isError) return toast.error(error.message);

  // Define columns based on isAdmin
  const columns = isAdmin
    ? [
        "Employee Number",
        "First Name",
        "Last Name",
        "SSS",
        "PhilHealth",
        "TIN",
        "Pag-IBIG",
        "Username",
        "Admin",
      ]
    : [
        "Employee Number",
        "First Name",
        "Last Name",
        "Birthday",
      ];

  return (
    <TableContainer component={Paper} sx={{ mt: 4, maxHeight: 600 }}>
      <Table stickyHeader aria-label="employee details table">
        <TableHead>
          <TableRow>
            {columns.map((column) => (
              <TableCell key={column}>{column}</TableCell>
            ))}
          </TableRow>
        </TableHead>
        <TableBody>
          {employees.map((emp) => (
            <TableRow key={emp.id}>
              <TableCell>{emp.employeeNumber}</TableCell>
              <TableCell>{emp.firstName}</TableCell>
              <TableCell>{emp.lastName}</TableCell>
              {isAdmin ? (
                <>
                  <TableCell>{emp.sss}</TableCell>
                  <TableCell>{emp.philhealth}</TableCell>
                  <TableCell>{emp.tin}</TableCell>
                  <TableCell>{emp.pagibig}</TableCell>
                  <TableCell>{emp.user?.username}</TableCell>
                  <TableCell>{emp.user?.isAdmin ? "Yes" : "No"}</TableCell>
                </>
              ) : (
                <TableCell>{emp.birthday}</TableCell>
              )}
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default EmployeesTable;
