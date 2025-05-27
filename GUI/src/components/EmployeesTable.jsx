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
  Button,
} from "@mui/material";
import { toast } from "react-hot-toast";
import { employeesAtom } from "../services/atoms";
import {
  useEmployeeBasicInfo,
  useEmployeePartialDetails,
} from "../services/hooks";
import { jwtAtom } from "../services/atoms";
import { useNavigate } from "react-router-dom";

const Loader = lazy(() => import("./Loader"));

const EmployeesTable = ({ isAdmin }) => {
  const [employees, setEmployees] = useAtom(employeesAtom);
  const jwt = useAtomValue(jwtAtom);
  const navigate = useNavigate();

  // Choose the right data fetcher based on isAdmin
  const useFetchData = isAdmin
    ? useEmployeePartialDetails
    : useEmployeeBasicInfo;
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
      ]
    : ["Employee Number", "First Name", "Last Name", "Birthday"];

  return (
    <>
      <TableContainer component={Paper} sx={{ mt: 4, maxHeight: 600 }}>
        <Table stickyHeader>
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
                <TableCell>
                  {isAdmin ? (
                    <Button
                      variant="outlined"
                      size="small"
                      onClick={(e) => {
                        e.preventDefault();
                        navigate(`/employees/${emp.employeeNumber}`);
                      }}
                    >
                      {emp.employeeNumber}
                    </Button>
                  ) : (
                    emp.employeeNumber
                  )}
                </TableCell>
                <TableCell>{emp.firstName}</TableCell>
                <TableCell>{emp.lastName}</TableCell>
                {isAdmin ? (
                  <>
                    <TableCell>{emp.sss}</TableCell>
                    <TableCell>{emp.philhealth}</TableCell>
                    <TableCell>{emp.tin}</TableCell>
                    <TableCell>{emp.pagibig}</TableCell>
                  </>
                ) : (
                  <TableCell>{emp.birthday}</TableCell>
                )}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </>
  );
};

export default EmployeesTable;
