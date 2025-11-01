import { Box, Heading } from "@chakra-ui/react";

export const HistoryPage = () => {
  return (
    <Box p={4}>
      <Heading as="h1" size="xl">
        Driver History Page
      </Heading>
      <p>This is where the driver's past deliveries will be displayed.</p>
      {/* TODO: Implement actual history display */}
    </Box>
  );
};
