import { Box, Button, Heading, Spinner, Table, Text } from "@chakra-ui/react";
import { useQuery } from "@tanstack/react-query";
import { getAllServicesHealth } from "../services/healthService";
import ServiceHealthCard from "./ServiceHealthCard";

const ServiceHealthDashboard = () => {
  const { data: services, isLoading, error, refetch } = useQuery({
    queryKey: ["serviceHealth"],
    queryFn: getAllServicesHealth,
    refetchInterval: 30000, // Refetch every 30 seconds
  });

  if (isLoading) {
    return (
      <Box textAlign="center" py={8}>
        <Spinner size="xl" color="blue.500" />
        <Text mt={4} color="gray.600">
          Checking service health...
        </Text>
      </Box>
    );
  }

  if (error) {
    return (
      <Box
        p={6}
        bg="red.50"
        borderRadius="md"
        borderWidth="1px"
        borderColor="red.200"
      >
        <Text color="red.700" fontWeight="medium">
          Failed to fetch service health status
        </Text>
      </Box>
    );
  }

  const upCount = services?.filter((s) => s.status === "UP").length || 0;
  const totalCount = services?.length || 0;

  return (
    <Box
      p={6}
      bg="white"
      borderRadius="md"
      borderWidth="1px"
      borderColor="gray.200"
      shadow="sm"
    >
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={6}>
        <Box>
          <Heading size="md" mb={1}>
            Service Health Status
          </Heading>
          <Text color="gray.600" fontSize="sm">
            {upCount} of {totalCount} services operational
          </Text>
        </Box>
        <Button
          size="sm"
          onClick={() => refetch()}
          colorScheme="blue"
          variant="outline"
        >
          Refresh
        </Button>
      </Box>

      <Table.Root size="sm">
        <Table.Header>
          <Table.Row>
            <Table.ColumnHeader>Service Name</Table.ColumnHeader>
            <Table.ColumnHeader>Endpoint</Table.ColumnHeader>
            <Table.ColumnHeader textAlign="end">Status</Table.ColumnHeader>
          </Table.Row>
        </Table.Header>
        <Table.Body>
          {services?.map((service) => (
            <ServiceHealthCard key={service.name} service={service} />
          ))}
        </Table.Body>
      </Table.Root>
    </Box>
  );
};

export default ServiceHealthDashboard;
