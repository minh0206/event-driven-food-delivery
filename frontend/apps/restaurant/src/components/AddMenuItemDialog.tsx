import {
  Button,
  CloseButton,
  Dialog,
  Field,
  Input,
  Portal,
  Stack,
} from "@chakra-ui/react";
import { useAddMenuItem, useAddMenuItemForm } from "@repo/shared/hooks";
import { FieldValues } from "react-hook-form";

const AddMenuItemDialog = ({
  onSuccess,
  onError,
}: {
  onSuccess?: () => void;
  onError?: (error: Error) => void;
}) => {
  // Form
  const {
    register,
    reset,
    handleSubmit,
    formState: { errors, isValid },
  } = useAddMenuItemForm();

  // Mutation
  const addMenuItem = useAddMenuItem();

  const onSubmit = async (data: FieldValues) => {
    try {
      await addMenuItem.mutateAsync({
        id: 0,
        name: data.name,
        description: data.description,
        price: data.price,
        restaurantId: 0,
      });
      onSuccess?.();
      reset();
    } catch (error) {
      onError?.(error as Error);
    }
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
