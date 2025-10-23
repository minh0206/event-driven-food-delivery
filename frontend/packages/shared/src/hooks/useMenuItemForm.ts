import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";

const formSchema = z.object({
  name: z.string().min(1, { message: "Name is required" }),
  description: z.string(),
  price: z
    .string()
    .min(1, { message: "Price is required" })
    .refine(
      (data) => {
        const price = parseFloat(data);
        if (isNaN(price)) return false;
        if (price.toString().length !== data.length) return false;
        return true;
      },
      {
        message: "Please enter a valid number",
      }
    )
    .refine((data) => parseFloat(data) > 0, {
      message: "Price must be greater than 0",
    }),
});

type FormValues = z.infer<typeof formSchema>;

export const useMenuItemForm = (defaultValues?: FormValues) => {
  return useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: defaultValues,
  });
};
