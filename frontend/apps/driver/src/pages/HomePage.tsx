import { Box, Heading, Stack, Switch, Text } from "@chakra-ui/react";
import useDriverStatus from "../hooks/useDriverStatus";
import useUpdateDriverStatus from "../hooks/useUpdateDriverStatus";

export const HomePage = () => {
  const { data, isLoading } = useDriverStatus();
  const updateDriverStatus = useUpdateDriverStatus();
  const isAvailable = data?.status === "AVAILABLE";

  if (isLoading) {
    return <Text>Loading...</Text>;
  }

  return (
    <Box p={4}>
      <Heading as="h1" size="xl">
        Driver Home Page
      </Heading>
      <Text>Welcome to the driver application!</Text>
      <Stack direction="row" align="center" mt={4}>
        <Text minWidth="115px">
          Status: {isAvailable ? "Available" : "Offline"}
        </Text>
        <Switch.Root
          checked={isAvailable}
          onCheckedChange={async (e) =>
            await updateDriverStatus.mutateAsync(
              e.checked ? "AVAILABLE" : "OFFLINE"
            )
          }
        >
          <Switch.HiddenInput />
          <Switch.Control>
            <Switch.Thumb />
          </Switch.Control>
          <Switch.Label srOnly>Toggle Driver Status</Switch.Label>
        </Switch.Root>
      </Stack>
      {/* TODO: Add driver-specific content here */}
    </Box>
  );
};
