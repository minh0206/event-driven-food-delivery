import { Badge, Box, Table, Text } from "@chakra-ui/react";
import { RefreshToken } from "../models/RefreshToken";

interface TokensTableProps {
    tokens: RefreshToken[];
    currentTime: Date;
}

const TokensTable = ({ tokens, currentTime }: TokensTableProps) => {
    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleString();
    };

    const isExpired = (expiresAt: string) => {
        return new Date(expiresAt) <= currentTime;
    };

    const getTimeLeft = (expiresAt: string) => {
        const expiryDate = new Date(expiresAt);
        const diffMs = expiryDate.getTime() - currentTime.getTime();

        if (diffMs <= 0) {
            return "Expired";
        }

        const diffSeconds = Math.floor(diffMs / 1000);
        const diffMinutes = Math.floor(diffSeconds / 60);
        const diffHours = Math.floor(diffMinutes / 60);
        const diffDays = Math.floor(diffHours / 24);

        if (diffDays > 0) {
            return `${diffDays}d ${diffHours % 24}h`;
        } else if (diffHours > 0) {
            return `${diffHours}h ${diffMinutes % 60}m`;
        } else if (diffMinutes > 0) {
            return `${diffMinutes}m`;
        } else {
            return `${diffSeconds}s`;
        }
    };

    return (
        <Box overflowX="auto">
            <Table.Root size="sm">
                <Table.Header>
                    <Table.Row>
                        <Table.ColumnHeader>ID</Table.ColumnHeader>
                        <Table.ColumnHeader>User ID</Table.ColumnHeader>
                        <Table.ColumnHeader>User Email</Table.ColumnHeader>
                        <Table.ColumnHeader>User Role</Table.ColumnHeader>
                        <Table.ColumnHeader minWidth={"70px"}>Status</Table.ColumnHeader>
                        <Table.ColumnHeader>Created At</Table.ColumnHeader>
                        <Table.ColumnHeader>Time Left</Table.ColumnHeader>
                        <Table.ColumnHeader>Revoked At</Table.ColumnHeader>
                        <Table.ColumnHeader>Token (Preview)</Table.ColumnHeader>
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {tokens.map((token) => (
                        <Table.Row key={token.id}>
                            <Table.Cell>{token.id}</Table.Cell>
                            <Table.Cell>{token.userId}</Table.Cell>
                            <Table.Cell>{token.userEmail}</Table.Cell>
                            <Table.Cell>{token.userRole}</Table.Cell>
                            <Table.Cell>
                                {token.revoked ? (
                                    <Badge colorPalette="red">Revoked</Badge>
                                ) : isExpired(token.expiresAt) ? (
                                    <Badge colorPalette="orange">Expired</Badge>
                                ) : (
                                    <Badge colorPalette="green">Active</Badge>
                                )}
                            </Table.Cell>
                            <Table.Cell>{formatDate(token.createdAt)}</Table.Cell>
                            <Table.Cell>
                                <Text color={isExpired(token.expiresAt) ? "gray.500" : undefined}>
                                    {getTimeLeft(token.expiresAt)}
                                </Text>
                            </Table.Cell>
                            <Table.Cell>{token.revokedAt ? formatDate(token.revokedAt) : "N/A"}</Table.Cell>
                            <Table.Cell>
                                <Text fontSize="xs" fontFamily="mono" truncate maxW="200px">
                                    {token.token}
                                </Text>
                            </Table.Cell>
                        </Table.Row>
                    ))}
                </Table.Body>
            </Table.Root>
            {tokens.length === 0 && (
                <Box textAlign="center" py={8}>
                    <Text color="gray.500">No tokens found</Text>
                </Box>
            )}
        </Box>
    );
};

export default TokensTable;
