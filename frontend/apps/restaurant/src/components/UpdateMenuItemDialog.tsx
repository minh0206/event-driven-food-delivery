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
import { MenuItem } from "@repo/shared/models";
import { useEffect, useState } from "react";
import { FieldValues } from "react-hook-form";
import { useUpdateMenuItemForm } from "../hooks/useUpdateMenuItemForm";

const UpdateMenuItemDialog = ({
  menuItem,
  onUpdateMenuItem,
}: {
  menuItem: MenuItem;
  onUpdateMenuItem: (
    item: MenuItem,
    successCallback: () => void,
    errorCallback: () => void
  ) => void;
}) => {
  const dialog = useDialog();
  const [originalMenuItem, setOriginalMenuItem] = useState<MenuItem>(menuItem);

  // Form
  const {
    register,
    reset,
    handleSubmit,
    formState: { errors, isDirty, isValid },
  } = useUpdateMenuItemForm(originalMenuItem);

  useEffect(() => {
    setOriginalMenuItem(menuItem);
    reset({
      name: menuItem.name,
      description: menuItem.description,
      price: menuItem.price.toString(),
    });
  }, [menuItem]);

  const onSubmit = async (data: FieldValues) => {
    const updatedMenuItem = {
      ...originalMenuItem,
      name: data.name,
      description: data.description,
      price: Number(data.price),
    };

    onUpdateMenuItem(
      updatedMenuItem,
      () => dialog.setOpen(false),
      () => reset()
    );
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
                    <Input
                      type="number"
                      step="0.01"
                      min="0"
                      {...register("price")}
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
                <Button type="submit" disabled={!isDirty || !isValid}>
                  Save
                </Button>
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
