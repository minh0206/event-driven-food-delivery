import {
  Box,
  Button,
  Container,
  Field,
  Fieldset,
  Flex,
  Input,
  SimpleGrid,
  Text,
} from "@chakra-ui/react";
import {
  RestaurantFormValues,
  useAuthStore,
  useRestaurant,
  useUpdateRestaurant,
  useUpdateRestaurantForm,
} from "@repo/shared/hooks";
import { Restaurant } from "@repo/shared/models";
import { toaster, Toaster } from "@repo/ui/components";
import { ReactNode, useEffect, useState } from "react";

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

const ProfilePage = () => {
  const { user } = useAuthStore();
  const { data: restaurant, isLoading } = useRestaurant();
  const updateRestaurant = useUpdateRestaurant();
  const [originalRestaurant, setOriginalRestaurant] = useState<Restaurant>(
    {} as Restaurant
  );

  const {
    register,
    handleSubmit,
    reset,
    formState: { isValid, errors, isDirty },
  } = useUpdateRestaurantForm(originalRestaurant);

  // Use an effect to reset the form after a successful submission
  useEffect(() => {
    if (restaurant) {
      setOriginalRestaurant(restaurant);
      reset(restaurant);
    }
  }, [restaurant]);

  const onSubmit = async (data: RestaurantFormValues) => {
    try {
      await updateRestaurant.mutateAsync({
        ...originalRestaurant,
        ...data,
      });
      toaster.success({
        title: "Restaurant profile updated",
      });
    } catch (error) {
      toaster.error({
        title: "Error updating restaurant profile",
      });
      reset();
    }
  };

  if (isLoading) {
    return <div>Loading restaurant profile...</div>;
  }

  return (
    <>
      <form onSubmit={handleSubmit(onSubmit)}>
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

          <Fieldset.Root size="lg" marginTop={10}>
            <Fieldset.Legend>Restaurant</Fieldset.Legend>
            <Fieldset.Content>
              <SimpleGrid columns={2} gap={5}>
                <Field.Root invalid={!!errors.name}>
                  <Field.Label>Name</Field.Label>
                  <Input {...register("name")} />
                  <Field.ErrorText>{errors.name?.message}</Field.ErrorText>
                </Field.Root>
                <Field.Root invalid={!!errors.address}>
                  <Field.Label>Address</Field.Label>
                  <Input {...register("address")} />
                  <Field.ErrorText>{errors.address?.message}</Field.ErrorText>
                </Field.Root>
                <Field.Root invalid={!!errors.cuisineType}>
                  <Field.Label>Cuisine Type</Field.Label>
                  <Input {...register("cuisineType")} />
                  <Field.ErrorText>
                    {errors.cuisineType?.message}
                  </Field.ErrorText>
                </Field.Root>
              </SimpleGrid>
            </Fieldset.Content>
          </Fieldset.Root>

          <Flex justify="flex-end">
            <Button
              disabled={!isDirty}
              variant="outline"
              mr={2}
              onClick={() => reset()}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              disabled={!isDirty || !isValid}
              colorPalette="blue"
            >
              Save
            </Button>
          </Flex>
        </Container>
      </form>
      <Toaster />
    </>
  );
};

export default ProfilePage;
