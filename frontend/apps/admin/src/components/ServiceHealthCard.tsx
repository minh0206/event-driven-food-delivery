import { Box, HStack, Table, Text } from "@chakra-ui/react";
import { ServiceHealth } from "../types/ServiceHealth";

interface ServiceHealthCardProps {
  service: ServiceHealth;
}

const ServiceHealthCard = ({ service }: ServiceHealthCardProps) => {
  const getStatusColor = (status: ServiceHealth["status"]) => {
    switch (status) {
      case "UP":
        return "green.500";
      case "DOWN":
        return "red.500";
      default:
        return "gray.400";
    }
  };

  const getStatusBgColor = (status: ServiceHealth["status"]) => {
    switch (status) {
      case "UP":
        return "green.50";
      case "DOWN":
        return "red.50";
      default:
        return "gray.50";
    }
  };

  return (
    <Table.Row>
      <Table.Cell>
        <Text fontWeight="medium" color="gray.700">
          {service.name}
        </Text>
      </Table.Cell>
      <Table.Cell>
        <Text fontSize="sm" color="gray.500">
          {service.endpoint}
        </Text>
      </Table.Cell>
      <Table.Cell textAlign="end">
        <HStack gap={2} justify="end">
          <Box
            w={2.5}
            h={2.5}
            borderRadius="full"
            bg={getStatusColor(service.status)}
            animation={service.status === "UP" ? "pulse 2s infinite" : undefined}
          />
          <Box
            px={3}
            py={1}
            borderRadius="md"
            bg={getStatusBgColor(service.status)}
            color={getStatusColor(service.status)}
            fontWeight="medium"
            fontSize="sm"
          >
            {service.status}
          </Box>
        </HStack>
      </Table.Cell>
    </Table.Row>
  );
};

export default ServiceHealthCard;
