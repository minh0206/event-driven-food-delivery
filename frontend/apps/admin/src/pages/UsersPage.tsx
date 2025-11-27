import { Box, Button, Container, Heading, VStack } from "@chakra-ui/react";
import UsersTable from "../components/UsersTable";
import { useUsers } from "../hooks/useUsers";

const UsersPage = () => {
    const { refetch } = useUsers();

    return (
        <Container maxW="container.xl" py={8}>
            <VStack gap={6} align="stretch">
                <Box>
                    <Heading size="2xl" mb={2}>
                        Users Management
                    </Heading>
                </Box>

                <Box p={6} bg="white" borderRadius="md" shadow="sm" borderWidth="1px">
                    <Box
                        display="flex"
                        justifyContent="space-between"
                        alignItems="center"
                        mb={4}
                    >
                        <Heading size="lg">All Users</Heading>
                        <Button
                            size="sm"
                            onClick={() => refetch()}
                            colorScheme="blue"
                            variant="outline"
                        >
                            Refresh
                        </Button>
                    </Box>
                    <UsersTable />
                </Box>
            </VStack>
        </Container>
    );
};

export default UsersPage;
