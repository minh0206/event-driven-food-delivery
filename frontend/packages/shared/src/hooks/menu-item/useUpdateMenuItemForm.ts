import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { MenuItem } from "../../models";
import { menuItemFormSchema } from "./useAddMenuItemForm";

const createMenuItemSchema = (originalMenuItem: MenuItem) => {
  return menuItemFormSchema.superRefine((data, ctx) => {
    // Compare the submitted data to the original data
    if (
      data.name === originalMenuItem.name &&
      data.description === originalMenuItem.description &&
      data.price === originalMenuItem.price.toString()
    ) {
      ctx.addIssue({
        code: "custom",
        message: "Please make a change to save your menu item.",
      });
    }
  });
};

// Define the inferred type
export type MenuItemFormValues = z.infer<
  ReturnType<typeof createMenuItemSchema>
>;

export const useUpdateMenuItemForm = (menuItem: MenuItem) => {
  return useForm<MenuItemFormValues>({
    resolver: zodResolver(createMenuItemSchema(menuItem)),
    defaultValues: {
      name: menuItem.name,
      description: menuItem.description,
      price: menuItem.price.toString(),
    },
    mode: "onChange",
  });
};
