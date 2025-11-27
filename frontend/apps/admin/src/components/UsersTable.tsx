import { Badge, Box, Spinner, Table, Text } from "@chakra-ui/react";
import { Role, User } from "@repo/shared/models";
import { useUsers } from "../hooks/useUsers";

const getRoleBadgeColor = (role: Role) => {
    switch (role) {
        case Role.SYSTEM_ADMIN:
            return "red";
        case Role.RESTAURANT_ADMIN:
            return "purple";
        case Role.DRIVER:
            return "blue";
        case Role.CUSTOMER:
            return "green";
        default:
            return "gray";
    }
};

const getRoleLabel = (role: Role) => {
    switch (role) {
        case Role.SYSTEM_ADMIN:
            return "System Admin";
        case Role.RESTAURANT_ADMIN:
            return "Restaurant Admin";
        case Role.DRIVER:
            return "Delivery Driver";
        case Role.CUSTOMER:
            return "Customer";
        default:
            return role;
    }
};

const UsersTable = () => {
    const { data: users, isLoading, error } = useUsers();

    if (isLoading) {
        return (
            <Box textAlign="center" py={8}>
                <Spinner size="xl" />
            </Box>
        );
    }

    if (error) {
        return (
            <Box textAlign="center" py={8}>
                <Text color="red.500">Error loading users: {error.message}</Text>
            </Box>
        );
    }

    if (!users || users.length === 0) {
        return (
            <Box textAlign="center" py={8}>
                <Text color="gray.500">No users found</Text>
            </Box>
        );
    }

    return (
        <Box overflowX="auto">
            <Table.Root size="sm">
                <Table.Header>
                    <Table.Row>
                        <Table.ColumnHeader>ID</Table.ColumnHeader>
                        <Table.ColumnHeader>Name</Table.ColumnHeader>
                        <Table.ColumnHeader>Email</Table.ColumnHeader>
                        <Table.ColumnHeader>Role</Table.ColumnHeader>
                        <Table.ColumnHeader>Restaurant ID</Table.ColumnHeader>
                        <Table.ColumnHeader>Driver ID</Table.ColumnHeader>
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {users.map((user: User) => (
                        <Table.Row key={user.id}>
                            <Table.Cell>{user.id}</Table.Cell>
                            <Table.Cell>
                                {user.firstName} {user.lastName}
                            </Table.Cell>
                            <Table.Cell>{user.email}</Table.Cell>
                            <Table.Cell>
                                <Badge colorPalette={getRoleBadgeColor(user.role)}>
                                    {getRoleLabel(user.role)}
                                </Badge>
                            </Table.Cell>
                            <Table.Cell>{user.restaurantId || "-"}</Table.Cell>
                            <Table.Cell>{user.driverId || "-"}</Table.Cell>
                        </Table.Row>
                    ))}
                </Table.Body>
            </Table.Root>
        </Box>
    );
};

export default UsersTable;
