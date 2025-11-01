import { Box, Heading } from "@chakra-ui/react";

export const HomePage = () => {
  return (
    <Box p={4}>
      <Heading as="h1" size="xl">
        Driver Home Page
      </Heading>
      <p>Welcome to the driver application!</p>
      {/* TODO: Add driver-specific content here */}
    </Box>
  );
};
