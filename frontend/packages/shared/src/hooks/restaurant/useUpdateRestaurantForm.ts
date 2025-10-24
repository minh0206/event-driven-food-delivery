import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { Restaurant } from "../../models";

const createRestaurantSchema = (originalRestaurant: Restaurant) => {
  return z
    .object({
      name: z.string().min(1, { message: "Name is required" }),
      address: z.string().min(1, { message: "Address is required" }),
      cuisineType: z.string().min(1, { message: "Cuisine Type is required" }),
    })
    .superRefine((data, ctx) => {
      // Compare the submitted data to the original data
      if (
        data.name === originalRestaurant.name &&
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
