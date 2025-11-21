import { Box, Container, Heading, Text, VStack } from "@chakra-ui/react";
import { useAuthStore } from "@repo/shared/hooks";
import ServiceHealthDashboard from "../components/ServiceHealthDashboard";
import TokensDashboard from "../components/TokensDashboard";

const HomePage = () => {
  const { user } = useAuthStore();

  return (
    <Container maxW="container.xl" py={8}>
      <VStack gap={6} align="stretch">
        <Box>
          <Heading size="2xl" mb={2}>
            Admin Dashboard
          </Heading>
          <Text color="gray.600">
            Welcome back, {user?.firstName} {user?.lastName}
          </Text>
        </Box>

        <ServiceHealthDashboard />

        <TokensDashboard />

        <Box p={6} bg="white" borderRadius="md" shadow="sm" borderWidth="1px">
          <Heading size="lg" mb={4}>
            System Overview
          </Heading>
          <Text color="gray.600">
            This is the system administration dashboard. Use the navigation bar
            to access different sections.
          </Text>
        </Box>
      </VStack>
    </Container>
  );
};

export default HomePage;
