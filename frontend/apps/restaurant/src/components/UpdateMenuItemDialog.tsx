import {
  Button,
  CloseButton,
  Dialog,
  Field,
  Input,
  Portal,
  Stack,
  useDialog,
} from "@chakra-ui/react";
import { useMenuItemForm, useUpdateMenuItem } from "@repo/shared/hooks";
import { MenuItem } from "@repo/shared/models";
import { FieldValues } from "react-hook-form";

const UpdateMenuItemDialog = ({
  menuItem,
  onSuccess,
  onError,
}: {
  menuItem: MenuItem;
  onSuccess?: () => void;
  onError?: (error: Error) => void;
}) => {
  // Form
  const {
    register,
    reset,
    handleSubmit,
    setError,
    formState: { errors },
  } = useMenuItemForm({
    name: menuItem.name,
    description: menuItem.description,
    price: menuItem.price.toString(),
  });

  // Mutation
  const updateMenuItem = useUpdateMenuItem();

  const dialog = useDialog();

  const onSubmit = async (data: FieldValues) => {
    try {
      // Validate the new values
      if (
        data.name === menuItem.name &&
        data.price === menuItem.price.toString()
      ) {
        setError("name", { message: "Please enter a new name" });
        setError("price", { message: "Please enter a new price" });
        return;
      }

      await updateMenuItem.mutateAsync({
        menuItemId: menuItem.id,
        menuItem: {
          id: menuItem.id,
          name: data.name,
          description: data.description,
          price: data.price,
          restaurantId: menuItem.restaurantId,
        },
      });
      onSuccess?.();
      dialog.setOpen(false);
    } catch (error) {
      onError?.(error as Error);
    }
  };

  return (
    <Dialog.RootProvider value={dialog}>
      <Dialog.Trigger asChild>
        <Button variant="outline">Edit</Button>
      </Dialog.Trigger>

      <Portal>
        <Dialog.Backdrop />
        <Dialog.Positioner>
          <Dialog.Content>
            <form onSubmit={handleSubmit(onSubmit)}>
              <Dialog.Header>
                <Dialog.Title>Edit Item</Dialog.Title>
              </Dialog.Header>
              <Dialog.Body>
                <Stack gap="4" w="full">
                  <Field.Root required invalid={!!errors.name}>
                    <Field.Label>
                      Name <Field.RequiredIndicator />
                    </Field.Label>
                    <Input {...register("name")} placeholder="eg. Pizza" />
                    <Field.ErrorText>{errors.name?.message}</Field.ErrorText>
                  </Field.Root>

                  <Field.Root invalid={!!errors.description}>
                    <Field.Label>Description</Field.Label>
                    <Input
                      {...register("description")}
                      placeholder="eg. Pizza"
                    />
                    <Field.ErrorText>
                      {errors.description?.message}
                    </Field.ErrorText>
                  </Field.Root>

                  <Field.Root required invalid={!!errors.price}>
                    <Field.Label>
                      Price <Field.RequiredIndicator />
                    </Field.Label>
                    <Input {...register("price")} placeholder="eg. 10" />
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
    </Dialog.RootProvider>
  );
};

export default UpdateMenuItemDialog;
