import {
  AbsoluteCenter,
  Alert,
  Button,
  Card,
  Link as ChakraLink,
  Field,
  Input,
  Stack,
  Text,
} from "@chakra-ui/react";
import { useAuthStore } from "@repo/shared/hooks";
import { Role } from "@repo/shared/models";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Link, useNavigate } from "react-router-dom";
import { PasswordInput } from "../components/ui/password-input";

interface FormValues {
  email: string;
  password: string;
  firstName: string;
  lastName?: string;
  restaurantName?: string;
  address?: string;
  cuisineType?: string;
}

export const SignUpPage = ({ role }: { role: Role }) => {
  const {
    register,
    handleSubmit,
    setError: setFormError,
    formState: { errors },
  } = useForm<FormValues>();

  const {
    register: registerUser,
    initialize,
    isInitialized,
    user,
    isLoading,
  } = useAuthStore();

  const [error, setError] = useState<string | null>(null);

  const navigate = useNavigate();

  const onSubmit = async (data: FormValues) => {
    try {
      await registerUser({ role, ...data });
    } catch (error: any) {
      if (error.response?.status === 409) {
        setFormError("email", { type: "conflict" });
      }

      setError(error.response?.data?.message || "Server is down");
    }
  };

  useEffect(() => {
    if (!isInitialized) initialize(role);
    if (user) navigate("/");
  }, [initialize, role, user]);

  return (
    <AbsoluteCenter>
      <form onSubmit={handleSubmit(onSubmit)}>
        <Card.Root
          width={{ base: "100%", lg: "400px" }}
          overflow="auto"
          shadow="md"
          rounded="md"
        >
          <Card.Header>
            <Card.Title>Sign up</Card.Title>
            <Card.Description>Create your account</Card.Description>
          </Card.Header>
          <Card.Body>
            <Stack gap="4">
              <Field.Root required invalid={!!errors.email}>
                <Field.Label>
                  Email <Field.RequiredIndicator />
                </Field.Label>
                <Input
                  {...register("email", { required: true })}
                  placeholder="Email"
                  type="email"
                />
                <Field.ErrorText>
                  {errors.email?.type === "required" && "Email is required"}
                  {errors.email?.type === "conflict" && "Email already exists"}
                </Field.ErrorText>
              </Field.Root>

              <Field.Root required invalid={!!errors.password}>
                <Field.Label>
                  Password <Field.RequiredIndicator />
                </Field.Label>
                <PasswordInput
                  {...register("password", { required: true, minLength: 8 })}
                  placeholder="Password"
                />
                <Field.ErrorText>
                  {errors.password?.type === "required" &&
                    "Password is required"}
                  {errors.password?.type === "minLength" &&
                    "Password must be at least 8 characters"}
                </Field.ErrorText>
              </Field.Root>

              <Field.Root required invalid={!!errors.firstName}>
                <Field.Label>
                  First name <Field.RequiredIndicator />
                </Field.Label>
                <Input
                  {...register("firstName", { required: true })}
                  placeholder="First name"
                />
                <Field.ErrorText>
                  {errors.firstName?.type === "required" &&
                    "First name is required"}
                </Field.ErrorText>
              </Field.Root>

              <Field.Root invalid={!!errors.lastName}>
                <Field.Label>Last name</Field.Label>
                <Input {...register("lastName")} placeholder="Last name" />
                <Field.ErrorText>{errors.lastName?.message}</Field.ErrorText>
              </Field.Root>

              {role === Role.RESTAURANT_ADMIN && (
                <>
                  <Field.Root
                    required={role === Role.RESTAURANT_ADMIN}
                    invalid={!!errors.restaurantName}
                  >
                    <Field.Label>
                      Restaurant name
                      <Field.RequiredIndicator />
                    </Field.Label>
                    <Input
                      {...register("restaurantName", {
                        required: role === Role.RESTAURANT_ADMIN,
                      })}
                      placeholder="Restaurant name"
                    />
                    <Field.ErrorText>
                      {errors.restaurantName?.type === "required" &&
                        "Restaurant name is required"}
                    </Field.ErrorText>
                  </Field.Root>

                  <Field.Root invalid={!!errors.address}>
                    <Field.Label>Address</Field.Label>
                    <Input {...register("address")} placeholder="Address" />
                    <Field.ErrorText>{errors.address?.message}</Field.ErrorText>
                  </Field.Root>

                  <Field.Root invalid={!!errors.cuisineType}>
                    <Field.Label>Cuisine type</Field.Label>
                    <Input
                      {...register("cuisineType")}
                      placeholder="Cuisine type"
                    />
                    <Field.ErrorText>
                      {errors.cuisineType?.message}
                    </Field.ErrorText>
                  </Field.Root>
                </>
              )}

              <Button type="submit" variant="solid" loading={isLoading}>
                Sign up
              </Button>
              {error && (
                <Alert.Root status="error">
                  <Alert.Indicator />
                  <Alert.Content>Error: {error}</Alert.Content>
                </Alert.Root>
              )}
            </Stack>
          </Card.Body>
          <Card.Footer justifyContent="center">
            <Text>Already have an account?</Text>
            <ChakraLink asChild>
              <Link to="/login">
                <b>Sign in</b>
              </Link>
            </ChakraLink>
          </Card.Footer>
        </Card.Root>
      </form>
    </AbsoluteCenter>
  );
};
