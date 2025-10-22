import {
  Button,
  CloseButton,
  Dialog,
  Field,
  Input,
  Portal,
  Stack,
} from "@chakra-ui/react";
import { zodResolver } from "@hookform/resolvers/zod";
import { MenuItem } from "@repo/shared/models";
import { restaurantService } from "@repo/shared/services";
import { useMutation } from "@tanstack/react-query";
import { FieldValues, useForm } from "react-hook-form";
import { z } from "zod";

const formSchema = z.object({
  name: z.string().min(1, { message: "Name is required" }),
  price: z.number({ message: "Please enter a valid number" }).min(0.01, {
    message: "Price must be greater than 0",
  }),
});

type FormValues = z.infer<typeof formSchema>;

const AddMenuItemDialog = () => {
  const {
    register,
    reset,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: { name: "", price: 0 },
  });

  const addMenuItem = useMutation({
    mutationFn: (menuItem: MenuItem) =>
      restaurantService.addMenuItem(2, menuItem),
  });

  const onSubmit = async (data: FieldValues) => {
    try {
      console.log(data);
      addMenuItem.mutate({
        id: 0,
        name: data.name,
        description: "",
        price: data.price,
        restaurantId: 0,
      });

      //   reset();
    } catch (error) {
      console.error("Login failed:", error);
      // Handle login error (e.g., show a notification)
    }
  };

  return (
    <Dialog.Root>
      <Dialog.Trigger asChild>
        <Button variant="outline" size="sm">
          Add Item
        </Button>
      </Dialog.Trigger>

      <Portal>
        <Dialog.Backdrop />
        <Dialog.Positioner>
          <Dialog.Content>
            <form onSubmit={handleSubmit(onSubmit)}>
              <Dialog.Header>
                <Dialog.Title>Add Item</Dialog.Title>
              </Dialog.Header>
              <Dialog.Body>
                <Stack gap="4" w="full">
                  <Field.Root invalid={!!errors.name}>
                    <Field.Label>Name</Field.Label>
                    <Input {...register("name")} placeholder="eg. Pizza" />
                    <Field.ErrorText>{errors.name?.message}</Field.ErrorText>
                  </Field.Root>

                  <Field.Root invalid={!!errors.price}>
                    <Field.Label>Price</Field.Label>
                    <Input
                      {...register("price", { valueAsNumber: true })}
                      placeholder="eg. 10"
                    />
                    <Field.ErrorText>{errors.price?.message}</Field.ErrorText>
                  </Field.Root>
                </Stack>
              </Dialog.Body>

              <Dialog.Footer>
                <Dialog.ActionTrigger asChild>
                  <Button variant="outline" onClick={() => reset()}>
                    Cancel
                  </Button>
                </Dialog.ActionTrigger>
                <Button type="submit">Save</Button>
              </Dialog.Footer>

              <Dialog.CloseTrigger asChild>
                <CloseButton size="sm" onClick={() => reset()} />
              </Dialog.CloseTrigger>
            </form>
          </Dialog.Content>
        </Dialog.Positioner>
      </Portal>
    </Dialog.Root>
  );
};

export default AddMenuItemDialog;
