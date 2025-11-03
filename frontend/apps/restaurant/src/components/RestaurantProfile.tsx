import {
  Button,
  Container,
  Field,
  Fieldset,
  Flex,
  Input,
  SimpleGrid,
} from "@chakra-ui/react";
import { Restaurant } from "@repo/shared/models";
import { useEffect, useState } from "react";

import { useAuthStore, useRestaurant } from "@repo/shared/hooks";
import { Toaster, toaster } from "@repo/ui/components";
import { useUpdateRestaurant } from "../hooks/useUpdateRestaurant";
import {
  RestaurantFormValues,
  useUpdateRestaurantForm,
} from "../hooks/useUpdateRestaurantForm";

const RestaurantProfile = () => {
  const { user } = useAuthStore();
  const { data: restaurant, isLoading } = useRestaurant(user?.restaurantId!);

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
      reset({
        restaurantName: restaurant.restaurantName,
        address: restaurant.address || "",
        cuisineType: restaurant.cuisineType || "",
      });
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
    } catch {
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
    <Container paddingX={10} paddingTop={5}>
      <form onSubmit={handleSubmit(onSubmit)}>
        <Fieldset.Root size="lg" marginTop={10}>
          <Fieldset.Legend>Restaurant</Fieldset.Legend>
          <Fieldset.Content>
            <SimpleGrid columns={2} gap={5}>
              <Field.Root invalid={!!errors.restaurantName}>
                <Field.Label>Name</Field.Label>
                <Input {...register("restaurantName")} />
                <Field.ErrorText>
                  {errors.restaurantName?.message}
                </Field.ErrorText>
              </Field.Root>
              <Field.Root invalid={!!errors.address}>
                <Field.Label>Address</Field.Label>
                <Input {...register("address")} />
                <Field.ErrorText>{errors.address?.message}</Field.ErrorText>
              </Field.Root>
              <Field.Root invalid={!!errors.cuisineType}>
                <Field.Label>Cuisine Type</Field.Label>
                <Input {...register("cuisineType")} />
                <Field.ErrorText>{errors.cuisineType?.message}</Field.ErrorText>
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
      </form>
      <Toaster />
    </Container>
  );
};

export default RestaurantProfile;
