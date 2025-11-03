import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";

export const menuItemFormSchema = z.object({
  name: z.string().min(1, { message: "Name is required" }),
  description: z.string(),
  price: z
    .string()
    .min(1, { message: "Price is required" })
    .refine((data) => Number.parseFloat(data) > 0, {
      message: "Price must be greater than 0",
    }),
});

type FormValues = z.infer<typeof menuItemFormSchema>;

export const useAddMenuItemForm = () => {
  return useForm<FormValues>({
    resolver: zodResolver(menuItemFormSchema),
  });
};
