import { Box, Heading, Text } from "@chakra-ui/react";
import { isRouteErrorResponse, useRouteError } from "react-router-dom";

export const ErrorPage = () => {
  const error = useRouteError();

  return (
    <Box p="4">
      <Heading>Oops</Heading>
      <Text>
        {isRouteErrorResponse(error)
          ? "This page doesn't exist"
          : "An error occurred"}
      </Text>
    </Box>
  );
};
