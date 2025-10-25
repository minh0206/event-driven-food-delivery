import {
  Box,
  Container,
  Field,
  Fieldset,
  SimpleGrid,
  Text,
} from "@chakra-ui/react";
import { useAuthStore } from "@repo/shared/hooks";
import { Toaster } from "@repo/ui/components";
import { ReactNode } from "react";
import RestaurantProfile from "../components/RestaurantProfile";

const TextBox = ({ children }: { children: ReactNode }) => {
  return (
    <Box
      bg="gray.100"
      w="full"
      padding="10px"
      color="gray.600"
      borderWidth="1px"
      borderRadius="sm"
    >
      <Text textStyle="sm">{children}</Text>
    </Box>
  );
};

export const ProfilePage = () => {
  const { user } = useAuthStore();

  return (
    <>
      <Container paddingX={10} paddingTop={5}>
        <Fieldset.Root size="lg">
          <Fieldset.Legend>User</Fieldset.Legend>
          <Fieldset.Content>
            <SimpleGrid columns={2} gap={5}>
              <Field.Root>
                <Field.Label>First Name</Field.Label>
                <TextBox>{user?.firstName}</TextBox>
              </Field.Root>
              <Field.Root>
                <Field.Label>Last Name</Field.Label>
                <TextBox>{user?.lastName}</TextBox>
              </Field.Root>
              <Field.Root>
                <Field.Label>Email</Field.Label>
                <TextBox>{user?.email}</TextBox>
              </Field.Root>
              <Field.Root>
                <Field.Label>Role</Field.Label>
                <TextBox>{user?.role}</TextBox>
              </Field.Root>
            </SimpleGrid>
          </Fieldset.Content>
        </Fieldset.Root>
        {user?.role === "RESTAURANT_ADMIN" && <RestaurantProfile />}
      </Container>

      <Toaster />
    </>
  );
};
