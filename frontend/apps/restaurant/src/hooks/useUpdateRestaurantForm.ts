import { zodResolver } from "@hookform/resolvers/zod";
import { Restaurant } from "@repo/shared/models";
import { useForm } from "react-hook-form";
import { z } from "zod";

const createRestaurantSchema = (originalRestaurant: Restaurant) => {
  return z
    .object({
      restaurantName: z.string().min(1, { message: "Name is required" }),
      address: z.string().optional(),
      cuisineType: z.string().optional(),
    })
    .superRefine((data, ctx) => {
      // Compare the submitted data to the original data
      if (
        data.restaurantName === originalRestaurant.restaurantName &&
        data.address === originalRestaurant.address &&
        data.cuisineType === originalRestaurant.cuisineType
      ) {
        ctx.addIssue({
          code: "custom",
          message: "Please make a change to save your restaurant.",
        });
      }
    });
};

// Define the inferred type
export type RestaurantFormValues = z.infer<
  ReturnType<typeof createRestaurantSchema>
>;

export const useUpdateRestaurantForm = (restaurant: Restaurant) => {
  return useForm<RestaurantFormValues>({
    resolver: zodResolver(createRestaurantSchema(restaurant)),
    defaultValues: restaurant,
    mode: "onChange",
  });
};
