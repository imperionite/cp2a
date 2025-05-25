import Box from "@mui/material/Box";
import Skeleton from "@mui/material/Skeleton";

export default function Loader() {
  return (
    <Box
      sx={{
        width: 1000,
        height: "60vh",
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        alignItems: "center",
        mx: "auto",
      }}
    >
      <Skeleton variant="text" sx={{ fontSize: "1rem" }} />
      <Skeleton variant="circular" width={40} height={40} />
      <Skeleton variant="rectangular" width={210} height={60} />
      <Skeleton variant="rounded" width={210} height={60} />
    </Box>
  );
}
