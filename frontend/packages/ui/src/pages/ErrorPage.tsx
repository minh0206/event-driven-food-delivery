import { Box, Heading, Icon, Text, VStack } from "@chakra-ui/react";
import { CiWarning } from "react-icons/ci";
import { isRouteErrorResponse, useRouteError } from "react-router-dom";

export const ErrorPage = ({
  errorMessage = "An error occurred",
}: {
  errorMessage?: string;
}) => {
  const error = useRouteError();
  const isNotFound = isRouteErrorResponse(error);
  const displayMessage = isNotFound ? "This page doesn't exist" : errorMessage;

  return (
    <Box
      minH="100vh"
      display="flex"
      alignItems="center"
      justifyContent="center"
      bg="gray.50"
      px={4}
    >
      <VStack textAlign="center" maxW="md">
        <Icon
          as={CiWarning}
          boxSize={16}
          color={isNotFound ? "orange.400" : "red.400"}
        />
        <Heading size="2xl" color="gray.800">
          {isNotFound ? "404" : "Oops!"}
        </Heading>
        <Text fontSize="lg" color="gray.600">
          {displayMessage}
        </Text>
      </VStack>
    </Box>
  );
};
