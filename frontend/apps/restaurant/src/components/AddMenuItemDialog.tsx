import {
  Button,
  CloseButton,
  Dialog,
  Field,
  Input,
  Portal,
  Stack,
} from "@chakra-ui/react";

import { MenuItem } from "@repo/shared/models";
import { FieldValues } from "react-hook-form";
import { useAddMenuItemForm } from "../hooks/useAddMenuItemForm";

const AddMenuItemDialog = ({
  onAddMenuItem,
}: {
  onAddMenuItem: (menuItem: MenuItem, successCallback: () => void) => void;
}) => {
  // Form
  const {
    register,
    reset,
    handleSubmit,
    formState: { errors, isValid },
  } = useAddMenuItemForm();

  const onSubmit = async (data: FieldValues) => {
    onAddMenuItem(
      {
        id: 0,
        name: data.name,
        description: data.description,
        price: data.price,
      },
      () => reset()
    );
  };

  return (
    <Dialog.Root>
      <Dialog.Trigger asChild>
        <Button variant="solid" colorPalette="blue">
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
                <Button type="submit" disabled={!isValid}>
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
    </Dialog.Root>
  );
};

export default AddMenuItemDialog;
