import { Box, Button, Heading, Spinner, Text } from "@chakra-ui/react";
import { Toaster, toaster } from "@repo/ui/components";
import { useState } from "react";
import { useTokens } from "../hooks/useTokens";
import { pruneTokens } from "../services/tokenService";
import TokensTable from "./TokensTable";

const TokensDashboard = () => {
    const { data: tokens = [], isLoading, error, refetch } = useTokens();
    const [isPruning, setIsPruning] = useState(false);
    const [currentTime, setCurrentTime] = useState(new Date());

    const handlePrune = async () => {
        setIsPruning(true);
        try {
            await pruneTokens();
            toaster.create({
                title: "Success",
                description: "Expired and revoked tokens have been pruned successfully",
                type: "success",
            });
            refetch();
        } catch (err) {
            toaster.create({
                title: "Error",
                description: "Failed to prune tokens",
                type: "error",
            });
        } finally {
            setIsPruning(false);
        }
    };

    if (isLoading) {
        return (
            <Box textAlign="center" py={8}>
                <Spinner size="xl" color="blue.500" />
                <Text mt={4} color="gray.600">
                    Loading tokens...
                </Text>
            </Box>
        );
    }

    if (error) {
        const errorMessage =
            (error as any)?.response?.data?.message ||
            (error as Error)?.message ||
            "Failed to load tokens";
        return (
            <Box
                p={6}
                bg="red.50"
                borderRadius="md"
                borderWidth="1px"
                borderColor="red.200"
            >
                <Text color="red.700" fontWeight="medium">
                    {errorMessage}
                </Text>
            </Box>
        );
    }

    const activeCount = tokens.filter(
        (t) => !t.revoked && new Date(t.expiresAt) > currentTime
    ).length;
    const revokedCount = tokens.filter((t) => t.revoked).length;
    const expiredCount = tokens.filter(
        (t) => !t.revoked && new Date(t.expiresAt) <= currentTime
    ).length;
    const totalCount = tokens.length;

    return (
        <Box
            p={6}
            bg="white"
            borderRadius="md"
            borderWidth="1px"
            borderColor="gray.200"
            shadow="sm"
        >
            <Box
                display="flex"
                justifyContent="space-between"
                alignItems="center"
                mb={6}
            >
                <Box>
                    <Heading size="md" mb={1}>
                        Refresh Tokens
                    </Heading>
                    <Text color="gray.600" fontSize="sm">
                        {activeCount} active, {expiredCount} expired, {revokedCount} revoked of {totalCount} total
                        tokens
                    </Text>
                </Box>
                <Box display="flex" gap={2}>
                    <Button
                        size="sm"
                        onClick={() => {
                            refetch();
                            setCurrentTime(new Date());
                        }}
                        colorScheme="blue"
                        variant="outline"
                    >
                        Refresh
                    </Button>
                    <Button
                        size="sm"
                        onClick={handlePrune}
                        colorScheme="red"
                        variant="outline"
                        loading={isPruning}
                        disabled={isPruning}
                    >
                        Prune
                    </Button>
                </Box>
            </Box>

            <TokensTable tokens={tokens} currentTime={currentTime} />
            <Toaster />
        </Box>
    );
};

export default TokensDashboard;
