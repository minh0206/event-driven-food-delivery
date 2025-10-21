// apps/customer/src/pages/LoginPage.tsx
import {
  AbsoluteCenter,
  Button,
  Card,
  Link as ChakraLink,
  Field,
  Input,
  Stack,
  Text,
} from "@chakra-ui/react";
import { PasswordInput } from "../components/ui/password-input";

import apiClient from "api-client";
import { useForm, type FieldValues } from "react-hook-form";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "shared-hooks";

interface FormValues {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export const SignUpPage = () => {
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm<FormValues>();
  const { login } = useAuth();
  const navigate = useNavigate();

  const onSubmit = async (data: FieldValues) => {
    try {
      const response = await apiClient.post("/users/register/customer", data);
      const { token } = response.data;
      login(token); // Save token to context and localStorage
      navigate("/"); // Redirect to home on successful login
    } catch (error: any) {
      // Handle sign up error (e.g., show a notification)
      if (error.response.status === 409) {
        console.log(error.response.data);
        setError("email", { type: "conflict" });
      }
    }
  };

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
              <Field.Root invalid={!!errors.email}>
                <Field.Label>Email</Field.Label>
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

              <Field.Root invalid={!!errors.password}>
                <Field.Label>Password</Field.Label>
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

              <Field.Root invalid={!!errors.firstName}>
                <Field.Label>First name</Field.Label>
                <Input {...register("firstName", { required: true })} />
                <Field.ErrorText>
                  {errors.firstName?.type === "required" &&
                    "First name is required"}
                </Field.ErrorText>
              </Field.Root>

              <Field.Root invalid={!!errors.lastName}>
                <Field.Label>Last name</Field.Label>
                <Input {...register("lastName", { required: true })} />
                <Field.ErrorText>
                  {errors.lastName?.type === "required" &&
                    "Last name is required"}
                </Field.ErrorText>
              </Field.Root>

              <Button type="submit" variant="solid">
                Sign up
              </Button>
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
