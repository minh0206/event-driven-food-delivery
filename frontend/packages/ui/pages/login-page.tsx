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
}

export const LoginPage = () => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>();
  const { login } = useAuth();
  const navigate = useNavigate();

  const onSubmit = async (data: FieldValues) => {
    try {
      const response = await apiClient.post("/users/login", data);
      const { token } = response.data;
      login(token); // Save token to context and localStorage
      navigate("/"); // Redirect to home on successful login
    } catch (error) {
      console.error("Login failed:", error);
      // Handle login error (e.g., show a notification)
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
            <Card.Title>Sign in</Card.Title>
            <Card.Description>Access your account</Card.Description>
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
                  {errors.email?.type === "required"} Email is required
                </Field.ErrorText>
              </Field.Root>

              <Field.Root invalid={!!errors.password}>
                <Field.Label>Password</Field.Label>
                <PasswordInput
                  {...register("password", { required: true })}
                  placeholder="Password"
                />
                <Field.ErrorText>
                  {errors.password?.type === "required"} Password is required
                </Field.ErrorText>
              </Field.Root>

              <Button type="submit" variant="solid">
                Sign in
              </Button>
            </Stack>
          </Card.Body>
          <Card.Footer justifyContent="center">
            <Text>Don't have an account?</Text>
            <ChakraLink asChild>
              <Link to="/signup">
                <b>Sign up</b>
              </Link>
            </ChakraLink>
          </Card.Footer>
        </Card.Root>
      </form>
    </AbsoluteCenter>
  );
};
